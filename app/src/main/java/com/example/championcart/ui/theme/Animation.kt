package com.example.championcart.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.animation.*

/**
 * Champion Cart Animations - Simplified
 * Essential animations only
 */

object Animations {
    // Spring specs for Float animations
    val springGentle = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    val springBouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val springSnappy = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    // Durations
    const val durationQuick = 200
    const val durationStandard = 300
    const val durationSlow = 500

    // Enter/Exit transitions
    val fadeIn = fadeIn(
        animationSpec = tween(durationStandard)
    )

    val fadeOut = fadeOut(
        animationSpec = tween(durationQuick)
    )

    val slideInFromBottom = slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(
            durationMillis = durationStandard,
            easing = FastOutSlowInEasing
        )
    )

    val slideOutToBottom = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(
            durationMillis = durationQuick,
            easing = FastOutSlowInEasing
        )
    )

    val scaleIn = scaleIn(
        initialScale = 0.9f,
        animationSpec = springBouncy
    )

    val scaleOut = scaleOut(
        targetScale = 0.9f,
        animationSpec = springGentle
    )
}