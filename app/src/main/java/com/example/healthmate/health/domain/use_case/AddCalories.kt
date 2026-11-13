package com.example.healthmate.health.domain.use_case

import com.example.healthmate.health.domain.repository.HealthRepository

class AddCalories(
    private val repository: HealthRepository
) {

    suspend operator fun invoke(date: String, amount: Int) {
        repository.addCalories(date, amount)
    }
}
