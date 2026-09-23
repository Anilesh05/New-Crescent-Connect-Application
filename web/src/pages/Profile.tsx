import React from 'react';
import { useAuth } from '../context/AuthContext';
import { User, School, ShieldCheck, Mail, Calendar, BookOpen, Award, CheckCircle2, QrCode } from 'lucide-react';
import { PageHeader } from '../components/PageHeader';
import { Badge } from '../components/Badge';

export const Profile = () => {
  const { userData } = useAuth();
  const userRole = userData?.role || 'STUDENT';

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      <PageHeader
        title="Institutional Profile & Digital ID"
        subtitle="Verified Crescent Institute academic credentials and digital identity access pass."
        badge={
          <Badge variant="emerald">
            <ShieldCheck className="w-3 h-3" />
            <span>Verified Member</span>
          </Badge>
        }
      />

      {/* Digital Identity Card (Physical Smart Card Representation) */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-crescent-navy-950 via-crescent-navy-900 to-[#163a63] p-6 sm:p-8 text-white shadow-xl border border-crescent-navy-700/60">
        {/* Subtle Watermark Crest */}
        <div className="absolute right-0 bottom-0 translate-x-12 translate-y-12 opacity-5 pointer-events-none">
          <School className="w-80 h-80 text-white" />
        </div>

        {/* Card Header */}
        <div className="flex items-center justify-between pb-6 border-b border-white/10">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-white text-crescent-navy-900 shadow-md">
              <School className="w-6 h-6 text-emerald-600" />
            </div>
            <div>
              <h2 className="text-sm sm:text-base font-bold tracking-tight text-white">
                B.S. ABDUR RAHMAN CRESCENT INSTITUTE
              </h2>
              <p className="text-[11px] text-emerald-400 font-semibold tracking-wider uppercase">
                Science and Technology • Deemed to be University
              </p>
            </div>
          </div>

          <Badge variant="emerald" size="md" className="bg-emerald-500/20 text-emerald-300 border-emerald-400/30">
            {userRole}
          </Badge>
        </div>

        {/* Card Body */}
        <div className="pt-6 flex flex-col sm:flex-row items-start gap-6">
          {/* Avatar / Photo */}
          <div className="relative shrink-0 mx-auto sm:mx-0">
            <div className="w-28 h-28 sm:w-32 sm:h-32 rounded-2xl bg-gradient-to-tr from-slate-200 to-white p-1 shadow-inner">
              <div className="w-full h-full rounded-xl bg-crescent-navy-800 flex items-center justify-center text-white overflow-hidden">
                {userData?.profileImageUri ? (
                  <img
                    src={userData.profileImageUri}
                    alt={userData.name}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <User className="w-16 h-16 text-slate-300" />
                )}
              </div>
            </div>
            <div className="absolute -bottom-2 -right-2 p-1.5 rounded-full bg-emerald-500 text-white shadow-md">
              <CheckCircle2 className="w-4 h-4" />
            </div>
          </div>

          {/* Details */}
          <div className="flex-1 space-y-3 text-center sm:text-left">
            <div>
              <span className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                Full Name
              </span>
              <h3 className="text-xl sm:text-2xl font-extrabold tracking-tight text-white">
                {userData?.name || 'Anilesh Moses'}
              </h3>
            </div>

            <div className="grid grid-cols-2 gap-3 text-xs">
              <div>
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">
                  {userRole === 'STAFF' ? 'Faculty ID' : 'Register Number'}
                </span>
                <span className="font-mono font-bold text-emerald-300 text-sm">
                  {userData?.registerNumber || (userRole === 'STAFF' ? 'FAC-MCA-042' : '220071601004')}
                </span>
              </div>

              <div>
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">
                  Department
                </span>
                <span className="font-semibold text-slate-200">
                  {userData?.department || 'Computer Applications'}
                </span>
              </div>

              <div>
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">
                  Programme / Section
                </span>
                <span className="font-semibold text-slate-200">
                  {userData?.programme || 'MCA'} • Section {userData?.section || 'A'}
                </span>
              </div>

              <div>
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">
                  Academic Year
                </span>
                <span className="font-semibold text-slate-200">
                  {userData?.academicYear || '2023-2024'}
                </span>
              </div>
            </div>
          </div>

          {/* Barcode / QR Section */}
          <div className="hidden md:flex flex-col items-center justify-center p-3 rounded-2xl bg-white/5 border border-white/10 shrink-0">
            <QrCode className="w-16 h-16 text-slate-200" />
            <span className="text-[9px] font-mono tracking-widest text-slate-400 mt-1">
              DIGITAL VERIFIED
            </span>
          </div>
        </div>
      </div>

      {/* Profile Details List */}
      <div className="bg-white dark:bg-crescent-dark-card rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border p-6 shadow-sm space-y-4">
        <h3 className="text-base font-bold text-slate-900 dark:text-white pb-3 border-b border-slate-100 dark:border-crescent-dark-border">
          Academic Registration Record
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs sm:text-sm">
          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60">
            <span className="text-xs text-slate-400 block font-medium">Institutional Email</span>
            <span className="font-semibold text-slate-900 dark:text-white mt-0.5 block">
              {userData?.email || 'anilesh@student.crescent.edu'}
            </span>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60">
            <span className="text-xs text-slate-400 block font-medium">Semester</span>
            <span className="font-semibold text-slate-900 dark:text-white mt-0.5 block">
              {userData?.semester || 'Semester IV'}
            </span>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60">
            <span className="text-xs text-slate-400 block font-medium">Campus Location</span>
            <span className="font-semibold text-slate-900 dark:text-white mt-0.5 block">
              Vandalur Campus, Chennai - 600048
            </span>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60">
            <span className="text-xs text-slate-400 block font-medium">Account Status</span>
            <span className="font-semibold text-emerald-600 dark:text-emerald-400 mt-0.5 block flex items-center gap-1">
              Active & In Good Academic Standing ✓
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
