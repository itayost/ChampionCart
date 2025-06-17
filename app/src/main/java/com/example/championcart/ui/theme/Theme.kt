package com.example.championcart.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
    // Time-based color determination
    val timeBasedColors = getTimeBasedColorsIfEnabled(timeBasedTheme)

    // Theme configuration
    val themeConfig = ThemeConfig(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        highContrast = highContrast,
        timeBasedColors = timeBasedColors,
        reduceMotion = reduceMotion,
        hapticsEnabled = hapticsEnabled,
        glassEffectsEnabled = glassEffectsEnabled,
        performanceMode = performanceMode
    )

    // Color schemes
    val colorScheme = getColorScheme(themeConfig)
    val extendedColors = getExtendedColors(themeConfig)

    // Animated color transitions
    val animatedColorScheme = createAnimatedColorScheme(colorScheme)
    val animatedExtendedColors = createAnimatedExtendedColors(extendedColors)

    // Glass theme configuration
    val glassConfig = GlassThemeConfig(
        defaultIntensity = if (performanceMode) GlassIntensity.Light else GlassIntensity.Medium,
        enableAnimations = !reduceMotion && glassEffectsEnabled,
        enableBlur = glassEffectsEnabled && !performanceMode,
        performanceMode = performanceMode
    )

    // Configure system UI
    ConfigureSystemUI(
        useDarkTheme = themeConfig.shouldUseDarkTheme(),
        colorScheme = animatedColorScheme
    )

    // Apply theme
    CompositionLocalProvider(
        LocalThemeConfig provides themeConfig,
        LocalExtendedColors provides animatedExtendedColors,
        LocalReduceMotion provides reduceMotion,
        LocalHapticsEnabled provides hapticsEnabled,
        LocalGlassEffectsEnabled provides glassEffectsEnabled
    ) {
        ProvideGlassTheme(config = glassConfig) {
            MaterialTheme(
                colorScheme = animatedColorScheme,
                typography = Typography,
                shapes = Shapes,
                content = content
            )
        }
    }
}

/**
 * Get appropriate color scheme based on configuration
 */
@Composable
private fun getColorScheme(config: ThemeConfig): ColorScheme {
    val context = LocalContext.current

    return when {
        // Dynamic color support (Android 12+)
        config.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (config.shouldUseDarkTheme()) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        // High contrast themes
        config.highContrast -> {
            if (config.shouldUseDarkTheme()) {
                HighContrastDarkColorScheme
            } else {
                HighContrastLightColorScheme
            }
        }

        // Time-based color modifications
        config.timeBasedColors -> {
            val baseScheme = if (config.shouldUseDarkTheme()) {
                DarkColorScheme
            } else {
                LightColorScheme
            }
            applyTimeBasedColors(baseScheme)
        }

        // Standard themes
        else -> {
            if (config.shouldUseDarkTheme()) {
                DarkColorScheme
            } else {
                LightColorScheme
            }
        }
    }
}

/**
 * Get extended colors based on configuration
 */
@Composable
private fun getExtendedColors(config: ThemeConfig): ExtendedColors {
    return when {
        config.highContrast -> {
            if (config.shouldUseDarkTheme()) {
                highContrastDarkExtendedColors
            } else {
                highContrastLightExtendedColors
            }
        }
        config.shouldUseDarkTheme() -> darkExtendedColors
        else -> lightExtendedColors
    }
}

/**
 * Create animated color scheme for smooth transitions
 */
