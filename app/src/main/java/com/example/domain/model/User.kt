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
    val isActive: Boolean = true,
    val status: String = "ACTIVE",
    val idVersion: Int = 1,
    val updatedAt: Long? = null,
    val skills: String? = null,
    val certifications: String? = null,
    val projects: String? = null,
    val internships: String? = null,
    val achievements: String? = null
)
