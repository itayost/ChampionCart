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
    const val StiffnessVeryHigh = Spring.StiffnessVeryHigh      // 10000f - Instant

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
    // Scale + Fade for glass appearance
    fun scaleIn(
        initialScale: Float = 0.8f,
        animationSpec: FiniteAnimationSpec<Float> = SpringSpecs.Bouncy
    ): EnterTransition = scaleIn(
        initialScale = initialScale,
        animationSpec = animationSpec
    ) + fadeIn(animationSpec = tween(DurationSpecs.Standard))

    fun scaleOut(
        targetScale: Float = 0.8f,
        animationSpec: FiniteAnimationSpec<Float> = SpringSpecs.Smooth
    ): ExitTransition = scaleOut(
        targetScale = targetScale,
        animationSpec = animationSpec
    ) + fadeOut(animationSpec = tween(DurationSpecs.Fast))

    // Slide + Glassmorphic glow
    fun slideInFromBottom(
        initialOffsetY: Int = 100,
        animationSpec: FiniteAnimationSpec<IntOffset> = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    ): EnterTransition = slideInVertically(
        initialOffsetY = { initialOffsetY },
        animationSpec = animationSpec
    ) + fadeIn(animationSpec = tween(DurationSpecs.Standard))

    fun slideOutToBottom(
        targetOffsetY: Int = 100,
        animationSpec: FiniteAnimationSpec<IntOffset> = spring(
            dampingRatio = SpringSpecs.DampingRatioNoBounce,
            stiffness = SpringSpecs.StiffnessHigh
        )
    ): ExitTransition = slideOutVertically(
        targetOffsetY = { targetOffsetY },
        animationSpec = animationSpec
    ) + fadeOut(animationSpec = tween(DurationSpecs.Fast))

    // Expand from center (good for FABs)
    fun expandIn(
        animationSpec: FiniteAnimationSpec<Float> = SpringSpecs.Playful
    ): EnterTransition = scaleIn(
        initialScale = 0.0f,
        animationSpec = animationSpec
    ) + fadeIn(animationSpec = tween(DurationSpecs.Medium))

    fun shrinkOut(
        animationSpec: FiniteAnimationSpec<Float> = SpringSpecs.Snappy
    ): ExitTransition = scaleOut(
        targetScale = 0.0f,
        animationSpec = animationSpec
    ) + fadeOut(animationSpec = tween(DurationSpecs.Fast))
}

/**
 * Interactive Animation Modifiers
 */

// Bouncy button press animation with haptic feedback
fun Modifier.bouncyClickable(
    enabled: Boolean = true,
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    this
        .scale(scale.value)
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            scale.animateTo(
                                scaleDown,
                                SpringSpecs.Playful
                            )
                        }
                        tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(
                                1f,
                                SpringSpecs.Bouncy
                            )
                        }
                    },
                    onTap = { onClick() }
                )
            }
        }
}

// Gentle hover/press effect for cards
fun Modifier.gentlePress(
    enabled: Boolean = true,
    scaleDown: Float = 0.98f,
    elevationIncrease: Dp = 4.dp,
    onPress: (() -> Unit)? = null
): Modifier = composed {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val elevation = remember { Animatable(0f) }
    val density = LocalDensity.current

    this
        .scale(scale.value)
        .shadow(elevation.value.dp, shape = MaterialTheme.shapes.medium)
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            launch {
                                scale.animateTo(scaleDown, SpringSpecs.Smooth)
                            }
                            launch {
                                elevation.animateTo(
                                    with(density) { elevationIncrease.toPx() },
                                    SpringSpecs.Smooth
                                )
                            }
                        }
                        onPress?.invoke()
                        tryAwaitRelease()
                        scope.launch {
                            launch {
                                scale.animateTo(1f, SpringSpecs.Gentle)
                            }
                            launch {
                                elevation.animateTo(0f, SpringSpecs.Gentle)
                            }
                        }
                    }
                )
            }
        }
}

