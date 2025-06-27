package com.example.championcart.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Champion Cart Glassmorphic Effects
 * Modern glass effects for Electric Harmony design
 */

enum class GlassIntensity {
    Light,
    Medium,
    Heavy,
    Ultra
}

/**
 * Main glassmorphic modifier
 */
fun Modifier.glass(
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp = 4.dp
): Modifier = composed {
    val config = ChampionCartTheme.config

    if (!config.enableGlassEffects || config.performanceMode) {
        // Simplified version for performance
        return@composed this
            .clip(shape)
            .background(
                when (intensity) {
                    GlassIntensity.Light -> ChampionCartColors.Glass.Light
                    GlassIntensity.Medium -> ChampionCartColors.Glass.Medium
                    GlassIntensity.Heavy -> ChampionCartColors.Glass.Heavy
                    GlassIntensity.Ultra -> ChampionCartColors.Glass.Ultra
                }
            )
    }

    // Full glass effect
    val glassColor = when (intensity) {
        GlassIntensity.Light -> ChampionCartColors.Glass.Light
        GlassIntensity.Medium -> ChampionCartColors.Glass.Medium
        GlassIntensity.Heavy -> ChampionCartColors.Glass.Heavy
        GlassIntensity.Ultra -> ChampionCartColors.Glass.Ultra
    }

    val borderColor = Color.White.copy(
        alpha = when (intensity) {
            GlassIntensity.Light -> 0.1f
            GlassIntensity.Medium -> 0.15f
            GlassIntensity.Heavy -> 0.2f
            GlassIntensity.Ultra -> 0.25f
        }
    )

    this
        .shadow(
            elevation = shadowElevation,
            shape = shape,
            ambientColor = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f),
            spotColor = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.2f)
        )
        .clip(shape)
        .background(glassColor)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = shape
        )
}

/**
 * Gradient glass effect
 */
fun Modifier.gradientGlass(
    colors: List<Color> = ChampionCartColors.Gradient.glass,
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp),
    angle: Float = 45f
): Modifier = composed {
    this
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(0f, 0f),
                end = Offset(1000f, 1000f)
            )
        )
        .glass(intensity = intensity, shape = shape)
}

/**
 * Animated glass effect
 */
fun Modifier.animatedGlass(
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp),
    duration: Int = 2000
): Modifier = composed {
    val config = ChampionCartTheme.config

    if (config.reduceMotion || config.performanceMode) {
        return@composed glass(intensity, shape)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "glass")
    val alpha = infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glassAlpha"
    )

    this
        .glass(intensity, shape)
        .graphicsLayer { this.alpha = alpha.value }
}

/**
 * Interactive glass with hover/press states
 */
fun Modifier.interactiveGlass(
    isPressed: Boolean = false,
    isHovered: Boolean = false,
    baseIntensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier = composed {
    val config = ChampionCartTheme.config

    val targetIntensity = when {
        isPressed -> GlassIntensity.Ultra
        isHovered -> GlassIntensity.Heavy
        else -> baseIntensity
    }

    val animatedAlpha = if (!config.reduceMotion) {
        animateFloatAsState(
            targetValue = when (targetIntensity) {
                GlassIntensity.Light -> 0.1f
                GlassIntensity.Medium -> 0.2f
                GlassIntensity.Heavy -> 0.3f
                GlassIntensity.Ultra -> 0.4f
            },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "glassIntensity"
        ).value
    } else {
        when (targetIntensity) {
            GlassIntensity.Light -> 0.1f
            GlassIntensity.Medium -> 0.2f
            GlassIntensity.Heavy -> 0.3f
            GlassIntensity.Ultra -> 0.4f
        }
    }

    this
        .clip(shape)
        .background(Color.White.copy(alpha = animatedAlpha))
        .border(
            width = if (isPressed) 2.dp else 1.dp,
            color = if (isPressed) {
                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.5f)
            } else {
                Color.White.copy(alpha = 0.15f)
            },
            shape = shape
        )
}

/**
 * Price glass effect
 */
fun Modifier.priceGlass(
    priceLevel: PriceLevel
): Modifier = composed {
    val color = when (priceLevel) {
        PriceLevel.Best -> ChampionCartColors.Price.Best
        PriceLevel.Mid -> ChampionCartColors.Price.Mid
        PriceLevel.High -> ChampionCartColors.Price.High
    }

    gradientGlass(
        colors = listOf(
            color.copy(alpha = 0.1f),
            color.copy(alpha = 0.05f)
        ),
        intensity = GlassIntensity.Light,
        shape = ComponentShapes.Product.Badge
    )
}

enum class PriceLevel { Best, Mid, High }

/**
 * Store glass effect
 */
fun Modifier.storeGlass(
    storeName: String
): Modifier = composed {
    val storeColor = when (storeName.lowercase()) {
        "shufersal", "שופרסל" -> ChampionCartColors.Store.Shufersal
        "rami levi", "רמי לוי" -> ChampionCartColors.Store.RamiLevi
        "victory", "ויקטורי" -> ChampionCartColors.Store.Victory
        "mega", "מגה" -> ChampionCartColors.Store.Mega
        "osher ad", "אושר עד" -> ChampionCartColors.Store.OsherAd
        "coop", "קופ" -> ChampionCartColors.Store.Coop
        else -> ChampionCartColors.Brand.ElectricMint
    }

    gradientGlass(
        colors = listOf(
            storeColor.copy(alpha = 0.15f),
            storeColor.copy(alpha = 0.08f)
        ),
        shape = ComponentShapes.Store.Card
    )
}

/**
 * Success/Warning/Error glass effects
 */
fun Modifier.successGlass() = gradientGlass(
    colors = ChampionCartColors.Gradient.success,
    intensity = GlassIntensity.Light
)

fun Modifier.warningGlass() = gradientGlass(
    colors = listOf(
        ChampionCartColors.Semantic.Warning.copy(alpha = 0.1f),
        ChampionCartColors.Semantic.Warning.copy(alpha = 0.05f)
    ),
    intensity = GlassIntensity.Light
)

fun Modifier.errorGlass() = gradientGlass(
    colors = listOf(
        ChampionCartColors.Semantic.Error.copy(alpha = 0.1f),
        ChampionCartColors.Semantic.Error.copy(alpha = 0.05f)
    ),
    intensity = GlassIntensity.Light
)

/**
 * Shimmer glass for loading states
 */
fun Modifier.shimmerGlass(
    shape: Shape = RoundedCornerShape(16.dp),
    duration: Int = 1200
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslateAnim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    this
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.05f),
                    Color.White.copy(alpha = 0.15f),
                    Color.White.copy(alpha = 0.05f)
                ),
                start = Offset(shimmerTranslateAnim.value - 200f, 0f),
                end = Offset(shimmerTranslateAnim.value, 0f)
            )
        )
}