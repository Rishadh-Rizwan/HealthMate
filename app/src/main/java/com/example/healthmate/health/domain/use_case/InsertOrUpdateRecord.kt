package com.example.healthmate.health.domain.use_case

import com.example.healthmate.health.domain.model.HealthRecord
import com.example.healthmate.health.domain.repository.HealthRepository

class InsertOrUpdateRecord (
    private val repository: HealthRepository
){
    suspend operator fun invoke(record: HealthRecord) {
        repository.insertOrUpdateRecord(record)
    }
}