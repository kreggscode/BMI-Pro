package com.kreggscode.bmi.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = OnPrimaryLight,
    onSecondary = OnSecondaryLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = OnPrimaryDark,
    onSecondary = OnSecondaryDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark
)

@Composable
fun BMITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isSplashScreen: Boolean = false, // NEW: Flag to skip system bar changes for splash
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode && !isSplashScreen) { // CRITICAL: Skip for splash screen
        SideEffect {
            val window = (view.context as Activity).window
            
            // Make status bar match background color (transparent with slight tint)
            val statusBarColor = if (darkTheme) {
                // Use the actual dark background color with slight transparency
                android.graphics.Color.argb(230, 15, 23, 42) // BackgroundDark color
            } else {
                // Use the actual light background color with slight transparency
                android.graphics.Color.argb(230, 248, 250, 252) // BackgroundLight color
            }
            window.statusBarColor = statusBarColor
            
            // Make navigation bar fully transparent
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            
            // Disable navigation bar scrim (the white/dark overlay Android adds)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

