package com.kreggscode.bmi.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTrackerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var selectedPeriod by remember { mutableStateOf("Week") }
    var showAIAnalysis by remember { mutableStateOf(false) }
    
    val today = remember { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) }
    val todayTracking by database.dailyTrackingDao().getTrackingForDate(today)
        .collectAsState(initial = null)
    
    // Get tracking data for selected period
    val trackingData by remember(selectedPeriod) {
        val endDate = LocalDate.now()
        val startDate = when (selectedPeriod) {
            "Day" -> endDate
            "Week" -> endDate.minusDays(6)
            "Month" -> endDate.minusDays(29)
            "Year" -> endDate.minusDays(364)
            else -> endDate.minusDays(6)
        }
        database.dailyTrackingDao().getTrackingBetweenDates(
            startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
    }.collectAsState(initial = emptyList())
    
    val currentValue = todayTracking?.sleepHours ?: 0f
    val goalValue = 8f // 8 hours goal
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sleep Tracker") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAIAnalysis = true }) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "AI Analysis",
                            tint = AccentPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Today's Progress
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Last Night's Sleep",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = String.format("%.1f / %.1f hours", currentValue, goalValue),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                AccentPurple.copy(alpha = 0.3f),
                                                AccentPink.copy(alpha = 0.1f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = String.format("%.1f", currentValue),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "hours",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                                }
                            }
                        }
                        
                        // Time breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val hours = currentValue.toInt()
                            val minutes = ((currentValue - hours) * 60).toInt()
                            val seconds = (((currentValue - hours) * 60 - minutes) * 60).toInt()
                            
                            TimeUnit(value = hours, label = "Hours", color = AccentPurple)
                            TimeUnit(value = minutes, label = "Minutes", color = AccentPink)
                            TimeUnit(value = seconds, label = "Seconds", color = AccentBlue)
                        }
                        
                        // Progress bar
                        LinearProgressIndicator(
                            progress = (currentValue / goalValue).coerceIn(0f, 1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = AccentPurple,
                            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                        )
                        
                        // Sleep quality indicator
                        val quality = when {
                            currentValue >= 7f -> "Excellent" to AccentGreen
                            currentValue >= 6f -> "Good" to AccentTeal
                            currentValue >= 5f -> "Fair" to AccentYellow
                            else -> "Poor" to AccentRed
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(quality.second.copy(alpha = 0.1f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = quality.second,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Sleep Quality: ${quality.first}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = quality.second
                            )
                        }
                    }
                }
            }
            
            // Period Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Day", "Week", "Month", "Year").forEach { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            label = { Text(period) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
            
            // Chart
            item {
                if (trackingData.isNotEmpty()) {
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Sleep Trend",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            
                            SleepChart(data = trackingData)
                        }
                    }
                }
            }
            
            // Statistics
            item {
                if (trackingData.isNotEmpty()) {
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Statistics",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            
                            val avgSleep = trackingData.map { it.sleepHours }.average().toFloat()
                            val totalSleep = trackingData.sumOf { it.sleepHours.toDouble() }.toFloat()
                            val daysGoalMet = trackingData.count { it.sleepHours >= goalValue }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    label = "Average",
                                    value = String.format("%.1f", avgSleep),
                                    unit = "hours",
                                    color = AccentPurple
                                )
                                StatItem(
                                    label = "Total",
                                    value = String.format("%.0f", totalSleep),
                                    unit = "hours",
                                    color = AccentPink
                                )
                                StatItem(
                                    label = "Goal Met",
                                    value = "$daysGoalMet",
                                    unit = "days",
                                    color = AccentGreen
                                )
                            }
                        }
                    }
                }
            }
            
            // Sleep Tips
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = AccentYellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Better Sleep Tips",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        listOf(
                            "Maintain a consistent sleep schedule",
                            "Create a relaxing bedtime routine",
                            "Keep your bedroom cool and dark",
                            "Avoid screens 1 hour before bed",
                            "Limit caffeine after 2 PM"
                        ).forEach { tip ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = tip,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // AI Analysis Dialog
    if (showAIAnalysis) {
        AIAnalysisDialog(
            title = "Sleep Analysis",
            data = trackingData,
            type = "sleep",
            onDismiss = { showAIAnalysis = false }
        )
    }
}

@Composable
fun SleepChart(data: List<com.kreggscode.bmi.data.model.DailyTracking>) {
    if (data.isEmpty()) return
    
    val chartValues = data.map { it.sleepHours }.toTypedArray()
    val chartEntryModel = entryModelOf(*chartValues)
    
    // Use theme-aware colors for chart text
    val textColor = MaterialTheme.colorScheme.onBackground
    
    // Create chart with VISIBLE lines and proper styling
    Chart(
        chart = lineChart(
            lines = listOf(
                LineChart.LineSpec(
                    lineColor = AccentPurple.toArgb(),
                    lineThicknessDp = 3f, // Make line visible
                    lineBackgroundShader = null,
                    point = com.patrykandpatrick.vico.core.component.shape.ShapeComponent(
                        shape = com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape,
                        color = AccentPurple.toArgb(),
                        strokeWidthDp = 2f,
                        strokeColor = Color.White.toArgb()
                    ),
                    pointSizeDp = 8f
                )
            )
        ),
        model = chartEntryModel,
        startAxis = rememberStartAxis(
            itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 6),
            labelRotationDegrees = 0f,
            tick = null,
            guideline = com.patrykandpatrick.vico.core.component.shape.LineComponent(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f).toArgb(),
                thicknessDp = 1f
            ),
            label = com.patrykandpatrick.vico.core.component.text.textComponent {
                color = textColor.toArgb()
                textSizeSp = 11f
                padding = com.patrykandpatrick.vico.core.dimensions.MutableDimensions(
                    startDp = 0f,
                    topDp = 0f,
                    endDp = 8f,
                    bottomDp = 0f
                )
            }
        ),
        bottomAxis = rememberBottomAxis(
            valueFormatter = { value, _ ->
                val index = value.toInt()
                if (index in data.indices) {
                    val date = LocalDate.parse(data[index].date)
                    SimpleDateFormat("MMM dd", Locale.getDefault()).format(
                        Date.from(date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
                    )
                } else ""
            },
            itemPlacer = AxisItemPlacer.Horizontal.default(
                spacing = 1,
                addExtremeLabelPadding = true
            ),
            labelRotationDegrees = 0f,
            tick = null,
            guideline = null,
            label = com.patrykandpatrick.vico.core.component.text.textComponent {
                color = textColor.toArgb()
                textSizeSp = 11f
                padding = com.patrykandpatrick.vico.core.dimensions.MutableDimensions(
                    startDp = 0f,
                    topDp = 8f,
                    endDp = 0f,
                    bottomDp = 0f
                )
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(vertical = 8.dp)
    )
}

@Composable
fun TimeUnit(value: Int, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

