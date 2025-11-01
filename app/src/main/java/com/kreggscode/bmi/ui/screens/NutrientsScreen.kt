package com.kreggscode.bmi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
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

data class Nutrient(
    val name: String,
    val type: String,
    val function: String,
    val sources: String,
    val recommendation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutrientsScreen(onBack: () -> Unit) {
    EnhancedNutrientsScreen(onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OldNutrientsScreen(onBack: () -> Unit) {
    val nutrients = remember {
        listOf(
            Nutrient(
                "Protein",
                "Macronutrient",
                "Essential for building and repairing tissues, making enzymes and hormones. Supports immune function and muscle growth.",
                "Meat, fish, eggs, dairy, legumes, nuts, tofu",
                "0.8g per kg of body weight (56g for men, 46g for women)"
            ),
            Nutrient(
                "Carbohydrates",
                "Macronutrient",
                "Primary energy source for the body and brain. Provides fiber for digestive health.",
                "Whole grains, fruits, vegetables, legumes, potatoes",
                "45-65% of daily calories (225-325g for 2000 cal diet)"
            ),
            Nutrient(
                "Fats",
                "Macronutrient",
                "Energy storage, hormone production, vitamin absorption, brain function, cell membrane structure.",
                "Avocados, nuts, seeds, olive oil, fatty fish, eggs",
                "20-35% of daily calories (44-77g for 2000 cal diet)"
            ),
            Nutrient(
                "Fiber",
                "Carbohydrate",
                "Promotes digestive health, regulates blood sugar, lowers cholesterol, aids weight management.",
                "Whole grains, fruits, vegetables, legumes, nuts",
                "Men: 38g, Women: 25g"
            ),
            Nutrient(
                "Calcium",
                "Mineral",
                "Essential for strong bones and teeth, muscle function, nerve transmission, blood clotting.",
                "Dairy products, leafy greens, fortified foods, sardines",
                "Adults: 1000-1200 mg"
            ),
            Nutrient(
                "Iron",
                "Mineral",
                "Critical for oxygen transport in blood, energy production, immune function, cognitive development.",
                "Red meat, poultry, fish, legumes, fortified cereals, spinach",
                "Men: 8 mg, Women: 18 mg"
            ),
            Nutrient(
                "Magnesium",
                "Mineral",
                "Supports muscle and nerve function, blood sugar control, blood pressure regulation, protein synthesis.",
                "Nuts, seeds, whole grains, leafy greens, legumes",
                "Men: 400-420 mg, Women: 310-320 mg"
            ),
            Nutrient(
                "Potassium",
                "Mineral",
                "Regulates fluid balance, muscle contractions, nerve signals. Helps maintain healthy blood pressure.",
                "Bananas, potatoes, beans, yogurt, salmon, avocados",
                "Adults: 2600-3400 mg"
            ),
            Nutrient(
                "Zinc",
                "Mineral",
                "Supports immune function, wound healing, DNA synthesis, cell division, sense of taste and smell.",
                "Meat, shellfish, legumes, seeds, nuts, dairy",
                "Men: 11 mg, Women: 8 mg"
            ),
            Nutrient(
                "Omega-3 Fatty Acids",
                "Essential Fat",
                "Reduces inflammation, supports heart health, brain function, and may reduce depression risk.",
                "Fatty fish (salmon, mackerel), walnuts, flaxseeds, chia seeds",
                "Adults: 250-500 mg EPA+DHA daily"
            )
        )
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
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
                                        colors = listOf(AccentGreen, AccentTeal)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fastfood,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Essential Nutrients",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Macro & micronutrients your body needs",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            items(nutrients) { nutrient ->
                NutrientCard(nutrient)
            }
        }
    }
}

@Composable
fun NutrientCard(nutrient: Nutrient) {
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = nutrient.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                    Text(
                        text = nutrient.type,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            NutrientInfoSection("Function", nutrient.function)
            NutrientInfoSection("Food Sources", nutrient.sources)
            NutrientInfoSection("Daily Recommendation", nutrient.recommendation)
        }
    }
}

@Composable
fun NutrientInfoSection(label: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AccentTeal
        )
        Text(
            text = content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 20.sp
        )
    }
}

