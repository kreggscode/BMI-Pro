package com.kreggscode.bmi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.data.model.Habit
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.viewmodel.HabitsViewModel
import com.kreggscode.bmi.viewmodel.HabitsViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen() {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val viewModel: HabitsViewModel = viewModel(
        factory = HabitsViewModelFactory(database.habitDao())
    )

    val habits by viewModel.habits.collectAsState(initial = emptyList())
    val todayCompletions by viewModel.todayCompletions.collectAsState(initial = emptyMap())
    val streaks by viewModel.streaks.collectAsState(initial = emptyMap())
    
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showAISuggestions by remember { mutableStateOf(false) }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habits Tracker") },
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
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Build healthy habits, one day at a time",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                
                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // AI Suggestions Button
                    OutlinedButton(
                        onClick = { showAISuggestions = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AccentPurple
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentPurple.copy(alpha = 0.5f))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("AI Suggestions", fontSize = 13.sp)
                        }
                    }
                    
                    // Add Habit Button
                    Button(
                        onClick = { showAddHabitDialog = true },
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
                                    brush = Brush.linearGradient(
                                        colors = listOf(AccentPurple, AccentPink)
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
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("Add Habit", color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
        
        // AI Suggestions Card
        if (habits.isEmpty()) {
            item {
                GlassCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAISuggestions = true }
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(AccentPurple, AccentBlue)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Get AI Habit Suggestions",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentPurple
                            )
                            Text(
                                text = "Let AI suggest personalized healthy habits for you",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AccentPurple
                        )
                    }
                }
            }
        }

        // Today's Progress Card
        item {
            val completedToday = todayCompletions.count { it.value }
            val totalHabits = habits.size
            val progress = if (totalHabits > 0) completedToday.toFloat() / totalHabits else 0f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                AccentBlue.copy(alpha = 0.2f),
                                AccentTeal.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Today's Progress",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$completedToday",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue
                            )
                            Text(
                                text = " / $totalHabits",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                        Text(
                            text = "habits completed",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    
                    // Progress circle
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = AccentBlue,
                            strokeWidth = 8.dp,
                            trackColor = Color.Gray.copy(alpha = 0.2f)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )
                    }
                }
            }
        }

        // Weekly View
        item {
            Text(
                text = "This Week",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Habits List
        items(habits) { habit ->
            val isCompleted = todayCompletions[habit.id] == true
            val streak = streaks[habit.id] ?: 0
            
            HabitCard(
                habit = habit,
                isCompleted = isCompleted,
                streak = streak,
                onToggle = {
                    viewModel.toggleHabitCompletion(habit.id, today)
                },
                onEdit = { /* TODO */ },
                onDelete = { viewModel.deleteHabit(habit) }
            )
        }

        // Empty state
        if (habits.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "No habits yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Start building healthy habits today!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Button(
                            onClick = { showAddHabitDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(AccentPurple, AccentPink)
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(horizontal = 32.dp, vertical = 16.dp)
                            ) {
                                Text("Add First Habit", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
        }
    }

    // Add Habit Dialog
    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onSave = { habit ->
                viewModel.addHabit(habit)
                showAddHabitDialog = false
            }
        )
    }
    
    // AI Suggestions Dialog
    if (showAISuggestions) {
        AIHabitSuggestionsDialog(
            onDismiss = { showAISuggestions = false },
            onSelectHabit = { habit ->
                viewModel.addHabit(habit)
                showAISuggestions = false
            }
        )
    }
}

