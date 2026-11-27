# Smart Heritage

Đồ án Smart Heritage là hệ thống tham quan di tích tương tác qua iBeacon/BLE, cung cấp nội dung đa ngôn ngữ, ghi nhận tương tác của khách và cổng quản trị.

## Các phần trong dự án chính

- `Code\Backend`: REST API Spring Boot cho tài khoản/JWT, quản lý di tích, hiện vật và bản dịch, beacon, lịch sử/yêu thích/đánh giá, thống kê, báo cáo PDF/Excel và OpenAPI.
- `Code\Admin`: trang quản trị kết nối trực tiếp backend tại `/admin`.
- `Code\Admin\ReactDashboard`: mã React/TypeScript được gộp từ `Project_Java-Quan`; hiện là bản demo dùng dữ liệu giả lập, xem README trong thư mục trước khi chạy.
- `Code\MobileApp`: ứng dụng Android cho khách tham quan, đăng nhập, duyệt nội dung, yêu thích/lịch sử/đánh giá và quét iBeacon BLE.
- `Documentation`: sơ đồ UML và tài liệu hỗ trợ.

Backend cùng trang quản trị API-connected là bản tích hợp chính. `Project_Java-ngan` có backend đăng nhập đơn giản hơn, trong khi backend chính đã mở rộng luồng đó cho toàn hệ thống. Phần Flutter trong thư mục nguồn đó chỉ là scaffold mặc định (màn hình trống), nên dự án chính dùng ứng dụng Android đầy đủ hơn tại `Code\MobileApp` thay vì giữ hai ứng dụng di động dở dang. Hai thư mục nguồn ban đầu vẫn được giữ để đối chiếu.

## Yêu cầu

- JDK 17
- Maven 3.9 trở lên
- PostgreSQL 16 trở lên (hoặc Docker Desktop để chạy PostgreSQL cục bộ)
- Android Studio và Android SDK 35 để chạy ứng dụng khách
- Node.js 20.19 trở lên và npm 10 trở lên nếu chạy React dashboard tùy chọn

## Chạy hệ thống tích hợp trên Windows

### 1. Khởi động PostgreSQL

Mở Docker Desktop, sau đó chạy trong PowerShell:

```powershell
Set-Location 'D:\Java\Project_Java\Code\Backend'
docker compose up -d db
```

Database phát triển sẽ chạy tại `localhost:5432` với tên database/tài khoản/mật khẩu là `smart_heritage`. Nếu đã cài PostgreSQL, hãy tạo database và dùng thông tin kết nối của bạn ở bước kế tiếp.

### 2. Khởi động backend

Trong PowerShell tại `D:\Java\Project_Java\Code\Backend`, thiết lập biến môi trường cho cửa sổ đó rồi chạy:

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

Giữ cửa sổ này mở. Flyway tự tạo cấu trúc database ở lần chạy đầu và nạp dữ liệu demo song ngữ cho 7 khu di tích cùng 22 hiện vật. Tài khoản quản trị được tạo tự động; hãy thay mật khẩu ví dụ bằng mật khẩu riêng đủ mạnh và không dùng mật khẩu ví dụ ngoài máy demo. Các bản ghi iBeacon thật vẫn cần được đăng ký riêng theo UUID, major và minor của thiết bị. Một số hiện vật có liên kết ảnh Wikimedia Commons; cần Internet để mở ảnh. Nguồn và giấy phép được ghi tại [Documentation/Media_Credits.md](./Documentation/Media_Credits.md).

### 3. Mở giao diện và tài liệu API

- Trang quản trị: <http://localhost:8080/admin>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

Trang quản trị dùng backend và PostgreSQL thật. Tài khoản quản trị đăng nhập bằng `ADMIN_USERNAME` và `ADMIN_PASSWORD` đã thiết lập. Có thể đăng ký khách tham quan qua ứng dụng Android hoặc `POST /api/auth/register`.

### 4. Chạy kiểm thử backend

Mở PowerShell khác:

```powershell
Set-Location 'D:\Java\Project_Java\Code\Backend'
mvn test
```

Integration test dùng H2 trong bộ nhớ, không cần PostgreSQL đang chạy.

## Chạy ứng dụng Android

1. Mở `D:\Java\Project_Java\Code\MobileApp` trong Android Studio và chờ Gradle sync (cần Android SDK 35 và JDK 17).
2. Khởi động backend trước. Bản debug mặc định kết nối `http://10.0.2.2:8080` — địa chỉ từ Android Emulator tới máy tính.
3. Chạy cấu hình `app` trên Android 8.0 trở lên hoặc điện thoại có BLE. Cấp quyền Bluetooth/thiết bị lân cận, vị trí và thông báo khi được hỏi.
4. Nếu chạy điện thoại thật, đặt thuộc tính Gradle `heritageApiUrl` thành URL LAN của máy tính (ví dụ `-PheritageApiUrl=http://192.168.1.10:8080`) và đảm bảo hai thiết bị truy cập được cổng 8080. `10.0.2.2` chỉ dùng cho Android Emulator. HTTP chỉ được cho phép ở debug; bản release vẫn tắt cleartext traffic.

Quét iBeacon cần điện thoại hỗ trợ BLE và beacon đã được cấu hình trong hệ thống. Emulator có thể kiểm tra đăng nhập/nội dung nhưng không mô phỏng tín hiệu beacon thật.

## React dashboard tùy chọn

Mã React từ `Project_Java-Quan` đã được gộp tại `Code\Admin\ReactDashboard`. Giao diện này hiện dùng dữ liệu mock và chưa tương thích hợp đồng API backend. Chạy bằng:

```powershell
Set-Location 'D:\Java\Project_Java\Code\Admin\ReactDashboard'
npm ci
npm run dev
```

Mở <http://localhost:5173>, đăng nhập demo `admin` / `admin123`. Không đặt `VITE_USE_MOCK=false`; các endpoint và kiểu dữ liệu của bản React cần được đồng bộ trước khi thay thế trang quản trị tích hợp tại `/admin`.

## PostgreSQL và SSMS

Backend hiện dùng PostgreSQL, bao gồm JDBC driver và migration Flyway. SSMS (SQL Server Management Studio) chỉ quản lý Microsoft SQL Server, không kết nối/quản lý PostgreSQL. Với cấu hình hiện tại, hãy dùng pgAdmin hoặc DBeaver. Muốn dùng SSMS thì cần chuyển driver, migration SQL và cấu hình database của backend sang SQL Server; chỉ cài SSMS là chưa đủ.

## Phạm vi hiện tại

Đây là prototype đồ án có thể chạy để trình diễn, chưa phải cấu hình production. Khi triển khai thực tế cần HTTPS, quản lý secrets, backup, giám sát, cùng cấu hình beacon và nội dung theo từng địa điểm.
