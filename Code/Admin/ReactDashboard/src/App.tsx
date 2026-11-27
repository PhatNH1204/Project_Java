import React from 'react';
import { ConfigProvider } from 'antd';
import viVN from 'antd/locale/vi_VN';
import AppRouter from './routes/AppRouter';
import './index.css';

const App: React.FC = () => {
  return (
    <ConfigProvider
      locale={viVN}
      theme={{
        token: {
          colorPrimary: '#8B4513',
          colorLink: '#8B4513',
          borderRadius: 8,
          fontFamily: "'Segoe UI', system-ui, -apple-system, sans-serif",
        },
        components: {
          Menu: {
            darkItemBg: 'transparent',
            darkItemSelectedBg: 'rgba(201,168,76,0.15)',
            darkItemSelectedColor: '#C9A84C',
            darkItemColor: '#F5EDD8',
          },
          Table: {
            headerBg: '#FFF8F0',
            headerColor: '#5C2D0A',
            headerSortActiveBg: '#FFF0E0',
          },
          Card: {
            borderRadius: 12,
          },
        },
      }}
    >
      <AppRouter />
    </ConfigProvider>
  );
};

export default App;
