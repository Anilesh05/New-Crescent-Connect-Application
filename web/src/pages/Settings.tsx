import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTheme, ThemeMode } from '../context/ThemeContext';
import {
  Sun,
  Moon,
  Monitor,
  Shield,
  Database,
  Cloud,
  BrainCircuit,
  LogOut,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';
import { PageHeader } from '../components/PageHeader';
import { Badge } from '../components/Badge';
import { Modal } from '../components/Modal';

export const Settings = () => {
  const { userData, logout } = useAuth();
  const { theme, setTheme } = useTheme();
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);

  const themeOptions: { mode: ThemeMode; label: string; icon: any; description: string }[] = [
    {
      mode: 'light',
      label: 'Light Theme',
      icon: Sun,
      description: 'Clean off-white canvas with navy contrast accents'
    },
    {
      mode: 'dark',
      label: 'Dark Theme',
      icon: Moon,
      description: 'Deep navy background for low-light eye comfort'
    },
    {
      mode: 'system',
      label: 'System Default',
      icon: Monitor,
      description: 'Synchronize dynamically with your device appearance'
    }
  ];

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      <PageHeader
        title="Preferences & System Diagnostics"
        subtitle="Manage display theme, review cloud infrastructure connectivity, and account security."
        badge={<Badge variant="navy">System Config</Badge>}
      />

      {/* Theme Selection */}
      <div className="bg-white dark:bg-crescent-dark-card rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border p-5 sm:p-6 shadow-sm space-y-4">
        <div>
          <h3 className="text-base font-bold text-slate-900 dark:text-white">
            Appearance & Color Theme
          </h3>
          <p className="text-xs text-slate-500 dark:text-slate-400">
            Customize your visual experience across CrescentConnect web interfaces.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
          {themeOptions.map((opt) => {
            const Icon = opt.icon;
            const isSelected = theme === opt.mode;
            return (
              <button
                key={opt.mode}
                onClick={() => setTheme(opt.mode)}
                className={`p-4 rounded-xl border text-left transition-all flex flex-col justify-between gap-3 ${
                  isSelected
                    ? 'border-crescent-navy-900 dark:border-emerald-500 bg-crescent-navy-50/60 dark:bg-crescent-navy-950/60 shadow-sm ring-1 ring-crescent-navy-900 dark:ring-emerald-500'
                    : 'border-slate-200/80 dark:border-crescent-dark-border hover:border-slate-300 dark:hover:border-crescent-navy-700 bg-white dark:bg-crescent-dark-card'
                }`}
              >
                <div className="flex items-center justify-between w-full">
                  <div className={`p-2 rounded-lg ${isSelected ? 'bg-crescent-navy-900 text-white dark:bg-emerald-500' : 'bg-slate-100 text-slate-600 dark:bg-crescent-navy-900 dark:text-slate-300'}`}>
                    <Icon className="w-5 h-5" />
                  </div>
                  {isSelected && (
                    <Badge variant="emerald" size="sm">Active</Badge>
                  )}
                </div>

                <div>
                  <h4 className="text-sm font-bold text-slate-900 dark:text-white">
                    {opt.label}
                  </h4>
                  <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5 leading-normal">
                    {opt.description}
                  </p>
                </div>
              </button>
            );
          })}
        </div>
      </div>

      {/* Cloud & Architecture Diagnostics */}
      <div className="bg-white dark:bg-crescent-dark-card rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border p-5 sm:p-6 shadow-sm space-y-4">
        <div>
          <h3 className="text-base font-bold text-slate-900 dark:text-white">
            Architecture & Service Status
          </h3>
          <p className="text-xs text-slate-500 dark:text-slate-400">
            Live operational status of CrescentConnect integrated backend services.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-emerald-50 text-emerald-600 dark:bg-emerald-950/50 dark:text-emerald-400">
                <Shield className="w-4 h-4" />
              </div>
              <div>
                <p className="font-bold text-slate-900 dark:text-white">Firebase Authentication</p>
                <p className="text-[11px] text-slate-500 dark:text-slate-400">Institutional SSO / Email Tokens</p>
              </div>
            </div>
            <Badge variant="emerald">Operational</Badge>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-blue-50 text-blue-600 dark:bg-blue-950/50 dark:text-blue-400">
                <Database className="w-4 h-4" />
              </div>
              <div>
                <p className="font-bold text-slate-900 dark:text-white">Cloud Firestore DB</p>
                <p className="text-[11px] text-slate-500 dark:text-slate-400">Real-time attendance & announcements</p>
              </div>
            </div>
            <Badge variant="emerald">Operational</Badge>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-purple-50 text-purple-600 dark:bg-purple-950/50 dark:text-purple-400">
                <Cloud className="w-4 h-4" />
              </div>
              <div>
                <p className="font-bold text-slate-900 dark:text-white">Supabase Storage</p>
                <p className="text-[11px] text-slate-500 dark:text-slate-400">Bucket: crescentconnect-files</p>
              </div>
            </div>
            <Badge variant="amber">Console Action Req.</Badge>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-amber-50 text-amber-600 dark:bg-amber-950/50 dark:text-amber-400">
                <BrainCircuit className="w-4 h-4" />
              </div>
              <div>
                <p className="font-bold text-slate-900 dark:text-white">Gemini Academic AI</p>
                <p className="text-[11px] text-slate-500 dark:text-slate-400">Document reasoning & summaries</p>
              </div>
            </div>
            <Badge variant="emerald">Ready</Badge>
          </div>
        </div>
      </div>

      {/* Account & Session Termination */}
      <div className="bg-white dark:bg-crescent-dark-card rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border p-5 sm:p-6 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 className="text-base font-bold text-slate-900 dark:text-white">
            Active Institutional Session
          </h3>
          <p className="text-xs text-slate-500 dark:text-slate-400">
            Signed in as <strong>{userData?.email}</strong> ({userData?.role})
          </p>
        </div>

        <button
          onClick={() => setIsLogoutModalOpen(true)}
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-rose-50 hover:bg-rose-100 text-rose-700 dark:bg-rose-950/40 dark:hover:bg-rose-950/60 dark:text-rose-300 border border-rose-200 dark:border-rose-800 rounded-xl text-xs sm:text-sm font-semibold transition-colors"
        >
          <LogOut className="w-4 h-4" />
          <span>Sign Out of Dashboard</span>
        </button>
      </div>

      {/* Logout Confirmation Modal */}
      <Modal
        isOpen={isLogoutModalOpen}
        onClose={() => setIsLogoutModalOpen(false)}
        title="Confirm Sign Out"
        description="Are you sure you want to end your current CrescentConnect session?"
      >
        <div className="space-y-4 pt-2">
          <p className="text-xs text-slate-500 dark:text-slate-400">
            You will be signed out from this browser. You can sign back in at any time with your institutional credentials.
          </p>
          <div className="flex items-center justify-end gap-2.5">
            <button
              onClick={() => setIsLogoutModalOpen(false)}
              className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-crescent-navy-900 rounded-xl"
            >
              Cancel
            </button>
            <button
              onClick={logout}
              className="px-4 py-2 text-xs font-semibold text-white bg-rose-600 hover:bg-rose-700 rounded-xl shadow-sm transition-all"
            >
              Sign Out
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
