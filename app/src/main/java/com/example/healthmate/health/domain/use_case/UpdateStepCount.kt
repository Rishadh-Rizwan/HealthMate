package com.example.healthmate.health.domain.use_case

import com.example.healthmate.health.domain.repository.HealthRepository

class UpdateStepCount (
    private val repository: HealthRepository){

    suspend operator fun invoke(date: String,steps: Int){

        repository.updateStepCount(date, steps)
    }
}