// Magnetic swipe gesture (for dismissing cards)
fun Modifier.magneticSwipe(
    enabled: Boolean = true,
    threshold: Float = 200f,
    onSwipeComplete: (direction: SwipeDirection) -> Unit
): Modifier = composed {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current

    this
        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
        .pointerInput(enabled) {
            if (enabled) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (kotlin.math.abs(offsetX.value) > threshold) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val direction = if (offsetX.value > 0) {
                                    SwipeDirection.Right
                                } else {
                                    SwipeDirection.Left
                                }
                                onSwipeComplete(direction)
                            } else {
                                offsetX.animateTo(0f, SpringSpecs.Bouncy)
                            }
                        }
                    }
                ) { _, dragAmount ->
                    scope.launch {
                        val newValue = offsetX.value + dragAmount
                        offsetX.snapTo(newValue)
                    }
                }
            }
        }
}

// Pulsing animation for notifications/badges
fun Modifier.pulsingEffect(
    enabled: Boolean = true,
    minScale: Float = 1f,
    maxScale: Float = 1.1f,
    duration: Int = 1000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    if (enabled) {
        this.scale(scale)
    } else {
        this
    }
}

// Shimmer loading effect
fun Modifier.shimmerEffect(
    enabled: Boolean = true,
    color: Color = Color.White.copy(alpha = 0.6f),
    duration: Int = 1000
): Modifier = composed {
    if (!enabled) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    this.graphicsLayer {
        this.alpha = alpha
    }
}

// Floating animation for FABs and cards
fun Modifier.floatingEffect(
    enabled: Boolean = true,
    amplitude: Float = 10f,
    duration: Int = 2000
): Modifier = composed {
    if (!enabled) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -amplitude,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )

    this.offset(y = offsetY.dp)
}

// Glow effect for glassmorphic elements
fun Modifier.glowEffect(
    enabled: Boolean = true,
    color: Color = Color.White,
    radius: Dp = 20.dp
): Modifier = composed {
    if (!enabled) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    this.shadow(
        elevation = radius,
        shape = MaterialTheme.shapes.medium,
        ambientColor = color.copy(alpha = glowAlpha),
        spotColor = color.copy(alpha = glowAlpha)
    )
}

/**
 * Content transitions for navigation
 */
object ContentTransitions {
    // Slide transitions for navigation
    @OptIn(ExperimentalAnimationApi::class)
    val SlideLeft = slideInHorizontally { it } + fadeIn() with
            slideOutHorizontally { -it } + fadeOut()

    @OptIn(ExperimentalAnimationApi::class)
    val SlideRight = slideInHorizontally { -it } + fadeIn() with
            slideOutHorizontally { it } + fadeOut()

    @OptIn(ExperimentalAnimationApi::class)
    val SlideUp = slideInVertically { it } + fadeIn() with
            slideOutVertically { -it } + fadeOut()

    @OptIn(ExperimentalAnimationApi::class)
    val SlideDown = slideInVertically { -it } + fadeIn() with
            slideOutVertically { it } + fadeOut()

    // Fade transition for simple content changes
    @OptIn(ExperimentalAnimationApi::class)
    val Fade = fadeIn(animationSpec = tween(DurationSpecs.Standard)) with
            fadeOut(animationSpec = tween(DurationSpecs.Fast))

    // Scale transition for modal appearances
    @OptIn(ExperimentalAnimationApi::class)
    val Scale = scaleIn(
        initialScale = 0.8f,
        animationSpec = SpringSpecs.Bouncy
    ) + fadeIn() with
            scaleOut(
                targetScale = 0.8f,
                animationSpec = SpringSpecs.Smooth
            ) + fadeOut()

    // Shared element-like transition
    @OptIn(ExperimentalAnimationApi::class)
    val SharedElement = scaleIn(
        initialScale = 0.9f,
        animationSpec = SpringSpecs.Smooth
    ) + fadeIn() with
            scaleOut(
                targetScale = 1.1f,
                animationSpec = SpringSpecs.Smooth
            ) + fadeOut()
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