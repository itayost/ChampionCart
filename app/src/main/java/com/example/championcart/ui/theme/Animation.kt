package com.example.championcart.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.IntOffset

/**
 * Champion Cart Animation Specifications
 * Material Design 3 spring physics for Electric Harmony
 */

object ChampionCartAnimations {
    // Spring specifications
    object Springs {
        val Gentle = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        )

        val Smooth = spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )

        val Responsive = spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )

        val Bouncy = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )

        val Snappy = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )

        // Component-specific
        val ButtonPress = spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        )

        val CardInteraction = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )

        val ListItem = spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )

        val BottomSheet = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )

        // Generic spring creators
        fun <T> gentle() = spring<T>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        )

        fun <T> smooth() = spring<T>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )

        fun <T> responsive() = spring<T>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )

        fun <T> bouncy() = spring<T>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )

        fun <T> snappy() = spring<T>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
    }

    // Duration-based animations
    object Durations {
        const val Instant = 50
        const val VeryFast = 100
        const val Fast = 150
        const val Quick = 200
        const val Standard = 300
        const val Medium = 400
        const val Slow = 500
        const val VerySlow = 800
        const val Elaborate = 1000
    }

    // Easing curves
    object Easings {
        val Standard = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        val Decelerate = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
        val Accelerate = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
        val AccelerateDecelerate = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)
        val Elastic = CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f)
    }

    // Enter/Exit transitions
    object Transitions {
        val fadeIn = fadeIn(
            animationSpec = tween(
                durationMillis = Durations.Standard,
                easing = Easings.Decelerate
            )
        )

        val fadeOut = fadeOut(
            animationSpec = tween(
                durationMillis = Durations.Quick,
                easing = Easings.Accelerate
            )
        )

        val scaleIn = scaleIn(
            initialScale = 0.85f,
            animationSpec = Springs.responsive()
        )

        val scaleOut = scaleOut(
            targetScale = 0.85f,
            animationSpec = Springs.smooth()
        )

        val slideInFromBottom = slideInVertically(
            initialOffsetY = { it },
            animationSpec = Springs.responsive()
        )

        val slideOutToBottom = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = Springs.smooth()
        )

        val slideInFromRight = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = Springs.responsive()
        )

        val slideOutToLeft = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = Springs.smooth()
        )

        // Combined transitions
        val enterFromBottom = fadeIn + slideInFromBottom
        val exitToTop = fadeOut + slideOutVertically { -it / 2 }
        val enterWithScale = fadeIn + scaleIn
        val exitWithScale = fadeOut + scaleOut
    }

    // Navigation transitions
    object Navigation {
        val enterTransition = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = Springs.responsive()
        ) + fadeIn(
            animationSpec = tween(Durations.Quick)
        )

        val exitTransition = slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = Springs.smooth()
        ) + fadeOut(
            animationSpec = tween(Durations.VeryFast)
        )

        val popEnterTransition = slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = Springs.responsive()
        ) + fadeIn(
            animationSpec = tween(Durations.Quick)
        )

        val popExitTransition = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = Springs.smooth()
        ) + fadeOut(
            animationSpec = tween(Durations.VeryFast)
        )
    }

    // List animations
    object List {
        const val StaggerDelay = 50
        const val StaggerDelayLong = 100

        fun staggeredFadeIn(index: Int, baseDelay: Int = 0) = fadeIn(
            animationSpec = tween(
                durationMillis = Durations.Standard,
                delayMillis = baseDelay + (index * StaggerDelay),
                easing = Easings.Decelerate
            )
        )

        fun staggeredSlideIn(index: Int, baseDelay: Int = 0) = slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(
                durationMillis = Durations.Medium,
                delayMillis = baseDelay + (index * StaggerDelay),
                easing = Easings.Decelerate
            )
        )

        fun staggeredScaleIn(index: Int, baseDelay: Int = 0) = scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(
                durationMillis = Durations.Standard,
                delayMillis = baseDelay + (index * StaggerDelay),
                easing = Easings.Elastic
            )
        )
    }

    // Infinite animations
    fun shimmerAnimation(
        durationMillis: Int = 1200
    ): InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = durationMillis,
            easing = LinearEasing
        ),
        repeatMode = RepeatMode.Restart
    )

    fun pulseAnimation(
        scale: Float = 1.1f,
        durationMillis: Int = 1000
    ): InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = durationMillis,
            easing = Easings.AccelerateDecelerate
        ),
        repeatMode = RepeatMode.Reverse
    )

    fun rotationAnimation(
        durationMillis: Int = 1500
    ): InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = durationMillis,
            easing = LinearEasing
        ),
        repeatMode = RepeatMode.Restart
    )
}

/**
 * Accessibility-aware animation wrapper
 */
/**
 * Accessibility-aware animation wrapper for IntOffset values
 */
@Composable
fun animateIntOffsetWithAccessibility(
    targetValue: IntOffset,
    animationSpec: AnimationSpec<IntOffset> = ChampionCartAnimations.Springs.responsive(),
    label: String = "animation",
    finishedListener: ((IntOffset) -> Unit)? = null
): State<IntOffset> {
    val config = ChampionCartTheme.config

    return animateIntOffsetAsState(
        targetValue = targetValue,
        animationSpec = if (config.reduceMotion) snap() else animationSpec,
        label = label,
        finishedListener = finishedListener
    )
}