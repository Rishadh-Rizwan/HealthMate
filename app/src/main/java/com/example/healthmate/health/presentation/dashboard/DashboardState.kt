package com.example.healthmate.health.presentation.dashboard

import com.example.healthmate.health.domain.model.HealthRecordEntity

data class DashboardState(
    val records: List<HealthRecordEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
