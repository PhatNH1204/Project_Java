// heritage site types

import type { SiteStatus } from './common.types';

export interface SiteTranslation {
  languageCode: 'vi' | 'en';
  name: string;
  description: string;
  address: string;
}

export interface HeritageSite {
  id: string;
  code: string;
  status: SiteStatus;
  latitude: number;
  longitude: number;
  thumbnailUrl: string;
  floorPlanUrl?: string;        // Ảnh sơ đồ mặt bằng (dùng cho bản đồ beacon)
  openingHours?: string;
  artifactCount: number;
  beaconCount: number;
  translations: SiteTranslation[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateSiteRequest {
  code: string;
  status: SiteStatus;
  latitude: number;
  longitude: number;
  thumbnailUrl: string;
  floorPlanUrl?: string;
  openingHours?: string;
  translations: SiteTranslation[];
}

export type UpdateSiteRequest = Partial<CreateSiteRequest>;

export interface SiteListItem {
  id: string;
  code: string;
  status: SiteStatus;
  thumbnailUrl: string;
  artifactCount: number;
  beaconCount: number;
  nameVi: string;
  nameEn: string;
  createdAt: string;
}
