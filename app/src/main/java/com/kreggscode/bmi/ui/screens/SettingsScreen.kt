package com.kreggscode.bmi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.data.model.UserProfile
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.viewmodel.SettingsViewModel
import com.kreggscode.bmi.viewmodel.SettingsViewModelFactory

@Composable
fun SettingsScreen(onToggleTheme: () -> Unit, isDarkTheme: Boolean) {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(database.userProfileDao())
    )

    val profiles by viewModel.profiles.collectAsState(initial = emptyList())
    val activeProfile by viewModel.activeProfile.collectAsState(initial = null)
    
    var showAddProfileDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf<UserProfile?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 60.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Text(
                text = "Settings",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Active Profile Card
        item {
            if (activeProfile != null) {
                ActiveProfileCard(
                    profile = activeProfile!!,
                    onEdit = { showEditProfileDialog = activeProfile }
                )
            }
        }

        // Profiles Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Family Profiles",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = { showAddProfileDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(AccentPurple, AccentPink)
                            ),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Profile",
                        tint = Color.White
                    )
                }
            }
        }

        items(profiles) { profile ->
            ProfileCard(
                profile = profile,
                isActive = profile.id == activeProfile?.id,
                onSelect = { viewModel.setActiveProfile(profile.id) },
                onEdit = { showEditProfileDialog = profile },
                onDelete = { viewModel.deleteProfile(profile) }
            )
        }

        // App Settings Section
        item {
            Text(
                text = "App Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        item {
            SettingItem(
                icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                title = "Theme",
                subtitle = if (isDarkTheme) "Dark Mode" else "Light Mode",
                colors = listOf(AccentPurple, AccentPink),
                onClick = onToggleTheme
            )
        }

        item {
            SettingItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Reminders and alerts",
                colors = listOf(AccentBlue, AccentTeal),
                onClick = { /* TODO: Implement notifications */ }
            )
        }

        item {
            SettingItem(
                icon = Icons.Default.Lock,
                title = "Privacy",
                subtitle = "Data and security settings",
                colors = listOf(AccentGreen, AccentTeal),
                onClick = { /* TODO: Implement privacy */ }
            )
        }

        item {
            SettingItem(
                icon = Icons.Default.CloudUpload,
                title = "Backup & Sync",
                subtitle = "Save your data to cloud",
                colors = listOf(AccentOrange, AccentYellow),
                onClick = { /* TODO: Implement backup */ }
            )
        }

        item {
            SettingItem(
                icon = Icons.Default.Info,
                title = "About",
                subtitle = "Version 2.0 • BMI Pro",
                colors = listOf(AccentPink, AccentPurple),
                onClick = { /* TODO: Implement about */ }
            )
        }
        
        // Support Section
        item {
            Text(
                text = "Support",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.Star,
                title = "Rate App",
                subtitle = "Love the app? Rate us!",
                colors = listOf(AccentYellow, AccentOrange),
                onClick = {
                    // Open Play Store rating
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.kreggscode.bmi")
                    }
                    context.startActivity(intent)
                }
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.Share,
                title = "Share App",
                subtitle = "Tell your friends about BMI Pro",
                colors = listOf(AccentBlue, AccentTeal),
                onClick = {
                    val sendIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        putExtra(
                            android.content.Intent.EXTRA_TEXT,
                            "Check out BMI Pro - Your Complete Health Companion!\n\nhttps://play.google.com/store/apps/details?id=com.kreggscode.bmi"
                        )
                        type = "text/plain"
                    }
                    val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.Apps,
                title = "More Apps",
                subtitle = "Try my other apps",
                colors = listOf(AccentPurple, AccentPink),
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("https://play.google.com/store/apps/developer?id=Kregg")
                    }
                    context.startActivity(intent)
                }
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.ShoppingCart,
                title = "Support Developer",
                subtitle = "Purchase premium apps",
                colors = listOf(AccentGreen, AccentTeal),
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.kreggscode.bmicalculator")
                    }
                    context.startActivity(intent)
                }
            )
        }
    }

    // Add Profile Dialog
    if (showAddProfileDialog) {
        AddEditProfileDialog(
            profile = null,
            onDismiss = { showAddProfileDialog = false },
            onSave = { profile ->
                viewModel.addProfile(profile)
                showAddProfileDialog = false
            }
        )
    }

    // Edit Profile Dialog
    showEditProfileDialog?.let { profile ->
        AddEditProfileDialog(
            profile = profile,
            onDismiss = { showEditProfileDialog = null },
            onSave = { updatedProfile ->
                viewModel.updateProfile(updatedProfile)
                showEditProfileDialog = null
            }
        )
    }
}

