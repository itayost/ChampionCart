package com.example.championcart.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Champion Cart - Electric Harmony Color System
 * Modern, vibrant color palette with glow effects and gradients
 */
object ChampionCartColors {
    // Primary Palette - Electric Mint (Primary Action)
    val electricMint = Color(0xFF00D9A3)
    val electricMintLight = Color(0xFF4DFFCE)
    val electricMintDark = Color(0xFF00A67C)
    val electricMintGlow = Color(0x5500D9A3) // 33% opacity for glow effects

    // Secondary Palette - Cosmic Purple (Secondary/Premium)
    val cosmicPurple = Color(0xFF7B3FF2)
    val cosmicPurpleLight = Color(0xFF9D6FFF)
    val cosmicPurpleDark = Color(0xFF5A2DB8)
    val cosmicPurpleGlow = Color(0x557B3FF2)

    // Accent Palette - Neon Coral (Deals/Urgent)
    val neonCoral = Color(0xFFFF6B9D)
    val neonCoralLight = Color(0xFFFF8FB3)
    val neonCoralDark = Color(0xFFE44D7A)
    val neonCoralGlow = Color(0x55FF6B9D)

    // Semantic Colors - Clear Communication
    val successGreen = Color(0xFF00E676)
    val successGreenGlow = Color(0x5500E676)
    val warningAmber = Color(0xFFFFB300)
    val warningAmberGlow = Color(0x55FFB300)
    val errorRed = Color(0xFFFF5252)
    val errorRedGlow = Color(0x55FF5252)
    val infoBlue = Color(0xFF448AFF)
    val infoBlueGlow = Color(0x55448AFF)

    // Price Indicators with Glow
    val bestPrice = Color(0xFF00E676) // Success Green
    val bestPriceGlow = Color(0x5500E676)
    val midPrice = Color(0xFFFFB300)  // Warning Amber (alias for warningAmber)
    val midPriceGlow = Color(0x55FFB300)
    val highPrice = Color(0xFFFF5252) // Error Red
    val highPriceGlow = Color(0x55FF5252)

    // Background Colors - Light Mode
    val backgroundLight = Color(0xFFFFFFFF)
    val backgroundLightGradientEnd = Color(0xFFF8F9FF) // Subtle blue tint
    val surfaceLight = Color(0xFFFFFFFF)
    val surfaceVariantLight = Color(0xFFF8F9FF)

    // Background Colors - Dark Mode
    val backgroundDark = Color(0xFF0A0E27)
    val backgroundDarkGradientEnd = Color(0xFF1A1F3A)
    val surfaceDark = Color(0xFF1A1F3A)
    val surfaceVariantDark = Color(0xFF252B4A)

    // Glass Morphism Colors
    val glassLight = Color(0x14FFFFFF) // 8% white
    val glassLightBorder = Color(0x2EFFFFFF) // 18% white
    val glassDark = Color(0x0DFFFFFF) // 5% white
    val glassDarkBorder = Color(0x1AFFFFFF) // 10% white
    val glassFrosted = Color(0xB8F8F9FF) // 72% opacity
    val glassFrostedBorder = Color(0x4DFFFFFF) // 30% white

    // Text Colors - Light Mode
    val textPrimary = Color(0xFF0A0E27)
    val textSecondary = Color(0xFF5A6178)
    val textTertiary = Color(0xFF8B92A8)
    val textOnPrimary = Color(0xFFFFFFFF)
    val textOnSecondary = Color(0xFFFFFFFF)
    val textOnAccent = Color(0xFFFFFFFF)

    // Text Colors - Dark Mode
    val textPrimaryDark = Color(0xFFFFFFFF)
    val textSecondaryDark = Color(0xFFB8BED0)
    val textTertiaryDark = Color(0xFF8B92A8)

    // Shadow Colors
    val shadowLight = Color(0x14000000) // 8% black
    val shadowMedium = Color(0x29000000) // 16% black
    val shadowDark = Color(0x3D000000) // 24% black

    // Store Brand Colors - Updated for vibrancy
    val shufersalBrand = Color(0xFF0066FF) // Brighter blue
    val shufersalGlow = Color(0x550066FF)
    val victoryBrand = Color(0xFFFF3366) // Brighter red
    val victoryGlow = Color(0x55FF3366)
    val ramiLevyBrand = Color(0xFFFF6B35) // Orange
    val ramiLevyGlow = Color(0x55FF6B35)
    val megaBrand = Color(0xFF00D68F) // Green
    val megaGlow = Color(0x5500D68F)
    val genericStore = Color(0xFF8B92A8) // Neutral gray

