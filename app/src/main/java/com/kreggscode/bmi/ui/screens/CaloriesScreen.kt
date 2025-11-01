package com.kreggscode.bmi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.data.model.MealLog
import com.kreggscode.bmi.ui.components.AnimatedCircularProgress
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.viewmodel.CaloriesViewModel
import com.kreggscode.bmi.viewmodel.CaloriesViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CaloriesScreen() {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val viewModel: CaloriesViewModel = viewModel(
        factory = CaloriesViewModelFactory(database.mealLogDao())
    )

    val mealLogs by viewModel.todayMealLogs.collectAsState(initial = emptyList())
    val totalCalories = mealLogs.sumOf { it.calories.toDouble() }.toFloat()
    val totalProtein = mealLogs.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = mealLogs.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = mealLogs.sumOf { it.fat.toDouble() }.toFloat()

    var showAddDialog by remember { mutableStateOf(false) }
    var showAIAnalysis by remember { mutableStateOf(false) }
    var showEditGoalDialog by remember { mutableStateOf(false) }
    var dailyCalorieGoal by remember { mutableStateOf(2000f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 60.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calorie Tracker",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.size(56.dp),
                    containerColor = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(AccentPurple, AccentPink)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Meal",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Today's Summary
        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Today's Intake",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AnimatedCircularProgress(
                            value = totalCalories,
                            maxValue = dailyCalorieGoal,
                            label = "Calories",
                            colors = listOf(AccentOrange, AccentYellow),
                            size = 100.dp,
                            strokeWidth = 10.dp
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MacroItem("Protein", totalProtein, AccentBlue)
                            MacroItem("Carbs", totalCarbs, AccentGreen)
                            MacroItem("Fat", totalFat, AccentRed)
                        }
                    }
                    
                    // Goal and AI Analysis buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showEditGoalDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AccentBlue
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Goal: ${dailyCalorieGoal.toInt()}")
                        }
                        
                        Button(
                            onClick = { showAIAnalysis = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentPurple
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Analysis")
                        }
                    }
                }
            }
        }

        // Meal Logs
        item {
            Text(
                text = "Today's Meals",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (mealLogs.isEmpty()) {
            item {
                GlassCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No meals logged today",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(mealLogs) { mealLog ->
                MealLogCard(
                    mealLog = mealLog,
                    onDelete = { viewModel.deleteMealLog(mealLog) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddMealDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { foodName, calories, protein, carbs, fat, mealType ->
                viewModel.addMealLog(foodName, calories, protein, carbs, fat, mealType)
                showAddDialog = false
            }
        )
    }
    
    if (showEditGoalDialog) {
        EditGoalDialog(
            currentGoal = dailyCalorieGoal,
            onDismiss = { showEditGoalDialog = false },
            onSave = { newGoal ->
                dailyCalorieGoal = newGoal
                showEditGoalDialog = false
            }
        )
    }
    
    if (showAIAnalysis) {
        CalorieAIAnalysisDialog(
            mealLogs = mealLogs,
            totalCalories = totalCalories,
            goal = dailyCalorieGoal,
            onDismiss = { showAIAnalysis = false }
        )
    }
}

@Composable
fun EditGoalDialog(
    currentGoal: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var goalText by remember { mutableStateOf(currentGoal.toInt().toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Daily Calorie Goal") },
        text = {
            OutlinedTextField(
                value = goalText,
                onValueChange = { goalText = it.filter { char -> char.isDigit() } },
                label = { Text("Daily Goal (kcal)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newGoal = goalText.toFloatOrNull() ?: currentGoal
                    onSave(newGoal)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CalorieAIAnalysisDialog(
    mealLogs: List<MealLog>,
    totalCalories: Float,
    goal: Float,
    onDismiss: () -> Unit
) {
    var aiResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val pollinationsService = com.kreggscode.bmi.data.api.PollinationsService()
                val mealSummary = mealLogs.joinToString(", ") { "${it.foodName} (${it.calories} kcal)" }
                val prompt = """
                    Analyze this daily calorie intake:
                    - Total Calories: ${totalCalories.toInt()} kcal
                    - Daily Goal: ${goal.toInt()} kcal
                    - Meals: $mealSummary
                    
                    Provide health insights, recommendations, and whether the intake is appropriate for the goal.
                """.trimIndent()
                
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
                Text("Calorie Analysis")
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
                        Text("Analyzing your intake...")
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

@Composable
fun MacroItem(
    label: String,
    value: Float,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "$label: ${String.format("%.1f", value)}g",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun MealLogCard(
    mealLog: MealLog,
    onDelete: () -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mealLog.foodName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${mealLog.calories.toInt()} cal • ${mealLog.mealType}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = "P: ${mealLog.protein.toInt()}g • C: ${mealLog.carbs.toInt()}g • F: ${mealLog.fat.toInt()}g",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
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
fun AddMealDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Float, Float, Float, Float, String) -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Breakfast") }
    var customMealType by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 600.dp) // Max height for scrolling
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        // FIXED: Use solid background with proper opacity for light/dark mode
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                    )
                    .clickable(onClick = {}) // Prevent dismiss on card click
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add Meal",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Meal Type Selection
                    Text(
                        text = "Meal Type",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Breakfast", "Lunch", "Dinner").forEach { type ->
                            FilterChip(
                                selected = selectedMealType == type,
                                onClick = { selectedMealType = type },
                                label = { Text(type) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentOrange.copy(alpha = 0.3f),
                                    selectedLabelColor = AccentOrange
                                )
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedMealType == "Snacks",
                            onClick = { selectedMealType = "Snacks" },
                            label = { Text("Snacks") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPink.copy(alpha = 0.3f),
                                selectedLabelColor = AccentPink
                            )
                        )
                        FilterChip(
                            selected = selectedMealType == "Other",
                            onClick = { selectedMealType = "Other" },
                            label = { Text("Other") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple.copy(alpha = 0.3f),
                                selectedLabelColor = AccentPurple
                            )
                        )
                    }

                    // Custom meal type input (only if "Other" is selected)
                    AnimatedVisibility(visible = selectedMealType == "Other") {
                        OutlinedTextField(
                            value = customMealType,
                            onValueChange = { customMealType = it },
                            label = { Text("Custom Meal Type") },
                            placeholder = { Text("e.g., Pre-workout, Post-workout") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentPurple,
                                focusedLabelColor = AccentPurple
                            )
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                    // Food Details
                    Text(
                        text = "Food Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name") },
                        placeholder = { Text("e.g., Grilled Chicken") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Restaurant, contentDescription = null)
                        }
                    )

                    // Nutritional Information
                    Text(
                        text = "Nutritional Information",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = calories,
                            onValueChange = { calories = it.filter { char -> char.isDigit() || char == '.' } },
                            label = { Text("Calories") },
                            placeholder = { Text("250") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = protein,
                            onValueChange = { protein = it.filter { char -> char.isDigit() || char == '.' } },
                            label = { Text("Protein (g)") },
                            placeholder = { Text("20") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = carbs,
                            onValueChange = { carbs = it.filter { char -> char.isDigit() || char == '.' } },
                            label = { Text("Carbs (g)") },
                            placeholder = { Text("30") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = fat,
                            onValueChange = { fat = it.filter { char -> char.isDigit() || char == '.' } },
                            label = { Text("Fat (g)") },
                            placeholder = { Text("10") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val finalMealType = if (selectedMealType == "Other") {
                                    customMealType.ifBlank { "Other" }
                                } else {
                                    selectedMealType
                                }
                                
                                onAdd(
                                    foodName.ifBlank { "Unnamed Food" },
                                    calories.toFloatOrNull() ?: 0f,
                                    protein.toFloatOrNull() ?: 0f,
                                    carbs.toFloatOrNull() ?: 0f,
                                    fat.toFloatOrNull() ?: 0f,
                                    finalMealType
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentOrange
                            )
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Add Meal")
                        }
                    }
                }
            }
        }
    }
}

