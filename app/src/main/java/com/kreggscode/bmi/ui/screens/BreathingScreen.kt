package com.kreggscode.bmi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.bmi.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(onBack: () -> Unit = {}) {
    var selectedExercise by remember { mutableStateOf(BreathingExercise.Box) }
    var isActive by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.Inhale) }
    var cyclesCompleted by remember { mutableStateOf(0) }
    var timeRemaining by remember { mutableStateOf(0) }

    // Animated background with gentle waves
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    LaunchedEffect(isActive) {
        if (isActive) {
            while (isActive) {
                // Inhale phase
                currentPhase = BreathingPhase.Inhale
                for (i in selectedExercise.inhaleSeconds downTo 1) {
                    timeRemaining = i
                    delay(1000)
                }
                
                // Hold phase (if applicable)
                if (selectedExercise.holdSeconds > 0) {
                    currentPhase = BreathingPhase.Hold
                    for (i in selectedExercise.holdSeconds downTo 1) {
                        timeRemaining = i
                        delay(1000)
                    }
                }
                
                // Exhale phase
                currentPhase = BreathingPhase.Exhale
                for (i in selectedExercise.exhaleSeconds downTo 1) {
                    timeRemaining = i
                    delay(1000)
                }
                
                // Rest phase (if applicable)
                if (selectedExercise.restSeconds > 0) {
                    currentPhase = BreathingPhase.Rest
                    for (i in selectedExercise.restSeconds downTo 1) {
                        timeRemaining = i
                        delay(1000)
                    }
                }
                
                cyclesCompleted++
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breathing Exercises") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Animated wave background
            Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            for (i in 0..5) {
                val angle = wave1 + i * 60f
                val radius = 150f + i * 80f
                val x = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * (radius / 4)
                val y = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * (radius / 4)
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentBlue.copy(alpha = 0.03f),
                            Color.Transparent
                        ),
                        center = Offset(x, y)
                    ),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Exercise Selection
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Choose Your Exercise",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Calm your mind, relax your body",
                    fontSize = 15.sp,
                    color = AccentBlue,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }

            // Central breathing visualization
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BreathingVisualization(
                    isActive = isActive,
                    phase = currentPhase,
                    timeRemaining = timeRemaining,
                    exercise = selectedExercise
                )
            }

            // Exercise selector and controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(bottom = 100.dp)
            ) {
                // Stats card
                if (cyclesCompleted > 0) {
                    StatsCard(cyclesCompleted = cyclesCompleted)
                }

                // Exercise selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BreathingExercise.values().forEach { exercise ->
                        ExerciseButton(
                            exercise = exercise,
                            isSelected = selectedExercise == exercise,
                            onClick = { 
                                if (!isActive) {
                                    selectedExercise = exercise
                                    cyclesCompleted = 0
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Play/Pause button - STUNNING!
                PlayPauseButton(
                    isActive = isActive,
                    onClick = { 
                        isActive = !isActive
                        if (!isActive) {
                            currentPhase = BreathingPhase.Inhale
                        }
                    }
                )
            }
        }
        }
    }
}

@Composable
fun BreathingVisualization(
    isActive: Boolean,
    phase: BreathingPhase,
    timeRemaining: Int,
    exercise: BreathingExercise
) {
    val targetScale = when (phase) {
        BreathingPhase.Inhale -> 1.3f
        BreathingPhase.Hold -> 1.3f
        BreathingPhase.Exhale -> 0.7f
        BreathingPhase.Rest -> 0.7f
    }

    val animationDuration = when (phase) {
        BreathingPhase.Inhale -> exercise.inhaleSeconds * 1000
        BreathingPhase.Hold -> exercise.holdSeconds * 1000
        BreathingPhase.Exhale -> exercise.exhaleSeconds * 1000
        BreathingPhase.Rest -> exercise.restSeconds * 1000
    }

    val scale by animateFloatAsState(
        targetValue = if (isActive) targetScale else 1f,
        animationSpec = tween(
            durationMillis = if (isActive) animationDuration else 1000,
            easing = if (phase == BreathingPhase.Hold || phase == BreathingPhase.Rest) 
                LinearEasing else FastOutSlowInEasing
        ),
        label = "breathScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isActive) 0.8f else 0.4f,
        animationSpec = tween(1000),
        label = "breathAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Outer glowing rings
        for (i in 1..3) {
            Box(
                modifier = Modifier
                    .size(280.dp + (i * 40).dp)
                    .scale(scale * (1f + i * 0.05f))
                    .alpha(alpha / (i + 1))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                phase.color.copy(alpha = 0.3f / i),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Main breathing circle
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(scale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            phase.color.copy(alpha = 0.6f),
                            phase.color.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Phase icon
                Icon(
                    imageVector = phase.icon,
                    contentDescription = phase.name,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )

                // Phase text
                Text(
                    text = if (isActive) phase.instruction else "Tap Play to Begin",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Timer
                if (isActive) {
                    Text(
                        text = "$timeRemaining",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseButton(
    exercise: BreathingExercise,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "exerciseScale"
    )

    Box(
        modifier = modifier
            .height(100.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    Brush.verticalGradient(
                        colors = listOf(exercise.color, exercise.color.copy(alpha = 0.7f))
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                        )
                    )
                }
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = exercise.icon,
                contentDescription = exercise.name,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = exercise.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PlayPauseButton(
    isActive: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "playScale"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = if (isActive) {
                        listOf(AccentPink, AccentPurple)
                    } else {
                        listOf(AccentBlue, AccentTeal)
                    }
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isActive) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun StatsCard(cyclesCompleted: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        AccentPurple.copy(alpha = 0.2f),
                        AccentBlue.copy(alpha = 0.2f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = AccentPurple,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = "Cycles Completed",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$cyclesCompleted",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

// Data classes for breathing exercises
enum class BreathingExercise(
    val exerciseName: String,
    val inhaleSeconds: Int,
    val holdSeconds: Int,
    val exhaleSeconds: Int,
    val restSeconds: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    Box("Box", 4, 4, 4, 0, Icons.Default.AllInclusive, AccentBlue),
    DeepCalm("Deep", 6, 0, 6, 0, Icons.Default.Spa, AccentTeal),
    Relax("Relax", 4, 7, 8, 0, Icons.Default.SelfImprovement, AccentPurple)
}

enum class BreathingPhase(
    val instruction: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    Inhale("Breathe In", Icons.Default.Air, AccentBlue),
    Hold("Hold", Icons.Default.PauseCircle, AccentTeal),
    Exhale("Breathe Out", Icons.Default.WindPower, AccentPurple),
    Rest("Rest", Icons.Default.Check, AccentPink)
}

