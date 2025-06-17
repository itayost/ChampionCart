package com.example.championcart.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.util.*

/**
 * Champion Cart - Simple Time-Based Theme
 * Colors that change throughout the day for Electric Harmony design
 */

/**
 * Get current time-based accent color
 */
@Composable
fun getCurrentTimeAccent(): Color {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 6..11 -> TimeBasedColors.MorningPrimary      // Morning - warm
        in 12..17 -> TimeBasedColors.AfternoonPrimary   // Afternoon - electric
        in 18..23 -> TimeBasedColors.EveningPrimary     // Evening - deep
        else -> TimeBasedColors.NightPrimary            // Night - calm
    }
}

/**
 * Get time-based greeting message
 */
@Composable
fun getTimeBasedGreeting(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 6..11 -> "Good Morning!"
        in 12..17 -> "Good Afternoon!"
        in 18..23 -> "Good Evening!"
        else -> "Good Night!"
    }
}

/**
 * Check if should auto-switch to dark mode
 */
@Composable
fun shouldUseDarkModeForTime(): Boolean {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return hour in 22..23 || hour in 0..6
}

/**
 * Simple time-based theme wrapper
 */
@Composable
fun TimeBasedChampionCartTheme(
    enableTimeBasedColors: Boolean = true,
    content: @Composable () -> Unit
) {
    val shouldUseDark = if (enableTimeBasedColors) {
        shouldUseDarkModeForTime()
    } else {
        false
    }

    ChampionCartTheme(
        darkTheme = shouldUseDark,
        timeBasedTheme = enableTimeBasedColors,
        content = content
    )
}