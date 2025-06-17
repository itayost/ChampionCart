package com.example.championcart.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Champion Cart - Advanced Visual Effects
 * Glow effects, shimmer animations, and special visual treatments
 * Following the Electric Harmony design system
 */

/**
 * Apply electric glow effect to any composable
 */
fun Modifier.electricGlow(
    glowColor: Color = ChampionCartColors.electricMintGlow,
    glowRadius: Dp = 20.dp,
    intensity: Float = 1f
) = composed {
    val animatedIntensity by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = intensity * 0.7f,
        targetValue = intensity,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_intensity"
    )

    this.drawBehind {
        val radius = glowRadius.toPx()
        val paint = Paint().apply {
            color = glowColor.copy(alpha = animatedIntensity * 0.6f)
            isAntiAlias = true
        }

        // Create multiple glow layers for depth
        repeat(3) { layer ->
            val layerRadius = radius * (1f + layer * 0.3f)
            val layerAlpha = animatedIntensity * (0.8f - layer * 0.2f)

            drawIntoCanvas { canvas ->
                canvas.drawCircle(
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = layerRadius,
                    paint = paint.apply {
                        color = glowColor.copy(alpha = layerAlpha)
                    }
                )
            }
        }
    }
}

/**
 * Cosmic purple glow for premium elements
 */
fun Modifier.cosmicGlow(
    intensity: Float = 1f
) = electricGlow(
    glowColor = ChampionCartColors.cosmicPurpleGlow,
    glowRadius = 24.dp,
    intensity = intensity
)

/**
 * Neon coral glow for deals and urgent actions
 */
fun Modifier.neonGlow(
    intensity: Float = 1f
) = electricGlow(
    glowColor = ChampionCartColors.neonCoralGlow,
    glowRadius = 16.dp,
    intensity = intensity
)

/**
 * Success glow for best prices
 */
fun Modifier.successGlow(
    intensity: Float = 1f
) = electricGlow(
    glowColor = ChampionCartColors.successGreenGlow,
    glowRadius = 12.dp,
    intensity = intensity
)

/**
 * Shimmer effect for loading states
 */
fun Modifier.shimmerEffect(
    highlightColor: Color = ChampionCartColors.shimmerHighlight,
    backgroundColor: Color = Color.Gray.copy(alpha = 0.1f),
    animationDuration: Int = 1500
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                backgroundColor,
                highlightColor,
                backgroundColor
            ),
            start = Offset(offsetX * 1000f, 0f),
            end = Offset((offsetX + 0.3f) * 1000f, 100f)
        )
    )
}

/**
 * Floating orbs effect for backgrounds
 */
@Composable
fun FloatingOrbs(
    modifier: Modifier = Modifier,
    orbCount: Int = 3,
    colors: List<Color> = listOf(
        ChampionCartColors.electricMintGlow,
        ChampionCartColors.cosmicPurpleGlow,
        ChampionCartColors.neonCoralGlow
    )
) {
    Box(modifier = modifier.fillMaxSize()) {
        repeat(orbCount) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "orb_$index")

            val offsetX by infiniteTransition.animateFloat(
                initialValue = -100f,
                targetValue = 100f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 8000 + index * 2000,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "orb_x_$index"
            )

            val offsetY by infiniteTransition.animateFloat(
                initialValue = -50f,
                targetValue = 50f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 6000 + index * 1500,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "orb_y_$index"
            )

            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 4000 + index * 1000,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "orb_scale_$index"
            )

            Box(
                modifier = Modifier
                    .size((60 + index * 20).dp)
                    .offset(offsetX.dp, offsetY.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                colors[index % colors.size].copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                    .alpha(0.6f)
            )
        }
    }
}

/**
 * Gradient background following mockup designs
 */
fun Modifier.gradientBackground(
    startColor: Color = Color(0xFFF8F9FF),
    endColor: Color = Color(0xFFE8F4FD),
    angle: Float = 135f
) = this.background(
    brush = Brush.linearGradient(
        colors = listOf(startColor, endColor),
        start = Offset.Zero,
        end = Offset(
            x = cos(Math.toRadians(angle.toDouble())).toFloat() * 1000f,
            y = sin(Math.toRadians(angle.toDouble())).toFloat() * 1000f
        )
    )
)

/**
 * Price change animation effect
 */
fun Modifier.priceChangeEffect(
    isDecreasing: Boolean,
    animate: Boolean = true
) = composed {
    val scale by animateFloatAsState(
        targetValue = if (animate) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "price_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (animate) 0.8f else 1f,
        animationSpec = tween(300),
        label = "price_alpha"
    )

    val glowColor = if (isDecreasing)
        ChampionCartColors.successGreenGlow
    else
        ChampionCartColors.errorRedGlow

    this
        .scale(scale)
        .alpha(alpha)
        .electricGlow(glowColor = glowColor, intensity = if (animate) 1.5f else 0f)
}

/**
 * Success burst animation for savings
 */
@Composable
fun SuccessBurst(
    show: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (show) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "burst_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (show) 360f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "burst_rotation"
    )

    if (show) {
        Box(
            modifier = modifier
                .scale(scale)
                .drawBehind {
                    // Draw burst rays
                    repeat(8) { ray ->
                        val angle = (ray * 45f) + rotation
                        val startRadius = size.minDimension * 0.2f
                        val endRadius = size.minDimension * 0.4f

                        val startX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * startRadius
                        val startY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * startRadius
                        val endX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * endRadius
                        val endY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * endRadius

                        drawLine(
                            color = ChampionCartColors.successGreen,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
        )
    }
}

/**
 * Glass morphism with enhanced blur simulation
 */
fun Modifier.enhancedGlass(
    backgroundColor: Color = ChampionCartColors.glassLight,
    borderColor: Color = ChampionCartColors.glassLightBorder,
    shape: Shape = ComponentShapes.GlassContainer,
    elevation: Dp = 8.dp
) = composed {
    this
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    backgroundColor.copy(alpha = 0.8f),
                    backgroundColor.copy(alpha = 0.4f)
                ),
                start = Offset.Zero,
                end = Offset(100f, 100f)
            ),
            shape = shape
        )
        .drawBehind {
            // Simple border drawing
            drawRect(
                color = borderColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
            )
        }
}