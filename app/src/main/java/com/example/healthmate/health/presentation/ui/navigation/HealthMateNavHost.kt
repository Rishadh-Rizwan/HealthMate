package com.example.healthmate.health.presentation.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.example.healthmate.health.presentation.ui.screens.dashboard.DashboardScreen
import com.example.healthmate.health.presentation.ui.screens.records.AddEditRecordScreen
import com.example.healthmate.health.presentation.ui.screens.records.RecordListScreen
import com.example.healthmate.health.presentation.ui.screens.search.SearchByDateScreen

@Composable
fun HealthMateNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier

    ){
        composable(Screen.RecordList.route){
            RecordListScreen (
                onNavigateBack = { navController.popBackStack() }
            )

        }
        composable(Screen.AddEditRecord.route){
            AddEditRecordScreen(
                onNavigateBack = { navController.popBackStack()},
                onSaveRecord = { steps, calories, water ->

                }
            )

        }
        composable(Screen.SearchByDate.route){
            SearchByDateScreen (
                onNavigateBack = { navController.popBackStack() }

            )
        }
    }
}