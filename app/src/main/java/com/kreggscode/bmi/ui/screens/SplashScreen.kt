package com.kreggscode.bmi.ui.screens

import android.view.View
import android.view.WindowManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kreggscode.bmi.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    val view = LocalView.current
    var startAnimation by remember { mutableStateOf(false) }

    // COMPLETELY REWRITTEN: TRUE FULLSCREEN - NO STATUS BAR
    DisposableEffect(Unit) {
        val window = (view.context as? android.app.Activity)?.window
        val insetsController = window?.let { WindowCompat.getInsetsController(it, view) }
        
        // Save original states
        val originalStatusBarColor = window?.statusBarColor
        val originalNavigationBarColor = window?.navigationBarColor
        val originalSystemUiVisibility = view.systemUiVisibility
        val originalFlags = window?.attributes?.flags
        
        window?.let { w ->
            // Make EVERYTHING transparent
            w.statusBarColor = android.graphics.Color.TRANSPARENT
            w.navigationBarColor = android.graphics.Color.TRANSPARENT
            
            // Add fullscreen flags
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            
            // CRITICAL: Use ALL fullscreen flags
            @Suppress("DEPRECATION")
            w.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
        
        // Modern API for hiding system bars
        insetsController?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        onDispose {
            // Restore everything
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
            window?.let { w ->
                w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                w.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                
                if (originalStatusBarColor != null) {
                    w.statusBarColor = originalStatusBarColor
                }
                if (originalNavigationBarColor != null) {
                    w.navigationBarColor = originalNavigationBarColor
                }
                @Suppress("DEPRECATION")
                w.decorView.systemUiVisibility = originalSystemUiVisibility
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
        delay(2500)
        onSplashComplete()
    }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    val iconAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800),
        label = "iconAlpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 300),
        label = "textAlpha"
    )

    // PERFECTLY CENTERED FULLSCREEN SPLASH
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Dark blue-gray
                        Color(0xFF1E293B),
                        Color(0xFF334155)
                    )
                )
            ),
        contentAlignment = Alignment.Center // PERFECTLY CENTERED
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Animated Icon with Glow
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(pulseScale)
                        .alpha(iconAlpha * 0.3f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AccentPurple.copy(alpha = 0.6f),
                                    AccentPink.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.height(-180.dp))

                // Main circle with gradient border
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(iconScale)
                        .alpha(iconAlpha)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    AccentPurple,
                                    AccentPink,
                                    AccentBlue
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner circle
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .background(
                                color = Color(0xFF1E293B),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "BMI Logo",
                            modifier = Modifier
                                .size(60.dp)
                                .scale(pulseScale),
                            tint = AccentPurple
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // App Name
            Text(
                text = "BMI Pro",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha),
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Your Complete Health Companion",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AccentPurple.copy(alpha = 0.9f),
                modifier = Modifier.alpha(textAlpha)
            )
        }
    }
}
