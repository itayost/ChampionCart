package com.example.championcart.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.repeatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.launch

/**
 * Champion Cart - Modern Animation System
 * Smooth, delightful animations with motion preferences support
 * Following 2025 design trends with springy, alive animations
 */

/**
 * Spring animation specifications for organic, bouncy feel
 * Based on Google's Material Design 3 Expressive spring animations
 */
object SpringSpecs {
    // Damping ratios - Control bounce behavior
    const val dampingRatioNoBounce = Spring.DampingRatioNoBouncy        // 1.0f - No bounce
    const val dampingRatioLowBounce = Spring.DampingRatioLowBouncy      // 0.75f - Subtle bounce
    const val dampingRatioMediumBounce = Spring.DampingRatioMediumBouncy // 0.5f - Noticeable bounce
    const val dampingRatioHighBounce = 0.3f                             // High bounce for playful elements

    // Stiffness values - Control animation speed
    const val stiffnessVeryLow = Spring.StiffnessVeryLow      // 50f - Very slow, gentle
    const val stiffnessLow = Spring.StiffnessLow              // 200f - Slow, smooth
    const val stiffnessMedium = Spring.StiffnessMedium        // 400f - Balanced
    const val stiffnessMediumHigh = 600f                      // Responsive feel
    const val stiffnessHigh = Spring.StiffnessHigh           // 800f - Quick response
    const val stiffnessVeryHigh = 1500f                      // Snappy, immediate
}

/**
 * Animation duration constants in milliseconds
 * Optimized for modern, responsive feel while maintaining smoothness
 */
object AnimationDurations {
    // Basic durations following 2025 responsive design patterns
    const val fast = 150           // Quick micro-interactions
    const val medium = 300         // Standard UI transitions
    const val slow = 500           // Prominent state changes
    const val extraSlow = 800      // Hero animations

    // Specific interaction durations
    const val fadeIn = 200         // Content appearing
    const val fadeOut = 150        // Content disappearing
    const val slideIn = 400        // Screen transitions entering
    const val slideOut = 250       // Screen transitions exiting

    // Special effect durations
    const val rotation = 600       // Loading spinners, icon rotations
    const val priceCount = 800     // Number counter animations
    const val colorTransition = 300 // Theme/color changes
    const val ripple = 400         // Touch feedback ripples

    // Complex animation durations
    const val stagger = 50         // Delay between list items
    const val bounce = 1200        // Attention-grabbing bounce
    const val shake = 400          // Error indication shake
    const val pulse = 2000         // Notification pulse cycle
}

/**
 * Standard animation specs for consistency
 */
object AnimationSpecs {
    // Spring animations - For interactive elements
    val springDefault = spring<Float>(
        dampingRatio = SpringSpecs.dampingRatioLowBounce,
        stiffness = SpringSpecs.stiffnessMedium
    )

    val springBouncy = spring<Float>(
        dampingRatio = SpringSpecs.dampingRatioMediumBounce,
        stiffness = SpringSpecs.stiffnessMediumHigh
    )

    val springGentle = spring<Float>(
        dampingRatio = SpringSpecs.dampingRatioNoBounce,
        stiffness = SpringSpecs.stiffnessLow
    )

    val springSnappy = spring<Float>(
        dampingRatio = SpringSpecs.dampingRatioLowBounce,
        stiffness = SpringSpecs.stiffnessHigh
    )

    // Tween animations - For precise timing
    val tweenFast = tween<Float>(
        durationMillis = AnimationDurations.fast,
        easing = FastOutSlowInEasing
    )

    val tweenMedium = tween<Float>(
        durationMillis = AnimationDurations.medium,
        easing = FastOutSlowInEasing
    )

    val tweenSlow = tween<Float>(
        durationMillis = AnimationDurations.slow,
        easing = FastOutSlowInEasing
    )

    // Fade animations
    val fadeIn = tween<Float>(
        durationMillis = AnimationDurations.fadeIn,
        easing = LinearOutSlowInEasing
    )

