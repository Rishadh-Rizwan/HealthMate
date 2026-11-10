package com.example.healthmate.health.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.health.data.data_source.HealthDao
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val healthDao: HealthDao
) : ViewModel() {

    var state by mutableStateOf(DashboardState())
        private set

    init {
        loadRecords()
    }

    private fun loadRecords() {
        healthDao.getAllRecords().onEach { records ->
            state = state.copy(records = records)
        }.launchIn(viewModelScope)
    }
}