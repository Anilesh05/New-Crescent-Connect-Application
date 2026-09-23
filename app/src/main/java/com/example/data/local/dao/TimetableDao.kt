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
