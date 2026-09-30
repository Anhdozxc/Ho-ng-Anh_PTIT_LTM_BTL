-- Khởi tạo Database nếu chưa có
CREATE DATABASE IF NOT EXISTS ltm_btl;

-- Chỉ định sử dụng Database ltm_btl
USE ltm_btl;

-- Xóa bảng worker trước (vì chứa khóa ngoại tham chiếu đến province)
DROP TABLE IF EXISTS worker;
-- Xóa bảng province sau
DROP TABLE IF EXISTS province;

-- Tạo bảng province lưu trữ danh sách Tỉnh/Thành phố
CREATE TABLE province (
    id INT AUTO_INCREMENT PRIMARY KEY, -- Khóa chính, tự tăng
    name VARCHAR(100) NOT NULL UNIQUE, -- Tên tỉnh, không được rỗng và không trùng nhau
    description VARCHAR(255)           -- Mô tả thêm về tỉnh
);

-- Tạo bảng worker lưu trữ thông tin Công nhân
CREATE TABLE worker (
    id INT AUTO_INCREMENT PRIMARY KEY, -- Khóa chính của công nhân
    full_name VARCHAR(100) NOT NULL,   -- Họ tên công nhân
    birth_year INT NOT NULL,           -- Năm sinh công nhân
    salary DECIMAL(12,2) NOT NULL,     -- Mức lương công nhân
    province_id INT NOT NULL,          -- Khóa ngoại trỏ đến bảng province (quê quán)
    
    -- Công nhân tham chiếu tới tỉnh/thành phố
    -- Không cho xóa tỉnh nếu vẫn còn công nhân thuộc tỉnh đó
    FOREIGN KEY (province_id) REFERENCES province(id) ON DELETE RESTRICT
);

-- Dữ liệu mẫu cho bảng province
INSERT INTO province (name, description) VALUES 
('Hà Nội', 'Thủ đô Việt Nam'),
('TP. Hồ Chí Minh', 'Trung tâm kinh tế lớn nhất'),
('Đà Nẵng', 'Thành phố đáng sống'),
('Hải Phòng', 'Thành phố hoa phượng đỏ'),
('Cần Thơ', 'Trung tâm đồng bằng sông Cửu Long');

-- Dữ liệu mẫu cho bảng worker
INSERT INTO worker (full_name, birth_year, salary, province_id) VALUES 
('Nguyễn Văn A', 1990, 15000000, 1),
('Trần Thị B', 1995, 12000000, 2),
('Lê Văn C', 1988, 20000000, 1),
('Phạm Thị D', 2000, 10000000, 3),
('Hoàng Văn E', 1992, 18000000, 4),
('Vũ Thị F', 1998, 11000000, 5),
('Đặng Văn G', 1985, 25000000, 2);
