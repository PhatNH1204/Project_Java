// types cho module analytics & feedback (TV5)

export interface AnalyticsOverview {
  totalVisitorsToday: number;
  totalVisitorsThisMonth: number;
  totalInteractions: number;
  avgVisitDurationMinutes: number;
  activeBeacons: number;
  totalArtifacts: number;
}

export interface PopularArtifact {
  artifactId: string;
  artifactName: string;
  siteName: string;
  thumbnailUrl: string;
  interactionCount: number;
  avgDurationSeconds: number;
}

export interface LanguageStat {
  languageCode: string;
  languageName: string;
  count: number;
  percentage: number;
}

export interface InteractionTimelinePoint {
  date: string;
  interactions: number;
  visitors: number;
}

export interface Feedback {
  id: string;
  visitorId: string;
  visitorName: string;
  artifactId?: string;
  artifactName?: string;
  siteId?: string;
  siteName?: string;
  rating: number;
  comment?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
}

export interface UpdateFeedbackStatusRequest {
  status: 'APPROVED' | 'REJECTED';
  adminNote?: string;
}
