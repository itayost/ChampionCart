package com.example.championcart.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Champion Cart Spacing & Sizing System
 * Consistent spacing based on 4dp grid
 */

object Spacing {
    // Base spacing values - 4dp grid
    val none = 0.dp
    val xxs = 2.dp    // 0.5x
    val xs = 4.dp     // 1x base
    val s = 8.dp      // 2x
    val m = 12.dp     // 3x
    val l = 16.dp     // 4x
    val xl = 20.dp    // 5x
    val xxl = 24.dp   // 6x
    val xxxl = 32.dp  // 8x
    val huge = 40.dp  // 10x
    val giant = 48.dp // 12x
    val mega = 64.dp  // 16x

    // Screen margins
    object Screen {
        val marginSmall = 16.dp
        val marginMedium = 20.dp
        val marginLarge = 24.dp
        val marginXLarge = 32.dp

        val paddingSmall = PaddingValues(16.dp)
        val paddingMedium = PaddingValues(20.dp)
        val paddingLarge = PaddingValues(24.dp)

        val paddingWithBottom = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 80.dp // For bottom navigation
        )
    }

    // Component padding
    object Component {
        val paddingXS = PaddingValues(8.dp)
        val paddingS = PaddingValues(12.dp)
        val paddingM = PaddingValues(16.dp)
        val paddingL = PaddingValues(20.dp)
        val paddingXL = PaddingValues(24.dp)

        // Asymmetric padding
        val paddingHorizontalM = PaddingValues(horizontal = 16.dp)
        val paddingVerticalM = PaddingValues(vertical = 16.dp)
        val paddingHorizontalL = PaddingValues(horizontal = 20.dp)
        val paddingVerticalL = PaddingValues(vertical = 20.dp)
    }

    // List item spacing
    object List {
        val itemSpacingXS = 4.dp
        val itemSpacingS = 8.dp
        val itemSpacingM = 12.dp
        val itemSpacingL = 16.dp
        val itemSpacingXL = 20.dp

        val sectionSpacing = 24.dp
        val groupSpacing = 32.dp
    }
}

object Sizing {
    // Icon sizes
    object Icon {
        val xs = 16.dp
        val s = 20.dp
        val m = 24.dp
        val l = 28.dp
        val xl = 32.dp
        val xxl = 40.dp
        val huge = 48.dp
        val giant = 64.dp
    }

    // Button sizes
    object Button {
        val heightS = 32.dp
        val heightM = 40.dp
        val heightL = 48.dp
        val heightXL = 56.dp
        val heightXXL = 64.dp

        val minWidthS = 64.dp
        val minWidthM = 88.dp
        val minWidthL = 112.dp
        val minWidthXL = 140.dp
    }

    // Input field heights
    object Input {
        val heightS = 40.dp
        val heightM = 48.dp
        val heightL = 56.dp
        val heightXL = 64.dp
    }

    // Card dimensions
    object Card {
        val minHeight = 80.dp
        val productWidth = 160.dp
        val productHeight = 200.dp
        val storeWidth = 120.dp
        val storeHeight = 80.dp
        val heroMinHeight = 200.dp
    }

    // Navigation
    object Navigation {
        val bottomBarHeight = 72.dp
        val topBarHeight = 64.dp
        val tabBarHeight = 48.dp
        val searchBarHeight = 56.dp
    }

    // FAB sizes
    object FAB {
        val mini = 40.dp
        val small = 48.dp
        val medium = 56.dp
        val large = 64.dp
        val extendedMinWidth = 96.dp
    }

    // Avatar sizes
    object Avatar {
        val xs = 24.dp
        val s = 32.dp
        val m = 40.dp
        val l = 48.dp
        val xl = 64.dp
        val xxl = 96.dp
    }

    // Divider thickness
    object Divider {
        val thin = 0.5.dp
        val regular = 1.dp
        val thick = 2.dp
        val bold = 4.dp
    }

    // Touch targets (minimum)
    val minTouchTarget = 48.dp
    val recommendedTouchTarget = 56.dp
    val largeTouchTarget = 64.dp

    // Content max widths
    object Content {
        val maxWidthCompact = 400.dp
        val maxWidthMedium = 600.dp
        val maxWidthExpanded = 800.dp
        val maxWidthFull = 1200.dp
    }
}

// Helper functions
fun paddingOf(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp
) = PaddingValues(
    horizontal = horizontal,
    vertical = vertical
)

fun paddingOf(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
) = PaddingValues(
    start = start,
    top = top,
    end = end,
    bottom = bottom
)