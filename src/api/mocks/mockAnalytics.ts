// mock data cho analytics - TV5 tự làm phần này
import type {
  AnalyticsOverview,
  PopularArtifact,
  LanguageStat,
  InteractionTimelinePoint,
  Feedback,
} from '../types/analytics.types';

export const mockAnalyticsOverview: AnalyticsOverview = {
  totalVisitorsToday: 342,
  totalVisitorsThisMonth: 7841,
  totalInteractions: 24593,
  avgVisitDurationMinutes: 42,
  activeBeacons: 5,
  totalArtifacts: 100,
};

export const mockPopularArtifacts: PopularArtifact[] = [
  {
    artifactId: 'art-001',
    artifactName: 'Ngai Vàng Triều Nguyễn',
    siteName: 'Đại Nội Huế',
    thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Throne_room_Hue.jpg/800px-Throne_room_Hue.jpg',
    interactionCount: 1248,
    avgDurationSeconds: 198,
  },
  {
    artifactId: 'art-002',
    artifactName: 'Áo Long Bào Hoàng Đế',
    siteName: 'Đại Nội Huế',
    thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5b/Dragon_robe_Hue.jpg/800px-Dragon_robe_Hue.jpg',
    interactionCount: 892,
    avgDurationSeconds: 165,
  },
  {
    artifactId: 'art-004',
    artifactName: 'Mũ Triều Phục Vua',
    siteName: 'Đại Nội Huế',
    thumbnailUrl: 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b?w=400',
    interactionCount: 743,
    avgDurationSeconds: 142,
  },
  {
    artifactId: 'art-003',
    artifactName: 'Đỉnh Vàng Khải Định',
    siteName: 'Lăng Khải Định',
    thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/9/9e/Lang_Khai_Dinh.jpg',
    interactionCount: 567,
    avgDurationSeconds: 125,
  },
  {
    artifactId: 'art-005',
    artifactName: 'Trống Đồng Điện Lương Khiêm',
    siteName: 'Lăng Tự Đức',
    thumbnailUrl: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400',
    interactionCount: 421,
    avgDurationSeconds: 110,
  },
];

export const mockLanguageStats: LanguageStat[] = [
  { languageCode: 'vi', languageName: 'Tiếng Việt', count: 5389, percentage: 68.7 },
  { languageCode: 'en', languageName: 'English', count: 2452, percentage: 31.3 },
];

// 7 ngày gần nhất tính từ hôm nay
const today = new Date();
export const mockInteractionTimeline: InteractionTimelinePoint[] = Array.from({ length: 7 }, (_, i) => {
  const d = new Date(today);
  d.setDate(d.getDate() - (6 - i));
  return {
    date: d.toISOString().split('T')[0],
    interactions: Math.floor(2000 + Math.random() * 1500),
    visitors: Math.floor(200 + Math.random() * 200),
  };
});

export const mockFeedbacks: Feedback[] = [
  {
    id: 'fb-001',
    visitorId: 'vis-101',
    visitorName: 'Nguyễn Thị Lan',
    artifactId: 'art-001',
    artifactName: 'Ngai Vàng Triều Nguyễn',
    siteId: 'site-001',
    siteName: 'Đại Nội Huế',
    rating: 5,
    comment: 'Rất ấn tượng! Âm thanh thuyết minh rõ ràng, thông tin phong phú. Ứng dụng hoạt động rất mượt khi đứng gần beacon.',
    status: 'APPROVED',
    createdAt: '2026-11-23T10:30:00Z',
  },
  {
    id: 'fb-002',
    visitorId: 'vis-102',
    visitorName: 'John Smith',
    artifactId: 'art-002',
    artifactName: 'Imperial Dragon Robe',
    siteId: 'site-001',
    siteName: 'Đại Nội Huế',
    rating: 4,
    comment: 'The English audio guide was very informative. Would love to see more artifacts added to the app.',
    status: 'APPROVED',
    createdAt: '2026-11-23T14:15:00Z',
  },
  {
    id: 'fb-003',
    visitorId: 'vis-103',
    visitorName: 'Trần Minh Khoa',
    artifactId: undefined,
    artifactName: undefined,
    siteId: 'site-002',
    siteName: 'Lăng Khải Định',
    rating: 3,
    comment: 'Một số beacon không phát hiện được khi đứng gần, cần kiểm tra lại thiết bị.',
    status: 'PENDING',
    createdAt: '2026-11-24T08:00:00Z',
  },
  {
    id: 'fb-004',
    visitorId: 'vis-104',
    visitorName: 'Lê Thu Hương',
    artifactId: 'art-003',
    artifactName: 'Đỉnh Vàng Khải Định',
    siteId: 'site-002',
    siteName: 'Lăng Khải Định',
    rating: 5,
    comment: 'Tuyệt vời! Giải thích lịch sử rất sâu sắc và chi tiết. Tôi học được rất nhiều điều về triều Nguyễn.',
    status: 'PENDING',
    createdAt: '2026-11-24T09:00:00Z',
  },
];
