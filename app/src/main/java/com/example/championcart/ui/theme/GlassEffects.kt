package com.example.championcart.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Champion Cart - Glass Morphism Effects
 * Modern glass effects with blur, transparency, and glow
 */

/**
 * Apply glass morphism effect to a composable
 */
fun Modifier.glassEffect(
    blurRadius: Dp = 20.dp,
    shape: Shape = ComponentShapes.GlassContainer,
    borderWidth: Dp = Dimensions.borderThin,
    isDark: Boolean = false
) = composed {
    val colors = MaterialTheme.extendedColors

    val glassColor = if (isDark) colors.glassDark else colors.glass
    val borderColor = if (isDark) colors.glassDarkBorder else colors.glassBorder

    this
        .shadow(
            elevation = Dimensions.elevationMedium,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.08f),
            spotColor = Color.Black.copy(alpha = 0.08f)
        )
        .background(
            color = glassColor,
            shape = shape
        )
        .border(
            width = borderWidth,
            color = borderColor,
            shape = shape
        )
        .clip(shape)
}

/**
 * Frosted glass effect for overlays
 */
fun Modifier.frostedGlass(
    blurRadius: Dp = 40.dp,
    shape: Shape = ComponentShapes.GlassContainer,
    tint: Color = Color.White,
    tintAlpha: Float = 0.72f
) = composed {
    val colors = MaterialTheme.extendedColors

    this
        .shadow(
            elevation = Dimensions.elevationLarge,
            shape = shape
        )
        .background(
            color = tint.copy(alpha = tintAlpha),
            shape = shape
        )
        .border(
            width = Dimensions.borderThin,
            color = colors.glassFrostedBorder,
            shape = shape
        )
        // Note: Real blur is not available in Compose, using alpha approximation
        .alpha(0.9f)
        .clip(shape)
}

/**
 * Animated gradient border for special components
 */
fun Modifier.animatedGradientBorder(
    gradientColors: List<Color> = listOf(
        ChampionCartColors.electricMint,
        ChampionCartColors.cosmicPurple,
        ChampionCartColors.neonCoral,
        ChampionCartColors.electricMint
    ),
    borderWidth: Dp = 2.dp,
    shape: Shape = ComponentShapes.Card,
    animationDuration: Int = 3000
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_border")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_offset"
    )

    this.drawWithContent {
        // Draw the content first
        drawContent()

        // Draw animated border
        val brush = Brush.linearGradient(
            colors = gradientColors,
            start = Offset(0f, 0f),
            end = Offset(size.width * offset, size.height * offset)
        )

        val strokeWidth = borderWidth.toPx()
        drawRoundRect(
            brush = brush,
            size = size,
            style = Stroke(width = strokeWidth)
        )
    }
}

/**
 * Glow effect for highlighting important elements
 */
fun Modifier.glowEffect(
    glowColor: Color,
    glowRadius: Dp = 16.dp,
    shape: Shape = ComponentShapes.Card,
    animateGlow: Boolean = false
) = composed {
    val animatedAlpha = if (animateGlow) {
        val infiniteTransition = rememberInfiniteTransition(label = "glow")
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow_alpha"
        ).value
    } else {
        0.5f
    }

    this.drawWithContent {
        // Draw glow effect behind content
        val glowRadiusPx = glowRadius.toPx()

        // Create glow effect using multiple shadow layers
        for (i in 1..3) {
            drawRect(
                color = glowColor.copy(alpha = animatedAlpha / (i * 2)),
                size = Size(
                    size.width + glowRadiusPx * i,
                    size.height + glowRadiusPx * i
                ),
                topLeft = Offset(
                    -glowRadiusPx * i / 2,
                    -glowRadiusPx * i / 2
                )
            )
        }

        // Draw the actual content on top
        drawContent()
    }
}

/**
 * Subtle noise texture for glass surfaces
 */
