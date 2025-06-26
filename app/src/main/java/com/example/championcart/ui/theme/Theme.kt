package com.example.championcart.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import java.util.*

/**
 * Champion Cart - Main Theme Orchestration
 * Electric Harmony design system with intelligent theming
 * Integrates all theme components for cohesive user experience
 */

/**
 * Theme preference types
 */
enum class ThemePreference(val displayName: String) {
    System("Follow System"),
    Light("Light"),
    Dark("Dark"),
    Auto("Auto (Time-based)")
}

/**
 * Theme configuration data class
 */
data class ThemeConfig(
    val darkTheme: Boolean = false,
    val dynamicColor: Boolean = true,
    val highContrast: Boolean = false,
    val timeBasedColors: Boolean = false,
    val reduceMotion: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val glassEffectsEnabled: Boolean = true,
    val performanceMode: Boolean = false
) {
    fun shouldUseDarkTheme(): Boolean = darkTheme
}

/**
 * Composition locals for theme configuration
 */
val LocalThemeConfig = staticCompositionLocalOf { ThemeConfig() }
val LocalReduceMotion = staticCompositionLocalOf { false }
val LocalHapticsEnabled = staticCompositionLocalOf { true }
val LocalGlassEffectsEnabled = staticCompositionLocalOf { true }
val LocalResponsiveConfig = staticCompositionLocalOf { ResponsiveConfig() }
val LocalTimeOfDay = staticCompositionLocalOf { TimeOfDay.AFTERNOON }

/**
 * Time of day enumeration for time-based theming
 */
enum class TimeOfDay {
    MORNING,    // 6am-12pm
    AFTERNOON,  // 12pm-6pm
    EVENING,    // 6pm-12am
    NIGHT       // 12am-6am
}

/**
 * Responsive configuration
 */
data class ResponsiveConfig(
    val isCompact: Boolean = true,
    val isMedium: Boolean = false,
    val isExpanded: Boolean = false
)

/**
 * Main Champion Cart Theme Composable
 */
