package com.example.healthmate.health.presentation.ui.screens.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmate.health.presentation.HealthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    initialDate: String? = null
) {
    val defaultDate = remember {
        initialDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    // Local state for the form fields
    var selectedDate by remember { mutableStateOf(defaultDate) }
    var stepsInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var waterInput by remember { mutableStateOf("") }
    var stepsError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Health Record") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (stepsInput.isBlank() && caloriesInput.isBlank() && waterInput.isBlank()) {
                        stepsError = true
                    }else {
                        viewModel.saveRecord(
                            steps = stepsInput,
                            calories = caloriesInput,
                            water = waterInput,
                            date = selectedDate
                        )
                        onNavigateBack()
                    }
                },
                icon = { Icon(Icons.Default.Check, contentDescription = "Save")},
                text = { Text("Save Record")},
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Record Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            // DAte Input Field
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { selectedDate = it },
                label = { Text("Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Steps Input Field
            OutlinedTextField(
                value = stepsInput,
                onValueChange = {
                    stepsInput = it
                    if (stepsError) stepsError = false
                },
                label = { Text("Steps") },
                placeholder = { Text("e.g. 8500") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = stepsError,
                supportingText = {
                    if (stepsError) {
                        Text(
                            text = "Please enter at least on metric to save.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Calories Input Field
            OutlinedTextField(
                value = caloriesInput,
                onValueChange = { caloriesInput = it },
                label = { Text("Calories Burned (kcal)") },
                placeholder = { Text("Auto-calculated if left blank") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Water Intake Input Field
            OutlinedTextField(
                value = waterInput,
                onValueChange = { waterInput = it },
                label = { Text("Water Intake (ml)") },
                placeholder = { Text("e.g. 2500") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}