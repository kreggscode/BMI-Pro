package com.kreggscode.bmi.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kreggscode.bmi.ui.theme.*
import androidx.compose.runtime.rememberCoroutineScope
import com.kreggscode.bmi.data.api.PollinationsService
import kotlinx.coroutines.launch

@Composable
fun AskAIFloatingButton(
    onAskAI: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var question by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var aiResponse by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Pulsing animation for the FAB
    val infiniteTransition = rememberInfiniteTransition(label = "fabPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabScale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fabRotation"
    )

    Box(
        modifier = modifier
            .padding(8.dp)
            .scale(scale)
    ) {
        // Outer glow ring (smaller)
        Box(
            modifier = Modifier
                .size(50.dp)
                .rotate(rotation)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            AccentPurple.copy(alpha = 0.3f),
                            AccentPink.copy(alpha = 0.3f),
                            AccentBlue.copy(alpha = 0.3f),
                            AccentTeal.copy(alpha = 0.3f),
                            AccentPurple.copy(alpha = 0.3f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Inner button (smaller)
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.Center),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AccentPurple,
                                AccentPink
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Ask AI",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    if (showDialog) {
        AskAIDialog(
            question = question,
            onQuestionChange = { question = it },
            aiResponse = aiResponse,
            isLoading = isLoading,
            onDismiss = { 
                showDialog = false
                question = ""
                aiResponse = ""
                isLoading = false
            },
            onAsk = {
                if (question.isNotBlank()) {
                    isLoading = true
                    onAskAI(question)
                    
                    // Call Pollinations AI
                    coroutineScope.launch {
                        try {
                            val pollinationsService = PollinationsService()
                            val result = pollinationsService.chatWithAI(question, emptyList())
                            result.onSuccess { response ->
                                aiResponse = response
                            }.onFailure { error ->
                                aiResponse = "Sorry, I encountered an error: ${error.message}\n\nPlease try again or ask a different question."
                            }
                        } catch (e: Exception) {
                            aiResponse = "Sorry, I encountered an error: ${e.message}\n\nPlease check your internet connection and try again."
                        } finally {
                            isLoading = false
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun AskAIDialog(
    question: String,
    onQuestionChange: (String) -> Unit,
    aiResponse: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onAsk: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onDismiss)
                .imePadding(), // Handle keyboard
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = {}) // Prevent dismiss on card click
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(AccentPurple, AccentPink)
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "Ask AI Assistant",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Quick suggestions
                    Text(
                        text = "Quick Questions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuickQuestionChip("What's a healthy BMI range?") { onQuestionChange(it) }
                        QuickQuestionChip("Give me diet tips") { onQuestionChange(it) }
                        QuickQuestionChip("How to stay motivated?") { onQuestionChange(it) }
                    }

                    // Question input
                    OutlinedTextField(
                        value = question,
                        onValueChange = onQuestionChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        placeholder = {
                            Text(
                                "Ask me anything about health, fitness, nutrition, or wellness...",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentPurple,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 5
                    )

                    // AI Response section
                    if (aiResponse.isNotEmpty() || isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            AccentPurple.copy(alpha = 0.1f),
                                            AccentBlue.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            if (isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = AccentPurple,
                                        strokeWidth = 3.dp
                                    )
                                    Text(
                                        "AI is thinking...",
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Psychology,
                                            contentDescription = null,
                                            tint = AccentPurple,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "AI Response",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentPurple
                                        )
                                    }
                                    FormattedAIText(
                                        text = aiResponse
                                    )
                                }
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = onAsk,
                            modifier = Modifier.weight(1f),
                            enabled = question.isNotBlank() && !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentPurple,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("Ask AI")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickQuestionChip(
    question: String,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .clickable { onClick(question) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = AccentTeal,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = question,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

