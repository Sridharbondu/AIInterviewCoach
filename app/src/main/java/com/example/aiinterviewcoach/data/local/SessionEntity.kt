package com.example.aiinterviewcoach.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: String,
    val difficulty: String,
    val totalScore: Int,
    val answersJson: String,
    val timestamp: Long
)