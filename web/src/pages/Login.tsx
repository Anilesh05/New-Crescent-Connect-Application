import React, { useState } from 'react';
import { signInWithEmailAndPassword } from 'firebase/auth';
import { useNavigate } from 'react-router-dom';
import { auth } from '../lib/firebase';
import { Loader2, Lock, Mail, Eye, EyeOff, ShieldCheck, GraduationCap, School } from 'lucide-react';
import { useTheme } from '../context/ThemeContext';

export const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { isDark } = useTheme();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await signInWithEmailAndPassword(auth, email.trim(), password);
      navigate('/');
    } catch (err: any) {
      console.error("Login error:", err);
      if (err.code === 'auth/invalid-credential' || err.code === 'auth/user-not-found' || err.code === 'auth/wrong-password') {
        setError('Invalid email or password. Please verify your institutional credentials.');
      } else if (err.code === 'auth/too-many-requests') {
        setError('Access temporarily disabled due to many failed attempts. Try again in a few minutes.');
      } else {
        setError(err.message || 'Failed to sign in. Please check your network connection.');
      }
    } finally {
      setLoading(false);
    }
  };

  const setDemoCredentials = (roleEmail: string) => {
    setEmail(roleEmail);
    setPassword('password');
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-crescent-dark-bg flex flex-col justify-center py-12 sm:px-6 lg:px-8 transition-colors duration-200">
      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        {/* Crescent Institute Crest / Logo */}
        <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-crescent-navy-900 text-white shadow-lg shadow-crescent-navy-900/20 mb-4 border border-crescent-navy-800">
          <School className="w-8 h-8 text-emerald-400" />
        </div>
        
        <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-crescent-navy-900 dark:text-white">
          CrescentConnect
        </h1>
        <p className="mt-1 text-xs sm:text-sm font-medium text-emerald-600 dark:text-emerald-400 uppercase tracking-wider">
          B.S. Abdur Rahman Crescent Institute of Science and Technology
        </p>
        <p className="mt-2 text-xs sm:text-sm text-slate-500 dark:text-slate-400">
          Smart Academic, Digital Identity & File Analysis Platform
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md px-4 sm:px-0">
        <div className="bg-white dark:bg-crescent-dark-card py-8 px-6 shadow-xl shadow-slate-200/50 dark:shadow-none sm:rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border">
          <form className="space-y-5" onSubmit={handleLogin}>
            {error && (
              <div className="p-3.5 rounded-xl bg-rose-50 border border-rose-200/80 text-rose-700 dark:bg-rose-950/40 dark:border-rose-800/60 dark:text-rose-300 text-xs sm:text-sm flex items-start gap-2.5">
                <ShieldCheck className="w-4 h-4 shrink-0 mt-0.5 text-rose-500" />
                <span>{error}</span>
              </div>
            )}

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-600 dark:text-slate-300 mb-1.5">
                Institutional Email
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <Mail className="h-4 w-4" />
                </div>
                <input
                  type="email"
                  required
                  placeholder="e.g. anilesh@student.crescent.edu"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="block w-full pl-10 pr-3.5 py-2.5 rounded-xl border border-slate-300 dark:border-crescent-navy-700 bg-white dark:bg-crescent-navy-950/40 text-slate-900 dark:text-white placeholder-slate-400 text-sm focus:outline-none focus:ring-2 focus:ring-crescent-navy-900 dark:focus:ring-emerald-500 transition-all"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-600 dark:text-slate-300 mb-1.5">
                Password
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <Lock className="h-4 w-4" />
                </div>
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="block w-full pl-10 pr-10 py-2.5 rounded-xl border border-slate-300 dark:border-crescent-navy-700 bg-white dark:bg-crescent-navy-950/40 text-slate-900 dark:text-white placeholder-slate-400 text-sm focus:outline-none focus:ring-2 focus:ring-crescent-navy-900 dark:focus:ring-emerald-500 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
                >
                  {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full flex items-center justify-center gap-2 py-3 px-4 rounded-xl shadow-md text-sm font-semibold text-white bg-crescent-navy-900 hover:bg-crescent-navy-800 dark:bg-emerald-600 dark:hover:bg-emerald-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-crescent-navy-900 transition-all disabled:opacity-50 cursor-pointer"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Authenticating...</span>
                </>
              ) : (
                <>
                  <span>Sign In to Dashboard</span>
                </>
              )}
            </button>
          </form>

          {/* Quick Demo Credentials Panel for evaluation */}
          <div className="mt-6 pt-5 border-t border-slate-200/80 dark:border-crescent-dark-border">
            <p className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2.5 flex items-center gap-1.5">
              <GraduationCap className="w-3.5 h-3.5 text-emerald-500" />
              Demo Roles (Quick Fill)
            </p>
            <div className="grid grid-cols-2 gap-2 text-xs">
              <button
                type="button"
                onClick={() => setDemoCredentials('anilesh@student.crescent.edu')}
                className="p-2 rounded-lg border border-slate-200 dark:border-crescent-navy-700 hover:bg-slate-50 dark:hover:bg-crescent-navy-900/50 text-left transition-colors"
              >
                <div className="font-semibold text-slate-800 dark:text-slate-200">Student</div>
                <div className="text-[10px] text-slate-400 truncate">anilesh@student...</div>
              </button>
              <button
                type="button"
                onClick={() => setDemoCredentials('rahmath@staff.crescent.edu')}
                className="p-2 rounded-lg border border-slate-200 dark:border-crescent-navy-700 hover:bg-slate-50 dark:hover:bg-crescent-navy-900/50 text-left transition-colors"
              >
                <div className="font-semibold text-slate-800 dark:text-slate-200">Faculty / Staff</div>
                <div className="text-[10px] text-slate-400 truncate">rahmath@staff...</div>
              </button>
              <button
                type="button"
                onClick={() => setDemoCredentials('rahul@student.crescent.edu')}
                className="p-2 rounded-lg border border-slate-200 dark:border-crescent-navy-700 hover:bg-slate-50 dark:hover:bg-crescent-navy-900/50 text-left transition-colors"
              >
                <div className="font-semibold text-slate-800 dark:text-slate-200">Class Rep (CR)</div>
                <div className="text-[10px] text-slate-400 truncate">rahul@student...</div>
              </button>
              <button
                type="button"
                onClick={() => setDemoCredentials('admin@crescent.edu')}
                className="p-2 rounded-lg border border-slate-200 dark:border-crescent-navy-700 hover:bg-slate-50 dark:hover:bg-crescent-navy-900/50 text-left transition-colors"
              >
                <div className="font-semibold text-slate-800 dark:text-slate-200">Administrator</div>
                <div className="text-[10px] text-slate-400 truncate">admin@crescent...</div>
              </button>
            </div>
          </div>
        </div>

        <p className="mt-6 text-center text-xs text-slate-400 dark:text-slate-500">
          Protected by Crescent Institute Security & Firebase Authentication
        </p>
      </div>
    </div>
  );
};
