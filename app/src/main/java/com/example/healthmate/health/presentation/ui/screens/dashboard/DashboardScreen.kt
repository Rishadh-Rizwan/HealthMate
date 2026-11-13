package com.example.healthmate.health.presentation.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.automirrored.filled.List
import com.example.healthmate.health.domain.model.ActivityLog
import com.example.healthmate.health.presentation.HealthViewModel
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private enum class MetricType {
    STEPS,
    CALORIES,
    WATER
}

private enum class ChartPeriod {
    HOURLY,
    DAILY
}

private data class ChartBar(
    val label: String,
    val value: Int
)

private fun getTodayDate(): String {
    return SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    ).format(Date())
}

private fun dateToUtcMillis(date: String): Long {
    val formatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )

    formatter.timeZone = TimeZone.getTimeZone("UTC")

    return formatter.parse(date)?.time ?: System.currentTimeMillis()
}

private fun utcMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )

    formatter.timeZone = TimeZone.getTimeZone("UTC")

    return formatter.format(Date(millis))
}

private fun getCalendarForDate(date: String): Calendar {
    val formatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )

    val calendar = Calendar.getInstance()

    val parsedDate = formatter.parse(date)

    if (parsedDate != null) {
        calendar.time = parsedDate
    }

    return calendar
}

private fun getDateDaysBefore(date: String, days: Int): String {
    val calendar = getCalendarForDate(date)
    calendar.add(Calendar.DAY_OF_YEAR, -days)

    return SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    ).format(calendar.time)
}

private fun getShortDate(date: String): String {
    val calendar = getCalendarForDate(date)

    return SimpleDateFormat(
        "EEE",
        Locale.getDefault()
    ).format(calendar.time)
}

private fun getHourlyBars(
    logs: List<ActivityLog>,
    metric: MetricType
): List<ChartBar> {

    val activityType = when (metric) {
        MetricType.STEPS -> "STEPS"
        MetricType.WATER -> "WATER"
        MetricType.CALORIES -> "CALORIES"
    }

    val hourlyValues = IntArray(24)

    logs.filter { it.activityType == activityType }.forEach { log ->

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = log.timestamp

        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        hourlyValues[hour] += log.amount
    }

    return (0 until 24).map { hour ->

        ChartBar(
            label = if (hour % 3 == 0) {
                String.format(Locale.getDefault(), "%02d", hour)
            } else {
                ""
            },
            value = hourlyValues[hour]
        )
    }
}

