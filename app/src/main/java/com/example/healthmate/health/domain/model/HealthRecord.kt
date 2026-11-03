package com.example.healthmate.health.domain.model

data class HealthRecord(
    val id: Int = 0,
    val date: String,
    val steps: Int,
    val calories: Int,
    val water: Int
)
