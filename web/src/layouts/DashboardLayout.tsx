import React, { useState } from 'react';
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import {
  LayoutDashboard,
  Users,
  FileText,
  BrainCircuit,
  BookOpen,
  Bell,
  User,
  Settings as SettingsIcon,
  LogOut,
  Menu,
  X,
  Sun,
  Moon,
  Monitor,
  School,
  ChevronRight,
  ShieldCheck
} from 'lucide-react';
import { cn } from '../lib/utils';
import { Badge } from '../components/Badge';

export const DashboardLayout = () => {
  const { userData, logout } = useAuth();
  const { theme, setTheme, isDark } = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const userRole = userData?.role || 'STUDENT';

  // Role-aware navigation definitions
  const allNavItems = [
    { name: 'Dashboard', path: '/', icon: LayoutDashboard, roles: ['STUDENT', 'STAFF', 'CR', 'CLASS_ADVISER', 'ADMIN'] },
    { name: 'Attendance', path: '/attendance', icon: Users, roles: ['STUDENT', 'STAFF', 'CR', 'CLASS_ADVISER', 'ADMIN'] },
    { name: 'Academics', path: '/academics', icon: BookOpen, roles: ['STUDENT', 'STAFF', 'CR', 'CLASS_ADVISER', 'ADMIN'] },
    { name: 'Materials & Files', path: '/files', icon: FileText, roles: ['STUDENT', 'STAFF', 'CR', 'CLASS_ADVISER', 'ADMIN'] },
    { name: 'AI Analytics', path: '/analytics', icon: BrainCircuit, roles: ['STUDENT', 'STAFF', 'CR', 'CLASS_ADVISER', 'ADMIN'] },
    { name: 'Announcements', path: '/notifications', icon: Bell, roles: ['STUDENT', 'STAFF', 'CR', 'CLASS_ADVISER', 'ADMIN'] },
    { name: 'Digital ID & Profile', path: '/profile', icon: User, roles: ['STUDENT', 'STAFF', 'CR', 'CLASS_ADVISER', 'ADMIN'] },
    { name: 'Settings', path: '/settings', icon: SettingsIcon, roles: ['STUDENT', 'STAFF', 'CR', 'CLASS_ADVISER', 'ADMIN'] },
  ];

  const permittedNavItems = allNavItems.filter(item => item.roles.includes(userRole));

  // Breadcrumb / Title mapping
  const getPageTitle = () => {
    switch (location.pathname) {
      case '/': return 'Academic Dashboard';
      case '/attendance': return 'Attendance Register';
      case '/academics': return 'Academics & Timetable';
      case '/files': return 'Course Materials & Extraction';
      case '/analytics': return 'AI Performance Analytics';
      case '/notifications': return 'Announcements & Bulletins';
      case '/profile': return 'Institutional Digital ID';
      case '/settings': return 'System Preferences';
      default: return 'CrescentConnect';
    }
  };

  const cycleTheme = () => {
    if (theme === 'light') setTheme('dark');
    else if (theme === 'dark') setTheme('system');
    else setTheme('light');
  };

  return (
    <div className="flex h-screen bg-slate-50 dark:bg-crescent-dark-bg text-slate-900 dark:text-slate-100 overflow-hidden font-sans transition-colors duration-200">
      {/* Mobile Drawer Backdrop */}
      {isMobileMenuOpen && (
        <div
          className="fixed inset-0 z-40 bg-slate-900/60 backdrop-blur-sm lg:hidden transition-opacity"
          onClick={() => setIsMobileMenuOpen(false)}
        />
      )}

      {/* Sidebar Navigation */}
      <aside
        className={cn(
          "fixed inset-y-0 left-0 z-50 w-72 bg-crescent-navy-900 dark:bg-crescent-dark-surface text-white flex flex-col border-r border-crescent-navy-800 dark:border-crescent-dark-border transition-transform duration-300 ease-in-out lg:static lg:translate-x-0",
          isMobileMenuOpen ? "translate-x-0 shadow-2xl" : "-translate-x-full"
        )}
      >
        {/* Brand Header */}
        <div className="p-5 border-b border-crescent-navy-800/80 dark:border-crescent-dark-border flex items-center justify-between">
          <Link to="/" className="flex items-center gap-3">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-emerald-400 to-emerald-600 text-crescent-navy-950 font-bold shadow-md">
              <School className="h-5 w-5 text-white" />
            </div>
            <div>
              <h1 className="text-base font-extrabold tracking-tight text-white leading-tight">
                CrescentConnect
              </h1>
              <p className="text-[10px] text-emerald-400 font-semibold tracking-wider uppercase">
                B.S. Abdur Rahman
              </p>
            </div>
          </Link>

          {/* Close button on mobile */}
          <button
            onClick={() => setIsMobileMenuOpen(false)}
            className="lg:hidden p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-crescent-navy-800"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* User Card in Sidebar */}
        <div className="p-4 mx-3 my-3 rounded-xl bg-crescent-navy-950/60 dark:bg-crescent-navy-950/80 border border-crescent-navy-800/80 dark:border-crescent-dark-border flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-500 to-crescent-navy-700 flex items-center justify-center text-white font-bold text-sm shrink-0 shadow-inner">
            {userData?.name?.charAt(0) || 'U'}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-xs font-bold text-white truncate">
              {userData?.name || 'Authorized Member'}
            </p>
            <div className="flex items-center gap-1.5 mt-0.5">
              <span className="inline-block w-1.5 h-1.5 rounded-full bg-emerald-400" />
              <span className="text-[10px] font-semibold text-emerald-300 uppercase tracking-wider">
                {userRole}
              </span>
            </div>
          </div>
        </div>

        {/* Navigation Items */}
        <nav className="flex-1 px-3 py-2 space-y-1 overflow-y-auto">
          {permittedNavItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                onClick={() => setIsMobileMenuOpen(false)}
                className={cn(
                  "flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs sm:text-sm font-medium transition-all duration-150 group",
                  isActive
                    ? "bg-emerald-500/20 text-emerald-300 font-semibold border border-emerald-500/30 shadow-sm"
                    : "text-slate-300 hover:bg-crescent-navy-800/70 hover:text-white dark:hover:bg-crescent-navy-900/60"
                )}
              >
                <Icon
                  className={cn(
                    "w-4 h-4 transition-colors",
                    isActive ? "text-emerald-400" : "text-slate-400 group-hover:text-slate-200"
                  )}
                />
                <span className="flex-1">{item.name}</span>
                {isActive && (
                  <ChevronRight className="w-3.5 h-3.5 text-emerald-400" />
                )}
              </Link>
            );
          })}
        </nav>

        {/* Sidebar Footer: Quick Theme & Sign Out */}
        <div className="p-4 border-t border-crescent-navy-800/80 dark:border-crescent-dark-border space-y-2">
          <div className="flex items-center justify-between text-xs text-slate-400 px-2">
            <span>Appearance:</span>
            <button
              onClick={cycleTheme}
              className="flex items-center gap-1 text-[11px] font-semibold text-emerald-400 hover:text-emerald-300 px-2 py-1 rounded-md bg-crescent-navy-800/60 hover:bg-crescent-navy-800 transition-colors"
            >
              {theme === 'light' && <Sun className="w-3.5 h-3.5" />}
              {theme === 'dark' && <Moon className="w-3.5 h-3.5" />}
              {theme === 'system' && <Monitor className="w-3.5 h-3.5" />}
              <span className="capitalize">{theme}</span>
            </button>
          </div>

          <button
            onClick={handleLogout}
            className="flex items-center gap-2.5 w-full px-3 py-2 text-xs font-semibold text-rose-400 hover:text-rose-300 hover:bg-rose-500/10 rounded-xl transition-colors cursor-pointer"
          >
            <LogOut className="w-4 h-4" />
            <span>Sign Out</span>
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Top Header */}
        <header className="h-16 bg-white dark:bg-crescent-dark-card border-b border-slate-200/80 dark:border-crescent-dark-border px-4 sm:px-8 flex items-center justify-between shadow-xs shrink-0 transition-colors duration-200">
          {/* Mobile menu trigger & Breadcrumb */}
          <div className="flex items-center gap-3">
            <button
              onClick={() => setIsMobileMenuOpen(true)}
              className="lg:hidden p-2 rounded-xl text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-crescent-navy-900 transition-colors"
              aria-label="Open menu"
            >
              <Menu className="w-5 h-5" />
            </button>

            <div>
              <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-medium">
                <span>CrescentConnect</span>
                <span>/</span>
                <span className="text-slate-600 dark:text-slate-300">{userRole}</span>
              </div>
              <h2 className="text-sm sm:text-base font-bold text-slate-900 dark:text-white leading-tight">
                {getPageTitle()}
              </h2>
            </div>
          </div>

          {/* Right Header Actions */}
          <div className="flex items-center gap-3">
            {/* Institution Badge */}
            <div className="hidden sm:flex items-center gap-1.5 px-3 py-1 rounded-full bg-slate-100 dark:bg-crescent-navy-900/60 border border-slate-200 dark:border-crescent-navy-800 text-[11px] font-semibold text-slate-700 dark:text-slate-300">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
              <span>Crescent Institute • Deemed University</span>
            </div>

            {/* Quick Theme Toggle Button */}
            <button
              onClick={cycleTheme}
              className="p-2 rounded-xl text-slate-500 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-crescent-navy-900 border border-slate-200 dark:border-crescent-navy-800 transition-colors"
              title={`Theme: ${theme} (Click to toggle)`}
            >
              {theme === 'light' && <Sun className="w-4 h-4 text-amber-500" />}
              {theme === 'dark' && <Moon className="w-4 h-4 text-blue-400" />}
              {theme === 'system' && <Monitor className="w-4 h-4 text-slate-500 dark:text-slate-300" />}
            </button>

            {/* User Profile Pill */}
            <Link
              to="/profile"
              className="flex items-center gap-2 p-1.5 sm:px-3 sm:py-1.5 rounded-xl border border-slate-200 dark:border-crescent-navy-800 hover:bg-slate-50 dark:hover:bg-crescent-navy-900/50 transition-colors"
            >
              <div className="w-7 h-7 rounded-lg bg-crescent-navy-900 text-white dark:bg-emerald-500 flex items-center justify-center text-xs font-bold shrink-0">
                {userData?.name?.charAt(0) || 'U'}
              </div>
              <div className="hidden md:block text-left">
                <p className="text-xs font-bold text-slate-900 dark:text-white leading-tight">
                  {userData?.name || 'Member'}
                </p>
                <p className="text-[10px] text-slate-400 leading-tight">
                  {userRole}
                </p>
              </div>
            </Link>
          </div>
        </header>

        {/* Main Body Content */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 bg-slate-50/70 dark:bg-crescent-dark-bg/80">
          <Outlet />
        </div>
      </main>
    </div>
  );
};
