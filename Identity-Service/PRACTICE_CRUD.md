# Bài Tập Thực Hành API CRUD & Spring Boot

File này chứa các bài tập giúp bạn ôn luyện và nắm vững kiến thức về xây dựng API CRUD trong Spring Boot, dựa trên project `Identity-Service` hiện tại.

## Mục Tiêu
- Hiểu rõ luồng dữ liệu: Controller -> Service -> Repository -> Database.
- Thành thạo các thao tác HTTP method: POST, GET, PUT, DELETE.
- Làm quen với việc xử lý lỗi và Validation.

---

## Bài Tập 1: Hoàn Thiện CRUD (Update & Delete)

Hiện tại project đã có:
- **Create**: Tạo user mới (`POST /users`)
- **Read**: Lấy danh sách user (`GET /users`) và lấy 1 user (`GET /users/{userId}`)

**Yêu cầu:**
Hãy bổ sung thêm 2 chức năng còn thiếu:
1.  **Update User**: Cập nhật thông tin user dựa trên ID.
2.  **Delete User**: Xóa user dựa trên ID.

### Gợi ý (Hint)

#### 1. Update User (`PUT /users/{userId}`)
- **Bước 1: Định nghĩa DTO**
  Mở file `src/main/java/com/eurus/Identity_Service/dto/request/UserUpdateRequest.java` và thêm các trường bạn muốn cho phép cập nhật (thường không cho update username).
  ```java
  // UserUpdateRequest.java
  package com.eurus.Identity_Service.dto.request;
  
  import lombok.*;
  import lombok.experimental.FieldDefaults;
  import java.time.LocalDate;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public class UserUpdateRequest {
      String password;
      String firstName;
      String lastName;
      LocalDate dob;
  }
  ```

- **Bước 2: Service Layer**
  Trong `UserService.java`, viết hàm `updateUser`.
  ```java
  // UserService.java
  public User updateUser(String userId, UserUpdateRequest request) {
      // 1. Tìm user trong DB, nếu không thấy thì báo lỗi
      User user = getUser(userId); // Tận dụng hàm getUser đã viết

      // 2. Cập nhật thông tin từ request vào user entity
      user.setPassword(request.getPassword());
      user.setFirstName(request.getFirstName());
      user.setLastName(request.getLastName());
      user.setDob(request.getDob());

      // 3. Lưu xuống DB
      return userRepository.save(user);
  }
  ```

- **Bước 3: Controller Layer**
  Trong `UserController.java`, thêm API endpoint.
  ```java
  // UserController.java
  @PutMapping("/{userId}")
  User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
      return userService.updateUser(userId, request);
  }
  ```

#### 2. Delete User (`DELETE /users/{userId}`)
- **Service Layer**:
  ```java
  // UserService.java
  public void deleteUser(String userId) {
      userRepository.deleteById(userId);
  }
  ```
- **Controller Layer**:
  ```java
  // UserController.java
  @DeleteMapping("/{userId}")
  String deleteUser(@PathVariable String userId) {
      userService.deleteUser(userId);
      return "User has been deleted";
  }
  ```

---

## Bài Tập 2: Refactor Code (Làm gọn code)

Trong `UserService.java`, đoạn code tạo User đang set thủ công từng trường (`user.setUsername(...)`, `user.setPassword(...)`...). Hãy cải tiến nó.

**Yêu cầu:**
Sử dụng **Lombok Builder** để convert từ DTO sang Entity gọn gàng hơn.

### Gợi ý (Hint)
- Trong `User.java` đã có `@Builder`.
- Thay vì `new User()` và set từng dòng:
  ```java
  // Code cũ (Manual setter)
  User user = new User();
  user.setUsername(request.getUsername());
  user.setPassword(request.getPassword());
  // ...
  ```
- Hãy đổi thành:
  ```java
  // Code mới (Builder pattern)
  User user = User.builder()
      .username(request.getUsername())
      .password(request.getPassword())
      .firstName(request.getFirstName())
      .lastName(request.getLastName())
      .dob(request.getDob())
      .build();
  ```

---

## Bài Tập 3: Validation (Kiểm tra dữ liệu đầu vào)

Hiện tại người dùng có thể gửi `username` rỗng hoặc `password` quá ngắn. Hãy chặn điều này.

**Yêu cầu:**
1.  `username`: Không được để trống, tối thiểu 3 ký tự.
2.  `password`: Tối thiểu 8 ký tự.

### Gợi ý (Hint)
- **Bước 1: Thêm Dependency** (nếu chưa có trong `pom.xml`)
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  ```

- **Bước 2: Thêm Annotation vào DTO (`UserCreationRequest.java`)**
  ```java
  import jakarta.validation.constraints.Size;

  public class UserCreationRequest {
      @Size(min = 3, message = "Username must be at least 3 characters")
      String username;

      @Size(min = 8, message = "Password must be at least 8 characters")
      String password;
      
      // ... các trường khác giữ nguyên
  }
  ```

- **Bước 3: Kích hoạt Validation ở Controller (`UserController.java`)**
  Thêm `@Valid` trước `@RequestBody`.
  ```java
  import jakarta.validation.Valid;

  @PostMapping
  User createUser(@RequestBody @Valid UserCreationRequest request) {
      return userService.createUser(request);
  }
  ```

---

## Bài Tập 4: Tìm kiếm User (Nâng cao)

**Yêu cầu:**
Viết API tìm kiếm user theo `username` (gần đúng hoặc chính xác).

### Gợi ý (Hint)
- **Repository (`UserRepository.java`)**:
  Spring Data JPA hỗ trợ "Query Methods" - tự động tạo câu lệnh SQL dựa trên tên hàm.
  ```java
  public interface UserRepository extends JpaRepository<User, String> {
      // Tìm user có username chứa chuỗi ký tự (LIKE %username%)
      // VD: tìm "an" sẽ ra "hoangan", "annguyen"
      List<User> findByUsernameContaining(String username);
  }
  ```

- **Service (`UserService.java`)**:
  ```java
  public List<User> searchUsers(String keyword) {
      return userRepository.findByUsernameContaining(keyword);
  }
  ```

- **Controller (`UserController.java`)**:
  ```java
  // GET /users/search?keyword=abc
  @GetMapping("/search")
  List<User> searchUsers(@RequestParam("keyword") String keyword) {
      return userService.searchUsers(keyword);
  }
  ```

---

## Tóm tắt các Annotation cần nhớ

| Annotation | Vị trí | Tác dụng |
| :--- | :--- | :--- |
| `@RestController` | Controller | Đánh dấu class xử lý API, trả về JSON. |
| `@RequestMapping` | Controller | Định nghĩa đường dẫn gốc (VD: `/users`). |
| `@Autowired` | Field | Tiêm dependency (DI) - VD: Service tiêm vào Controller. |
| `@Service` | Service | Đánh dấu class xử lý logic nghiệp vụ. |
| `@Repository` | Repository | Đánh dấu class giao tiếp DB. |
| `@Entity` | Entity | Đánh dấu class ánh xạ với bảng trong DB. |
| `@RequestBody` | Parameter | Map JSON từ body request vào Object Java. |
| `@PathVariable` | Parameter | Lấy giá trị từ đường dẫn URL (VD: `/users/{id}`). |
| `@RequestParam` | Parameter | Lấy giá trị từ query param (VD: `/users?name=abc`). |
| `@Valid` | Parameter | Kích hoạt kiểm tra validation cho object. |

