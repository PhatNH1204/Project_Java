// các type dùng chung cho toàn dự án

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  pagination?: PaginationMeta;
}

export interface PaginationMeta {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface PageRequest {
  page?: number;
  size?: number;
  search?: string;
  sort?: string;
}

export type LanguageCode = 'vi' | 'en';

export type MediaType = 'IMAGE' | 'AUDIO' | 'VIDEO';

export type BeaconStatus = 'ACTIVE' | 'INACTIVE' | 'DAMAGED' | 'UNASSIGNED';

export type SiteStatus = 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE';

export type FeedbackStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export type UserRole = 'ROLE_ADMIN' | 'ROLE_VISITOR';

export interface SelectOption {
  label: string;
  value: string | number;
}
