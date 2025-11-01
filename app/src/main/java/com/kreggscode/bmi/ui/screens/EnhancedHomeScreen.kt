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
import com.kreggscode.bmi.ui.components.calculateDynamicBottomPadding
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
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.data.model.DailyTracking
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EnhancedHomeScreen(
    onNavigate: (String) -> Unit,
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean
) {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()
    val dynamicBottomPadding = calculateDynamicBottomPadding()
    
    var animationStarted by remember { mutableStateOf(false) }
    var waterGlasses by remember { mutableStateOf(0) }
    val waterGoal = 8
    var sleepHours by remember { mutableStateOf(0f) }
    val sleepGoal = 8f
    
    // Get today's date
    val today = remember { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) }
    
    // Load today's tracking data
    val todayTracking by database.dailyTrackingDao().getTrackingForDate(today)
        .collectAsState(initial = null)
    
    // Update local state when data loads
    LaunchedEffect(todayTracking) {
        todayTracking?.let {
            waterGlasses = it.waterGlasses
            sleepHours = it.sleepHours
        }
    }
    
    // Save water glasses
    fun saveWaterGlasses(glasses: Int) {
        coroutineScope.launch {
            val tracking = todayTracking ?: DailyTracking(date = today)
            database.dailyTrackingDao().insertOrUpdate(
                tracking.copy(waterGlasses = glasses)
            )
        }
    }
    
    // Save sleep hours
    fun saveSleepHours(hours: Float) {
        coroutineScope.launch {
            val tracking = todayTracking ?: DailyTracking(date = today)
            database.dailyTrackingDao().insertOrUpdate(
                tracking.copy(sleepHours = hours)
            )
        }
    }
    
    // Greeting based on time
    val greeting = remember {
        val hour = try {
            LocalTime.now().hour
        } catch (e: Exception) {
            12
        }
        when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    // Animated background
    val infiniteTransition = rememberInfiniteTransition(label = "homeBackground")
    val backgroundRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(200)
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
            val centerY = size.height / 4

            for (i in 0..4) {
                val angle = backgroundRotation + i * 72f
                val radius = 180f + i * 70f
                val x = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * (radius / 3)
                val y = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * (radius / 3)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = 0.04f),
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
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 60.dp, bottom = dynamicBottomPadding),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Animated Header
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(800)) +
                           slideInVertically(animationSpec = tween(800))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = greeting,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = AccentPurple,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ready to crush your health goals?",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                lineHeight = 38.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Theme toggle
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            AccentPurple.copy(alpha = 0.2f),
                                            AccentPink.copy(alpha = 0.2f)
                                        )
                                    )
                                )
                                .clickable(onClick = onToggleTheme),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = AccentPurple,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Water Intake Tracker
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 200)) +
                           slideInHorizontally(animationSpec = tween(1000, delayMillis = 200))
                ) {
                    WaterIntakeCard(
                        currentGlasses = waterGlasses,
                        goalGlasses = waterGoal,
                        onAddGlass = { 
                            if (waterGlasses < waterGoal) {
                                waterGlasses++
                                saveWaterGlasses(waterGlasses)
                            }
                        },
                        onRemoveGlass = { 
                            if (waterGlasses > 0) {
                                waterGlasses--
                                saveWaterGlasses(waterGlasses)
                            }
                        },
                        onViewDetails = { onNavigate("water-tracker") }
                    )
                }
            }

            // Sleep Tracker
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 300))
                ) {
                    SleepTrackerCard(
                        sleepHours = sleepHours,
                        sleepGoal = sleepGoal,
                        onSleepChange = { 
                            sleepHours = it
                            saveSleepHours(it)
                        },
                        onViewDetails = { onNavigate("sleep-tracker") }
                    )
                }
            }

            // Quick Actions Row
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 400))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Quick Actions",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            item {
                                QuickActionCard(
                                    title = "Calculate BMI",
                                    icon = Icons.Default.Calculate,
                                    colors = listOf(AccentBlue, AccentTeal),
                                    onClick = { onNavigate("calculator") }
                                )
                            }
                            item {
                                QuickActionCard(
                                    title = "Scan Food",
                                    icon = Icons.Default.CameraAlt,
                                    colors = listOf(AccentOrange, AccentYellow),
                                    onClick = { onNavigate("scanner") }
                                )
                            }
                            item {
                                QuickActionCard(
                                    title = "Affirmations",
                                    icon = Icons.Default.AutoAwesome,
                                    colors = listOf(AccentPurple, AccentPink),
                                    onClick = { onNavigate("affirmations") }
                                )
                            }
                            item {
                                QuickActionCard(
                                    title = "Breathe",
                                    icon = Icons.Default.Spa,
                                    colors = listOf(AccentTeal, AccentGreen),
                                    onClick = { onNavigate("breathing") }
                                )
                            }
                        }
                    }
                }
            }

            // Health Encyclopedia Section
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 500))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Health Encyclopedia",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            EncyclopediaCard(
                                title = "Vitamins",
                                subtitle = "A-Z Guide",
                                icon = Icons.Default.Science,
                                colors = listOf(AccentOrange, AccentYellow),
                                onClick = { onNavigate("vitamins") },
                                modifier = Modifier.weight(1f)
                            )
                            EncyclopediaCard(
                                title = "Nutrients",
                                subtitle = "Macro & Micro",
                                icon = Icons.Default.Fastfood,
                                colors = listOf(AccentGreen, AccentTeal),
                                onClick = { onNavigate("nutrients") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            EncyclopediaCard(
                                title = "Blood Groups",
                                subtitle = "Know Your Type",
                                icon = Icons.Default.Bloodtype,
                                colors = listOf(AccentRed, AccentOrange),
                                onClick = { onNavigate("blood-groups") },
                                modifier = Modifier.weight(1f)
                            )
                            EncyclopediaCard(
                                title = "Water Info",
                                subtitle = "Hydration Tips",
                                icon = Icons.Default.WaterDrop,
                                colors = listOf(AccentBlue, AccentTeal),
                                onClick = { onNavigate("water-info") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Main Features Grid
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 600))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "All Features",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                EnhancedFeatureCard(
                                    title = "BMI Tracker",
                                    subtitle = "Track progress",
                                    icon = Icons.Default.TrendingUp,
                                    colors = listOf(AccentGreen, AccentTeal),
                                    onClick = { onNavigate("tracker") },
                                    modifier = Modifier.weight(1f)
                                )
                                EnhancedFeatureCard(
                                    title = "Diet Plans",
                                    subtitle = "AI powered",
                                    icon = Icons.Default.Restaurant,
                                    colors = listOf(AccentPurple, AccentPink),
                                    onClick = { onNavigate("diet") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                EnhancedFeatureCard(
                                    title = "Calorie Tracker",
                                    subtitle = "Daily goals",
                                    icon = Icons.Default.LocalFireDepartment,
                                    colors = listOf(AccentRed, AccentOrange),
                                    onClick = { onNavigate("calories") },
                                    modifier = Modifier.weight(1f)
                                )
                                EnhancedFeatureCard(
                                    title = "AI Chat",
                                    subtitle = "Ask anything",
                                    icon = Icons.Default.Chat,
                                    colors = listOf(AccentPink, AccentPurple),
                                    onClick = { onNavigate("chat") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                EnhancedFeatureCard(
                                    title = "Habits",
                                    subtitle = "Build routine",
                                    icon = Icons.Default.EmojiEvents,
                                    colors = listOf(AccentBlue, AccentTeal),
                                    onClick = { onNavigate("habits") },
                                    modifier = Modifier.weight(1f)
                                )
                                EnhancedFeatureCard(
                                    title = "To-Do List",
                                    subtitle = "Stay organized",
                                    icon = Icons.Default.CheckCircle,
                                    colors = listOf(AccentGreen, AccentTeal),
                                    onClick = { onNavigate("todo") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Wellness Tip
            item {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 800))
                ) {
                    WellnessTipCard()
                }
            }
        }
    }
}

@Composable
fun WaterIntakeCard(
    currentGlasses: Int,
    goalGlasses: Int,
    onAddGlass: () -> Unit,
    onRemoveGlass: () -> Unit,
    onViewDetails: () -> Unit = {}
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
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Water Intake",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "$currentGlasses / $goalGlasses glasses today",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            // Water glasses visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(goalGlasses) { index ->
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = if (index < currentGlasses) AccentBlue else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRemoveGlass,
                    modifier = Modifier.weight(1f),
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
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Remove",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Button(
                    onClick = onAddGlass,
                    modifier = Modifier.weight(1f),
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
                                    colors = listOf(AccentBlue, AccentTeal)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                            Text("Add Glass", color = Color.White)
                        }
                    }
                }
            }
            
            // View Details Button
            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AccentBlue
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("View Detailed Stats")
                }
            }
        }
    }
}

