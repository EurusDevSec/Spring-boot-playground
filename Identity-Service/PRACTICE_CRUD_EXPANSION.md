# Bài Tập Mở Rộng: Quản Lý Công Việc (Task Management)

Để thành thạo API CRUD, cách tốt nhất là làm lại quy trình này với một đối tượng (Entity) hoàn toàn mới.
Bài tập này yêu cầu bạn xây dựng chức năng **Quản lý công việc (To-Do List)** ngay trong project hiện tại.

---

## Mục Tiêu
Tự tay xây dựng luồng 3 lớp (Controller -> Service -> Repository) cho một đối tượng mới là `Task`.

## Yêu Cầu Nghiệp Vụ
Chúng ta cần quản lý các đầu việc (`Task`) với các thông tin sau:
1.  **title**: Tên công việc (VD: "Học Spring Boot").
2.  **description**: Mô tả chi tiết.
3.  **status**: Trạng thái (VD: "PENDING", "COMPLETED").
4.  **dueDate**: Ngày hết hạn.

---

## Hướng Dẫn Từng Bước (Step-by-Step)

### Bước 1: Tạo Entity (`Task.java`)
Tạo class `Task` trong package `entity`.

```java
package com.eurus.Identity_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String title;
    String description;
    String status; // Có thể dùng Enum sau này, giờ dùng String cho dễ
    LocalDate dueDate;
}
```

### Bước 2: Tạo Repository (`TaskRepository.java`)
Tạo interface `TaskRepository` trong package `repository`.

**Gợi ý:**
- Kế thừa `JpaRepository<Task, String>`.
- (Tùy chọn) Thêm hàm `boolean existsByTitle(String title)` nếu muốn kiểm tra trùng tên.

### Bước 3: Tạo DTO (Data Transfer Object)
Tạo 2 class trong package `dto.request`:

1.  **`TaskCreationRequest`** (Dùng cho POST):
    *   Cần các trường: `title`, `description`, `status`, `dueDate`.
    *   *Thử thách*: Thêm `@NotBlank` cho `title`.

2.  **`TaskUpdateRequest`** (Dùng cho PUT):
    *   Cần các trường có thể sửa: `description`, `status`, `dueDate`. (Thường ít khi sửa title, nhưng tùy bạn).

### Bước 4: Tạo Service (`TaskService.java`)
Tạo class `TaskService` trong package `service`. Nhớ đánh dấu `@Service`.

**Nhiệm vụ:**
1.  Tiêm `TaskRepository` vào (`@Autowired`).
2.  Viết hàm `createTask(TaskCreationRequest request)`:
    *   Map từ Request -> Entity `Task`.
    *   Gọi `repository.save()`.
3.  Viết hàm `getTasks()`: Lấy tất cả.
4.  Viết hàm `updateTask(String taskId, TaskUpdateRequest request)`:
    *   Tìm task cũ theo ID.
    *   Cập nhật thông tin mới.
    *   Lưu lại.
5.  Viết hàm `deleteTask(String taskId)`.

### Bước 5: Tạo Controller (`TaskController.java`)
Tạo class `TaskController` trong package `controller`.

**Cấu trúc gợi ý:**
```java
@RestController
@RequestMapping("/tasks") // Đường dẫn gốc là /tasks
public class TaskController {
    @Autowired
    private TaskService taskService;

    // 1. Tạo mới (POST /tasks)
    @PostMapping
    Task createTask(@RequestBody TaskCreationRequest request) {
        return taskService.createTask(request);
    }

    // 2. Lấy danh sách (GET /tasks)
    @GetMapping
    List<Task> getTasks() {
        return taskService.getTasks();
    }

    // 3. Cập nhật (PUT /tasks/{taskId})
    // ... bạn tự viết tiếp

    // 4. Xóa (DELETE /tasks/{taskId})
    // ... bạn tự viết tiếp
}
```

---

## Kiểm Tra Kết Quả (Testing)

Sau khi code xong, hãy mở Postman (hoặc file `test-api.http` trong IntelliJ) để test:

1.  **Tạo Task 1**:
    *   POST `http://localhost:8080/tasks`
    *   Body: `{"title": "Đi chợ", "description": "Mua rau, thịt", "status": "PENDING"}`
2.  **Tạo Task 2**:
    *   POST `http://localhost:8080/tasks`
    *   Body: `{"title": "Code Java", "description": "Làm bài tập CRUD", "status": "PENDING"}`
3.  **Xem danh sách**:
    *   GET `http://localhost:8080/tasks`
4.  **Sửa Task 1**:
    *   PUT `http://localhost:8080/tasks/{id_của_task_1}`
    *   Body: `{"status": "COMPLETED"}`
5.  **Xóa Task 2**:
    *   DELETE `http://localhost:8080/tasks/{id_của_task_2}`

---

## Ghi Nhớ Quan Trọng
*   **Copy-Paste thông minh**: Bạn có thể mở file `UserController`, `UserService` cũ ra xem để tham khảo, nhưng hãy **tự gõ lại** cho `Task` để tay quen với code.
*   **Lỗi thường gặp**: Quên `@Service`, `@RestController`, hoặc quên `@Autowired`. Nếu chạy lên thấy lỗi "Bean not found", hãy kiểm tra mấy cái này đầu tiên.

