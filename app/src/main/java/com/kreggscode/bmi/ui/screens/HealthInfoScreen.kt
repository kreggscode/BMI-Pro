package com.kreggscode.bmi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.theme.*

data class HealthTopic(
    val title: String,
    val icon: ImageVector,
    val colors: List<Color>,
    val content: List<HealthSection>
)

data class HealthSection(
    val title: String,
    val content: String
)

@Composable
fun HealthInfoScreen(onNavigate: (String) -> Unit) {
    var selectedTopic by remember { mutableStateOf<HealthTopic?>(null) }

    val topics = remember {
        listOf(
            HealthTopic(
                title = "Understanding BMI",
                icon = Icons.Default.Calculate,
                colors = listOf(AccentBlue, AccentTeal),
                content = listOf(
                    HealthSection(
                        "What is BMI?",
                        "Body Mass Index (BMI) is a measure of body fat based on height and weight. It's calculated by dividing weight in kilograms by height in meters squared. BMI helps assess if you're at a healthy weight for your height."
                    ),
                    HealthSection(
                        "BMI Categories",
                        "Underweight: Below 18.5 - May indicate malnutrition or health issues\n\n" +
                        "Normal Weight: 18.5-24.9 - Healthy range for most adults\n\n" +
                        "Overweight: 25.0-29.9 - Increased health risks\n\n" +
                        "Obese: 30.0 and above - Higher health risks"
                    ),
                    HealthSection(
                        "Why BMI Matters",
                        "Maintaining a healthy BMI reduces risk of heart disease, diabetes, high blood pressure, and many other health conditions. It's a simple screening tool that helps identify potential weight-related health problems."
                    ),
                    HealthSection(
                        "BMI Limitations",
                        "BMI doesn't distinguish between muscle and fat mass. Athletes may have high BMI due to muscle. Consider BMI alongside other health indicators like waist circumference and fitness level."
                    )
                )
            ),
            HealthTopic(
                title = "Water & Hydration",
                icon = Icons.Default.Water,
                colors = listOf(AccentTeal, AccentBlue),
                content = listOf(
                    HealthSection(
                        "Why Water is Essential",
                        "Water makes up 60% of your body weight. Every system depends on water: regulates temperature, lubricates joints, transports nutrients, and removes waste. Even mild dehydration affects energy and cognition."
                    ),
                    HealthSection(
                        "Daily Water Goals",
                        "General recommendation: 8 glasses (2 liters) daily\n\n" +
                        "Men: About 3.7 liters (15.5 cups)\n" +
                        "Women: About 2.7 liters (11.5 cups)\n\n" +
                        "Adjust for activity level, climate, and individual needs."
                    ),
                    HealthSection(
                        "Benefits of Proper Hydration",
                        "• Boosts energy and brain function\n" +
                        "• Supports weight management\n" +
                        "• Promotes clear, healthy skin\n" +
                        "• Aids digestion and prevents constipation\n" +
                        "• Protects organs and tissues\n" +
                        "• Helps maintain healthy blood pressure"
                    ),
                    HealthSection(
                        "Signs of Dehydration",
                        "Watch for: Dark urine, dry mouth, fatigue, dizziness, reduced urination, headache, dry skin. If experiencing severe symptoms, seek medical attention immediately."
                    ),
                    HealthSection(
                        "Hydration Tips",
                        "• Carry a reusable water bottle\n" +
                        "• Drink water before, during, and after exercise\n" +
                        "• Eat water-rich foods (fruits, vegetables)\n" +
                        "• Set hourly reminders\n" +
                        "• Start your day with a glass of water\n" +
                        "• Drink water before meals"
                    )
                )
            ),
            HealthTopic(
                title = "Nutrition Basics",
                icon = Icons.Default.Restaurant,
                colors = listOf(AccentGreen, AccentTeal),
                content = listOf(
                    HealthSection(
                        "Macronutrients",
                        "Proteins: Build and repair tissues (4 cal/g)\n" +
                        "Good sources: Lean meats, fish, eggs, legumes, nuts\n\n" +
                        "Carbohydrates: Primary energy source (4 cal/g)\n" +
                        "Choose complex carbs: Whole grains, fruits, vegetables\n\n" +
                        "Fats: Essential for hormones and absorption (9 cal/g)\n" +
                        "Focus on healthy fats: Avocados, nuts, olive oil, fish"
                    ),
                    HealthSection(
                        "Balanced Diet",
                        "A healthy plate should include:\n" +
                        "• 50% vegetables and fruits\n" +
                        "• 25% lean proteins\n" +
                        "• 25% whole grains\n" +
                        "• Healthy fats in moderation\n" +
                        "• Plenty of water"
                    ),
                    HealthSection(
                        "Micronutrients",
                        "Vitamins and minerals are crucial for health. Eat a rainbow of fruits and vegetables to ensure adequate intake. Consider supplements if you have deficiencies, but food sources are always best."
                    ),
                    HealthSection(
                        "Portion Control",
                        "Use the hand method:\n" +
                        "• Palm-sized protein\n" +
                        "• Fist-sized carbs\n" +
                        "• Thumb-sized fats\n" +
                        "• Two handfuls of vegetables\n\n" +
                        "Eat slowly, listen to hunger cues, stop when satisfied."
                    )
                )
            ),
            HealthTopic(
                title = "Exercise & Fitness",
                icon = Icons.Default.FitnessCenter,
                colors = listOf(AccentOrange, AccentYellow),
                content = listOf(
                    HealthSection(
                        "Benefits of Exercise",
                        "• Strengthens heart and improves circulation\n" +
                        "• Helps manage weight\n" +
                        "• Reduces risk of chronic diseases\n" +
                        "• Improves mood and mental health\n" +
                        "• Builds stronger bones and muscles\n" +
                        "• Boosts energy levels\n" +
                        "• Promotes better sleep"
                    ),
                    HealthSection(
                        "Exercise Guidelines",
                        "Adults need:\n\n" +
                        "Aerobic: 150 minutes moderate (brisk walking) or 75 minutes vigorous (running) per week\n\n" +
                        "Strength Training: 2+ days per week for all major muscle groups\n\n" +
                        "Flexibility: Daily stretching recommended"
                    ),
                    HealthSection(
                        "Types of Exercise",
                        "Cardio: Running, cycling, swimming - improves heart health\n\n" +
                        "Strength: Weights, resistance bands - builds muscle\n\n" +
                        "Flexibility: Yoga, stretching - prevents injury\n\n" +
                        "Balance: Tai chi, stability exercises - prevents falls"
                    ),
                    HealthSection(
                        "Getting Started",
                        "• Start slowly and gradually increase intensity\n" +
                        "• Choose activities you enjoy\n" +
                        "• Set realistic goals\n" +
                        "• Find a workout buddy\n" +
                        "• Schedule exercise like appointments\n" +
                        "• Listen to your body\n" +
                        "• Rest and recover adequately"
                    )
                )
            ),
            HealthTopic(
                title = "Sleep Health",
                icon = Icons.Default.Bedtime,
                colors = listOf(AccentPurple, AccentPink),
                content = listOf(
                    HealthSection(
                        "Importance of Sleep",
                        "Sleep is essential for physical and mental health. During sleep, your body repairs tissues, consolidates memories, and regulates hormones. Poor sleep affects mood, cognition, and increases disease risk."
                    ),
                    HealthSection(
                        "Sleep Requirements",
                        "Adults: 7-9 hours per night\n" +
                        "Teenagers: 8-10 hours\n" +
                        "Children: 9-12 hours\n\n" +
                        "Quality matters as much as quantity. Aim for uninterrupted, deep sleep."
                    ),
                    HealthSection(
                        "Better Sleep Habits",
                        "• Maintain consistent sleep schedule\n" +
                        "• Create dark, quiet, cool bedroom\n" +
                        "• Avoid screens 1 hour before bed\n" +
                        "• Limit caffeine after 2 PM\n" +
                        "• Exercise regularly (not close to bedtime)\n" +
                        "• Manage stress with relaxation techniques\n" +
                        "• Avoid large meals before bed"
                    ),
                    HealthSection(
                        "Sleep Disorders",
                        "Common issues include insomnia, sleep apnea, restless leg syndrome. If you consistently have trouble sleeping or feel tired despite adequate sleep, consult a healthcare provider."
                    )
                )
            ),
            HealthTopic(
                title = "Mental Wellness",
                icon = Icons.Default.Psychology,
                colors = listOf(AccentPink, AccentPurple),
                content = listOf(
                    HealthSection(
                        "Mental Health Matters",
                        "Mental health is as important as physical health. It affects how we think, feel, and act. Good mental health helps you cope with stress, build relationships, and make healthy choices."
                    ),
                    HealthSection(
                        "Stress Management",
                        "• Practice deep breathing\n" +
                        "• Exercise regularly\n" +
                        "• Get adequate sleep\n" +
                        "• Connect with others\n" +
                        "• Set boundaries\n" +
                        "• Take breaks\n" +
                        "• Practice gratitude\n" +
                        "• Seek professional help when needed"
                    ),
                    HealthSection(
                        "Mindfulness & Meditation",
                        "Regular practice improves focus, reduces anxiety, and enhances emotional regulation. Start with 5 minutes daily. Apps and guided meditations can help beginners."
                    ),
                    HealthSection(
                        "Work-Life Balance",
                        "Set boundaries between work and personal time. Make time for hobbies, relationships, and self-care. Remember that rest and recreation are productive activities."
                    ),
                    HealthSection(
                        "When to Seek Help",
                        "Don't hesitate to reach out if you experience persistent sadness, anxiety, mood changes, or thoughts of self-harm. Mental health professionals can provide effective treatment and support."
                    )
                )
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (selectedTopic == null) {
            HealthTopicsGrid(topics = topics, onTopicClick = { selectedTopic = it })
        } else {
            HealthTopicDetail(
                topic = selectedTopic!!,
                onBack = { selectedTopic = null }
            )
        }
    }
}

@Composable
fun HealthTopicsGrid(
    topics: List<HealthTopic>,
    onTopicClick: (HealthTopic) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 60.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Health Information",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Everything you need to know about health and wellness",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 22.sp
            )
        }

        items(topics) { topic ->
            HealthTopicCard(
                topic = topic,
                onClick = { onTopicClick(topic) }
            )
        }
    }
}

@Composable
fun HealthTopicCard(
    topic: HealthTopic,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(topic.colors)
            )
            .clickable {
                isPressed = true
                onClick()
            }
            .padding(24.dp)
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
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = topic.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = topic.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun HealthTopicDetail(
    topic: HealthTopic,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 60.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.clickable(onClick = onBack)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Back",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(brush = Brush.horizontalGradient(topic.colors))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = topic.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = topic.title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        items(topic.content) { section ->
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = section.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = section.content,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

