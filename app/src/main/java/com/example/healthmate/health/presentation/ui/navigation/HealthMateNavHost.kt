package com.example.healthmate.health.presentation.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.example.healthmate.health.presentation.ui.screens.dashboard.DashboardScreen

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

        }
        composable(Screen.AddEditRecord.route){

        }
        composable(Screen.SearchByDate.route){

        }
    }
}