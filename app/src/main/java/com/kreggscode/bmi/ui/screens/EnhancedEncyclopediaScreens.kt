package com.kreggscode.bmi.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*

// Enhanced Nutrients Screen with Tabs
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedNutrientsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Macros", "Minerals", "Fiber", "Fats")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nutrients Guide") },
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
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                contentColor = AccentGreen
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Content
            when (selectedTab) {
                0 -> MacrosContent()
                1 -> MineralsContent()
                2 -> FiberContent()
                3 -> FatsContent()
            }
        }
    }
}

@Composable
fun MacrosContent() {
    val macros = listOf(
        NutrientInfo(
            "Protein",
            "Building blocks of body tissue",
            "Meat, fish, eggs, dairy, legumes, nuts",
            "0.8g per kg body weight",
            AccentBlue,
            Icons.Default.FitnessCenter
        ),
        NutrientInfo(
            "Carbohydrates",
            "Primary energy source for the body",
            "Grains, fruits, vegetables, legumes",
            "45-65% of daily calories",
            AccentGreen,
            Icons.Default.Grain
        ),
        NutrientInfo(
            "Fats",
            "Energy storage and hormone production",
            "Oils, nuts, avocados, fatty fish",
            "20-35% of daily calories",
            AccentYellow,
            Icons.Default.WaterDrop
        )
    )
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(macros) { nutrient ->
            NutrientCard(nutrient)
        }
    }
}

@Composable
fun MineralsContent() {
    val minerals = listOf(
        NutrientInfo(
            "Calcium",
            "Essential for strong bones and teeth",
            "Dairy, leafy greens, fortified foods",
            "1000-1200 mg/day",
            AccentBlue,
            Icons.Default.Healing
        ),
        NutrientInfo(
            "Iron",
            "Carries oxygen in blood",
            "Red meat, beans, fortified cereals",
            "Men: 8mg, Women: 18mg",
            AccentRed,
            Icons.Default.Bloodtype
        ),
        NutrientInfo(
            "Magnesium",
            "Supports muscle and nerve function",
            "Nuts, seeds, whole grains, leafy greens",
            "Men: 400mg, Women: 310mg",
            AccentGreen,
            Icons.Default.Spa
        ),
        NutrientInfo(
            "Zinc",
            "Immune function and wound healing",
            "Meat, shellfish, legumes, seeds",
            "Men: 11mg, Women: 8mg",
            AccentPurple,
            Icons.Default.Shield
        ),
        NutrientInfo(
            "Potassium",
            "Regulates fluid balance and blood pressure",
            "Bananas, potatoes, beans, yogurt",
            "2600-3400 mg/day",
            AccentOrange,
            Icons.Default.Favorite
        )
    )
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(minerals) { nutrient ->
            NutrientCard(nutrient)
        }
    }
}

@Composable
fun FiberContent() {
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
                                        colors = listOf(AccentGreen, AccentTeal)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Dietary Fiber",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Essential for digestive health",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Text(
                        text = "Fiber is a type of carbohydrate that the body can't digest. It helps regulate blood sugar, maintain bowel health, and achieve healthy weight.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 20.sp
                    )
                    
                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                    
                    InfoRow("Daily Intake", "Men: 38g, Women: 25g")
                    InfoRow("Types", "Soluble & Insoluble")
                    InfoRow("Best Sources", "Whole grains, fruits, vegetables, legumes, nuts")
                }
            }
        }
        
        item {
            Text(
                text = "Benefits",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            BenefitCard(
                "Digestive Health",
                "Prevents constipation and maintains bowel health",
                Icons.Default.Favorite,
                AccentGreen
            )
        }
        item {
            BenefitCard(
                "Weight Management",
                "Helps you feel full longer and control appetite",
                Icons.Default.MonitorWeight,
                AccentBlue
            )
        }
        item {
            BenefitCard(
                "Blood Sugar Control",
                "Slows absorption of sugar and improves blood sugar levels",
                Icons.Default.Insights,
                AccentPurple
            )
        }
        item {
            BenefitCard(
                "Heart Health",
                "Lowers cholesterol and reduces heart disease risk",
                Icons.Default.FavoriteBorder,
                AccentRed
            )
        }
    }
}

@Composable
fun FatsContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Types of Fats",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            FatTypeCard(
                "Unsaturated Fats (Good)",
                "Help reduce bad cholesterol and provide essential fatty acids",
                "Olive oil, avocados, nuts, fatty fish",
                AccentGreen,
                Icons.Default.CheckCircle
            )
        }
        
        item {
            FatTypeCard(
                "Saturated Fats (Moderate)",
                "Should be limited but not completely avoided",
                "Butter, cheese, red meat, coconut oil",
                AccentYellow,
                Icons.Default.Warning
            )
        }
        
        item {
            FatTypeCard(
                "Trans Fats (Avoid)",
                "Increase bad cholesterol and heart disease risk",
                "Fried foods, baked goods, margarine",
                AccentRed,
                Icons.Default.Cancel
            )
        }
    }
}

data class NutrientInfo(
    val name: String,
    val description: String,
    val sources: String,
    val dailyIntake: String,
    val color: Color,
    val icon: ImageVector
)

@Composable
fun NutrientCard(nutrient: NutrientInfo) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(nutrient.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = nutrient.icon,
                    contentDescription = null,
                    tint = nutrient.color,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = nutrient.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = nutrient.color
                )
                Text(
                    text = nutrient.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 18.sp
                )
                InfoRow("Sources", nutrient.sources)
                InfoRow("Daily Intake", nutrient.dailyIntake)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$label:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = AccentPurple
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BenefitCard(title: String, description: String, icon: ImageVector, color: Color) {
    GlassCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun FatTypeCard(title: String, description: String, sources: String, color: Color, icon: ImageVector) {
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 20.sp
            )
            InfoRow("Common Sources", sources)
        }
    }
}

