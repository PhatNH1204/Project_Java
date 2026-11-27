# React Dashboard Smart Heritage (Demo)

Dashboard React/TypeScript được gộp từ `Project_Java-Quan`, gồm các màn hình demo cho di tích, hiện vật, beacon, thống kê, đánh giá và báo cáo. Bản mock tải báo cáo dạng CSV để mở bằng Excel.

## Chạy

Cần Node.js 20.19 trở lên và npm 10 trở lên.

```powershell
npm ci
npm run dev
```

Mở <http://localhost:5173>. Tài khoản demo: `admin` / `admin123`.

Kiểm tra build production:

```powershell
npm run build
```

## Lưu ý: dữ liệu mock

Dashboard hiện chạy bằng dữ liệu giả lập. Kiểu request/response và một số endpoint chưa khớp với Spring Boot backend tích hợp. Không đặt `VITE_USE_MOCK=false` và không dùng giao diện này để cập nhật dữ liệu thật. Dùng trang quản trị API-connected tại <http://localhost:8080/admin>.

Mã nguồn được giữ trong dự án chính để có thể đồng bộ API ở bước phát triển tiếp theo.
