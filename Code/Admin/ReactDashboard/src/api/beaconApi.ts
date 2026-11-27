// quản lý beacon - CRUD, gán/hủy gán hiện vật
import axiosClient, { USE_MOCK, delay } from './axiosClient';
import type { ApiResponse, PageRequest, PaginationMeta } from './types/common.types';
import type { Beacon, BeaconListItem, CreateBeaconRequest, UpdateBeaconRequest, BeaconAssignRequest } from './types/beacon.types';
import { mockBeacons } from './mocks/mockBeacons';

let _mockBeacons = [...mockBeacons];

const toListItem = (b: Beacon): BeaconListItem => ({
  id: b.id, uuid: b.uuid, major: b.major, minor: b.minor, name: b.name,
  status: b.status, batteryLevel: b.batteryLevel, siteName: b.siteName,
  assignedArtifactName: b.assignedArtifactName, lastSeenAt: b.lastSeenAt,
});

const beaconApi = {
  getList: async (params?: PageRequest & { siteId?: string; status?: string }): Promise<{ data: BeaconListItem[]; pagination: PaginationMeta }> => {
    if (USE_MOCK) {
      await delay();
      let items = _mockBeacons.map(toListItem);
      if (params?.siteId) items = items.filter(b => _mockBeacons.find(x => x.id === b.id)?.siteId === params.siteId);
      if (params?.status) items = items.filter(b => b.status === params.status);
      if (params?.search) {
        const q = params.search.toLowerCase();
        items = items.filter(b => b.name.toLowerCase().includes(q) || b.uuid.toLowerCase().includes(q));
      }
      const page = params?.page ?? 0;
      const size = params?.size ?? 10;
      const sliced = items.slice(page * size, page * size + size);
      return { data: sliced, pagination: { page, size, totalElements: items.length, totalPages: Math.ceil(items.length / size) } };
    }
    const res = await axiosClient.get<ApiResponse<BeaconListItem[]>>('/admin/beacons', { params });
    return { data: res.data.data, pagination: res.data.pagination! };
  },

  getById: async (id: string): Promise<Beacon> => {
    if (USE_MOCK) {
      await delay();
      const b = _mockBeacons.find(x => x.id === id);
      if (!b) throw new Error('Không tìm thấy beacon');
      return b;
    }
    const res = await axiosClient.get<ApiResponse<Beacon>>(`/admin/beacons/${id}`);
    return res.data.data;
  },

  getBySite: async (siteId: string): Promise<Beacon[]> => {
    if (USE_MOCK) {
      await delay(200);
      return _mockBeacons.filter(b => b.siteId === siteId);
    }
    const res = await axiosClient.get<ApiResponse<Beacon[]>>('/admin/beacons', { params: { siteId, size: 100 } });
    return res.data.data;
  },

  create: async (body: CreateBeaconRequest): Promise<Beacon> => {
    if (USE_MOCK) {
      await delay();
      const nb: Beacon = {
        ...body, id: 'bcn-' + Date.now(), status: 'UNASSIGNED', batteryLevel: 100,
        assignedArtifactId: undefined, assignedArtifactName: undefined,
        positionX: undefined, positionY: undefined, lastSeenAt: undefined,
        createdAt: new Date().toISOString(), updatedAt: new Date().toISOString(),
      };
      _mockBeacons.push(nb);
      return nb;
    }
    const res = await axiosClient.post<ApiResponse<Beacon>>('/admin/beacons', body);
    return res.data.data;
  },

  update: async (id: string, body: UpdateBeaconRequest): Promise<Beacon> => {
    if (USE_MOCK) {
      await delay();
      const idx = _mockBeacons.findIndex(b => b.id === id);
      if (idx === -1) throw new Error('Không tìm thấy beacon');
      _mockBeacons[idx] = { ..._mockBeacons[idx], ...body, updatedAt: new Date().toISOString() };
      return _mockBeacons[idx];
    }
    const res = await axiosClient.put<ApiResponse<Beacon>>(`/admin/beacons/${id}`, body);
    return res.data.data;
  },

  remove: async (id: string): Promise<void> => {
    if (USE_MOCK) {
      await delay();
      _mockBeacons = _mockBeacons.filter(b => b.id !== id);
      return;
    }
    await axiosClient.delete(`/admin/beacons/${id}`);
  },

  assign: async (id: string, body: BeaconAssignRequest): Promise<Beacon> => {
    if (USE_MOCK) {
      await delay();
      const idx = _mockBeacons.findIndex(b => b.id === id);
      if (idx === -1) throw new Error('Không tìm thấy beacon');
      _mockBeacons[idx] = {
        ..._mockBeacons[idx],
        assignedArtifactId: body.artifactId,
        assignedArtifactName: 'Hiện vật #' + body.artifactId,
        positionX: body.positionX, positionY: body.positionY,
        status: 'ACTIVE', updatedAt: new Date().toISOString(),
      };
      return _mockBeacons[idx];
    }
    const res = await axiosClient.post<ApiResponse<Beacon>>(`/admin/beacons/${id}/assign`, body);
    return res.data.data;
  },

  unassign: async (id: string): Promise<Beacon> => {
    if (USE_MOCK) {
      await delay();
      const idx = _mockBeacons.findIndex(b => b.id === id);
      if (idx === -1) throw new Error('Không tìm thấy beacon');
      _mockBeacons[idx] = {
        ..._mockBeacons[idx], assignedArtifactId: undefined, assignedArtifactName: undefined,
        positionX: undefined, positionY: undefined, status: 'UNASSIGNED', updatedAt: new Date().toISOString(),
      };
      return _mockBeacons[idx];
    }
    const res = await axiosClient.post<ApiResponse<Beacon>>(`/admin/beacons/${id}/unassign`);
    return res.data.data;
  },
};

export default beaconApi;