@Composable
private fun createAnimatedColorScheme(targetScheme: ColorScheme): ColorScheme {
    val animationSpec: AnimationSpec<Color> = if (LocalReduceMotion.current) {
        snap()
    } else {
        spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    }

    return ColorScheme(
        primary = animateColorAsState(
            targetValue = targetScheme.primary,
            animationSpec = animationSpec,
            label = "primary"
        ).value,
        onPrimary = animateColorAsState(
            targetValue = targetScheme.onPrimary,
            animationSpec = animationSpec,
            label = "onPrimary"
        ).value,
        primaryContainer = animateColorAsState(
            targetValue = targetScheme.primaryContainer,
            animationSpec = animationSpec,
            label = "primaryContainer"
        ).value,
        onPrimaryContainer = animateColorAsState(
            targetValue = targetScheme.onPrimaryContainer,
            animationSpec = animationSpec,
            label = "onPrimaryContainer"
        ).value,
        inversePrimary = animateColorAsState(
            targetValue = targetScheme.inversePrimary,
            animationSpec = animationSpec,
            label = "inversePrimary"
        ).value,
        secondary = animateColorAsState(
            targetValue = targetScheme.secondary,
            animationSpec = animationSpec,
            label = "secondary"
        ).value,
        onSecondary = animateColorAsState(
            targetValue = targetScheme.onSecondary,
            animationSpec = animationSpec,
            label = "onSecondary"
        ).value,
        secondaryContainer = animateColorAsState(
            targetValue = targetScheme.secondaryContainer,
            animationSpec = animationSpec,
            label = "secondaryContainer"
        ).value,
        onSecondaryContainer = animateColorAsState(
            targetValue = targetScheme.onSecondaryContainer,
            animationSpec = animationSpec,
            label = "onSecondaryContainer"
        ).value,
        tertiary = animateColorAsState(
            targetValue = targetScheme.tertiary,
            animationSpec = animationSpec,
            label = "tertiary"
        ).value,
        onTertiary = animateColorAsState(
            targetValue = targetScheme.onTertiary,
            animationSpec = animationSpec,
            label = "onTertiary"
        ).value,
        tertiaryContainer = animateColorAsState(
            targetValue = targetScheme.tertiaryContainer,
            animationSpec = animationSpec,
            label = "tertiaryContainer"
        ).value,
        onTertiaryContainer = animateColorAsState(
            targetValue = targetScheme.onTertiaryContainer,
            animationSpec = animationSpec,
            label = "onTertiaryContainer"
        ).value,
        error = animateColorAsState(
            targetValue = targetScheme.error,
            animationSpec = animationSpec,
            label = "error"
        ).value,
        onError = animateColorAsState(
            targetValue = targetScheme.onError,
            animationSpec = animationSpec,
            label = "onError"
        ).value,
        errorContainer = animateColorAsState(
            targetValue = targetScheme.errorContainer,
            animationSpec = animationSpec,
            label = "errorContainer"
        ).value,
        onErrorContainer = animateColorAsState(
            targetValue = targetScheme.onErrorContainer,
            animationSpec = animationSpec,
            label = "onErrorContainer"
        ).value,
        background = animateColorAsState(
            targetValue = targetScheme.background,
            animationSpec = animationSpec,
            label = "background"
        ).value,
        onBackground = animateColorAsState(
            targetValue = targetScheme.onBackground,
            animationSpec = animationSpec,
            label = "onBackground"
        ).value,
        surface = animateColorAsState(
            targetValue = targetScheme.surface,
            animationSpec = animationSpec,
            label = "surface"
        ).value,
        onSurface = animateColorAsState(
            targetValue = targetScheme.onSurface,
            animationSpec = animationSpec,
            label = "onSurface"
        ).value,
        surfaceVariant = animateColorAsState(
            targetValue = targetScheme.surfaceVariant,
            animationSpec = animationSpec,
            label = "surfaceVariant"
        ).value,
        onSurfaceVariant = animateColorAsState(
            targetValue = targetScheme.onSurfaceVariant,
            animationSpec = animationSpec,
            label = "onSurfaceVariant"
        ).value,
        surfaceTint = animateColorAsState(
            targetValue = targetScheme.surfaceTint,
            animationSpec = animationSpec,
            label = "surfaceTint"
        ).value,
        outline = animateColorAsState(
            targetValue = targetScheme.outline,
            animationSpec = animationSpec,
            label = "outline"
        ).value,
        outlineVariant = animateColorAsState(
            targetValue = targetScheme.outlineVariant,
            animationSpec = animationSpec,
            label = "outlineVariant"
        ).value,
        scrim = animateColorAsState(
            targetValue = targetScheme.scrim,
            animationSpec = animationSpec,
            label = "scrim"
        ).value,
        inverseSurface = animateColorAsState(
            targetValue = targetScheme.inverseSurface,
            animationSpec = animationSpec,
            label = "inverseSurface"
        ).value,
        inverseOnSurface = animateColorAsState(
            targetValue = targetScheme.inverseOnSurface,
            animationSpec = animationSpec,
            label = "inverseOnSurface"
        ).value,
        surfaceDim = animateColorAsState(
            targetValue = targetScheme.surfaceDim,
            animationSpec = animationSpec,
            label = "surfaceDim"
        ).value,
        surfaceBright = animateColorAsState(
            targetValue = targetScheme.surfaceBright,
            animationSpec = animationSpec,
            label = "surfaceBright"
        ).value,
        surfaceContainerLowest = animateColorAsState(
            targetValue = targetScheme.surfaceContainerLowest,
            animationSpec = animationSpec,
            label = "surfaceContainerLowest"
        ).value,
        surfaceContainerLow = animateColorAsState(
            targetValue = targetScheme.surfaceContainerLow,
            animationSpec = animationSpec,
            label = "surfaceContainerLow"
        ).value,
        surfaceContainer = animateColorAsState(
            targetValue = targetScheme.surfaceContainer,
            animationSpec = animationSpec,
            label = "surfaceContainer"
        ).value,
        surfaceContainerHigh = animateColorAsState(
            targetValue = targetScheme.surfaceContainerHigh,
            animationSpec = animationSpec,
            label = "surfaceContainerHigh"
        ).value,
        surfaceContainerHighest = animateColorAsState(
            targetValue = targetScheme.surfaceContainerHighest,
            animationSpec = animationSpec,
            label = "surfaceContainerHighest"
        ).value
    )
}

