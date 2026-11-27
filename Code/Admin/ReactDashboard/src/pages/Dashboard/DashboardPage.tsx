import React, { useCallback, useEffect, useState } from 'react';
import { Row, Col, Card, Typography, Spin, Statistic, Tag, List, Avatar } from 'antd';
import {
  UserOutlined,
  InteractionOutlined,
  ClockCircleOutlined,
  ApiOutlined,
  TrophyOutlined,
} from '@ant-design/icons';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import analyticsApi from '../../api/analyticsApi';
import type { AnalyticsOverview, PopularArtifact, InteractionTimelinePoint } from '../../api/types/analytics.types';
import { formatNumber, formatDuration } from '../../utils/helpers';

const { Title, Text } = Typography;

const DashboardPage: React.FC = () => {
  const [overview, setOverview] = useState<AnalyticsOverview | null>(null);
  const [popular, setPopular] = useState<PopularArtifact[]>([]);
  const [timeline, setTimeline] = useState<InteractionTimelinePoint[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const [ov, pop, tl] = await Promise.all([
        analyticsApi.getOverview(),
        analyticsApi.getPopularArtifacts(5),
        analyticsApi.getInteractionTimeline(),
      ]);
      setOverview(ov);
      setPopular(pop);
      setTimeline(tl);
    } catch (err) {
      console.error('Lỗi load dashboard:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void fetchData();
  }, [fetchData]);

  if (loading || !overview) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 400 }}>
        <Spin size="large" tip="Đang tải dữ liệu..." />
      </div>
    );
  }

  const statCards = [
    {
      title: 'Lượt khách hôm nay',
      value: overview.totalVisitorsToday,
      icon: <UserOutlined />,
      cls: 'stat-card-primary',
      suffix: 'lượt',
    },
    {
      title: 'Tổng tương tác',
      value: overview.totalInteractions,
      icon: <InteractionOutlined />,
      cls: 'stat-card-blue',
      suffix: 'lần',
    },
    {
      title: 'Thời gian TB',
      value: overview.avgVisitDurationMinutes,
      icon: <ClockCircleOutlined />,
      cls: 'stat-card-green',
      suffix: 'phút',
    },
    {
      title: 'Beacon hoạt động',
      value: overview.activeBeacons,
      icon: <ApiOutlined />,
      cls: 'stat-card-purple',
      suffix: 'thiết bị',
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <Title level={3} style={{ margin: 0, color: '#2C1810' }}>🏛️ Dashboard</Title>
        <Text style={{ color: '#6b7280' }}>Thống kê hoạt động Smart Heritage</Text>
      </div>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        {statCards.map((card) => (
          <Col xs={24} sm={12} lg={6} key={card.title}>
            <Card
              className={card.cls}
              style={{ borderRadius: 12, border: 'none' }}
              styles={{ body: { padding: '20px 24px' } }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: 13 }}>{card.title}</Text>
                  <div style={{ color: '#fff', fontSize: 30, fontWeight: 700, marginTop: 4 }}>
                    {formatNumber(card.value)}
                  </div>
                  <Text style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12 }}>{card.suffix}</Text>
                </div>
                <div style={{ fontSize: 26, color: 'rgba(255,255,255,0.5)' }}>
                  {card.icon}
                </div>
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24}>
          <Card title="Lượt tương tác 7 ngày gần nhất" style={{ borderRadius: 12 }}>
            <ResponsiveContainer width="100%" height={240}>
              <AreaChart data={timeline}>
                <defs>
                  <linearGradient id="colorInteract" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#8B4513" stopOpacity={0.3} />
                    <stop offset="95%" stopColor="#8B4513" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <XAxis dataKey="date" tick={{ fontSize: 11 }} />
                <YAxis tick={{ fontSize: 11 }} />
                <Tooltip contentStyle={{ borderRadius: 8 }} />
                <Area
                  type="monotone"
                  dataKey="interactions"
                  stroke="#8B4513"
                  fill="url(#colorInteract)"
                  strokeWidth={2}
                  name="Tương tác"
                />
              </AreaChart>
            </ResponsiveContainer>
          </Card>
        </Col>
      </Row>

      <Card
        title={
          <>
            <TrophyOutlined style={{ color: '#C9A84C', marginRight: 6 }} />
            Top 5 hiện vật được tương tác nhiều nhất
          </>
        }
        style={{ borderRadius: 12 }}
      >
        <List
          dataSource={popular}
          renderItem={(item, index) => (
            <List.Item
              style={{
                padding: '12px 0',
                borderBottom: index < popular.length - 1 ? '1px solid #f3f4f6' : 'none',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: 14, width: '100%' }}>
                <div
                  style={{
                    width: 30,
                    height: 30,
                    borderRadius: '50%',
                    flexShrink: 0,
                    background:
                      index === 0 ? '#C9A84C'
                      : index === 1 ? '#d1d5db'
                      : index === 2 ? '#cd7f32'
                      : '#f3f4f6',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: 700,
                    fontSize: 13,
                    color: index < 3 ? '#fff' : '#6b7280',
                  }}
                >
                  {index + 1}
                </div>
                <Avatar
                  src={item.thumbnailUrl}
                  size={44}
                  shape="square"
                  style={{ borderRadius: 6, flexShrink: 0 }}
                />
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div
                    style={{
                      fontWeight: 600,
                      fontSize: 14,
                      color: '#2C1810',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap',
                    }}
                  >
                    {item.artifactName}
                  </div>
                  <Tag color="orange" style={{ marginTop: 2, fontSize: 11 }}>
                    {item.siteName}
                  </Tag>
                </div>
                <div style={{ textAlign: 'right', flexShrink: 0 }}>
                  <Statistic
                    value={item.interactionCount}
                    valueStyle={{ fontSize: 18, color: '#8B4513', fontWeight: 700 }}
                    suffix={<span style={{ fontSize: 12 }}>lượt</span>}
                  />
                  <Text style={{ fontSize: 12, color: '#9ca3af' }}>
                    TB {formatDuration(item.avgDurationSeconds)}
                  </Text>
                </div>
              </div>
            </List.Item>
          )}
        />
      </Card>
    </div>
  );
};

export default DashboardPage;
