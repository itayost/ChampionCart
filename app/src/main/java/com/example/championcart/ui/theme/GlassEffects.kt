package com.example.championcart.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

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

    this.drawWithCache {
        val brush = Brush.linearGradient(
            colors = gradientColors,
            start = Offset(0f, 0f),
            end = Offset(size.width * offset, size.height * offset)
        )

        onDrawBehind {
            val strokeWidth = borderWidth.toPx()
            drawWithLayer {
                // Draw background
                drawRect(Color.Transparent)
                // Draw border
                drawRoundRect(
                    brush = brush,
                    size = size,
                    style = Stroke(width = strokeWidth)
                )
            }
        }
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

    this.drawBehind {
        val glowRadiusPx = glowRadius.toPx()
        drawWithLayer {
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
        }
    }
}

/*
 * Subtle noise texture for glass surfaces
 */
fun Modifier.noiseTexture(
    opacity: Float = 0.02f
) = this.drawWithCache {
    val noise = generateNoise(size.width.toInt(), size.height.toInt())
    onDrawOver {
        drawWithLayer {
            drawRect(
                color = Color.White.copy(alpha = opacity),
                size = size,
                blendMode = BlendMode.Overlay
            )
        }
    }
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
) = this.drawWithCache {
    val brush = Brush.linearGradient(
        colors = gradient.colors.map { it.copy(alpha = alpha) },
        start = Offset(size.width * gradient.start.x, size.height * gradient.start.y),
        end = Offset(size.width * gradient.end.x, size.height * gradient.end.y)
    )

    onDrawBehind {
        drawRect(brush = brush, size = size)
    }
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

    this.drawWithCache {
        val brush = Brush.linearGradient(
            colors = colors,
            start = Offset(size.width * offset, 0f),
            end = Offset(size.width * (offset + 0.5f), size.height)
        )

        onDrawBehind {
            drawRect(brush = brush, size = size)
        }
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

// Helper functions
private fun generateNoise(width: Int, height: Int): ImageBitmap {
    // Simple noise generation - in a real app you might use a more sophisticated approach
    val random = Random(42) // Fixed seed for consistent noise
    val pixels = IntArray(width * height) {
        if (random.nextFloat() > 0.5f) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
    }
    return ImageBitmap(width, height, ImageBitmapConfig.Argb8888, true) {
        // This is a simplified implementation
        // In a real app, you'd properly set the pixel data
    }
}

// Extension for drawing with layer effects
private fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    drawIntoCanvas { canvas ->
        canvas.saveLayer(Rect(Offset.Zero, size), Paint())
        block()
        canvas.restore()
    }
}