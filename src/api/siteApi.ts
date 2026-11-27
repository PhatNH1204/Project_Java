// quản lý di tích lịch sử - CRUD
import axiosClient, { USE_MOCK, delay } from './axiosClient';
import type { ApiResponse, PaginationMeta, PageRequest } from './types/common.types';
import type { HeritageSite, SiteListItem, CreateSiteRequest, UpdateSiteRequest } from './types/site.types';
import { mockSites, mockSiteListItems } from './mocks/mockSites';

let _mockSites = [...mockSites];
let _mockList = [...mockSiteListItems];

const refresh = () => {
  _mockList = _mockSites.map(s => ({
    id: s.id, code: s.code, status: s.status, thumbnailUrl: s.thumbnailUrl,
    artifactCount: s.artifactCount, beaconCount: s.beaconCount,
    nameVi: s.translations.find(t => t.languageCode === 'vi')?.name ?? '',
    nameEn: s.translations.find(t => t.languageCode === 'en')?.name ?? '',
    createdAt: s.createdAt,
  }));
};

const siteApi = {
  getList: async (params?: PageRequest): Promise<{ data: SiteListItem[]; pagination: PaginationMeta }> => {
    if (USE_MOCK) {
      await delay();
      let items = [..._mockList];
      if (params?.search) {
        const q = params.search.toLowerCase();
        items = items.filter(s => s.nameVi.toLowerCase().includes(q) || s.nameEn.toLowerCase().includes(q) || s.code.toLowerCase().includes(q));
      }
      const page = params?.page ?? 0;
      const size = params?.size ?? 10;
      const total = items.length;
      const sliced = items.slice(page * size, page * size + size);
      return { data: sliced, pagination: { page, size, totalElements: total, totalPages: Math.ceil(total / size) } };
    }
    const res = await axiosClient.get<ApiResponse<SiteListItem[]>>('/admin/sites', { params });
    return { data: res.data.data, pagination: res.data.pagination! };
  },

  getById: async (id: string): Promise<HeritageSite> => {
    if (USE_MOCK) {
      await delay();
      const site = _mockSites.find(s => s.id === id);
      if (!site) throw new Error('Không tìm thấy di tích');
      return site;
    }
    const res = await axiosClient.get<ApiResponse<HeritageSite>>(`/admin/sites/${id}`);
    return res.data.data;
  },

  create: async (body: CreateSiteRequest): Promise<HeritageSite> => {
    if (USE_MOCK) {
      await delay();
      const newSite: HeritageSite = {
        ...body, id: 'site-' + Date.now(), artifactCount: 0, beaconCount: 0,
        createdAt: new Date().toISOString(), updatedAt: new Date().toISOString(),
      };
      _mockSites.push(newSite);
      refresh();
      return newSite;
    }
    const res = await axiosClient.post<ApiResponse<HeritageSite>>('/admin/sites', body);
    return res.data.data;
  },

  update: async (id: string, body: UpdateSiteRequest): Promise<HeritageSite> => {
    if (USE_MOCK) {
      await delay();
      const idx = _mockSites.findIndex(s => s.id === id);
      if (idx === -1) throw new Error('Không tìm thấy di tích');
      _mockSites[idx] = { ..._mockSites[idx], ...body, updatedAt: new Date().toISOString() };
      refresh();
      return _mockSites[idx];
    }
    const res = await axiosClient.put<ApiResponse<HeritageSite>>(`/admin/sites/${id}`, body);
    return res.data.data;
  },

  remove: async (id: string): Promise<void> => {
    if (USE_MOCK) {
      await delay();
      _mockSites = _mockSites.filter(s => s.id !== id);
      refresh();
      return;
    }
    await axiosClient.delete(`/admin/sites/${id}`);
  },
};

export default siteApi;
