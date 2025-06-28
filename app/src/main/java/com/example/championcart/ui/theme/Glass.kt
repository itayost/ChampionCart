package com.example.championcart.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.HazeStyle

/**
 * Theme-Aware Glassmorphic Effects
 * Adapts to light/dark theme for better visibility
 * Enhanced with Haze library for real blur effects
 */

enum class GlassIntensity {
    Light,
    Medium,
    Heavy,
    Ultra
}

enum class GlassStyle {
    Default,    // Adaptive based on theme
    Elevated,   // Strong shadow for light theme
    Subtle,     // Minimal effect
    Bordered,   // With defined border
    Colored     // With color tint (new)
}

// Global Haze state for consistent blur across components
val LocalHazeState = compositionLocalOf<HazeState?> { null }

/**
 * Main theme-aware glassmorphic modifier
 * Enhanced with real blur using Haze
 */
@Composable
fun Modifier.glass(
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp),
    style: GlassStyle = GlassStyle.Default,
    borderWidth: Dp = 1.5.dp,
    shadowElevation: Dp? = null, // Auto-calculated if null
    darkTheme: Boolean = isSystemInDarkTheme(),
    hazeState: HazeState? = LocalHazeState.current, // New parameter for Haze
    tintColor: Color? = null // Optional color tint
): Modifier = composed {
    val config = ChampionCartTheme.config

    // Determine blur radius based on intensity
    val blurRadius = when (intensity) {
        GlassIntensity.Light -> 10.dp
        GlassIntensity.Medium -> 20.dp
        GlassIntensity.Heavy -> 30.dp
        GlassIntensity.Ultra -> 40.dp
    }

    if (!config.enableGlassEffects || config.performanceMode) {
        // Simplified version with theme awareness
        return@composed this
            .clip(shape)
            .background(
                if (darkTheme) {
                    // Dark theme - white overlay
                    when (intensity) {
                        GlassIntensity.Light -> Color.White.copy(alpha = 0.08f)
                        GlassIntensity.Medium -> Color.White.copy(alpha = 0.12f)
                        GlassIntensity.Heavy -> Color.White.copy(alpha = 0.16f)
                        GlassIntensity.Ultra -> Color.White.copy(alpha = 0.24f)
                    }
                } else {
                    // Light theme - darker overlay for contrast
                    when (intensity) {
                        GlassIntensity.Light -> Color.Black.copy(alpha = 0.03f)
                        GlassIntensity.Medium -> Color.Black.copy(alpha = 0.05f)
                        GlassIntensity.Heavy -> Color.Black.copy(alpha = 0.08f)
                        GlassIntensity.Ultra -> Color.Black.copy(alpha = 0.12f)
                    }
                }
            )
    }

    // Theme-aware glass configuration
    val (glassColors, borderColor, defaultElevation) = if (darkTheme) {
        // Dark theme configuration
        val baseAlpha = when (intensity) {
            GlassIntensity.Light -> 0.08f
            GlassIntensity.Medium -> 0.12f
            GlassIntensity.Heavy -> 0.18f
            GlassIntensity.Ultra -> 0.25f
        }

        Triple(
            // Glass gradient
            listOf(
                Color.White.copy(alpha = baseAlpha * 1.2f),
                Color.White.copy(alpha = baseAlpha),
                Color.White.copy(alpha = baseAlpha * 0.8f)
            ),
            // Border
            Color.White.copy(alpha = baseAlpha * 1.5f),
            // Shadow
            when (intensity) {
                GlassIntensity.Light -> 4.dp
                GlassIntensity.Medium -> 6.dp
                GlassIntensity.Heavy -> 8.dp
                GlassIntensity.Ultra -> 10.dp
            }
        )
    } else {
        // Light theme configuration - enhanced for visibility
        val baseAlpha = when (intensity) {
            GlassIntensity.Light -> 0.95f
            GlassIntensity.Medium -> 0.97f
            GlassIntensity.Heavy -> 0.98f
            GlassIntensity.Ultra -> 0.99f
        }

        Triple(
            // Glass gradient - nearly opaque white
            listOf(
                Color.White.copy(alpha = baseAlpha),
                Color.White.copy(alpha = baseAlpha * 0.98f),
                Color.White.copy(alpha = baseAlpha * 0.96f)
            ),
            // Border - subtle gray for definition
            Color.Black.copy(alpha = 0.08f),
            // Shadow - stronger for light theme
            when (intensity) {
                GlassIntensity.Light -> 2.dp
                GlassIntensity.Medium -> 4.dp
                GlassIntensity.Heavy -> 6.dp
                GlassIntensity.Ultra -> 8.dp
            }
        )
    }

    val actualElevation = shadowElevation ?: when (style) {
        GlassStyle.Elevated -> defaultElevation * 2
        GlassStyle.Subtle -> defaultElevation * 0.5f
        else -> defaultElevation
    }

    // Build the modifier chain
    var modifier = this

    // Apply Haze blur if available
    if (hazeState != null && config.enableGlassEffects) {
        modifier = modifier.hazeChild(
            state = hazeState,
            shape = shape,
            style = HazeDefaults.style(
                backgroundColor = when (style) {
                    GlassStyle.Colored -> tintColor ?: ChampionCartColors.Brand.ElectricMint
                    else -> MaterialTheme.colorScheme.surface
                }.copy(alpha = glassColors[0].alpha),
                tint = if (darkTheme) {
                    Color.Black.copy(alpha = 0.2f)
                } else {
                    Color.White.copy(alpha = 0.3f)
                },
                blurRadius = blurRadius
            )
        )
    }

    modifier = modifier
        .shadow(
            elevation = actualElevation,
            shape = shape,
            ambientColor = if (darkTheme) {
                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.08f)
            } else {
                Color.Black.copy(alpha = 0.05f)
            },
            spotColor = if (darkTheme) {
                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.12f)
            } else {
                Color.Black.copy(alpha = 0.08f)
            }
        )
        .clip(shape)

    // Only apply background gradient if Haze is not being used
    if (hazeState == null || !config.enableGlassEffects) {
        modifier = modifier.background(
            brush = Brush.verticalGradient(colors = glassColors)
        )
    }

    // Add border based on style
    when (style) {
        GlassStyle.Default -> {
            if (!darkTheme) {
                // Always add border in light theme for definition
                modifier = modifier.border(
                    width = 1.dp,
                    color = borderColor,
                    shape = shape
                )
            }
        }
        GlassStyle.Bordered -> {
            modifier = modifier.border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            )
        }
        GlassStyle.Elevated -> {
            // Additional inner shadow effect for light theme
            if (!darkTheme) {
                modifier = modifier
                    .border(
                        width = 0.5.dp,
                        color = Color.Black.copy(alpha = 0.05f),
                        shape = shape
                    )
            }
        }
        GlassStyle.Subtle -> {
            // Minimal or no border
        }
        GlassStyle.Colored -> {
            // Add colored border
            modifier = modifier.border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        tintColor?.copy(alpha = 0.4f) ?: borderColor,
                        tintColor?.copy(alpha = 0.1f) ?: borderColor.copy(alpha = 0.1f)
                    )
                ),
                shape = shape
            )
        }
    }

    modifier
}

