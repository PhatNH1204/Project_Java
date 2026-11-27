// auth types

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: AdminUser;
}

export interface AdminUser {
  id: string;
  username: string;
  email: string;
  fullName: string;
  role: string;
  avatarUrl?: string;
}
