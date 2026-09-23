import React, { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../context/AuthContext';
import {
  UploadCloud,
  File,
  Search,
  BrainCircuit,
  FileText,
  Download,
  AlertCircle,
  CheckCircle2,
  XCircle,
  Clock,
  Sparkles,
  HelpCircle,
  ListOrdered,
  BookOpen,
  Filter,
  Eye
} from 'lucide-react';
import { supabase } from '../lib/supabase';
import { db } from '../lib/firebase';
import { collection, query, where, addDoc, updateDoc, doc, onSnapshot, orderBy } from 'firebase/firestore';
import { extractTextFromFile, normalizeText } from '../lib/documentExtractor';
import { analyzeDocumentText } from '../lib/ai';
import { Badge } from '../components/Badge';
import { PageHeader } from '../components/PageHeader';
import { EmptyState } from '../components/EmptyState';

export const FileAnalysis = () => {
  const { userData } = useAuth();
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState('');
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [files, setFiles] = useState<any[]>([]);
  const [selectedFile, setSelectedFile] = useState<any>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [isDragOver, setIsDragOver] = useState(false);
  const [activeTab, setActiveTab] = useState<'files' | 'analysis'>('files');

  const userRole = userData?.role || 'STUDENT';
  const isFacultyOrAdmin = userRole === 'STAFF' || userRole === 'ADMIN' || userRole === 'CLASS_ADVISER';

  useEffect(() => {
    if (!userData) return;

    const materialsRef = collection(db, 'study_materials');
    // If faculty, optionally query own or all. For student, query all materials for their courses
    const q = isFacultyOrAdmin
      ? query(materialsRef)
      : query(materialsRef);

    const unsubscribe = onSnapshot(q, (snapshot) => {
      const fetchedFiles = snapshot.docs.map(d => {
        const data = d.data();
        return {
          id: d.id,
          name: data.fileName || data.title || 'Course Material',
          type: data.fileType || 'application/octet-stream',
          status: data.analysisStatus || (data.syncStatus === 'COMPLETED' ? 'COMPLETED' : 'PENDING'),
          date: data.createdAt ? new Date(data.createdAt).toLocaleDateString() : 'Just now',
          storagePath: data.storagePath,
          analysisResult: data.analysisResult || null,
          extractedTextPreview: data.extractedTextPreview || null,
          extractionStatus: data.extractionStatus || null,
          courseId: data.courseId || 'General'
        };
      });

      // Default high quality sample study materials if collection is fresh
      if (fetchedFiles.length === 0) {
        const defaultSamples = [
          {
            id: 'sample-1',
            name: 'MCA401-Unit-3-Relational-Calculus-and-Transactions.pdf',
            type: 'application/pdf',
            status: 'COMPLETED',
            date: 'Yesterday',
            storagePath: null,
            courseId: 'MCA401',
            extractionStatus: 'Successfully extracted 14,280 characters from 12 pages.',
            extractedTextPreview: 'Unit 3: Transaction Management & Concurrency Control. ACID Properties: Atomicity, Consistency, Isolation, Durability. Serializability and Conflict Serializability. 2PL (Two-Phase Locking) Protocol. Deadlock Prevention and Detection mechanisms...',
            analysisResult: {
              summary: 'Comprehensive academic notes detailing ACID transaction semantics, conflict serializability algorithms, Two-Phase Locking (2PL), and database concurrency control protocols for relational databases.',
              keyTopics: ['ACID Properties', 'Conflict Serializability', 'Two-Phase Locking (2PL)', 'Deadlock Handling', 'Timestamp Ordering'],
              importantConcepts: ['Strict 2PL prevents cascading rollbacks', 'Precedence Graph cycles indicate non-serializable schedules'],
              definitions: [
                'Atomicity: All-or-nothing execution of transaction operations.',
                'Serializability: The highest isolation level ensuring execution order equivalence with serial execution.'
              ],
              examFocus: [
                'Prove conflict serializability using a Precedence Graph (Expected 10-mark question).',
                'Differentiate between Strict 2PL and Rigorous 2PL with concurrency graphs.'
              ],
              studyQuestions: [
                'What is the phantom read anomaly and how does 2PL prevent it?',
                'Explain how the Wait-Die and Wound-Wait schemes achieve deadlock avoidance.'
              ],
              studyPlan: [
                'Review ACID property proofs and schedule equivalence (30 mins).',
                'Practice constructing precedence dependency graphs (45 mins).',
                'Memorize Lock-Compatibility matrices (15 mins).'
              ]
            }
          },
          {
            id: 'sample-2',
            name: 'MCA402-React-State-Architecture-and-Hooks.docx',
            type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
            status: 'COMPLETED',
            date: '3 days ago',
            storagePath: null,
            courseId: 'MCA402',
            extractionStatus: 'Successfully extracted 8,920 characters.',
            extractedTextPreview: 'Modern Frontend Architecture: React 18 Concurrent Features, Fiber reconciler, Custom Hooks lifecycle, State synchronization patterns using Context API and external stores...',
            analysisResult: {
              summary: 'Lecture overview on declarative UI patterns, fiber reconciliation, custom hook composition, and reactive state management in modern full-stack web applications.',
              keyTopics: ['React Fiber', 'Custom Hooks', 'Reconciliation', 'State Management'],
              importantConcepts: ['Immutability prevents stale state closures', 'Derived state computation using useMemo'],
              definitions: ['Reconciliation: The algorithm React uses to diff one tree with another to determine which parts need to be changed.'],
              examFocus: ['Explain the lifecycle and cleanup phases of useEffect vs useLayoutEffect.'],
              studyQuestions: ['Why should state mutations never be performed directly in React?'],
              studyPlan: ['Build a sample custom hook managing localStorage and window resize listeners.']
            }
          }
        ];
        setFiles(defaultSamples);
        setSelectedFile(defaultSamples[0]);
      } else {
        setFiles(fetchedFiles);
        setSelectedFile((prev: any) => {
          if (!prev) return fetchedFiles[0];
          return fetchedFiles.find(f => f.id === prev.id) || fetchedFiles[0];
        });
      }
    });

    return () => unsubscribe();
  }, [userData, isFacultyOrAdmin]);

  const validateFile = (file: File): string | null => {
    const allowedExtensions = ['.pdf', '.doc', '.docx', '.txt', '.md', '.csv'];
    const fileNameLower = file.name.toLowerCase();
    const hasValidExt = allowedExtensions.some(ext => fileNameLower.endsWith(ext));
    if (!hasValidExt) {
      return `Invalid file type. Supported formats are: ${allowedExtensions.join(', ')}`;
    }

    const MAX_SIZE_BYTES = 50 * 1024 * 1024; // 50MB
    if (file.size > MAX_SIZE_BYTES) {
      return `File size exceeds institutional limit (50MB). Your file is ${(file.size / (1024 * 1024)).toFixed(1)}MB.`;
    }

    if (file.size === 0) {
      return 'Selected file is empty (0 bytes).';
    }

    return null;
  };

  const processFile = async (file: File) => {
    if (!file || !userData) return;
    setUploadError(null);

    // 1. Validate file format and size
    const validationError = validateFile(file);
    if (validationError) {
      setUploadError(validationError);
      return;
    }

    setUploading(true);
    setUploadProgress('Uploading file to Supabase Storage (crescentconnect-files)...');

    try {
      // Predictable, secure path: materials/{userId}/{timestamp}-{safeFilename}
      const sanitizedName = file.name.replace(/[^a-zA-Z0-9.-]/g, '_');
      const filePath = `materials/${userData.id}/${Date.now()}-${sanitizedName}`;

      // 2. Upload directly to Supabase Storage
      const { data: uploadData, error: uploadErrorRes } = await supabase.storage
        .from('crescentconnect-files')
        .upload(filePath, file, {
          cacheControl: '3600',
          upsert: false
        });

      // 3. Confirm successful storage upload (Do NOT create Firestore metadata if upload fails!)
      if (uploadErrorRes || !uploadData) {
        const errorMsg = uploadErrorRes?.message || 'Storage service unreachable';
        console.error("Supabase Storage upload failed:", errorMsg);
        throw new Error(
          `Supabase upload failed: ${errorMsg}. ` +
          `(Note: Storage bucket 'crescentconnect-files' must be provisioned in the Supabase Console)`
        );
      }

      // 4. Create Firestore metadata ONLY after Supabase Storage upload succeeds
      setUploadProgress('Registering material metadata in Firestore...');
      const materialsRef = collection(db, 'study_materials');
      const docRef = await addDoc(materialsRef, {
        courseId: 'MCA401',
        staffId: userData.id,
        title: file.name,
        description: 'Uploaded via CrescentConnect File Analysis',
        fileName: file.name,
        fileType: file.type || 'application/octet-stream',
        fileSize: file.size,
        storagePath: uploadData.path || filePath,
        createdAt: Date.now(),
        syncStatus: 'COMPLETED',
        analysisStatus: 'EXTRACTING',
        extractionStatus: 'Pending text extraction...',
        analysisResult: null
      });

      // 5. Client-side extraction and AI reasoning
      try {
        setUploadProgress('Extracting text content from document...');
        const rawText = await extractTextFromFile(file);
        const normalizedText = normalizeText(rawText);

        if (!normalizedText) {
          await updateDoc(doc(db, 'study_materials', docRef.id), {
            analysisStatus: 'FAILED',
            extractionStatus: 'Text could not be extracted (possibly a rasterized scan or empty document).'
          });
          setUploading(false);
          return;
        }

        const preview = normalizedText.substring(0, 500) + (normalizedText.length > 500 ? '...' : '');

        await updateDoc(doc(db, 'study_materials', docRef.id), {
          analysisStatus: 'ANALYZING',
          extractionStatus: `Successfully extracted ${normalizedText.length} characters.`,
          extractedTextPreview: preview
        });

        setUploadProgress('Synthesizing academic concepts with Gemini AI...');
        const aiResult = await analyzeDocumentText(normalizedText);

        await updateDoc(doc(db, 'study_materials', docRef.id), {
          analysisStatus: 'COMPLETED',
          analysisResult: aiResult
        });

      } catch (extractionError: any) {
        console.error("Extraction/Analysis error:", extractionError);
        await updateDoc(doc(db, 'study_materials', docRef.id), {
          analysisStatus: 'FAILED',
          extractionStatus: `Failed: ${extractionError.message}`
        });
      }

      setUploading(false);
      setUploadProgress('');
      setActiveTab('analysis');
    } catch (error: any) {
      console.error("Upload error:", error);
      setUploading(false);
      setUploadProgress('');
      setUploadError(error.message || 'File upload failed. Please try again.');
    }
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) processFile(file);
    // Reset file input value to allow re-selecting the same file if needed
    e.target.value = '';
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
    const file = e.dataTransfer.files?.[0];
    if (file) processFile(file);
  };

  const handleDownload = async (f: any) => {
    if (!f.storagePath) {
      alert("This is a reference document cataloged for institutional demonstration.");
      return;
    }
    try {
      const { data, error } = await supabase.storage
        .from('crescentconnect-files')
        .createSignedUrl(f.storagePath, 3600);

      if (error) {
        console.error("Supabase Storage signed URL error:", error);
        alert(`Failed to retrieve file from storage: ${error.message}`);
        return;
      }

      if (data?.signedUrl) {
        window.open(data.signedUrl, '_blank', 'noopener,noreferrer');
      } else {
        alert("Failed to generate a valid download link. Please contact administrator.");
      }
    } catch (error: any) {
      console.error("Download error:", error);
      alert("Failed to create download link: " + error.message);
    }
  };

  const filteredFiles = useMemo(() => {
    return files.filter(f => {
      const matchesSearch = f.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            (f.courseId && f.courseId.toLowerCase().includes(searchQuery.toLowerCase()));
      const matchesStatus = statusFilter === 'ALL' || f.status === statusFilter;
      return matchesSearch && matchesStatus;
    });
  }, [files, searchQuery, statusFilter]);

  return (
    <div className="space-y-6">
      <PageHeader
        title="Course Materials & File Analysis"
        subtitle="Upload syllabus, lecture notes, or question banks for automated text extraction and structured AI academic breakdown."
        badge={<Badge variant="purple">Gemini & Supabase Powered</Badge>}
        actions={
          <div className="flex items-center gap-2">
            <label className="cursor-pointer inline-flex items-center gap-2 px-4 py-2.5 bg-crescent-navy-900 hover:bg-crescent-navy-800 dark:bg-emerald-600 dark:hover:bg-emerald-500 text-white rounded-xl text-xs sm:text-sm font-semibold shadow-sm transition-all">
              <UploadCloud className="w-4 h-4" />
              <span>{uploading ? 'Processing File...' : 'Upload Document'}</span>
              <input
                type="file"
                className="hidden"
                onChange={handleFileUpload}
                accept=".pdf,.doc,.docx,.txt,.md,.csv"
                disabled={uploading}
              />
            </label>
          </div>
        }
      />

      {/* Upload Drag & Drop Zone */}
      <div
        onDragOver={(e) => { e.preventDefault(); setIsDragOver(true); }}
        onDragLeave={() => setIsDragOver(false)}
        onDrop={handleDrop}
        className={`relative rounded-2xl border-2 border-dashed transition-all p-6 text-center ${
          isDragOver
            ? 'border-emerald-500 bg-emerald-50/50 dark:bg-emerald-950/20'
            : 'border-slate-300 dark:border-crescent-dark-border bg-white dark:bg-crescent-dark-card'
        }`}
      >
        <div className="flex flex-col items-center justify-center space-y-2">
          <div className="p-3 rounded-full bg-blue-50 text-crescent-navy-900 dark:bg-crescent-navy-900/60 dark:text-blue-400">
            <UploadCloud className="w-6 h-6" />
          </div>
          <h3 className="text-sm font-bold text-slate-800 dark:text-slate-200">
            Drag & drop course files here, or click upload
          </h3>
          <p className="text-xs text-slate-500 dark:text-slate-400 max-w-md">
            Supports PDF, DOCX, TXT, MD, and CSV (up to 50MB). Files are encrypted and stored in institutional Supabase cloud storage.
          </p>
          <div className="flex items-center gap-2 pt-1">
            <Badge variant="navy">PDF</Badge>
            <Badge variant="blue">DOCX</Badge>
            <Badge variant="slate">TXT / MD</Badge>
            <Badge variant="emerald">CSV</Badge>
          </div>
        </div>

        {uploading && (
          <div className="mt-4 p-3 rounded-xl bg-blue-50 dark:bg-crescent-navy-950/80 border border-blue-200 dark:border-crescent-navy-700 flex items-center justify-center gap-3">
            <div className="w-4 h-4 border-2 border-blue-300 border-t-crescent-navy-900 dark:border-t-emerald-400 rounded-full animate-spin" />
            <span className="text-xs font-semibold text-crescent-navy-900 dark:text-blue-300">
              {uploadProgress}
            </span>
          </div>
        )}

        {uploadError && (
          <div className="mt-4 p-3.5 rounded-xl bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-900/60 flex items-start justify-between gap-3 text-left">
            <div className="flex items-start gap-2.5">
              <XCircle className="w-4 h-4 text-rose-600 dark:text-rose-400 shrink-0 mt-0.5" />
              <div>
                <p className="text-xs font-bold text-rose-800 dark:text-rose-300">
                  Upload Failed
                </p>
                <p className="text-xs text-rose-700 dark:text-rose-400 mt-0.5">
                  {uploadError}
                </p>
              </div>
            </div>
            <button
              onClick={() => setUploadError(null)}
              className="text-xs font-semibold text-rose-600 dark:text-rose-400 hover:text-rose-800 dark:hover:text-rose-200 p-1"
            >
              Dismiss
            </button>
          </div>
        )}
      </div>

      {/* Mobile Tab Switcher */}
      <div className="flex lg:hidden rounded-xl bg-slate-200/70 dark:bg-crescent-navy-950 p-1">
        <button
          onClick={() => setActiveTab('files')}
          className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all ${
            activeTab === 'files'
              ? 'bg-white text-crescent-navy-900 dark:bg-crescent-dark-card dark:text-white shadow-sm'
              : 'text-slate-500 hover:text-slate-700 dark:text-slate-400'
          }`}
        >
          Files List ({filteredFiles.length})
        </button>
        <button
          onClick={() => setActiveTab('analysis')}
          className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all ${
            activeTab === 'analysis'
              ? 'bg-white text-crescent-navy-900 dark:bg-crescent-dark-card dark:text-white shadow-sm'
              : 'text-slate-500 hover:text-slate-700 dark:text-slate-400'
          }`}
        >
          AI Analysis Breakdown
        </button>
      </div>

      {/* Split-Pane Layout (2-Column on Desktop) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Left Pane: Files List (5 Cols) */}
        <div className={`lg:col-span-5 space-y-3 ${activeTab === 'analysis' ? 'hidden lg:block' : 'block'}`}>
          <div className="bg-white dark:bg-crescent-dark-card rounded-xl border border-slate-200/80 dark:border-crescent-dark-border p-3 shadow-sm flex flex-col gap-2.5">
            {/* Search and filter */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
              <input
                type="text"
                placeholder="Search course materials..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-9 pr-3 py-2 text-xs bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200 dark:border-crescent-navy-700 rounded-lg text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-crescent-navy-900"
              />
            </div>

            <div className="flex items-center justify-between text-xs px-1">
              <span className="text-slate-500 dark:text-slate-400 font-medium">
                {filteredFiles.length} documents
              </span>
              <div className="flex items-center gap-1.5">
                <Filter className="w-3 h-3 text-slate-400" />
                <select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value)}
                  className="text-xs bg-transparent border-none text-slate-600 dark:text-slate-300 font-medium focus:outline-none"
                >
                  <option value="ALL">All Status</option>
                  <option value="COMPLETED">Analyzed</option>
                  <option value="ANALYZING">Processing</option>
                  <option value="FAILED">Failed</option>
                </select>
              </div>
            </div>
          </div>

          <div className="space-y-2 max-h-[600px] overflow-y-auto pr-1">
            {filteredFiles.length > 0 ? (
              filteredFiles.map((f) => {
                const isSelected = selectedFile?.id === f.id;
                return (
                  <div
                    key={f.id}
                    onClick={() => {
                      setSelectedFile(f);
                      setActiveTab('analysis');
                    }}
                    className={`p-3.5 rounded-xl border transition-all cursor-pointer flex items-start justify-between gap-3 ${
                      isSelected
                        ? 'border-crescent-navy-900 dark:border-emerald-500 bg-crescent-navy-50/50 dark:bg-crescent-navy-950/60 shadow-sm'
                        : 'border-slate-200/80 dark:border-crescent-dark-border bg-white dark:bg-crescent-dark-card hover:border-slate-300 dark:hover:border-crescent-navy-700'
                    }`}
                  >
                    <div className="flex items-start gap-3 min-w-0">
                      <div className={`p-2.5 rounded-lg shrink-0 ${
                        isSelected
                          ? 'bg-crescent-navy-900 text-white dark:bg-emerald-500 dark:text-white'
                          : 'bg-slate-100 dark:bg-crescent-navy-900/60 text-crescent-navy-900 dark:text-blue-400'
                      }`}>
                        <FileText className="w-4 h-4" />
                      </div>
                      <div className="min-w-0">
                        <p className="text-xs sm:text-sm font-semibold text-slate-900 dark:text-white truncate">
                          {f.name}
                        </p>
                        <div className="flex items-center gap-2 mt-1 text-[11px] text-slate-500 dark:text-slate-400">
                          <span className="font-semibold text-emerald-600 dark:text-emerald-400">{f.courseId}</span>
                          <span>•</span>
                          <span>{f.date}</span>
                        </div>
                      </div>
                    </div>

                    <div className="flex flex-col items-end gap-1.5 shrink-0">
                      <Badge
                        variant={
                          f.status === 'COMPLETED' ? 'emerald' :
                          f.status === 'ANALYZING' || f.status === 'EXTRACTING' ? 'amber' :
                          f.status === 'FAILED' ? 'rose' : 'slate'
                        }
                      >
                        {f.status === 'COMPLETED' ? 'Analyzed' :
                         f.status === 'ANALYZING' ? 'AI Analyzing' :
                         f.status === 'EXTRACTING' ? 'Extracting' : f.status}
                      </Badge>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDownload(f);
                        }}
                        className="p-1 rounded-md text-slate-400 hover:text-crescent-navy-900 dark:hover:text-emerald-400 transition-colors"
                        title="Download file"
                      >
                        <Download className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>
                );
              })
            ) : (
              <EmptyState
                icon={FileText}
                title="No Documents Found"
                description="No materials match your query. Upload a course syllabus or notes to get started."
              />
            )}
          </div>
        </div>

        {/* Right Pane: AI Analysis & Concept Breakdown (7 Cols) */}
        <div className={`lg:col-span-7 ${activeTab === 'files' ? 'hidden lg:block' : 'block'}`}>
          <div className="bg-white dark:bg-crescent-dark-card rounded-2xl border border-slate-200/80 dark:border-crescent-dark-border shadow-sm p-5 sm:p-6 min-h-[500px]">
            {!selectedFile ? (
              <div className="h-full py-20 flex flex-col items-center justify-center text-center">
                <BrainCircuit className="w-12 h-12 text-slate-300 dark:text-slate-600 mb-3" />
                <h3 className="text-base font-bold text-slate-800 dark:text-slate-200">
                  Select a document to inspect AI breakdown
                </h3>
                <p className="text-xs text-slate-500 dark:text-slate-400 max-w-sm mt-1">
                  Choose any uploaded lecture material from the list to view its summary, extracted concepts, key definitions, and exam focus points.
                </p>
              </div>
            ) : selectedFile.status === 'ANALYZING' || selectedFile.status === 'EXTRACTING' ? (
              <div className="py-20 flex flex-col items-center justify-center text-center space-y-3">
                <div className="w-10 h-10 border-3 border-purple-200 border-t-purple-600 rounded-full animate-spin" />
                <h3 className="text-base font-bold text-slate-800 dark:text-slate-200">
                  Document Intelligence in Progress
                </h3>
                <p className="text-xs text-slate-500 dark:text-slate-400 max-w-sm">
                  {selectedFile.status === 'EXTRACTING'
                    ? 'Extracting raw textual content and formatting...'
                    : 'Gemini AI is parsing academic terminology and synthesizing study questions...'}
                </p>
              </div>
            ) : selectedFile.status === 'FAILED' ? (
              <div className="py-16 flex flex-col items-center justify-center text-center space-y-3">
                <AlertCircle className="w-10 h-10 text-rose-500" />
                <h3 className="text-base font-bold text-slate-800 dark:text-slate-200">
                  Extraction or Analysis Failed
                </h3>
                <p className="text-xs text-rose-600 dark:text-rose-400 max-w-sm">
                  {selectedFile.extractionStatus || 'Could not parse document structure. Please verify file format.'}
                </p>
              </div>
            ) : (
              <div className="space-y-6">
                {/* Header info */}
                <div className="pb-4 border-b border-slate-100 dark:border-crescent-dark-border flex items-start justify-between gap-4">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-bold text-emerald-600 dark:text-emerald-400">{selectedFile.courseId}</span>
                      <span className="text-slate-300">•</span>
                      <span className="text-xs text-slate-400">{selectedFile.date}</span>
                    </div>
                    <h2 className="text-lg font-bold text-slate-900 dark:text-white mt-1">
                      {selectedFile.name}
                    </h2>
                  </div>
                  <button
                    onClick={() => handleDownload(selectedFile)}
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-slate-200 dark:border-crescent-navy-700 text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-crescent-navy-900/40 transition-colors"
                  >
                    <Download className="w-3.5 h-3.5" />
                    <span>Download</span>
                  </button>
                </div>

                {/* Extraction badge status */}
                {selectedFile.extractionStatus && (
                  <div className="flex items-center gap-2 p-2.5 rounded-xl bg-emerald-50 dark:bg-emerald-950/30 border border-emerald-200/70 dark:border-emerald-800/40 text-xs text-emerald-700 dark:text-emerald-300">
                    <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600" />
                    <span>{selectedFile.extractionStatus}</span>
                  </div>
                )}

                {/* AI Summary */}
                {selectedFile.analysisResult?.summary && (
                  <div>
                    <h4 className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                      <Sparkles className="w-3.5 h-3.5 text-purple-500" />
                      Executive Document Summary
                    </h4>
                    <div className="p-4 rounded-xl bg-slate-50 dark:bg-crescent-navy-950/40 border border-slate-200/80 dark:border-crescent-navy-800/60 text-xs sm:text-sm text-slate-700 dark:text-slate-200 leading-relaxed">
                      {selectedFile.analysisResult.summary}
                    </div>
                  </div>
                )}

                {/* Key Concepts Extracted */}
                {selectedFile.analysisResult?.keyTopics && (
                  <div>
                    <h4 className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                      <BookOpen className="w-3.5 h-3.5 text-blue-500" />
                      Extracted Key Topics & Concepts
                    </h4>
                    <div className="flex flex-wrap gap-2">
                      {selectedFile.analysisResult.keyTopics.map((topic: string, i: number) => (
                        <Badge key={i} variant="purple" size="md">
                          {topic}
                        </Badge>
                      ))}
                    </div>
                  </div>
                )}

                {/* Important Definitions */}
                {selectedFile.analysisResult?.definitions && selectedFile.analysisResult.definitions.length > 0 && (
                  <div>
                    <h4 className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2">
                      Formal Definitions
                    </h4>
                    <div className="space-y-2">
                      {selectedFile.analysisResult.definitions.map((def: string, i: number) => (
                        <div key={i} className="p-3 rounded-lg bg-blue-50/50 dark:bg-blue-950/20 border border-blue-100 dark:border-blue-900/30 text-xs text-slate-700 dark:text-slate-300">
                          {def}
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Potential Exam Questions */}
                {selectedFile.analysisResult?.studyQuestions && selectedFile.analysisResult.studyQuestions.length > 0 && (
                  <div>
                    <h4 className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                      <HelpCircle className="w-3.5 h-3.5 text-amber-500" />
                      Potential Exam & Viva Questions
                    </h4>
                    <ul className="space-y-2">
                      {selectedFile.analysisResult.studyQuestions.map((q: string, i: number) => (
                        <li key={i} className="text-xs sm:text-sm text-slate-700 dark:text-slate-300 pl-4 relative before:absolute before:left-0 before:top-2 before:w-1.5 before:h-1.5 before:bg-amber-500 before:rounded-full">
                          {q}
                        </li>
                      ))}
                    </ul>
                  </div>
                )}

                {/* Suggested Study Roadmap */}
                {selectedFile.analysisResult?.studyPlan && selectedFile.analysisResult.studyPlan.length > 0 && (
                  <div>
                    <h4 className="text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                      <ListOrdered className="w-3.5 h-3.5 text-emerald-500" />
                      Recommended Study Roadmap
                    </h4>
                    <div className="space-y-2">
                      {selectedFile.analysisResult.studyPlan.map((step: string, i: number) => (
                        <div key={i} className="flex items-start gap-2.5 text-xs text-slate-700 dark:text-slate-300">
                          <span className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-400 font-bold text-[10px]">
                            {i + 1}
                          </span>
                          <span className="mt-0.5">{step}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Raw Extracted Text Preview */}
                {selectedFile.extractedTextPreview && (
                  <div className="pt-2">
                    <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">
                      Extracted Text Stream Preview
                    </h4>
                    <div className="text-[11px] font-mono text-slate-600 dark:text-slate-400 bg-slate-50 dark:bg-crescent-navy-950/60 p-3.5 rounded-xl border border-slate-200 dark:border-crescent-navy-800 max-h-36 overflow-y-auto whitespace-pre-wrap">
                      {selectedFile.extractedTextPreview}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
