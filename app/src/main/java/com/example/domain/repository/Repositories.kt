package com.example.domain.repository

import com.example.domain.model.Course
import com.example.domain.model.Timetable
import com.example.domain.model.User
import com.example.domain.model.AttendanceSession
import com.example.domain.model.AttendanceRecord
import com.example.domain.model.CRPermission
import com.example.domain.model.*

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String):Result<User>
    suspend fun logout()
    fun getSessionUserId(): Flow<String?>
}
interface UserRepository {
    suspend fun getUserById(id: String): User?
    fun getAllStudents(): Flow<List<User>>
    fun getAllStaff(): Flow<List<User>>
}
interface CourseRepository {
    fun getCoursesForStudent(studentId: String): Flow<List<Course>>
    fun getCoursesForFaculty(facultyId: String): Flow<List<Course>>
}
interface TimetableRepository {
    fun getTimetableForStudent(studentId: String): Flow<List<Timetable>>
    fun getTimetableForFaculty(facultyId: String): Flow<List<Timetable>>
}
interface AttendanceRepository {
    suspend fun saveAttendance(session: AttendanceSession, records: List<AttendanceRecord>)
    suspend fun saveSession(session: AttendanceSession)
    suspend fun saveRecord(record: AttendanceRecord)
    fun getSessionsForCourse(courseId: String): Flow<List<AttendanceSession>>
    fun getAllSessions(): Flow<List<AttendanceSession>>
    suspend fun getSessionByDetails(courseId: String, date: String, periodNumber: Int): AttendanceSession?
    fun getRecordsForSessionFlow(sessionId: String): Flow<List<AttendanceRecord>>
    suspend fun getRecordsForSession(sessionId: String): List<AttendanceRecord>
    fun getRecordsForStudent(studentId: String): Flow<List<AttendanceRecord>>
    fun getAllRecords(): Flow<List<AttendanceRecord>>
    fun getActivePermissionsForCR(crId: String): Flow<List<CRPermission>>
    suspend fun grantCRPermission(permission: CRPermission)
    suspend fun getPermissionById(id: String): CRPermission?
}

interface ChatRepository {
    suspend fun getCourseConversation(courseId: String): Conversation
    suspend fun getDirectConversation(participant1Id: String, participant2Id: String): Conversation
    fun getMessages(conversationId: String): Flow<List<Message>>
    suspend fun sendMessage(message: Message)
}

interface AcademicRepository {
    fun getAnnouncementsForCourse(courseId: String): Flow<List<Announcement>>
    suspend fun createAnnouncement(announcement: Announcement)
    suspend fun deleteAnnouncement(id: String)
    
    fun getStudyMaterialsForCourse(courseId: String): Flow<List<StudyMaterial>>
    suspend fun createStudyMaterial(material: StudyMaterial)
    suspend fun deleteStudyMaterial(id: String)
    
    fun getAssignmentsForCourse(courseId: String): Flow<List<Assignment>>
    suspend fun createAssignment(assignment: Assignment)
    suspend fun deleteAssignment(id: String)
    
    fun getStudentCompletions(studentId: String): Flow<List<AssignmentCompletion>>
    suspend fun markAssignmentCompleted(completion: AssignmentCompletion)
    suspend fun getAssignmentCompletion(assignmentId: String, studentId: String): AssignmentCompletion?
    
    fun getNotificationsForUser(userId: String): Flow<List<Notification>>
    suspend fun markNotificationAsRead(id: String)
    suspend fun createNotification(notification: Notification)
}
