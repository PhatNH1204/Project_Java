// định nghĩa routing cho toàn bộ app
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import AdminLayout from '../components/layout/AdminLayout';
import LoginPage from '../pages/Login/LoginPage';
import DashboardPage from '../pages/Dashboard/DashboardPage';
import SitesPage from '../pages/HeritageSites/SitesPage';
import ArtifactsPage from '../pages/Artifacts/ArtifactsPage';
import BeaconsPage from '../pages/Beacons/BeaconsPage';
import FeedbackPage from '../pages/Feedback/FeedbackPage';
import ReportsPage from '../pages/Reports/ReportsPage';

// Protected Route wrapper
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuthStore();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return <>{children}</>;
};

export const AppRouter: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/login" element={<LoginPage />} />

        {/* Protected admin routes */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <AdminLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />
          <Route path="sites" element={<SitesPage />} />
          <Route path="artifacts" element={<ArtifactsPage />} />
          <Route path="beacons" element={<BeaconsPage />} />
          <Route path="feedback" element={<FeedbackPage />} />
          <Route path="reports" element={<ReportsPage />} />
        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;
