# Tại sao phải chia ra `CreationRequest` và `UpdateRequest`?

Bạn cảm thấy "máy móc" là đúng, vì ở các bài tập đơn giản (CRUD cơ bản), các trường dữ liệu của Create và Update thường giống hệt nhau (Title, Description...).

Tuy nhiên, trong thực tế đi làm, **Create** và **Update** là hai thế giới hoàn toàn khác nhau. Việc tách DTO ngay từ đầu là để bảo vệ hệ thống của bạn.

Dưới đây là lý do cốt lõi (không máy móc):

## 1. Sự khác biệt về "Quyền hạn" (Security)

Hãy tưởng tượng form đăng ký nhân viên (`User`):

*   **Khi Tạo mới (`UserCreationRequest`)**:
    *   Bạn **BẮT BUỘC** phải nhập: `Username`, `Password`.
    *   Bạn **KHÔNG ĐƯỢC** nhập: `Ngày nghỉ việc` (vì mới vào làm mà).

*   **Khi Cập nhật (`UserUpdateRequest`)**:
    *   Bạn **KHÔNG ĐƯỢC** sửa: `Username` (đây là định danh, cấm sửa).
    *   Bạn **CÓ THỂ** sửa: `Ngày nghỉ việc`.
    *   `Password`: Có thể để trống (nghĩa là không đổi pass).

=> Nếu dùng chung 1 cái `UserDTO` cho cả 2 việc:
*   Lúc tạo: Người ta có thể lén gửi `ngayNghiViec` lên -> Sai logic.
*   Lúc sửa: Người ta có thể lén sửa `username` -> Hỏng hệ thống.

**Tách ra 2 DTO giúp bạn định nghĩa chính xác: "Ở hành động này, anh được phép đụng vào cái gì".**

## 2. Sự khác biệt về "Validation" (Kiểm tra đúng sai)

*   **Create**: `Password` bắt buộc phải có (NotNull).
*   **Update**: `Password` có thể Null (nghĩa là giữ nguyên pass cũ).

Nếu dùng chung 1 DTO, bạn sẽ không biết lúc nào nên bắt lỗi Null, lúc nào không.

## 3. Ví dụ thực tế với `Task` của bạn

Giả sử sau này sếp yêu cầu: *"Khi tạo Task thì Status luôn luôn phải là PENDING, không được chọn cái khác"*.

*   **Trong `TaskCreationRequest`**: Bạn xóa trường `status` đi. Người dùng không gửi lên được nữa. Code trong Service sẽ tự set `PENDING`.
*   **Trong `TaskUpdateRequest`**: Bạn vẫn giữ trường `status` để người dùng chuyển sang `COMPLETED`.

=> Nếu dùng chung DTO, bạn không thể chặn người dùng gửi `status: COMPLETED` ngay lúc tạo mới được.

## Tóm lại

Việc gọi DTO cụ thể (`CreationRequest` hay `UpdateRequest`) trong Service không chỉ là để map dữ liệu, mà là để **Service biết chính xác ngữ cảnh** mình đang xử lý là gì.

*   Nhận `CreationRequest` -> Service hiểu: "À, đây là thằng mới, mình phải generate ID cho nó, set ngày tạo cho nó".
*   Nhận `UpdateRequest` -> Service hiểu: "À, thằng này cũ rồi, mình chỉ được sửa mấy cái cho phép thôi, cấm đụng vào ID".

Code bạn viết:
```java
public Task createTask(TaskCreationRequest request) { ... }
public Task updateTask(String id, TaskUpdateRequest request) { ... }
```
Là cách viết **An toàn** và **Chuyên nghiệp**, giúp code dễ mở rộng sau này mà không sợ bug logic.

