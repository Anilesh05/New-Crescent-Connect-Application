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
        MessageEntity::class,
        AttendanceSessionEntity::class,
        AttendanceRecordEntity::class,
        CRPermissionEntity::class,
        AnnouncementEntity::class,
        StudyMaterialEntity::class,
        AssignmentEntity::class,
        AssignmentCompletionEntity::class,
        NotificationEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class CrescentDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun timetableDao(): TimetableDao
    abstract fun chatDao(): ChatDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun academicDao(): AcademicDao

    companion object {
        @Volatile
        private var INSTANCE: CrescentDatabase? = null

        val MIGRATION_5_6 = object : androidx.room.migration.Migration(5, 6) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // No schema changes in Phase 14, just bumping version to solidify schema
                // and remove destructive migration.
            }
        }

        fun getDatabase(context: Context): CrescentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CrescentDatabase::class.java,
                    "crescent_database"
                )
                .addMigrations(MIGRATION_5_6)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
