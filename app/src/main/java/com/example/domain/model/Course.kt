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
