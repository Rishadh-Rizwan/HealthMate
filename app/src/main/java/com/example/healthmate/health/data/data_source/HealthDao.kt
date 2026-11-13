package com.example.healthmate.health.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.healthmate.health.domain.model.HealthRecordEntity
import com.example.healthmate.health.domain.model.CalorieEstimate
import com.example.healthmate.health.domain.model.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    @Query("SELECT * FROM health_records WHERE date = :date LIMIT 1")
    fun getRecordByDate(date: String): Flow<HealthRecordEntity?>

    @Query("SELECT * FROM health_records WHERE date = :date LIMIT 1")
    suspend fun getRecordByDateOnce(date: String): HealthRecordEntity?

    @Query(
        "SELECT * FROM health_records " +
                "WHERE date LIKE '%' || :searchQuery || '%' ORDER BY date DESC"
    )
    fun searchRecords(searchQuery: String): Flow<List<HealthRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHealthRecord(record: HealthRecordEntity)

    @Delete
    suspend fun deleteRecord(record: HealthRecordEntity)

    @Query("DELETE FROM health_records WHERE date = :date")
    suspend fun deleteRecordByDate(date: String)

    @Query("SELECT * FROM health_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<HealthRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertActivityLog(log: ActivityLogEntity)

    @Query(
        """
        SELECT * FROM activity_logs
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY timestamp ASC
        """
    )
    fun getActivityLogsBetween(
        startDate: String,
        endDate: String
    ): Flow<List<ActivityLogEntity>>

    @Transaction
    suspend fun addWaterIntake(date: String, amount: Int) {

        val existing = getRecordByDateOnce(date)

        if (existing != null) {
            upsertHealthRecord(
                existing.copy(water = existing.water + amount)
            )
        } else {
            upsertHealthRecord(
                HealthRecordEntity(
                    date = date,
                    steps = 0,
                    calories = 0,
                    water = amount
                )
            )
        }
    }

    /**
     * Adds steps and calculates the additional estimated calories.
     * Manual calories remain unchanged.
     */
    @Transaction
    suspend fun addStepCount(date: String, amount: Int): Int {

        val existing = getRecordByDateOnce(date)

        val oldSteps = existing?.steps ?: 0
        val newSteps = oldSteps + amount
        val accountedSteps = existing?.calorieSteps ?: 0

        val calorieDelta =
            CalorieEstimate.fromSteps(newSteps) -
                    CalorieEstimate.fromSteps(accountedSteps)

        val safeDelta = calorieDelta.coerceAtLeast(0)

        val updated = if (existing != null) {

            existing.copy(
                steps = newSteps,
                calories = existing.calories + safeDelta,
                stepCalories = existing.stepCalories + safeDelta,
                calorieSteps = newSteps
            )

        } else {

            HealthRecordEntity(
                date = date,
                steps = newSteps,
                calories = safeDelta,
                water = 0,
                stepCalories = safeDelta,
                calorieSteps = newSteps
            )
        }

        upsertHealthRecord(updated)

        return safeDelta
    }

    /**
     * Adds manually entered calories without changing step calories.
     */
    @Transaction
    suspend fun addCalories(date: String, amount: Int) {

        val existing = getRecordByDateOnce(date)

        if (existing != null) {

            upsertHealthRecord(
                existing.copy(
                    calories = existing.calories + amount
                )
            )

        } else {

            upsertHealthRecord(
                HealthRecordEntity(
                    date = date,
                    steps = 0,
                    calories = amount,
                    water = 0
                )
            )
        }
    }

    /**
     * Updates the absolute step count.
     * Only the automatically calculated calorie portion is adjusted.
     */
    @Transaction
    suspend fun updateSteps(date: String, steps: Int): Int {

        val existing = getRecordByDateOnce(date)

        if (existing == null) {

            val estimatedCalories = CalorieEstimate.fromSteps(steps)

            upsertHealthRecord(
                HealthRecordEntity(
                    date = date,
                    steps = steps,
                    calories = estimatedCalories,
                    water = 0,
                    stepCalories = estimatedCalories,
                    calorieSteps = steps
                )
            )

            return 0
        }

        val delta =
            CalorieEstimate.fromSteps(steps) -
                    CalorieEstimate.fromSteps(existing.calorieSteps)

        val appliedDelta = if (delta < 0) {
            -minOf(existing.stepCalories, -delta)
        } else {
            delta
        }

        upsertHealthRecord(
            existing.copy(
                steps = steps,
                calories = (existing.calories + appliedDelta).coerceAtLeast(0),
                stepCalories = (existing.stepCalories + appliedDelta).coerceAtLeast(0),
                calorieSteps = steps
            )
        )

        return appliedDelta
    }

    @Transaction
    suspend fun addWaterIntakeWithLog(
        date: String,
        amount: Int,
        timestamp: Long
    ) {
        addWaterIntake(date, amount)

        insertActivityLog(
            ActivityLogEntity(
                date = date,
                activityType = "WATER",
                amount = amount,
                timestamp = timestamp
            )
        )
    }

    @Transaction
    suspend fun addStepCountWithLog(
        date: String,
        amount: Int,
        timestamp: Long
    ) {

        val caloriesAdded = addStepCount(date, amount)

        insertActivityLog(
            ActivityLogEntity(
                date = date,
                activityType = "STEPS",
                amount = amount,
                timestamp = timestamp
            )
        )

        if (caloriesAdded > 0) {
            insertActivityLog(
                ActivityLogEntity(
                    date = date,
                    activityType = "CALORIES",
                    amount = caloriesAdded,
                    timestamp = timestamp
                )
            )
        }
    }

    @Transaction
    suspend fun addCaloriesWithLog(
        date: String,
        amount: Int,
        timestamp: Long
    ) {
        addCalories(date, amount)

        insertActivityLog(
            ActivityLogEntity(
                date = date,
                activityType = "CALORIES",
                amount = amount,
                timestamp = timestamp
            )
        )
    }
}