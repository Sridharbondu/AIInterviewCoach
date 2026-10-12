package com.example.aiinterviewcoach.domain.model

enum class JobRole(val displayName: String) {
    ANDROID_DEVELOPER("Android Developer"),
    DATA_SCIENTIST("Data Scientist"),
    FULL_STACK("Full Stack Developer"),
    ML_ENGINEER("ML Engineer"),
    IOS_DEVELOPER("iOS Developer")
}

enum class Difficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}