// cấu hình axios dùng chung
// VITE_USE_MOCK=true thì dùng mock data, false thì kết nối backend thật
import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';

export const USE_MOCK = import.meta.env.VITE_USE_MOCK !== 'false';
export const API_BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';

const axiosClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15_000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// ---- REQUEST INTERCEPTOR: đính kèm JWT token ----
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// ---- RESPONSE INTERCEPTOR: xử lý lỗi 401 / 403 ----
axiosClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error) => {
    const status = error?.response?.status;

    if (status === 401) {
      // Token hết hạn → xóa và redirect về trang Login
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      window.location.href = '/login';
    }

    if (status === 403) {
      console.error('[API] Không có quyền truy cập tài nguyên này.');
    }

    return Promise.reject(error);
  },
);

// ---- Helper: mock delay ----
export const delay = (ms = 350): Promise<void> =>
  new Promise((resolve) => setTimeout(resolve, ms));

export default axiosClient;
