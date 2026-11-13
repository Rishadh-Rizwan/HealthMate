package com.example.healthmate.health.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.healthmate.health.data.data_source.HealthDatabase
import com.example.healthmate.health.data.repository.HealthRepositoryImpl
import com.example.healthmate.health.domain.use_case.*
import com.example.healthmate.health.presentation.ui.navigation.HealthMateNavHost
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
                    calculateCaloriesBurned = CalculateCaloriesBurned(),
                    addStepCount = AddStepCount(repository),
                    addCalories = AddCalories(repository),
                    deleteHealthRecord = DeleteHealthRecord(repository),
                    getActivityLogsBetweenUseCase = GetActivityLogsBetween(repository)
                ) as T
            }
        }
    }

    private val activityRecognitionPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->

            if (granted) {
                viewModel.startListeningToSensor(applicationContext)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            )

            if (permission == PackageManager.PERMISSION_GRANTED) {
                viewModel.startListeningToSensor(applicationContext)
            } else {
                activityRecognitionPermission.launch(
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            }

        } else {
            viewModel.startListeningToSensor(applicationContext)
        }

        setContent {
            HealthMateTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    HealthMateNavHost(
                        navController = rememberNavController(),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
