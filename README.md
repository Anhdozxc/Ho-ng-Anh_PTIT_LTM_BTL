# Quản Lý Công Nhân (Client-Server bằng Java Socket thuần)

Đây là Bài tập lớn môn Lập trình mạng (INT1433) tại PTIT.
Dự án sử dụng mô hình Client-Server giao tiếp qua giao thức TCP/IP, kết nối CSDL MySQL bằng JDBC thuần. (Hoàn thiện PHASE 3).

## 1. Giới thiệu & Mục tiêu
Dự án là phần mềm quản lý công nhân, cho phép Thêm/Sửa/Xóa/Tìm kiếm công nhân và Xóa Tỉnh/TP. 
Mục tiêu là mô phỏng luồng giao tiếp mạng qua Socket và làm việc trực tiếp với CSDL mà không phụ thuộc vào Framework nào.

## 2. Công nghệ sử dụng
- **Ngôn ngữ**: Java 21
- **Giao thức mạng**: TCP/IP bằng Java Socket thuần (`ServerSocket` / `Socket`).
- **Truyền tải dữ liệu**: Serialization thông qua `ObjectOutputStream` và `ObjectInputStream`.
- **Cơ sở dữ liệu**: MySQL 8.0
- **Kết nối CSDL**: JDBC thuần với `PreparedStatement`.
- **Quản lý Dependency**: Maven.

## 3. Kiến trúc Client-Server & Cấu trúc Project

```text
D:\LTM_PTIT\BTL\
├── pom.xml             # Quản lý dependency (MySQL JDBC)
├── database/
│   └── schema.sql      # Cấu trúc DB và dữ liệu mẫu
├── src/main/java/
│   ├── shared/         # Chứa Model và Protocol dùng chung (để Serialization 2 bên khớp nhau).
│   ├── server/         # Chứa ServerApp, ClientHandler xử lý đa luồng, Service xử lý logic/validation, và DAO chứa câu lệnh JDBC.
│   └── client/         # ClientApp chứa giao diện Console và giao tiếp Socket với Server.
└── README.md           
```

## 4. Database
Sử dụng 2 bảng:
- `province(id, name, description)`
- `worker(id, full_name, birth_year, salary, province_id)` 
*(Ràng buộc khóa ngoại `ON DELETE RESTRICT` để không xóa được Tỉnh nếu vẫn còn Công nhân)*.

## 5. TCP Protocol
Sử dụng Request/Response object.
- **Request**: `(Operation operation, Object data)`
- **Response**: `(boolean success, String message, Object data)`

Các Operation: `GET_PROVINCES`, `ADD_WORKER`, `GET_WORKERS`, `UPDATE_WORKER`, `SEARCH_WORKERS`, `DELETE_WORKER`, `DELETE_PROVINCE`.

## 6. Mô tả chức năng hoàn thiện
- Thêm / Sửa công nhân: Xác thực input chặt chẽ, quê quán được Load tự động từ DB.
- Lấy danh sách công nhân: JOIN với bảng Province để hiển thị Tên thay vì ID.
- Tìm kiếm nâng cao: Cho phép lọc theo nhiều khoảng (`min`/`max` tuổi, `min`/`max` lương, `province`, `keyword`) theo cơ chế động.
- Xóa công nhân theo từ khóa: Server tìm kiếm công nhân theo từ khóa, Client hiển thị danh sách kết quả, người dùng chọn ID công nhân cần xóa, sau đó Server xóa đúng công nhân theo ID đã chọn và Client load lại danh sách.
- Xóa tỉnh: Nếu tỉnh đang có công nhân, bắt Exception Foreign Key trong JDBC và báo lỗi lên Client lịch sự.

## 7. Cách chạy ứng dụng
1. Mở project trong IntelliJ IDEA, import qua Maven.
2. Khởi tạo CSDL MySQL bằng script `database/schema.sql`.
3. Kiểm tra thông tin đăng nhập trong `server.dao.DBConnection` (mặc định: `root`/`12345678`, `localhost:3306`).
4. **Khởi động Server**: Chạy class `server.ServerApp`.
5. **Khởi động Client**: Chạy class `client.ClientApp` (có thể chạy nhiều tab Client cùng lúc).

## 8. Một số case lỗi đã handle (Testing)
- Không nhập dữ liệu tuổi/lương mà nhấn kí tự chữ -> Client bắt lỗi ngay tại Console.
- Nhập tuổi lố / lương âm -> Server validation báo lỗi trả về.
- Xóa tỉnh Hà Nội (đang có người) -> Server bắt SQL Exception báo về Client.
- Chạy 2 Client cùng lúc (Multi-threading) -> Server đáp ứng bình thường do mỗi Client chạy trên 1 Thread riêng.
