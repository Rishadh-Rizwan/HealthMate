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
import kotlinx.coroutines.flow.firstOrNull
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
        initialDate ?: SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())
    }

    var selectedDate by remember {
        mutableStateOf(defaultDate)
    }

    var stepsInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var waterInput by remember { mutableStateOf("") }

    var stepsError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    // Load existing record whenever the selected date changes.
    LaunchedEffect(selectedDate) {

        isLoading = true

        val existingRecord =
            viewModel.getRecordForDate(selectedDate).firstOrNull()

        if (existingRecord != null) {

            stepsInput = existingRecord.steps.toString()

            // Show only manually entered calories here.
            caloriesInput = (
                    existingRecord.calories -
                            existingRecord.stepCalories
                    ).coerceAtLeast(0).toString()

            waterInput = existingRecord.water.toString()

        } else {

            stepsInput = ""
            caloriesInput = ""
            waterInput = ""
        }

        isLoading = false
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text("Log Health Record")
                },
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

                    val validDate = try {
                        val format = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).apply {
                            isLenient = false
                        }

                        format.parse(selectedDate) != null
                    } catch (e: Exception) {
                        false
                    }

                    dateError = !validDate

                    val hasInput =
                        stepsInput.isNotBlank() ||
                                caloriesInput.isNotBlank() ||
                                waterInput.isNotBlank()

                    stepsError = !hasInput

                    if (validDate && hasInput) {

                        viewModel.saveRecord(
                            steps = stepsInput,
                            calories = caloriesInput,
                            water = waterInput,
                            date = selectedDate
                        )

                        onNavigateBack()
                    }
                },

                icon = {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Save"
                    )
                },

                text = {
                    Text("Save Record")
                },

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

            OutlinedTextField(

                value = selectedDate,

                onValueChange = {
                    selectedDate = it
                    dateError = false
                },

                label = {
                    Text("Date (YYYY-MM-DD)")
                },

                isError = dateError,

                supportingText = {
                    if (dateError) {
                        Text("Please enter a valid date.")
                    }
                },

                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(

                value = stepsInput,

                onValueChange = {
                    stepsInput = it
                    stepsError = false
                },

                label = {
                    Text("Steps")
                },

                placeholder = {
                    Text("e.g. 8500")
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(

                value = caloriesInput,

                onValueChange = {
                    caloriesInput = it
                },

                label = {
                    Text("Additional Calories Burned (kcal)")
                },

                placeholder = {
                    Text("e.g. 150")
                },

                supportingText = {
                    Text(
                        "Enter workout or other activity calories. " +
                                "Step calories are calculated automatically."
                    )
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(

                value = waterInput,

                onValueChange = {
                    waterInput = it
                },

                label = {
                    Text("Water Intake (ml)")
                },

                placeholder = {
                    Text("e.g. 2500")
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (stepsError) {
                Text(
                    text = "Please enter at least one metric to save.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}
