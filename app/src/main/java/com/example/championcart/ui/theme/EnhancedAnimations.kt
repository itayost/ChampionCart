package com.example.championcart.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Simple Enhanced Animation Modifiers for Champion Cart
 * Modern micro-interactions with haptic feedback and performance optimization
 * SIMPLIFIED VERSION - No compilation errors
 */

/**
 * Modern button press animation with haptic feedback
 */
fun Modifier.modernPressAnimation(
    enabled: Boolean = true,
    pressScale: Float = 0.94f,
    hapticEnabled: Boolean = true
) = composed {
    val haptic = LocalHapticFeedback.current
    val reduceMotion = LocalReduceMotion.current

    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !reduceMotion) pressScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "modern_press_scale"
    )

    this
        .scale(scale)
        .pointerInput(enabled, hapticEnabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (hapticEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }

                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    }
                )
            }
        }
}

/**
 * Organic entrance animation for cards and components
 */
fun Modifier.organicEntrance(
    delay: Int = 0,
    enabled: Boolean = true
) = composed {
    val reduceMotion = LocalReduceMotion.current

    var visible by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (visible && enabled && !reduceMotion) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "organic_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible && enabled) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (reduceMotion) 150 else 400,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "organic_alpha"
    )

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }

    this
        .scale(if (reduceMotion) 1f else scale)
        .graphicsLayer { this.alpha = alpha }
}

/**
 * Price counting animation for savings displays
 */
@Composable
fun animatedPriceValue(
    targetValue: Float,
    currency: String = "₪",
    duration: Int = 800
): String {
    val reduceMotion = LocalReduceMotion.current

    val animatedValue by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = if (reduceMotion) {
            snap()
        } else {
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "price_count"
    )

    return "$currency%.2f".format(animatedValue)
}

/**
 * Simple tap reveal animation for cards
 */
fun Modifier.tapReveal(
    enabled: Boolean = true,
    onReveal: () -> Unit = {}
) = composed {
    var isRevealed by remember { mutableStateOf(false) }

    val offsetX by animateFloatAsState(
        targetValue = if (isRevealed) 100f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "reveal_offset"
    )

    this
        .offset { IntOffset(offsetX.roundToInt(), 0) }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onTap = {
                        isRevealed = !isRevealed
                        onReveal()
                    }
                )
            }
        }
}

/**
 * Loading pulse animation for placeholders
 */
fun Modifier.loadingPulse(
    enabled: Boolean = true
) = composed {
    val reduceMotion = LocalReduceMotion.current

    if (!enabled || reduceMotion) {
        this
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "loading_pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )

        this.graphicsLayer { this.alpha = alpha }
    }
}

/**
 * Attention grabbing bounce for notifications
 */
fun Modifier.attentionBounce(
    trigger: Boolean,
    intensity: Float = 1.1f
) = composed {
    val reduceMotion = LocalReduceMotion.current

    val scale by animateFloatAsState(
        targetValue = if (trigger && !reduceMotion) intensity else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "attention_bounce"
    )

    this.scale(scale)
}

/**
 * Staggered list animation for product grids
 */
fun Modifier.staggeredListAnimation(
    index: Int,
    staggerDelay: Int = 50
) = composed {
    val reduceMotion = LocalReduceMotion.current
    val delay = if (reduceMotion) 0 else (index * staggerDelay).coerceAtMost(500)

    this.organicEntrance(delay = delay, enabled = !reduceMotion)
}

/**
 * Morphing animation between states
 */
fun Modifier.morphingAnimation(
    progress: Float,
    startScale: Float = 1f,
    endScale: Float = 1.2f,
    startAlpha: Float = 1f,
    endAlpha: Float = 0.8f
) = composed {
    val reduceMotion = LocalReduceMotion.current

    if (reduceMotion) {
        this
    } else {
        val scale = lerp(startScale, endScale, progress)
        val alpha = lerp(startAlpha, endAlpha, progress)

        this
            .scale(scale)
            .graphicsLayer { this.alpha = alpha }
    }
}

/**
 * Breath animation for floating action buttons
 */
fun Modifier.breathAnimation(
    enabled: Boolean = true,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f
) = composed {
    val reduceMotion = LocalReduceMotion.current

    if (!enabled || reduceMotion) {
        this
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "breath")
        val scale by infiniteTransition.animateFloat(
            initialValue = minScale,
            targetValue = maxScale,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breath_scale"
        )

        this.scale(scale)
    }
}

/**
 * Glass card hover effect for desktop/large screens
 */
fun Modifier.glassCardHover(
    enabled: Boolean = true,
    hoverScale: Float = 1.02f,
    hoverAlpha: Float = 0.9f
) = composed {
    val reduceMotion = LocalReduceMotion.current
    var isHovered by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isHovered && enabled && !reduceMotion) hoverScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "glass_hover_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isHovered && enabled) hoverAlpha else 1f,
        animationSpec = tween(200),
        label = "glass_hover_alpha"
    )

    this
        .scale(scale)
        .graphicsLayer { this.alpha = alpha }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        isHovered = true
                        try {
                            awaitRelease()
                        } finally {
                            isHovered = false
                        }
                    }
                )
            }
        }
}

/**
 * Performance-aware animation wrapper
 */
@Composable
fun PerformanceAwareAnimation(
    content: @Composable (
        reduceMotion: Boolean,
        batteryOptimization: Boolean,
        slowNetwork: Boolean
    ) -> Unit
) {
    val reduceMotion = LocalReduceMotion.current
    val batteryOptimization = remember { false } // Simplified for now
    val slowNetwork = remember { false } // Simplified for now

    content(reduceMotion, batteryOptimization, slowNetwork)
}

// Helper function for linear interpolation
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}