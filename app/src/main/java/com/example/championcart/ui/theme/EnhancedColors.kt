package com.example.championcart.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalTime

/**
 * Enhanced Color System for Champion Cart
 * Adds dynamic color functions and time-based color management
 * EXTENDS your existing Color.kt - doesn't replace it
 */

/**
 * Dynamic color provider based on time of day
 */
@Composable
fun getDynamicPrimaryColor(): Color {
    val currentHour = remember { LocalTime.now().hour }

    return when (currentHour) {
        in 6..11 -> ChampionCartColors.morningPrimary
        in 18..23 -> ChampionCartColors.eveningPrimary
        in 0..5 -> ChampionCartColors.nightPrimary
        else -> ChampionCartColors.electricMint // Default afternoon
    }
}

/**
 * Animated color transitions for smooth time-based changes
 */
@Composable
fun animatedDynamicColor(
    baseColor: Color,
    targetColor: Color = getDynamicPrimaryColor(),
    animationDuration: Int = 1000
): Color {
    return animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = animationDuration),
        label = "dynamic_color"
    ).value
}

/**
 * Get appropriate glow color for any base color
 */
fun Color.toGlowColor(intensity: Float = 0.4f): Color {
    return this.copy(alpha = intensity)
}

/**
 * Generate semantic color variations
 */
object SemanticColors {

    @Composable
    fun success(isDark: Boolean = false): Color {
        return if (isDark) ChampionCartColors.successGreen.copy(alpha = 0.8f)
        else ChampionCartColors.successGreen
    }

    @Composable
    fun warning(isDark: Boolean = false): Color {
        return if (isDark) ChampionCartColors.warningAmber.copy(alpha = 0.8f)
        else ChampionCartColors.warningAmber
    }

    @Composable
    fun error(isDark: Boolean = false): Color {
        return if (isDark) ChampionCartColors.errorRed.copy(alpha = 0.8f)
        else ChampionCartColors.errorRed
    }

    @Composable
    fun info(isDark: Boolean = false): Color {
        return if (isDark) ChampionCartColors.infoBlue.copy(alpha = 0.8f)
        else ChampionCartColors.infoBlue
    }
}

/**
 * Price indicator colors with animations
 */
object PriceColors {

    @Composable
    fun bestPrice(animated: Boolean = false): Color {
        val baseColor = ChampionCartColors.successGreen
        return if (animated) {
            animateColorAsState(
                targetValue = baseColor,
                animationSpec = tween(500),
                label = "best_price"
            ).value
        } else baseColor
    }

    @Composable
    fun midPrice(): Color = ChampionCartColors.warningAmber

    @Composable
    fun highPrice(animated: Boolean = false): Color {
        val baseColor = ChampionCartColors.errorRed
        return if (animated) {
            animateColorAsState(
                targetValue = baseColor,
                animationSpec = tween(500),
                label = "high_price"
            ).value
        } else baseColor
    }

    @Composable
    fun savingsHighlight(): Color {
        val primary = getDynamicPrimaryColor()
        return animateColorAsState(
            targetValue = primary,
            animationSpec = tween(800),
            label = "savings_highlight"
        ).value
    }
}

/**
 * Store brand colors with dynamic variations
 */
object StoreColors {

    fun getShufersal(alpha: Float = 1f) = ChampionCartColors.shufersalBrand.copy(alpha = alpha)
    fun getVictory(alpha: Float = 1f) = ChampionCartColors.victoryBrand.copy(alpha = alpha)
    fun getRamiLevy(alpha: Float = 1f) = ChampionCartColors.ramiLevyBrand.copy(alpha = alpha)
    fun getMega(alpha: Float = 1f) = ChampionCartColors.megaBrand.copy(alpha = alpha)

    @Composable
    fun getStoreGlow(storeType: String): Color {
        return when (storeType.lowercase()) {
            "shufersal" -> ChampionCartColors.shufersalGlow
            "victory" -> ChampionCartColors.victoryGlow
            "rami_levy" -> ChampionCartColors.ramiLevyGlow
            "mega" -> ChampionCartColors.megaGlow
            else -> MaterialTheme.extendedColors.glass
        }
    }
}

