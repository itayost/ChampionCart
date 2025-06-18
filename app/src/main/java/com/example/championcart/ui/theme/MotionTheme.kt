package com.example.championcart.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Champion Cart - Motion Theme System
 * Adaptive animation framework with accessibility support
 * Follows Material Design 3 expressive motion principles
 */

/**
 * Motion preferences for accessibility and user preference
 */
enum class MotionPreference {
    None,       // No animations (accessibility)
    Minimal,    // Reduced motion
    Reduced,    // Standard but simplified
    Full        // Full expressive animations
}

/**
 * Performance modes that affect animation complexity
 */
enum class PerformanceMode {
    UltraPowerSaver,    // Minimal animations
    PowerSaver,         // Reduced complexity
    Balanced,           // Default mode
    HighPerformance     // All effects enabled
}

/**
 * Motion configuration data class
 */
data class MotionConfig(
    val motionPreference: MotionPreference = MotionPreference.Full,
    val performanceMode: PerformanceMode = PerformanceMode.Balanced,
    val reduceMotion: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val parallaxEnabled: Boolean = true,
    val particleEffectsEnabled: Boolean = true,
    val glowEffectsEnabled: Boolean = true,
    val crossFadeEnabled: Boolean = true,
    val springAnimationsEnabled: Boolean = true
) {
    companion object {
        val Default = MotionConfig()
        val Reduced = MotionConfig(
            motionPreference = MotionPreference.Reduced,
            reduceMotion = true,
            parallaxEnabled = false,
            particleEffectsEnabled = false
        )
        val Minimal = MotionConfig(
            motionPreference = MotionPreference.Minimal,
            reduceMotion = true,
            parallaxEnabled = false,
            particleEffectsEnabled = false,
            glowEffectsEnabled = false,
            springAnimationsEnabled = false
        )
    }
}

/**
 * CompositionLocal for motion configuration
 */
val LocalMotionConfig = compositionLocalOf { MotionConfig.Default }

/**
 * Motion configuration provider
 */
@Composable
fun ProvideMotionConfig(
    config: MotionConfig,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMotionConfig provides config,
        content = content
    )
}
/**
 * Adaptive animation specs based on user preferences
 */
object AdaptiveAnimationSpecs {
    // Float animations (most common)
    @Composable
    fun standardFloat(): AnimationSpec<Float> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> tween(
                durationMillis = DurationSpecs.Fast,
                easing = LinearEasing
            )
            MotionPreference.Reduced -> tween(
                durationMillis = DurationSpecs.Standard,
                easing = FastOutSlowInEasing
            )
            MotionPreference.Full -> spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        }
    }

    // Dp animations
    @Composable
    fun standardDp(): AnimationSpec<Dp> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> tween(
                durationMillis = DurationSpecs.Fast,
                easing = LinearEasing
            )
            MotionPreference.Reduced -> tween(
                durationMillis = DurationSpecs.Standard,
                easing = FastOutSlowInEasing
            )
            MotionPreference.Full -> spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        }
    }

    // Color animations
    @Composable
    fun standardColor(): AnimationSpec<Color> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> tween(
                durationMillis = DurationSpecs.Fast,
                easing = LinearEasing
            )
            MotionPreference.Reduced -> tween(
                durationMillis = DurationSpecs.Standard,
                easing = FastOutSlowInEasing
            )
            MotionPreference.Full -> tween(
                durationMillis = DurationSpecs.Standard,
                easing = FastOutSlowInEasing
            )
        }
    }

    // Generic specs for any type
    @Composable
    fun <T> standard(): AnimationSpec<T> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> tween(DurationSpecs.Fast, easing = LinearEasing)
            MotionPreference.Reduced -> tween(DurationSpecs.Standard, easing = FastOutSlowInEasing)
            MotionPreference.Full -> tween(DurationSpecs.Standard, easing = FastOutSlowInEasing)
        }
    }

    @Composable
    fun <T> gentle(): AnimationSpec<T> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> tween(DurationSpecs.VeryFast)
            MotionPreference.Reduced -> tween(DurationSpecs.Fast)
            MotionPreference.Full -> tween(DurationSpecs.Standard, easing = LinearEasing)
        }
    }

    @Composable
    fun <T> bouncy(): AnimationSpec<T> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> tween(DurationSpecs.Fast)
            MotionPreference.Reduced -> tween(DurationSpecs.Standard)
            MotionPreference.Full -> tween(DurationSpecs.Standard, easing = FastOutLinearInEasing)
        }
    }

    @Composable
    fun <T> playful(): AnimationSpec<T> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> snap()
            MotionPreference.Reduced -> tween(DurationSpecs.Fast)
            MotionPreference.Full -> tween(DurationSpecs.Standard, easing = FastOutSlowInEasing)
        }
    }

    @Composable
    fun <T> complex(): AnimationSpec<T> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> snap()
            MotionPreference.Reduced -> tween(DurationSpecs.Standard)
            MotionPreference.Full -> when (config.performanceMode) {
                PerformanceMode.UltraPowerSaver -> tween(DurationSpecs.Fast)
                PerformanceMode.PowerSaver -> tween(DurationSpecs.Standard)
                PerformanceMode.Balanced -> tween(DurationSpecs.SlowComplex, easing = FastOutSlowInEasing)
                PerformanceMode.HighPerformance -> tween(DurationSpecs.Elaborate, easing = FastOutSlowInEasing)
            }
        }
    }
}

