# Smart Heritage — Admin Dashboard

Web admin cho hệ thống **Smart Heritage** — ứng dụng tham quan di tích lịch sử tương tác qua iBeacon.

Phần **TV5** trong nhóm: Analytics, Feedback & Reporting.

---

## Giới thiệu

Smart Heritage là hệ thống giúp du khách tham quan các di tích lịch sử tại Việt Nam (Đại Nội Huế, Lăng Khải Định, Lăng Tự Đức...) thông qua thiết bị iBeacon. Khi du khách đứng gần một hiện vật, ứng dụng mobile tự động nhận diện và hiển thị thông tin, hình ảnh, âm thanh thuyết minh.

Web admin này cho phép quản trị viên:
- Xem thống kê lượt tham quan theo ngày
- Duyệt hoặc từ chối đánh giá của du khách
- Xuất báo cáo Excel theo khoảng thời gian

---

## Chạy project

```bash
npm install
npm run dev
```

Mở trình duyệt tại `http://localhost:5173`

Đăng nhập: `admin` / `admin123`

> Mặc định chạy ở chế độ **mock** — không cần backend, dữ liệu giả lập sẵn trong code.

---

## Cấu trúc thư mục

```
src/
├── api/
│   ├── types/          TypeScript interfaces cho toàn dự án
│   ├── mocks/          Dữ liệu giả lập khi chưa có backend
│   ├── analyticsApi.ts Gọi API thống kê, feedback, xuất Excel
│   ├── authApi.ts      Đăng nhập / đăng xuất
│   └── axiosClient.ts  Cấu hình Axios + JWT interceptor
├── components/
│   └── layout/         Sidebar, Header
├── pages/
│   ├── Dashboard/      Tổng quan KPI và biểu đồ (TV5)
│   ├── Feedback/       Duyệt đánh giá du khách (TV5)
│   ├── Reports/        Báo cáo và xuất Excel (TV5)
│   ├── HeritageSites/  Quản lý di tích (TV4)
│   ├── Artifacts/      Quản lý hiện vật (TV4)
│   └── Beacons/        Quản lý iBeacon (TV4)
├── routes/             React Router
├── store/              Auth state (Zustand)
└── utils/              Hàm tiện ích dùng chung
```

---

## Kết nối backend

Khi backend (TV1 — Spring Boot) đã sẵn sàng, chỉ cần tạo file `.env.local`:

```env
VITE_USE_MOCK=false
VITE_API_URL=http://localhost:8080/api/v1
```

Chạy lại `npm run dev` là xong, không cần sửa code.

---

## Tech stack

| Thư viện | Mục đích |
|----------|----------|
| React 19 + TypeScript | UI framework |
| Vite | Build tool |
| Ant Design 6 | Component library |
| Recharts | Biểu đồ thống kê |
| Axios | HTTP client |
| Zustand | Quản lý state đăng nhập |
| SheetJS (xlsx) | Xuất file Excel |
| React Router 7 | Điều hướng trang |

---

## Phân công nhóm

| Thành viên | Phụ trách |
|-----------|-----------|
| TV1 | Backend (Spring Boot), Authentication, JWT |
| TV2 | Mobile App (React Native/Flutter), BLE scan |
| TV3 | iBeacon Engine, Proximity Detection |
| TV4 | CMS: quản lý di tích, hiện vật, beacon |
| TV5 | Analytics, Feedback, Báo cáo — **repo này** |