/**
 * Create animated extended colors
 */
@Composable
private fun createAnimatedExtendedColors(targetColors: ExtendedColors): ExtendedColors {
    val animationSpec: AnimationSpec<Color> = if (LocalReduceMotion.current) {
        snap()
    } else {
        spring(
            dampingRatio = SpringSpecs.DampingRatioNoBounce,
            stiffness = SpringSpecs.StiffnessLow
        )
    }

    return ExtendedColors(
        electricMint = animateColorAsState(
            targetValue = targetColors.electricMint,
            animationSpec = animationSpec,
            label = "electricMint"
        ).value,
        electricMintVariant = animateColorAsState(
            targetValue = targetColors.electricMintVariant,
            animationSpec = animationSpec,
            label = "electricMintVariant"
        ).value,
        cosmicPurple = animateColorAsState(
            targetValue = targetColors.cosmicPurple,
            animationSpec = animationSpec,
            label = "cosmicPurple"
        ).value,
        cosmicPurpleVariant = animateColorAsState(
            targetValue = targetColors.cosmicPurpleVariant,
            animationSpec = animationSpec,
            label = "cosmicPurpleVariant"
        ).value,
        deepNavy = animateColorAsState(
            targetValue = targetColors.deepNavy,
            animationSpec = animationSpec,
            label = "deepNavy"
        ).value,
        deepNavyVariant = animateColorAsState(
            targetValue = targetColors.deepNavyVariant,
            animationSpec = animationSpec,
            label = "deepNavyVariant"
        ).value,
        glassLight = targetColors.glassLight,
        glassMedium = targetColors.glassMedium,
        glassHeavy = targetColors.glassHeavy,
        glassAccent = targetColors.glassAccent,
        success = targetColors.success,
        successContainer = targetColors.successContainer,
        onSuccess = targetColors.onSuccess,
        onSuccessContainer = targetColors.onSuccessContainer,
        warning = targetColors.warning,
        warningContainer = targetColors.warningContainer,
        onWarning = targetColors.onWarning,
        onWarningContainer = targetColors.onWarningContainer,
        info = targetColors.info,
        infoContainer = targetColors.infoContainer,
        onInfo = targetColors.onInfo,
        onInfoContainer = targetColors.onInfoContainer,
        bestPrice = targetColors.bestPrice,
        bestPriceContainer = targetColors.bestPriceContainer,
        onBestPrice = targetColors.onBestPrice,
        midPrice = targetColors.midPrice,
        midPriceContainer = targetColors.midPriceContainer,
        onMidPrice = targetColors.onMidPrice,
        highPrice = targetColors.highPrice,
        highPriceContainer = targetColors.highPriceContainer,
        onHighPrice = targetColors.onHighPrice,
        shufersal = targetColors.shufersal,
        ramiLevi = targetColors.ramiLevi,
        victory = targetColors.victory,
        mega = targetColors.mega,
        osherAd = targetColors.osherAd,
        coop = targetColors.coop,
        dairy = targetColors.dairy,
        meat = targetColors.meat,
        produce = targetColors.produce,
        bakery = targetColors.bakery,
        frozen = targetColors.frozen,
        household = targetColors.household,
        kosher = targetColors.kosher,
        organic = targetColors.organic,
        morningAccent = animateColorAsState(
            targetValue = targetColors.morningAccent,
            animationSpec = animationSpec,
            label = "morningAccent"
        ).value,
        afternoonAccent = animateColorAsState(
            targetValue = targetColors.afternoonAccent,
            animationSpec = animationSpec,
            label = "afternoonAccent"
        ).value,
        eveningAccent = animateColorAsState(
            targetValue = targetColors.eveningAccent,
            animationSpec = animationSpec,
            label = "eveningAccent"
        ).value,
        nightAccent = animateColorAsState(
            targetValue = targetColors.nightAccent,
            animationSpec = animationSpec,
            label = "nightAccent"
        ).value,
        gradientStart = targetColors.gradientStart,
        gradientMiddle = targetColors.gradientMiddle,
        gradientEnd = targetColors.gradientEnd,
        interactiveDefault = targetColors.interactiveDefault,
        interactiveHover = targetColors.interactiveHover,
        interactivePressed = targetColors.interactivePressed,
        interactiveDisabled = targetColors.interactiveDisabled,
        interactiveFocus = targetColors.interactiveFocus,
        surfaceGlass = targetColors.surfaceGlass,
        surfaceElevated = targetColors.surfaceElevated,
        surfaceCard = targetColors.surfaceCard,
        surfaceModal = targetColors.surfaceModal,
        surfaceNavigation = targetColors.surfaceNavigation,
        borderSubtle = targetColors.borderSubtle,
        borderDefault = targetColors.borderDefault,
        borderStrong = targetColors.borderStrong,
        borderGlass = targetColors.borderGlass,
        textPrimary = targetColors.textPrimary,
        textSecondary = targetColors.textSecondary,
        textTertiary = targetColors.textTertiary,
        textInverse = targetColors.textInverse,
        textOnGlass = targetColors.textOnGlass,
        shadow = targetColors.shadow,
        glow = targetColors.glow,
        shimmer = targetColors.shimmer,
        highlight = targetColors.highlight
    )
}

/**
 * Time-based color helpers
 */
@Composable
private fun getTimeBasedColorsIfEnabled(enabled: Boolean): Boolean {
    return if (enabled) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        // Use time-based logic here
        true
    } else {
        false
    }
}

/**
 * Apply time-based color modifications
 */
@Composable
private fun applyTimeBasedColors(baseScheme: ColorScheme): ColorScheme {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    // For now, return the base scheme - time-based modifications can be added here
    return baseScheme
}

/**
 * Configure system UI colors
 */
@Composable
private fun ConfigureSystemUI(
    useDarkTheme: Boolean,
    colorScheme: ColorScheme
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }
}