package com.example.healthmate.health.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.health.domain.model.HealthRecord
import com.example.healthmate.health.data.sensor.StepSensorManager
import android.content.Context
import com.example.healthmate.health.domain.use_case.*
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
    private val calculateCaloriesBurned: CalculateCaloriesBurned
) : ViewModel() {

    private val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val todayRecord: StateFlow<HealthRecord?> = getDailyHealthRecord(currentDate)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allRecords: StateFlow<List<HealthRecord>> = getAllRecordsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onAddWater(amount: Int) {
        viewModelScope.launch {
            addWaterIntake(currentDate, amount)
        }
    }

    fun onUpdateSteps(steps: Int) {
        viewModelScope.launch {
            updateStepCount(currentDate, steps)
        }
    }

    fun startListeningToSensor(context: Context) {
        val sensorManager = StepSensorManager(context)
        viewModelScope.launch {
            sensorManager.stepCountFlow.collect { sensorSteps ->
                updateStepCount(currentDate, sensorSteps)
            }
        }
    }

    fun saveRecord(steps: String, calories: String, water: String, date: String = currentDate) {
        viewModelScope.launch {
            val parsedSteps = steps.toIntOrNull() ?: 0

            val parsedCalories = calories.toIntOrNull() ?: calculateCaloriesBurned(parsedSteps)

            val record = HealthRecord(
                id = 0,
                date = date,
                steps = parsedSteps,
                calories = parsedCalories,
                water = water.toIntOrNull() ?: 0
            )
            insertRecordUseCase(record)
        }
    }
}