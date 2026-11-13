package com.example.healthmate.health.domain.use_case

import com.example.healthmate.health.domain.model.ActivityLog
import com.example.healthmate.health.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetActivityLogsBetween(
    private val repository: HealthRepository
) {

    operator fun invoke(
        startDate: String,
        endDate: String
    ): Flow<List<ActivityLog>> {
        return repository.getActivityLogsBetween(startDate, endDate)
    }
}
