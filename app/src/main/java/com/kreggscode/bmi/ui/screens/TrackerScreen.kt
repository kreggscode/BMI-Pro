package com.kreggscode.bmi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.kreggscode.bmi.ui.components.calculateDynamicBottomPadding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.data.model.BMIRecord
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.viewmodel.TrackerViewModel
import com.kreggscode.bmi.viewmodel.TrackerViewModelFactory
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrackerScreen() {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val viewModel: TrackerViewModel = viewModel(
        factory = TrackerViewModelFactory(database.bmiDao())
    )
    val dynamicBottomPadding = calculateDynamicBottomPadding()

    val records by viewModel.records.collectAsState(initial = emptyList())
    var selectedPeriod by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 60.dp, bottom = dynamicBottomPadding),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Text(
                text = "BMI Tracker",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Period Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Week", "Month", "Year").forEach { period ->
                    PeriodChip(
                        text = period,
                        isSelected = selectedPeriod == period,
                        onClick = {
                            selectedPeriod = period
                            viewModel.filterRecords(period)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Statistics Card
        item {
            if (records.isNotEmpty()) {
                val avgBMI = records.map { it.bmi }.average().toFloat()
                val latestBMI = records.firstOrNull()?.bmi ?: 0f
                val change = if (records.size > 1) {
                    latestBMI - records[1].bmi
                } else 0f

                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Statistics",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                label = "Latest",
                                value = String.format("%.1f", latestBMI),
                                color = AccentBlue
                            )
                            StatItem(
                                label = "Average",
                                value = String.format("%.1f", avgBMI),
                                color = AccentGreen
                            )
                            StatItem(
                                label = "Change",
                                value = String.format("%+.1f", change),
                                color = if (change > 0) AccentRed else AccentGreen
                            )
                        }
                    }
                }
            }
        }

        // BMI Chart
        item {
            if (records.size >= 2) {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "BMI Trend",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        BMIChart(records = records.reversed().take(10))
                    }
                }
            }
        }

        // Records List
        item {
            Text(
                text = "History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (records.isEmpty()) {
            item {
                GlassCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No records yet",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Start tracking your BMI!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        } else {
            items(records) { record ->
                BMIRecordCard(
                    record = record,
                    onDelete = { viewModel.deleteRecord(record) }
                )
            }
        }
    }
}

@Composable
fun PeriodChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(AccentPurple, AccentPink)
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    )
                }
            )
            .then(
                Modifier.padding(0.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun BMIRecordCard(
    record: BMIRecord,
    onDelete: () -> Unit
) {
    val categoryColor = when (record.category.lowercase()) {
        "underweight" -> AccentBlue
        "normal" -> AccentGreen
        "overweight" -> AccentYellow
        "obese" -> AccentRed
        else -> Color.Gray
    }

    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BMI Circle
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    categoryColor.copy(alpha = 0.3f),
                                    categoryColor.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format("%.1f", record.bmi),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }

                Column {
                    Text(
                        text = record.category,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${record.weight.toInt()}kg • ${record.height.toInt()}cm",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatDate(record.timestamp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = AccentRed.copy(alpha = 0.7f)
                )
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun BMIChart(records: List<BMIRecord>) {
    if (records.isEmpty()) return
    
    // Create entry model with vararg Float values
    val bmiValues = records.map { it.bmi }.toTypedArray()
    val chartEntryModel = entryModelOf(*bmiValues)
    
    ProvideChartStyle {
        Chart(
            chart = lineChart(
                lines = listOf(
                    LineChart.LineSpec(
                        lineColor = AccentPurple.hashCode(),
                        lineBackgroundShader = null
                    )
                )
            ),
            model = chartEntryModel,
            startAxis = rememberStartAxis(
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 5)
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = { value, _ ->
                    val index = value.toInt()
                    if (index in records.indices) {
                        SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(records[index].timestamp))
                    } else ""
                },
                itemPlacer = AxisItemPlacer.Horizontal.default(
                    spacing = 1,
                    addExtremeLabelPadding = true
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