/**
 * Modern glass with animated shimmer effect
 */
@Composable
fun Modifier.modernGlass(
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp),
    shimmer: Boolean = false,
    hazeState: HazeState? = LocalHazeState.current
): Modifier = composed {
    val infiniteTransition = if (shimmer) {
        rememberInfiniteTransition(label = "shimmer")
    } else null

    val shimmerAlpha = infiniteTransition?.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )?.value ?: 0.5f

    var modifier = this.glass(
        intensity = intensity,
        shape = shape,
        hazeState = hazeState
    )

    if (shimmer) {
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0f),
                        Color.White.copy(alpha = shimmerAlpha * 0.3f),
                        Color.White.copy(alpha = 0f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )
        }
    }

    modifier
}

/**
 * Gradient glass with theme awareness
 */
@Composable
fun Modifier.gradientGlass(
    colors: List<Color>,
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp),
    darkTheme: Boolean = isSystemInDarkTheme()
): Modifier = composed {
    val adjustedColors = if (darkTheme) {
        // Dark theme - use provided colors as-is
        colors
    } else {
        // Light theme - ensure colors are visible
        colors.map { color ->
            if (color.alpha < 0.5f) {
                // Boost alpha for light theme
                color.copy(alpha = (color.alpha * 3f).coerceAtMost(0.3f))
            } else {
                color
            }
        }
    }

    this
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                colors = adjustedColors,
                start = Offset(0f, 0f),
                end = Offset(1000f, 1000f)
            )
        )
        .glass(
            intensity = intensity,
            shape = shape,
            style = if (darkTheme) GlassStyle.Subtle else GlassStyle.Default,
            darkTheme = darkTheme
        )
}