@Composable
fun AIHabitSuggestionsDialog(
    onDismiss: () -> Unit,
    onSelectHabit: (Habit) -> Unit
) {
    var aiSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val pollinationsService = com.kreggscode.bmi.data.api.PollinationsService()
                val prompt = """
                    Suggest 5 healthy habits for someone looking to improve their health and wellness.
                    For each habit, provide:
                    1. A clear, concise habit name (max 50 characters)
                    2. A brief description
                    Format each as: "HABIT: [name] | DESC: [description]"
                """.trimIndent()
                
                val result = pollinationsService.chatWithAI(prompt, emptyList())
                result.onSuccess { response ->
                    aiSuggestions = response.lines()
                        .filter { it.contains("HABIT:") }
                        .take(5)
                }.onFailure {
                    aiSuggestions = listOf(
                        "HABIT: Drink 8 glasses of water daily | DESC: Stay hydrated throughout the day",
                        "HABIT: 30 minutes of exercise | DESC: Move your body every day",
                        "HABIT: 7-8 hours of sleep | DESC: Get quality rest each night",
                        "HABIT: Eat 5 servings of fruits/vegetables | DESC: Nourish your body with whole foods",
                        "HABIT: 10 minutes of meditation | DESC: Practice mindfulness daily"
                    )
                }
            } catch (e: Exception) {
                aiSuggestions = listOf(
                    "HABIT: Drink 8 glasses of water daily | DESC: Stay hydrated throughout the day",
                    "HABIT: 30 minutes of exercise | DESC: Move your body every day",
                    "HABIT: 7-8 hours of sleep | DESC: Get quality rest each night",
                    "HABIT: Eat 5 servings of fruits/vegetables | DESC: Nourish your body with whole foods",
                    "HABIT: 10 minutes of meditation | DESC: Practice mindfulness daily"
                )
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
                Text("AI Habit Suggestions")
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
                        Text("Generating personalized habits...")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(aiSuggestions) { suggestion ->
                            val parts = suggestion.split("|")
                            val habitName = parts.getOrNull(0)?.substringAfter("HABIT:")?.trim() ?: ""
                            val habitDesc = parts.getOrNull(1)?.substringAfter("DESC:")?.trim() ?: ""
                            
                            if (habitName.isNotBlank()) {
                                GlassCard {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val habit = Habit(
                                                    name = habitName,
                                                    description = habitDesc,
                                                    category = "Health",
                                                    icon = "default",
                                                    color = "#6366F1",
                                                    frequency = "Daily"
                                                )
                                                onSelectHabit(habit)
                                            }
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = AccentPurple,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = habitName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            if (habitDesc.isNotBlank()) {
                                                Text(
                                                    text = habitDesc,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
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

@Composable
fun HabitCard(
    habit: Habit,
    isCompleted: Boolean,
    streak: Int,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isCompleted) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(android.graphics.Color.parseColor(habit.color)).copy(alpha = 0.3f),
                            Color(android.graphics.Color.parseColor(habit.color)).copy(alpha = 0.15f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        )
                    )
                }
            )
            .then(
                if (isCompleted) Modifier.border(
                    2.dp,
                    Color(android.graphics.Color.parseColor(habit.color)).copy(alpha = 0.5f),
                    RoundedCornerShape(24.dp)
                ) else Modifier
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) {
                                Color(android.graphics.Color.parseColor(habit.color))
                            } else {
                                Color.Gray.copy(alpha = 0.2f)
                            }
                        )
                        .clickable(onClick = onToggle),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = habit.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = habit.category,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        if (streak > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = AccentOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$streak day streak",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange
                                )
                            }
                        }
                    }
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

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onSave: (Habit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Health") }
    
    val habitColors = listOf("#6366F1", "#8B5CF6", "#EC4899", "#3B82F6", "#10B981", "#F59E0B", "#EF4444")
    var selectedColor by remember { mutableStateOf(habitColors.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Habit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit Name") },
                    placeholder = { Text("e.g., Drink 8 glasses of water") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                // Category
                Text("Category", fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Health", "Fitness", "Nutrition", "Wellness")) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                // Color picker
                Text("Color", fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    habitColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color(android.graphics.Color.parseColor(color)),
                                    CircleShape
                                )
                                .clickable { selectedColor = color }
                                .then(
                                    if (selectedColor == color) Modifier.border(
                                        3.dp,
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    ) else Modifier
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val habit = Habit(
                            name = name,
                            description = description,
                            category = category,
                            icon = "default",
                            color = selectedColor,
                            frequency = "Daily"
                        )
                        onSave(habit)
                    }
                }
            ) {
                Text("Add Habit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

