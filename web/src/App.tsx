import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { DashboardLayout } from './layouts/DashboardLayout';
import { Login } from './pages/Login';
import { DashboardHome } from './pages/DashboardHome';
import { AttendanceAnalytics } from './pages/AttendanceAnalytics';
import { FileAnalysis } from './pages/FileAnalysis';
import { AIAnalytics } from './pages/AIAnalytics';
import { Academics } from './pages/Academics';
import { Notifications } from './pages/Notifications';
import { Profile } from './pages/Profile';
import { Settings } from './pages/Settings';
import { Loader2, School } from 'lucide-react';

const ProtectedRoute = ({ children, allowedRoles }: { children: React.ReactNode, allowedRoles?: string[] }) => {
  const { currentUser, userData, loading } = useAuth();
  
  if (loading) {
    return (
      <div className="flex h-screen w-screen flex-col items-center justify-center bg-slate-50 dark:bg-crescent-dark-bg transition-colors">
        <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-crescent-navy-900 text-white shadow-lg mb-4">
          <School className="h-7 w-7 text-emerald-400" />
        </div>
        <div className="flex items-center gap-2 text-sm font-semibold text-crescent-navy-900 dark:text-emerald-400">
          <Loader2 className="w-5 h-5 animate-spin" />
          <span>Verifying Institutional Credentials...</span>
        </div>
      </div>
    );
  }

  if (!currentUser) return <Navigate to="/login" replace />;
  
  if (allowedRoles && userData && !allowedRoles.includes(userData.role)) {
    return <Navigate to="/" replace />;
  }
  
  return <>{children}</>;
};

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/" element={<ProtectedRoute><DashboardLayout /></ProtectedRoute>}>
              <Route index element={<DashboardHome />} />
              <Route path="attendance" element={<AttendanceAnalytics />} />
              <Route path="academics" element={<Academics />} />
              <Route path="files" element={<FileAnalysis />} />
              <Route path="analytics" element={<AIAnalytics />} />
              <Route path="notifications" element={<Notifications />} />
              <Route path="profile" element={<Profile />} />
              <Route path="settings" element={<Settings />} />
              {/* Catch-all */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
