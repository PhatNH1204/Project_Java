// các hàm tiện ích dùng chung trong dự án

export const downloadBlob = (blob: Blob, filename: string): void => {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
};

export const formatDate = (dateStr?: string): string => {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  });
};

export const formatDateTime = (dateStr?: string): string => {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
};

export const formatDuration = (seconds?: number): string => {
  if (!seconds) return '—';
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return m > 0 ? `${m}p ${s}s` : `${s}s`;
};

export const formatNumber = (n: number): string =>
  n.toLocaleString('vi-VN');

export const batteryColor = (level?: number): string => {
  if (!level) return '#9ca3af';
  if (level > 50) return '#10b981';
  if (level > 20) return '#f59e0b';
  return '#ef4444';
};

export const beaconStatusText: Record<string, string> = {
  ACTIVE: 'Hoạt động',
  INACTIVE: 'Không hoạt động',
  DAMAGED: 'Hỏng',
  UNASSIGNED: 'Chưa gán',
};

export const siteStatusText: Record<string, string> = {
  ACTIVE: 'Mở cửa',
  INACTIVE: 'Đóng cửa',
  MAINTENANCE: 'Đang bảo trì',
};

export const feedbackStatusText: Record<string, string> = {
  PENDING: 'Chờ duyệt',
  APPROVED: 'Đã duyệt',
  REJECTED: 'Từ chối',
};