@Composable
fun ChampionCartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    highContrast: Boolean = false,
    timeBasedTheme: Boolean = false,
    reduceMotion: Boolean = false,
    hapticsEnabled: Boolean = true,
    glassEffectsEnabled: Boolean = true,
    performanceMode: Boolean = false,
    content: @Composable () -> Unit
) {
    // Calculate current time of day
    val timeOfDay = remember { getCurrentTimeOfDay() }

    // Time-based color determination
    val timeBasedColors = if (timeBasedTheme) {
        getTimeBasedColors(timeOfDay)
    } else null

    // Theme configuration
    val themeConfig = ThemeConfig(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        highContrast = highContrast,
        timeBasedColors = timeBasedTheme,
        reduceMotion = reduceMotion,
        hapticsEnabled = hapticsEnabled,
        glassEffectsEnabled = glassEffectsEnabled,
        performanceMode = performanceMode
    )

    // Color schemes
    val baseColorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Apply time-based color overrides
    val colorScheme = if (timeBasedColors != null) {
        baseColorScheme.copy(
            primary = timeBasedColors.primary,
            secondary = timeBasedColors.secondary,
            tertiary = timeBasedColors.tertiary
        )
    } else {
        baseColorScheme
    }

    // Extended colors
    val extendedColors = when {
        darkTheme -> DarkExtendedColors
        else -> LightExtendedColors
    }

    // Animated color transitions
    val animatedColorScheme = if (!reduceMotion) {
        createAnimatedColorScheme(colorScheme)
    } else {
        colorScheme
    }

    // Glass theme configuration
    val glassConfig = GlassThemeConfig(
        defaultIntensity = if (performanceMode) GlassIntensity.Light else GlassIntensity.Medium,
        blurEnabled = glassEffectsEnabled && !performanceMode,
        borderEnabled = glassEffectsEnabled,
        shadowEnabled = !performanceMode
    )

    // Responsive configuration
    val responsiveConfig = rememberResponsiveConfig()

    // Component tokens based on screen size
    val componentTokens = when {
        responsiveConfig.isExpanded -> ExpandedComponentTokens
        responsiveConfig.isMedium -> DefaultComponentTokens
        else -> CompactComponentTokens
    }

    // Apply window decoration
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Provide theme values
    CompositionLocalProvider(
        LocalThemeConfig provides themeConfig,
        LocalReduceMotion provides reduceMotion,
        LocalHapticsEnabled provides hapticsEnabled,
        LocalGlassEffectsEnabled provides glassEffectsEnabled,
        LocalExtendedColors provides extendedColors,
        LocalGlassThemeConfig provides glassConfig,
        LocalResponsiveConfig provides responsiveConfig,
        LocalComponentTokens provides componentTokens,
        LocalTimeOfDay provides timeOfDay
    ) {
        MaterialTheme(
            colorScheme = animatedColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * Create animated color scheme for smooth transitions
 */
@Composable
private fun createAnimatedColorScheme(targetScheme: ColorScheme): ColorScheme {
    val animationSpec = spring<Color>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    return ColorScheme(
        primary = animateColorAsState(targetScheme.primary, animationSpec).value,
        onPrimary = animateColorAsState(targetScheme.onPrimary, animationSpec).value,
        primaryContainer = animateColorAsState(targetScheme.primaryContainer, animationSpec).value,
        onPrimaryContainer = animateColorAsState(targetScheme.onPrimaryContainer, animationSpec).value,
        inversePrimary = animateColorAsState(targetScheme.inversePrimary, animationSpec).value,
        secondary = animateColorAsState(targetScheme.secondary, animationSpec).value,
        onSecondary = animateColorAsState(targetScheme.onSecondary, animationSpec).value,
        secondaryContainer = animateColorAsState(targetScheme.secondaryContainer, animationSpec).value,
        onSecondaryContainer = animateColorAsState(targetScheme.onSecondaryContainer, animationSpec).value,
        tertiary = animateColorAsState(targetScheme.tertiary, animationSpec).value,
        onTertiary = animateColorAsState(targetScheme.onTertiary, animationSpec).value,
        tertiaryContainer = animateColorAsState(targetScheme.tertiaryContainer, animationSpec).value,
        onTertiaryContainer = animateColorAsState(targetScheme.onTertiaryContainer, animationSpec).value,
        background = animateColorAsState(targetScheme.background, animationSpec).value,
        onBackground = animateColorAsState(targetScheme.onBackground, animationSpec).value,
        surface = animateColorAsState(targetScheme.surface, animationSpec).value,
        onSurface = animateColorAsState(targetScheme.onSurface, animationSpec).value,
        surfaceVariant = animateColorAsState(targetScheme.surfaceVariant, animationSpec).value,
        onSurfaceVariant = animateColorAsState(targetScheme.onSurfaceVariant, animationSpec).value,
        surfaceTint = animateColorAsState(targetScheme.surfaceTint, animationSpec).value,
        inverseSurface = animateColorAsState(targetScheme.inverseSurface, animationSpec).value,
        inverseOnSurface = animateColorAsState(targetScheme.inverseOnSurface, animationSpec).value,
        error = animateColorAsState(targetScheme.error, animationSpec).value,
        onError = animateColorAsState(targetScheme.onError, animationSpec).value,
        errorContainer = animateColorAsState(targetScheme.errorContainer, animationSpec).value,
        onErrorContainer = animateColorAsState(targetScheme.onErrorContainer, animationSpec).value,
        outline = animateColorAsState(targetScheme.outline, animationSpec).value,
        outlineVariant = animateColorAsState(targetScheme.outlineVariant, animationSpec).value,
        scrim = animateColorAsState(targetScheme.scrim, animationSpec).value
    )
}

/**
 * Get current time of day
 */
private fun getCurrentTimeOfDay(): TimeOfDay {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 6..11 -> TimeOfDay.MORNING
        in 12..17 -> TimeOfDay.AFTERNOON
        in 18..23 -> TimeOfDay.EVENING
        else -> TimeOfDay.NIGHT
    }
}

/**
 * Get time-based color overrides
 */
private fun getTimeBasedColors(timeOfDay: TimeOfDay): TimeBasedColorOverrides? {
    return when (timeOfDay) {
        TimeOfDay.MORNING -> TimeBasedColorOverrides(
            primary = TimeBasedColors.MorningPrimary,
            secondary = TimeBasedColors.MorningSecondary,
            tertiary = TimeBasedColors.MorningTertiary
        )
        TimeOfDay.AFTERNOON -> TimeBasedColorOverrides(
            primary = TimeBasedColors.AfternoonPrimary,
            secondary = TimeBasedColors.AfternoonSecondary,
            tertiary = TimeBasedColors.AfternoonTertiary
        )
        TimeOfDay.EVENING -> TimeBasedColorOverrides(
            primary = TimeBasedColors.EveningPrimary,
            secondary = TimeBasedColors.EveningSecondary,
            tertiary = TimeBasedColors.EveningTertiary
        )
        TimeOfDay.NIGHT -> TimeBasedColorOverrides(
            primary = TimeBasedColors.NightPrimary,
            secondary = TimeBasedColors.NightSecondary,
            tertiary = TimeBasedColors.NightTertiary
        )
    }
}

/**
 * Time-based color overrides
 */
private data class TimeBasedColorOverrides(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color
)

/**
 * Remember responsive configuration based on window size
 */
@Composable
private fun rememberResponsiveConfig(): ResponsiveConfig {
    // In a real implementation, this would check actual window size
    // For now, returning compact configuration
    return ResponsiveConfig(
        isCompact = true,
        isMedium = false,
        isExpanded = false
    )
}

/**
 * Glass theme configuration
 */
data class GlassThemeConfig(
    val defaultIntensity: GlassIntensity = GlassIntensity.Medium,
    val blurEnabled: Boolean = true,
    val borderEnabled: Boolean = true,
    val shadowEnabled: Boolean = true
)

val LocalGlassThemeConfig = staticCompositionLocalOf { GlassThemeConfig() }

/**
 * Glass intensity levels
 */
enum class GlassIntensity {
    Light,
    Medium,
    Heavy,
    Ultra
}

/**
 * Theme helper functions
 */
object ChampionCartTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val extendedColors: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val spacing: SpacingTokens
        @Composable
        @ReadOnlyComposable
        get() = SpacingTokens

    val sizing: SizingTokens
        @Composable
        @ReadOnlyComposable
        get() = SizingTokens

    val componentTokens: ComponentTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalComponentTokens.current

    val isReduceMotion: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalReduceMotion.current

    val isHapticsEnabled: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalHapticsEnabled.current

    val isGlassEnabled: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalGlassEffectsEnabled.current
}