package com.kreggscode.bmi.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars

/**
 * Calculates dynamic bottom padding based on the actual Android navigation bar height
 * plus space for the custom navigation bar. This ensures content doesn't get cut off
 * on different devices with varying navigation bar sizes.
 */
@Composable
fun calculateDynamicBottomPadding(): Dp {
    val density = LocalDensity.current

    // Get the actual Android system navigation bar height
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(density)

    // Space for custom navigation bar (60dp height + padding)
    val customNavBarHeight = 100.dp

    // Calculate total padding, ensuring minimum of 120.dp for safety
    return with(density) {
        (navigationBarHeight.toDp() + customNavBarHeight).coerceAtLeast(120.dp)
    }
}
