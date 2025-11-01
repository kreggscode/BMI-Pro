package com.kreggscode.bmi.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedCircularProgress(
    value: Float,
    maxValue: Float,
    label: String,
    colors: List<Color>,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "progressAnimation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = this.size
            val radius = (canvasSize.minDimension / 2) - strokeWidth.toPx() / 2
            val center = Offset(canvasSize.width / 2, canvasSize.height / 2)

            // Background circle
            drawCircle(
                color = Color.Gray.copy(alpha = 0.1f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Progress arc
            val sweepAngle = (animatedValue / maxValue) * 360f
            drawArc(
                brush = Brush.sweepGradient(colors),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp) // Add padding to prevent overflow
        ) {
            Text(
                text = String.format("%.0f", animatedValue), // Remove decimal for cleaner look
                fontSize = (size.value / 5).sp, // Responsive font size based on circle size
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                fontSize = (size.value / 10).sp, // Responsive label size
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BMICircularIndicator(
    bmi: Float,
    category: String,
    modifier: Modifier = Modifier
) {
    val colors = when (category.lowercase()) {
        "underweight" -> listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
        "normal" -> listOf(Color(0xFF10B981), Color(0xFF34D399))
        "overweight" -> listOf(Color(0xFFF59E0B), Color(0xFFFBBF24))
        "obese" -> listOf(Color(0xFFEF4444), Color(0xFFF87171))
        else -> listOf(Color.Gray, Color.LightGray)
    }

    AnimatedCircularProgress(
        value = bmi,
        maxValue = 40f,
        label = "BMI",
        colors = colors,
        size = 160.dp,
        strokeWidth = 16.dp,
        modifier = modifier
    )
}

