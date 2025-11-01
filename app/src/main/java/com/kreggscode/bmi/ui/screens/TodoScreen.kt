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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.data.model.TodoItem
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.viewmodel.TodoViewModel
import com.kreggscode.bmi.viewmodel.TodoViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen() {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val viewModel: TodoViewModel = viewModel(
        factory = TodoViewModelFactory(database.todoDao())
    )

    val activeTodos by viewModel.activeTodos.collectAsState(initial = emptyList())
    val completedTodos by viewModel.completedTodos.collectAsState(initial = emptyList())
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showCompleted by remember { mutableStateOf(false) }
    var showAISuggestions by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Health", "Fitness", "Nutrition", "Wellness", "Medical")
    val filteredTodos = if (selectedCategory == "All") {
        activeTodos
    } else {
        activeTodos.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("To-Do List") },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Stay on top of your health goals",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { showAISuggestions = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(AccentPurple, AccentBlue)
                                ),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "AI Suggestions",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(AccentPurple, AccentPink)
                                ),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Todo",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Stats Card
        item {
            val totalActive = activeTodos.size
            val totalCompleted = completedTodos.size
            val highPriority = activeTodos.count { it.priority == "High" }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                AccentGreen.copy(alpha = 0.2f),
                                AccentTeal.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatColumn("Active", totalActive.toString(), AccentBlue)
                    StatColumn("Completed", totalCompleted.toString(), AccentGreen)
                    StatColumn("High Priority", highPriority.toString(), AccentRed)
                }
            }
        }

        // Category Filter
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }
        }

        // Active Todos
        if (filteredTodos.isNotEmpty()) {
            items(filteredTodos) { todo ->
                TodoCard(
                    todo = todo,
                    onToggle = { viewModel.toggleTodoCompletion(todo) },
                    onDelete = { viewModel.deleteTodo(todo) }
                )
            }
        } else {
            item {
                EmptyState(
                    message = if (selectedCategory == "All") "No active tasks" else "No $selectedCategory tasks",
                    onAddClick = { showAddDialog = true }
                )
            }
        }

        // Show Completed Toggle
        if (completedTodos.isNotEmpty()) {
            item {
                TextButton(
                    onClick = { showCompleted = !showCompleted },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (showCompleted) "Hide Completed (${completedTodos.size})" else "Show Completed (${completedTodos.size})",
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (showCompleted) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (showCompleted) {
                items(completedTodos) { todo ->
                    TodoCard(
                        todo = todo,
                        onToggle = { viewModel.toggleTodoCompletion(todo) },
                        onDelete = { viewModel.deleteTodo(todo) }
                    )
                }
            }
        }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AddTodoDialog(
            onDismiss = { showAddDialog = false },
            onSave = { todo ->
                viewModel.addTodo(todo)
                showAddDialog = false
            }
        )
    }
    
    // AI Suggestions Dialog
    if (showAISuggestions) {
        AITodoSuggestionsDialog(
            onDismiss = { showAISuggestions = false },
            onSelectTodo = { todo ->
                viewModel.addTodo(todo)
                showAISuggestions = false
            }
        )
    }
}

