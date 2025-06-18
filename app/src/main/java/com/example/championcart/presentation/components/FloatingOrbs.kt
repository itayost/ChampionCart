package com.example.championcart.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random

/**
 * Champion Cart - Floating Orbs Background Animation
 * Creates the magical floating orb effect from your design system
 */

data class Orb(
    val id: Int,
    val baseX: Float,
    val baseY: Float,
    val size: Float,
    val color: Color,
    val speed: Float,
    val amplitude: Float,
    val phase: Float
)

@Composable
fun FloatingOrbsBackground(
    modifier: Modifier = Modifier,
    orbCount: Int = 5,
    alpha: Float = 0.6f
) {
    val density = LocalDensity.current
    val colors = MaterialTheme.colorScheme

    // Create stable orbs that persist across recompositions
    val orbs = remember {
        List(orbCount) { index ->
            Orb(
                id = index,
                baseX = Random.nextFloat(),
                baseY = Random.nextFloat(),
                size = Random.nextFloat() * 80f + 40f, // 40-120dp
                color = when (index % 4) {
                    0 -> colors.primary
                    1 -> colors.secondary
                    2 -> colors.tertiary
                    else -> colors.error
                },
                speed = Random.nextFloat() * 0.5f + 0.3f, // 0.3-0.8
                amplitude = Random.nextFloat() * 50f + 30f, // 30-80dp
                phase = Random.nextFloat() * 2f * PI.toFloat()
            )
        }
    }

    // Infinite animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating_orbs")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .alpha(alpha)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        orbs.forEach { orb ->
            // Calculate floating position
            val timeOffset = time + orb.phase
            val xOffset = cos(timeOffset * orb.speed) * orb.amplitude
            val yOffset = sin(timeOffset * orb.speed * 0.7f) * orb.amplitude * 0.5f

            val x = orb.baseX * canvasWidth + xOffset
            val y = orb.baseY * canvasHeight + yOffset

            drawFloatingOrb(
                center = Offset(x, y),
                radius = with(density) { orb.size.dp.toPx() },
                color = orb.color,
                time = timeOffset
            )
        }
    }
}

private fun DrawScope.drawFloatingOrb(
    center: Offset,
    radius: Float,
    color: Color,
    time: Float
) {
    // Pulsing effect
    val pulseScale = 1f + sin(time * 2f) * 0.1f
    val currentRadius = radius * pulseScale

    // Create gradient with glow effect
    val gradient = Brush.radialGradient(
        colors = listOf(
            color.copy(alpha = 0.4f),
            color.copy(alpha = 0.2f),
            color.copy(alpha = 0.1f),
            Color.Transparent
        ),
        center = center,
        radius = currentRadius * 1.5f
    )

    // Draw the main orb
    drawCircle(
        brush = gradient,
        radius = currentRadius * 1.5f,
        center = center
    )

    // Draw inner glow
    val innerGradient = Brush.radialGradient(
        colors = listOf(
            color.copy(alpha = 0.6f),
            color.copy(alpha = 0.3f),
            Color.Transparent
        ),
        center = center,
        radius = currentRadius
    )

    drawCircle(
        brush = innerGradient,
        radius = currentRadius,
        center = center
    )
}

/**
 * Hero orbs for special sections
 */
@Composable
fun HeroOrbs(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    // Main animation
    val infiniteTransition = rememberInfiniteTransition(label = "hero_orbs")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = minOf(size.width, size.height) * 0.3f

        // Draw three orbiting orbs
        repeat(3) { index ->
            val angle = rotation + (index * 120f)
            val radians = Math.toRadians(angle.toDouble()).toFloat()
            val orbCenter = Offset(
                center.x + cos(radians) * baseRadius,
                center.y + sin(radians) * baseRadius
            )

            val orbColor = when (index) {
                0 -> colors.primary
                1 -> colors.secondary
                else -> colors.tertiary
            }

            drawFloatingOrb(
                center = orbCenter,
                radius = 40f * pulse,
                color = orbColor,
                time = rotation / 60f
            )
        }
    }
}

/**
 * Subtle background orbs for cards
 */
@Composable
fun CardBackgroundOrbs(
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_orbs")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Two small orbs for subtle background effect
        val orb1Center = Offset(
            size.width * 0.8f + cos(time) * 20f,
            size.height * 0.2f + sin(time * 0.8f) * 15f
        )

        val orb2Center = Offset(
            size.width * 0.2f + cos(time * 1.3f) * 25f,
            size.height * 0.7f + sin(time * 1.1f) * 20f
        )

        drawFloatingOrb(
            center = orb1Center,
            radius = 25f,
            color = primaryColor,
            time = time
        )

        drawFloatingOrb(
            center = orb2Center,
            radius = 30f,
            color = primaryColor.copy(alpha = 0.8f), // Fixed: removed .hue reference
            time = time + 1f
        )
    }
}