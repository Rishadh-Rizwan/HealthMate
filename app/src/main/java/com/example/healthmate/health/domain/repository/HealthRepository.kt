package com.example.healthmate.health.domain.repository

import com.example.healthmate.health.domain.model.HealthRecord
import kotlinx.coroutines.flow.Flow


interface HealthRepository {

    fun getRecordByDate(date: String): Flow<HealthRecord?>
    suspend fun insertOrUpdateRecord(record: HealthRecord)
    suspend fun updateWaterIntake(date: String, amount: Int)
    suspend fun updateStepCount(date: String, steps: Int)
}