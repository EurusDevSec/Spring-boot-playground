# Tại sao Create, Update, Delete lại khác nhau? (Quy Chuẩn & Cách Nhớ)

Bạn thắc mắc rất đúng. Mới nhìn thì thấy code mỗi cái một kiểu, nhưng thực ra nó tuân theo một **quy chuẩn quốc tế** gọi là **RESTful API**. Bất kỳ dự án Java, C#, hay NodeJS nào cũng đều làm theo logic này.

Dưới đây là cách giải thích đơn giản để bạn hiểu bản chất và không bao giờ quên.

## 1. Tư Duy Đời Thường (Mental Model)

Hãy tưởng tượng Database là một **Cái Kho Hàng**.

*   **CREATE (POST)**: Bạn muốn **nhập kho** một món hàng mới.
    *   Bạn cần gì? Cần thông tin món hàng (Tên, giá...).
    *   Bạn có cần biết nó nằm ở kệ số mấy không? Không, kho sẽ tự xếp và đưa cho bạn cái phiếu ghi mã số (ID).
    *   -> **Code**: Chỉ cần `RequestBody` (thông tin), không cần ID đầu vào.

*   **UPDATE (PUT)**: Bạn muốn **sửa lại** thông tin một món hàng đã có (ví dụ dán lại tem giá).
    *   Bạn cần gì?
        1.  **Mã số (ID)**: Để biết chạy đến kệ nào mà lấy hàng ra.
        2.  **Thông tin mới**: Cái tem giá mới để dán vào.
    *   -> **Code**: Cần cả `PathVariable` (ID) VÀ `RequestBody` (thông tin mới).

*   **DELETE (DELETE)**: Bạn muốn **vứt** món hàng đi.
    *   Bạn cần gì? Chỉ cần **Mã số (ID)** để biết vứt cái nào. Không cần mang theo cái gì khác cả.
    *   -> **Code**: Chỉ cần `PathVariable` (ID).

---

## 2. Phân Tích Chi Tiết & Cách Nhớ

### A. CREATE (Tạo mới) - "Người Xây Dựng"
*   **Hành động**: Tạo ra cái chưa tồn tại.
*   **Controller**:
    *   Dùng `@PostMapping`.
    *   **Input**: Chỉ nhận `DTO` (thông tin user).
*   **Service**:
    *   `new User()`: Phải tạo ra một tờ giấy trắng (Entity mới).
    *   Map dữ liệu từ DTO vào Entity.
    *   `save()`: Lưu xong mới có ID.

### B. UPDATE (Cập nhật) - "Thợ Sửa Chữa"
*   **Hành động**: Tìm cái cũ -> Sửa lại -> Lưu đè.
*   **Controller**:
    *   Dùng `@PutMapping("/{id}")`.
    *   **Input**: Cần **2 thứ**: `ID` (để tìm) + `DTO` (dữ liệu mới).
*   **Service**:
    *   **Khác biệt lớn nhất**: Không được `new User()`.
    *   Phải `findById(id)`: Lôi thằng cũ từ DB lên.
    *   Sau đó mới set lại thông tin mới đè lên thông tin cũ.
    *   `save()`: Lúc này JPA hiểu là "Cập nhật" vì user này đã có ID rồi.

### C. DELETE (Xóa) - "Người Dọn Dẹp"
*   **Hành động**: Tìm -> Xóa.
*   **Controller**:
    *   Dùng `@DeleteMapping("/{id}")`.
    *   **Input**: Chỉ cần `ID`.
*   **Service**:
    *   `deleteById(id)`: Gọn lẹ.

---

## 3. Bảng Tóm Tắt (Cheat Sheet)

Đây là quy chuẩn chung, bạn có thể lưu lại bảng này:

| Chức năng | HTTP Method | Cần ID trên URL? | Cần Body (JSON)? | Logic Service |
| :--- | :--- | :--- | :--- | :--- |
| **Tạo mới** | `@PostMapping` | ❌ Không | ✅ Có (DTO) | `new Entity` -> `save` |
| **Cập nhật** | `@PutMapping` | ✅ Có (`/{id}`) | ✅ Có (DTO) | `findById` -> sửa field -> `save` |
| **Xóa** | `@DeleteMapping` | ✅ Có (`/{id}`) | ❌ Không | `deleteById` |
| **Xem 1 cái** | `@GetMapping` | ✅ Có (`/{id}`) | ❌ Không | `findById` |
| **Xem tất cả**| `@GetMapping` | ❌ Không | ❌ Không | `findAll` |

## 4. Mẹo để nhớ khi code lại

Khi bạn bắt đầu viết một API, hãy tự hỏi 2 câu:

1.  **"Mình đang làm việc với thằng nào?"**
    *   Nếu là thằng **mới tinh**: Không cần ID -> `POST`.
    *   Nếu là thằng **đã có**: Cần ID -> `PUT` hoặc `DELETE`.

2.  **"Mình có cần gửi dữ liệu gì lên không?"**
    *   Nếu cần gửi thông tin (tên, tuổi...): Cần `@RequestBody`.
    *   Nếu chỉ cần chỉ định đối tượng: Chỉ cần `@PathVariable`.

**Ví dụ áp dụng:**
*   *Đề bài: Viết API đổi mật khẩu.*
    *   Đổi mật khẩu cho ai? -> Cho user đã có -> Cần ID (`/{id}`).
    *   Đổi thành gì? -> Mật khẩu mới -> Cần Body.
    *   => Kết luận: Dùng `PUT`, cần cả ID và Body.

*   *Đề bài: Viết API đuổi việc nhân viên.*
    *   Đuổi ai? -> Nhân viên A -> Cần ID (`/{id}`).
    *   Cần gửi thêm gì không? -> Không, đuổi là đuổi thôi.
    *   => Kết luận: Dùng `DELETE`, chỉ cần ID.

