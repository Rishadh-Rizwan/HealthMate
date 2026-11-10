package com.example.healthmate.health.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthmate.health.data.data_source.HealthDatabase
import com.example.healthmate.health.data.repository.HealthRepositoryImpl
import com.example.healthmate.health.domain.use_case.*
import com.example.healthmate.health.presentation.ui.navigation.HealthMateNavHost
import androidx.navigation.compose.rememberNavController
import com.example.healthmate.ui.theme.HealthMateTheme

class MainActivity : ComponentActivity() {

    private val viewModel: HealthViewModel by viewModels {
        val database = HealthDatabase.getInstance(applicationContext)
        val dao = database.healthDao
        val repository = HealthRepositoryImpl(dao)

        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HealthViewModel(
                    getDailyHealthRecord = GetDailyHealthRecord(repository),
                    updateStepCount = UpdateStepCount(repository),
                    addWaterIntake = AddWaterIntake(repository),
                    getAllRecordsUseCase = GetAllRecords(repository),
                    insertRecordUseCase = InsertOrUpdateRecord(repository),
                    calculateCaloriesBurned = CalculateCaloriesBurned()
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.startListeningToSensor(applicationContext)

        setContent {
            HealthMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HealthMateNavHost(
                        navController = rememberNavController(), // If using rememberNavController() here, or pass it from your NavGraph wrapper
                        viewModel = viewModel)
                }
            }
        }
    }
}