package com.example.healthmate.health.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.health.domain.model.HealthRecord
import com.example.healthmate.health.domain.use_case.AddWaterIntake
import com.example.healthmate.health.domain.use_case.GetDailyHealthRecord
import com.example.healthmate.health.domain.use_case.UpdateStepCount
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HealthViewModel (
    private val getDailyHealthRecord: GetDailyHealthRecord,
    private val updateStepCount: UpdateStepCount,
    private val addWaterIntake: AddWaterIntake): ViewModel(){

    private val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val todayRecord: StateFlow<HealthRecord?> = getDailyHealthRecord(currentDate)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun onAddWater (amount: Int){
        viewModelScope.launch {
            addWaterIntake(currentDate, amount)
        }
    }

    fun onUpdateSteps(steps: Int){
        viewModelScope.launch {
            updateStepCount(currentDate, steps)
        }
    }

}