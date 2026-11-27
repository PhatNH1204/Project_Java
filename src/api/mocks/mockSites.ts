// ============================================================
// MOCK DATA — Heritage Sites (Di tích lịch sử Việt Nam)
// ============================================================
import type { HeritageSite, SiteListItem } from '../types/site.types';

export const mockSites: HeritageSite[] = [
  {
    id: 'site-001',
    code: 'SITE_DAI_NOI',
    status: 'ACTIVE',
    latitude: 16.4698,
    longitude: 107.5796,
    thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/Ngo_Mon_Hue.jpg/1200px-Ngo_Mon_Hue.jpg',
    floorPlanUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Hue_citadel_map.svg/800px-Hue_citadel_map.svg.png',
    openingHours: '07:00 – 17:30',
    artifactCount: 48,
    beaconCount: 12,
    translations: [
      {
        languageCode: 'vi',
        name: 'Đại Nội Huế',
        description: 'Hoàng thành Huế – trung tâm quyền lực của triều đại nhà Nguyễn từ năm 1805 đến 1945.',
        address: '23 Tháng 8, Phú Hậu, Thành phố Huế, Thừa Thiên Huế',
      },
      {
        languageCode: 'en',
        name: 'Hue Imperial City (Dai Noi)',
        description: 'The Imperial Citadel of Hue – the seat of power of the Nguyen dynasty from 1805 to 1945.',
        address: '23 August St, Phu Hau, Hue City, Thua Thien Hue',
      },
    ],
    createdAt: '2026-01-10T07:00:00Z',
    updatedAt: '2026-11-01T10:00:00Z',
  },
  {
    id: 'site-002',
    code: 'SITE_LANG_KHAI_DINH',
    status: 'ACTIVE',
    latitude: 16.3966,
    longitude: 107.5951,
    thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Lang_Khai_Dinh.jpg/1200px-Lang_Khai_Dinh.jpg',
    floorPlanUrl: undefined,
    openingHours: '07:00 – 17:00',
    artifactCount: 22,
    beaconCount: 6,
    translations: [
      {
        languageCode: 'vi',
        name: 'Lăng Khải Định',
        description: 'Lăng mộ của vua Khải Định – công trình kết hợp độc đáo giữa kiến trúc Đông–Tây, xây dựng từ năm 1920 đến 1931.',
        address: 'Châu Chữ, Thuỷ Bằng, Thành phố Huế',
      },
      {
        languageCode: 'en',
        name: 'Khai Dinh Mausoleum',
        description: 'The mausoleum of Emperor Khai Dinh – a unique blend of Eastern and Western architecture, built from 1920 to 1931.',
        address: 'Chau Chu, Thuy Bang, Hue City',
      },
    ],
    createdAt: '2026-01-15T07:00:00Z',
    updatedAt: '2026-11-01T10:00:00Z',
  },
  {
    id: 'site-003',
    code: 'SITE_LANG_TU_DUC',
    status: 'ACTIVE',
    latitude: 16.4582,
    longitude: 107.5566,
    thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Lang_Tu_Duc_Hue.jpg/1200px-Lang_Tu_Duc_Hue.jpg',
    floorPlanUrl: undefined,
    openingHours: '07:30 – 17:30',
    artifactCount: 30,
    beaconCount: 8,
    translations: [
      {
        languageCode: 'vi',
        name: 'Lăng Tự Đức',
        description: 'Quần thể lăng tẩm rộng lớn và thơ mộng nhất của triều Nguyễn, do vua Tự Đức cho xây dựng để làm nơi nghỉ ngơi và sau đó là lăng mộ.',
        address: 'Dương Xuân Thượng, Thuỷ Xuân, Thành phố Huế',
      },
      {
        languageCode: 'en',
        name: 'Tu Duc Mausoleum',
        description: 'The largest and most poetic royal mausoleum complex of the Nguyen dynasty, built by Emperor Tu Duc as both a retreat and his final resting place.',
        address: 'Duong Xuan Thuong, Thuy Xuan, Hue City',
      },
    ],
    createdAt: '2026-02-01T07:00:00Z',
    updatedAt: '2026-11-01T10:00:00Z',
  },
];

export const mockSiteListItems: SiteListItem[] = mockSites.map(s => ({
  id: s.id,
  code: s.code,
  status: s.status,
  thumbnailUrl: s.thumbnailUrl,
  artifactCount: s.artifactCount,
  beaconCount: s.beaconCount,
  nameVi: s.translations.find(t => t.languageCode === 'vi')?.name ?? '',
  nameEn: s.translations.find(t => t.languageCode === 'en')?.name ?? '',
  createdAt: s.createdAt,
}));
