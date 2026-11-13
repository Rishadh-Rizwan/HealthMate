package com.example.healthmate.health.presentation.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthmate.health.presentation.HealthViewModel
import com.example.healthmate.health.presentation.ui.screens.records.HealthRecordItemCard
import com.example.healthmate.health.presentation.ui.screens.records.HealthRecordUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByDateScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    onEditRecord: (String) -> Unit
) {
    val dateFormat = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    var selectedDate by remember {
        mutableStateOf(dateFormat.format(Date()))
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf(false) }

    val recordFlow = remember(selectedDate) {
        viewModel.getRecordForDate(selectedDate)
    }

    val record by recordFlow.collectAsStateWithLifecycle(initialValue = null)

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val utcFormat = SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            )
                            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
                            selectedDate = utcFormat.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Search")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (recordToDelete && record != null) {
        AlertDialog(
            onDismissRequest = { recordToDelete = false },
            title = { Text("Delete Record?") },
            text = {
                Text("Are you sure you want to delete the record for $selectedDate?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeleteRecord(selectedDate)
                        recordToDelete = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Records by Date") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selected Date",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                alpha = 0.7f
                            )
                        )

                        Text(
                            text = selectedDate,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Button(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Choose date"
                        )
                    }
                }
            }

            Text(
                text = "Search Results",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (record != null) {
                HealthRecordItemCard(
                    record = HealthRecordUiModel(
                        id = record!!.id.toString(),
                        date = record!!.date,
                        steps = record!!.steps,
                        calories = record!!.calories,
                        waterMl = record!!.water
                    ),
                    onEdit = {
                        onEditRecord(selectedDate)
                    },
                    onDelete = {
                        recordToDelete = true
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records found for this date.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
