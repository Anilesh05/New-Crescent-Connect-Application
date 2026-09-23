package com.example.data.repository

import com.example.domain.repository.AiService
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiServiceImpl(private val apiKey: String) : AiService {
    
    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
            }
        )
    }

    override suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "YOUR_API_KEY") {
            return@withContext "AI OFFLINE / UNAVAILABLE\n\nAI functionality is disabled.\n\nRaw Data Context:\n" + prompt.substringAfter("Context:")
        }
        try {
            val response = model.generateContent(prompt)
            response.text ?: "I am sorry, I couldn't generate a response."
        } catch (e: Exception) {
            "AI OFFLINE / UNAVAILABLE\n\nError: ${e.message}\n\nRaw Data Context:\n" + prompt.substringAfter("Context:")
        }
    }
}
