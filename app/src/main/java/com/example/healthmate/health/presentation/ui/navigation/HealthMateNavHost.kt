package com.example.healthmate.health.presentation.ui.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToAddRecord = {
                    navController.navigate(Screen.AddEditRecord.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.RecordList.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.SearchByDate.route)
                }
            )
        }

        composable(Screen.RecordList.route) {
            RecordListScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditRecord = { date ->
                    navController.navigate(
                        "${Screen.AddEditRecord.route}?date=${Uri.encode(date)}"
                    )
                }
            )
        }

        composable(
            route = "${Screen.AddEditRecord.route}?date={date}",
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val initialDate = backStackEntry.arguments?.getString("date")

            AddEditRecordScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                initialDate = initialDate
            )
        }

        composable(Screen.SearchByDate.route) {
            SearchByDateScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditRecord = { date ->
                    navController.navigate(
                        "${Screen.AddEditRecord.route}?date=${Uri.encode(date)}"
                    )
                }
            )
        }
    }
}
