package com.example.championcart.presentation.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale

/**
 * Shared modifier extensions for UI effects
 */

/**
 * Shimmer loading effect
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.surface
            ),
            start = Offset(translateAnim - 500f, 0f),
            end = Offset(translateAnim, 0f)
        )
    )
}

/**
 * Pulsating effect for emphasis
 */
fun Modifier.pulsate(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "pulsate")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    this.then(
        Modifier.drawWithContent {
            drawContext.canvas.save()
            drawContext.transform.scale(scale)
            this@drawWithContent.drawContent()
            drawContext.canvas.restore()
        }
    )
}

/**
 * Glow effect for selected items
 */
fun Modifier.glowEffect(
    color: Color,
    alpha: Float = 0.3f,
    radius: Float = 20f
): Modifier = composed {
    drawWithContent {
        drawContent()
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = size.minDimension / 2 + radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Fill
        )
    }
}