private fun getDailyBars(
    logs: List<ActivityLog>,
    metric: MetricType,
    selectedDate: String
): List<ChartBar> {

    val activityType = when (metric) {
        MetricType.STEPS -> "STEPS"
        MetricType.WATER -> "WATER"
        MetricType.CALORIES -> "CALORIES"
    }

    val dailyValues = logs
        .filter { it.activityType == activityType }
        .groupBy { it.date }

    return (6 downTo 0).map { daysBefore ->

        val date = getDateDaysBefore(selectedDate, daysBefore)

        val total = dailyValues[date]
            ?.sumOf { it.amount }
            ?: 0

        ChartBar(
            label = getShortDate(date),
            value = total
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onNavigateToAddRecord: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSearch: () -> Unit
) {

    val todayRecord by viewModel.todayRecord.collectAsState()

    var selectedMetric by remember {
        mutableStateOf<MetricType?>(null)
    }

    var amount by remember {
        mutableStateOf("")
    }

    var selectedChartMetric by remember {
        mutableStateOf(MetricType.STEPS)
    }

    var selectedPeriod by remember {
        mutableStateOf(ChartPeriod.HOURLY)
    }

    var selectedDate by remember {
        mutableStateOf(getTodayDate())
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val startDate = remember(selectedDate, selectedPeriod) {
        if (selectedPeriod == ChartPeriod.HOURLY) {
            selectedDate
        } else {
            getDateDaysBefore(selectedDate, 6)
        }
    }

    val endDate = selectedDate

    val logsFlow = remember(startDate, endDate) {
        viewModel.getActivityLogsBetween(startDate, endDate)
    }

    val activityLogs by logsFlow.collectAsState(
        initial = emptyList()
    )

    val chartBars = remember(
        activityLogs,
        selectedChartMetric,
        selectedPeriod,
        selectedDate
    ) {

        if (selectedPeriod == ChartPeriod.HOURLY) {
            getHourlyBars(activityLogs, selectedChartMetric)
        } else {
            getDailyBars(
                activityLogs,
                selectedChartMetric,
                selectedDate
            )
        }
    }

    var selectedBarIndex by remember(
        selectedChartMetric,
        selectedPeriod,
        selectedDate
    ) {
        mutableIntStateOf(
            if (selectedPeriod == ChartPeriod.HOURLY) {
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            } else {
                6
            }
        )
    }

    val chartColor = when (selectedChartMetric) {
        MetricType.STEPS -> Color(0xFF22C55E)
        MetricType.WATER -> Color(0xFF2196F3)
        MetricType.CALORIES -> Color(0xFFF97316)
    }

    val chartTitle = when (selectedChartMetric) {
        MetricType.STEPS -> "Steps Activity"
        MetricType.WATER -> "Water Intake"
        MetricType.CALORIES -> "Calories Burned"
    }

    val chartUnit = when (selectedChartMetric) {
        MetricType.STEPS -> "steps"
        MetricType.WATER -> "ml"
        MetricType.CALORIES -> "kcal"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "HealthMate",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "Today's Health Overview",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {

                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Search by Date"
                        )
                    }

                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "History Log"
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddRecord,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Record"
                )
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Daily Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            StepCounterCard(
                currentSteps = todayRecord?.steps ?: 0,
                targetSteps = 10000,
                isAutoTracking = true,
                onClick = {
                    amount = ""
                    selectedMetric = MetricType.STEPS
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                MetricCard(
                    title = "Calories",
                    value = "${todayRecord?.calories ?: 0} kcal",
                    subtitle = "Target: 600 kcal",
                    icon = Icons.Default.LocalFireDepartment,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        amount = ""
                        selectedMetric = MetricType.CALORIES
                    }
                )

                MetricCard(
                    title = "Water Intake",
                    value = "${todayRecord?.water ?: 0} ml",
                    subtitle = "Target: 2,500 ml",
                    icon = Icons.Default.WaterDrop,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = {
                        amount = ""
                        selectedMetric = MetricType.WATER
                    }
                )
            }

            HorizontalDivider()

            // ACTIVITY CHART SECTION

            Text(
                text = "Activity Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // METRIC SELECTION

                    Text(
                        text = "Select Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        FilterChip(
                            selected = selectedChartMetric == MetricType.STEPS,
                            onClick = {
                                selectedChartMetric = MetricType.STEPS
                            },
                            label = { Text("Steps") }
                        )

                        FilterChip(
                            selected = selectedChartMetric == MetricType.WATER,
                            onClick = {
                                selectedChartMetric = MetricType.WATER
                            },
                            label = { Text("Water") }
                        )

                        FilterChip(
                            selected = selectedChartMetric == MetricType.CALORIES,
                            onClick = {
                                selectedChartMetric = MetricType.CALORIES
                            },
                            label = { Text("Calories") }
                        )
                    }

                    // HOURLY / DAILY SELECTION

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        SegmentedButton(
                            selected = selectedPeriod == ChartPeriod.HOURLY,
                            onClick = {
                                selectedPeriod = ChartPeriod.HOURLY
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 0,
                                count = 2
                            )
                        ) {
                            Text("Hourly")
                        }

                        SegmentedButton(
                            selected = selectedPeriod == ChartPeriod.DAILY,
                            onClick = {
                                selectedPeriod = ChartPeriod.DAILY
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 1,
                                count = 2
                            )
                        ) {
                            Text("Daily")
                        }
                    }

                    // DATE PICKER

                    /*OutlinedButton(
                        onClick = {
                            showDatePicker = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.getDefault()
                            ).format(
                                getCalendarForDate(selectedDate).time
                            )
                        )
                    }*/

                    HorizontalDivider()

                    // CHART TITLE

                    Text(
                        text = chartTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = chartColor
                    )

                    Text(
                        text = if (selectedPeriod == ChartPeriod.HOURLY) {
                            "Activity recorded during each hour"
                        } else {
                            "Activity recorded over the last 7 days"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // SELECTED BAR VALUE

                    val selectedValue = chartBars
                        .getOrNull(selectedBarIndex)
                        ?.value ?: 0

                    Text(
                        text = "$selectedValue $chartUnit",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (activityLogs.none {
                            it.activityType == when (selectedChartMetric) {
                                MetricType.STEPS -> "STEPS"
                                MetricType.WATER -> "WATER"
                                MetricType.CALORIES -> "CALORIES"
                            }
                        }) {

                        Text(
                            text = "No activity recorded for this period yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // VERTICAL BAR CHART

                    ActivityBarChart(
                        bars = chartBars,
                        barColor = chartColor,
                        selectedIndex = selectedBarIndex,
                        onBarSelected = { index ->
                            selectedBarIndex = index
                        }
                    )

                    Text(
                        text = if (selectedPeriod == ChartPeriod.HOURLY) {
                            "Time of day"
                        } else {
                            "Day of week"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // MANUAL INPUT DIALOG

    selectedMetric?.let { metric ->

        val title = when (metric) {
            MetricType.STEPS -> "Add Steps"
            MetricType.CALORIES -> "Add Calories Burned"
            MetricType.WATER -> "Add Water Intake"
        }

        val unit = when (metric) {
            MetricType.STEPS -> "steps"
            MetricType.CALORIES -> "kcal"
            MetricType.WATER -> "ml"
        }

        AlertDialog(
            onDismissRequest = {
                selectedMetric = null
            },

            title = {
                Text(title)
            },

            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Enter the amount you want to add ($unit)."
                    )

                    if (metric == MetricType.WATER) {

                        Text(
                            text = "Quick Add",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            listOf(150, 250, 500).forEach { waterAmount ->

                                OutlinedButton(
                                    onClick = {
                                        viewModel.onAddWater(waterAmount)
                                        selectedMetric = null
                                    },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(
                                        horizontal = 4.dp,
                                        vertical = 8.dp
                                    )
                                ) {
                                    Text("${waterAmount}ml")
                                }
                            }
                        }

                        HorizontalDivider()

                        Text("Or enter a custom amount:")
                    }

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                amount = newValue
                            }
                        },
                        label = {
                            Text("Amount ($unit)")
                        },
                        placeholder = {
                            Text("Enter amount")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val enteredAmount = amount.toIntOrNull()

                        if (enteredAmount != null && enteredAmount > 0) {

                            when (metric) {

                                MetricType.STEPS -> {
                                    viewModel.onAddSteps(enteredAmount)
                                }

                                MetricType.CALORIES -> {
                                    viewModel.onAddCalories(enteredAmount)
                                }

                                MetricType.WATER -> {
                                    viewModel.onAddWater(enteredAmount)
                                }
                            }

                            selectedMetric = null
                        }
                    },
                    enabled = (amount.toIntOrNull() ?: 0) > 0
                ) {
                    Text("Add")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        selectedMetric = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // DATE PICKER DIALOG

    if (showDatePicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateToUtcMillis(selectedDate)
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = utcMillisToDate(millis)
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("Select")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }

        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }
}

@Composable
private fun ActivityBarChart(
    bars: List<ChartBar>,
    barColor: Color,
    selectedIndex: Int,
    onBarSelected: (Int) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .pointerInput(bars.size) {

                    detectTapGestures { offset ->

                        if (bars.isNotEmpty()) {

                            val slotWidth = size.width / bars.size

                            val index = (
                                    offset.x / slotWidth
                                    ).toInt().coerceIn(
                                    0,
                                    bars.lastIndex
                                )

                            onBarSelected(index)
                        }
                    }
                }
        ) {

            if (bars.isNotEmpty()) {

                val maximumValue = (bars.maxOfOrNull {
                    it.value
                } ?: 0).coerceAtLeast(1)

                val chartHeight = size.height - 8.dp.toPx()

                // HORIZONTAL GRID LINES

                for (line in 0..4) {

                    val y = chartHeight -
                            (chartHeight * line / 4f)

                    drawLine(
                        color = Color.Gray.copy(alpha = 0.20f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // VERTICAL BARS

                val slotWidth = size.width / bars.size
                val barWidth = slotWidth * 0.55f

                bars.forEachIndexed { index, bar ->

                    val barHeight =
                        (bar.value.toFloat() / maximumValue) *
                                (chartHeight - 8.dp.toPx())

                    val left = (
                            index * slotWidth
                            ) + ((slotWidth - barWidth) / 2f)

                    val top = chartHeight - barHeight

                    if (bar.value > 0) {

                        drawRoundRect(
                            color = if (index == selectedIndex) {
                                barColor
                            } else {
                                barColor.copy(alpha = 0.65f)
                            },
                            topLeft = Offset(left, top),
                            size = Size(
                                barWidth,
                                barHeight
                            ),
                            cornerRadius = CornerRadius(
                                6.dp.toPx(),
                                6.dp.toPx()
                            )
                        )
                    }
                }
            }
        }

        // CHART LABELS

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            bars.forEach { bar ->

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = bar.label,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StepCounterCard(
    currentSteps: Int,
    targetSteps: Int,
    isAutoTracking: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val progress = (
            currentSteps.toFloat() / targetSteps.toFloat()
            ).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = "Steps",
                        tint = Color(0xFF22C55E)
                    )

                    Text(
                        text = "Steps Tracked",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                ) {

                    Text(
                        text = if (isAutoTracking) {
                            "Auto (Sensor)"
                        } else {
                            "Manual"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = "$currentSteps",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF22C55E),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "$currentSteps / $targetSteps steps",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "Tap to add",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = when (title) {
                        "Calories" -> Color(0xFFF97316)
                        "Water Intake" -> Color(0xFF2196F3)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Tap to add",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
