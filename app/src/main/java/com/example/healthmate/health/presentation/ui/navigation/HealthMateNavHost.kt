package com.example.healthmate.health.presentation.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.example.healthmate.health.presentation.HealthViewModel
import com.example.healthmate.health.presentation.ui.screens.dashboard.DashboardScreen
import com.example.healthmate.health.presentation.ui.screens.records.AddEditRecordScreen
import com.example.healthmate.health.presentation.ui.screens.records.RecordListScreen
import com.example.healthmate.health.presentation.ui.screens.search.SearchByDateScreen

@Composable
fun HealthMateNavHost(
    navController: NavHostController,
    viewModel: HealthViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        // 1. Dashboard Screen (Triggers navigation to Add, History, and Search)
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToAddRecord = { navController.navigate(Screen.AddEditRecord.route) },
                onNavigateToHistory = { navController.navigate(Screen.RecordList.route) },
                onNavigateToSearch = { navController.navigate(Screen.SearchByDate.route) }
            )
        }

        // 2. Record List (History) Screen
        composable(Screen.RecordList.route) {
            RecordListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 3. Add / Edit Record Screen (Passes ViewModel and back navigation)
        composable(Screen.AddEditRecord.route) {
            AddEditRecordScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 4. Search By Date Screen
        composable(Screen.SearchByDate.route) {
            SearchByDateScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}