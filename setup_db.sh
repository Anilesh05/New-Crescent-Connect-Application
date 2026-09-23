#!/bin/bash

# 1. Update Domain Models
cat << 'INNER_EOF' > app/src/main/java/com/example/domain/model/User.kt
package com.example.domain.model

enum class Role {
    ADMIN,
    STAFF,
    STUDENT,
    CR,
    CLASS_ADVISER
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val registerNumber: String? = null,
    val programme: String? = null,
    val department: String? = null,
    val semester: String? = null,
    val section: String? = null,
    val designation: String? = null,
    val academicYear: String? = null,
    val profileImageUri: String? = null,
    val isActive: Boolean = true
)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/domain/model/Course.kt
package com.example.domain.model

data class Course(
    val id: String,
    val subjectCode: String,
    val subjectName: String,
    val facultyId: String,
    val programme: String,
    val semester: String,
    val section: String,
    val academicYear: String
)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/domain/model/Timetable.kt
package com.example.domain.model

data class Timetable(
    val id: String,
    val courseId: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val periodNumber: Int,
    val section: String
)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/domain/model/Chat.kt
package com.example.domain.model

data class Conversation(
    val id: String,
    val courseId: String
)

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val messageText: String,
    val timestamp: Long,
    val attachmentUri: String? = null,
    val isRead: Boolean = false
)
INNER_EOF

# 2. Room Entities
cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/Entities.kt
package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String,
    val registerNumber: String?,
    val programme: String?,
    val department: String?,
    val semester: String?,
    val section: String?,
    val designation: String?,
    val academicYear: String?,
    val profileImageUri: String?,
    val isActive: Boolean
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val subjectCode: String,
    val subjectName: String,
    val facultyId: String,
    val programme: String,
    val semester: String,
    val section: String,
    val academicYear: String
)

@Entity(tableName = "enrollments", primaryKeys = ["studentId", "courseId"])
data class EnrollmentEntity(
    val studentId: String,
    val courseId: String
)

@Entity(tableName = "timetables")
data class TimetableEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val periodNumber: Int,
    val section: String
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val courseId: String
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val messageText: String,
    val timestamp: Long,
    val attachmentUri: String?,
    val isRead: Boolean
)
INNER_EOF

# 3. DAOs
cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/dao/UserDao.kt
package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'STUDENT'")
    fun getAllStudents(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role IN ('STAFF', 'CLASS_ADVISER')")
    fun getAllStaff(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)
}
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/dao/CourseDao.kt
package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CourseEntity
import com.example.data.local.entity.EnrollmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT c.* FROM courses c INNER JOIN enrollments e ON c.id = e.courseId WHERE e.studentId = :studentId")
    fun getCoursesForStudent(studentId: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE facultyId = :facultyId")
    fun getCoursesForFaculty(facultyId: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollments(enrollments: List<EnrollmentEntity>)
}
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/dao/TimetableDao.kt
package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.TimetableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Query("SELECT t.* FROM timetables t INNER JOIN enrollments e ON t.courseId = e.courseId WHERE e.studentId = :studentId")
    fun getTimetableForStudent(studentId: String): Flow<List<TimetableEntity>>
    
    @Query("SELECT * FROM timetables WHERE courseId IN (SELECT id FROM courses WHERE facultyId = :facultyId)")
    fun getTimetableForFaculty(facultyId: String): Flow<List<TimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timetables: List<TimetableEntity>)
}
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/dao/ChatDao.kt
package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations WHERE courseId = :courseId LIMIT 1")
    suspend fun getConversationByCourseId(courseId: String): ConversationEntity?

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)
}
INNER_EOF

# 4. Database Class
cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/CrescentDatabase.kt
package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        EnrollmentEntity::class,
        TimetableEntity::class,
        ConversationEntity::class,
        MessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CrescentDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun timetableDao(): TimetableDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: CrescentDatabase? = null

        fun getDatabase(context: Context): CrescentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CrescentDatabase::class.java,
                    "crescent_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
INNER_EOF

# 5. Database Seeder
cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/DatabaseSeeder.kt
package com.example.data.local

import com.example.data.local.entity.*

object DatabaseSeeder {
    fun seedDatabase(db: CrescentDatabase) {
        val users = listOf(
            UserEntity("U1", "Anilesh Moses", "anilesh@student.crescent.edu", "password", "STUDENT", "220071601004", "MCA", "Computer Applications", "Semester IV", "A", null, "2023-2024", null, true),
            UserEntity("U2", "Dr. Rahmath S", "rahmath@staff.crescent.edu", "password", "STAFF", null, null, "Computer Applications", null, null, "Professor", "2023-2024", null, true),
            UserEntity("U3", "Admin User", "admin@crescent.edu", "password", "ADMIN", null, null, null, null, null, "System Admin", null, null, true),
            UserEntity("U4", "Rahul Sharma", "rahul@student.crescent.edu", "password", "CR", "220071601005", "MCA", "Computer Applications", "Semester IV", "A", null, "2023-2024", null, true)
        )

        val courses = listOf(
            CourseEntity("C1", "MCA401", "Database Management Systems", "U2", "MCA", "Semester IV", "A", "2023-2024"),
            CourseEntity("C2", "MCA402", "Web Development", "U2", "MCA", "Semester IV", "A", "2023-2024")
        )

        val enrollments = listOf(
            EnrollmentEntity("U1", "C1"),
            EnrollmentEntity("U1", "C2"),
            EnrollmentEntity("U4", "C1")
        )

        val timetables = listOf(
            TimetableEntity("T1", "C1", "Monday", "10:00 AM", "11:00 AM", "Room 301", 2, "A"),
            TimetableEntity("T2", "C2", "Tuesday", "11:00 AM", "12:00 PM", "Lab 2", 3, "A")
        )

        val conversations = listOf(
            ConversationEntity("Conv1", "C1")
        )

        val messages = listOf(
            MessageEntity("M1", "Conv1", "U1", "Has anyone completed the assignment?", System.currentTimeMillis() - 3600000, null, false),
            MessageEntity("M2", "Conv1", "U2", "Yes, it's due today by 5 PM.", System.currentTimeMillis() - 3000000, null, false),
            MessageEntity("M3", "Conv1", "U4", "Will we have a quiz next week?", System.currentTimeMillis() - 2000000, null, false)
        )

        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            val userDao = db.userDao()
            if (userDao.getUserByEmail("anilesh@student.crescent.edu") == null) {
                userDao.insertAll(users)
                db.courseDao().insertAll(courses)
                db.courseDao().insertEnrollments(enrollments)
                db.timetableDao().insertAll(timetables)
                db.chatDao().insertConversations(conversations)
                db.chatDao().insertMessages(messages)
            }
        }
    }
}
INNER_EOF

chmod +x setup_db.sh
./setup_db.sh
