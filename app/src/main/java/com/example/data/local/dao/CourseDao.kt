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
