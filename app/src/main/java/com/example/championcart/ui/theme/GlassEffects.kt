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
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
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

    val glassColor = if (isDark) colors.glass else colors.glass
    val borderColor = if (isDark) colors.glassBorder else colors.glassBorder

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
        .blur(radius = blurRadius)
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
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    this.drawWithCache {
        val brush = Brush.sweepGradient(
            colors = gradientColors,
            center = Offset(size.width / 2, size.height / 2)
        )

        onDrawBehind {
            rotate(angle) {
                drawRoundRect(
                    brush = brush,
                    size = size,
                    style = Stroke(width = borderWidth.toPx())
                )
            }
        }
    }.clip(shape)
}

/**
 * Glow effect for important elements
 */
fun Modifier.glowEffect(
    glowColor: Color,
    glowRadius: Dp = 20.dp,
    shape: Shape = ComponentShapes.Card,
    animateGlow: Boolean = true
) = composed {
    val infiniteTransition = if (animateGlow) {
        rememberInfiniteTransition(label = "glow")
    } else null

    val glowAlpha = if (animateGlow && infiniteTransition != null) {
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = AnimationDurations.glowPulse,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow_alpha"
        ).value
    } else {
        0.5f
    }

    this.drawBehind {
        // Simple glow effect using multiple concentric shadows
        for (i in 1..3) {
            drawRect(
                color = glowColor.copy(alpha = glowAlpha / (i * 2)),
                size = size,
                style = Stroke(width = (glowRadius.toPx() / i))
            )
        }
    }
}

/**
 * Electric border with animated energy effect
 */
fun Modifier.electricBorder(
    color: Color = ChampionCartColors.electricMint,
    borderWidth: Dp = 2.dp,
    particleCount: Int = 20,
    shape: Shape = ComponentShapes.Card
) = composed {
    val particles = remember {
        List(particleCount) {
            ElectricParticle(Random.nextFloat() * 360f)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "electric")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    this
        .border(borderWidth, color, shape)
        .drawWithCache {
            onDrawBehind {
                particles.forEach { particle ->
                    val progress = (time + particle.offset) % 1f
                    val alpha = if (progress < 0.5f) progress * 2 else (1f - progress) * 2

                    val angle = progress * 360f
                    val radius = size.minDimension / 2
                    val x = size.width / 2 + cos(Math.toRadians(angle.toDouble())).toFloat() * radius
                    val y = size.height / 2 + sin(Math.toRadians(angle.toDouble())).toFloat() * radius

                    drawCircle(
                        color = color.copy(alpha = alpha * 0.8f),
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }
        .clip(shape)
}

/**
 * Shimmer loading effect
 */
fun Modifier.shimmerEffect(
    shimmerColor: Color = ChampionCartColors.shimmerHighlight,
    animationDuration: Int = AnimationDurations.shimmer
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    this.drawWithCache {
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                shimmerColor,
                Color.Transparent
            ),
            start = Offset(size.width * translateAnimation - size.width, 0f),
            end = Offset(size.width * translateAnimation, size.height)
        )

        onDrawBehind {
            drawRect(brush = brush, size = size)
        }
    }
}

/**
 * Noise texture overlay for glass effects
 */
fun Modifier.noiseTexture(
    noiseAlpha: Float = GlassParams.noiseOpacity
) = composed {
    this.drawBehind {
        // Simplified noise - in production, use a noise texture
        val noiseColor = Color.White.copy(alpha = noiseAlpha)
        val density = 50 // Reduce number of dots for better performance
        for (i in 0..density) {
            val x = Random.nextFloat() * size.width
            val y = Random.nextFloat() * size.height
            drawCircle(
                color = noiseColor,
                radius = 0.5f,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Morphing shadow for dynamic elevation
 */
fun Modifier.morphingShadow(
    elevation: Dp,
    shape: Shape = ComponentShapes.Card,
    animateElevation: Boolean = false
) = composed {
    val targetElevation = if (animateElevation) {
        val infiniteTransition = rememberInfiniteTransition(label = "shadow")
        infiniteTransition.animateDp(
            initialValue = elevation,
            targetValue = elevation + 4.dp,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "elevation"
        ).value
    } else elevation

    this.shadow(
        elevation = targetElevation,
        shape = shape,
        ambientColor = Color.Black.copy(alpha = 0.12f),
        spotColor = Color.Black.copy(alpha = 0.12f)
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

// Helper data class for electric particles
private data class ElectricParticle(val offset: Float)

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