@Composable
fun AITodoSuggestionsDialog(
    onDismiss: () -> Unit,
    onSelectTodo: (TodoItem) -> Unit
) {
    var aiSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val pollinationsService = com.kreggscode.bmi.data.api.PollinationsService()
                val prompt = """
                    Suggest 5 health-related tasks for someone's to-do list.
                    For each task, provide:
                    1. A clear, actionable task title (max 50 characters)
                    2. A brief description
                    Format each as: "TASK: [title] | DESC: [description]"
                """.trimIndent()
                
                val result = pollinationsService.chatWithAI(prompt, emptyList())
                result.onSuccess { response ->
                    aiSuggestions = response.lines()
                        .filter { it.contains("TASK:") }
                        .take(5)
                }.onFailure {
                    aiSuggestions = listOf(
                        "TASK: Schedule annual checkup | DESC: Book appointment with your doctor",
                        "TASK: Meal prep for the week | DESC: Plan and prepare healthy meals",
                        "TASK: Update medication list | DESC: Review and organize current medications",
                        "TASK: Track daily water intake | DESC: Monitor hydration throughout the day",
                        "TASK: Plan workout routine | DESC: Create a weekly exercise schedule"
                    )
                }
            } catch (e: Exception) {
                aiSuggestions = listOf(
                    "TASK: Schedule annual checkup | DESC: Book appointment with your doctor",
                    "TASK: Meal prep for the week | DESC: Plan and prepare healthy meals",
                    "TASK: Update medication list | DESC: Review and organize current medications",
                    "TASK: Track daily water intake | DESC: Monitor hydration throughout the day",
                    "TASK: Plan workout routine | DESC: Create a weekly exercise schedule"
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
                Text("AI Task Suggestions")
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
                        Text("Generating personalized tasks...")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(aiSuggestions) { suggestion ->
                            val parts = suggestion.split("|")
                            val taskTitle = parts.getOrNull(0)?.substringAfter("TASK:")?.trim() ?: ""
                            val taskDesc = parts.getOrNull(1)?.substringAfter("DESC:")?.trim() ?: ""
                            
                            if (taskTitle.isNotBlank()) {
                                GlassCard {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val todo = TodoItem(
                                                    title = taskTitle,
                                                    description = taskDesc,
                                                    category = "Health",
                                                    priority = "Medium",
                                                    dueDate = null
                                                )
                                                onSelectTodo(todo)
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
                                                text = taskTitle,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            if (taskDesc.isNotBlank()) {
                                                Text(
                                                    text = taskDesc,
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
fun TodoCard(
    todo: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (todo.priority) {
        "High" -> AccentRed
        "Medium" -> AccentOrange
        else -> AccentGreen
    }

    val scale by animateFloatAsState(
        targetValue = if (todo.isCompleted) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (todo.isCompleted) {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                }
            )
            .border(
                2.dp,
                if (!todo.isCompleted) priorityColor.copy(alpha = 0.3f) else Color.Transparent,
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (todo.isCompleted) AccentGreen else Color.Gray.copy(alpha = 0.2f)
                        )
                        .clickable(onClick = onToggle),
                    contentAlignment = Alignment.Center
                ) {
                    if (todo.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = todo.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
                    )
                    if (todo.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = todo.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category
                        Box(
                            modifier = Modifier
                                .background(
                                    getCategoryColor(todo.category).copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = todo.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = getCategoryColor(todo.category)
                            )
                        }
                        // Priority
                        Box(
                            modifier = Modifier
                                .background(
                                    priorityColor.copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = todo.priority,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor
                            )
                        }
                        // Due date
                        todo.dueDate?.let { date ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = formatDueDate(date),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
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
                    tint = AccentRed.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (category == "All") MaterialTheme.colorScheme.primary else getCategoryColor(category)
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) color else color.copy(alpha = 0.2f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = category,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else color
        )
    }
}

@Composable
fun StatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
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
fun EmptyState(message: String, onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
            Text(
                text = message,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Button(onClick = onAddClick) {
                Text("Add Task")
            }
        }
    }
}

@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onSave: (TodoItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Health") }
    var priority by remember { mutableStateOf("Medium") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                // Category
                Text("Category", fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Health", "Fitness", "Nutrition", "Wellness", "Medical")) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                // Priority
                Text("Priority", fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Low", "Medium", "High").forEach { pri ->
                        FilterChip(
                            selected = priority == pri,
                            onClick = { priority = pri },
                            label = { Text(pri) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val todo = TodoItem(
                            title = title,
                            description = description,
                            category = category,
                            priority = priority
                        )
                        onSave(todo)
                    }
                }
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Health" -> AccentBlue
        "Fitness" -> AccentGreen
        "Nutrition" -> AccentOrange
        "Wellness" -> AccentPurple
        "Medical" -> AccentRed
        else -> Color.Gray
    }
}

fun formatDueDate(date: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateObj = sdf.parse(date)
        val today = Calendar.getInstance()
        val dueDate = Calendar.getInstance().apply {
            time = dateObj ?: Date()
        }
        
        when {
            today.get(Calendar.YEAR) == dueDate.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == dueDate.get(Calendar.DAY_OF_YEAR) -> "Today"
            else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(dateObj)
        }
    } catch (e: Exception) {
        date
    }
}

