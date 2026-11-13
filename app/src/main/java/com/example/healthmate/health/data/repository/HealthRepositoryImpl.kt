package com.example.healthmate.health.data.repository

import com.example.healthmate.health.data.data_source.HealthDao
import com.example.healthmate.health.domain.model.HealthRecord
import com.example.healthmate.health.domain.model.HealthRecordEntity
import com.example.healthmate.health.domain.model.ActivityLog
import com.example.healthmate.health.domain.model.ActivityLogEntity
import com.example.healthmate.health.domain.model.CalorieEstimate
import com.example.healthmate.health.domain.model.toHealthRecord
import com.example.healthmate.health.domain.model.toHealthRecordEntity
import com.example.healthmate.health.domain.model.toActivityLog
import com.example.healthmate.health.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HealthRepositoryImpl(
    private val dao: HealthDao
) : HealthRepository {

    override fun getRecordByDate(date: String): Flow<HealthRecord?> {
        return dao.getRecordByDate(date).map { entity ->
            entity?.toHealthRecord()
        }
    }

    override fun getAllRecords(): Flow<List<HealthRecord>> {
        return dao.getAllRecords().map { entities ->
            entities.map { it.toHealthRecord() }
        }
    }

    override fun getActivityLogsBetween(
        startDate: String,
        endDate: String
    ): Flow<List<ActivityLog>> {
        return dao.getActivityLogsBetween(startDate, endDate).map { logs ->
            logs.map { it.toActivityLog() }
        }
    }

    override suspend fun insertOrUpdateRecord(record: HealthRecord) {

        val existing = dao.getRecordByDateOnce(record.date)

        if (existing == null) {

            val automaticCalories =
                CalorieEstimate.fromSteps(record.steps)

            val manualCalories = record.calories.coerceAtLeast(0)

            dao.upsertHealthRecord(
                HealthRecordEntity(
                    id = record.id,
                    date = record.date,
                    steps = record.steps,
                    calories = automaticCalories + manualCalories,
                    water = record.water,
                    stepCalories = automaticCalories,
                    calorieSteps = record.steps
                )
            )

        } else {

            val newSteps = record.steps.coerceAtLeast(0)

            val calorieDifference =
                CalorieEstimate.fromSteps(newSteps) -
                        CalorieEstimate.fromSteps(existing.calorieSteps)

            val newStepCalories =
                (existing.stepCalories + calorieDifference)
                    .coerceAtLeast(0)

            val manualCalories =
                (record.calories - existing.stepCalories)
                    .coerceAtLeast(0)

            dao.upsertHealthRecord(
                existing.copy(
                    steps = newSteps,
                    calories = manualCalories + newStepCalories,
                    water = record.water,
                    stepCalories = newStepCalories,
                    calorieSteps = newSteps
                )
            )
        }
    }

    override suspend fun updateWaterIntake(date: String, amount: Int) {
        if (amount <= 0) return

        dao.addWaterIntakeWithLog(
            date,
            amount,
            System.currentTimeMillis()
        )
    }

    override suspend fun updateStepCount(date: String, steps: Int) {
        if (steps < 0) return

        val calorieDifference = dao.updateSteps(date, steps)

        if (calorieDifference > 0) {
            dao.insertActivityLog(
                ActivityLogEntity(
                    date = date,
                    activityType = "CALORIES",
                    amount = calorieDifference,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun addStepCount(date: String, amount: Int) {
        if (amount <= 0) return

        dao.addStepCountWithLog(
            date,
            amount,
            System.currentTimeMillis()
        )
    }

    override suspend fun addCalories(date: String, amount: Int) {
        if (amount <= 0) return

        dao.addCaloriesWithLog(
            date,
            amount,
            System.currentTimeMillis()
        )
    }

    override suspend fun deleteRecord(date: String) {
        dao.deleteRecordByDate(date)
    }
}
