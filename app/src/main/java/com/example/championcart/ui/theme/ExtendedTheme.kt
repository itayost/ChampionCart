package com.example.championcart.ui.theme

import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Extended Material Theme Colors for Champion Cart
 * Provides access to custom Electric Harmony colors beyond Material3 defaults
 * COMPLETE VERSION - Compatible with existing Color.kt
 */

/**
 * Extended colors data class that includes all custom colors from your design system
 */
data class ExtendedColors(
    // Primary Electric Harmony colors
    val electricMint: Color,
    val electricMintGlow: Color,
    val cosmicPurple: Color,
    val cosmicPurpleGlow: Color,
    val neonCoral: Color,
    val neonCoralGlow: Color,

    // Semantic colors with glow effects
    val success: Color,
    val successGlow: Color,
    val warning: Color,
    val warningGlow: Color,
    val error: Color,
    val errorGlow: Color,
    val info: Color,
    val infoGlow: Color,

    // Price indicators
    val bestPrice: Color,
    val bestPriceGlow: Color,
    val midPrice: Color,
    val highPrice: Color,
    val priceLow: Color,        // Alias for bestPrice
    val priceHigh: Color,       // Alias for highPrice
    val bestDeal: Color,        // For best deal badges
    val savings: Color,         // For savings indicators
    val tertiary: Color,        // Standard Material3 tertiary
    val errorRed: Color,        // Alias for error

    // Glass morphism effects
    val glass: Color,
    val glassBorder: Color,
    val glassDark: Color,
    val glassDarkBorder: Color,
    val glassFrosted: Color,
    val glassFrostedBorder: Color,

    // Store brand colors
    val shufersal: Color,
    val shufersalGlow: Color,
    val victory: Color,
    val victoryGlow: Color,
    val ramiLevy: Color,
    val ramiLevyGlow: Color,
    val mega: Color,
    val megaGlow: Color,
    val genericStore: Color,

    // Time-based dynamic colors
    val morningPrimary: Color,
    val morningAccent: Color,
    val morningGradientStart: Color,
    val morningGradientEnd: Color,
    val eveningPrimary: Color,
    val eveningAccent: Color,
    val eveningGradientStart: Color,
    val eveningGradientEnd: Color,
    val nightPrimary: Color,
    val nightAccent: Color,

    // Visual effects
    val shimmerHighlight: Color,
    val shadowLight: Color,
    val shadowMedium: Color,
    val shadowDark: Color
)

/**
 * Helper functions for extended colors
 */

/**
 * Get appropriate glow color for any base color
 */
fun Color.toGlow(intensity: Float = 0.4f): Color {
    return this.copy(alpha = intensity)
}

/**
 * Get store-specific glow color
 */
@ReadOnlyComposable
fun getStoreGlow(storeId: String, extendedColors: ExtendedColors): Color {
    return when (storeId.lowercase()) {
        "shufersal" -> extendedColors.shufersalGlow
        "victory" -> extendedColors.victoryGlow
        "rami_levy", "ramilevy" -> extendedColors.ramiLevyGlow
        "mega" -> extendedColors.megaGlow
        else -> extendedColors.glass
    }
}

/**
 * Get store-specific brand color
 */
@ReadOnlyComposable
fun getStoreBrand(storeId: String, extendedColors: ExtendedColors): Color {
    return when (storeId.lowercase()) {
        "shufersal" -> extendedColors.shufersal
        "victory" -> extendedColors.victory
        "rami_levy", "ramilevy" -> extendedColors.ramiLevy
        "mega" -> extendedColors.mega
        else -> extendedColors.genericStore
    }
}

/**
 * Get price indicator color based on price tier
 */
@ReadOnlyComposable
fun getPriceColor(tier: String, extendedColors: ExtendedColors): Color {
    return when (tier.lowercase()) {
        "best", "lowest", "low" -> extendedColors.bestPrice
        "mid", "medium", "average" -> extendedColors.midPrice
        "high", "highest", "expensive" -> extendedColors.highPrice
        else -> extendedColors.midPrice
    }
}

/**
 * Get price glow color based on price tier
 */
@ReadOnlyComposable
fun getPriceGlow(tier: String, extendedColors: ExtendedColors): Color {
    return when (tier.lowercase()) {
        "best", "lowest", "low" -> extendedColors.bestPriceGlow
        "mid", "medium", "average" -> extendedColors.warning.toGlow()
        "high", "highest", "expensive" -> extendedColors.errorGlow
        else -> extendedColors.warning.toGlow()
    }
}

/**
 * Get semantic color with optional glow
 */
@ReadOnlyComposable
fun getSemanticColor(
    semantic: String,
    withGlow: Boolean = false,
    extendedColors: ExtendedColors
): Color {
    return when (semantic.lowercase()) {
        "success" -> if (withGlow) extendedColors.successGlow else extendedColors.success
        "warning" -> if (withGlow) extendedColors.warningGlow else extendedColors.warning
        "error" -> if (withGlow) extendedColors.errorGlow else extendedColors.error
        "info" -> if (withGlow) extendedColors.infoGlow else extendedColors.info
        else -> if (withGlow) extendedColors.glass else extendedColors.electricMint
    }
}

/**
 * Color utility extensions
 */

/**
 * Check if color is considered "light" for contrast purposes
 */
fun Color.isLight(): Boolean {
    val luminance = 0.299 * red + 0.587 * green + 0.114 * blue
    return luminance > 0.5f
}

/**
 * Get appropriate text color for background
 */
fun Color.contrastingTextColor(): Color {
    return if (this.isLight()) Color.Black else Color.White
}

/**
 * Generate lighter shade of color
 */
fun Color.lighten(factor: Float = 0.2f): Color {
    return this.copy(
        red = (red + (1f - red) * factor).coerceIn(0f, 1f),
        green = (green + (1f - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1f - blue) * factor).coerceIn(0f, 1f)
    )
}

/**
 * Generate darker shade of color
 */
fun Color.darken(factor: Float = 0.2f): Color {
    return this.copy(
        red = (red * (1f - factor)).coerceIn(0f, 1f),
        green = (green * (1f - factor)).coerceIn(0f, 1f),
        blue = (blue * (1f - factor)).coerceIn(0f, 1f)
    )
}