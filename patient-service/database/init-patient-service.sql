-- postgresql
-- patient table
CREATE TABLE IF NOT EXISTS patient (
    id SERIAL PRIMARY KEY,
    fullname VARCHAR(50) NOT NULL,
    address VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    email VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    gender VARCHAR(10) NOT NULL,
    phone VARCHAR(15) NOT NULL
)

-- insert dummy data into patient table
INSERT INTO patient (fullname, address, date_of_birth, email, gender, phone)
VALUES
('Nguyễn Văn A', '123 Lê Lợi, Hà Nội', '1990-05-12', 'nguyenvana@example.com', 'Male', '0912345678'),
('Trần Thị B', '456 Trần Hưng Đạo, Đà Nẵng', '1985-10-20', 'tranthib@example.com', 'Female', '0934567890'),
('Lê Văn C', '789 Nguyễn Huệ, TP.HCM', '1992-03-15', 'levanc@example.com', 'Male', '0987654321'),
('Phạm Thị D', '234 Pasteur, Cần Thơ', '1995-07-30', 'phamthid@example.com', 'Female', '0968123456'),
('Đỗ Mạnh E', '12 Nguyễn Trãi, Huế', '1988-12-01', 'domanhe@example.com', 'Male', '0909876543');
