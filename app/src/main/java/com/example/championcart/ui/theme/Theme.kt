package com.example.championcart.ui.theme

import android.app.Activity
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
import dev.chrisbanes.haze.HazeState
import java.util.*

/**
 * Champion Cart Theme
 * Electric Harmony Design System
 *
 * A modern, glassmorphic theme with time-based variations,
 * accessibility features, and Hebrew-first design considerations.
 * Enhanced with Haze blur effects and modern animations.
 */

// Theme Configuration
data class ChampionCartConfig(
    val useDarkTheme: Boolean = false,
    val useTimeBasedTheme: Boolean = true,
    val useHighContrast: Boolean = false,
    val reduceMotion: Boolean = false,
    val enableHaptics: Boolean = true,
    val enableGlassEffects: Boolean = true,
    val performanceMode: Boolean = false,
    val enableBlurEffects: Boolean = true, // New: for Haze effects
    val enableMicroAnimations: Boolean = true // New: for subtle animations
)

// Composition Locals
val LocalChampionCartConfig = staticCompositionLocalOf { ChampionCartConfig() }
val LocalTimeOfDay = staticCompositionLocalOf { TimeOfDay.AFTERNOON }
val LocalSpacing = staticCompositionLocalOf { Spacing }
val LocalElevation = staticCompositionLocalOf { Elevation }
val LocalReduceMotion = staticCompositionLocalOf { false } // New: for accessibility
val LocalResponsiveConfig = staticCompositionLocalOf { ResponsiveConfig() } // New: for responsive design

// Time of Day
enum class TimeOfDay {
    MORNING,    // 6am-12pm
    AFTERNOON,  // 12pm-6pm
    EVENING,    // 6pm-12am
    NIGHT       // 12am-6am
}

// Responsive Configuration
data class ResponsiveConfig(
    val isCompact: Boolean = true,
    val isMedium: Boolean = false,
    val isExpanded: Boolean = false,
    val screenWidthDp: Int = 360
)

/**
 * Main Theme Composable
 */
