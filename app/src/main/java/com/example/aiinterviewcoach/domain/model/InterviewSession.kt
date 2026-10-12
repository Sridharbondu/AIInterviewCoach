package com.example.aiinterviewcoach.domain.model

data class InterviewSession(
    val id: Long = 0,
    val role: String,
    val difficulty: String,
    val totalScore: Int,
    val answers: List<Answer>,
    val timestamp: Long = System.currentTimeMillis()
)