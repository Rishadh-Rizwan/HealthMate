package com.example.healthmate.health.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_records",
    indices = [Index(value = ["date"], unique = true)]
)
data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val steps: Int,
    val calories: Int,
    val water: Int,
    val stepCalories: Int = 0,
    val calorieSteps: Int = 0
)

fun HealthRecordEntity.toHealthRecord(): HealthRecord {
    return HealthRecord(
        id = id,
        date = date,
        steps = steps,
        calories = calories,
        water = water,
        stepCalories = stepCalories,
        calorieSteps = calorieSteps
    )
}

fun HealthRecord.toHealthRecordEntity(): HealthRecordEntity {
    return HealthRecordEntity(
        id = id,
        date = date,
        steps = steps,
        calories = calories,
        water = water,
        stepCalories = stepCalories,
        calorieSteps = calorieSteps
    )
}