package com.example.healthmate.health.presentation.ui.screens.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    onNavigateBack: () -> Unit,
    onSaveRecord: (steps: String, calories: String, water: String) -> Unit
) {
    // Local state for the form fields
    var stepsInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var waterInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Health Record") },
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
            FloatingActionButton(
                onClick = {
                    onSaveRecord(stepsInput, caloriesInput, waterInput)
                    onNavigateBack()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Record"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Log Today's Progress",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Steps Input Field
            OutlinedTextField(
                value = stepsInput,
                onValueChange = { stepsInput = it },
                label = { Text("Steps") },
                placeholder = { Text("e.g. 5000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Calories Input Field
            OutlinedTextField(
                value = caloriesInput,
                onValueChange = { caloriesInput = it },
                label = { Text("Calories Burned (kcal)") },
                placeholder = { Text("e.g. 400") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Water Intake Input Field
            OutlinedTextField(
                value = waterInput,
                onValueChange = { waterInput = it },
                label = { Text("Water Intake (ml)") },
                placeholder = { Text("e.g. 2000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}