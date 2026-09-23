package com.example.domain.repository

interface AiService {
    suspend fun generateResponse(prompt: String): String
}
