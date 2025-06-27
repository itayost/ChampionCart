package com.example.championcart.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FloatingOrb(
    color: Color,
    size: Dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    duration: Int = 6000,
    delay: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")

    val animatedY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbY"
    )

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY + animatedY.dp)
            .size(size)
            .blur(radius = 40.dp)
            .background(
                color = color,
                shape = CircleShape
            )
    )
}