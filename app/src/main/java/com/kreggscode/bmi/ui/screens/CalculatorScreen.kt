package com.kreggscode.bmi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
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
import com.kreggscode.bmi.ui.components.*
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.viewmodel.CalculatorViewModel
import com.kreggscode.bmi.viewmodel.CalculatorViewModelFactory
import kotlin.math.pow

@Composable
fun CalculatorScreen(
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val viewModel: CalculatorViewModel = viewModel(
        factory = CalculatorViewModelFactory(database.bmiDao())
    )
    val dynamicBottomPadding = calculateDynamicBottomPadding()

    var weight by remember { mutableFloatStateOf(70f) }
    var height by remember { mutableFloatStateOf(170f) }
    var showResult by remember { mutableStateOf(false) }
    var bmi by remember { mutableFloatStateOf(0f) }
    var category by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 60.dp, bottom = dynamicBottomPadding),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Text(
                text = "BMI Calculator",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Weight Slider
        item {
            GlassCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weight",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${weight.toInt()} kg",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = weight,
                        onValueChange = { weight = it },
                        valueRange = 30f..200f,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentBlue,
                            activeTrackColor = AccentBlue,
                            inactiveTrackColor = AccentBlue.copy(alpha = 0.2f)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "30 kg",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "200 kg",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // Height Slider
        item {
            GlassCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Height",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${height.toInt()} cm",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = height,
                        onValueChange = { height = it },
                        valueRange = 100f..250f,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentGreen,
                            activeTrackColor = AccentGreen,
                            inactiveTrackColor = AccentGreen.copy(alpha = 0.2f)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "100 cm",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "250 cm",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // Calculate Button
        item {
            Button(
                onClick = {
                    bmi = calculateBMI(weight, height)
                    category = getBMICategory(bmi)
                    showResult = true
                    viewModel.saveBMIRecord(weight, height, bmi, category)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(AccentPurple, AccentPink)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Calculate BMI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Result Section
        if (showResult) {
            item {
                AnimatedVisibility(
                    visible = showResult,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    GlassCard {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = "Your BMI Result",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            BMICircularIndicator(
                                bmi = bmi,
                                category = category
                            )

                            // Category Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = getCategoryColors(category)
                                        )
                                    )
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Additional Metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                MetricItem("Weight", "${weight.toInt()} kg", AccentBlue)
                                MetricItem("Height", "${height.toInt()} cm", AccentGreen)
                                MetricItem("BMI", String.format("%.1f", bmi), AccentPurple)
                            }

                            // AI Analysis Button
                            Button(
                                onClick = {
                                    viewModel.analyzeBMI(bmi, weight, height, category)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentPurple.copy(alpha = 0.2f)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = AccentPurple
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Analyze with AI",
                                    color = AccentPurple,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // AI Analysis Result
            item {
                val analysisState by viewModel.analysisState.collectAsState()
                
                when (analysisState) {
                    is CalculatorViewModel.AnalysisState.Loading -> {
                        GlassCard {
                            LoadingAnimation(text = "Analyzing your BMI...")
                        }
                    }
                    is CalculatorViewModel.AnalysisState.Success -> {
                        val analysis = (analysisState as CalculatorViewModel.AnalysisState.Success).analysis
                        GlassCard {
                            FormattedAIText(text = analysis)
                        }
                    }
                    is CalculatorViewModel.AnalysisState.Error -> {
                        val error = (analysisState as CalculatorViewModel.AnalysisState.Error).message
                        GlassCard {
                            Text(
                                text = "Error: $error",
                                color = AccentRed,
                                fontSize = 14.sp
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun MetricItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
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

fun calculateBMI(weight: Float, height: Float): Float {
    val heightInMeters = height / 100
    return weight / (heightInMeters.pow(2))
}

fun getBMICategory(bmi: Float): String {
    return when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
    }
}

fun getCategoryColors(category: String): List<Color> {
    return when (category.lowercase()) {
        "underweight" -> listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
        "normal" -> listOf(Color(0xFF10B981), Color(0xFF34D399))
        "overweight" -> listOf(Color(0xFFF59E0B), Color(0xFFFBBF24))
        "obese" -> listOf(Color(0xFFEF4444), Color(0xFFF87171))
        else -> listOf(Color.Gray, Color.LightGray)
    }
}

