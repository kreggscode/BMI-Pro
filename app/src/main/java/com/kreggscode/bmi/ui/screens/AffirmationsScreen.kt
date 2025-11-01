package com.kreggscode.bmi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.data.model.Affirmation
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.viewmodel.AffirmationsViewModel
import com.kreggscode.bmi.viewmodel.AffirmationsViewModelFactory
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffirmationsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val viewModel: AffirmationsViewModel = viewModel(
        factory = AffirmationsViewModelFactory(database.affirmationDao())
    )

    val affirmations by viewModel.affirmations.collectAsState(initial = emptyList())
    val dailyAffirmation by viewModel.dailyAffirmation.collectAsState(initial = null)
    
    var selectedCategory by remember { mutableStateOf("All") }
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val categories = listOf("All", "Health", "Confidence", "Motivation", "Success", "Peace", "Gratitude")
    
    val filteredAffirmations = if (selectedCategory == "All") {
        affirmations
    } else {
        affirmations.filter { it.category == selectedCategory }
    }

    // Animated background
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val backgroundRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Affirmations") },
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
            // Animated background shapes
            Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 3
            
            for (i in 0..3) {
                val angle = backgroundRotation + i * 90f
                val radius = 200f + i * 50f
                val x = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * (radius / 2)
                val y = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * (radius / 2)
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF8B5CF6).copy(alpha = 0.05f),
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
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Daily Affirmation Card
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Today's Affirmation",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Empower your mind, transform your life",
                        fontSize = 15.sp,
                        color = AccentPurple,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Daily Featured Affirmation Card - STUNNING!
            item {
                dailyAffirmation?.let { affirmation ->
                    DailyAffirmationCard(
                        affirmation = affirmation,
                        onFavoriteToggle = { viewModel.toggleFavorite(affirmation) }
                    )
                }
            }

            // Category Filter with beautiful chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(categories) { category ->
                        CategoryFilterChip(
                            category = category,
                            isSelected = selectedCategory == category,
                            onClick = { 
                                selectedCategory = category
                                currentIndex = 0
                            }
                        )
                    }
                }
            }

            // Affirmations Carousel - STUNNING CARDS!
            if (filteredAffirmations.isNotEmpty()) {
                item {
                    AffirmationsCarousel(
                        affirmations = filteredAffirmations,
                        currentIndex = currentIndex,
                        onIndexChange = { currentIndex = it },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) }
                    )
                }
                
                // Navigation Dots
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        filteredAffirmations.take(10).forEachIndexed { index, _ ->
                            NavigationDot(isSelected = index == currentIndex)
                            if (index < filteredAffirmations.size - 1 && index < 9) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActionButton(
                        icon = Icons.Default.Shuffle,
                        label = "Random",
                        colors = listOf(AccentBlue, AccentTeal),
                        onClick = { 
                            if (filteredAffirmations.isNotEmpty()) {
                                currentIndex = (0 until filteredAffirmations.size).random()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ActionButton(
                        icon = Icons.Default.Favorite,
                        label = "Favorites",
                        colors = listOf(AccentPink, AccentPurple),
                        onClick = { /* Show favorites */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tips Card
            item {
                TipsCard()
            }
        }
        }
    }
}

@Composable
fun DailyAffirmationCard(
    affirmation: Affirmation,
    onFavoriteToggle: () -> Unit
) {
    val scale by rememberInfiniteTransition(label = "dailyScale").animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(32.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(android.graphics.Color.parseColor(affirmation.backgroundColor)),
                        Color(android.graphics.Color.parseColor(affirmation.backgroundColor)).copy(alpha = 0.7f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Decorative top element
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "Today's Affirmation",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 2.sp
            )

            Text(
                text = "\"${affirmation.text}\"",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )

            // Favorite button
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = if (affirmation.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AffirmationsCarousel(
    affirmations: List<Affirmation>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    onFavoriteToggle: (Affirmation) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX.absoluteValue > 100) {
                            if (offsetX > 0 && currentIndex > 0) {
                                onIndexChange(currentIndex - 1)
                            } else if (offsetX < 0 && currentIndex < affirmations.size - 1) {
                                onIndexChange(currentIndex + 1)
                            }
                        }
                        offsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                    }
                )
            }
    ) {
        affirmations.getOrNull(currentIndex)?.let { affirmation ->
            AffirmationCard(
                affirmation = affirmation,
                onFavoriteToggle = { onFavoriteToggle(affirmation) }
            )
        }
    }
}

@Composable
fun AffirmationCard(
    affirmation: Affirmation,
    onFavoriteToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(android.graphics.Color.parseColor(affirmation.backgroundColor)).copy(alpha = 0.3f),
                        Color(android.graphics.Color.parseColor(affirmation.backgroundColor)).copy(alpha = 0.15f)
                    )
                )
            )
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Category badge
            Box(
                modifier = Modifier
                    .background(
                        Color(android.graphics.Color.parseColor(affirmation.backgroundColor)).copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = affirmation.category,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(android.graphics.Color.parseColor(affirmation.backgroundColor)),
                    letterSpacing = 1.sp
                )
            }

            // Affirmation text
            Text(
                text = "\"${affirmation.text}\"",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Bottom actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(android.graphics.Color.parseColor(affirmation.backgroundColor)).copy(alpha = 0.2f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (affirmation.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color(android.graphics.Color.parseColor(affirmation.backgroundColor))
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(AccentPurple, AccentPink)
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                        )
                    )
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = category,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun NavigationDot(isSelected: Boolean) {
    val size by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "size"
    )

    Box(
        modifier = Modifier
            .size(size)
            .background(
                if (isSelected) AccentPurple else Color.Gray.copy(alpha = 0.3f),
                CircleShape
            )
    )
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    colors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(brush = Brush.linearGradient(colors))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun TipsCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        AccentTeal.copy(alpha = 0.2f),
                        AccentBlue.copy(alpha = 0.2f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AccentTeal,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Tips for Affirmations",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "• Read affirmations aloud for maximum impact\n" +
                      "• Practice daily, ideally in the morning\n" +
                      "• Visualize your affirmations as you read them\n" +
                      "• Believe in the words you're affirming",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                lineHeight = 22.sp
            )
        }
    }
}

