package com.example.healthmate.health.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.health.data.data_source.HealthDao
import com.example.healthmate.health.domain.model.HealthRecordEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(
    private val healthDao: HealthDao
) : ViewModel() {

    var state by mutableStateOf(DashboardState())
        private set

    private var allRecordsCache: List<HealthRecordEntity> = emptyList()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val todayString = dateFormat.format(calendar.time)

        healthDao.getAllRecords().onEach { records ->
            allRecordsCache = records
            val todayRecord = records.find { it.date == todayString }

            // Generate past 7 days stats for the chart
            val weeklyStats = mutableListOf<DayStat>()
            for (i in 6 downTo 0) {
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
                val dateStr = dateFormat.format(cal.time)
                val dayName = dayFormat.format(cal.time)

                val matchingRecord = records.find { it.date == dateStr }
                weeklyStats.add(
                    DayStat(
                        dayName = dayName,
                        date = dateStr,
                        steps = matchingRecord?.steps ?: 0
                    )
                )
            }

            // Apply current search query filter if active
            val filteredRecords = if (state.searchQuery.isBlank()) {
                records
            } else {
                records.filter { it.date.contains(state.searchQuery, ignoreCase = true) }
            }

            state = state.copy(
                records = filteredRecords,
                todaySteps = todayRecord?.steps ?: 0,
                todayCalories = todayRecord?.calories ?: 0,
                todayWater = todayRecord?.water ?: 0,
                weeklySteps = weeklyStats
            )
        }.launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        state = state.copy(searchQuery = query)
        val targetQuery = query.trim()

        val filtered = if (targetQuery.isEmpty()) {
            allRecordsCache
        } else {
            allRecordsCache.filter { it.date.contains(targetQuery, ignoreCase = true) }
        }

        state = state.copy(records = filtered)
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            val todayString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val existing = allRecordsCache.find { it.date == todayString }

            val updatedWater = (existing?.water ?: 0) + amountMl
            val newRecord = HealthRecordEntity(
                id = existing?.id ?: 0,
                date = todayString,
                steps = existing?.steps ?: 0,
                calories = existing?.calories ?: 0,
                water = updatedWater
            )
            healthDao.upsertHealthRecord(newRecord)
        }
    }

    fun saveRecord(date: String, steps: Int, calories: Int, water: Int) {
        viewModelScope.launch {
            val existing = allRecordsCache.find { it.date == date }
            val record = HealthRecordEntity(
                id = existing?.id ?: 0,
                date = date,
                steps = steps,
                calories = calories,
                water = water
            )
            healthDao.upsertHealthRecord(record)
        }
    }

    fun deleteRecord(record: HealthRecordEntity) {
        viewModelScope.launch {
            healthDao.deleteRecord(record)
        }
    }
}