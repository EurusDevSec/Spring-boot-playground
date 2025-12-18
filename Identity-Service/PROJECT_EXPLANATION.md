# Giải Thích Chi Tiết Cấu Trúc Project & Luồng Hoạt Động

Tài liệu này giải thích chi tiết từng file trong project `Identity-Service`, tại sao chúng được code như vậy, và luồng dữ liệu đi qua các lớp này như thế nào.

---

## 1. Kiến Trúc Tổng Quan (Layered Architecture)

Project này tuân theo mô hình **3 lớp (3-Tier Architecture)** phổ biến trong Spring Boot:

1.  **Controller Layer** (`UserController`): Tiếp nhận request từ người dùng (API).
2.  **Service Layer** (`UserService`): Xử lý logic nghiệp vụ (business logic).
3.  **Repository Layer** (`UserRepository`): Giao tiếp trực tiếp với Database.

**Tại sao lại chia lớp như vậy?**
-   **Dễ bảo trì**: Mỗi lớp chỉ làm một việc duy nhất (Single Responsibility Principle).
-   **Dễ thay đổi**: Nếu bạn đổi Database (VD: MySQL sang PostgreSQL), bạn chỉ cần sửa Repository, không ảnh hưởng Controller.
-   **Dễ kiểm thử (Testing)**: Có thể test riêng lẻ từng lớp.

---

## 2. Giải Thích Chi Tiết Từng File

### A. Entity: `User.java`
**Vị trí**: `com.eurus.Identity_Service.entity`

```java
@Entity
@Table(name="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    // ... các field khác
}
```

*   **`@Entity`**: Báo cho Spring Data JPA biết class này đại diện cho một bảng trong Database.
*   **`@Id`**: Khóa chính (Primary Key).
*   **`@GeneratedValue(strategy = GenerationType.UUID)`**:
    *   **Tại sao?**: Thay vì dùng số tự tăng (1, 2, 3...), dùng UUID (chuỗi ngẫu nhiên dài) bảo mật hơn, khó đoán hơn và tránh xung đột khi gộp dữ liệu từ nhiều nguồn.
*   **`@Table(name="user")`**: Đặt tên bảng trong DB là `user`.

### B. Repository: `UserRepository.java`
**Vị trí**: `com.eurus.Identity_Service.repository`

```java
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
}
```

*   **`interface` (không phải class)**:
    *   **Tại sao?**: Spring Data JPA sử dụng cơ chế **Proxy Pattern**. Bạn chỉ cần khai báo interface, Spring sẽ tự động "code" phần thực thi (implementation) lúc chạy.
*   **`extends JpaRepository<User, String>`**:
    *   Kế thừa hàng loạt hàm có sẵn: `save()`, `findById()`, `findAll()`, `deleteById()`... mà không cần viết một dòng SQL nào.
    *   `<User, String>`: Quản lý entity `User`, khóa chính kiểu `String`.
*   **`boolean existsByUsername(String username);`**:
    *   **Magic Method**: Spring tự động phân tích tên hàm (`exists` + `By` + `Username`) để tạo câu lệnh SQL: `SELECT count(*) > 0 FROM user WHERE username = ?`.
    *   **Lưu ý lỗi**: Nếu bạn viết sai tên (VD: `existBy...` thiếu 's'), Spring sẽ không hiểu và báo lỗi "No property found".

