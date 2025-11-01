package com.kreggscode.bmi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Science
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

data class Vitamin(
    val name: String,
    val fullName: String,
    val benefits: String,
    val sources: String,
    val dailyIntake: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitaminsScreen(onBack: () -> Unit) {
    val vitamins = remember {
        listOf(
            Vitamin(
                "Vitamin A",
                "Retinol",
                "Essential for vision, immune function, and skin health. Supports cell growth and reproduction.",
                "Carrots, sweet potatoes, spinach, liver, eggs, dairy products",
                "Men: 900 mcg, Women: 700 mcg"
            ),
            Vitamin(
                "Vitamin B1",
                "Thiamine",
                "Helps convert nutrients into energy. Essential for nerve function and metabolism.",
                "Whole grains, pork, fish, legumes, nuts, seeds",
                "Men: 1.2 mg, Women: 1.1 mg"
            ),
            Vitamin(
                "Vitamin B2",
                "Riboflavin",
                "Important for energy production, cell function, and fat metabolism.",
                "Eggs, organ meats, lean meats, milk, green vegetables",
                "Men: 1.3 mg, Women: 1.1 mg"
            ),
            Vitamin(
                "Vitamin B3",
                "Niacin",
                "Supports digestive system, skin, and nervous system health. Helps convert food to energy.",
                "Meat, fish, poultry, whole grains, peanuts",
                "Men: 16 mg, Women: 14 mg"
            ),
            Vitamin(
                "Vitamin B6",
                "Pyridoxine",
                "Important for brain development and function. Helps body make serotonin and norepinephrine.",
                "Fish, poultry, potatoes, chickpeas, bananas",
                "Adults: 1.3-1.7 mg"
            ),
            Vitamin(
                "Vitamin B12",
                "Cobalamin",
                "Essential for red blood cell formation, DNA synthesis, and neurological function.",
                "Meat, fish, poultry, eggs, dairy, fortified cereals",
                "Adults: 2.4 mcg"
            ),
            Vitamin(
                "Vitamin C",
                "Ascorbic Acid",
                "Powerful antioxidant. Boosts immune system, aids iron absorption, promotes wound healing.",
                "Citrus fruits, berries, tomatoes, peppers, broccoli",
                "Men: 90 mg, Women: 75 mg"
            ),
            Vitamin(
                "Vitamin D",
                "Calciferol",
                "Essential for calcium absorption, bone health, immune function, and mood regulation.",
                "Sunlight, fatty fish, fortified milk, egg yolks",
                "Adults: 600-800 IU (15-20 mcg)"
            ),
            Vitamin(
                "Vitamin E",
                "Tocopherol",
                "Antioxidant that protects cells from damage. Supports immune function and skin health.",
                "Nuts, seeds, vegetable oils, leafy greens",
                "Adults: 15 mg"
            ),
            Vitamin(
                "Vitamin K",
                "Phylloquinone",
                "Essential for blood clotting and bone metabolism. May help prevent heart disease.",
                "Leafy greens, broccoli, Brussels sprouts, fish, meat",
                "Men: 120 mcg, Women: 90 mcg"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vitamins A-Z Guide") },
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
                                        colors = listOf(AccentOrange, AccentYellow)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Essential Vitamins",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Complete guide to vitamins and their benefits",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            items(vitamins) { vitamin ->
                VitaminCard(vitamin)
            }
        }
    }
}

@Composable
fun VitaminCard(vitamin: Vitamin) {
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = vitamin.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange
                    )
                    Text(
                        text = vitamin.fullName,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            VitaminInfoSection("Benefits", vitamin.benefits)
            VitaminInfoSection("Food Sources", vitamin.sources)
            VitaminInfoSection("Daily Intake", vitamin.dailyIntake)
        }
    }
}

@Composable
fun VitaminInfoSection(label: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AccentPurple
        )
        Text(
            text = content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 20.sp
        )
    }
}

