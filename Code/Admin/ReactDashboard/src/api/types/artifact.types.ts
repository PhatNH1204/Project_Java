// artifact types

import type { MediaType } from './common.types';

export interface ArtifactTranslation {
  languageCode: 'vi' | 'en';
  name: string;
  description: string;
  historicalContext: string;
}

export interface MediaFile {
  id: string;
  url: string;
  publicId: string;        // Cloudinary public_id
  type: MediaType;
  title?: string;
  durationSeconds?: number; // Cho AUDIO/VIDEO
  sortOrder: number;
}

export interface Artifact {
  id: string;
  code: string;
  siteId: string;
  siteName: string;
  thumbnailUrl: string;
  yearCreated?: number;
  material?: string;
  dimensions?: string;
  interactionCount: number;
  beaconAssigned?: string;  // beaconId nếu có gán
  translations: ArtifactTranslation[];
  media: MediaFile[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateArtifactRequest {
  code: string;
  siteId: string;
  thumbnailUrl: string;
  yearCreated?: number;
  material?: string;
  dimensions?: string;
  translations: ArtifactTranslation[];
  media?: Omit<MediaFile, 'id'>[];
}

export type UpdateArtifactRequest = Partial<CreateArtifactRequest>;

export interface ArtifactListItem {
  id: string;
  code: string;
  siteId: string;
  siteName: string;
  thumbnailUrl: string;
  nameVi: string;
  nameEn: string;
  interactionCount: number;
  beaconAssigned?: string;
  createdAt: string;
}

export interface MediaUploadResponse {
  id: string;
  url: string;
  publicId: string;
  type: MediaType;
  bytes: number;
  format: string;
}