@Composable
fun ActiveProfileCard(profile: UserProfile, onEdit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(android.graphics.Color.parseColor(profile.avatarColor)).copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = Color(android.graphics.Color.parseColor(profile.avatarColor)),
                            shape = CircleShape
                        )
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.name.take(2).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(AccentGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                        }
                    }
                    Text(
                        text = "${profile.age} years • ${profile.gender} • ${profile.goal}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    profile: UserProfile,
    isActive: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .then(
                if (isActive) Modifier.border(
                    2.dp,
                    Brush.horizontalGradient(listOf(AccentPurple, AccentPink)),
                    RoundedCornerShape(24.dp)
                ) else Modifier
            )
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
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color(android.graphics.Color.parseColor(profile.avatarColor)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.name.take(2).uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column {
                    Text(
                        text = profile.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${profile.age} years • ${profile.gender}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = AccentBlue
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = AccentRed
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Profile") },
            text = { Text("Are you sure you want to delete ${profile.name}'s profile? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    colors: List<Color>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(
                brush = Brush.horizontalGradient(
                    colors = colors.map { it.copy(alpha = 0.15f) }
                )
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(colors),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun AddEditProfileDialog(
    profile: UserProfile?,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var age by remember { mutableStateOf(profile?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(profile?.gender ?: "Male") }
    var height by remember { mutableStateOf(profile?.height?.toString() ?: "") }
    var targetWeight by remember { mutableStateOf(profile?.targetWeight?.toString() ?: "") }
    var goal by remember { mutableStateOf(profile?.goal ?: "General Health") }
    var activityLevel by remember { mutableStateOf(profile?.activityLevel ?: "Moderate") }
    
    val avatarColors = listOf("#6366F1", "#8B5CF6", "#EC4899", "#3B82F6", "#10B981", "#F59E0B", "#EF4444")
    var selectedColor by remember { mutableStateOf(profile?.avatarColor ?: avatarColors.random()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (profile == null) "Add Profile" else "Edit Profile") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Avatar color picker
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        avatarColors.forEach { color ->
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
                
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                item {
                    Text("Gender", fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Male", "Female", "Other").forEach { g ->
                            FilterChip(
                                selected = gender == g,
                                onClick = { gender = g },
                                label = { Text(g) }
                            )
                        }
                    }
                }
                
                item {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (cm)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = targetWeight,
                        onValueChange = { targetWeight = it },
                        label = { Text("Target Weight (kg)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                item {
                    Text("Goal", fontWeight = FontWeight.Medium)
                    listOf("Weight Loss", "Muscle Gain", "Maintain Weight", "General Health").forEach { g ->
                        FilterChip(
                            selected = goal == g,
                            onClick = { goal = g },
                            label = { Text(g) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && age.isNotBlank()) {
                        val newProfile = UserProfile(
                            id = profile?.id ?: 0,
                            name = name,
                            age = age.toIntOrNull() ?: 25,
                            gender = gender,
                            height = height.toFloatOrNull() ?: 170f,
                            targetWeight = targetWeight.toFloatOrNull() ?: 70f,
                            goal = goal,
                            activityLevel = activityLevel,
                            avatarColor = selectedColor,
                            isActive = profile?.isActive ?: false
                        )
                        onSave(newProfile)
                    }
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

