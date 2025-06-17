package com.example.championcart.ui.theme

import android.content.Context
import android.os.BatteryManager
import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Champion Cart - Motion Theme System
 * Intelligent motion management with accessibility and performance optimization
 * Electric Harmony animations that adapt to user preferences and device capabilities
 */

/**
 * Motion preference levels
 */
enum class MotionPreference(val displayName: String) {
    Full("Full animations"),           // All animations enabled
    Reduced("Reduced motion"),         // Essential animations only
    Minimal("Minimal motion"),         // Critical feedback only
    None("No animations")             // Static interface
}

/**
 * Motion complexity levels
 */
enum class MotionComplexity {
    Simple,     // Basic fade/slide animations
    Standard,   // Standard spring animations
    Complex,    // Multi-step animations with easing
    Elaborate   // Complex choreographed sequences
}

/**
 * Performance mode for battery optimization
 */
enum class PerformanceMode {
    HighPerformance,    // Full animations, all effects
    Balanced,           // Standard animations, some effects
    PowerSaver,         // Reduced animations, minimal effects
    UltraPowerSaver     // Essential animations only
}

/**
 * Motion configuration data class
 */
@Immutable
data class MotionConfig(
    val motionPreference: MotionPreference,
    val performanceMode: PerformanceMode,
    val batteryLevel: Float,
    val isLowPowerMode: Boolean,
    val reduceMotion: Boolean,
    val crossFadeEnabled: Boolean,
    val parallaxEnabled: Boolean,
    val particleEffectsEnabled: Boolean,
    val hapticFeedbackEnabled: Boolean,
    val autoReduceOnLowBattery: Boolean,
    val respectSystemSettings: Boolean
)

/**
 * Default motion configuration
 */
val DefaultMotionConfig = MotionConfig(
    motionPreference = MotionPreference.Full,
    performanceMode = PerformanceMode.Balanced,
    batteryLevel = 1.0f,
    isLowPowerMode = false,
    reduceMotion = false,
    crossFadeEnabled = true,
    parallaxEnabled = true,
    particleEffectsEnabled = true,
    hapticFeedbackEnabled = true,
    autoReduceOnLowBattery = true,
    respectSystemSettings = true
)

/**
 * Motion configuration manager
 */
class MotionConfigManager(private val context: Context) {
    private val _motionConfig = MutableStateFlow(DefaultMotionConfig)
    val motionConfig: StateFlow<MotionConfig> = _motionConfig.asStateFlow()

    init {
        updateFromSystemSettings()
        updateBatteryStatus()
    }

    /**
     * Update configuration from system accessibility settings
     */
    private fun updateFromSystemSettings() {
        val currentConfig = _motionConfig.value

        if (currentConfig.respectSystemSettings) {
            val systemReduceMotion = try {
                Settings.Global.getFloat(
                    context.contentResolver,
                    Settings.Global.ANIMATOR_DURATION_SCALE
                ) == 0.0f
            } catch (e: Settings.SettingNotFoundException) {
                false
            }

            val systemTransitionScale = try {
                Settings.Global.getFloat(
                    context.contentResolver,
                    Settings.Global.TRANSITION_ANIMATION_SCALE
                )
            } catch (e: Settings.SettingNotFoundException) {
                1.0f
            }

            val motionPreference = when {
                systemReduceMotion -> MotionPreference.None
                systemTransitionScale < 0.5f -> MotionPreference.Minimal
                systemTransitionScale < 1.0f -> MotionPreference.Reduced
                else -> MotionPreference.Full
            }

            _motionConfig.value = currentConfig.copy(
                motionPreference = motionPreference,
                reduceMotion = systemReduceMotion
            )
        }
    }

    /**
     * Update battery status and performance mode
     */
    private fun updateBatteryStatus() {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val currentConfig = _motionConfig.value

        if (batteryManager != null && currentConfig.autoReduceOnLowBattery) {
            val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) / 100f
            val isLowPowerMode = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS) ==
                    BatteryManager.BATTERY_STATUS_UNKNOWN

            val performanceMode = when {
                batteryLevel < 0.15f || isLowPowerMode -> PerformanceMode.UltraPowerSaver
                batteryLevel < 0.30f -> PerformanceMode.PowerSaver
                batteryLevel < 0.60f -> PerformanceMode.Balanced
                else -> PerformanceMode.HighPerformance
            }

            _motionConfig.value = currentConfig.copy(
                batteryLevel = batteryLevel,
                isLowPowerMode = isLowPowerMode,
                performanceMode = performanceMode
            )
        }
    }

    /**
     * Update motion preference manually
     */
    fun updateMotionPreference(preference: MotionPreference) {
        _motionConfig.value = _motionConfig.value.copy(
            motionPreference = preference,
            respectSystemSettings = false
        )
    }

    /**
     * Update performance mode manually
     */
    fun updatePerformanceMode(mode: PerformanceMode) {
        _motionConfig.value = _motionConfig.value.copy(performanceMode = mode)
    }

    /**
     * Toggle specific motion features
     */
    fun toggleCrossFade(enabled: Boolean) {
        _motionConfig.value = _motionConfig.value.copy(crossFadeEnabled = enabled)
    }

    fun toggleParallax(enabled: Boolean) {
        _motionConfig.value = _motionConfig.value.copy(parallaxEnabled = enabled)
    }

    fun toggleParticleEffects(enabled: Boolean) {
        _motionConfig.value = _motionConfig.value.copy(particleEffectsEnabled = enabled)
    }

    fun toggleHapticFeedback(enabled: Boolean) {
        _motionConfig.value = _motionConfig.value.copy(hapticFeedbackEnabled = enabled)
    }
}

/**
 * Adaptive animation specifications based on motion preferences
 */
