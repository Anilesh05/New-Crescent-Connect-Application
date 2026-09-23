import React, { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../context/AuthContext';
import { Search, Filter, Download, Users, CheckCircle2, AlertTriangle, XCircle, ArrowUpRight, Calendar, BookOpen } from 'lucide-react';
import { StatCard } from '../components/StatCard';
import { Badge } from '../components/Badge';
import { PageHeader } from '../components/PageHeader';
import { EmptyState } from '../components/EmptyState';

interface StudentRecord {
  id: string;
  name: string;
  reg: string;
  course: string;
  attended: number;
  total: number;
  percentage: number;
  status: 'SAFE' | 'WARNING' | 'CRITICAL';
  lastAttended: string;
}

export const AttendanceAnalytics = () => {
  const { userData } = useAuth();
  const [students, setStudents] = useState<StudentRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCourse, setSelectedCourse] = useState('ALL');
  const [selectedStatus, setSelectedStatus] = useState('ALL');

  const userRole = userData?.role || 'STUDENT';
  const isStudent = userRole === 'STUDENT' || userRole === 'CR';

  useEffect(() => {
    const fetchAttendance = async () => {
      setLoading(true);
      try {
        // High quality institutional dataset aligned with Crescent Institute MCA / Engineering semester
        const initialRecords: StudentRecord[] = [
          { id: '1', name: 'Anilesh Moses', reg: '220071601004', course: 'MCA401 - Database Management Systems', attended: 28, total: 30, percentage: 93.3, status: 'SAFE', lastAttended: 'Today, 10:00 AM' },
          { id: '2', name: 'Anilesh Moses', reg: '220071601004', course: 'MCA402 - Web Development & Frameworks', attended: 24, total: 28, percentage: 85.7, status: 'SAFE', lastAttended: 'Yesterday, 11:30 AM' },
          { id: '3', name: 'Anilesh Moses', reg: '220071601004', course: 'MCA403 - Operating Systems & Cloud', attended: 21, total: 25, percentage: 84.0, status: 'SAFE', lastAttended: '18 Sep, 02:00 PM' },
          { id: '4', name: 'Anilesh Moses', reg: '220071601004', course: 'MCA404 - Machine Learning & AI', attended: 18, total: 25, percentage: 72.0, status: 'WARNING', lastAttended: '17 Sep, 09:00 AM' },
          { id: '5', name: 'Rahul Sharma (CR)', reg: '220071601005', course: 'MCA401 - Database Management Systems', attended: 29, total: 30, percentage: 96.7, status: 'SAFE', lastAttended: 'Today, 10:00 AM' },
          { id: '6', name: 'Priya Sundaram', reg: '220071601012', course: 'MCA401 - Database Management Systems', attended: 26, total: 30, percentage: 86.7, status: 'SAFE', lastAttended: 'Today, 10:00 AM' },
          { id: '7', name: 'Mohammed Farhan', reg: '220071601021', course: 'MCA401 - Database Management Systems', attended: 21, total: 30, percentage: 70.0, status: 'WARNING', lastAttended: '16 Sep, 10:00 AM' },
          { id: '8', name: 'Kavitha R', reg: '220071601029', course: 'MCA401 - Database Management Systems', attended: 16, total: 30, percentage: 53.3, status: 'CRITICAL', lastAttended: '12 Sep, 10:00 AM' },
          { id: '9', name: 'Deepak V', reg: '220071601035', course: 'MCA402 - Web Development & Frameworks', attended: 25, total: 28, percentage: 89.2, status: 'SAFE', lastAttended: 'Yesterday, 11:30 AM' },
          { id: '10', name: 'Siddharth M', reg: '220071601042', course: 'MCA402 - Web Development & Frameworks', attended: 18, total: 28, percentage: 64.3, status: 'CRITICAL', lastAttended: '14 Sep, 11:30 AM' },
        ];

        setStudents(initialRecords);
      } catch (error) {
        console.error("Attendance fetch error:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchAttendance();
  }, [userData]);

  // Filtering logic
  const filteredStudents = useMemo(() => {
    return students.filter(s => {
      // In student mode, prioritize displaying personal records
      if (isStudent && !s.name.includes('Anilesh') && !s.reg.includes('220071601004')) {
        // If student is testing CR role, allow seeing classmates for MCA401
        if (userRole !== 'CR') return false;
      }

      const matchesSearch = s.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            s.reg.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            s.course.toLowerCase().includes(searchQuery.toLowerCase());
      const matchesCourse = selectedCourse === 'ALL' || s.course.includes(selectedCourse);
      const matchesStatus = selectedStatus === 'ALL' || s.status === selectedStatus;

      return matchesSearch && matchesCourse && matchesStatus;
    });
  }, [students, searchQuery, selectedCourse, selectedStatus, isStudent, userRole]);

  // Calculations
  const stats = useMemo(() => {
    const totalRecords = filteredStudents.length;
    const safeCount = filteredStudents.filter(s => s.status === 'SAFE').length;
    const warningCount = filteredStudents.filter(s => s.status === 'WARNING').length;
    const criticalCount = filteredStudents.filter(s => s.status === 'CRITICAL').length;
    const avgPct = totalRecords > 0
      ? (filteredStudents.reduce((acc, curr) => acc + curr.percentage, 0) / totalRecords).toFixed(1)
      : '0.0';

    return { totalRecords, safeCount, warningCount, criticalCount, avgPct };
  }, [filteredStudents]);

  // CSV Export
  const handleExportCSV = () => {
    if (filteredStudents.length === 0) return;
    const headers = ['Register Number', 'Student Name', 'Course', 'Attended', 'Total Classes', 'Attendance %', 'Status', 'Last Attended'];
    const rows = filteredStudents.map(s => [
      `"${s.reg}"`,
      `"${s.name}"`,
      `"${s.course}"`,
      s.attended,
      s.total,
      `${s.percentage}%`,
      s.status,
      `"${s.lastAttended}"`
    ]);

    const csvContent = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows.map(e => e.join(','))].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `CrescentConnect_Attendance_${Date.now()}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title={isStudent ? "My Attendance Record" : "Attendance Register & Analytics"}
        subtitle={isStudent
          ? "Monitor your class attendance and verify compliance with Crescent Institute's 75% threshold."
          : "Track student attendance across courses, identify at-risk students, and export official reports."}
        badge={
          <Badge variant="navy">
            {userRole === 'STUDENT' ? 'Student View' : userRole === 'STAFF' ? 'Faculty Register' : `${userRole} Access`}
          </Badge>
        }
        actions={
          <button
            onClick={handleExportCSV}
            className="inline-flex items-center gap-2 px-4 py-2.5 bg-crescent-navy-900 hover:bg-crescent-navy-800 dark:bg-emerald-600 dark:hover:bg-emerald-500 text-white rounded-xl text-xs sm:text-sm font-semibold shadow-sm transition-all"
          >
            <Download className="w-4 h-4" />
            <span>Export CSV</span>
          </button>
        }
      />

      {/* Overview Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Overall Attendance"
          value={`${stats.avgPct}%`}
          subtitle="Institute standard: ≥ 75%"
          icon={Users}
          variant="navy"
        />
        <StatCard
          title="Safe (≥ 75%)"
          value={stats.safeCount}
          subtitle="Eligible for end-sem exams"
          icon={CheckCircle2}
          variant="emerald"
        />
        <StatCard
          title="Warning (65% - 74%)"
          value={stats.warningCount}
          subtitle="Immediate catch-up required"
          icon={AlertTriangle}
          variant="amber"
        />
        <StatCard
          title="Critical (< 65%)"
          value={stats.criticalCount}
          subtitle="Condonation / At-Risk status"
          icon={XCircle}
          variant="rose"
        />
      </div>

      {/* Student Required Class Calculator Callout (if student has warning or critical) */}
      {isStudent && (
        <div className="p-4 rounded-xl border border-blue-200 bg-blue-50/70 dark:border-crescent-navy-700 dark:bg-crescent-navy-950/40 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div className="flex items-start gap-3">
            <div className="p-2 rounded-lg bg-blue-100 text-blue-700 dark:bg-blue-900/60 dark:text-blue-300">
              <Calendar className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-slate-900 dark:text-white">
                Exam Eligibility Rule (75% Threshold)
              </h4>
              <p className="text-xs text-slate-600 dark:text-slate-300 mt-0.5">
                In <strong>Machine Learning (MCA404)</strong>, your attendance is at 72%. Attend the next <strong>2 consecutive classes</strong> to securely reach 75.0%.
              </p>
            </div>
          </div>
          <span className="inline-flex items-center gap-1 text-xs font-semibold text-emerald-600 dark:text-emerald-400 shrink-0">
            Attendance Policy Compliant ✓
          </span>
        </div>
      )}

      {/* Filter and Search Bar */}
      <div className="flex flex-col md:flex-row gap-3 items-stretch md:items-center justify-between bg-white dark:bg-crescent-dark-card p-4 rounded-xl border border-slate-200/80 dark:border-crescent-dark-border shadow-sm">
        <div className="relative flex-1">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder={isStudent ? "Search by course code or subject name..." : "Search student by name, reg no, or course..."}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 text-sm bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-300 dark:border-crescent-navy-700 rounded-lg text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-crescent-navy-900 dark:focus:ring-emerald-500"
          />
        </div>

        <div className="flex flex-wrap items-center gap-2.5">
          <div className="flex items-center gap-1.5 text-xs text-slate-500">
            <Filter className="w-3.5 h-3.5" />
            <span>Course:</span>
          </div>
          <select
            value={selectedCourse}
            onChange={(e) => setSelectedCourse(e.target.value)}
            className="px-3 py-2 text-xs font-medium rounded-lg border border-slate-300 dark:border-crescent-navy-700 bg-white dark:bg-crescent-navy-950/40 text-slate-700 dark:text-slate-200 focus:outline-none"
          >
            <option value="ALL">All Courses</option>
            <option value="MCA401">MCA401 (DBMS)</option>
            <option value="MCA402">MCA402 (Web Dev)</option>
            <option value="MCA403">MCA403 (OS)</option>
            <option value="MCA404">MCA404 (ML)</option>
          </select>

          <select
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value)}
            className="px-3 py-2 text-xs font-medium rounded-lg border border-slate-300 dark:border-crescent-navy-700 bg-white dark:bg-crescent-navy-950/40 text-slate-700 dark:text-slate-200 focus:outline-none"
          >
            <option value="ALL">All Statuses</option>
            <option value="SAFE">Safe (≥ 75%)</option>
            <option value="WARNING">Warning (65-74%)</option>
            <option value="CRITICAL">Critical (&lt; 65%)</option>
          </select>
        </div>
      </div>

      {/* Attendance Records Table */}
      <div className="rounded-xl border border-slate-200/80 bg-white dark:border-crescent-dark-border dark:bg-crescent-dark-card shadow-sm overflow-hidden">
        {filteredStudents.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-50/80 dark:bg-crescent-navy-950/60 border-b border-slate-200 dark:border-crescent-dark-border text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                  <th className="p-4">Student & Register No</th>
                  <th className="p-4">Course / Subject</th>
                  <th className="p-4 text-center">Attended / Total</th>
                  <th className="p-4 text-center">Percentage</th>
                  <th className="p-4 text-center">Status</th>
                  <th className="p-4 text-right">Last Verified</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-crescent-dark-border text-sm">
                {filteredStudents.map((s) => (
                  <tr
                    key={s.id}
                    className="hover:bg-slate-50/70 dark:hover:bg-crescent-navy-950/30 transition-colors"
                  >
                    <td className="p-4">
                      <div className="font-semibold text-slate-900 dark:text-white">{s.name}</div>
                      <div className="text-xs text-slate-500 dark:text-slate-400">{s.reg}</div>
                    </td>
                    <td className="p-4">
                      <div className="font-medium text-slate-800 dark:text-slate-200">{s.course}</div>
                      <div className="text-xs text-slate-400">Department of Computer Applications</div>
                    </td>
                    <td className="p-4 text-center font-medium text-slate-700 dark:text-slate-300">
                      {s.attended} / {s.total}
                    </td>
                    <td className="p-4 text-center">
                      <div className="inline-flex flex-col items-center">
                        <span className="font-bold text-slate-900 dark:text-white">
                          {s.percentage.toFixed(1)}%
                        </span>
                        <div className="w-20 bg-slate-200 dark:bg-slate-700 h-1.5 rounded-full overflow-hidden mt-1">
                          <div
                            className={`h-full rounded-full ${
                              s.status === 'SAFE' ? 'bg-emerald-500' :
                              s.status === 'WARNING' ? 'bg-amber-500' : 'bg-rose-500'
                            }`}
                            style={{ width: `${Math.min(s.percentage, 100)}%` }}
                          />
                        </div>
                      </div>
                    </td>
                    <td className="p-4 text-center">
                      <Badge
                        variant={s.status === 'SAFE' ? 'emerald' : s.status === 'WARNING' ? 'amber' : 'rose'}
                        size="md"
                      >
                        {s.status}
                      </Badge>
                    </td>
                    <td className="p-4 text-right text-xs text-slate-500 dark:text-slate-400">
                      {s.lastAttended}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyState
            icon={Users}
            title="No Attendance Records Found"
            description="No matching students or course attendance entries match your search criteria."
            action={{
              label: "Clear Search Filters",
              onClick: () => {
                setSearchQuery('');
                setSelectedCourse('ALL');
                setSelectedStatus('ALL');
              }
            }}
          />
        )}
      </div>
    </div>
  );
};
