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
