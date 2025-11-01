package com.kreggscode.bmi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*

data class BloodGroup(
    val type: String,
    val description: String,
    val canDonateTo: String,
    val canReceiveFrom: String,
    val percentage: String,
    val characteristics: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodGroupsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All Types", "Compatibility", "Facts")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blood Groups Guide") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                contentColor = AccentRed
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> BloodTypesContent()
                1 -> CompatibilityContent()
                2 -> BloodFactsContent()
            }
        }
    }
}

@Composable
fun BloodTypesContent() {
    val bloodGroups = remember {
        listOf(
            BloodGroup(
                "A+",
                "Second most common blood type",
                "A+, AB+",
                "A+, A-, O+, O-",
                "~34% of population",
                "Can donate to A+ and AB+ recipients. Universal plasma donor potential."
            ),
            BloodGroup(
                "A-",
                "Relatively rare blood type",
                "A+, A-, AB+, AB-",
                "A-, O-",
                "~6% of population",
                "Can donate to all A and AB types. High demand in emergencies."
            ),
            BloodGroup(
                "B+",
                "Less common blood type",
                "B+, AB+",
                "B+, B-, O+, O-",
                "~9% of population",
                "Can donate to B+ and AB+ recipients. Important for specific ethnic groups."
            ),
            BloodGroup(
                "B-",
                "Rare blood type",
                "B+, B-, AB+, AB-",
                "B-, O-",
                "~2% of population",
                "Can donate to all B and AB types. Critical for rare blood type patients."
            ),
            BloodGroup(
                "AB+",
                "Universal plasma donor",
                "AB+",
                "All blood types",
                "~4% of population",
                "Universal recipient - can receive from any blood type. Plasma highly sought after."
            ),
            BloodGroup(
                "AB-",
                "Rarest blood type",
                "AB+, AB-",
                "AB-, A-, B-, O-",
                "~1% of population",
                "Rarest blood type. Can receive from all negative types. Plasma universal donor."
            ),
            BloodGroup(
                "O+",
                "Most common blood type",
                "O+, A+, B+, AB+",
                "O+, O-",
                "~38% of population",
                "Most common type. Can donate to all positive blood types. High demand."
            ),
            BloodGroup(
                "O-",
                "Universal blood donor",
                "All blood types",
                "O-",
                "~7% of population",
                "Universal donor - can donate to anyone. Always in critical demand for emergencies."
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            item {
                GlassCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(AccentRed, AccentOrange)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bloodtype,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Blood Type Guide",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Know your type and compatibility",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Why Blood Type Matters",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentRed
                        )
                        Text(
                            text = "Blood type is determined by antigens on red blood cells. Knowing your blood type is crucial for transfusions, pregnancy planning, and understanding health risks. Incompatible blood transfusions can be life-threatening.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            items(bloodGroups) { bloodGroup ->
                BloodGroupCard(bloodGroup)
            }
    }
}

@Composable
fun CompatibilityContent() {
    var selectedBloodType by remember { mutableStateOf("A+") }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Blood Type Compatibility Checker",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Text(
                        text = "Select your blood type:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    
                    // Blood type selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bloodTypes.take(4).forEach { type ->
                            FilterChip(
                                selected = selectedBloodType == type,
                                onClick = { selectedBloodType = type },
                                label = { Text(type) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentRed,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bloodTypes.drop(4).forEach { type ->
                            FilterChip(
                                selected = selectedBloodType == type,
                                onClick = { selectedBloodType = type },
                                label = { Text(type) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentRed,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
        
        item {
            val compatibility = getCompatibilityInfo(selectedBloodType)
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "If you are $selectedBloodType:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentRed
                    )
                    
                    BloodGroupInfoSection("You can donate to", compatibility.canDonateTo)
                    BloodGroupInfoSection("You can receive from", compatibility.canReceiveFrom)
                }
            }
        }
    }
}

@Composable
fun BloodFactsContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Interesting Blood Facts",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            FactCard(
                "🩸",
                "Universal Donor",
                "O- blood type is the universal donor and can be given to anyone in emergencies."
            )
        }
        item {
            FactCard(
                "💉",
                "Universal Recipient",
                "AB+ blood type is the universal recipient and can receive blood from any type."
            )
        }
        item {
            FactCard(
                "🔬",
                "Rh Factor",
                "The + or - in blood types refers to the Rh factor, a protein on red blood cells."
            )
        }
        item {
            FactCard(
                "🌍",
                "Global Distribution",
                "Blood type distribution varies by ethnicity and geographic region."
            )
        }
        item {
            FactCard(
                "⏱️",
                "Shelf Life",
                "Red blood cells can be stored for up to 42 days, platelets only 5 days."
            )
        }
        item {
            FactCard(
                "💪",
                "Donation Benefits",
                "Regular blood donation may reduce the risk of heart disease and cancer."
            )
        }
    }
}

@Composable
fun FactCard(emoji: String, title: String, description: String) {
    GlassCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentRed
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

fun getCompatibilityInfo(bloodType: String): BloodGroup {
    return when (bloodType) {
        "A+" -> BloodGroup("A+", "", "A+, AB+", "A+, A-, O+, O-", "", "")
        "A-" -> BloodGroup("A-", "", "A+, A-, AB+, AB-", "A-, O-", "", "")
        "B+" -> BloodGroup("B+", "", "B+, AB+", "B+, B-, O+, O-", "", "")
        "B-" -> BloodGroup("B-", "", "B+, B-, AB+, AB-", "B-, O-", "", "")
        "AB+" -> BloodGroup("AB+", "", "AB+", "All blood types", "", "")
        "AB-" -> BloodGroup("AB-", "", "AB+, AB-", "A-, B-, AB-, O-", "", "")
        "O+" -> BloodGroup("O+", "", "A+, B+, AB+, O+", "O+, O-", "", "")
        "O-" -> BloodGroup("O-", "", "All blood types", "O-", "", "")
        else -> BloodGroup("", "", "", "", "", "")
    }
}

@Composable
fun BloodGroupCard(bloodGroup: BloodGroup) {
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        AccentRed.copy(alpha = 0.3f),
                                        AccentRed.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bloodGroup.type,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentRed
                        )
                    }
                    Column {
                        Text(
                            text = bloodGroup.description,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = bloodGroup.percentage,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            BloodGroupInfoSection("Characteristics", bloodGroup.characteristics)
            BloodGroupInfoSection("Can Donate To", bloodGroup.canDonateTo)
            BloodGroupInfoSection("Can Receive From", bloodGroup.canReceiveFrom)
        }
    }
}

@Composable
fun BloodGroupInfoSection(label: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AccentOrange
        )
        Text(
            text = content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 20.sp
        )
    }
}

