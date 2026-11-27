// quản lý hiện vật - CRUD và upload media
import axiosClient, { USE_MOCK, delay } from './axiosClient';
import type { ApiResponse, PageRequest, PaginationMeta } from './types/common.types';
import type {
  Artifact, ArtifactListItem, CreateArtifactRequest,
  UpdateArtifactRequest, MediaUploadResponse,
} from './types/artifact.types';
import { mockArtifacts, mockArtifactListItems } from './mocks/mockArtifacts';

let _mockArtifacts = [...mockArtifacts];
let _mockList = [...mockArtifactListItems];

const refresh = () => {
  _mockList = _mockArtifacts.map(a => ({
    id: a.id, code: a.code, siteId: a.siteId, siteName: a.siteName,
    thumbnailUrl: a.thumbnailUrl, interactionCount: a.interactionCount,
    beaconAssigned: a.beaconAssigned,
    nameVi: a.translations.find(t => t.languageCode === 'vi')?.name ?? '',
    nameEn: a.translations.find(t => t.languageCode === 'en')?.name ?? '',
    createdAt: a.createdAt,
  }));
};

const artifactApi = {
  getList: async (params?: PageRequest & { siteId?: string }): Promise<{ data: ArtifactListItem[]; pagination: PaginationMeta }> => {
    if (USE_MOCK) {
      await delay();
      let items = [..._mockList];
      if (params?.siteId) items = items.filter(a => a.siteId === params.siteId);
      if (params?.search) {
        const q = params.search.toLowerCase();
        items = items.filter(a => a.nameVi.toLowerCase().includes(q) || a.nameEn.toLowerCase().includes(q) || a.code.toLowerCase().includes(q));
      }
      const page = params?.page ?? 0;
      const size = params?.size ?? 10;
      const sliced = items.slice(page * size, page * size + size);
      return { data: sliced, pagination: { page, size, totalElements: items.length, totalPages: Math.ceil(items.length / size) } };
    }
    const res = await axiosClient.get<ApiResponse<ArtifactListItem[]>>('/admin/artifacts', { params });
    return { data: res.data.data, pagination: res.data.pagination! };
  },

  getById: async (id: string): Promise<Artifact> => {
    if (USE_MOCK) {
      await delay();
      const a = _mockArtifacts.find(x => x.id === id);
      if (!a) throw new Error('Không tìm thấy hiện vật');
      return a;
    }
    const res = await axiosClient.get<ApiResponse<Artifact>>(`/admin/artifacts/${id}`);
    return res.data.data;
  },

  create: async (body: CreateArtifactRequest): Promise<Artifact> => {
    if (USE_MOCK) {
      await delay();
      const newArtifact: Artifact = {
        ...body,
        id: 'art-' + Date.now(),
        siteName: body.siteId,
        interactionCount: 0,
        media: body.media?.map(m => ({ ...m, id: 'med-' + Date.now() + Math.random() })) ?? [],
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };
      _mockArtifacts.push(newArtifact);
      refresh();
      return newArtifact;
    }
    const res = await axiosClient.post<ApiResponse<Artifact>>('/admin/artifacts', body);
    return res.data.data;
  },

  update: async (id: string, body: UpdateArtifactRequest): Promise<Artifact> => {
    if (USE_MOCK) {
      await delay();
      const idx = _mockArtifacts.findIndex(a => a.id === id);
      if (idx === -1) throw new Error('Không tìm thấy hiện vật');
      _mockArtifacts[idx] = {
        ..._mockArtifacts[idx],
        ...body,
        media: body.media
          ? body.media.map(m => ({ ...m, id: 'med-' + Date.now() + Math.random() }))
          : _mockArtifacts[idx].media,
        updatedAt: new Date().toISOString(),
      };
      refresh();
      return _mockArtifacts[idx];
    }
    const res = await axiosClient.put<ApiResponse<Artifact>>(`/admin/artifacts/${id}`, body);
    return res.data.data;
  },

  remove: async (id: string): Promise<void> => {
    if (USE_MOCK) {
      await delay();
      _mockArtifacts = _mockArtifacts.filter(a => a.id !== id);
      refresh();
      return;
    }
    await axiosClient.delete(`/admin/artifacts/${id}`);
  },

  uploadMedia: async (file: File, type: 'IMAGE' | 'AUDIO' | 'VIDEO'): Promise<MediaUploadResponse> => {
    if (USE_MOCK) {
      await delay(800);
      // Mock tạo URL giả lập
      const fakeUrl = type === 'IMAGE'
        ? `https://images.unsplash.com/photo-1584464491033-06628f3a6b7b?w=800&mock=${Date.now()}`
        : type === 'AUDIO'
          ? `https://www.soundhelix.com/examples/mp3/SoundHelix-Song-${Math.ceil(Math.random() * 3)}.mp3`
          : `https://www.w3schools.com/html/mov_bbb.mp4`;
      return {
        id: 'med-mock-' + Date.now(),
        url: fakeUrl,
        publicId: `heritage/mock_${Date.now()}`,
        type,
        bytes: file.size,
        format: file.name.split('.').pop() ?? 'jpg',
      };
    }
    const form = new FormData();
    form.append('file', file);
    form.append('type', type);
    const res = await axiosClient.post<ApiResponse<MediaUploadResponse>>('/admin/media/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data.data;
  },
};

export default artifactApi;