### C. Service: `UserService.java`
**Vị trí**: `com.eurus.Identity_Service.service`

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreationRequest request){
        User user = new User();
        // Map dữ liệu thủ công từ Request -> Entity
        user.setUsername(request.getUsername());
        // ...
        return userRepository.save(user);
    }
}
```

*   **`@Service`**: Đánh dấu đây là nơi chứa logic nghiệp vụ. Spring sẽ tạo một Bean (object) của class này và quản lý nó.
*   **`@Autowired`**:
    *   **Dependency Injection (DI)**: Spring tự động tìm Bean `UserRepository` đã tạo ở trên và "tiêm" vào đây. Bạn không cần `new UserRepository()`.
*   **Logic `createUser`**:
    1.  Nhận `UserCreationRequest` (DTO).
    2.  Chuyển đổi sang `User` (Entity). *Tại sao?* Vì Repository chỉ làm việc với Entity, còn Controller nhận DTO.
    3.  Gọi `userRepository.save(user)` để lưu xuống DB.

### D. Controller: `UserController.java`
**Vị trí**: `com.eurus.Identity_Service.controller`

```java
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    User createUser(@RequestBody UserCreationRequest request){
        return userService.createUser(request);
    }
}
```

*   **`@RestController`**: Báo hiệu class này chuyên xử lý API, kết quả trả về sẽ là JSON (thay vì giao diện HTML).
*   **`@RequestMapping("/users")`**: Định nghĩa đường dẫn gốc. Tất cả API trong này đều bắt đầu bằng `/users`.
*   **`@PostMapping`**: Xử lý hành động tạo mới (HTTP POST).
*   **`@RequestBody`**:
    *   **Tại sao?**: Dữ liệu gửi lên (JSON) nằm trong phần Body của request. Annotation này giúp Spring "bóc" JSON đó ra và map vào object `UserCreationRequest`.

---

## 3. Luồng Hoạt Động (Data Flow)

Ví dụ: Khi bạn gửi request tạo User mới.

**Bước 1: Client (Postman) gửi Request**
*   Method: `POST`
*   URL: `http://localhost:8080/users`
*   Body (JSON): `{"username": "eurus", "password": "123"}`

**Bước 2: Controller (`UserController`)**
*   Spring nhận request, thấy khớp với `@PostMapping` trong `UserController`.
*   Spring convert JSON `{"username": "eurus"...}` thành object `UserCreationRequest`.
*   Controller gọi: `userService.createUser(request)`.

**Bước 3: Service (`UserService`)**
*   Nhận `UserCreationRequest`.
*   Tạo mới object `User` (Entity).
*   Copy dữ liệu: `user.setUsername("eurus")`, `user.setPassword("123")`.
*   Gọi: `userRepository.save(user)`.

**Bước 4: Repository (`UserRepository`)**
*   Nhận object `User`.
*   Hibernate (bên dưới JPA) sinh câu lệnh SQL: `INSERT INTO user (id, username, password...) VALUES (...)`.
*   Thực thi SQL xuống MySQL Database.

**Bước 5: Database**
*   Lưu dữ liệu, trả về kết quả thành công.

**Bước 6: Response**
*   Repository trả về `User` đã lưu (kèm ID vừa sinh) cho Service.
*   Service trả về cho Controller.
*   Controller convert object `User` thành JSON và trả về cho Client.

---

## 4. Các File Cấu Hình

### `pom.xml`
*   Là file quản lý dự án của Maven.
*   Chứa danh sách thư viện (`dependencies`):
    *   `spring-boot-starter-web`: Để làm API.
    *   `spring-boot-starter-data-jpa`: Để làm việc với DB.
    *   `mysql-connector-j`: Driver để kết nối MySQL.
    *   `lombok`: Để viết code ngắn gọn hơn.

### `application.yaml`
*   Chứa cấu hình kết nối Database (URL, username, password).
*   `ddl-auto: update`: Tự động cập nhật cấu trúc bảng trong DB nếu bạn sửa code Entity (VD: thêm cột mới).

---

## Tổng Kết

Bạn code như vậy để tận dụng sức mạnh của **Spring Boot**:
1.  **Không cần viết SQL cơ bản** (nhờ JPA Repository).
2.  **Không cần tự khởi tạo object** (nhờ Dependency Injection `@Autowired`).
3.  **Code gọn gàng, dễ đọc** (nhờ Lombok và kiến trúc 3 lớp).
4.  **Chuẩn hóa giao tiếp**: Dùng DTO cho input, Entity cho lưu trữ.

