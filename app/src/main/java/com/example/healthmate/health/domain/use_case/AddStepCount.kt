package com.example.healthmate.health.domain.use_case

import com.example.healthmate.health.domain.repository.HealthRepository

class AddStepCount(
    private val repository: HealthRepository
) {

    suspend operator fun invoke(date: String, amount: Int) {
        repository.addStepCount(date, amount)
    }
}
