import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import {
  BrainCircuit,
  Sparkles,
  TrendingUp,
  AlertCircle,
  CheckCircle2,
  Send,
  Loader2,
  BookOpen,
  Calendar,
  GraduationCap,
  ShieldCheck,
  HelpCircle,
  Lightbulb,
  Cpu
} from 'lucide-react';
import { analyzeDocumentText } from '../lib/ai';
import { Badge } from '../components/Badge';
import { PageHeader } from '../components/PageHeader';
import { StatCard } from '../components/StatCard';

export const AIAnalytics = () => {
  const { userData } = useAuth();
  const [customPrompt, setCustomPrompt] = useState('');
  const [analyzing, setAnalyzing] = useState(false);
  const [customResult, setCustomResult] = useState<any>(null);

  const userRole = userData?.role || 'STUDENT';
  const isStudent = userRole === 'STUDENT' || userRole === 'CR';
  const isApiKeyConfigured = Boolean(import.meta.env.VITE_GEMINI_API_KEY && import.meta.env.VITE_GEMINI_API_KEY !== "YOUR_API_KEY");

  const handleAskAI = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!customPrompt.trim()) return;

    setAnalyzing(true);
    try {
      const result = await analyzeDocumentText(customPrompt);
      setCustomResult(result);
    } catch (err) {
      console.error("Custom query error:", err);
    } finally {
      setAnalyzing(false);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="AI Academic Intelligence & Analytics"
        subtitle="Predictive attendance modeling, syllabus mastery diagnostics, and cognitive study roadmaps powered by Gemini AI."
        badge={
          <Badge variant={isApiKeyConfigured ? "emerald" : "amber"}>
            <Cpu className="w-3 h-3" />
            <span>{isApiKeyConfigured ? 'Gemini 1.5 Flash Active' : 'Offline Academic Engine Active'}</span>
          </Badge>
        }
      />

      {/* Hero Banner */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-crescent-navy-900 via-crescent-navy-800 to-[#1e3a63] p-6 sm:p-8 text-white shadow-lg border border-crescent-navy-700/60">
        <div className="absolute right-0 top-0 translate-x-8 -translate-y-8 opacity-10 pointer-events-none">
          <BrainCircuit className="w-72 h-72 text-white" />
        </div>
        <div className="relative z-10 max-w-2xl space-y-3">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md text-xs font-semibold text-emerald-300 border border-white/20">
            <Sparkles className="w-3.5 h-3.5 text-amber-300" />
            <span>Institutional AI Analytics Model</span>
          </div>
          <h2 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            {isStudent ? 'Personalized Semester Performance Diagnostic' : 'Faculty & Batch Academic Intelligence'}
          </h2>
          <p className="text-xs sm:text-sm text-slate-300 leading-relaxed">
            {isStudent
              ? `Synthesized from your 5 registered courses in MCA Semester IV. Attendance trajectory indicates 93% safe probability if current attendance rhythm is maintained.`
              : `Real-time pattern analysis across 3 taught courses. Detects attendance disengagement 14 days before standard midterm assessments.`}
          </p>
        </div>
      </div>

      {/* Top AI Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard
          title={isStudent ? "Attendance Trajectory" : "Batch Predictability"}
          value={isStudent ? "88.4% Safe" : "84.2% Safe"}
          subtitle={isStudent ? "Projected end-sem: 91.2%" : "Predicted condonation rate: 4%"}
          icon={TrendingUp}
          variant="emerald"
        />
        <StatCard
          title={isStudent ? "Weak Concept Focus" : "Syllabus Pacing"}
          value={isStudent ? "Normalization (BCNF)" : "Unit 3 (On Schedule)"}
          subtitle={isStudent ? "Based on DBMS quiz analysis" : "2 sessions ahead of timetable"}
          icon={BrainCircuit}
          variant="purple"
        />
        <StatCard
          title={isStudent ? "Risk Interventions" : "At-Risk Alert"}
          value={isStudent ? "1 Action Required" : "4 Students Flagged"}
          subtitle={isStudent ? "Machine Learning Attendance" : "Automated SMS notification dispatched"}
          icon={AlertCircle}
          variant="amber"
        />
      </div>

      {/* Two Column Diagnostic: Attention Areas vs Strengths */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Areas Requiring Attention */}
        <div className="bg-white dark:bg-crescent-dark-card p-5 sm:p-6 rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border shadow-sm">
          <div className="flex items-center gap-3 mb-4 pb-3 border-b border-slate-100 dark:border-crescent-dark-border">
            <div className="w-9 h-9 rounded-xl bg-amber-50 dark:bg-amber-950/40 flex items-center justify-center text-amber-600 dark:text-amber-400">
              <AlertCircle className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900 dark:text-white">
                {isStudent ? 'Areas Requiring Academic Attention' : 'Class Vulnerabilities & Action Items'}
              </h3>
              <p className="text-xs text-slate-500 dark:text-slate-400">High priority items flagged by AI engine</p>
            </div>
          </div>

          <div className="space-y-3">
            {isStudent ? (
              <>
                <div className="p-3.5 rounded-xl bg-amber-50/50 dark:bg-amber-950/20 border border-amber-200/60 dark:border-amber-800/40 text-xs text-slate-700 dark:text-slate-300">
                  <strong className="text-slate-900 dark:text-white block font-semibold mb-1">
                    Attendance Vulnerability in Machine Learning (MCA404)
                  </strong>
                  Current attendance is at 72%. Attend the next <strong>2 consecutive classes</strong> to cross the 75% safe threshold. Missing another class risks university condonation review.
                </div>
                <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60 text-xs text-slate-700 dark:text-slate-300">
                  <strong className="text-slate-900 dark:text-white block font-semibold mb-1">
                    Continuous Assessment Assignment Approaching
                  </strong>
                  Database Management Systems Assignment 3 is due in 2 days. Based on historical class submissions, students submitting early achieve 15% higher peer review marks.
                </div>
              </>
            ) : (
              <>
                <div className="p-3.5 rounded-xl bg-amber-50/50 dark:bg-amber-950/20 border border-amber-200/60 dark:border-amber-800/40 text-xs text-slate-700 dark:text-slate-300">
                  <strong className="text-slate-900 dark:text-white block font-semibold mb-1">
                    Friday Morning Session Disengagement
                  </strong>
                  Attendance in MCA401 dips by 12% during Period 1 on Fridays. AI suggests conducting interactive coding polls at the start of the lecture.
                </div>
                <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60 text-xs text-slate-700 dark:text-slate-300">
                  <strong className="text-slate-900 dark:text-white block font-semibold mb-1">
                    At-Risk Student Intervention Recommended
                  </strong>
                  Students Mohammed Farhan and Kavitha R have dropped below 70%. An automated academic advisement appointment is recommended.
                </div>
              </>
            )}
          </div>
        </div>

        {/* Strengths & Accreditations */}
        <div className="bg-white dark:bg-crescent-dark-card p-5 sm:p-6 rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border shadow-sm">
          <div className="flex items-center gap-3 mb-4 pb-3 border-b border-slate-100 dark:border-crescent-dark-border">
            <div className="w-9 h-9 rounded-xl bg-emerald-50 dark:bg-emerald-950/40 flex items-center justify-center text-emerald-600 dark:text-emerald-400">
              <TrendingUp className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900 dark:text-white">
                {isStudent ? 'Cognitive Strengths & Mastery' : 'High Performing Metrics'}
              </h3>
              <p className="text-xs text-slate-500 dark:text-slate-400">Verified institutional competencies</p>
            </div>
          </div>

          <div className="space-y-3">
            {isStudent ? (
              <>
                <div className="p-3.5 rounded-xl bg-emerald-50/50 dark:bg-emerald-950/20 border border-emerald-200/60 dark:border-emerald-800/40 text-xs text-slate-700 dark:text-slate-300">
                  <strong className="text-slate-900 dark:text-white block font-semibold mb-1">
                    Superior Command of Web Development & Frameworks
                  </strong>
                  Your practical submissions in React and state reconciliation rank in the top 10% of the MCA cohort.
                </div>
                <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60 text-xs text-slate-700 dark:text-slate-300">
                  <strong className="text-slate-900 dark:text-white block font-semibold mb-1">
                    Exemplary DBMS Attendance Consistency
                  </strong>
                  93.3% attendance recorded. Perfect biometric attendance compliance throughout all lab sessions.
                </div>
              </>
            ) : (
              <>
                <div className="p-3.5 rounded-xl bg-emerald-50/50 dark:bg-emerald-950/20 border border-emerald-200/60 dark:border-emerald-800/40 text-xs text-slate-700 dark:text-slate-300">
                  <strong className="text-slate-900 dark:text-white block font-semibold mb-1">
                    MCA402 Course Material Engagement (89%)
                  </strong>
                  Students spent an average of 42 minutes interacting with the uploaded React Architecture document.
                </div>
                <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60 text-xs text-slate-700 dark:text-slate-300">
                  <strong className="text-slate-900 dark:text-white block font-semibold mb-1">
                    Continuous Assessment Timelines Fully Maintained
                  </strong>
                  100% of internal assessment marks synced ahead of the institutional deadline.
                </div>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Recommended Study Roadmap */}
      <div className="bg-white dark:bg-crescent-dark-card p-5 sm:p-6 rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border shadow-sm">
        <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-crescent-dark-border">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-5 h-5 text-blue-600 dark:text-blue-400" />
            <h3 className="text-base font-bold text-slate-900 dark:text-white">
              {isStudent ? 'Personalized Cognitive Study Roadmap' : 'Recommended Instructional Plan'}
            </h3>
          </div>
          <Badge variant="navy">Semester IV Target</Badge>
        </div>

        <div className="mt-4 space-y-3">
          <div className="flex items-start gap-3 p-3 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-100 dark:border-crescent-navy-800/60">
            <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-100 text-blue-700 dark:bg-blue-900/60 dark:text-blue-300 font-bold text-xs">
              1
            </span>
            <div>
              <p className="text-xs sm:text-sm font-semibold text-slate-900 dark:text-white">
                Review Boyce-Codd Normal Form (BCNF) Proofs
              </p>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                Focus on functional dependency closures. Model exam trends show this carries 15% question weight in Unit 2.
              </p>
            </div>
          </div>

          <div className="flex items-start gap-3 p-3 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-100 dark:border-crescent-navy-800/60">
            <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-100 text-blue-700 dark:bg-blue-900/60 dark:text-blue-300 font-bold text-xs">
              2
            </span>
            <div>
              <p className="text-xs sm:text-sm font-semibold text-slate-900 dark:text-white">
                Complete Machine Learning Supervised Algorithms Lab
              </p>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                Implementation of decision trees in Python. Essential to maintain internal assessment marks before Thursday.
              </p>
            </div>
          </div>

          <div className="flex items-start gap-3 p-3 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-100 dark:border-crescent-navy-800/60">
            <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-100 text-blue-700 dark:bg-blue-900/60 dark:text-blue-300 font-bold text-xs">
              3
            </span>
            <div>
              <p className="text-xs sm:text-sm font-semibold text-slate-900 dark:text-white">
                Review Operating Systems Deadlock Avoidance (Banker's Algorithm)
              </p>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                Practice resource-allocation matrices for 4-process scenarios.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Interactive Academic Reasoning Console */}
      <div className="bg-white dark:bg-crescent-dark-card p-5 sm:p-6 rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border shadow-sm">
        <div className="flex items-center gap-2 mb-3">
          <Sparkles className="w-5 h-5 text-purple-600 dark:text-purple-400" />
          <h3 className="text-base font-bold text-slate-900 dark:text-white">
            Academic Query Console
          </h3>
        </div>
        <p className="text-xs text-slate-500 dark:text-slate-400 mb-4">
          Ask any syllabus topic, request exam questions, or prompt the AI for an explanation based on Crescent Institute course content.
        </p>

        <form onSubmit={handleAskAI} className="space-y-3">
          <div className="relative">
            <textarea
              rows={3}
              value={customPrompt}
              onChange={(e) => setCustomPrompt(e.target.value)}
              placeholder="e.g. Explain how Two-Phase Locking ensures conflict serializability in DBMS, and generate 2 viva exam questions."
              className="w-full p-3.5 rounded-xl border border-slate-300 dark:border-crescent-navy-700 bg-slate-50 dark:bg-crescent-navy-950/40 text-slate-900 dark:text-white text-xs sm:text-sm placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-crescent-navy-900 dark:focus:ring-emerald-500"
            />
          </div>

          <div className="flex items-center justify-between">
            <span className="text-[11px] text-slate-400">
              Powered by Google Gemini 1.5 Flash & Institutional Knowledge Base
            </span>
            <button
              type="submit"
              disabled={analyzing || !customPrompt.trim()}
              className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-crescent-navy-900 hover:bg-crescent-navy-800 dark:bg-emerald-600 dark:hover:bg-emerald-500 text-white text-xs sm:text-sm font-semibold shadow-sm transition-all disabled:opacity-50 cursor-pointer"
            >
              {analyzing ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Synthesizing...</span>
                </>
              ) : (
                <>
                  <Send className="w-4 h-4" />
                  <span>Analyze Query</span>
                </>
              )}
            </button>
          </div>
        </form>

        {customResult && (
          <div className="mt-4 p-4 rounded-xl bg-purple-50/50 dark:bg-purple-950/20 border border-purple-200 dark:border-purple-800/40 space-y-3">
            <h4 className="text-xs font-bold text-purple-800 dark:text-purple-300 uppercase tracking-wider">
              AI Academic Response
            </h4>
            <p className="text-xs sm:text-sm text-slate-800 dark:text-slate-200 leading-relaxed">
              {customResult.summary}
            </p>
            {customResult.keyTopics && (
              <div className="flex flex-wrap gap-1.5 pt-1">
                {customResult.keyTopics.map((t: string, i: number) => (
                  <Badge key={i} variant="purple">{t}</Badge>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
