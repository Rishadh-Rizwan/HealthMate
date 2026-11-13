package com.example.healthmate.health.domain.repository

import com.example.healthmate.health.domain.model.HealthRecord
import com.example.healthmate.health.domain.model.ActivityLog
import kotlinx.coroutines.flow.Flow

interface HealthRepository {

    fun getRecordByDate(date: String): Flow<HealthRecord?>

    fun getAllRecords(): Flow<List<HealthRecord>>

    fun getActivityLogsBetween(
        startDate: String,
        endDate: String
    ): Flow<List<ActivityLog>>

    suspend fun insertOrUpdateRecord(record: HealthRecord)

    suspend fun updateWaterIntake(date: String, amount: Int)

    suspend fun updateStepCount(date: String, steps: Int)

    suspend fun addStepCount(date: String, amount: Int)

    suspend fun addCalories(date: String, amount: Int)

    suspend fun deleteRecord(date: String)
}
