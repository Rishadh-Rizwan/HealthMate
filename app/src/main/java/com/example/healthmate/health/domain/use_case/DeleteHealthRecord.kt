package com.example.healthmate.health.domain.use_case

import com.example.healthmate.health.domain.repository.HealthRepository

class DeleteHealthRecord(
    private val repository: HealthRepository
) {

    suspend operator fun invoke(date: String) {
        repository.deleteRecord(date)
    }
}