object AdaptiveAnimationSpecs {
    @Composable
    fun <T> standard(): AnimationSpec<T> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> tween(
                durationMillis = DurationSpecs.Fast,
                easing = LinearEasing
            )
            MotionPreference.Reduced -> tween(
                durationMillis = DurationSpecs.Standard,
                easing = DurationSpecs.StandardEasing
            )
            MotionPreference.Full -> SpringSpecs.Smooth
        }
    }

    @Composable
    fun <T> gentle(): AnimationSpec<T> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> tween(DurationSpecs.VeryFast)
            MotionPreference.Reduced -> tween(DurationSpecs.Fast)
            MotionPreference.Full -> SpringSpecs.Gentle
        }
    }

    @Composable
    fun <T> bouncy(): AnimationSpec<T> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> tween(DurationSpecs.Fast)
            MotionPreference.Reduced -> tween(DurationSpecs.Standard)
            MotionPreference.Full -> SpringSpecs.Bouncy
        }
    }

    @Composable
    fun <T> playful(): AnimationSpec<T> {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> snap()
            MotionPreference.Minimal -> snap()
            MotionPreference.Reduced -> tween(DurationSpecs.Fast)
            MotionPreference.Full -> SpringSpecs.Playful
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
                PerformanceMode.Balanced -> SpringSpecs.Smooth
                PerformanceMode.HighPerformance -> SpringSpecs.Bouncy
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
                config.performanceMode in listOf(PerformanceMode.HighPerformance, PerformanceMode.Balanced)
    }

    @Composable
    fun shouldUseFloatingEffects(): Boolean {
        val config = LocalMotionConfig.current
        return config.motionPreference == MotionPreference.Full &&
                config.performanceMode == PerformanceMode.HighPerformance
    }
}

/**
 * Motion-aware component behaviors
 */
object AdaptiveMotionBehaviors {
    @Composable
    fun buttonPress(): MotionBehavior {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> MotionBehavior.Static
            MotionPreference.Minimal -> MotionBehavior.SimpleScale
            MotionPreference.Reduced -> MotionBehavior.StandardScale
            MotionPreference.Full -> MotionBehavior.BouncyScale
        }
    }

    @Composable
    fun cardHover(): MotionBehavior {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> MotionBehavior.Static
            MotionPreference.Minimal -> MotionBehavior.Static
            MotionPreference.Reduced -> MotionBehavior.SimpleElevation
            MotionPreference.Full -> MotionBehavior.FloatingElevation
        }
    }

    @Composable
    fun listItemEntry(): MotionBehavior {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> MotionBehavior.Static
            MotionPreference.Minimal -> MotionBehavior.SimpleFade
            MotionPreference.Reduced -> MotionBehavior.SlideIn
            MotionPreference.Full -> if (MotionFeatures.shouldUseStaggeredAnimations()) {
                MotionBehavior.StaggeredSlideIn
            } else {
                MotionBehavior.SlideIn
            }
        }
    }

    @Composable
    fun pageTransition(): MotionBehavior {
        val config = LocalMotionConfig.current
        return when (config.motionPreference) {
            MotionPreference.None -> MotionBehavior.Static
            MotionPreference.Minimal -> MotionBehavior.SimpleFade
            MotionPreference.Reduced -> MotionBehavior.StandardSlide
            MotionPreference.Full -> MotionBehavior.SpringSlide
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

    @Composable
    fun <T> accessibleAnimation(
        targetValue: T,
        animationSpec: AnimationSpec<T>,
        label: String = ""
    ): State<T> {
        val config = LocalMotionConfig.current
        val finalSpec = if (config.reduceMotion) snap() else animationSpec

        return animateValueAsState(
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
        return config.motionPreference in listOf(MotionPreference.Full, MotionPreference.Reduced)
    }
}

/**
 * Battery-aware motion optimization
 */
object BatteryOptimizedMotion {
    @Composable
    fun shouldLimitAnimations(): Boolean {
        val config = LocalMotionConfig.current
        return config.batteryLevel < 0.20f || config.isLowPowerMode
    }

    @Composable
    fun shouldDisableParticles(): Boolean {
        val config = LocalMotionConfig.current
        return config.batteryLevel < 0.30f ||
                config.performanceMode in listOf(PerformanceMode.PowerSaver, PerformanceMode.UltraPowerSaver)
    }

    @Composable
    fun shouldReduceBlur(): Boolean {
        val config = LocalMotionConfig.current
        return config.batteryLevel < 0.40f || config.performanceMode != PerformanceMode.HighPerformance
    }

    @Composable
    fun getOptimalFrameRate(): Int {
        val config = LocalMotionConfig.current
        return when (config.performanceMode) {
            PerformanceMode.UltraPowerSaver -> 30
            PerformanceMode.PowerSaver -> 45
            PerformanceMode.Balanced -> 60
            PerformanceMode.HighPerformance -> 60
        }
    }
}

/**
 * Composition locals for motion configuration
 */
val LocalMotionConfig = staticCompositionLocalOf { DefaultMotionConfig }
val LocalMotionConfigManager = staticCompositionLocalOf<MotionConfigManager?> { null }

/**
 * Motion theme provider
 */
@Composable
fun ProvideMotionTheme(
    motionConfig: MotionConfig = DefaultMotionConfig,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current

    val configManager = remember(context) {
        if (isInPreview) null else MotionConfigManager(context)
    }

    val finalConfig = if (configManager != null) {
        configManager.motionConfig.collectAsState().value
    } else {
        motionConfig
    }

    CompositionLocalProvider(
        LocalMotionConfig provides finalConfig,
        LocalMotionConfigManager provides configManager
    ) {
        content()
    }
}