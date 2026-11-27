# Backend Smart Heritage

REST API Spring Boot cho prototype Smart Heritage. Backend hỗ trợ đăng ký/đăng nhập JWT, phân quyền quản trị, quản lý di tích/hiện vật/nội dung đa ngôn ngữ, iBeacon, yêu thích/lịch sử tham quan/đánh giá, thống kê, báo cáo PDF/Excel và tài liệu OpenAPI.

## Yêu cầu

- JDK 17
- Maven 3.9 trở lên
- PostgreSQL 16 trở lên hoặc Docker Desktop

Xem [README chính](../../README.md) để biết hướng dẫn cài đặt Windows, chạy ứng dụng Android và giải thích về PostgreSQL/SSMS.

## Chạy database cục bộ

Từ thư mục này:

```powershell
docker compose up -d db
```

Compose chỉ dành cho phát triển cục bộ, mở PostgreSQL tại `localhost:5432`. Nếu đã có PostgreSQL, hãy tạo database và dùng thông tin tài khoản tương ứng.

## Chạy backend

Mở PowerShell tại `Code\Backend`, thiết lập các biến môi trường rồi chạy:

```powershell
$env:POSTGRES_URL = 'jdbc:postgresql://localhost:5432/smart_heritage'
$env:POSTGRES_USER = 'smart_heritage'
$env:POSTGRES_PASSWORD = 'smart_heritage_dev'
$secretBytes = New-Object byte[] 48
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($secretBytes)
$rng.Dispose()
$env:JWT_SECRET = [Convert]::ToBase64String($secretBytes)
$env:ADMIN_USERNAME = 'admin'
$env:ADMIN_EMAIL = 'admin@localhost'
$env:ADMIN_PASSWORD = 'ChangeThisAdminPassword123'
mvn spring-boot:run
```

Đặt mật khẩu quản trị riêng, tối thiểu 12 ký tự. Tài khoản được bootstrap nếu chưa tồn tại. Flyway áp dụng migration và Hibernate kiểm tra cấu trúc database.

Mặc định trang quản trị được phục vụ từ `Code\Admin`. Đặt biến `ADMIN_UI_DIR` nếu muốn dùng thư mục khác.

## Địa chỉ hữu ích

- Trang vào: <http://localhost:8080/>
- Cổng quản trị: <http://localhost:8080/admin>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

## Ví dụ API

Đăng ký khách tham quan:

```powershell
$body = @{
    username = 'visitor01'
    email = 'visitor@example.com'
    password = 'StrongPassword123'
    languageCode = 'vi'
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
    -Uri 'http://localhost:8080/api/auth/register' `
    -ContentType 'application/json' -Body $body
```

Đăng nhập bằng email hoặc tên đăng nhập:

```powershell
$body = @{
    identifier = 'visitor@example.com'
    password = 'StrongPassword123'
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
    -Uri 'http://localhost:8080/api/auth/login' `
    -ContentType 'application/json' -Body $body
```

## Kiểm thử

```powershell
mvn test
```

Integration test dùng H2 ở chế độ tương thích PostgreSQL nên không cần bật PostgreSQL.