@Composable
fun SleepTrackerCard(
    sleepHours: Float,
    sleepGoal: Float,
    onSleepChange: (Float) -> Unit,
    onViewDetails: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        AccentPurple.copy(alpha = 0.15f),
                        AccentPink.copy(alpha = 0.15f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bedtime,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Sleep Tracker",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${String.format("%.1f", sleepHours)} / ${sleepGoal.toInt()} hours",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            Slider(
                value = sleepHours,
                onValueChange = onSleepChange,
                valueRange = 0f..12f,
                steps = 23,
                colors = SliderDefaults.colors(
                    thumbColor = AccentPurple,
                    activeTrackColor = AccentPurple,
                    inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                )
            )

            Text(
                text = when {
                    sleepHours < 6 -> "⚠️ You need more sleep for optimal health"
                    sleepHours in 6f..9f -> "✅ Great! You're getting enough sleep"
                    else -> "😴 That's a lot of sleep!"
                },
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // View Details Button
            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AccentPurple
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentPurple.copy(alpha = 0.5f))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("View Detailed Stats")
                }
            }
        }
    }
}

@Composable
fun EncyclopediaCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    colors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "encyclopediaScale"
    )

    Box(
        modifier = modifier
            .height(110.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(brush = Brush.linearGradient(colors))
            .clickable {
                isPressed = true
                onClick()
            }
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    colors: List<Color>,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "quickActionScale"
    )

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(140.dp)
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(brush = Brush.linearGradient(colors))
            .clickable {
                isPressed = true
                onClick()
            }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(44.dp),
                tint = Color.White
            )
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
fun EnhancedFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    colors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "featureScale"
    )

    Box(
        modifier = modifier
            .height(120.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .clickable {
                isPressed = true
                onClick()
            }
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(36.dp),
                tint = Color.White
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
fun WellnessTipCard() {
    val tips = remember {
        listOf(
            "Stay hydrated! Drink at least 8 glasses of water daily.",
            "Get 7-9 hours of quality sleep each night.",
            "Take breaks every hour to stretch and move.",
            "Practice mindful eating - savor each bite.",
            "Start your day with a nutritious breakfast.",
            "Vitamin D from sunlight boosts your immune system.",
            "Regular exercise improves both physical and mental health."
        )
    }

    var currentTipIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentTipIndex = (currentTipIndex + 1) % tips.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        AccentTeal.copy(alpha = 0.15f),
                        AccentGreen.copy(alpha = 0.15f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(AccentTeal, AccentGreen)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Wellness Tip",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal
                )
                Spacer(modifier = Modifier.height(6.dp))
                AnimatedContent(
                    targetState = tips[currentTipIndex],
                    transitionSpec = {
                        fadeIn(animationSpec = tween(600)) togetherWith
                                fadeOut(animationSpec = tween(600))
                    },
                    label = "tipAnimation"
                ) { tip ->
                    Text(
                        text = tip,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