    // Dynamic Theme Colors (Time-based)
    // Morning (6am-12pm)
    val morningPrimary = Color(0xFFFFB74D) // Warm peach
    val morningAccent = Color(0xFFFF8A65)
    val morningGradientStart = Color(0xFFFFF3E0)
    val morningGradientEnd = Color(0xFFFFE0B2)

    // Afternoon (12pm-6pm) - Uses default Electric Mint

    // Evening (6pm-12am)
    val eveningPrimary = Color(0xFF7B3FF2) // Cosmic Purple
    val eveningAccent = Color(0xFF9D6FFF)
    val eveningGradientStart = Color(0xFF1A1F3A)
    val eveningGradientEnd = Color(0xFF2D3561)

    // Night (12am-6am) - Dark mode activated
    val nightPrimary = Color(0xFF448AFF) // Calming blue
    val nightAccent = Color(0xFF82B1FF)

    // Special Effects Colors
    val shimmerHighlight = Color(0x33FFFFFF)
    val rippleColor = Color(0x1F000000)
    val divider = Color(0x14000000)
    val dividerDark = Color(0x14FFFFFF)
}

/**
 * Gradient definitions for the Electric Harmony theme
 */
data class GradientColors(
    val colors: List<Color>,
    val start: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset(0f, 0f),
    val end: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset(1f, 1f)
)

object ChampionCartGradients {
    // Primary CTA Gradient
    val primaryAction = GradientColors(
        colors = listOf(
            ChampionCartColors.electricMint,
            ChampionCartColors.successGreen
        )
    )

    // Premium/Secondary Gradient
    val premium = GradientColors(
        colors = listOf(
            ChampionCartColors.cosmicPurple,
            ChampionCartColors.cosmicPurpleLight
        )
    )

    // Deals/Urgent Gradient
    val deals = GradientColors(
        colors = listOf(
            ChampionCartColors.neonCoral,
            ChampionCartColors.neonCoralLight
        )
    )

    // Success State Gradient
    val success = GradientColors(
        colors = listOf(
            ChampionCartColors.successGreen,
            ChampionCartColors.electricMint
        )
    )

    // Animated Border Gradient (for special effects)
    val animatedBorder = GradientColors(
        colors = listOf(
            ChampionCartColors.electricMint,
            ChampionCartColors.cosmicPurple,
            ChampionCartColors.neonCoral,
            ChampionCartColors.electricMint
        )
    )

    // Background Gradients
    val lightBackground = GradientColors(
        colors = listOf(
            ChampionCartColors.backgroundLight,
            ChampionCartColors.backgroundLightGradientEnd
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(0f, 1f)
    )

    val darkBackground = GradientColors(
        colors = listOf(
            ChampionCartColors.backgroundDark,
            ChampionCartColors.backgroundDarkGradientEnd
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(0f, 1f)
    )

    // Glass Effect Gradients
    val glassOverlay = GradientColors(
        colors = listOf(
            Color(0x0DFFFFFF),
            Color(0x05FFFFFF)
        )
    )

    // Price Indicator Gradient (for progress bars)
    val priceIndicator = GradientColors(
        colors = listOf(
            ChampionCartColors.bestPrice,
            ChampionCartColors.midPrice,
            ChampionCartColors.highPrice
        )
    )
}

/**
 * Get the appropriate glow color for any given color
 */
fun Color.withGlow(): Color {
    return this.copy(alpha = 0.33f)
}

/**
 * Get time-based theme colors
 */
fun getTimeBasedColors(hour: Int): TimeBasedColors {
    return when (hour) {
        in 6..11 -> TimeBasedColors(
            primary = ChampionCartColors.morningPrimary,
            accent = ChampionCartColors.morningAccent,
            gradientStart = ChampionCartColors.morningGradientStart,
            gradientEnd = ChampionCartColors.morningGradientEnd,
            isDark = false
        )
        in 12..17 -> TimeBasedColors(
            primary = ChampionCartColors.electricMint,
            accent = ChampionCartColors.neonCoral,
            gradientStart = ChampionCartColors.backgroundLight,
            gradientEnd = ChampionCartColors.backgroundLightGradientEnd,
            isDark = false
        )
        in 18..23 -> TimeBasedColors(
            primary = ChampionCartColors.eveningPrimary,
            accent = ChampionCartColors.eveningAccent,
            gradientStart = ChampionCartColors.eveningGradientStart,
            gradientEnd = ChampionCartColors.eveningGradientEnd,
            isDark = true
        )
        else -> TimeBasedColors(
            primary = ChampionCartColors.nightPrimary,
            accent = ChampionCartColors.nightAccent,
            gradientStart = ChampionCartColors.backgroundDark,
            gradientEnd = ChampionCartColors.backgroundDarkGradientEnd,
            isDark = true
        )
    }
}

data class TimeBasedColors(
    val primary: Color,
    val accent: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val isDark: Boolean
)