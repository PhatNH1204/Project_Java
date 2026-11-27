// ============================================================
// BEACONS PAGE — Quản lý iBeacon + Bản đồ gán hiện vật
// ============================================================
import React, { useEffect, useState, useCallback } from 'react';
import {
  Table, Button, Input, Space, Tag, Tooltip, Typography, Badge,
  Modal, Form, Select, InputNumber, message, Popconfirm, Card, Tabs, Progress, Row, Col,
} from 'antd';
import {
  PlusOutlined, DeleteOutlined, SearchOutlined,
  ApartmentOutlined, LinkOutlined, DisconnectOutlined, ThunderboltOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import beaconApi from '../../api/beaconApi';
import artifactApi from '../../api/artifactApi';
import siteApi from '../../api/siteApi';
import type { Beacon, BeaconListItem, CreateBeaconRequest } from '../../api/types/beacon.types';
import type { ArtifactListItem } from '../../api/types/artifact.types';
import type { SiteListItem } from '../../api/types/site.types';
import { formatDateTime, beaconStatusText, batteryColor } from '../../utils/helpers';

const { Title, Text } = Typography;

const STATUS_COLOR: Record<string, string> = {
  ACTIVE: 'green', INACTIVE: 'default', DAMAGED: 'red', UNASSIGNED: 'gold',
};

const BeaconsPage: React.FC = () => {
  const [beacons, setBeacons] = useState<BeaconListItem[]>([]);
  const [sites, setSites] = useState<SiteListItem[]>([]);
  const [artifacts, setArtifacts] = useState<ArtifactListItem[]>([]);
  const [beaconsOnSite, setBeaconsOnSite] = useState<Beacon[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [filterStatus, setFilterStatus] = useState<string | undefined>();
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [modalOpen, setModalOpen] = useState(false);
  const [assignModal, setAssignModal] = useState<BeaconListItem | null>(null);
  const [mapSiteId, setMapSiteId] = useState<string | undefined>();
  const [form] = Form.useForm();
  const [assignForm] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);

  const loadSites = useCallback(async () => {
    const res = await siteApi.getList({ size: 100 });
    setSites(res.data);
  }, []);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await beaconApi.getList({ page, size: 10, search, status: filterStatus });
      setBeacons(res.data);
      setTotal(res.pagination.totalElements);
    } finally { setLoading(false); }
  }, [page, search, filterStatus]);

  const loadArtifacts = useCallback(async (siteId?: string) => {
    const res = await artifactApi.getList({ size: 100, siteId });
    setArtifacts(res.data);
  }, []);

  const loadMapBeacons = useCallback(async (siteId: string) => {
    const list = await beaconApi.getBySite(siteId);
    setBeaconsOnSite(list);
  }, []);

  useEffect(() => { loadSites(); }, [loadSites]);
  useEffect(() => { load(); }, [load]);
  useEffect(() => { if (mapSiteId) { loadArtifacts(mapSiteId); loadMapBeacons(mapSiteId); } }, [mapSiteId, loadArtifacts, loadMapBeacons]);

  const openCreate = () => {
    form.resetFields();
    setModalOpen(true);
  };

  const openAssign = (beacon: BeaconListItem) => {
    setAssignModal(beacon);
    assignForm.resetFields();
    loadArtifacts();
  };

  const onDelete = async (id: string) => {
    try {
      await beaconApi.remove(id);
      message.success('Đã xóa beacon');
      load();
    } catch { message.error('Xóa thất bại'); }
  };

  const onUnassign = async (id: string) => {
    try {
      await beaconApi.unassign(id);
      message.success('Đã hủy gán beacon');
      load();
    } catch { message.error('Hủy gán thất bại'); }
  };

  const onSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      const body: CreateBeaconRequest = {
        uuid: values.uuid, major: values.major, minor: values.minor,
        macAddress: values.macAddress, name: values.name,
        txPower: values.txPower, siteId: values.siteId,
      };
      await beaconApi.create(body);
      message.success('Thêm beacon thành công');
      setModalOpen(false);
      load();
    } catch (err: unknown) {
      if ((err as { errorFields?: unknown })?.errorFields) return;
      message.error('Lưu thất bại');
    } finally { setSubmitting(false); }
  };

  const onAssign = async () => {
    if (!assignModal) return;
    try {
      const values = await assignForm.validateFields();
      setSubmitting(true);
      await beaconApi.assign(assignModal.id, { artifactId: values.artifactId });
      message.success('Gán beacon thành công');
      setAssignModal(null);
      load();
    } catch (err: unknown) {
      if ((err as { errorFields?: unknown })?.errorFields) return;
      message.error('Gán thất bại');
    } finally { setSubmitting(false); }
  };

  const columns: ColumnsType<BeaconListItem> = [
    {
      title: 'Tên Beacon', dataIndex: 'name', key: 'name',
      render: (name, r) => (
        <div>
          <div style={{ fontWeight: 600, color: '#2C1810' }}>{name}</div>
          <Text style={{ fontSize: 11, color: '#9ca3af', fontFamily: 'monospace' }}>
            UUID: {r.uuid.substring(0, 18)}... | Maj: {r.major} | Min: {r.minor}
          </Text>
        </div>
      ),
    },
    {
      title: 'Trạng thái', dataIndex: 'status', width: 130,
      render: (s) => <Badge status={s === 'ACTIVE' ? 'success' : s === 'DAMAGED' ? 'error' : s === 'INACTIVE' ? 'default' : 'warning'}
        text={<Tag color={STATUS_COLOR[s]}>{beaconStatusText[s] ?? s}</Tag>} />,
    },
    {
      title: 'Pin', dataIndex: 'batteryLevel', width: 120,
      render: (level) => level != null
        ? <div>
          <Progress percent={level} strokeColor={batteryColor(level)} size="small" showInfo={false} style={{ marginBottom: 2 }} />
          <Text style={{ fontSize: 11, color: batteryColor(level) }}>{level}%</Text>
        </div>
        : <Text style={{ color: '#9ca3af' }}>—</Text>,
    },
    {
      title: 'Di tích', dataIndex: 'siteName', width: 150,
      render: (name) => name ? <Tag color="orange">{name}</Tag> : <Text style={{ color: '#9ca3af' }}>Chưa gán</Text>,
    },
    {
      title: 'Hiện vật', dataIndex: 'assignedArtifactName', width: 180,
      render: (name) => name ? <Tag color="blue">{name}</Tag> : <Tag color="default">Chưa gán</Tag>,
    },
    {
      title: 'Lần cuối thấy', dataIndex: 'lastSeenAt', width: 140,
      render: (d) => <Text style={{ fontSize: 11 }}>{formatDateTime(d)}</Text>,
    },
    {
      title: 'Thao tác', key: 'actions', width: 130, fixed: 'right',
      render: (_, r) => (
        <Space>
          {r.assignedArtifactName
            ? <Tooltip title="Hủy gán">
              <Popconfirm title="Hủy gán beacon này?" onConfirm={() => onUnassign(r.id)} okText="Hủy gán" cancelText="Không">
                <Button type="text" icon={<DisconnectOutlined />} style={{ color: '#f59e0b' }} />
              </Popconfirm>
            </Tooltip>
            : <Tooltip title="Gán hiện vật">
              <Button type="text" icon={<LinkOutlined />} onClick={() => openAssign(r)} style={{ color: '#10b981' }} />
            </Tooltip>
          }
          <Tooltip title="Xóa">
            <Popconfirm title="Xóa beacon này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy" okButtonProps={{ danger: true }}>
              <Button type="text" danger icon={<DeleteOutlined />} />
            </Popconfirm>
          </Tooltip>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 20 }}>
        <div>
          <Title level={3} style={{ margin: 0, color: '#2C1810' }}>
            <ApartmentOutlined /> Quản lý iBeacon
          </Title>
          <Text style={{ color: '#6b7280' }}>Thiết bị iBeacon và bản đồ gán hiện vật</Text>
        </div>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={openCreate}
          style={{ borderRadius: 8, background: 'var(--color-primary)' }}>
          Thêm Beacon
        </Button>
      </div>

      <Tabs
        type="card"
        items={[
          {
            key: 'list',
            label: '📋 Danh sách Beacon',
            children: (
              <Card style={{ borderRadius: 12 }} styles={{ body: { padding: 0 } }}>
                <div style={{ padding: '16px 20px', borderBottom: '1px solid #f0f0f0', display: 'flex', gap: 12, flexWrap: 'wrap' }}>
                  <Input
                    placeholder="Tìm theo tên, UUID..."
                    prefix={<SearchOutlined style={{ color: '#9ca3af' }} />}
                    value={search}
                    onChange={e => { setSearch(e.target.value); setPage(0); }}
                    style={{ width: 260, borderRadius: 8 }}
                    allowClear
                  />
                  <Select
                    placeholder="Lọc theo trạng thái"
                    allowClear style={{ width: 180 }}
                    value={filterStatus}
                    onChange={v => { setFilterStatus(v); setPage(0); }}
                    options={[
                      { value: 'ACTIVE', label: '✅ Hoạt động' },
                      { value: 'INACTIVE', label: '⬜ Không HĐ' },
                      { value: 'DAMAGED', label: '❌ Hỏng' },
                      { value: 'UNASSIGNED', label: '🟡 Chưa gán' },
                    ]}
                  />
                </div>
                <Table
                  columns={columns} dataSource={beacons} rowKey="id"
                  loading={loading} scroll={{ x: 1000 }}
                  pagination={{
                    current: page + 1, total, pageSize: 10,
                    onChange: (p) => setPage(p - 1),
                    showTotal: (t) => `Tổng ${t} beacon`,
                  }}
                />
              </Card>
            ),
          },
          {
            key: 'map',
            label: '🗺️ Bản đồ Beacon',
            children: (
              <BeaconMapView
                sites={sites}
                selectedSiteId={mapSiteId}
                onSelectSite={setMapSiteId}
                beacons={beaconsOnSite}
                artifacts={artifacts}
                onRefresh={() => mapSiteId && loadMapBeacons(mapSiteId)}
              />
            ),
          },
        ]}
      />

      {/* Create Beacon Modal */}
      <Modal
        title="➕ Thêm iBeacon mới"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={onSubmit}
        okText="Thêm"
        cancelText="Hủy"
        confirmLoading={submitting}
        okButtonProps={{ style: { background: 'var(--color-primary)' } }}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item name="name" label="Tên Beacon" rules={[{ required: true }]}>
            <Input placeholder="Beacon – Phòng trưng bày số 1" style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="uuid" label="UUID" rules={[{ required: true }]}>
            <Input placeholder="FDA50693-A4E2-4FB1-AFCF-C6EB07647825" style={{ borderRadius: 8, fontFamily: 'monospace' }} />
          </Form.Item>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="major" label="Major" rules={[{ required: true }]}>
                <InputNumber min={0} max={65535} style={{ width: '100%', borderRadius: 8 }} placeholder="1" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="minor" label="Minor" rules={[{ required: true }]}>
                <InputNumber min={0} max={65535} style={{ width: '100%', borderRadius: 8 }} placeholder="1" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="macAddress" label="MAC Address">
                <Input placeholder="E2:85:7B:3A:1C:00" style={{ borderRadius: 8, fontFamily: 'monospace' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="txPower" label="TX Power (dBm)">
                <InputNumber style={{ width: '100%', borderRadius: 8 }} placeholder="-59" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="siteId" label="Di tích (tùy chọn)">
            <Select placeholder="Chọn di tích..." allowClear
              options={sites.map(s => ({ value: s.id, label: s.nameVi }))} />
          </Form.Item>
        </Form>
      </Modal>

      {/* Assign Artifact Modal */}
      <Modal
        title={<span>🔗 Gán Hiện vật cho Beacon: <Text style={{ color: '#8B4513' }}>{assignModal?.name}</Text></span>}
        open={!!assignModal}
        onCancel={() => setAssignModal(null)}
        onOk={onAssign}
        okText="Gán"
        cancelText="Hủy"
        confirmLoading={submitting}
        okButtonProps={{ style: { background: '#10b981', borderColor: '#10b981' } }}
        destroyOnHidden
      >
        <Form form={assignForm} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item name="artifactId" label="Chọn Hiện vật" rules={[{ required: true, message: 'Chọn hiện vật để gán' }]}>
            <Select
              showSearch
              placeholder="Tìm và chọn hiện vật..."
              optionFilterProp="label"
              options={artifacts
                .filter(a => !a.beaconAssigned || a.beaconAssigned === assignModal?.id)
                .map(a => ({
                  value: a.id,
                  label: `${a.nameVi} — ${a.siteName}`,
                }))}
            />
          </Form.Item>
          <div style={{ padding: '10px 12px', background: '#f0fdf4', borderRadius: 8, border: '1px solid #bbf7d0' }}>
            <Text style={{ fontSize: 12, color: '#065f46' }}>
              <ThunderboltOutlined /> Sau khi gán, khi du khách đứng gần beacon này (RSSI &gt; ngưỡng Near),
              ứng dụng sẽ tự động hiển thị nội dung thuyết minh của hiện vật được gán.
            </Text>
          </div>
        </Form>
      </Modal>
    </div>
  );
};

// ---- Beacon Map View Component ----
interface BeaconMapViewProps {
  sites: SiteListItem[];
  selectedSiteId?: string;
  onSelectSite: (id: string) => void;
  beacons: Beacon[];
  artifacts: ArtifactListItem[];
  onRefresh: () => void;
}

const BeaconMapView: React.FC<BeaconMapViewProps> = ({ sites, selectedSiteId, onSelectSite, beacons }) => {
  const selectedSite = sites.find(s => s.id === selectedSiteId);

  return (
    <Card style={{ borderRadius: 12 }}>
      <div style={{ marginBottom: 16, display: 'flex', gap: 16, alignItems: 'center', flexWrap: 'wrap' }}>
        <Select
          placeholder="Chọn di tích để xem bản đồ..."
          value={selectedSiteId}
          onChange={onSelectSite}
          style={{ width: 280 }}
          options={sites.map(s => ({ value: s.id, label: s.nameVi }))}
        />
        {selectedSiteId && (
          <div style={{ display: 'flex', gap: 16 }}>
            <Badge color="green" text={<Text style={{ fontSize: 12 }}>Beacon hoạt động ({beacons.filter(b => b.status === 'ACTIVE').length})</Text>} />
            <Badge color="gold" text={<Text style={{ fontSize: 12 }}>Chưa gán ({beacons.filter(b => !b.assignedArtifactId).length})</Text>} />
            <Badge color="red" text={<Text style={{ fontSize: 12 }}>Hỏng ({beacons.filter(b => b.status === 'DAMAGED').length})</Text>} />
          </div>
        )}
      </div>

      {!selectedSiteId ? (
        <div style={{ textAlign: 'center', padding: 80, color: '#9ca3af' }}>
          <div style={{ fontSize: 48, marginBottom: 12 }}>🗺️</div>
          <Text style={{ color: '#6b7280', fontSize: 15 }}>Chọn một di tích để xem bản đồ bố trí beacon</Text>
        </div>
      ) : (
        <div>
          <div style={{
            position: 'relative', width: '100%', paddingBottom: '50%',
            background: selectedSite ? `url('${sites.find(s => s.id === selectedSiteId)?.thumbnailUrl}') center/cover` : '#f3f4f6',
            borderRadius: 12, overflow: 'hidden', border: '2px solid #e5e7eb',
          }}>
            {/* Overlay */}
            <div style={{ position: 'absolute', inset: 0, background: 'rgba(44, 24, 16, 0.5)' }} />

            {/* Floor Plan Label */}
            <div style={{ position: 'absolute', top: 12, left: 12, background: 'rgba(0,0,0,0.6)', color: '#fff', padding: '4px 10px', borderRadius: 6, fontSize: 13 }}>
              🏛️ {selectedSite ? sites.find(s => s.id === selectedSiteId)?.nameVi : ''} — Mặt bằng Beacon
            </div>

            {/* Beacon Dots */}
            {beacons.map(beacon => (
              <Tooltip
                key={beacon.id}
                title={
                  <div>
                    <div style={{ fontWeight: 600 }}>{beacon.name}</div>
                    <div style={{ fontSize: 11 }}>Maj: {beacon.major} | Min: {beacon.minor}</div>
                    {beacon.assignedArtifactName && <div style={{ fontSize: 11, color: '#86efac' }}>→ {beacon.assignedArtifactName}</div>}
                    {beacon.batteryLevel != null && <div style={{ fontSize: 11 }}>🔋 {beacon.batteryLevel}%</div>}
                  </div>
                }
              >
                <div
                  style={{
                    position: 'absolute',
                    left: `${beacon.positionX ?? 50}%`,
                    top: `${beacon.positionY ?? 50}%`,
                    transform: 'translate(-50%, -50%)',
                    width: 36, height: 36, borderRadius: '50%',
                    background: beacon.status === 'ACTIVE' ? '#10b981' : beacon.status === 'DAMAGED' ? '#ef4444' : '#f59e0b',
                    border: `3px solid ${beacon.assignedArtifactId ? '#fff' : 'rgba(255,255,255,0.4)'}`,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    cursor: 'pointer', boxShadow: '0 2px 8px rgba(0,0,0,0.5)',
                    fontSize: 16, transition: 'transform 0.2s',
                  }}
                >
                  📡
                </div>
              </Tooltip>
            ))}
          </div>

          {/* Beacon List Below Map */}
          <div style={{ marginTop: 16 }}>
            <Text strong style={{ color: '#2C1810', display: 'block', marginBottom: 10 }}>
              Danh sách Beacon tại {sites.find(s => s.id === selectedSiteId)?.nameVi}:
            </Text>
            <Row gutter={[8, 8]}>
              {beacons.map(b => (
                <Col xs={24} sm={12} md={8} key={b.id}>
                  <div style={{
                    padding: '10px 12px', borderRadius: 8, border: '1px solid #e5e7eb',
                    background: b.status === 'ACTIVE' ? '#f0fdf4' : '#fafafa',
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 4 }}>
                      <Text strong style={{ fontSize: 13 }}>📡 {b.name}</Text>
                      <Badge status={b.status === 'ACTIVE' ? 'success' : b.status === 'DAMAGED' ? 'error' : 'warning'} />
                    </div>
                    <div style={{ fontSize: 11, color: '#6b7280' }}>Maj:{b.major} / Min:{b.minor}</div>
                    {b.assignedArtifactName
                      ? <Tag color="blue" style={{ marginTop: 4, fontSize: 11 }}>{b.assignedArtifactName}</Tag>
                      : <Tag color="default" style={{ marginTop: 4, fontSize: 11 }}>Chưa gán hiện vật</Tag>
                    }
                    {b.batteryLevel != null && (
                      <Progress
                        percent={b.batteryLevel}
                        strokeColor={batteryColor(b.batteryLevel)}
                        size="small" showInfo={false}
                        style={{ marginTop: 6 }}
                      />
                    )}
                  </div>
                </Col>
              ))}
            </Row>
          </div>
        </div>
      )}
    </Card>
  );
};

export default BeaconsPage;
