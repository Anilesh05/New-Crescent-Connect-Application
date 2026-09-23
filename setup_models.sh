#!/bin/bash
mkdir -p app/src/main/java/com/example/domain/model

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
