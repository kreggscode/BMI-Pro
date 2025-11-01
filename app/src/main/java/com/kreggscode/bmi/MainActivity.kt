package com.kreggscode.bmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kreggscode.bmi.ui.components.GlassNavigationBar
import com.kreggscode.bmi.ui.components.getNavigationItems
import com.kreggscode.bmi.ui.screens.*
import com.kreggscode.bmi.ui.theme.BMITheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val ComponentActivity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val scope = rememberCoroutineScope()
            val darkThemeKey = booleanPreferencesKey("dark_theme")
            
            val isDarkTheme by dataStore.data
                .map { preferences -> preferences[darkThemeKey] ?: false }
                .collectAsState(initial = false)

            // Hide system bars for splash screen
            var showSplash by remember { mutableStateOf(true) }
            
            // Update system bar colors based on theme
            LaunchedEffect(isDarkTheme) {
                val window = (this@MainActivity).window
                val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
                insetsController.isAppearanceLightNavigationBars = !isDarkTheme
            }
            
            // CRITICAL: Use isSplashScreen flag to prevent theme from overriding splash fullscreen
            BMITheme(darkTheme = isDarkTheme, isSplashScreen = showSplash) {
                if (showSplash) {
                    SplashScreen(onSplashComplete = { showSplash = false })
                } else {
                    MainScreen(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = {
                            scope.launch {
                                dataStore.edit { preferences ->
                                    preferences[darkThemeKey] = !isDarkTheme
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val navigationItems = getNavigationItems()
    var currentRoute by remember { mutableStateOf("home") }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("home") {
                currentRoute = "home"
                EnhancedHomeScreen(
                    onNavigate = { route ->
                        navController.navigate(route)
                    },
                    onToggleTheme = onToggleTheme,
                    isDarkTheme = isDarkTheme
                )
            }
            composable("calculator") {
                currentRoute = "calculator"
                CalculatorScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable("tracker") {
                currentRoute = "tracker"
                TrackerScreen()
            }
            composable("scanner") {
                currentRoute = "scanner"
                ScannerScreen()
            }
            composable("diet") {
                currentRoute = "diet"
                DietScreen()
            }
            composable("calories") {
                currentRoute = "calories"
                CaloriesScreen()
            }
            composable("chat") {
                currentRoute = "chat"
                ChatScreen(onBack = { navController.popBackStack() })
            }
            composable("habits") {
                currentRoute = "habits"
                HabitsScreen()
            }
            composable("todo") {
                currentRoute = "todo"
                TodoScreen()
            }
            composable("affirmations") {
                currentRoute = "affirmations"
                AffirmationsScreen(onBack = { navController.popBackStack() })
            }
            composable("breathing") {
                currentRoute = "breathing"
                BreathingScreen(onBack = { navController.popBackStack() })
            }
            composable("settings") {
                currentRoute = "settings"
                SettingsScreen(
                    onToggleTheme = onToggleTheme,
                    isDarkTheme = isDarkTheme
                )
            }
            composable("health-info") {
                currentRoute = "health-info"
                HealthInfoScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable("vitamins") {
                currentRoute = "vitamins"
                VitaminsScreen(onBack = { navController.popBackStack() })
            }
            composable("nutrients") {
                currentRoute = "nutrients"
                NutrientsScreen(onBack = { navController.popBackStack() })
            }
            composable("blood-groups") {
                currentRoute = "blood-groups"
                BloodGroupsScreen(onBack = { navController.popBackStack() })
            }
            composable("water-info") {
                currentRoute = "water-info"
                WaterInfoScreen(onBack = { navController.popBackStack() })
            }
            composable("water-tracker") {
                currentRoute = "water-tracker"
                WaterTrackerScreen(onBack = { navController.popBackStack() })
            }
            composable("sleep-tracker") {
                currentRoute = "sleep-tracker"
                SleepTrackerScreen(onBack = { navController.popBackStack() })
            }
        }

        // Floating Navigation Bar (hide on chat screen)
        if (currentRoute != "chat") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.navigationBars.asPaddingValues()),
                contentAlignment = androidx.compose.ui.Alignment.BottomCenter
            ) {
                GlassNavigationBar(
                    items = navigationItems,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route == "home") {
                            // Always pop back to home
                            navController.navigate(route) {
                                popUpTo("home") {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }

        // Global Ask AI Floating Button (hide on chat and scanner screens)
        if (currentRoute != "chat" && currentRoute != "scanner" && currentRoute != "water-tracker" && currentRoute != "sleep-tracker") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(bottom = 100.dp, end = 16.dp),
                contentAlignment = androidx.compose.ui.Alignment.BottomEnd
            ) {
                com.kreggscode.bmi.ui.components.AskAIFloatingButton(
                    onAskAI = { question ->
                        // Handle AI question - this will be processed by the component itself
                    }
                )
            }
        }
    }
}

