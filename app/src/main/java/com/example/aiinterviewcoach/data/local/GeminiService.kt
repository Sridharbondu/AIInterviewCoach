package com.example.aiinterviewcoach.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val apiKey = "AIzaSyBfxlWQg8FMVUIWQ741nq0icjCeK9hGsEk"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=$apiKey"

    suspend fun generateQuestions(
        role: String,
        difficulty: String,
        count: Int = 5
    ): List<String> = withContext(Dispatchers.IO) {

        val prompt = """
            You are an expert technical interviewer.
            
            Generate exactly $count interview questions for:
            Role: $role
            Difficulty: $difficulty
            
            Rules:
            - Questions must be technical and role-specific
            - Each question on a new line
            - Start each question with a number (1. 2. 3.)
            - No explanations, just questions
            - Mix conceptual and practical questions
            
            Questions:
        """.trimIndent()

        val response = callGemini(prompt)

        // Parse numbered questions
        response.lines()
            .filter { it.matches(Regex("^\\d+\\..*")) }
            .map { it.replace(Regex("^\\d+\\.\\s*"), "").trim() }
            .take(count)
    }

    suspend fun evaluateAnswer(
        question: String,
        answer: String,
        role: String
    ): Pair<Int, String> = withContext(Dispatchers.IO) {

        val prompt = """
            You are an expert technical interviewer for $role positions.
            
            Question asked: "$question"
            
            Candidate's answer: "$answer"
            
            Evaluate this answer and respond in EXACTLY this format:
            SCORE: [number from 0-100]
            FEEDBACK: [2-3 sentences of specific feedback]
            IMPROVEMENT: [1 specific tip to improve]
            
            Be fair but strict. Consider technical accuracy and clarity.
        """.trimIndent()

        val response = callGemini(prompt)

        // Parse score
        val scoreMatch = Regex("SCORE:\\s*(\\d+)").find(response)
        val score = scoreMatch?.groupValues?.get(1)?.toIntOrNull() ?: 50

        // Parse feedback
        val feedbackMatch = Regex("FEEDBACK:\\s*(.+?)(?=IMPROVEMENT:|$)", RegexOption.DOT_MATCHES_ALL)
            .find(response)
        val feedback = feedbackMatch?.groupValues?.get(1)?.trim() ?: response

        Pair(score, feedback)
    }

    private fun callGemini(prompt: String): String {
        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 1024)
            })
        }.toString()

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBody
        )

        val request = Request.Builder()
            .url(baseUrl)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return "Error"

        return try {
            val json = JSONObject(responseBody)
            if (json.has("error")) {
                "Error: ${json.getJSONObject("error").getString("message")}"
            } else {
                json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        } catch (e: Exception) {
            "Error parsing response"
        }
    }
}