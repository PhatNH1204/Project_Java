// gọi API thống kê, feedback và xuất báo cáo
import axiosClient, { USE_MOCK, delay } from './axiosClient';
import type { ApiResponse, PageRequest, PaginationMeta } from './types/common.types';
import type {
  AnalyticsOverview,
  PopularArtifact,
  LanguageStat,
  InteractionTimelinePoint,
  Feedback,
  UpdateFeedbackStatusRequest,
} from './types/analytics.types';
import {
  mockAnalyticsOverview,
  mockPopularArtifacts,
  mockLanguageStats,
  mockInteractionTimeline,
  mockFeedbacks,
} from './mocks/mockAnalytics';

let _mockFeedbacks = [...mockFeedbacks];

const analyticsApi = {
  getOverview: async (): Promise<AnalyticsOverview> => {
    if (USE_MOCK) {
      await delay();
      return mockAnalyticsOverview;
    }
    const res = await axiosClient.get<ApiResponse<AnalyticsOverview>>('/admin/analytics/overview');
    return res.data.data;
  },

  getPopularArtifacts: async (limit = 5): Promise<PopularArtifact[]> => {
    if (USE_MOCK) {
      await delay();
      return mockPopularArtifacts.slice(0, limit);
    }
    const res = await axiosClient.get<ApiResponse<PopularArtifact[]>>(
      '/admin/analytics/popular-artifacts',
      { params: { limit } },
    );
    return res.data.data;
  },

  getLanguageStats: async (): Promise<LanguageStat[]> => {
    if (USE_MOCK) {
      await delay(200);
      return mockLanguageStats;
    }
    const res = await axiosClient.get<ApiResponse<LanguageStat[]>>('/admin/analytics/language-stats');
    return res.data.data;
  },

  getInteractionTimeline: async (): Promise<InteractionTimelinePoint[]> => {
    if (USE_MOCK) {
      await delay();
      return mockInteractionTimeline;
    }
    const res = await axiosClient.get<ApiResponse<InteractionTimelinePoint[]>>(
      '/admin/analytics/interaction-timeline',
    );
    return res.data.data;
  },

  getFeedbacks: async (
    params?: PageRequest & { status?: string },
  ): Promise<{ data: Feedback[]; pagination: PaginationMeta }> => {
    if (USE_MOCK) {
      await delay();
      let items = [..._mockFeedbacks];
      if (params?.status && params.status !== 'ALL') {
        items = items.filter((f) => f.status === params.status);
      }
      const page = params?.page ?? 0;
      const size = params?.size ?? 10;
      const sliced = items.slice(page * size, page * size + size);
      return {
        data: sliced,
        pagination: {
          page,
          size,
          totalElements: items.length,
          totalPages: Math.ceil(items.length / size),
        },
      };
    }
    const res = await axiosClient.get<ApiResponse<Feedback[]>>('/admin/feedback', { params });
    return { data: res.data.data, pagination: res.data.pagination! };
  },

  updateFeedbackStatus: async (
    id: string,
    body: UpdateFeedbackStatusRequest,
  ): Promise<Feedback> => {
    if (USE_MOCK) {
      await delay();
      const idx = _mockFeedbacks.findIndex((f) => f.id === id);
      if (idx === -1) throw new Error('Không tìm thấy feedback');
      _mockFeedbacks[idx] = { ..._mockFeedbacks[idx], status: body.status };
      return _mockFeedbacks[idx];
    }
    const res = await axiosClient.put<ApiResponse<Feedback>>(`/admin/feedback/${id}`, body);
    return res.data.data;
  },

  exportExcel: async (fromDate: string, toDate: string): Promise<Blob> => {
    if (USE_MOCK) {
      await delay(1000);
      const { utils, write } = await import('xlsx');
      const wsData = [
        ['Báo cáo Smart Heritage'],
        ['Từ ngày', fromDate, 'Đến ngày', toDate],
        [],
        ['Hiện vật', 'Lượt tương tác', 'TG xem TB (giây)', 'Di tích'],
        ...mockPopularArtifacts.map((a) => [
          a.artifactName,
          a.interactionCount,
          a.avgDurationSeconds,
          a.siteName,
        ]),
        [],
        ['Ngôn ngữ', 'Lượt dùng', 'Tỷ lệ (%)'],
        ...mockLanguageStats.map((l) => [l.languageName, l.count, l.percentage]),
      ];
      const ws = utils.aoa_to_sheet(wsData);
      const wb = utils.book_new();
      utils.book_append_sheet(wb, ws, 'Thống kê');
      const buf = write(wb, { bookType: 'xlsx', type: 'array' });
      return new Blob([buf], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      });
    }
    const res = await axiosClient.get('/admin/reports/export/excel', {
      params: { fromDate, toDate },
      responseType: 'blob',
    });
    return res.data as Blob;
  },
};

export default analyticsApi;
