package com.kreggscode.bmi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.CheckCircle
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

data class WaterFact(
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterInfoScreen(onBack: () -> Unit) {
    val waterFacts = remember {
        listOf(
            WaterFact(
                "Daily Water Needs",
                "Adults should drink 8-10 glasses (2-2.5 liters) of water daily. Needs increase with exercise, hot weather, or illness. Listen to your body's thirst signals."
            ),
            WaterFact(
                "Hydration Benefits",
                "Proper hydration regulates body temperature, lubricates joints, delivers nutrients to cells, keeps organs functioning properly, and improves sleep quality and mood."
            ),
            WaterFact(
                "Dehydration Signs",
                "Watch for dark urine, dry mouth, fatigue, dizziness, headaches, and decreased urination. Mild dehydration can affect concentration and physical performance."
            ),
            WaterFact(
                "Water and Weight Loss",
                "Drinking water before meals can reduce appetite and calorie intake. It boosts metabolism and helps the body burn fat more efficiently."
            ),
            WaterFact(
                "Skin Health",
                "Adequate hydration keeps skin moisturized, elastic, and helps flush out toxins. Water can reduce acne and give skin a healthy glow."
            ),
            WaterFact(
                "Brain Function",
                "Even mild dehydration (1-3% body weight) can impair mood, concentration, memory, and increase anxiety. The brain is 75% water."
            ),
            WaterFact(
                "Exercise Hydration",
                "Drink 17-20 oz of water 2-3 hours before exercise, 8 oz during warm-up, 7-10 oz every 10-20 minutes during exercise, and 8 oz within 30 minutes after."
            ),
            WaterFact(
                "Water vs. Other Drinks",
                "Water is calorie-free and doesn't contain sugar or additives. While tea and coffee count toward hydration, avoid excessive caffeine and sugary drinks."
            ),
            WaterFact(
                "Overhydration Risk",
                "Drinking too much water too quickly can lead to hyponatremia (low sodium). Drink steadily throughout the day rather than large amounts at once."
            ),
            WaterFact(
                "Water Quality",
                "Filtered or purified water is best. If using tap water, ensure it meets safety standards. Avoid water with high mineral content if you have kidney issues."
            )
        )
    }

    val hydrationTips = remember {
        listOf(
            "Start your day with a glass of water",
            "Carry a reusable water bottle",
            "Set reminders to drink water",
            "Eat water-rich foods (fruits & vegetables)",
            "Drink water before, during, and after exercise",
            "Replace sugary drinks with water",
            "Drink water when you feel hungry",
            "Add lemon or cucumber for flavor"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water & Hydration") },
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
                                        colors = listOf(AccentBlue, AccentTeal)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Hydration Guide",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Essential water facts and tips",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Water Facts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(waterFacts) { fact ->
                WaterFactCard(fact)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hydration Tips",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        hydrationTips.forEach { tip ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = tip,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WaterFactCard(fact: WaterFact) {
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = fact.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AccentBlue
            )
            Text(
                text = fact.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 20.sp
            )
        }
    }
}

