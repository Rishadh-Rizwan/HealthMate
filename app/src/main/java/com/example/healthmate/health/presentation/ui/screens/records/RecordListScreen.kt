package com.example.healthmate.health.presentation.ui.screens.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


data class HealthRecordUiModel(
    val id: String,
    val date: String,
    val steps: Int,
    val calories: Int,
    val waterMl: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen (
    onNavigateBack: () -> Unit
) {
    val sampleRecord = listOf(
        HealthRecordUiModel("1", "Today, Nov 10", 6420, 450, 750),
        HealthRecordUiModel("2", "Yesterday, Nov 9", 5992, 430, 1550),
        HealthRecordUiModel("3", "Friday, Nov 8", 7624, 550, 1250),
        HealthRecordUiModel("4", "Thursday, Nov 7", 6530, 480, 820),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health History Log") },
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
    ) {
        innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleRecord) { record ->
                HealthRecordItemCard(record = record)
            }
        }
    }
}

@Composable
fun HealthRecordItemCard (record: HealthRecordUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = record.date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Steps: ${record.steps}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Calories: ${record.calories}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Water: ${record.waterMl}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}