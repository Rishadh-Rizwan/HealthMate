package com.example.healthmate.health.domain.use_case

import com.example.healthmate.health.domain.repository.HealthRepository
import java.sql.Date

class AddWaterIntake (
    private val repository: HealthRepository){

    suspend operator fun invoke(date: String, amount: Int){

        repository.updateWaterIntake(date, amount)
    }
}