/**
 * Motion-aware animation duration calculation
 */
object AdaptiveDurations {
    @Composable
    fun quick(): Int {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> 0
            MotionPreference.Minimal -> DurationSpecs.VeryFast
            MotionPreference.Reduced -> DurationSpecs.Fast
            MotionPreference.Full -> DurationSpecs.Fast
        }
    }

    @Composable
    fun standard(): Int {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> 0
            MotionPreference.Minimal -> DurationSpecs.Fast
            MotionPreference.Reduced -> DurationSpecs.Standard
            MotionPreference.Full -> DurationSpecs.Standard
        }
    }

    @Composable
    fun complex(): Int {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> 0
            MotionPreference.Minimal -> DurationSpecs.Fast
            MotionPreference.Reduced -> DurationSpecs.Standard
            MotionPreference.Full -> when (config.performanceMode) {
                PerformanceMode.UltraPowerSaver -> DurationSpecs.Fast
                PerformanceMode.PowerSaver -> DurationSpecs.Standard
                PerformanceMode.Balanced -> DurationSpecs.SlowComplex
                PerformanceMode.HighPerformance -> DurationSpecs.Elaborate
            }
        }
    }

    @Composable
    fun stagger(): Int {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> 0
            MotionPreference.Minimal -> 0
            MotionPreference.Reduced -> AnimationTokens.StaggerDelay / 2
            MotionPreference.Full -> AnimationTokens.StaggerDelay
        }
    }
}

/**
 * Feature availability based on motion preferences
 */
object MotionFeatures {
    @Composable
    fun shouldAnimateEntry(): Boolean {
        val config = LocalMotionConfig.current
        return config.motionPreference != MotionPreference.None
    }

    @Composable
    fun shouldAnimateExit(): Boolean {
        val config = LocalMotionConfig.current
        return config.motionPreference == MotionPreference.Full
    }

    @Composable
    fun shouldUseCrossFade(): Boolean {
        val config = LocalMotionConfig.current
        return config.crossFadeEnabled && config.motionPreference != MotionPreference.None
    }

    @Composable
    fun shouldUseParallax(): Boolean {
        val config = LocalMotionConfig.current
        return config.parallaxEnabled &&
                config.motionPreference == MotionPreference.Full &&
                config.performanceMode != PerformanceMode.UltraPowerSaver
    }

    @Composable
    fun shouldUseParticleEffects(): Boolean {
        val config = LocalMotionConfig.current
        return config.particleEffectsEnabled &&
                config.motionPreference == MotionPreference.Full &&
                config.performanceMode in listOf(PerformanceMode.HighPerformance, PerformanceMode.Balanced)
    }

    @Composable
    fun shouldUseHapticFeedback(): Boolean {
        val config = LocalMotionConfig.current
        return config.hapticFeedbackEnabled && config.motionPreference != MotionPreference.None
    }

    @Composable
    fun shouldUseSpringAnimations(): Boolean {
        val config = LocalMotionConfig.current
        return config.motionPreference in listOf(MotionPreference.Full, MotionPreference.Reduced)
    }

    @Composable
    fun shouldUseStaggeredAnimations(): Boolean {
        val config = LocalMotionConfig.current
        return config.motionPreference == MotionPreference.Full &&
                config.performanceMode != PerformanceMode.UltraPowerSaver
    }

    @Composable
    fun shouldUseGlowEffects(): Boolean {
        val config = LocalMotionConfig.current
        return config.motionPreference == MotionPreference.Full &&
                config.glowEffectsEnabled &&
                config.performanceMode != PerformanceMode.UltraPowerSaver
    }
}

/**
 * Smart motion behavior selector
 */
