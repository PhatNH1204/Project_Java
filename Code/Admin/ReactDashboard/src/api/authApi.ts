// gọi API đăng nhập / đăng xuất / lấy thông tin user
import axiosClient, { USE_MOCK, delay } from './axiosClient';
import type { ApiResponse } from './types/common.types';
import type { LoginRequest, LoginResponse, AdminUser } from './types/auth.types';

const mockAdmin: AdminUser = {
  id: 'admin-001',
  username: 'admin',
  email: 'admin@smartheritage.vn',
  fullName: 'Quản trị viên hệ thống',
  role: 'ROLE_ADMIN',
};

const authApi = {
  login: async (body: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    if (USE_MOCK) {
      await delay();
      if (body.username === 'admin' && body.password === 'admin123') {
        return {
          success: true,
          message: 'Đăng nhập thành công',
          data: {
            accessToken: 'mock-jwt-access-token-' + Date.now(),
            refreshToken: 'mock-jwt-refresh-token',
            expiresIn: 86400,
            user: mockAdmin,
          },
        };
      }
      throw { response: { status: 401, data: { success: false, message: 'Sai tên đăng nhập hoặc mật khẩu' } } };
    }
    const res = await axiosClient.post<ApiResponse<LoginResponse>>('/auth/login', body);
    return res.data;
  },

  logout: async (): Promise<void> => {
    if (!USE_MOCK) {
      await axiosClient.post('/auth/logout');
    }
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('admin_user');
  },

  getProfile: async (): Promise<ApiResponse<AdminUser>> => {
    if (USE_MOCK) {
      await delay();
      return { success: true, message: 'OK', data: mockAdmin };
    }
    const res = await axiosClient.get<ApiResponse<AdminUser>>('/users/me');
    return res.data;
  },
};

export default authApi;
