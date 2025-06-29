package com.example.championcart.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

/**
 * Champion Cart Spacing System - Simplified
 * Based on 4dp grid
 */

object Spacing {
    // Base values - 4dp grid
    val xs = 4.dp     // 1x
    val s = 8.dp      // 2x
    val m = 12.dp     // 3x
    val l = 16.dp     // 4x
    val xl = 24.dp    // 6x
    val xxl = 32.dp   // 8x
}

object Padding {
    val xs = PaddingValues(4.dp)
    val s = PaddingValues(8.dp)
    val m = PaddingValues(12.dp)
    val l = PaddingValues(16.dp)
    val xl = PaddingValues(24.dp)

    // Screen padding with bottom nav
    val screenWithBottomNav = PaddingValues(
        start = 16.dp,
        top = 16.dp,
        end = 16.dp,
        bottom = 80.dp
    )
}

object Size {
    // Touch targets
    val minTouch = 48.dp
    val touch = 56.dp

    // Icon sizes
    val iconSmall = 20.dp
    val icon = 24.dp
    val iconLarge = 32.dp

    // Common heights
    val buttonHeight = 48.dp
    val inputHeight = 56.dp
    val bottomNavHeight = 72.dp
    val topBarHeight = 64.dp

    // Card dimensions
    val productCardWidth = 160.dp
    val productCardHeight = 200.dp
}