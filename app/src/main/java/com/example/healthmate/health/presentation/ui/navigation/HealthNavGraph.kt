package com.example.healthmate.health.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.healthmate.health.presentation.HealthViewModel

@Composable
fun HealthNavGraph(
    viewModel: HealthViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    HealthMateNavHost(
        navController = navController,
        viewModel = viewModel,
        modifier = modifier
    )
}