    val fadeOut = tween<Float>(
        durationMillis = AnimationDurations.fadeOut,
        easing = FastOutLinearInEasing
    )
}

/**
 * Enter/Exit transitions for navigation
 */
object TransitionSpecs {
    // Slide transitions
    val slideInFromRight = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(AnimationDurations.slideIn, easing = FastOutSlowInEasing)
    )

    val slideOutToLeft = slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(AnimationDurations.slideOut, easing = FastOutSlowInEasing)
    )

    val slideInFromBottom = slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(AnimationDurations.slideIn, easing = FastOutSlowInEasing)
    )

    val slideOutToTop = slideOutVertically(
        targetOffsetY = { -it },
        animationSpec = tween(AnimationDurations.slideOut, easing = FastOutSlowInEasing)
    )

    // Scale transitions
    val scaleIn = scaleIn(
        initialScale = 0.8f,
        animationSpec = AnimationSpecs.springBouncy
    )

    val scaleOut = scaleOut(
        targetScale = 0.8f,
        animationSpec = AnimationSpecs.tweenFast
    )

    // Combined transitions
    val fadeInScale = fadeIn(AnimationSpecs.fadeIn) + scaleIn
    val fadeOutScale = fadeOut(AnimationSpecs.fadeOut) + scaleOut
}

/**
 * Animated visibility with motion preferences
 */
@Composable
fun AnimatedVisibilityWithMotion(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = TransitionSpecs.fadeInScale,
    exit: ExitTransition = TransitionSpecs.fadeOutScale,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val reduceMotion = LocalReduceMotion.current

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (reduceMotion) fadeIn() else enter,
        exit = if (reduceMotion) fadeOut() else exit,
        content = content
    )
}

/**
 * Press animation for buttons and interactive elements
 */
fun Modifier.pressAnimation(
    enabled: Boolean = true,
    pressScale: Float = 0.96f
) = composed {
    val reduceMotion = LocalReduceMotion.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !reduceMotion) pressScale else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.dampingRatioMediumBounce,
            stiffness = SpringSpecs.stiffnessHigh
        ),
        label = "press_scale"
    )

    this
        .scale(scale)
        .pointerInput(enabled) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    when (event.type) {
                        PointerEventType.Press -> isPressed = true
                        PointerEventType.Release, PointerEventType.Exit -> isPressed = false
                    }
                }
            }
        }
}

/**
 * Hover animation for desktop/large screens
 */
fun Modifier.hoverAnimation(
    hoverScale: Float = 1.02f,
    hoverElevation: Dp = 12.dp
) = composed {
    var isHovered by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isHovered) hoverScale else 1f,
        animationSpec = AnimationSpecs.springGentle,
        label = "hover_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isHovered) hoverElevation else Dimensions.elevationMedium,
        animationSpec = tween(AnimationDurations.fast),
        label = "hover_elevation"
    )

    this
        .scale(scale)
        .shadow(elevation, ComponentShapes.Card)
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    when (event.type) {
                        PointerEventType.Enter -> isHovered = true
                        PointerEventType.Exit -> isHovered = false
                    }
                }
            }
        }
}

/**
 * Bounce animation for attention
 */
fun Modifier.bounceAnimation(
    targetScale: Float = 1.1f,
    duration: Int = AnimationDurations.bounce
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = targetScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_scale"
    )

    this.scale(scale)
}

/**
 * Shake animation for errors
 */