/**
 * Background gradients following mockup designs
 */
object BackgroundGradients {

    @Composable
    fun primaryGradient(): Brush {
        val primary = getDynamicPrimaryColor()
        return Brush.linearGradient(
            colors = listOf(
                primary.copy(alpha = 0.1f),
                Color.Transparent
            )
        )
    }

    @Composable
    fun timeBasedGradient(): Brush {
        val currentHour = remember { LocalTime.now().hour }

        val colors = when (currentHour) {
            in 6..11 -> listOf(
                ChampionCartColors.morningGradientStart,
                ChampionCartColors.morningGradientEnd
            )
            in 18..23 -> listOf(
                ChampionCartColors.eveningGradientStart,
                ChampionCartColors.eveningGradientEnd
            )
            in 0..5 -> listOf(
                ChampionCartColors.eveningGradientStart,
                ChampionCartColors.eveningGradientEnd
            )
            else -> listOf(
                Color(0xFFF8F9FF),
                Color(0xFFE8F4FD)
            )
        }

        return Brush.linearGradient(colors = colors)
    }

    val glassGradient = Brush.linearGradient(
        colors = listOf(
            ChampionCartColors.glassLight,
            ChampionCartColors.glassDark
        )
    )

    val successGradient = Brush.linearGradient(
        colors = listOf(
            ChampionCartColors.successGreen,
            ChampionCartColors.successGreen.copy(alpha = 0.7f)
        )
    )

    val warningGradient = Brush.linearGradient(
        colors = listOf(
            ChampionCartColors.warningAmber,
            ChampionCartColors.warningAmber.copy(alpha = 0.7f)
        )
    )

    val errorGradient = Brush.linearGradient(
        colors = listOf(
            ChampionCartColors.errorRed,
            ChampionCartColors.errorRed.copy(alpha = 0.7f)
        )
    )
}

/**
 * Color utilities for the design system
 */
object ColorUtils {

    /**
     * Convert color to hex string for logging/debugging
     */
    fun Color.toHex(): String {
        val argb = this.toArgb()
        return String.format("#%08X", argb)
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
}

/**
 * Animated color palette for special effects
 */
@Composable
fun rememberRainbowColors(): List<Color> {
    return remember {
        listOf(
            ChampionCartColors.electricMint,
            ChampionCartColors.cosmicPurple,
            ChampionCartColors.neonCoral,
            ChampionCartColors.successGreen,
            ChampionCartColors.warningAmber,
            ChampionCartColors.infoBlue
        )
    }
}

/**
 * Get contextual color based on state
 */
@Composable
fun getContextualColor(
    context: String,
    state: String = "default",
    isAnimated: Boolean = false
): Color {
    val baseColor = when (context) {
        "price" -> when (state) {
            "best" -> PriceColors.bestPrice(isAnimated)
            "mid" -> PriceColors.midPrice()
            "high" -> PriceColors.highPrice(isAnimated)
            else -> MaterialTheme.colorScheme.onSurface
        }
        "status" -> when (state) {
            "success" -> SemanticColors.success()
            "warning" -> SemanticColors.warning()
            "error" -> SemanticColors.error()
            "info" -> SemanticColors.info()
            else -> MaterialTheme.colorScheme.onSurface
        }
        "action" -> when (state) {
            "primary" -> getDynamicPrimaryColor()
            "secondary" -> ChampionCartColors.cosmicPurple
            "accent" -> ChampionCartColors.neonCoral
            else -> MaterialTheme.colorScheme.primary
        }
        else -> MaterialTheme.colorScheme.onSurface
    }

    return if (isAnimated) {
        animateColorAsState(
            targetValue = baseColor,
            animationSpec = tween(300),
            label = "contextual_color"
        ).value
    } else {
        baseColor
    }
}