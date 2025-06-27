package com.example.championcart.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Champion Cart Elevation System
 * Material Design 3 elevation with Electric Harmony enhancements
 */

object Elevation {
    // Standard elevation levels
    val level0 = 0.dp   // Surface
    val level1 = 1.dp   // Raised surface
    val level2 = 3.dp   // Cards (default)
    val level3 = 6.dp   // Cards (hover)
    val level4 = 8.dp   // Modals, FAB
    val level5 = 12.dp  // Dialogs

    // Component-specific elevations
    object Component {
        val card = 2.dp
        val cardHover = 6.dp
        val cardPressed = 1.dp

        val button = 2.dp
        val buttonHover = 4.dp
        val buttonPressed = 0.dp

        val fab = 6.dp
        val fabHover = 8.dp
        val fabPressed = 12.dp

        val bottomSheet = 8.dp
        val dialog = 12.dp
        val modal = 16.dp

        val navigationBar = 3.dp
        val topAppBar = 2.dp
        val menu = 8.dp
    }

    // Interactive elevation states
    data class InteractiveElevation(
        val default: Dp,
        val hover: Dp,
        val pressed: Dp,
        val focused: Dp = hover,
        val disabled: Dp = 0.dp
    )

    // Predefined interactive elevations
    val cardElevation = InteractiveElevation(
        default = 2.dp,
        hover = 6.dp,
        pressed = 1.dp
    )

    val buttonElevation = InteractiveElevation(
        default = 2.dp,
        hover = 4.dp,
        pressed = 0.dp
    )

    val fabElevation = InteractiveElevation(
        default = 6.dp,
        hover = 8.dp,
        pressed = 12.dp
    )

    // Glass elevation (subtle)
    object Glass {
        val light = 2.dp
        val medium = 4.dp
        val heavy = 6.dp
        val ultra = 8.dp
    }

    // Price card elevations
    object Price {
        val best = 4.dp    // Slightly elevated to stand out
        val normal = 2.dp
        val high = 1.dp    // Less prominent
    }

    // Helper function to get elevation based on state
    fun getElevation(
        elevation: InteractiveElevation,
        isPressed: Boolean = false,
        isHovered: Boolean = false,
        isFocused: Boolean = false,
        isEnabled: Boolean = true
    ): Dp {
        return when {
            !isEnabled -> elevation.disabled
            isPressed -> elevation.pressed
            isHovered -> elevation.hover
            isFocused -> elevation.focused
            else -> elevation.default
        }
    }
}