object SmartMotion {
    @Composable
    fun getMotionBehavior(complexity: Int = 1): MotionBehavior {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> MotionBehavior.Static
            MotionPreference.Minimal -> if (complexity > 2) MotionBehavior.SimpleFade else MotionBehavior.SimpleScale
            MotionPreference.Reduced -> MotionBehavior.StandardSlide
            MotionPreference.Full -> when (complexity) {
                1 -> MotionBehavior.SpringSlide
                2 -> MotionBehavior.BouncyScale
                else -> MotionBehavior.StaggeredSlideIn
            }
        }
    }

    @Composable
    fun selectTransition(screen: String): MotionBehavior {
        val config = LocalMotionConfig.current
        return when (screen) {
            "splash" -> MotionBehavior.SimpleFade
            "home" -> if (config.motionPreference == MotionPreference.Full)
                MotionBehavior.StaggeredSlideIn else MotionBehavior.StandardSlide
            "search" -> MotionBehavior.SlideIn
            "cart" -> MotionBehavior.StandardScale
            "profile" -> MotionBehavior.SimpleFade
            else -> when (config.motionPreference) {
                MotionPreference.None -> MotionBehavior.Static
                MotionPreference.Minimal -> MotionBehavior.SimpleFade
                MotionPreference.Reduced -> MotionBehavior.StandardSlide
                MotionPreference.Full -> MotionBehavior.SpringSlide
            }
        }
    }
}

/**
 * Motion behavior types
 */
enum class MotionBehavior {
    Static,
    SimpleFade,
    SimpleScale,
    SimpleElevation,
    StandardScale,
    StandardSlide,
    SlideIn,
    BouncyScale,
    FloatingElevation,
    SpringSlide,
    StaggeredSlideIn
}

/**
 * Performance monitoring for animations
 */
class MotionPerformanceMonitor {
    private var frameDropCount = 0
    private var animationCount = 0
    private var lastFrameTime = System.currentTimeMillis()

    fun recordAnimation() {
        animationCount++
    }

    fun recordFrameDrop() {
        frameDropCount++
    }

    fun shouldReduceMotion(): Boolean {
        val frameDropRate = if (animationCount > 0) frameDropCount.toFloat() / animationCount else 0f
        return frameDropRate > 0.1f // More than 10% frame drops
    }

    fun reset() {
        frameDropCount = 0
        animationCount = 0
        lastFrameTime = System.currentTimeMillis()
    }
}

/**
 * Accessibility-aware animation helpers
 */
object AccessibilityMotion {
    @Composable
    fun respectsReducedMotion(
        defaultSpec: AnimationSpec<Float>,
        reducedSpec: AnimationSpec<Float> = snap()
    ): AnimationSpec<Float> {
        val config = LocalMotionConfig.current
        return if (config.reduceMotion) reducedSpec else defaultSpec
    }

    // Float animation
    @Composable
    fun animateFloatAccessible(
        targetValue: Float,
        animationSpec: AnimationSpec<Float> = AdaptiveAnimationSpecs.standardFloat(),
        label: String = ""
    ): State<Float> {
        val config = LocalMotionConfig.current
        val finalSpec = if (config.reduceMotion) snap() else animationSpec

        return animateFloatAsState(
            targetValue = targetValue,
            animationSpec = finalSpec,
            label = label
        )
    }

    // Dp animation
    @Composable
    fun animateDpAccessible(
        targetValue: Dp,
        animationSpec: AnimationSpec<Dp> = AdaptiveAnimationSpecs.standardDp(),
        label: String = ""
    ): State<Dp> {
        val config = LocalMotionConfig.current
        val finalSpec = if (config.reduceMotion) snap() else animationSpec

        return animateDpAsState(
            targetValue = targetValue,
            animationSpec = finalSpec,
            label = label
        )
    }

    // Color animation
    @Composable
    fun animateColorAccessible(
        targetValue: Color,
        animationSpec: AnimationSpec<Color> = AdaptiveAnimationSpecs.standardColor(),
        label: String = ""
    ): State<Color> {
        val config = LocalMotionConfig.current
        val finalSpec = if (config.reduceMotion) snap() else animationSpec

        return animateColorAsState(
            targetValue = targetValue,
            animationSpec = finalSpec,
            label = label
        )
    }

    @Composable
    fun shouldShowLoadingAnimation(): Boolean {
        val config = LocalMotionConfig.current
        return config.motionPreference != MotionPreference.None
    }

    @Composable
    fun shouldAnimateProgress(): Boolean {
        val config = LocalMotionConfig.current
        return config.motionPreference != MotionPreference.None
    }
}