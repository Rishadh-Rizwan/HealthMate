package com.example.healthmate.health.domain.model

data class ActivityLog(
    val id: Int = 0,
    val date: String,
    val activityType: String,
    val amount: Int,
    val timestamp: Long
)
