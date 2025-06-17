package com.example.championcart.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Champion Cart - Modern Animation System
 * Material Design 3 Expressive spring physics with Electric Harmony feel
 * Following 2025 mobile design trends with intelligent motion
 */

/**
 * Spring Animation Specifications
 * Based on Google's Material Design 3 Expressive motion system
 */
object SpringSpecs {
    // Damping ratios - Control bounce behavior
    const val DampingRatioNoBounce = Spring.DampingRatioNoBouncy        // 1.0f - No bounce
    const val DampingRatioLowBounce = Spring.DampingRatioLowBouncy      // 0.75f - Subtle bounce
    const val DampingRatioMediumBounce = Spring.DampingRatioMediumBouncy // 0.5f - Moderate bounce
    const val DampingRatioHighBounce = 0.3f                              // Custom - High bounce

    // Stiffness values - Control animation speed
    const val StiffnessVeryLow = Spring.StiffnessVeryLow        // 50f - Slow, fluid
    const val StiffnessLow = Spring.StiffnessLow                // 200f - Relaxed
    const val StiffnessMedium = Spring.StiffnessMedium          // 400f - Balanced
    const val StiffnessMediumHigh = 600f                        // Custom - Snappy
    const val StiffnessHigh = Spring.StiffnessHigh              // 1400f - Quick
    const val StiffnessVeryHigh = 2000f                         // Custom - Instant (was Spring.StiffnessVeryHigh)

    // Standard spring configurations
    val Gentle = spring<Float>(
        dampingRatio = DampingRatioNoBounce,
        stiffness = StiffnessLow
    )

    val Smooth = spring<Float>(
        dampingRatio = DampingRatioLowBounce,
        stiffness = StiffnessMedium
    )

    val Bouncy = spring<Float>(
        dampingRatio = DampingRatioMediumBounce,
        stiffness = StiffnessMediumHigh
    )

    val Playful = spring<Float>(
        dampingRatio = DampingRatioHighBounce,
        stiffness = StiffnessHigh
    )

    val Snappy = spring<Float>(
        dampingRatio = DampingRatioNoBounce,
        stiffness = StiffnessHigh
    )
}

/**
 * Duration-based animations for when springs aren't appropriate
 */
object DurationSpecs {
    // Fast interactions - Under 200ms for immediate feedback
    const val Fast = 150
    const val VeryFast = 100
    const val Instant = 50

    // Standard interactions - 200-500ms for most UI changes
    const val Standard = 300
    const val Medium = 400
    const val Slow = 500

    // Complex animations - Over 500ms for elaborate transitions
    const val SlowComplex = 600
    const val VerySlowComplex = 800
    const val Elaborate = 1000

    // Easing curves
    val StandardEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val DecelerateEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val AccelerateEasing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    val AccelerateDecelerateEasing = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)
    val ElasticEasing = CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f)
}

/**
 * Glassmorphic Enter/Exit Transitions
 */
object GlassmorphicTransitions {
    // Slide from edge with glass effect
    @OptIn(ExperimentalAnimationApi::class)
    val SlideFromRight = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    ) + fadeIn() with
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = spring(
                    dampingRatio = SpringSpecs.DampingRatioLowBounce,
                    stiffness = SpringSpecs.StiffnessMedium
                )
            ) + fadeOut()

    @OptIn(ExperimentalAnimationApi::class)
    val SlideFromLeft = slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    ) + fadeIn() with
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = spring(
                    dampingRatio = SpringSpecs.DampingRatioLowBounce,
                    stiffness = SpringSpecs.StiffnessMedium
                )
            ) + fadeOut()

    // Glassmorphic fade for simple content changes
    @OptIn(ExperimentalAnimationApi::class)
    val Fade = fadeIn(animationSpec = tween(DurationSpecs.Standard)) with
            fadeOut(animationSpec = tween(DurationSpecs.Fast))

    // Scale transition for modal appearances
    @OptIn(ExperimentalAnimationApi::class)
    val Scale = scaleIn(
        initialScale = 0.8f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioMediumBounce,
            stiffness = SpringSpecs.StiffnessMediumHigh
        )
    ) + fadeIn() with
            scaleOut(
                targetScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = SpringSpecs.DampingRatioLowBounce,
                    stiffness = SpringSpecs.StiffnessMedium
                )
            ) + fadeOut()

    // Shared element-like transition
    @OptIn(ExperimentalAnimationApi::class)
    val SharedElement = scaleIn(
        initialScale = 0.9f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    ) + fadeIn() with
            scaleOut(
                targetScale = 1.1f,
                animationSpec = spring(
                    dampingRatio = SpringSpecs.DampingRatioLowBounce,
                    stiffness = SpringSpecs.StiffnessMedium
                )
            ) + fadeOut()
}

/**
 * Gesture-Based Animations
 */
fun Modifier.glassCardPress(): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = SpringSpecs.Bouncy,
        label = "glass_card_press"
    )

    this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

fun Modifier.swipeToReveal(
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {}
): Modifier = composed {
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = SpringSpecs.Smooth,
        label = "swipe_reveal"
    )

    this
        .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    when {
                        offsetX > 200 -> onSwipeRight()
                        offsetX < -200 -> onSwipeLeft()
                    }
                    offsetX = 0f
                },
                onHorizontalDrag = { _, dragAmount ->
                    offsetX += dragAmount
                }
            )
        }
}

fun Modifier.pressEffect(): Modifier = composed {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = SpringSpecs.Snappy,
        label = "press_effect"
    )

    this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

fun Modifier.bounceOnLoad(): Modifier = composed {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = SpringSpecs.Playful,
        label = "bounce_on_load"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    this.scale(scale)
}

fun Modifier.glassmorphicHover(): Modifier = composed {
    var isHovered by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(
        targetValue = if (isHovered) 8.dp else 2.dp,
        animationSpec = tween(DurationSpecs.Fast),
        label = "glassmorphic_hover"
    )

    this
        .shadow(elevation, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
        .pointerInput(Unit) {
            // Note: Hover detection would require desktop platform
            // This is a placeholder for touch-based hover simulation
        }
}

/**
 * Supporting enums and data classes
 */
enum class SwipeDirection { Left, Right, Up, Down }

data class AnimationConfig(
    val duration: Int = DurationSpecs.Standard,
    val easing: Easing = DurationSpecs.StandardEasing,
    val delay: Int = 0
)

/**
 * Utility functions for animation state management
 */
@Composable
fun rememberAnimationState(initialValue: Boolean = false): MutableState<Boolean> {
    return remember { mutableStateOf(initialValue) }
}

fun <T> AnimationSpec<T>.withReducedMotion(
    reduceMotion: Boolean,
    alternativeSpec: AnimationSpec<T> = snap()
): AnimationSpec<T> = if (reduceMotion) alternativeSpec else this

/**
 * Common animation sequences
 */
object AnimationSequences {
    // Staggered list item animations
    fun staggeredListAnimation(
        index: Int,
        baseDelay: Int = 50
    ): AnimationSpec<Float> = tween(
        durationMillis = DurationSpecs.Standard,
        delayMillis = index * baseDelay,
        easing = DurationSpecs.StandardEasing
    )

    // Loading sequence animation
    fun loadingSequence(
        phase: Int,
        totalPhases: Int
    ): AnimationSpec<Float> = tween(
        durationMillis = DurationSpecs.Medium,
        delayMillis = (phase * DurationSpecs.Standard) / totalPhases,
        easing = DurationSpecs.DecelerateEasing
    )
}