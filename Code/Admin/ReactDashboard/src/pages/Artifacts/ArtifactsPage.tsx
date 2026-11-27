// ============================================================
// ARTIFACTS PAGE — Quản lý Hiện vật + Media Upload
// ============================================================
import React, { useEffect, useState, useCallback } from 'react';
import {
  Table, Button, Input, Space, Tag, Tooltip, Image, Typography,
  Modal, Form, Select, Tabs, InputNumber, message, Popconfirm, Card, Upload,
  Row, Col, Spin, Badge,
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined,
  AppstoreOutlined, SoundOutlined, VideoCameraOutlined,
  PictureOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import artifactApi from '../../api/artifactApi';
import siteApi from '../../api/siteApi';
import type { ArtifactListItem, Artifact, CreateArtifactRequest, MediaFile } from '../../api/types/artifact.types';
import type { SiteListItem } from '../../api/types/site.types';
import { formatDate } from '../../utils/helpers';

const { Title, Text } = Typography;

const ArtifactsPage: React.FC = () => {
  const [artifacts, setArtifacts] = useState<ArtifactListItem[]>([]);
  const [sites, setSites] = useState<SiteListItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [filterSite, setFilterSite] = useState<string | undefined>();
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [modalOpen, setModalOpen] = useState(false);
  const [detailModal, setDetailModal] = useState<Artifact | null>(null);
  const [editTarget, setEditTarget] = useState<Artifact | null>(null);
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);
  const [uploadedMedia, setUploadedMedia] = useState<MediaFile[]>([]);
  const [uploadingType, setUploadingType] = useState<'IMAGE' | 'AUDIO' | 'VIDEO' | null>(null);

  const loadSites = useCallback(async () => {
    const res = await siteApi.getList({ size: 100 });
    setSites(res.data);
  }, []);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await artifactApi.getList({ page, size: 10, search, siteId: filterSite });
      setArtifacts(res.data);
      setTotal(res.pagination.totalElements);
    } finally { setLoading(false); }
  }, [page, search, filterSite]);

  useEffect(() => { loadSites(); }, [loadSites]);
  useEffect(() => { load(); }, [load]);

  const openCreate = () => {
    setEditTarget(null);
    setUploadedMedia([]);
    form.resetFields();
    form.setFieldsValue({
      translations: [
        { languageCode: 'vi', name: '', description: '', historicalContext: '' },
        { languageCode: 'en', name: '', description: '', historicalContext: '' },
      ],
    });
    setModalOpen(true);
  };

  const openEdit = async (id: string) => {
    try {
      const art = await artifactApi.getById(id);
      setEditTarget(art);
      setUploadedMedia(art.media);
      form.setFieldsValue({
        code: art.code, siteId: art.siteId,
        thumbnailUrl: art.thumbnailUrl,
        yearCreated: art.yearCreated, material: art.material,
        dimensions: art.dimensions,
        translations: art.translations,
      });
      setModalOpen(true);
    } catch { message.error('Không thể tải dữ liệu hiện vật'); }
  };

  const openDetail = async (id: string) => {
    try {
      const art = await artifactApi.getById(id);
      setDetailModal(art);
    } catch { message.error('Không thể tải dữ liệu'); }
  };

  const onDelete = async (id: string) => {
    try {
      await artifactApi.remove(id);
      message.success('Đã xóa hiện vật');
      load();
    } catch { message.error('Xóa thất bại'); }
  };

  const handleUpload = async (file: File, type: 'IMAGE' | 'AUDIO' | 'VIDEO') => {
    setUploadingType(type);
    try {
      const res = await artifactApi.uploadMedia(file, type);
      const newMedia: MediaFile = {
        id: res.id, url: res.url, publicId: res.publicId, type, sortOrder: uploadedMedia.length + 1,
        title: file.name.replace(/\.[^.]+$/, ''),
      };
      setUploadedMedia(prev => [...prev, newMedia]);
      message.success(`Tải lên ${type === 'IMAGE' ? 'ảnh' : type === 'AUDIO' ? 'audio' : 'video'} thành công`);
    } catch { message.error('Tải lên thất bại'); }
    finally { setUploadingType(null); }
    return false; // Prevent default upload
  };

  const onSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      const body: CreateArtifactRequest = {
        code: values.code, siteId: values.siteId,
        thumbnailUrl: values.thumbnailUrl,
        yearCreated: values.yearCreated, material: values.material,
        dimensions: values.dimensions,
        translations: values.translations,
        media: uploadedMedia.map(({ id: _id, ...m }) => m),
      };
      if (editTarget) {
        await artifactApi.update(editTarget.id, body);
        message.success('Cập nhật hiện vật thành công');
      } else {
        await artifactApi.create(body);
        message.success('Tạo hiện vật thành công');
      }
      setModalOpen(false);
      load();
    } catch (err: unknown) {
      if ((err as { errorFields?: unknown })?.errorFields) return;
      message.error('Lưu thất bại');
    } finally { setSubmitting(false); }
  };

  const columns: ColumnsType<ArtifactListItem> = [
    {
      title: 'Ảnh', dataIndex: 'thumbnailUrl', width: 80,
      render: (url) => <Image src={url} width={60} height={45} style={{ objectFit: 'cover', borderRadius: 6 }} />,
    },
    {
      title: 'Tên Hiện vật', key: 'name',
      render: (_, r) => (
        <div>
          <Button type="link" style={{ padding: 0, fontWeight: 600, color: '#2C1810', textAlign: 'left', height: 'auto' }}
            onClick={() => openDetail(r.id)}>
            {r.nameVi}
          </Button>
          <div style={{ fontSize: 12, color: '#9ca3af' }}>{r.nameEn}</div>
          <Text style={{ fontSize: 11, color: '#6b7280' }}>#{r.code}</Text>
        </div>
      ),
    },
    {
      title: 'Di tích', dataIndex: 'siteName', width: 150,
      render: (name) => <Tag color="orange" style={{ fontSize: 12 }}>{name}</Tag>,
    },
    {
      title: 'Tương tác', dataIndex: 'interactionCount', width: 110, align: 'center',
      render: (n) => <Tag color="blue">{n.toLocaleString()}</Tag>,
      sorter: (a, b) => a.interactionCount - b.interactionCount,
    },
    {
      title: 'Beacon', dataIndex: 'beaconAssigned', width: 100, align: 'center',
      render: (b) => b
        ? <Badge status="success" text={<Text style={{ fontSize: 12 }}>Đã gán</Text>} />
        : <Badge status="default" text={<Text style={{ fontSize: 12 }}>Chưa gán</Text>} />,
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
          <Popconfirm title="Xóa hiện vật này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy" okButtonProps={{ danger: true }}>
            <Tooltip title="Xóa"><Button type="text" danger icon={<DeleteOutlined />} /></Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 20 }}>
        <div>
          <Title level={3} style={{ margin: 0, color: '#2C1810' }}>
            <AppstoreOutlined /> Quản lý Hiện vật
          </Title>
          <Text style={{ color: '#6b7280' }}>Toàn bộ hiện vật lịch sử trong hệ thống</Text>
        </div>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={openCreate}
          style={{ borderRadius: 8, background: 'var(--color-primary)' }}>
          Thêm Hiện vật
        </Button>
      </div>

      <Card style={{ borderRadius: 12 }} styles={{ body: { padding: 0 } }}>
        <div style={{ padding: '16px 20px', borderBottom: '1px solid #f0f0f0', display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          <Input
            placeholder="Tìm kiếm hiện vật..."
            prefix={<SearchOutlined style={{ color: '#9ca3af' }} />}
            value={search}
            onChange={e => { setSearch(e.target.value); setPage(0); }}
            style={{ width: 280, borderRadius: 8 }}
            allowClear
          />
          <Select
            placeholder="Lọc theo di tích"
            allowClear
            style={{ width: 200, borderRadius: 8 }}
            value={filterSite}
            onChange={v => { setFilterSite(v); setPage(0); }}
            options={sites.map(s => ({ value: s.id, label: s.nameVi }))}
          />
        </div>
        <Table
          columns={columns} dataSource={artifacts} rowKey="id"
          loading={loading} scroll={{ x: 900 }}
          pagination={{
            current: page + 1, total, pageSize: 10,
            onChange: (p) => setPage(p - 1),
            showTotal: (t) => `Tổng ${t} hiện vật`,
          }}
        />
      </Card>

      {/* Detail Modal */}
      <Modal
        open={!!detailModal}
        onCancel={() => setDetailModal(null)}
        footer={null}
        width={700}
        title={<span style={{ color: '#2C1810' }}>📜 Chi tiết Hiện vật</span>}
        destroyOnHidden
      >
        {detailModal && (
          <div>
            <Image src={detailModal.thumbnailUrl} style={{ width: '100%', maxHeight: 300, objectFit: 'cover', borderRadius: 10, marginBottom: 16 }} />
            <Tabs items={[
              {
                key: 'vi', label: '🇻🇳 Tiếng Việt',
                children: (
                  <div>
                    <Title level={4}>{detailModal.translations.find(t => t.languageCode === 'vi')?.name}</Title>
                    <Text>{detailModal.translations.find(t => t.languageCode === 'vi')?.description}</Text>
                    <div style={{ marginTop: 12, padding: 12, background: '#FFF8F0', borderRadius: 8 }}>
                      <Text strong>Bối cảnh lịch sử: </Text>
                      <Text style={{ fontSize: 13 }}>{detailModal.translations.find(t => t.languageCode === 'vi')?.historicalContext}</Text>
                    </div>
                  </div>
                ),
              },
              {
                key: 'en', label: '🇬🇧 English',
                children: (
                  <div>
                    <Title level={4}>{detailModal.translations.find(t => t.languageCode === 'en')?.name}</Title>
                    <Text>{detailModal.translations.find(t => t.languageCode === 'en')?.description}</Text>
                    <div style={{ marginTop: 12, padding: 12, background: '#FFF8F0', borderRadius: 8 }}>
                      <Text strong>Historical Context: </Text>
                      <Text style={{ fontSize: 13 }}>{detailModal.translations.find(t => t.languageCode === 'en')?.historicalContext}</Text>
                    </div>
                  </div>
                ),
              },
              {
                key: 'media', label: `🎨 Media (${detailModal.media.length})`,
                children: (
                  <div>
                    {detailModal.media.map(m => (
                      <div key={m.id} style={{ marginBottom: 12, padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
                          {m.type === 'IMAGE' && <PictureOutlined style={{ color: '#3b82f6' }} />}
                          {m.type === 'AUDIO' && <SoundOutlined style={{ color: '#10b981' }} />}
                          {m.type === 'VIDEO' && <VideoCameraOutlined style={{ color: '#8b5cf6' }} />}
                          <Text strong>{m.title ?? m.type}</Text>
                          <Tag>{m.type}</Tag>
                        </div>
                        {m.type === 'IMAGE' && <Image src={m.url} height={120} style={{ objectFit: 'cover', borderRadius: 6 }} />}
                        {m.type === 'AUDIO' && <audio controls src={m.url} style={{ width: '100%' }} />}
                        {m.type === 'VIDEO' && <video controls src={m.url} style={{ width: '100%', maxHeight: 180, borderRadius: 6 }} />}
                      </div>
                    ))}
                    {detailModal.media.length === 0 && <Text style={{ color: '#9ca3af' }}>Chưa có media</Text>}
                  </div>
                ),
              },
            ]} />
          </div>
        )}
      </Modal>

      {/* Create/Edit Modal */}
      <Modal
        title={<span style={{ color: '#2C1810' }}>{editTarget ? '✏️ Chỉnh sửa Hiện vật' : '➕ Thêm Hiện vật mới'}</span>}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={onSubmit}
        okText={editTarget ? 'Cập nhật' : 'Tạo mới'}
        cancelText="Hủy"
        confirmLoading={submitting}
        width={820}
        okButtonProps={{ style: { background: 'var(--color-primary)' } }}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Row gutter={[16, 0]}>
            <Col span={8}>
              <Form.Item name="code" label="Mã hiện vật" rules={[{ required: true }]}>
                <Input placeholder="ART_001" style={{ borderRadius: 8 }} />
              </Form.Item>
            </Col>
            <Col span={16}>
              <Form.Item name="siteId" label="Di tích" rules={[{ required: true }]}>
                <Select placeholder="Chọn di tích..." style={{ borderRadius: 8 }}
                  options={sites.map(s => ({ value: s.id, label: s.nameVi }))} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item name="thumbnailUrl" label="URL ảnh đại diện" rules={[{ required: true }]}>
                <Input placeholder="https://..." style={{ borderRadius: 8 }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="yearCreated" label="Năm chế tác">
                <InputNumber style={{ width: '100%', borderRadius: 8 }} placeholder="1805" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="material" label="Chất liệu">
                <Input placeholder="Gỗ, Đồng..." style={{ borderRadius: 8 }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="dimensions" label="Kích thước">
                <Input placeholder="Cao 2m x Rộng 1.5m" style={{ borderRadius: 8 }} />
              </Form.Item>
            </Col>
          </Row>

          {/* Multilingual */}
          <div style={{ marginBottom: 16 }}>
            <Text strong style={{ color: '#2C1810' }}>🌐 Nội dung đa ngôn ngữ</Text>
            <Tabs type="card" style={{ marginTop: 8 }} items={[
              {
                key: 'vi', label: '🇻🇳 Tiếng Việt',
                children: <ArtifactTranslationFields langIndex={0} />,
              },
              {
                key: 'en', label: '🇬🇧 English',
                children: <ArtifactTranslationFields langIndex={1} />,
              },
            ]} />
          </div>

          {/* Media Upload */}
          <div>
            <Text strong style={{ color: '#2C1810', display: 'block', marginBottom: 12 }}>🎨 Tệp đa phương tiện</Text>
            <Row gutter={[8, 8]}>
              {(['IMAGE', 'AUDIO', 'VIDEO'] as const).map(type => (
                <Col span={8} key={type}>
                  <Upload
                    showUploadList={false}
                    beforeUpload={(file) => { handleUpload(file, type); return false; }}
                    accept={type === 'IMAGE' ? 'image/*' : type === 'AUDIO' ? 'audio/*' : 'video/*'}
                  >
                    <Button
                      icon={type === 'IMAGE' ? <PictureOutlined /> : type === 'AUDIO' ? <SoundOutlined /> : <VideoCameraOutlined />}
                      block style={{ borderRadius: 8 }}
                      loading={uploadingType === type}
                    >
                      {type === 'IMAGE' ? 'Tải ảnh' : type === 'AUDIO' ? 'Tải audio' : 'Tải video'}
                    </Button>
                  </Upload>
                </Col>
              ))}
            </Row>
            {uploadedMedia.length > 0 && (
              <div style={{ marginTop: 12 }}>
                {uploadedMedia.map((m, i) => (
                  <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '6px 10px', background: '#f9fafb', borderRadius: 6, marginBottom: 6 }}>
                    <Tag color={m.type === 'IMAGE' ? 'blue' : m.type === 'AUDIO' ? 'green' : 'purple'} style={{ marginRight: 0 }}>{m.type}</Tag>
                    <Text style={{ fontSize: 12, flex: 1, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{m.title ?? m.url}</Text>
                    <Button type="text" danger size="small" onClick={() => setUploadedMedia(prev => prev.filter((_, idx) => idx !== i))}>✕</Button>
                  </div>
                ))}
              </div>
            )}
            {uploadingType && <Spin size="small" style={{ marginTop: 8 }} tip="Đang tải lên..." />}
          </div>
        </Form>
      </Modal>
    </div>
  );
};

const ArtifactTranslationFields: React.FC<{ langIndex: number }> = ({ langIndex }) => (
  <>
    <Form.Item name={['translations', langIndex, 'languageCode']} hidden><Input /></Form.Item>
    <Form.Item name={['translations', langIndex, 'name']} label="Tên hiện vật" rules={[{ required: true, message: 'Nhập tên' }]}>
      <Input placeholder="VD: Ngai Vàng Triều Nguyễn" style={{ borderRadius: 8 }} />
    </Form.Item>
    <Form.Item name={['translations', langIndex, 'description']} label="Mô tả">
      <Input.TextArea rows={3} placeholder="Mô tả hiện vật..." style={{ borderRadius: 8 }} />
    </Form.Item>
    <Form.Item name={['translations', langIndex, 'historicalContext']} label="Bối cảnh lịch sử">
      <Input.TextArea rows={3} placeholder="Bối cảnh lịch sử chi tiết..." style={{ borderRadius: 8 }} />
    </Form.Item>
  </>
);

export default ArtifactsPage;