fun Modifier.noiseTexture(
    opacity: Float = 0.02f
) = this.drawWithContent {
    // Draw content first
    drawContent()

    // Add subtle noise overlay
    drawRect(
        color = Color.White.copy(alpha = opacity),
        size = size,
        blendMode = BlendMode.Overlay
    )
}

/**
 * Price indicator gradient background
 */
fun Modifier.priceGradient(
    price: Double,
    minPrice: Double,
    maxPrice: Double
) = composed {
    val colors = MaterialTheme.extendedColors
    val percentage = if (maxPrice > minPrice) {
        ((price - minPrice) / (maxPrice - minPrice)).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    val gradientColors = listOf(
        colors.bestPrice,
        colors.midPrice,
        colors.highPrice
    )

    this.drawBehind {
        drawRect(
            brush = Brush.horizontalGradient(
                colors = gradientColors,
                startX = 0f,
                endX = size.width * percentage
            ),
            size = size
        )
    }
}

/**
 * Pulsing scale effect for attention
 */
fun Modifier.pulseEffect(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    duration: Int = 1000
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    this.scale(scale)
}

/**
 * Gradient overlay effect
 */
fun Modifier.gradientOverlay(
    gradient: GradientColors,
    alpha: Float = 1f
) = this.drawWithContent {
    // Draw content first
    drawContent()

    // Add gradient overlay
    val brush = Brush.linearGradient(
        colors = gradient.colors.map { it.copy(alpha = alpha) },
        start = Offset(size.width * gradient.start.x, size.height * gradient.start.y),
        end = Offset(size.width * gradient.end.x, size.height * gradient.end.y)
    )

    drawRect(brush = brush, size = size)
}

/**
 * Shimmer effect for loading states
 */
fun Modifier.shimmerEffect(
    colors: List<Color> = listOf(
        Color.Gray.copy(alpha = 0.3f),
        Color.Gray.copy(alpha = 0.1f),
        Color.Gray.copy(alpha = 0.3f)
    ),
    duration: Int = 1200
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val offset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    this.drawWithContent {
        // Draw content first
        drawContent()

        // Add shimmer effect
        val brush = Brush.linearGradient(
            colors = colors,
            start = Offset(size.width * offset, 0f),
            end = Offset(size.width * (offset + 0.5f), size.height)
        )

        drawRect(brush = brush, size = size, blendMode = BlendMode.Overlay)
    }
}

/**
 * Glass card preset combining multiple effects
 */
fun Modifier.glassCard(
    shape: Shape = ComponentShapes.Card,
    glowColor: Color? = null,
    animateGlow: Boolean = false
) = this
    .glassEffect(shape = shape)
    .then(
        if (glowColor != null) {
            Modifier.glowEffect(
                glowColor = glowColor,
                shape = shape,
                animateGlow = animateGlow
            )
        } else Modifier
    )
    .noiseTexture()

/**
 * Premium glass card with animated border
 */
fun Modifier.premiumGlassCard(
    shape: Shape = ComponentShapes.Card
) = this
    .glassEffect(shape = shape)
    .animatedGradientBorder(
        gradientColors = ChampionCartGradients.animatedBorder.colors,
        shape = shape
    )
    .noiseTexture()

/**
 * Success state modifier
 */
fun Modifier.successState(
    enabled: Boolean
) = this.then(
    if (enabled) {
        Modifier
            .glowEffect(
                glowColor = ChampionCartColors.successGreen,
                animateGlow = true
            )
            .pulseEffect()
    } else {
        Modifier
    }
)

/**
 * Loading skeleton effect
 */
fun Modifier.skeletonEffect(
    shape: Shape = ComponentShapes.Card
) = this
    .clip(shape)
    .shimmerEffect()

// Helper functions - removed the problematic generateNoise function
// and drawWithLayer extension since they were causing issues

// Simple implementation that focuses on functionality over complex effects
private fun DrawScope.drawSimpleGlow(
    color: Color,
    radius: Float,
    center: Offset = this.center
) {
    for (i in 1..3) {
        drawCircle(
            color = color.copy(alpha = 0.1f / i),
            radius = radius * i,
            center = center
        )
    }
}