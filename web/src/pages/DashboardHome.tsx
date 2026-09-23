import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { collection, query, where, getDocs, orderBy, limit } from 'firebase/firestore';
import { db } from '../lib/firebase';
import {
  Users,
  BookOpen,
  Clock,
  AlertTriangle,
  Calendar,
  FileText,
  Bell,
  Sparkles,
  CheckCircle2,
  ArrowUpRight,
  GraduationCap,
  ShieldCheck,
  TrendingUp,
  ChevronRight
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import { Link } from 'react-router-dom';
import { StatCard } from '../components/StatCard';
import { Badge } from '../components/Badge';
import { PageHeader } from '../components/PageHeader';
import { DashboardSkeleton } from '../components/LoadingSkeleton';

export const DashboardHome = () => {
  const { userData } = useAuth();
  const [loading, setLoading] = useState(true);

  // Stats state
  const [stats, setStats] = useState({
    totalCourses: 0,
    attendanceAvg: 0,
    classesAttended: 0,
    totalClasses: 0,
    atRiskStudents: 0,
    materialsCount: 0,
    activeSessions: 0,
  });

  // Recent data
  const [recentMaterials, setRecentMaterials] = useState<any[]>([]);
  const [announcements, setAnnouncements] = useState<any[]>([]);
  const [courses, setCourses] = useState<any[]>([]);

  // Chart data
  const [attendanceTrend, setAttendanceTrend] = useState<any[]>([]);

  const userRole = userData?.role || 'STUDENT';

  useEffect(() => {
    const fetchDashboardData = async () => {
      if (!userData) return;
      setLoading(true);

      try {
        // 1. Fetch study materials
        try {
          const matRef = collection(db, 'study_materials');
          const matQuery = userRole === 'STAFF'
            ? query(matRef, where('staffId', '==', userData.id), limit(5))
            : query(matRef, limit(5));
          const matSnap = await getDocs(matQuery);
          const mats = matSnap.docs.map(doc => ({ id: doc.id, ...doc.data() }));
          setRecentMaterials(mats);
        } catch (e) {
          console.warn("Could not load study materials:", e);
        }

        // 2. Fetch announcements
        try {
          const annRef = collection(db, 'announcements');
          const annSnap = await getDocs(query(annRef, limit(4)));
          const anns = annSnap.docs.map(doc => ({ id: doc.id, ...doc.data() }));
          if (anns.length > 0) {
            setAnnouncements(anns);
          } else {
            // Default institutional notices if Firestore collection is fresh
            setAnnouncements([
              {
                id: '1',
                title: 'Semester IV Continuous Assessment - Model Lab Exam',
                category: 'Academic',
                date: 'Tomorrow, 10:00 AM',
                author: 'Dean of Academic Affairs'
              },
              {
                id: '2',
                title: 'Mandatory 75% Attendance Requirement Reminder',
                category: 'Attendance',
                date: '2 days ago',
                author: 'Office of Student Affairs'
              },
              {
                id: '3',
                title: 'Digital ID & Library Access Card Synchronization',
                category: 'Notice',
                date: '3 days ago',
                author: 'CrescentConnect Admin'
              }
            ]);
          }
        } catch (e) {
          console.warn("Could not load announcements:", e);
        }

        // 3. Populate role-specific metrics & charts
        if (userRole === 'STUDENT' || userRole === 'CR') {
          setStats({
            totalCourses: 5,
            attendanceAvg: 88.4,
            classesAttended: 46,
            totalClasses: 52,
            atRiskStudents: 0,
            materialsCount: 14,
            activeSessions: 2,
          });

          setAttendanceTrend([
            { name: 'DBMS', attended: 18, total: 20, pct: 90 },
            { name: 'Web Dev', attended: 14, total: 16, pct: 87.5 },
            { name: 'OS', attended: 15, total: 18, pct: 83.3 },
            { name: 'Networks', attended: 12, total: 12, pct: 100 },
            { name: 'Machine Learning', attended: 11, total: 14, pct: 78.5 },
          ]);

          setCourses([
            { code: 'MCA401', name: 'Database Management Systems', faculty: 'Dr. Rahmath S', time: 'Today, 10:00 AM - Room 301' },
            { code: 'MCA402', name: 'Web Development & Frameworks', faculty: 'Prof. Anitha K', time: 'Today, 11:30 AM - Lab 2' },
            { code: 'MCA403', name: 'Operating Systems & Cloud', faculty: 'Dr. John Doe', time: 'Tomorrow, 09:00 AM - Room 204' },
          ]);
        } else if (userRole === 'STAFF') {
          setStats({
            totalCourses: 3,
            attendanceAvg: 84.2,
            classesAttended: 38,
            totalClasses: 45,
            atRiskStudents: 4,
            materialsCount: 8,
            activeSessions: 1,
          });

          setAttendanceTrend([
            { name: 'MCA Sec A', attended: 42, total: 50, pct: 84 },
            { name: 'MCA Sec B', attended: 45, total: 50, pct: 90 },
            { name: 'BCA Sec A', attended: 39, total: 50, pct: 78 },
          ]);

          setCourses([
            { code: 'MCA401', name: 'Database Management Systems', section: 'Sec A (IV Sem)', studentsCount: 45, nextClass: 'Today, 10:00 AM' },
            { code: 'MCA402', name: 'Web Development & Cloud', section: 'Sec A (IV Sem)', studentsCount: 45, nextClass: 'Tomorrow, 11:30 AM' },
          ]);
        } else if (userRole === 'CLASS_ADVISER') {
          setStats({
            totalCourses: 6,
            attendanceAvg: 81.5,
            classesAttended: 120,
            totalClasses: 147,
            atRiskStudents: 6,
            materialsCount: 22,
            activeSessions: 3,
          });

          setAttendanceTrend([
            { name: 'Week 1', present: 92, absent: 8 },
            { name: 'Week 2', present: 88, absent: 12 },
            { name: 'Week 3', present: 85, absent: 15 },
            { name: 'Week 4', present: 81, absent: 19 },
          ]);
        } else {
          // ADMIN
          setStats({
            totalCourses: 28,
            attendanceAvg: 86.8,
            classesAttended: 840,
            totalClasses: 968,
            atRiskStudents: 18,
            materialsCount: 112,
            activeSessions: 8,
          });

          setAttendanceTrend([
            { name: 'MCA', attended: 88, total: 100, pct: 88 },
            { name: 'B.Tech CSE', attended: 85, total: 100, pct: 85 },
            { name: 'B.Tech IT', attended: 87, total: 100, pct: 87 },
            { name: 'MBA', attended: 91, total: 100, pct: 91 },
          ]);
        }
      } catch (error) {
        console.error("Dashboard fetch error:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [userData, userRole]);

  if (loading) {
    return <DashboardSkeleton />;
  }

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-crescent-navy-900 via-crescent-navy-800 to-[#123157] p-6 sm:p-8 text-white shadow-md border border-crescent-navy-700/50">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div className="space-y-1.5">
            <div className="flex items-center gap-2">
              <Badge variant="emerald" className="bg-emerald-500/20 text-emerald-300 border-emerald-400/30">
                {userRole === 'STUDENT' ? 'Student Portal' :
                 userRole === 'STAFF' ? 'Faculty Portal' :
                 userRole === 'CR' ? 'Class Representative Portal' :
                 userRole === 'CLASS_ADVISER' ? 'Class Adviser Portal' : 'Admin Control Portal'}
              </Badge>
              <span className="text-xs text-slate-300">
                Semester IV • 2023-2024
              </span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-white">
              Welcome back, {userData?.name || 'Scholar'}
            </h1>
            <p className="text-xs sm:text-sm text-slate-300 max-w-2xl">
              {userRole === 'STUDENT' || userRole === 'CR'
                ? 'Your overall academic attendance is currently healthy at ' + stats.attendanceAvg + '%. Review your schedule and latest AI-analyzed course materials below.'
                : userRole === 'STAFF'
                ? 'You have ' + stats.totalCourses + ' assigned courses. ' + stats.atRiskStudents + ' students currently require attendance advisement.'
                : 'Real-time academic monitoring and digital identity operations for Crescent Institute.'}
            </p>
          </div>

          <div className="flex items-center gap-2 shrink-0">
            <Link
              to="/attendance"
              className="inline-flex items-center gap-1.5 rounded-xl bg-emerald-500 hover:bg-emerald-600 px-4 py-2.5 text-xs sm:text-sm font-semibold text-white shadow-sm transition-all"
            >
              <Users className="w-4 h-4" />
              <span>Attendance Register</span>
            </Link>
            <Link
              to="/files"
              className="inline-flex items-center gap-1.5 rounded-xl bg-white/10 hover:bg-white/20 backdrop-blur-sm px-4 py-2.5 text-xs sm:text-sm font-semibold text-white border border-white/20 transition-all"
            >
              <FileText className="w-4 h-4" />
              <span>Course Materials</span>
            </Link>
          </div>
        </div>
      </div>

      {/* Role-Specific Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {userRole === 'STUDENT' || userRole === 'CR' ? (
          <>
            <StatCard
              title="Overall Attendance"
              value={`${stats.attendanceAvg}%`}
              subtitle={`${stats.classesAttended} of ${stats.totalClasses} classes attended`}
              icon={Users}
              variant="emerald"
              trend={{ value: '1.2%', isPositive: true }}
            />
            <StatCard
              title="Enrolled Courses"
              value={stats.totalCourses}
              subtitle="Current Semester (MCA IV)"
              icon={BookOpen}
              variant="navy"
            />
            <StatCard
              title="Today's Sessions"
              value={stats.activeSessions}
              subtitle="DBMS (10 AM), Web Dev (11:30 AM)"
              icon={Clock}
              variant="blue"
            />
            <StatCard
              title="Study Materials"
              value={stats.materialsCount}
              subtitle="Documents & AI Summaries"
              icon={FileText}
              variant="purple"
            />
          </>
        ) : userRole === 'STAFF' ? (
          <>
            <StatCard
              title="Assigned Courses"
              value={stats.totalCourses}
              subtitle="MCA IV Sem • Sec A & B"
              icon={BookOpen}
              variant="navy"
            />
            <StatCard
              title="Today's Sessions"
              value={stats.activeSessions}
              subtitle="Ready to take attendance"
              icon={Clock}
              variant="emerald"
            />
            <StatCard
              title="Batch Avg Attendance"
              value={`${stats.attendanceAvg}%`}
              subtitle="Class aggregate"
              icon={Users}
              variant="blue"
            />
            <StatCard
              title="At-Risk Students"
              value={stats.atRiskStudents}
              subtitle="Attendance < 75% threshold"
              icon={AlertTriangle}
              variant="rose"
            />
          </>
        ) : (
          <>
            <StatCard
              title="Total Courses"
              value={stats.totalCourses}
              subtitle="Active academic catalog"
              icon={BookOpen}
              variant="navy"
            />
            <StatCard
              title="Institute Avg Attendance"
              value={`${stats.attendanceAvg}%`}
              subtitle="Cross-department metric"
              icon={Users}
              variant="emerald"
            />
            <StatCard
              title="At-Risk Alerts"
              value={stats.atRiskStudents}
              subtitle="Students requiring advisement"
              icon={AlertTriangle}
              variant="rose"
            />
            <StatCard
              title="Active Sessions"
              value={stats.activeSessions}
              subtitle="Synchronized across campuses"
              icon={Clock}
              variant="purple"
            />
          </>
        )}
      </div>

      {/* Main Analytics Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Attendance Breakdown Chart */}
        <div className="lg:col-span-2 rounded-xl border border-slate-200/80 bg-white p-5 sm:p-6 shadow-sm dark:border-crescent-dark-border dark:bg-crescent-dark-card">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-100 dark:border-crescent-dark-border gap-2">
            <div>
              <h2 className="text-base font-bold text-slate-900 dark:text-white">
                {userRole === 'STUDENT' || userRole === 'CR'
                  ? 'Course-wise Attendance Breakdown'
                  : 'Batch Attendance Overview'}
              </h2>
              <p className="text-xs text-slate-500 dark:text-slate-400">
                Minimum 75% requirement for semester exam hall ticket eligibility
              </p>
            </div>
            <div className="flex items-center gap-3">
              <div className="flex items-center gap-1.5 text-xs text-slate-500 dark:text-slate-400">
                <span className="h-2.5 w-2.5 rounded-full bg-emerald-500" />
                <span>Safe (≥75%)</span>
              </div>
              <div className="flex items-center gap-1.5 text-xs text-slate-500 dark:text-slate-400">
                <span className="h-2.5 w-2.5 rounded-full bg-rose-500" />
                <span>Critical (&lt;75%)</span>
              </div>
            </div>
          </div>

          <div className="h-72 mt-4">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={attendanceTrend} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" className="dark:stroke-slate-800" />
                <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: '#94A3B8', fontSize: 12 }} />
                <YAxis domain={[0, 100]} axisLine={false} tickLine={false} tick={{ fill: '#94A3B8', fontSize: 12 }} />
                <Tooltip
                  cursor={{ fill: '#F1F5F9', opacity: 0.5 }}
                  contentStyle={{
                    backgroundColor: '#0B1E36',
                    borderColor: '#1E3A5F',
                    borderRadius: '8px',
                    color: '#fff',
                    fontSize: '12px'
                  }}
                />
                <Bar
                  dataKey="pct"
                  name="Attendance %"
                  fill="#10B981"
                  radius={[6, 6, 0, 0]}
                />
              </BarChart>
            </ResponsiveContainer>
          </div>

          <div className="mt-4 pt-4 border-t border-slate-100 dark:border-crescent-dark-border flex items-center justify-between text-xs text-slate-500 dark:text-slate-400">
            <span>Official University Policy: 75% attendance threshold strictly enforced.</span>
            <Link to="/attendance" className="font-semibold text-crescent-navy-900 dark:text-emerald-400 hover:underline flex items-center gap-1">
              View Detailed Register <ChevronRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>

        {/* Schedule / Quick Actions Card */}
        <div className="rounded-xl border border-slate-200/80 bg-white p-5 sm:p-6 shadow-sm dark:border-crescent-dark-border dark:bg-crescent-dark-card flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-crescent-dark-border">
              <h2 className="text-base font-bold text-slate-900 dark:text-white flex items-center gap-2">
                <Calendar className="w-4 h-4 text-crescent-navy-800 dark:text-blue-400" />
                {userRole === 'STAFF' ? "Today's Teaching Schedule" : "Today's Classes"}
              </h2>
              <Badge variant="navy">Day Order 2</Badge>
            </div>

            <div className="space-y-3 mt-4">
              {courses.length > 0 ? (
                courses.map((c, i) => (
                  <div
                    key={i}
                    className="p-3 rounded-xl border border-slate-100 dark:border-crescent-navy-800/80 bg-slate-50/70 dark:bg-crescent-navy-950/40 hover:border-slate-300 dark:hover:border-crescent-navy-700 transition-all"
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-bold text-emerald-600 dark:text-emerald-400">{c.code}</span>
                      <span className="text-[11px] font-medium text-slate-400">{c.time || c.nextClass}</span>
                    </div>
                    <p className="text-xs sm:text-sm font-semibold text-slate-900 dark:text-white mt-1">
                      {c.name}
                    </p>
                    <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">
                      {c.faculty || c.section}
                    </p>
                  </div>
                ))
              ) : (
                <div className="text-center py-6 text-xs text-slate-400">
                  No scheduled classes for today.
                </div>
              )}
            </div>
          </div>

          {/* AI Quick Insight Box */}
          <div className="mt-4 p-3.5 rounded-xl bg-gradient-to-br from-purple-50 to-blue-50 dark:from-purple-950/30 dark:to-blue-950/30 border border-purple-200/60 dark:border-purple-800/40">
            <div className="flex items-center gap-2 text-xs font-bold text-purple-700 dark:text-purple-300">
              <Sparkles className="w-3.5 h-3.5" />
              <span>AI Advisor Note</span>
            </div>
            <p className="text-xs text-slate-600 dark:text-slate-300 mt-1">
              {userRole === 'STUDENT' || userRole === 'CR'
                ? "You have an exam review scheduled in Machine Learning. Upload your syllabus in File Analysis for instant test question generation."
                : "Continuous assessment attendance sync is complete. 2 pending sessions require verification."}
            </p>
            <Link
              to="/analytics"
              className="inline-flex items-center gap-1 text-[11px] font-semibold text-purple-700 dark:text-purple-400 hover:underline mt-2"
            >
              Open AI Analytics <ArrowUpRight className="w-3 h-3" />
            </Link>
          </div>
        </div>
      </div>

      {/* Second Row: Announcements & Recent Study Materials */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Official Announcements */}
        <div className="rounded-xl border border-slate-200/80 bg-white p-5 sm:p-6 shadow-sm dark:border-crescent-dark-border dark:bg-crescent-dark-card">
          <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-crescent-dark-border">
            <div className="flex items-center gap-2">
              <div className="p-1.5 rounded-lg bg-amber-50 text-amber-600 dark:bg-amber-950/40 dark:text-amber-400">
                <Bell className="w-4 h-4" />
              </div>
              <h2 className="text-base font-bold text-slate-900 dark:text-white">
                Institutional Announcements
              </h2>
            </div>
            <Link to="/notifications" className="text-xs font-semibold text-crescent-navy-900 dark:text-emerald-400 hover:underline">
              View All
            </Link>
          </div>

          <div className="divide-y divide-slate-100 dark:divide-crescent-dark-border mt-2">
            {announcements.map((ann) => (
              <div key={ann.id} className="py-3.5 first:pt-2 last:pb-1">
                <div className="flex items-center justify-between gap-2">
                  <span className="text-xs font-semibold text-crescent-navy-800 dark:text-blue-300">
                    {ann.category || 'General'}
                  </span>
                  <span className="text-[11px] text-slate-400">{ann.date || 'Recent'}</span>
                </div>
                <h3 className="text-xs sm:text-sm font-semibold text-slate-800 dark:text-slate-100 mt-1">
                  {ann.title}
                </h3>
                <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">
                  Published by: {ann.author || 'Academic Dean Office'}
                </p>
              </div>
            ))}
          </div>
        </div>

        {/* Study Materials & Document Extractions */}
        <div className="rounded-xl border border-slate-200/80 bg-white p-5 sm:p-6 shadow-sm dark:border-crescent-dark-border dark:bg-crescent-dark-card">
          <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-crescent-dark-border">
            <div className="flex items-center gap-2">
              <div className="p-1.5 rounded-lg bg-blue-50 text-blue-600 dark:bg-blue-950/40 dark:text-blue-400">
                <FileText className="w-4 h-4" />
              </div>
              <h2 className="text-base font-bold text-slate-900 dark:text-white">
                Course Materials & Extractions
              </h2>
            </div>
            <Link to="/files" className="text-xs font-semibold text-crescent-navy-900 dark:text-emerald-400 hover:underline">
              Upload / View
            </Link>
          </div>

          <div className="space-y-3 mt-3">
            {recentMaterials.length > 0 ? (
              recentMaterials.map((mat) => (
                <div
                  key={mat.id}
                  className="flex items-center justify-between p-3 rounded-xl border border-slate-100 dark:border-crescent-navy-800/80 bg-slate-50/50 dark:bg-crescent-navy-950/30"
                >
                  <div className="flex items-center gap-3 min-w-0">
                    <div className="w-9 h-9 rounded-lg bg-crescent-navy-50 dark:bg-crescent-navy-900/60 text-crescent-navy-800 dark:text-blue-300 flex items-center justify-center shrink-0">
                      <FileText className="w-4 h-4" />
                    </div>
                    <div className="min-w-0">
                      <p className="text-xs sm:text-sm font-medium text-slate-900 dark:text-white truncate">
                        {mat.fileName || mat.title || 'Course Notes'}
                      </p>
                      <p className="text-[11px] text-slate-400">
                        {mat.courseId || 'General'} • {mat.analysisStatus || 'COMPLETED'}
                      </p>
                    </div>
                  </div>
                  <Badge variant={mat.analysisStatus === 'COMPLETED' ? 'emerald' : 'amber'}>
                    {mat.analysisStatus === 'COMPLETED' ? 'Analyzed' : 'Pending'}
                  </Badge>
                </div>
              ))
            ) : (
              <div className="p-6 text-center text-xs text-slate-400 border border-dashed border-slate-200 dark:border-crescent-dark-border rounded-xl">
                <FileText className="w-8 h-8 text-slate-300 dark:text-slate-600 mx-auto mb-2" />
                <p className="font-medium text-slate-600 dark:text-slate-300">No course files uploaded yet</p>
                <p className="text-slate-400 mt-0.5">Use the File Analysis section to upload PDF, DOCX, TXT documents for AI summarization.</p>
                <Link
                  to="/files"
                  className="mt-3 inline-flex items-center gap-1.5 px-3 py-1.5 bg-crescent-navy-900 dark:bg-blue-600 text-white rounded-lg text-xs font-medium"
                >
                  Upload Material
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
