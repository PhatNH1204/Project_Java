// trang đăng nhập admin
import React, { useState } from 'react';
import { Form, Input, Button, Card, Typography, Alert, Divider } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import authApi from '../../api/authApi';
import { useAuthStore } from '../../store/authStore';

const { Title, Text } = Typography;

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { setAuth } = useAuthStore();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true);
    setError(null);
    try {
      const res = await authApi.login(values);
      setAuth(res.data.user, res.data.accessToken);
      navigate('/dashboard');
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
        ?? 'Đăng nhập thất bại. Vui lòng thử lại.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'linear-gradient(135deg, #2C1810 0%, #5C2D0A 50%, #8B4513 100%)',
      padding: 24,
    }}>
      <div style={{ width: '100%', maxWidth: 420 }}>
        {/* Logo & Title */}
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <div style={{ fontSize: 56, marginBottom: 12 }}>🏛️</div>
          <Title level={2} style={{ color: '#C9A84C', margin: 0, fontWeight: 700 }}>
            Smart Heritage
          </Title>
          <Text style={{ color: 'rgba(245,237,216,0.7)', fontSize: 14 }}>
            Hệ thống quản trị Di sản Thông minh
          </Text>
        </div>

        <Card
          style={{ borderRadius: 16, boxShadow: '0 20px 60px rgba(0,0,0,0.3)', border: 'none' }}
          styles={{ body: { padding: '32px 32px' } }}
        >
          <Title level={4} style={{ textAlign: 'center', marginBottom: 24, color: '#5C2D0A' }}>
            Đăng nhập quản trị
          </Title>

          {error && (
            <Alert type="error" message={error} showIcon style={{ marginBottom: 16, borderRadius: 8 }} closable onClose={() => setError(null)} />
          )}

          <Form name="login" layout="vertical" onFinish={onFinish} autoComplete="off">
            <Form.Item
              name="username"
              rules={[{ required: true, message: 'Vui lòng nhập tên đăng nhập' }]}
            >
              <Input
                prefix={<UserOutlined style={{ color: '#8B4513' }} />}
                placeholder="Tên đăng nhập"
                size="large"
                style={{ borderRadius: 8 }}
              />
            </Form.Item>

            <Form.Item
              name="password"
              rules={[{ required: true, message: 'Vui lòng nhập mật khẩu' }]}
            >
              <Input.Password
                prefix={<LockOutlined style={{ color: '#8B4513' }} />}
                placeholder="Mật khẩu"
                size="large"
                style={{ borderRadius: 8 }}
              />
            </Form.Item>

            <Button
              type="primary"
              htmlType="submit"
              size="large"
              block
              loading={loading}
              style={{
                borderRadius: 8, height: 48, fontSize: 15, fontWeight: 600,
                background: 'linear-gradient(135deg, #8B4513 0%, #C9A84C 100%)',
                border: 'none', marginTop: 8,
              }}
            >
              {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
            </Button>
          </Form>

          <Divider style={{ margin: '20px 0' }} />
          <div style={{ textAlign: 'center', background: '#FFF8F0', borderRadius: 8, padding: '10px 16px' }}>
            <Text style={{ fontSize: 12, color: '#6b7280' }}>
              🔑 Demo: <strong>admin</strong> / <strong>admin123</strong>
            </Text>
          </div>
        </Card>

        <Text style={{ display: 'block', textAlign: 'center', marginTop: 20, color: 'rgba(245,237,216,0.4)', fontSize: 12 }}>
          © 2026 Smart Heritage – Hệ thống Du lịch Di sản Thông minh
        </Text>
      </div>
    </div>
  );
};

export default LoginPage;
