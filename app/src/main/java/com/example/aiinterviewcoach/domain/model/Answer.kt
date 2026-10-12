package com.example.aiinterviewcoach.domain.model

data class Answer (
    val questionId: Int,
    val questionText: String,
    val userAnswer: String,
    val aiFeedback: String,
    val score: Int
)