package com.example.championcart.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.time.LocalTime


/**
 * Price level categories for visual representation
 */
enum class PriceLevel {
    Best,  // Lowest price
    Mid,   // Medium price
    High   // Highest price
}


/**
 * Color helper utilities for the ChampionCart app
 */
object ColorHelpers {

    /**
     * Get color based on price level
     */
    @Composable
    fun getPriceColor(priceLevel: PriceLevel): Color {
        val colors = MaterialTheme.colorScheme.extended
        return when (priceLevel) {
            PriceLevel.Best -> colors.bestPrice
            PriceLevel.Mid -> colors.midPrice
            PriceLevel.High -> colors.highPrice
        }
    }

    /**
     * Get time-based accent color
     */
    @Composable
    fun getTimeBasedAccent(): Color {
        val hour = LocalTime.now().hour
        return when {
            hour in 6..11 -> TimeBasedColors.MorningPrimary
            hour in 12..17 -> TimeBasedColors.AfternoonPrimary
            hour in 18..23 -> TimeBasedColors.EveningPrimary
            else -> TimeBasedColors.NightPrimary
        }
    }

    /**
     * Get store-specific color (based on store name)
     */
    @Composable
    fun getStoreColor(storeName: String): Color {
        val colors = MaterialTheme.colorScheme.extended
        return when (storeName.lowercase()) {
            "shufersal" -> Color(0xFF00A651)  // Shufersal green
            "rami levy" -> Color(0xFFFF0000)  // Rami Levy red
            "victory" -> Color(0xFF0066CC)    // Victory blue
            "yochananof" -> Color(0xFFFF6600) // Yochananof orange
            else -> colors.electricMint
        }
    }

    /**
     * Get category-specific color
     */
    @Composable
    fun getCategoryColor(category: String): Color {
        val colors = MaterialTheme.colorScheme.extended
        return when (category.lowercase()) {
            "dairy" -> Color(0xFF4FC3F7)      // Light blue
            "meat" -> Color(0xFFE57373)       // Light red
            "produce" -> Color(0xFF81C784)    // Light green
            "bakery" -> Color(0xFFFFD54F)     // Light yellow
            "frozen" -> Color(0xFF64B5F6)     // Ice blue
            else -> colors.cosmicPurple
        }
    }
}