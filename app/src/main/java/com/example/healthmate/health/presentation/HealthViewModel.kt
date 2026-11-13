package com.example.healthmate.health.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.health.data.sensor.StepSensorManager
import com.example.healthmate.health.domain.model.HealthRecord
import com.example.healthmate.health.domain.model.ActivityLog
import com.example.healthmate.health.domain.use_case.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HealthViewModel(
    private val getDailyHealthRecord: GetDailyHealthRecord,
    private val updateStepCount: UpdateStepCount,
    private val addWaterIntake: AddWaterIntake,
    private val getAllRecordsUseCase: GetAllRecords,
    private val insertRecordUseCase: InsertOrUpdateRecord,
    private val calculateCaloriesBurned: CalculateCaloriesBurned,
    private val addStepCount: AddStepCount,
    private val addCalories: AddCalories,
    private val deleteHealthRecord: DeleteHealthRecord,
    private val getActivityLogsBetweenUseCase: GetActivityLogsBetween
) : ViewModel() {

    private val currentDate: String
        get() = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())

    private var sensorJob: Job? = null
    private var lastSensorSteps = 0

    val todayRecord: StateFlow<HealthRecord?> =
        getDailyHealthRecord(currentDate)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    val allRecords: StateFlow<List<HealthRecord>> =
        getAllRecordsUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getRecordForDate(date: String): Flow<HealthRecord?> {
        return getDailyHealthRecord(date)
    }

    fun getActivityLogsBetween(
        startDate: String,
        endDate: String
    ): Flow<List<ActivityLog>> {
        return getActivityLogsBetweenUseCase(startDate, endDate)
    }

    fun onAddWater(amount: Int, date: String = currentDate) {
        if (amount <= 0) return

        viewModelScope.launch {
            addWaterIntake(date, amount)
        }
    }

    fun onAddSteps(amount: Int, date: String = currentDate) {
        if (amount <= 0) return

        viewModelScope.launch {
            addStepCount(date, amount)
        }
    }

    fun onAddCalories(amount: Int, date: String = currentDate) {
        if (amount <= 0) return

        viewModelScope.launch {
            addCalories(date, amount)
        }
    }

    fun onUpdateSteps(steps: Int) {
        if (steps < 0) return

        viewModelScope.launch {
            updateStepCount(currentDate, steps)
        }
    }

    fun onDeleteRecord(date: String) {
        viewModelScope.launch {
            deleteHealthRecord(date)
        }
    }

    fun saveRecord(
        steps: String,
        calories: String,
        water: String,
        date: String = currentDate
    ) {
        viewModelScope.launch {

            val existingRecord =
                getDailyHealthRecord(date).firstOrNull()

            val manualCalories =
                calories.toIntOrNull()
                    ?: (
                            (existingRecord?.calories ?: 0) -
                                    (existingRecord?.stepCalories ?: 0)
                            ).coerceAtLeast(0)

            val record = HealthRecord(
                id = existingRecord?.id ?: 0,
                date = date,
                steps = steps.toIntOrNull()
                    ?: existingRecord?.steps
                    ?: 0,
                calories = manualCalories,
                water = water.toIntOrNull()
                    ?: existingRecord?.water
                    ?: 0
            )

            insertRecordUseCase(record)
        }
    }

    fun startListeningToSensor(context: Context) {

        if (sensorJob?.isActive == true) return

        val sensorManager = StepSensorManager(context)

        lastSensorSteps = 0

        sensorJob = viewModelScope.launch {

            sensorManager.stepCountFlow.collect { sensorSteps ->

                val difference = sensorSteps - lastSensorSteps

                if (difference > 0) {
                    addStepCount(currentDate, difference)
                }

                lastSensorSteps = sensorSteps
            }
        }
    }

    override fun onCleared() {
        sensorJob?.cancel()
        super.onCleared()
    }
}
