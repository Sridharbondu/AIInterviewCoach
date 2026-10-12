package com.example.aiinterviewcoach.domain.model

data class Question(
    val id: Int,
    val text: String,
    val category: String,
    val difficulty: String
)