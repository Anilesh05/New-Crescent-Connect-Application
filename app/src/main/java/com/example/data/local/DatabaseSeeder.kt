package com.example.data.local

import com.example.data.local.entity.*
import com.example.domain.model.AttendanceStatus
import com.example.domain.model.SyncStatus

object DatabaseSeeder {
    fun seedDatabase(db: CrescentDatabase) {
        val users = listOf(
            UserEntity("U1", "Anilesh Demo", "student@crescentconnect.demo", "Student@123", "STUDENT", "DEMO2026001", "MCA", "Computer Applications", "II", "A", null, "2023-2024", null, true, "ACTIVE", 1, System.currentTimeMillis()),
            UserEntity("U2", "Dr. Kumar Demo", "staff@crescentconnect.demo", "Staff@123", "STAFF", null, null, "Computer Applications", null, null, "Assistant Professor", "2023-2024", null, true, "ACTIVE", 1, System.currentTimeMillis()),
            UserEntity("U3", "System Administrator", "admin@crescentconnect.demo", "Admin@123", "ADMIN", null, null, null, null, null, "System Admin", null, null, true, "ACTIVE", 1, System.currentTimeMillis()),
            UserEntity("U4", "CR Student Demo", "cr@crescentconnect.demo", "CR@123", "CR", "DEMO2026002", "MCA", "Computer Applications", "II", "A", null, "2023-2024", null, true, "ACTIVE", 1, System.currentTimeMillis()),
            UserEntity("U5", "Sarah Ahmed", "sarah@student.crescent.edu", "password", "STUDENT", "220071601006", "MCA", "Computer Applications", "Semester IV", "A", null, "2023-2024", null, true, "ACTIVE", 1, System.currentTimeMillis()),
            UserEntity("U6", "Dr. Priya Demo", "advisor@crescentconnect.demo", "Advisor@123", "CLASS_ADVISER", null, null, "Computer Applications", null, null, "Assistant Professor", "2023-2024", null, true, "ACTIVE", 1, System.currentTimeMillis())
        )
        
        val courses = listOf(
            CourseEntity("C1", "MCA401", "Database Management Systems", "U2", "MCA", "Semester IV", "A", "2023-2024"),
            CourseEntity("C2", "MCA402", "Web Development", "U2", "MCA", "Semester IV", "A", "2023-2024"),
            CourseEntity("C3", "MCA403", "Python Programming", "U2", "MCA", "Semester IV", "A", "2023-2024")
        )
        
        val enrollments = listOf(
            EnrollmentEntity("U1", "C1"),
            EnrollmentEntity("U1", "C2"),
            EnrollmentEntity("U1", "C3"),
            EnrollmentEntity("U4", "C1"),
            EnrollmentEntity("U4", "C2"),
            EnrollmentEntity("U5", "C1")
        )
        
        val timetables = listOf(
            TimetableEntity("T1", "C1", "Monday", "10:00 AM", "11:00 AM", "Room 301", 2, "A"),
            TimetableEntity("T2", "C2", "Tuesday", "11:00 AM", "12:00 PM", "Lab 2", 3, "A")
        )
        
        val conversations = listOf(
            ConversationEntity("Conv1", "COURSE", "C1", null, null)
        )
        
        val messages = listOf(
            MessageEntity("M1", "Conv1", "U1", "STUDENT", "Has anyone completed the assignment?", System.currentTimeMillis() - 3600000, null, false, "SYNCED"),
            MessageEntity("M2", "Conv1", "U2", "STAFF", "Yes, it's due today by 5 PM.", System.currentTimeMillis() - 3000000, null, false, "SYNCED"),
            MessageEntity("M3", "Conv1", "U4", "STUDENT", "Will we have a quiz next week?", System.currentTimeMillis() - 2000000, null, false, "SYNCED")
        )
        
        val attendanceSessions = listOf(
            AttendanceSessionEntity("S1", "C1", "2026-08-28", 2, "U2", "STAFF", SyncStatus.SYNCED.name, System.currentTimeMillis() - 172800000),
            AttendanceSessionEntity("S2", "C1", "2026-08-29", 2, "U2", "STAFF", SyncStatus.SYNCED.name, System.currentTimeMillis() - 86400000)
        )
        
        val attendanceRecords = listOf(
            AttendanceRecordEntity("R1", "S1", "U1", AttendanceStatus.PRESENT.name, System.currentTimeMillis(), SyncStatus.SYNCED.name),
            AttendanceRecordEntity("R2", "S1", "U4", AttendanceStatus.PRESENT.name, System.currentTimeMillis(), SyncStatus.SYNCED.name),
            AttendanceRecordEntity("R3", "S1", "U5", AttendanceStatus.ABSENT.name, System.currentTimeMillis(), SyncStatus.SYNCED.name),
            AttendanceRecordEntity("R4", "S2", "U1", AttendanceStatus.ABSENT.name, System.currentTimeMillis(), SyncStatus.SYNCED.name),
            AttendanceRecordEntity("R5", "S2", "U4", AttendanceStatus.PRESENT.name, System.currentTimeMillis(), SyncStatus.SYNCED.name),
            AttendanceRecordEntity("R6", "S2", "U5", AttendanceStatus.PRESENT.name, System.currentTimeMillis(), SyncStatus.SYNCED.name)
        )
        
        val crPermissions = listOf(
            CRPermissionEntity("P1", "U4", "C2", "2026-08-30", 3, System.currentTimeMillis(), System.currentTimeMillis() + 86400000, "U2", true, "SYNCED")
        )

        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            val userDao = db.userDao()
            if (userDao.getUserByEmail("student@crescentconnect.demo") == null) {
                userDao.insertAll(users)
                db.courseDao().insertAll(courses)
                db.courseDao().insertEnrollments(enrollments)
                db.timetableDao().insertAll(timetables)
                db.chatDao().insertConversations(conversations)
                db.chatDao().insertMessages(messages)
                db.attendanceDao().insertSession(attendanceSessions[0])
                db.attendanceDao().insertSession(attendanceSessions[1])
                db.attendanceDao().insertRecords(attendanceRecords)
                db.attendanceDao().insertPermission(crPermissions[0])
            }
        }
    }
}