@Composable
fun ChampionCartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    config: ChampionCartConfig = ChampionCartConfig(
        useDarkTheme = darkTheme
    ),
    content: @Composable () -> Unit
) {
    val timeOfDay = remember { getCurrentTimeOfDay() }

    // Create a global HazeState for consistent blur effects
    val hazeState = remember { HazeState() }

    // Calculate responsive configuration
    val context = LocalContext.current
    val screenWidthDp = context.resources.configuration.screenWidthDp
    val responsiveConfig = remember(screenWidthDp) {
        ResponsiveConfig(
            isCompact = screenWidthDp < 600,
            isMedium = screenWidthDp in 600..839,
            isExpanded = screenWidthDp >= 840,
            screenWidthDp = screenWidthDp
        )
    }

    // Determine if we should use dark theme
    val shouldUseDarkTheme = config.useDarkTheme ||
            (config.useTimeBasedTheme && timeOfDay == TimeOfDay.NIGHT)

    // Get appropriate color scheme
    val targetColorScheme = when {
        config.useHighContrast && shouldUseDarkTheme -> highContrastDarkColorScheme
        config.useHighContrast -> highContrastLightColorScheme
        shouldUseDarkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    // Apply time-based color overrides
    val colorScheme = if (config.useTimeBasedTheme && !shouldUseDarkTheme) {
        applyTimeBasedColors(targetColorScheme, timeOfDay)
    } else {
        targetColorScheme
    }

    // Animate color transitions
    val animatedColorScheme = if (!config.reduceMotion && config.enableMicroAnimations) {
        animateColorScheme(colorScheme)
    } else {
        colorScheme
    }

    // Update status bar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = animatedColorScheme.surface.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !shouldUseDarkTheme

            // Enable edge-to-edge display for modern look
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    // Provide theme values with enhanced locals
    CompositionLocalProvider(
        LocalChampionCartConfig provides config,
        LocalTimeOfDay provides timeOfDay,
        LocalContentColor provides animatedColorScheme.onBackground,
        LocalHazeState provides if (config.enableBlurEffects) hazeState else null,
        LocalReduceMotion provides config.reduceMotion,
        LocalResponsiveConfig provides responsiveConfig,
        LocalExtendedColors provides ExtendedColors(
            bestPrice = ChampionCartColors.Price.Best,
            midPrice = ChampionCartColors.Price.Mid,
            highPrice = ChampionCartColors.Price.High,
            bestPriceGlow = ChampionCartColors.Price.BestGlow,
            midPriceGlow = ChampionCartColors.Price.MidGlow,
            highPriceGlow = ChampionCartColors.Price.HighGlow,
            glassSurface = ChampionCartColors.Glass.Medium,
            glassOverlay = ChampionCartColors.Glass.Light
        )
    ) {
        MaterialTheme(
            colorScheme = animatedColorScheme,
            typography = ChampionCartTypography,
            shapes = ChampionCartShapes,
            content = content
        )
    }
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
 * Apply time-based color modifications
 */
private fun applyTimeBasedColors(
    baseScheme: ColorScheme,
    timeOfDay: TimeOfDay
): ColorScheme {
    return when (timeOfDay) {
        TimeOfDay.MORNING -> baseScheme.copy(
            primary = ChampionCartColors.Morning.primary,
            secondary = ChampionCartColors.Morning.secondary,
            tertiary = ChampionCartColors.Morning.tertiary,
            surface = ChampionCartColors.Morning.surface,
            background = ChampionCartColors.Morning.background
        )
        TimeOfDay.AFTERNOON -> baseScheme // Use default Electric Harmony colors
        TimeOfDay.EVENING -> baseScheme.copy(
            primary = ChampionCartColors.Evening.primary,
            secondary = ChampionCartColors.Evening.secondary,
            tertiary = ChampionCartColors.Evening.tertiary,
            surface = ChampionCartColors.Evening.surface,
            background = ChampionCartColors.Evening.background
        )
        TimeOfDay.NIGHT -> baseScheme // Night uses dark theme
    }
}

/**
 * Animate color scheme transitions
 */
@Composable
private fun animateColorScheme(targetScheme: ColorScheme): ColorScheme {
    val animationSpec = spring<Color>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    return ColorScheme(
        primary = animateColorAsState(targetScheme.primary, animationSpec, label = "primary").value,
        onPrimary = animateColorAsState(targetScheme.onPrimary, animationSpec, label = "onPrimary").value,
        primaryContainer = animateColorAsState(targetScheme.primaryContainer, animationSpec, label = "primaryContainer").value,
        onPrimaryContainer = animateColorAsState(targetScheme.onPrimaryContainer, animationSpec, label = "onPrimaryContainer").value,
        inversePrimary = animateColorAsState(targetScheme.inversePrimary, animationSpec, label = "inversePrimary").value,
        secondary = animateColorAsState(targetScheme.secondary, animationSpec, label = "secondary").value,
        onSecondary = animateColorAsState(targetScheme.onSecondary, animationSpec, label = "onSecondary").value,
        secondaryContainer = animateColorAsState(targetScheme.secondaryContainer, animationSpec, label = "secondaryContainer").value,
        onSecondaryContainer = animateColorAsState(targetScheme.onSecondaryContainer, animationSpec, label = "onSecondaryContainer").value,
        tertiary = animateColorAsState(targetScheme.tertiary, animationSpec, label = "tertiary").value,
        onTertiary = animateColorAsState(targetScheme.onTertiary, animationSpec, label = "onTertiary").value,
        tertiaryContainer = animateColorAsState(targetScheme.tertiaryContainer, animationSpec, label = "tertiaryContainer").value,
        onTertiaryContainer = animateColorAsState(targetScheme.onTertiaryContainer, animationSpec, label = "onTertiaryContainer").value,
        background = animateColorAsState(targetScheme.background, animationSpec, label = "background").value,
        onBackground = animateColorAsState(targetScheme.onBackground, animationSpec, label = "onBackground").value,
        surface = animateColorAsState(targetScheme.surface, animationSpec, label = "surface").value,
        onSurface = animateColorAsState(targetScheme.onSurface, animationSpec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(targetScheme.surfaceVariant, animationSpec, label = "surfaceVariant").value,
        onSurfaceVariant = animateColorAsState(targetScheme.onSurfaceVariant, animationSpec, label = "onSurfaceVariant").value,
        surfaceTint = animateColorAsState(targetScheme.surfaceTint, animationSpec, label = "surfaceTint").value,
        inverseSurface = animateColorAsState(targetScheme.inverseSurface, animationSpec, label = "inverseSurface").value,
        inverseOnSurface = animateColorAsState(targetScheme.inverseOnSurface, animationSpec, label = "inverseOnSurface").value,
        error = animateColorAsState(targetScheme.error, animationSpec, label = "error").value,
        onError = animateColorAsState(targetScheme.onError, animationSpec, label = "onError").value,
        errorContainer = animateColorAsState(targetScheme.errorContainer, animationSpec, label = "errorContainer").value,
        onErrorContainer = animateColorAsState(targetScheme.onErrorContainer, animationSpec, label = "onErrorContainer").value,
        outline = animateColorAsState(targetScheme.outline, animationSpec, label = "outline").value,
        outlineVariant = animateColorAsState(targetScheme.outlineVariant, animationSpec, label = "outlineVariant").value,
        scrim = animateColorAsState(targetScheme.scrim, animationSpec, label = "scrim").value
    )
}

/**
 * Theme accessor object
 */
object ChampionCartTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val spacing: Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current

    val elevation: Elevation
        @Composable
        @ReadOnlyComposable
        get() = LocalElevation.current

    val config: ChampionCartConfig
        @Composable
        @ReadOnlyComposable
        get() = LocalChampionCartConfig.current

    val hazeState: HazeState?
        @Composable
        @ReadOnlyComposable
        get() = LocalHazeState.current

    val isReduceMotion: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalReduceMotion.current

    val responsive: ResponsiveConfig
        @Composable
        @ReadOnlyComposable
        get() = LocalResponsiveConfig.current
}