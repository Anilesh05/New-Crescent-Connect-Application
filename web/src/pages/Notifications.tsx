import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Bell, Plus, Calendar, Megaphone, CheckCircle2, Search, Filter, ShieldCheck, Tag } from 'lucide-react';
import { collection, addDoc, query, orderBy, onSnapshot, limit } from 'firebase/firestore';
import { db } from '../lib/firebase';
import { PageHeader } from '../components/PageHeader';
import { Badge } from '../components/Badge';
import { Modal } from '../components/Modal';
import { EmptyState } from '../components/EmptyState';

export const Notifications = () => {
  const { userData } = useAuth();
  const [announcements, setAnnouncements] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);

  // New announcement form
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [category, setCategory] = useState('Academic');
  const [submitting, setSubmitting] = useState(false);

  const userRole = userData?.role || 'STUDENT';
  const canPost = userRole === 'STAFF' || userRole === 'ADMIN' || userRole === 'CLASS_ADVISER' || userRole === 'CR';

  useEffect(() => {
    const annRef = collection(db, 'announcements');
    const q = query(annRef, limit(20));

    const unsubscribe = onSnapshot(q, (snapshot) => {
      const fetched = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      }));

      if (fetched.length === 0) {
        setAnnouncements([
          {
            id: 'ann-1',
            title: 'Continuous Assessment - Model Laboratory Exam Schedule Released',
            content: 'The Model Practical Examinations for Semester IV MCA will commence on 28th September 2024. All students are advised to check their respective batch lab allocations and submit verified lab record notebooks before 26th September.',
            category: 'Examination',
            author: 'Dean of Academic Affairs',
            date: 'Today, 09:30 AM',
            targetRole: 'ALL'
          },
          {
            id: 'ann-2',
            title: 'Attendance Policy Enforcement: 75% Requirement for Hall Ticket',
            content: 'In accordance with Crescent Institute academic regulations, students with attendance below 75% across registered courses will not be issued end-semester hall tickets. Please monitor your daily attendance via CrescentConnect.',
            category: 'Attendance',
            author: 'Controller of Examinations',
            date: 'Yesterday, 04:15 PM',
            targetRole: 'STUDENT'
          },
          {
            id: 'ann-3',
            title: 'Campus Recruitment Drive: Tier-1 Tech Companies Pre-Registration',
            content: 'The Department of Training & Placement announces pre-registration for upcoming campus recruitment visits. Eligible MCA & B.Tech students with CGPA >= 7.5 are required to upload their updated resume.',
            category: 'Placement',
            author: 'Director, Career Guidance & Placement Cell',
            date: '20 Sep, 11:00 AM',
            targetRole: 'ALL'
          },
          {
            id: 'ann-4',
            title: 'Digital Identity Card & Automated Library Access Live',
            content: 'CrescentConnect digital student ID is now valid at the Central Library gate turnstiles and sports complex. Contact the IT Helpdesk for profile image updates.',
            category: 'Campus',
            author: 'Director of IT Services',
            date: '18 Sep, 02:00 PM',
            targetRole: 'ALL'
          }
        ]);
      } else {
        setAnnouncements(fetched);
      }
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  const handleCreateAnnouncement = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !content.trim() || !userData) return;

    setSubmitting(true);
    try {
      await addDoc(collection(db, 'announcements'), {
        title: title.trim(),
        content: content.trim(),
        category,
        author: userData.name || 'Institutional Staff',
        authorId: userData.id,
        date: 'Just now',
        createdAt: Date.now(),
        targetRole: 'ALL'
      });

      setTitle('');
      setContent('');
      setIsModalOpen(false);
    } catch (err: any) {
      console.error("Announcement error:", err);
      alert("Failed to publish announcement: " + err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const filteredAnnouncements = announcements.filter(a => {
    const matchesCategory = selectedCategory === 'ALL' || a.category === selectedCategory;
    const matchesSearch = a.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          (a.content && a.content.toLowerCase().includes(searchQuery.toLowerCase()));
    return matchesCategory && matchesSearch;
  });

  return (
    <div className="space-y-6">
      <PageHeader
        title="Institutional Announcements & Notices"
        subtitle="Official notifications from the Academic Dean, Faculty, Examination Office, and Student Affairs."
        badge={<Badge variant="navy">Official Bulletins</Badge>}
        actions={
          canPost ? (
            <button
              onClick={() => setIsModalOpen(true)}
              className="inline-flex items-center gap-2 px-4 py-2.5 bg-crescent-navy-900 hover:bg-crescent-navy-800 dark:bg-emerald-600 dark:hover:bg-emerald-500 text-white rounded-xl text-xs sm:text-sm font-semibold shadow-sm transition-all"
            >
              <Plus className="w-4 h-4" />
              <span>Broadcast Notice</span>
            </button>
          ) : null
        }
      />

      {/* Filter and search bar */}
      <div className="flex flex-col sm:flex-row gap-3 items-stretch sm:items-center justify-between bg-white dark:bg-crescent-dark-card p-4 rounded-xl border border-slate-200/80 dark:border-crescent-dark-border shadow-sm">
        <div className="relative flex-1">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search notifications and announcements..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 text-xs sm:text-sm bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-300 dark:border-crescent-navy-700 rounded-lg text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-crescent-navy-900"
          />
        </div>

        <div className="flex items-center gap-2">
          <Filter className="w-4 h-4 text-slate-400" />
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="px-3 py-2 text-xs font-medium rounded-lg border border-slate-300 dark:border-crescent-navy-700 bg-white dark:bg-crescent-navy-950/40 text-slate-700 dark:text-slate-200 focus:outline-none"
          >
            <option value="ALL">All Categories</option>
            <option value="Academic">Academic</option>
            <option value="Examination">Examination</option>
            <option value="Attendance">Attendance</option>
            <option value="Placement">Placement</option>
            <option value="Campus">Campus</option>
          </select>
        </div>
      </div>

      {/* Announcements Stream */}
      <div className="space-y-4">
        {filteredAnnouncements.length > 0 ? (
          filteredAnnouncements.map((ann) => (
            <div
              key={ann.id}
              className="p-5 sm:p-6 rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border bg-white dark:bg-crescent-dark-card shadow-sm hover:border-slate-300 dark:hover:border-crescent-navy-700 transition-all"
            >
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-3 border-b border-slate-100 dark:border-crescent-dark-border">
                <div className="flex items-center gap-2">
                  <Badge
                    variant={
                      ann.category === 'Examination' ? 'rose' :
                      ann.category === 'Attendance' ? 'amber' :
                      ann.category === 'Placement' ? 'emerald' : 'navy'
                    }
                  >
                    {ann.category || 'General'}
                  </Badge>
                  <span className="text-xs text-slate-400">•</span>
                  <span className="text-xs text-slate-500 dark:text-slate-400 font-medium">
                    Issued by: {ann.author}
                  </span>
                </div>
                <div className="flex items-center gap-1.5 text-xs text-slate-400">
                  <Calendar className="w-3.5 h-3.5" />
                  <span>{ann.date || 'Recent'}</span>
                </div>
              </div>

              <h3 className="text-base sm:text-lg font-bold text-slate-900 dark:text-white mt-3">
                {ann.title}
              </h3>
              <p className="text-xs sm:text-sm text-slate-600 dark:text-slate-300 mt-2 leading-relaxed">
                {ann.content}
              </p>
            </div>
          ))
        ) : (
          <EmptyState
            icon={Bell}
            title="No Announcements Found"
            description="There are currently no announcements matching your category or search keywords."
          />
        )}
      </div>

      {/* Broadcast Modal for Faculty & Staff */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Broadcast Institutional Announcement"
        description="Publish an official notification to students, staff, or department batches."
      >
        <form onSubmit={handleCreateAnnouncement} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-600 dark:text-slate-300 mb-1.5">
              Notice Title
            </label>
            <input
              type="text"
              required
              placeholder="e.g. Schedule for Continuous Assessment Test 2"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 dark:border-crescent-navy-700 bg-slate-50 dark:bg-crescent-navy-950/40 text-slate-900 dark:text-white text-xs sm:text-sm focus:outline-none focus:ring-2 focus:ring-crescent-navy-900"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-600 dark:text-slate-300 mb-1.5">
              Category
            </label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 dark:border-crescent-navy-700 bg-slate-50 dark:bg-crescent-navy-950/40 text-slate-900 dark:text-white text-xs sm:text-sm focus:outline-none"
            >
              <option value="Academic">Academic</option>
              <option value="Examination">Examination</option>
              <option value="Attendance">Attendance</option>
              <option value="Placement">Placement</option>
              <option value="Campus">Campus</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-600 dark:text-slate-300 mb-1.5">
              Announcement Message
            </label>
            <textarea
              rows={4}
              required
              placeholder="Provide complete details, instructions, venue, and deadline..."
              value={content}
              onChange={(e) => setContent(e.target.value)}
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 dark:border-crescent-navy-700 bg-slate-50 dark:bg-crescent-navy-950/40 text-slate-900 dark:text-white text-xs sm:text-sm focus:outline-none focus:ring-2 focus:ring-crescent-navy-900"
            />
          </div>

          <div className="flex items-center justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-crescent-navy-900/60 rounded-xl"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="px-4 py-2 text-xs font-semibold text-white bg-crescent-navy-900 hover:bg-crescent-navy-800 dark:bg-emerald-600 dark:hover:bg-emerald-500 rounded-xl shadow-sm transition-all"
            >
              {submitting ? 'Broadcasting...' : 'Publish Announcement'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
