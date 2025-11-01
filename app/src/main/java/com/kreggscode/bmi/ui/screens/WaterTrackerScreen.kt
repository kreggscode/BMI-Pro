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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var selectedPeriod by remember { mutableStateOf("Week") }
    var selectedUnit by remember { mutableStateOf("Glasses") } // Glasses, Liters, Gallons
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
    
    // Convert glasses to selected unit
    fun convertValue(glasses: Int): Float {
        return when (selectedUnit) {
            "Liters" -> glasses * 0.25f // 1 glass = 250ml = 0.25L
            "Gallons" -> glasses * 0.066f // 1 glass = 0.066 gallons
            else -> glasses.toFloat()
        }
    }
    
    val currentValue = todayTracking?.waterGlasses ?: 0
    val goalValue = 8 // 8 glasses goal
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water Tracker") },
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
                                    text = "Today's Progress",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "$currentValue / $goalValue glasses",
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
                                                AccentBlue.copy(alpha = 0.3f),
                                                AccentTeal.copy(alpha = 0.1f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = String.format("%.1f", convertValue(currentValue)),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = when (selectedUnit) {
                                            "Liters" -> "L"
                                            "Gallons" -> "gal"
                                            else -> "glasses"
                                        },
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                        
                        // Progress bar
                        LinearProgressIndicator(
                            progress = (currentValue.toFloat() / goalValue).coerceIn(0f, 1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = AccentBlue,
                            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
            
            // Unit Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Glasses", "Liters", "Gallons").forEach { unit ->
                        FilterChip(
                            selected = selectedUnit == unit,
                            onClick = { selectedUnit = unit },
                            label = { Text(unit) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentBlue,
                                selectedLabelColor = Color.White
                            )
                        )
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
                                selectedContainerColor = AccentTeal,
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
                                text = "Water Intake Trend",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            
                            WaterChart(
                                data = trackingData,
                                selectedUnit = selectedUnit
                            )
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
                            
                            val avgGlasses = trackingData.map { it.waterGlasses }.average().toFloat()
                            val totalGlasses = trackingData.sumOf { it.waterGlasses }
                            val daysGoalMet = trackingData.count { it.waterGlasses >= goalValue }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    label = "Average",
                                    value = String.format("%.1f", convertValue(avgGlasses.toInt())),
                                    unit = when (selectedUnit) {
                                        "Liters" -> "L"
                                        "Gallons" -> "gal"
                                        else -> "glasses"
                                    },
                                    color = AccentBlue
                                )
                                StatItem(
                                    label = "Total",
                                    value = String.format("%.1f", convertValue(totalGlasses)),
                                    unit = when (selectedUnit) {
                                        "Liters" -> "L"
                                        "Gallons" -> "gal"
                                        else -> "glasses"
                                    },
                                    color = AccentTeal
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
            
            // Tips
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
                                text = "Hydration Tips",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        listOf(
                            "Drink water first thing in the morning",
                            "Keep a water bottle with you throughout the day",
                            "Set reminders to drink water regularly",
                            "Drink water before, during, and after exercise"
                        ).forEach { tip ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentTeal,
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
            title = "Water Intake Analysis",
            data = trackingData,
            type = "water",
            onDismiss = { showAIAnalysis = false }
        )
    }
}

@Composable
fun WaterChart(
    data: List<com.kreggscode.bmi.data.model.DailyTracking>,
    selectedUnit: String
) {
    if (data.isEmpty()) return
    
    fun convertValue(glasses: Int): Float {
        return when (selectedUnit) {
            "Liters" -> glasses * 0.25f
            "Gallons" -> glasses * 0.066f
            else -> glasses.toFloat()
        }
    }
    
    val chartValues = data.map { convertValue(it.waterGlasses) }.toTypedArray()
    val chartEntryModel = entryModelOf(*chartValues)
    
    // Use theme-aware colors for chart text
    val textColor = MaterialTheme.colorScheme.onBackground
    
    // Create chart with proper spacing and styling
    Chart(
        chart = columnChart(),
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
fun StatItem(
    label: String,
    value: String,
    unit: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = unit,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun AIAnalysisDialog(
    title: String,
    data: List<com.kreggscode.bmi.data.model.DailyTracking>,
    type: String,
    onDismiss: () -> Unit
) {
    var aiResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val pollinationsService = com.kreggscode.bmi.data.api.PollinationsService()
                val prompt = if (type == "water") {
                    val avgWater = data.map { it.waterGlasses }.average()
                    "Analyze this water intake data: Average ${avgWater.toInt()} glasses per day over ${data.size} days. Provide health insights and recommendations."
                } else {
                    val avgSleep = data.map { it.sleepHours }.average()
                    "Analyze this sleep data: Average ${String.format("%.1f", avgSleep)} hours per night over ${data.size} days. Provide health insights and recommendations."
                }
                
                val result = pollinationsService.chatWithAI(prompt, emptyList())
                result.onSuccess { response ->
                    aiResponse = response
                }.onFailure {
                    aiResponse = "Unable to generate analysis. Please try again."
                }
            } catch (e: Exception) {
                aiResponse = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = AccentPurple
                )
                Text(title)
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp)
            ) {
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = AccentPurple)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Analyzing your data...")
                    }
                } else {
                    com.kreggscode.bmi.ui.components.FormattedAIText(text = aiResponse)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

