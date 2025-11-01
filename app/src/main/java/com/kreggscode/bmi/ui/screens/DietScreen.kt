package com.kreggscode.bmi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.ui.components.FormattedAIText
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.components.LoadingAnimation
import com.kreggscode.bmi.ui.components.calculateDynamicBottomPadding
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.viewmodel.DietViewModel
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DietScreen() {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val viewModel: DietViewModel = viewModel()
    val dynamicBottomPadding = calculateDynamicBottomPadding()

    val dietPlanState by viewModel.dietPlanState.collectAsState()
    
    // Get latest BMI from database
    var latestBMI by remember { mutableStateOf<com.kreggscode.bmi.data.model.BMIRecord?>(null) }
    
    LaunchedEffect(Unit) {
        database.bmiDao().getAllRecords().collect { records ->
            latestBMI = records.firstOrNull()
        }
    }

    var selectedGoal by remember { mutableStateOf("Weight Loss") }
    var bmiInput by remember { mutableStateOf("") }
    
    LaunchedEffect(latestBMI) {
        if (latestBMI != null) {
            bmiInput = String.format("%.1f", latestBMI!!.bmi)
        }
    }
    
    val dietPlan = when (val state = dietPlanState) {
        is DietViewModel.DietPlanState.Success -> state.plan
        else -> null
    }
    
    val isLoading = dietPlanState is DietViewModel.DietPlanState.Loading
    var animationStarted by remember { mutableStateOf(false) }

    // Animated background
    val infiniteTransition = rememberInfiniteTransition(label = "dietBackground")
    val backgroundRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(100)
        animationStarted = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Animated background shapes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 3

            for (i in 0..5) {
                val angle = backgroundRotation + i * 60f
                val radius = 200f + i * 80f
                val x = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * (radius / 4)
                val y = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * (radius / 4)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF10B981).copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(x, y)
                    ),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 60.dp, bottom = dynamicBottomPadding, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Animated Header
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(800)) +
                           slideInVertically(animationSpec = tween(800))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.sweepGradient(
                                        colors = listOf(
                                            AccentGreen,
                                            AccentTeal,
                                            AccentBlue,
                                            AccentGreen
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "AI Diet Plans",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Personalized nutrition powered by AI",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // BMI Input Card
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 200)) +
                           slideInHorizontally(animationSpec = tween(1000, delayMillis = 200))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AccentBlue.copy(alpha = 0.15f),
                                        AccentTeal.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonitorWeight,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "Your BMI",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            OutlinedTextField(
                                value = bmiInput,
                                onValueChange = { bmiInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Enter your BMI") },
                                placeholder = { Text("e.g., 22.5") },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                ),
                                singleLine = true
                            )

                            if (latestBMI != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = AccentTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Latest BMI: ${String.format("%.1f", latestBMI!!.bmi)}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Goal Selection
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 400))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = AccentPurple,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Select Your Goal",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        val goals = listOf(
                            "Weight Loss" to listOf(AccentRed, AccentOrange),
                            "Muscle Gain" to listOf(AccentBlue, AccentTeal),
                            "Maintain Weight" to listOf(AccentGreen, AccentTeal),
                            "General Health" to listOf(AccentPurple, AccentPink)
                        )

                        goals.forEach { (goal, colors) ->
                            GoalCard(
                                goal = goal,
                                isSelected = selectedGoal == goal,
                                colors = colors,
                                onClick = { selectedGoal = goal }
                            )
                        }
                    }
                }
            }

            // Generate Button
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 600)) +
                           scaleIn(animationSpec = tween(1000, delayMillis = 600))
                ) {
                    Button(
                        onClick = {
                            val bmi = bmiInput.toFloatOrNull()
                            if (bmi != null) {
                                viewModel.generateDietPlan(bmi, selectedGoal)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        enabled = bmiInput.toFloatOrNull() != null && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(AccentGreen, AccentTeal, AccentBlue)
                                    ),
                                    shape = RoundedCornerShape(32.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Generate AI Diet Plan",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Diet Plan Result
            if (dietPlan != null) {
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(800)) +
                               expandVertically(animationSpec = tween(800))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Your Personalized Plan",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.surface,
                                                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                            )
                                        )
                                    )
                                    .padding(24.dp)
                            ) {
                                FormattedAIText(text = dietPlan!!)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: String,
    isSelected: Boolean,
    colors: List<Color>,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "goalScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    Brush.linearGradient(colors)
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                }
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                Color.White.copy(alpha = 0.3f)
                            } else {
                                Brush.linearGradient(colors).let { MaterialTheme.colorScheme.surface }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (goal) {
                        "Weight Loss" -> Icons.Default.TrendingDown
                        "Muscle Gain" -> Icons.Default.FitnessCenter
                        "Maintain Weight" -> Icons.Default.Balance
                        else -> Icons.Default.Favorite
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else colors.first(),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = goal,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = when (goal) {
                            "Weight Loss" -> "Reduce body weight healthily"
                            "Muscle Gain" -> "Build lean muscle mass"
                            "Maintain Weight" -> "Stay at current weight"
                            else -> "Improve overall wellness"
                        },
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
