# TV1 - Đăng ký và đăng nhập

Backend Spring Boot đơn giản cho chức năng đăng ký và đăng nhập khách tham quan.
Thông tin người dùng được lưu trong PostgreSQL, mật khẩu được mã hóa bằng BCrypt
và mỗi tài khoản mới được gán vai trò `VISITOR`.

> Bản này chỉ minh họa đăng ký/đăng nhập; chưa cấp JWT, quản lý hồ sơ, đổi mật
> khẩu, refresh token hoặc có API dành riêng cho quản trị viên.

## Yêu cầu

- Java 17 trở lên
- Maven 3.9 trở lên
- PostgreSQL

## Chạy backend

Tạo database PostgreSQL tên `smart_heritage`, sau đó mở PowerShell và chạy:

```powershell
cd D:\Java\Project_Java\Code\Backend
$env:POSTGRES_URL = "jdbc:postgresql://localhost:5432/smart_heritage"
$env:POSTGRES_USER = "postgres"
$env:POSTGRES_PASSWORD = "mật-khẩu-PostgreSQL-của-bạn"
mvn spring-boot:run
```

Flyway sẽ tự tạo các bảng `app_role`, `app_user` và thêm các vai trò Visitor,
Administrator. Hibernate kiểm tra cấu trúc database khi ứng dụng khởi động.

## Thử API

### Đăng ký

Mở một cửa sổ PowerShell khác khi backend đang chạy:

```powershell
$body = @{
    username = "visitor01"
    email = "visitor@example.com"
    password = "StrongPassword123"
    languageCode = "vi"
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8080/api/auth/register" `
    -ContentType "application/json" -Body $body
```

### Đăng nhập

Có thể đăng nhập bằng email hoặc username:

```powershell
$body = @{
    identifier = "visitor@example.com"
    password = "StrongPassword123"
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8080/api/auth/login" `
    -ContentType "application/json" -Body $body
```

Đăng ký lại bằng username hoặc email đã tồn tại sẽ bị từ chối.

## Chạy kiểm thử

```powershell
mvn test
```

Các integration test sử dụng H2 trong bộ nhớ nên không cần PostgreSQL.
