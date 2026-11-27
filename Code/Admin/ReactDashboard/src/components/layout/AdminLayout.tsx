// layout chính: sidebar + header + content
import React, { useState } from 'react';
import { Layout, Menu, Avatar, Dropdown, Badge, Typography, Button } from 'antd';
import { useNavigate, useLocation, Outlet } from 'react-router-dom';
import {
  DashboardOutlined, ApartmentOutlined, AppstoreOutlined,
  EnvironmentOutlined, StarOutlined, FileTextOutlined,
  LogoutOutlined, UserOutlined, BellOutlined, MenuFoldOutlined, MenuUnfoldOutlined,
} from '@ant-design/icons';
import { useAuthStore } from '../../store/authStore';
import authApi from '../../api/authApi';

const { Sider, Header, Content } = Layout;
const { Text } = Typography;

const menuItems = [
  { key: '/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
  { key: '/sites', icon: <EnvironmentOutlined />, label: 'Di tích lịch sử' },
  { key: '/artifacts', icon: <AppstoreOutlined />, label: 'Hiện vật' },
  { key: '/beacons', icon: <ApartmentOutlined />, label: 'iBeacon' },
  { key: '/feedback', icon: <StarOutlined />, label: 'Đánh giá' },
  { key: '/reports', icon: <FileTextOutlined />, label: 'Báo cáo & Xuất file' },
];

export const AdminLayout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, clearAuth } = useAuthStore();
  const [collapsed, setCollapsed] = useState(false);

  const handleLogout = async () => {
    await authApi.logout();
    clearAuth();
    navigate('/login');
  };

  const userMenu = {
    items: [
      { key: 'profile', icon: <UserOutlined />, label: 'Hồ sơ' },
      { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất', danger: true },
    ],
    onClick: ({ key }: { key: string }) => {
      if (key === 'logout') handleLogout();
    },
  };

  // Find active parent key
  const selectedKey = menuItems.find(m => location.pathname.startsWith(m.key))?.key ?? '/dashboard';

  return (
    <Layout style={{ minHeight: '100vh' }}>
      {/* ---- SIDEBAR ---- */}
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        trigger={null}
        width={240}
        style={{ background: 'var(--color-sidebar-bg)', position: 'fixed', height: '100vh', zIndex: 100 }}
      >
        {/* Logo */}
        <div
          style={{
            display: 'flex', alignItems: 'center', gap: 10,
            padding: collapsed ? '20px 24px' : '20px 20px',
            borderBottom: '1px solid rgba(255,255,255,0.1)',
            cursor: 'pointer',
          }}
          onClick={() => navigate('/dashboard')}
        >
          <div style={{
            width: 36, height: 36, background: 'var(--color-gold)',
            borderRadius: 8, display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 18, flexShrink: 0,
          }}>🏛️</div>
          {!collapsed && (
            <div>
              <div style={{ color: 'var(--color-gold)', fontWeight: 700, fontSize: 15, lineHeight: 1.2 }}>
                Smart Heritage
              </div>
              <div style={{ color: 'rgba(245,237,216,0.6)', fontSize: 11 }}>Admin Portal</div>
            </div>
          )}
        </div>

        {/* Menu */}
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          onClick={({ key }) => navigate(key)}
          style={{ background: 'transparent', border: 'none', marginTop: 8 }}
          items={menuItems.map(item => ({
            ...item,
            style: {
              color: selectedKey === item.key ? 'var(--color-gold)' : 'var(--color-sidebar-text)',
              background: selectedKey === item.key ? 'rgba(201,168,76,0.15)' : 'transparent',
              borderRadius: 6, margin: '2px 8px', paddingLeft: collapsed ? undefined : 16,
            },
          }))}
        />
      </Sider>

      {/* ---- MAIN AREA ---- */}
      <Layout style={{ marginLeft: collapsed ? 80 : 240, transition: 'margin 0.2s' }}>
        {/* Header */}
        <Header style={{
          background: '#fff', display: 'flex', alignItems: 'center',
          justifyContent: 'space-between', padding: '0 24px',
          boxShadow: '0 1px 4px rgba(0,0,0,0.08)', position: 'sticky', top: 0, zIndex: 99,
        }}>
          <Button
            type="text" size="large"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            style={{ color: 'var(--color-primary)' }}
          />

          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <Badge count={2} size="small">
              <BellOutlined style={{ fontSize: 18, color: '#6b7280', cursor: 'pointer' }} />
            </Badge>
            <Dropdown menu={userMenu} placement="bottomRight">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer' }}>
                <Avatar
                  size={32}
                  style={{ background: 'var(--color-primary)', fontSize: 13 }}
                  src={user?.avatarUrl}
                >
                  {!user?.avatarUrl && (user?.fullName?.[0] ?? 'A')}
                </Avatar>
                <div style={{ display: 'flex', flexDirection: 'column', lineHeight: 1.2 }}>
                  <Text strong style={{ fontSize: 13 }}>{user?.fullName ?? 'Admin'}</Text>
                  <Text style={{ fontSize: 11, color: '#9ca3af' }}>Quản trị viên</Text>
                </div>
              </div>
            </Dropdown>
          </div>
        </Header>

        {/* Content */}
        <Content style={{ padding: 24, background: 'var(--color-bg)', minHeight: 'calc(100vh - 64px)' }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default AdminLayout;