fun Modifier.shakeAnimation(
    enabled: Boolean,
    shakeOffset: Dp = 10.dp
) = composed {
    val density = LocalDensity.current
    val shakeOffsetPx = with(density) { shakeOffset.roundToPx() }

    val offsetX by animateIntAsState(
        targetValue = if (enabled) shakeOffsetPx else 0,
        animationSpec = if (enabled) {
            repeatable(
                iterations = 4,
                animation = keyframes {
                    durationMillis = AnimationDurations.shake
                    0 at 0
                    -shakeOffsetPx at 20
                    shakeOffsetPx at 40
                    -shakeOffsetPx at 60
                    shakeOffsetPx at 80
                    0 at 100
                }
            )
        } else {
            spring(stiffness = SpringSpecs.stiffnessMedium)
        },
        label = "shake"
    )

    this.offset { IntOffset(offsetX, 0) }
}

/**
 * Parallax scroll effect
 */
fun Modifier.parallaxEffect(
    scrollOffset: Float,
    parallaxFactor: Float = 0.5f
) = composed {
    this.graphicsLayer {
        translationY = scrollOffset * parallaxFactor
    }
}

/**
 * Stagger animation for lists
 */
@Composable
fun <T> StaggeredAnimatedList(
    items: List<T>,
    modifier: Modifier = Modifier,
    delayBetweenItems: Int = AnimationDurations.stagger,
    content: @Composable (index: Int, item: T) -> Unit
) {
    items.forEachIndexed { index, item ->
        val delay = index * delayBetweenItems
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(item) {
            kotlinx.coroutines.delay(delay.toLong())
            visible = true
        }

        AnimatedVisibilityWithMotion(
            visible = visible,
            enter = TransitionSpecs.slideInFromRight + fadeIn(),
            modifier = modifier
        ) {
            content(index, item)
        }
    }
}

/**
 * Number counter animation
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    duration: Int = AnimationDurations.priceCount
) {
    var oldValue by remember { mutableStateOf(targetValue) }
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "counter"
    )

    SideEffect {
        oldValue = targetValue
    }

    androidx.compose.material3.Text(
        text = animatedValue.toString(),
        modifier = modifier,
        style = AppTextStyles.priceLarge
    )
}

/**
 * Animated color transition
 */
@Composable
fun animateColorTransition(
    targetColor: Color,
    animationSpec: AnimationSpec<Color> = tween(AnimationDurations.colorTransition)
): Color {
    return animateColorAsState(
        targetValue = targetColor,
        animationSpec = animationSpec,
        label = "color_transition"
    ).value
}

/**
 * Swipe to dismiss animation
 */
fun Modifier.swipeToDismiss(
    onDismiss: () -> Unit,
    threshold: Dp = Dimensions.swipeThreshold
) = composed {
    val offsetX = remember { Animatable(0f) }
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    this
        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    if (kotlin.math.abs(offsetX.value) > threshold.toPx()) {
                        coroutineScope.launch {
                            offsetX.animateTo(
                                targetValue = if (offsetX.value > 0) size.width.toFloat() else -size.width.toFloat(),
                                animationSpec = AnimationSpecs.tweenFast
                            )
                            onDismiss()
                        }
                    } else {
                        coroutineScope.launch {
                            offsetX.animateTo(0f, AnimationSpecs.springDefault)
                        }
                    }
                }
            ) { _, dragAmount ->
                coroutineScope.launch {
                    offsetX.snapTo(offsetX.value + dragAmount)
                }
            }
        }
}

/**
 * Pulse animation for notifications/badges
 */
fun Modifier.pulseAnimation(
    enabled: Boolean = true,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f
) = composed {
    if (!enabled) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(AnimationDurations.pulse, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    this.scale(scale)
}

/**
 * Rotation animation
 */
fun Modifier.rotationAnimation(
    targetRotation: Float = 360f,
    duration: Int = AnimationDurations.rotation,
    continuous: Boolean = false
) = composed {
    if (continuous) {
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = targetRotation,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "continuous_rotation"
        )
        this.graphicsLayer { rotationZ = rotation }
    } else {
        val rotation by animateFloatAsState(
            targetValue = targetRotation,
            animationSpec = tween(duration, easing = FastOutSlowInEasing),
            label = "rotation"
        )
        this.graphicsLayer { rotationZ = rotation }
    }
}