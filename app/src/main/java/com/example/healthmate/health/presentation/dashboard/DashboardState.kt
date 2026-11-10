package com.example.healthmate.health.presentation.dashboard

import com.example.healthmate.health.domain.model.HealthRecordEntity

data class DayStat(
    val dayName: String, // e.g. "Mon", "Tue"
    val date: String,
    val steps: Int
)

data class DashboardState(
    val records: List<HealthRecordEntity> = emptyList(),
    val searchQuery: String = "",
    val todaySteps: Int = 0,
    val todayCalories: Int = 0,
    val todayWater: Int = 0,
    val dailyWaterGoal: Int = 2500,
    val dailyStepGoal: Int = 10000,
    val weeklySteps: List<DayStat> = emptyList(),
    val isLoading: Boolean = false
)