import React, { useEffect, useState, useCallback } from 'react';
import {
  Card,
  Typography,
  Button,
  DatePicker,
  Row,
  Col,
  Table,
  Tag,
  Spin,
  message,
  Statistic,
  Avatar,
} from 'antd';
import { FileExcelOutlined, FileTextOutlined, ReloadOutlined } from '@ant-design/icons';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts';
import dayjs from 'dayjs';
import analyticsApi from '../../api/analyticsApi';
import type { PopularArtifact, InteractionTimelinePoint } from '../../api/types/analytics.types';
import { formatDuration, downloadBlob } from '../../utils/helpers';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;

const ReportsPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [exporting, setExporting] = useState(false);
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs]>([
    dayjs().subtract(7, 'day'),
    dayjs(),
  ]);
  const [popular, setPopular] = useState<PopularArtifact[]>([]);
  const [timeline, setTimeline] = useState<InteractionTimelinePoint[]>([]);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const [pop, tl] = await Promise.all([
        analyticsApi.getPopularArtifacts(10),
        analyticsApi.getInteractionTimeline(),
      ]);
      setPopular(pop);
      setTimeline(tl);
    } catch (err) {
      console.error('Lỗi load report:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleExportExcel = async () => {
    setExporting(true);
    try {
      const blob = await analyticsApi.exportCsv(
        dateRange[0].format('YYYY-MM-DD'),
        dateRange[1].format('YYYY-MM-DD'),
      );
      downloadBlob(blob, `SmartHeritage_${dayjs().format('YYYYMMDD')}.csv`);
      message.success('Tải CSV thành công!');
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Tải CSV thất bại');
    } finally {
      setExporting(false);
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 20 }}>
        <div>
          <Title level={3} style={{ margin: 0, color: '#2C1810' }}>
            <FileTextOutlined /> Báo cáo thống kê
          </Title>
          <Text style={{ color: '#6b7280' }}>Xem và xuất báo cáo hoạt động hệ thống</Text>
        </div>
        <Button icon={<ReloadOutlined />} onClick={loadData} style={{ borderRadius: 8 }}>
          Làm mới
        </Button>
      </div>

      {/* CSV có thể mở bằng Excel */}
      <Card style={{ borderRadius: 12, marginBottom: 20 }}>
        <Row gutter={[16, 12]} align="middle">
          <Col xs={24} md={12}>
            <Text strong style={{ display: 'block', marginBottom: 6 }}>Khoảng thời gian:</Text>
            <RangePicker
              value={dateRange}
              onChange={(dates) => {
                if (dates) setDateRange(dates as [dayjs.Dayjs, dayjs.Dayjs]);
              }}
              format="DD/MM/YYYY"
              style={{ width: '100%' }}
            />
          </Col>
          <Col xs={24} md={12}>
            <Button
              icon={<FileExcelOutlined />}
              onClick={handleExportExcel}
              loading={exporting}
              size="large"
              style={{
                background: '#217346',
                color: '#fff',
                border: 'none',
                borderRadius: 8,
                fontWeight: 600,
              }}
            >
              Tải CSV (Excel)
            </Button>
          </Col>
        </Row>
      </Card>

      {loading ? (
        <div style={{ textAlign: 'center', padding: 80 }}>
          <Spin size="large" />
        </div>
      ) : (
        <Row gutter={[16, 16]}>
          <Col xs={24}>
            <Card title="Lượt tương tác theo ngày" style={{ borderRadius: 12 }}>
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={timeline}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                  <XAxis dataKey="date" tick={{ fontSize: 11 }} />
                  <YAxis tick={{ fontSize: 11 }} />
                  <Tooltip contentStyle={{ borderRadius: 8 }} />
                  <Bar dataKey="interactions" fill="#8B4513" radius={[4, 4, 0, 0]} name="Tương tác" />
                  <Bar dataKey="visitors" fill="#C9A84C" radius={[4, 4, 0, 0]} name="Khách" />
                </BarChart>
              </ResponsiveContainer>
            </Card>
          </Col>

          <Col xs={24}>
            <Card title="Thống kê hiện vật theo lượt tương tác" style={{ borderRadius: 12 }}>
              <Table
                dataSource={popular}
                rowKey="artifactId"
                pagination={false}
                columns={[
                  {
                    title: '#',
                    key: 'rank',
                    width: 50,
                    render: (_, __, i) => (
                      <Text strong style={{ color: i < 3 ? '#8B4513' : '#6b7280' }}>
                        {i + 1}
                      </Text>
                    ),
                  },
                  {
                    title: 'Hiện vật',
                    key: 'name',
                    render: (_, r) => (
                      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                        <Avatar src={r.thumbnailUrl} shape="square" size={36} style={{ borderRadius: 4 }} />
                        <div>
                          <div style={{ fontWeight: 600, fontSize: 13 }}>{r.artifactName}</div>
                          <Tag color="orange" style={{ fontSize: 11 }}>{r.siteName}</Tag>
                        </div>
                      </div>
                    ),
                  },
                  {
                    title: 'Lượt tương tác',
                    dataIndex: 'interactionCount',
                    align: 'center' as const,
                    render: (n) => (
                      <Statistic
                        value={n}
                        valueStyle={{ fontSize: 18, color: '#8B4513', fontWeight: 700 }}
                      />
                    ),
                    sorter: (a, b) => a.interactionCount - b.interactionCount,
                    defaultSortOrder: 'descend' as const,
                  },
                  {
                    title: 'TG xem TB',
                    dataIndex: 'avgDurationSeconds',
                    align: 'center' as const,
                    render: (s) => <Text>{formatDuration(s)}</Text>,
                  },
                ]}
              />
            </Card>
          </Col>
        </Row>
      )}
    </div>
  );
};

export default ReportsPage;
