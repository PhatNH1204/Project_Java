import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Button,
  Select,
  Typography,
  Tag,
  Rate,
  Card,
  Space,
  Tooltip,
  message,
  Avatar,
  Popconfirm,
} from 'antd';
import { StarOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import analyticsApi from '../../api/analyticsApi';
import type { Feedback } from '../../api/types/analytics.types';
import { formatDateTime, feedbackStatusText } from '../../utils/helpers';

const { Title, Text } = Typography;

const STATUS_COLOR: Record<string, string> = {
  PENDING: 'gold',
  APPROVED: 'green',
  REJECTED: 'red',
};

const FeedbackPage: React.FC = () => {
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const [loading, setLoading] = useState(false);
  const [filterStatus, setFilterStatus] = useState<string>('ALL');
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);

  const loadFeedbacks = useCallback(async () => {
    setLoading(true);
    try {
      const res = await analyticsApi.getFeedbacks({ page, size: 10, status: filterStatus });
      setFeedbacks(res.data);
      setTotal(res.pagination.totalElements);
    } catch (err) {
      console.error('Lỗi load feedback:', err);
    } finally {
      setLoading(false);
    }
  }, [page, filterStatus]);

  useEffect(() => {
    loadFeedbacks();
  }, [loadFeedbacks]);

  const handleApprove = async (id: string) => {
    try {
      await analyticsApi.updateFeedbackStatus(id, { status: 'APPROVED' });
      message.success('Đã duyệt');
      loadFeedbacks();
    } catch {
      message.error('Có lỗi xảy ra');
    }
  };

  const handleReject = async (id: string) => {
    try {
      await analyticsApi.updateFeedbackStatus(id, { status: 'REJECTED' });
      message.success('Đã từ chối');
      loadFeedbacks();
    } catch {
      message.error('Có lỗi xảy ra');
    }
  };

  const columns: ColumnsType<Feedback> = [
    {
      title: 'Du khách',
      key: 'visitor',
      render: (_, r) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <Avatar style={{ background: '#8B4513' }}>{r.visitorName?.[0] ?? 'V'}</Avatar>
          <div>
            <div style={{ fontWeight: 600, fontSize: 13 }}>{r.visitorName}</div>
            <Text style={{ fontSize: 11, color: '#9ca3af' }}>{formatDateTime(r.createdAt)}</Text>
          </div>
        </div>
      ),
    },
    {
      title: 'Đối tượng',
      key: 'target',
      render: (_, r) => (
        <div>
          {r.artifactName && <Tag color="blue">{r.artifactName}</Tag>}
          {r.siteName && <Tag color="orange">{r.siteName}</Tag>}
        </div>
      ),
    },
    {
      title: 'Đánh giá',
      key: 'rating',
      width: 150,
      render: (_, r) => <Rate disabled defaultValue={r.rating} style={{ fontSize: 14 }} />,
    },
    {
      title: 'Nội dung',
      dataIndex: 'comment',
      ellipsis: true,
      render: (c) => <Text style={{ fontSize: 13 }}>{c ?? '—'}</Text>,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      width: 120,
      render: (s) => <Tag color={STATUS_COLOR[s]}>{feedbackStatusText[s] ?? s}</Tag>,
    },
    {
      title: 'Thao tác',
      key: 'actions',
      width: 110,
      fixed: 'right' as const,
      render: (_, r) =>
        r.status === 'PENDING' ? (
          <Space>
            <Tooltip title="Duyệt">
              <Button
                type="text"
                icon={<CheckOutlined />}
                style={{ color: '#10b981' }}
                onClick={() => handleApprove(r.id)}
              />
            </Tooltip>
            <Tooltip title="Từ chối">
              <Popconfirm
                title="Từ chối đánh giá này?"
                onConfirm={() => handleReject(r.id)}
                okText="Từ chối"
                cancelText="Hủy"
                okButtonProps={{ danger: true }}
              >
                <Button type="text" icon={<CloseOutlined />} danger />
              </Popconfirm>
            </Tooltip>
          </Space>
        ) : (
          <Text style={{ fontSize: 12, color: '#9ca3af' }}>—</Text>
        ),
    },
  ];

  const pendingCount = feedbacks.filter((f) => f.status === 'PENDING').length;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 20 }}>
        <div>
          <Title level={3} style={{ margin: 0, color: '#2C1810' }}>
            <StarOutlined /> Quản lý Đánh giá
          </Title>
          <Text style={{ color: '#6b7280' }}>Duyệt phản hồi từ du khách tham quan</Text>
        </div>
        {pendingCount > 0 && (
          <Tag color="gold" style={{ fontSize: 14, padding: '4px 12px' }}>
            {pendingCount} đánh giá chờ duyệt
          </Tag>
        )}
      </div>

      <Card style={{ borderRadius: 12 }} styles={{ body: { padding: 0 } }}>
        <div style={{ padding: '16px 20px', borderBottom: '1px solid #f0f0f0' }}>
          <Select
            value={filterStatus}
            onChange={(v) => {
              setFilterStatus(v);
              setPage(0);
            }}
            style={{ width: 180 }}
            options={[
              { value: 'ALL', label: 'Tất cả' },
              { value: 'PENDING', label: 'Chờ duyệt' },
              { value: 'APPROVED', label: 'Đã duyệt' },
              { value: 'REJECTED', label: 'Từ chối' },
            ]}
          />
        </div>
        <Table
          columns={columns}
          dataSource={feedbacks}
          rowKey="id"
          loading={loading}
          scroll={{ x: 800 }}
          pagination={{
            current: page + 1,
            total,
            pageSize: 10,
            onChange: (p) => setPage(p - 1),
            showTotal: (t) => `Tổng ${t} đánh giá`,
          }}
        />
      </Card>
    </div>
  );
};

export default FeedbackPage;
