package com.example.data.repository

import com.example.data.local.SessionManager
import com.example.data.local.dao.CourseDao
import com.example.data.local.dao.TimetableDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.CourseEntity
import com.example.data.local.entity.TimetableEntity
import com.example.data.local.entity.UserEntity
import com.example.domain.model.Course
import com.example.domain.model.Role
import com.example.domain.model.Timetable
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.CourseRepository
import com.example.domain.repository.TimetableRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    email = email,
    role = Role.valueOf(role),
    registerNumber = registerNumber,
    programme = programme,
    department = department,
    semester = semester,
    section = section,
    designation = designation,
    academicYear = academicYear,
    profileImageUri = profileImageUri,
    isActive = isActive,
    status = status,
    idVersion = idVersion,
    updatedAt = updatedAt,
    skills = skills,
    certifications = certifications,
    projects = projects,
    internships = internships,
    achievements = achievements
)

fun CourseEntity.toDomain() = Course(
    id = id,
    subjectCode = subjectCode,
    subjectName = subjectName,
    facultyId = facultyId,
    programme = programme,
    semester = semester,
    section = section,
    academicYear = academicYear
)

fun TimetableEntity.toDomain() = Timetable(
    id = id,
    courseId = courseId,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime,
    room = room,
    periodNumber = periodNumber,
    section = section
)


class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getUserById(id: String): User? {
        return userDao.getUserById(id)?.toDomain()
    }

    override fun getAllStudents(): Flow<List<User>> {
        return userDao.getAllStudents().map { list -> list.map { it.toDomain() } }
    }

    override fun getAllStaff(): Flow<List<User>> {
        return userDao.getAllStaff().map { list -> list.map { it.toDomain() } }
    }
}

class CourseRepositoryImpl(
    private val courseDao: CourseDao
) : CourseRepository {
    override fun getCoursesForStudent(studentId: String): Flow<List<Course>> {
        return courseDao.getCoursesForStudent(studentId).map { list -> list.map { it.toDomain() } }
    }

    override fun getCoursesForFaculty(facultyId: String): Flow<List<Course>> {
        return courseDao.getCoursesForFaculty(facultyId).map { list -> list.map { it.toDomain() } }
    }
}

class TimetableRepositoryImpl(
    private val timetableDao: TimetableDao
) : TimetableRepository {
    override fun getTimetableForStudent(studentId: String): Flow<List<Timetable>> {
        return timetableDao.getTimetableForStudent(studentId).map { list -> list.map { it.toDomain() } }
    }

    override fun getTimetableForFaculty(facultyId: String): Flow<List<Timetable>> {
        return timetableDao.getTimetableForFaculty(facultyId).map { list -> list.map { it.toDomain() } }
    }
}
