// ============================================================
// HERITAGE SITES PAGE — Danh sách và CRUD Di tích lịch sử
// ============================================================
import React, { useEffect, useState, useCallback } from 'react';
import {
  Table, Button, Input, Space, Tag, Tooltip, Image, Typography,
  Modal, Form, Select, Tabs, InputNumber, message, Popconfirm, Card, Row, Col,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, EnvironmentOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import siteApi from '../../api/siteApi';
import type { SiteListItem, HeritageSite, CreateSiteRequest } from '../../api/types/site.types';
import { formatDate, siteStatusText } from '../../utils/helpers';

const { Title, Text } = Typography;

const STATUS_COLOR: Record<string, string> = {
  ACTIVE: 'green', INACTIVE: 'default', MAINTENANCE: 'orange',
};

const SitesPage: React.FC = () => {
  const [sites, setSites] = useState<SiteListItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [modalOpen, setModalOpen] = useState(false);
  const [editTarget, setEditTarget] = useState<HeritageSite | null>(null);
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await siteApi.getList({ page, size: 10, search });
      setSites(res.data);
      setTotal(res.pagination.totalElements);
    } finally { setLoading(false); }
  }, [page, search]);

  useEffect(() => { load(); }, [load]);

  const openCreate = () => {
    setEditTarget(null);
    form.resetFields();
    form.setFieldsValue({
      status: 'ACTIVE',
      translations: [
        { languageCode: 'vi', name: '', description: '', address: '' },
        { languageCode: 'en', name: '', description: '', address: '' },
      ],
    });
    setModalOpen(true);
  };

  const openEdit = async (id: string) => {
    try {
      const site = await siteApi.getById(id);
      setEditTarget(site);
      form.setFieldsValue({
        code: site.code, status: site.status,
        latitude: site.latitude, longitude: site.longitude,
        thumbnailUrl: site.thumbnailUrl, openingHours: site.openingHours,
        translations: site.translations,
      });
      setModalOpen(true);
    } catch { message.error('Không thể tải dữ liệu di tích'); }
  };

  const onDelete = async (id: string) => {
    try {
      await siteApi.remove(id);
      message.success('Đã xóa di tích thành công');
      load();
    } catch { message.error('Xóa thất bại'); }
  };

  const onSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      const body: CreateSiteRequest = {
        code: values.code, status: values.status,
        latitude: values.latitude, longitude: values.longitude,
        thumbnailUrl: values.thumbnailUrl, openingHours: values.openingHours,
        translations: values.translations,
      };
      if (editTarget) {
        await siteApi.update(editTarget.id, body);
        message.success('Cập nhật di tích thành công');
      } else {
        await siteApi.create(body);
        message.success('Tạo di tích thành công');
      }
      setModalOpen(false);
      load();
    } catch (err: unknown) {
      if ((err as { errorFields?: unknown })?.errorFields) return;
      message.error('Lưu thất bại');
    } finally { setSubmitting(false); }
  };

  const columns: ColumnsType<SiteListItem> = [
    {
      title: 'Ảnh', dataIndex: 'thumbnailUrl', width: 80,
      render: (url) => <Image src={url} width={60} height={45} style={{ objectFit: 'cover', borderRadius: 6 }} fallback="data:image/png;base64,iVBORw0KGgo=" />,
    },
    {
      title: 'Tên di tích', key: 'name',
      render: (_, r) => (
        <div>
          <div style={{ fontWeight: 600, color: '#2C1810' }}>{r.nameVi}</div>
          <div style={{ fontSize: 12, color: '#9ca3af' }}>{r.nameEn}</div>
          <Text style={{ fontSize: 11, color: '#6b7280' }} copyable={{ text: r.code }}>#{r.code}</Text>
        </div>
      ),
    },
    {
      title: 'Trạng thái', dataIndex: 'status', width: 130,
      render: (s) => <Tag color={STATUS_COLOR[s]}>{siteStatusText[s] ?? s}</Tag>,
    },
    {
      title: 'Hiện vật', dataIndex: 'artifactCount', width: 100, align: 'center',
      render: (n) => <Tag color="blue">{n}</Tag>,
    },
    {
      title: 'Beacon', dataIndex: 'beaconCount', width: 90, align: 'center',
      render: (n) => <Tag color="purple">{n}</Tag>,
    },
    {
      title: 'Ngày tạo', dataIndex: 'createdAt', width: 110,
      render: (d) => <Text style={{ fontSize: 12 }}>{formatDate(d)}</Text>,
    },
    {
      title: 'Thao tác', key: 'actions', width: 100, fixed: 'right',
      render: (_, r) => (
        <Space>
          <Tooltip title="Chỉnh sửa">
            <Button type="text" icon={<EditOutlined />} onClick={() => openEdit(r.id)} style={{ color: '#8B4513' }} />
          </Tooltip>
          <Popconfirm title="Xác nhận xóa di tích này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy" okButtonProps={{ danger: true }}>
            <Tooltip title="Xóa">
              <Button type="text" danger icon={<DeleteOutlined />} />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 20 }}>
        <div>
          <Title level={3} style={{ margin: 0, color: '#2C1810' }}>
            <EnvironmentOutlined /> Quản lý Di tích Lịch sử
          </Title>
          <Text style={{ color: '#6b7280' }}>Danh sách và quản lý toàn bộ di tích trong hệ thống</Text>
        </div>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={openCreate}
          style={{ borderRadius: 8, background: 'var(--color-primary)' }}>
          Thêm Di tích
        </Button>
      </div>

      {/* Stats Row */}
      <Row gutter={[12, 12]} style={{ marginBottom: 20 }}>
        {[
          { label: 'Tổng di tích', value: total, color: '#8B4513' },
          { label: 'Đang mở cửa', value: sites.filter(s => s.status === 'ACTIVE').length, color: '#10b981' },
          { label: 'Tổng hiện vật', value: sites.reduce((a, s) => a + s.artifactCount, 0), color: '#3b82f6' },
          { label: 'Tổng beacon', value: sites.reduce((a, s) => a + s.beaconCount, 0), color: '#8b5cf6' },
        ].map(s => (
          <Col xs={12} sm={6} key={s.label}>
            <Card style={{ borderRadius: 10, textAlign: 'center', border: `1px solid ${s.color}20` }} styles={{ body: { padding: 16 } }}>
              <div style={{ fontSize: 24, fontWeight: 700, color: s.color }}>{s.value}</div>
              <div style={{ fontSize: 12, color: '#6b7280' }}>{s.label}</div>
            </Card>
          </Col>
        ))}
      </Row>

      {/* Search + Table */}
      <Card style={{ borderRadius: 12 }} styles={{ body: { padding: 0 } }}>
        <div style={{ padding: '16px 20px', borderBottom: '1px solid #f0f0f0' }}>
          <Input
            placeholder="Tìm kiếm theo tên, mã di tích..."
            prefix={<SearchOutlined style={{ color: '#9ca3af' }} />}
            value={search}
            onChange={e => { setSearch(e.target.value); setPage(0); }}
            style={{ width: 320, borderRadius: 8 }}
            allowClear
          />
        </div>
        <Table
          columns={columns}
          dataSource={sites}
          rowKey="id"
          loading={loading}
          scroll={{ x: 800 }}
          pagination={{
            current: page + 1, total, pageSize: 10,
            onChange: (p) => setPage(p - 1),
            showSizeChanger: false,
            showTotal: (t) => `Tổng ${t} di tích`,
          }}
          style={{ borderRadius: 12 }}
        />
      </Card>

      {/* Modal Create/Edit */}
      <Modal
        title={<span style={{ color: '#2C1810' }}>{editTarget ? '✏️ Chỉnh sửa Di tích' : '➕ Thêm Di tích mới'}</span>}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={onSubmit}
        okText={editTarget ? 'Cập nhật' : 'Tạo mới'}
        cancelText="Hủy"
        confirmLoading={submitting}
        width={760}
        okButtonProps={{ style: { background: 'var(--color-primary)' } }}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Row gutter={[16, 0]}>
            <Col span={12}>
              <Form.Item name="code" label="Mã Di tích" rules={[{ required: true, message: 'Nhập mã di tích' }]}>
                <Input placeholder="VD: SITE_DAI_NOI" style={{ borderRadius: 8 }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="status" label="Trạng thái" rules={[{ required: true }]}>
                <Select style={{ borderRadius: 8 }} options={[
                  { value: 'ACTIVE', label: '✅ Mở cửa' },
                  { value: 'INACTIVE', label: '⛔ Đóng cửa' },
                  { value: 'MAINTENANCE', label: '🔧 Bảo trì' },
                ]} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="latitude" label="Vĩ độ (Latitude)" rules={[{ required: true, message: 'Nhập vĩ độ' }]}>
                <InputNumber style={{ width: '100%', borderRadius: 8 }} placeholder="16.4698" step={0.0001} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="longitude" label="Kinh độ (Longitude)" rules={[{ required: true, message: 'Nhập kinh độ' }]}>
                <InputNumber style={{ width: '100%', borderRadius: 8 }} placeholder="107.5796" step={0.0001} />
              </Form.Item>
            </Col>
            <Col span={16}>
              <Form.Item name="thumbnailUrl" label="URL Ảnh đại diện" rules={[{ required: true, message: 'Nhập URL ảnh' }]}>
                <Input placeholder="https://..." style={{ borderRadius: 8 }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="openingHours" label="Giờ mở cửa">
                <Input placeholder="07:00 – 17:30" style={{ borderRadius: 8 }} />
              </Form.Item>
            </Col>
          </Row>

          {/* Multilingual content */}
          <div style={{ marginTop: 8 }}>
            <Text strong style={{ color: '#2C1810', display: 'block', marginBottom: 12 }}>🌐 Nội dung đa ngôn ngữ</Text>
            <Tabs
              type="card"
              items={[
                { key: 'vi', label: '🇻🇳 Tiếng Việt', children: <TranslationFields langIndex={0} /> },
                { key: 'en', label: '🇬🇧 English', children: <TranslationFields langIndex={1} /> },
              ]}
            />
          </div>
        </Form>
      </Modal>
    </div>
  );
};

const TranslationFields: React.FC<{ langIndex: number }> = ({ langIndex }) => (
  <>
    <Form.Item name={['translations', langIndex, 'languageCode']} hidden>
      <Input />
    </Form.Item>
    <Form.Item
      name={['translations', langIndex, 'name']}
      label="Tên di tích"
      rules={[{ required: true, message: 'Nhập tên' }]}
    >
      <Input placeholder="VD: Đại Nội Huế" style={{ borderRadius: 8 }} />
    </Form.Item>
    <Form.Item name={['translations', langIndex, 'address']} label="Địa chỉ">
      <Input placeholder="Địa chỉ..." style={{ borderRadius: 8 }} />
    </Form.Item>
    <Form.Item name={['translations', langIndex, 'description']} label="Mô tả">
      <Input.TextArea rows={3} placeholder="Mô tả ngắn về di tích..." style={{ borderRadius: 8 }} />
    </Form.Item>
  </>
);

export default SitesPage;
