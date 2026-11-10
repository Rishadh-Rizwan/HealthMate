package com.example.healthmate.health.domain.use_case

import com.example.healthmate.health.domain.model.HealthRecord
import com.example.healthmate.health.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetAllRecords (
    private val repository: HealthRepository
) {
    operator fun invoke(): Flow<List<HealthRecord>> {
        return repository.getAllRecords()
    }
}