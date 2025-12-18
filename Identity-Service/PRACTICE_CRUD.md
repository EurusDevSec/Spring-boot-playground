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
- **Controller**: Cần một method lắng nghe `@PutMapping("/{userId}")`.
    - Input: `userId` (từ path) và `UserUpdateRequest` (từ body - bạn cần tạo class DTO này tương tự `UserCreationRequest`).
- **Service**: Tạo method `updateUser(String userId, UserUpdateRequest request)`.
    - Bước 1: Tìm user cũ bằng `findById`. Nếu không thấy thì báo lỗi.
    - Bước 2: Cập nhật các trường thông tin từ `request` vào `user` vừa tìm được.
    - Bước 3: Gọi `userRepository.save(user)` để lưu lại.
- **Repository**: Không cần viết thêm gì, hàm `save()` của JPA sẽ tự hiểu là update nếu object đã có ID.

#### 2. Delete User (`DELETE /users/{userId}`)
- **Controller**: Cần một method lắng nghe `@DeleteMapping("/{userId}")`.
- **Service**: Tạo method `deleteUser(String userId)`.
    - Gọi `userRepository.deleteById(userId)`.
- **Repository**: Có sẵn `deleteById`.

---

## Bài Tập 2: Refactor Code (Làm gọn code)

Trong `UserService.java`, đoạn code tạo User đang set thủ công từng trường (`user.setUsername(...)`, `user.setPassword(...)`...). Hãy cải tiến nó.

**Yêu cầu:**
Sử dụng **Lombok Builder** hoặc **MapStruct** (nếu muốn nâng cao) để convert từ DTO sang Entity gọn gàng hơn.

### Gợi ý (Hint)
- Trong `User.java` đã có `@Builder`.
- Thay vì `new User()` và set từng dòng, bạn có thể dùng:
  ```java
  User user = User.builder()
      .username(request.getUsername())
      .password(request.getPassword())
      // ... các trường khác
      .build();
  ```

---

## Bài Tập 3: Validation (Kiểm tra dữ liệu đầu vào)

Hiện tại người dùng có thể gửi `username` rỗng hoặc `password` quá ngắn. Hãy chặn điều này.

**Yêu cầu:**
1.  `username`: Không được để trống, tối thiểu 3 ký tự.
2.  `password`: Tối thiểu 8 ký tự.

### Gợi ý (Hint)
- Thêm dependency `spring-boot-starter-validation` vào `pom.xml` (nếu chưa có).
- Trong `UserCreationRequest.java`:
    - Dùng annotation `@Size(min = 3, message = "Username must be at least 3 characters")` trên field `username`.
    - Dùng `@Size(min = 8, ...)` trên field `password`.
- Trong `UserController.java`:
    - Thêm `@Valid` trước tham số `UserCreationRequest request`.
    - *Nâng cao*: Tìm hiểu cách bắt lỗi `MethodArgumentNotValidException` để trả về thông báo lỗi đẹp hơn (dùng `@ControllerAdvice`).

---

## Bài Tập 4: Tìm kiếm User (Nâng cao)

**Yêu cầu:**
Viết API tìm kiếm user theo `username` (gần đúng hoặc chính xác).

### Gợi ý (Hint)
- **Repository**: Khai báo thêm hàm trong `UserRepository`:
  ```java
  // Tìm chính xác
  Optional<User> findByUsername(String username);
  
  // Hoặc tìm gần đúng (LIKE %username%)
  List<User> findByUsernameContaining(String username);
  ```
- **Service & Controller**: Kết nối tương tự các bài trên.

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
| `@PathVariable` | Parameter | Lấy giá trị từ đường dẫn URL (VD: userId). |