/**
 * Interactive glass with enhanced press states
 */
@Composable
fun Modifier.interactiveGlass(
    isPressed: Boolean = false,
    isHovered: Boolean = false,
    baseIntensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp),
    darkTheme: Boolean = isSystemInDarkTheme()
): Modifier = composed {
    val config = ChampionCartTheme.config

    val targetIntensity = when {
        isPressed -> GlassIntensity.Ultra
        isHovered -> GlassIntensity.Heavy
        else -> baseIntensity
    }

    val animatedScale = if (!config.reduceMotion) {
        animateFloatAsState(
            targetValue = if (isPressed) 0.98f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "glassScale"
        ).value
    } else 1f

    this
        .graphicsLayer {
            scaleX = animatedScale
            scaleY = animatedScale
        }
        .glass(
            intensity = targetIntensity,
            shape = shape,
            style = if (isPressed && !darkTheme) GlassStyle.Elevated else GlassStyle.Default,
            darkTheme = darkTheme
        )
}

/**
 * Card-specific glass effect with better light theme visibility
 */
@Composable
fun Modifier.cardGlass(
    intensity: GlassIntensity = GlassIntensity.Light,
    shape: Shape = ComponentShapes.Card.Medium,
    darkTheme: Boolean = isSystemInDarkTheme()
): Modifier = composed {
    glass(
        intensity = intensity,
        shape = shape,
        style = if (darkTheme) GlassStyle.Default else GlassStyle.Elevated,
        shadowElevation = if (darkTheme) 4.dp else 2.dp,
        darkTheme = darkTheme
    )
}

/**
 * Button-specific glass effect
 */
@Composable
fun Modifier.buttonGlass(
    intensity: GlassIntensity = GlassIntensity.Medium,
    isPrimary: Boolean = false,
    shape: Shape = ComponentShapes.Button.Medium,
    darkTheme: Boolean = isSystemInDarkTheme()
): Modifier = composed {
    if (isPrimary) {
        // Primary buttons don't need glass in light theme
        this
    } else {
        glass(
            intensity = intensity,
            shape = shape,
            style = if (darkTheme) GlassStyle.Default else GlassStyle.Bordered,
            darkTheme = darkTheme
        )
    }
}

/**
 * Price level glass with theme awareness
 */
@Composable
fun Modifier.priceGlass(
    priceLevel: PriceLevel,
    animated: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme()
): Modifier = composed {
    val color = when (priceLevel) {
        PriceLevel.Best -> ChampionCartColors.Price.Best
        PriceLevel.Mid -> ChampionCartColors.Price.Mid
        PriceLevel.High -> ChampionCartColors.Price.High
    }

    val config = ChampionCartTheme.config

    if (animated && !config.reduceMotion && priceLevel == PriceLevel.Best) {
        val infiniteTransition = rememberInfiniteTransition(label = "bestPrice")
        val pulseAlpha = infiniteTransition.animateFloat(
            initialValue = if (darkTheme) 0.1f else 0.05f,
            targetValue = if (darkTheme) 0.2f else 0.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pricePulse"
        )

        this
            .clip(ComponentShapes.Product.Badge)
            .background(
                if (darkTheme) {
                    color.copy(alpha = pulseAlpha.value)
                } else {
                    // Light theme - use tinted white
                    Color.White.copy(alpha = 0.9f)
                }
            )
            .border(
                width = 1.5.dp,
                color = color.copy(alpha = if (darkTheme) 0.4f else 0.8f),
                shape = ComponentShapes.Product.Badge
            )
    } else {
        this
            .clip(ComponentShapes.Product.Badge)
            .background(
                if (darkTheme) {
                    color.copy(alpha = 0.15f)
                } else {
                    // Light theme - tinted white background
                    Color.White.copy(alpha = 0.95f)
                }
            )
            .border(
                width = 1.5.dp,
                color = color.copy(alpha = if (darkTheme) 0.3f else 0.6f),
                shape = ComponentShapes.Product.Badge
            )
    }
}

enum class PriceLevel { Best, Mid, High }