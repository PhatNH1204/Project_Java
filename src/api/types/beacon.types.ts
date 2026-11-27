// beacon types

import type { BeaconStatus } from './common.types';

export interface Beacon {
  id: string;
  uuid: string;
  major: number;
  minor: number;
  macAddress?: string;
  name: string;
  status: BeaconStatus;
  batteryLevel?: number;      // 0–100 %
  txPower?: number;           // dBm
  siteId?: string;
  siteName?: string;
  assignedArtifactId?: string;
  assignedArtifactName?: string;
  positionX?: number;         // Tọa độ % trên floor plan
  positionY?: number;
  lastSeenAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBeaconRequest {
  uuid: string;
  major: number;
  minor: number;
  macAddress?: string;
  name: string;
  txPower?: number;
  siteId?: string;
}

export type UpdateBeaconRequest = Partial<CreateBeaconRequest & { status: BeaconStatus; batteryLevel: number }>;

export interface BeaconAssignRequest {
  artifactId: string;
  positionX?: number;
  positionY?: number;
}

export interface BeaconListItem {
  id: string;
  uuid: string;
  major: number;
  minor: number;
  name: string;
  status: BeaconStatus;
  batteryLevel?: number;
  siteName?: string;
  assignedArtifactName?: string;
  lastSeenAt?: string;
}

// Phản hồi từ Backend khi Mobile resolve beacon (Online mode)
export interface BeaconResolveRequest {
  uuid: string;
  major: number;
  minor: number;
  rssi: number;
  visitorId: string;
}

export interface BeaconResolveResponse {
  isNewArtifact: boolean;
  shouldNotify: boolean;      // Backend quyết định chống spam
  cooldownRemainingSeconds?: number;
  artifact?: {
    id: string;
    name: string;
    description: string;
    historicalContext: string;
    thumbnailUrl: string;
    audioUrl?: string;
    videoUrl?: string;
    interactionSessionId: string; // Dùng để ghi lại duration sau
  };
}
