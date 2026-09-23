import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { BookOpen, Calendar, Clock, Award, CheckCircle2, FileText, ChevronRight } from 'lucide-react';
import { PageHeader } from '../components/PageHeader';
import { Badge } from '../components/Badge';
import { StatCard } from '../components/StatCard';

export const Academics = () => {
  const { userData } = useAuth();
  const [selectedDay, setSelectedDay] = useState('Monday');

  const userRole = userData?.role || 'STUDENT';

  const courses = [
    {
      code: 'MCA401',
      name: 'Database Management Systems & Distributed DB',
      credits: 4,
      faculty: 'Dr. Rahmath S (Professor)',
      progress: 68,
      internalMarks: '22 / 25',
      schedule: 'Mon (10:00 AM), Wed (02:00 PM), Fri (11:30 AM)',
      room: 'Room 301, Academic Block 2'
    },
    {
      code: 'MCA402',
      name: 'Full Stack Web Development & Cloud Deployment',
      credits: 4,
      faculty: 'Prof. Anitha K (Associate Professor)',
      progress: 75,
      internalMarks: '24 / 25',
      schedule: 'Tue (11:30 AM), Thu (10:00 AM), Fri (02:00 PM)',
      room: 'Computer Lab 2'
    },
    {
      code: 'MCA403',
      name: 'Operating Systems & Virtualization Architecture',
      credits: 3,
      faculty: 'Dr. John Doe (Professor)',
      progress: 60,
      internalMarks: '20 / 25',
      schedule: 'Mon (02:00 PM), Wed (10:00 AM), Thu (11:30 AM)',
      room: 'Room 204, Academic Block 1'
    },
    {
      code: 'MCA404',
      name: 'Machine Learning Foundations & Neural Networks',
      credits: 4,
      faculty: 'Dr. Priya V (Assistant Professor)',
      progress: 55,
      internalMarks: '19 / 25',
      schedule: 'Tue (09:00 AM), Wed (11:30 AM), Thu (02:00 PM)',
      room: 'AI & Data Science Lab'
    },
    {
      code: 'MCA405',
      name: 'Software Engineering & Agile Project Management',
      credits: 3,
      faculty: 'Prof. Ramesh K (Assistant Professor)',
      progress: 80,
      internalMarks: '23 / 25',
      schedule: 'Mon (11:30 AM), Tue (02:00 PM), Fri (09:00 AM)',
      room: 'Room 302, Academic Block 2'
    }
  ];

  const timetable: Record<string, any[]> = {
    Monday: [
      { time: '09:00 - 10:00 AM', code: 'MCA401', name: 'Database Management Systems', room: 'Room 301', type: 'Lecture' },
      { time: '10:00 - 11:00 AM', code: 'MCA405', name: 'Software Engineering', room: 'Room 302', type: 'Lecture' },
      { time: '11:30 - 01:00 PM', code: 'MCA403', name: 'Operating Systems Lab', room: 'Lab 1', type: 'Practical' },
      { time: '02:00 - 03:30 PM', code: 'MCA402', name: 'Web Dev Tutorial', room: 'Lab 2', type: 'Tutorial' },
    ],
    Tuesday: [
      { time: '09:00 - 10:00 AM', code: 'MCA404', name: 'Machine Learning', room: 'AI Lab', type: 'Lecture' },
      { time: '10:00 - 11:00 AM', code: 'MCA402', name: 'Web Development', room: 'Lab 2', type: 'Lecture' },
      { time: '11:30 - 01:00 PM', code: 'MCA405', name: 'Agile Project Review', room: 'Room 302', type: 'Seminar' },
    ],
    Wednesday: [
      { time: '09:00 - 10:00 AM', code: 'MCA403', name: 'Operating Systems', room: 'Room 204', type: 'Lecture' },
      { time: '10:00 - 11:30 AM', code: 'MCA401', name: 'Distributed DB Lab', room: 'Lab 3', type: 'Practical' },
      { time: '01:30 - 03:00 PM', code: 'MCA404', name: 'ML Lab Session', room: 'AI Lab', type: 'Practical' },
    ],
    Thursday: [
      { time: '09:00 - 10:30 AM', code: 'MCA402', name: 'Full Stack Lab', room: 'Lab 2', type: 'Practical' },
      { time: '11:00 - 12:30 PM', code: 'MCA403', name: 'Operating Systems', room: 'Room 204', type: 'Lecture' },
      { time: '02:00 - 03:30 PM', code: 'MCA404', name: 'Neural Networks Seminar', room: 'Seminar Hall 1', type: 'Seminar' },
    ],
    Friday: [
      { time: '09:00 - 10:00 AM', code: 'MCA405', name: 'Software Engineering', room: 'Room 302', type: 'Lecture' },
      { time: '10:00 - 11:00 AM', code: 'MCA401', name: 'Database Management', room: 'Room 301', type: 'Lecture' },
      { time: '11:30 - 01:00 PM', code: 'MCA402', name: 'Cloud Architecture', room: 'Lab 2', type: 'Lecture' },
    ]
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Academic Curriculum & Timetable"
        subtitle="Current semester courses, syllabus pacing, continuous assessments, and weekly class schedule."
        badge={<Badge variant="navy">Semester IV (MCA) • 2023-2024</Badge>}
      />

      {/* Top Academic Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <StatCard
          title="Total Credits"
          value="18 Credits"
          subtitle="All courses active"
          icon={BookOpen}
          variant="navy"
        />
        <StatCard
          title="Current CGPA"
          value="8.74"
          subtitle="Top 12% in department"
          icon={Award}
          variant="emerald"
        />
        <StatCard
          title="Continuous Assessment"
          value="108 / 125"
          subtitle="86.4% internal average"
          icon={CheckCircle2}
          variant="blue"
        />
        <StatCard
          title="Syllabus Completion"
          value="67.6%"
          subtitle="Across all 5 subjects"
          icon={FileText}
          variant="purple"
        />
      </div>

      {/* Course Catalog Grid */}
      <div>
        <h2 className="text-lg font-bold text-slate-900 dark:text-white mb-3">
          Registered Semester Courses
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {courses.map((c) => (
            <div
              key={c.code}
              className="p-5 rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border bg-white dark:bg-crescent-dark-card shadow-sm hover:border-slate-300 dark:hover:border-crescent-navy-700 transition-all flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-emerald-600 dark:text-emerald-400">
                    {c.code} • {c.credits} Credits
                  </span>
                  <Badge variant="navy">Internal: {c.internalMarks}</Badge>
                </div>
                <h3 className="text-base font-bold text-slate-900 dark:text-white mt-1.5">
                  {c.name}
                </h3>
                <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
                  Faculty: <strong className="text-slate-700 dark:text-slate-300">{c.faculty}</strong>
                </p>
                <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                  Venue: {c.room}
                </p>

                {/* Progress bar */}
                <div className="mt-4">
                  <div className="flex items-center justify-between text-xs text-slate-500 dark:text-slate-400 mb-1">
                    <span>Syllabus Covered</span>
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{c.progress}%</span>
                  </div>
                  <div className="w-full bg-slate-100 dark:bg-slate-800 h-2 rounded-full overflow-hidden">
                    <div
                      className="bg-crescent-navy-900 dark:bg-emerald-500 h-full rounded-full transition-all duration-500"
                      style={{ width: `${c.progress}%` }}
                    />
                  </div>
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-slate-100 dark:border-crescent-dark-border flex items-center justify-between text-xs text-slate-500 dark:text-slate-400">
                <span>{c.schedule}</span>
                <span className="text-emerald-600 dark:text-emerald-400 font-semibold flex items-center gap-1">
                  Syllabus <ChevronRight className="w-3 h-3" />
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Weekly Timetable Explorer */}
      <div className="bg-white dark:bg-crescent-dark-card rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border p-5 sm:p-6 shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-slate-100 dark:border-crescent-dark-border">
          <div>
            <h2 className="text-base font-bold text-slate-900 dark:text-white flex items-center gap-2">
              <Calendar className="w-5 h-5 text-crescent-navy-900 dark:text-emerald-400" />
              Weekly Class Timetable
            </h2>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Department of Computer Applications • Academic Block 2
            </p>
          </div>

          {/* Day Selector */}
          <div className="flex items-center gap-1.5 rounded-xl bg-slate-100 dark:bg-crescent-navy-950/60 p-1">
            {['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'].map((day) => (
              <button
                key={day}
                onClick={() => setSelectedDay(day)}
                className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all ${
                  selectedDay === day
                    ? 'bg-crescent-navy-900 text-white dark:bg-emerald-600 dark:text-white shadow-sm'
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900'
                }`}
              >
                {day.substring(0, 3)}
              </button>
            ))}
          </div>
        </div>

        {/* Timetable slots */}
        <div className="divide-y divide-slate-100 dark:divide-crescent-dark-border mt-3">
          {timetable[selectedDay]?.map((slot, idx) => (
            <div
              key={idx}
              className="py-3.5 flex flex-col sm:flex-row sm:items-center justify-between gap-2 hover:bg-slate-50/60 dark:hover:bg-crescent-navy-950/30 px-2 rounded-lg transition-colors"
            >
              <div className="flex items-center gap-4">
                <div className="w-32 text-xs font-semibold text-slate-500 dark:text-slate-400 flex items-center gap-1.5">
                  <Clock className="w-3.5 h-3.5 text-slate-400" />
                  {slot.time}
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-bold text-emerald-600 dark:text-emerald-400">{slot.code}</span>
                    <span className="text-sm font-semibold text-slate-900 dark:text-white">{slot.name}</span>
                  </div>
                  <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">Venue: {slot.room}</p>
                </div>
              </div>
              <Badge variant={slot.type === 'Practical' ? 'purple' : slot.type === 'Tutorial' ? 'amber' : 'navy'}>
                {slot.type}
              </Badge>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
