export type Role = 'ADMIN' | 'STAFF' | 'STUDENT' | 'CR' | 'CLASS_ADVISER';

export interface User {
  id: string;
  name: string;
  email: string;
  role: Role;
  registerNumber?: string;
  programme?: string;
  department?: string;
  semester?: string;
  section?: string;
  designation?: string;
  academicYear?: string;
  profileImageUri?: string;
  isActive: boolean;
  status: string;
}

export type SyncStatus = 'PENDING' | 'SYNCED' | 'FAILED';
export type AttendanceStatus = 'PRESENT' | 'ABSENT';

export interface AttendanceSession {
  id: string;
  courseId: string;
  date: string;
  periodNumber: number;
  markedBy: string;
  markerRole: Role;
  syncStatus: SyncStatus;
  timestamp: number;
  isSelfAttendanceEnabled: boolean;
  startTime?: number;
  endTime?: number;
  latitude?: number;
  longitude?: number;
  allowedRadiusMeters?: number;
}

export interface AttendanceRecord {
  id: string;
  sessionId: string;
  studentId: string;
  status: AttendanceStatus;
  timestamp: number;
  syncStatus: SyncStatus;
  verificationMethod: string;
  verifiedAt?: number;
  distanceFromCenter?: number;
  locationAccuracy?: number;
}

export interface Course {
  id: string;
  code: string;
  name: string;
  facultyId: string;
  credits: number;
  semester: string;
  department: string;
}

export interface FileMetadata {
  id: string;
  fileId: string;
  fileName: string;
  storagePath: string;
  uploadedBy: string;
  courseId: string;
  subjectId?: string;
  fileType: string;
  fileSize: number;
  createdAt: number;
  updatedAt: number;
  analysisStatus: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'UNSUPPORTED';
  aiSummary?: string;
  aiImportantTopics?: string[];
  aiQuestions?: string[];
}
