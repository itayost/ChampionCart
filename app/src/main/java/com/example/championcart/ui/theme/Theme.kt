package com.example.championcart.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
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
)

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
    val animationSpec = if (LocalReduceMotion.current) {
        snap()
    } else {
        SpringSpecs.Smooth
    }

    return ColorScheme(
        primary = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.primary,
            animationSpec = animationSpec,
            label = "primary"
        ).value,
        onPrimary = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onPrimary,
            animationSpec = animationSpec,
            label = "onPrimary"
        ).value,
        primaryContainer = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.primaryContainer,
            animationSpec = animationSpec,
            label = "primaryContainer"
        ).value,
        onPrimaryContainer = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onPrimaryContainer,
            animationSpec = animationSpec,
            label = "onPrimaryContainer"
        ).value,
        secondary = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.secondary,
            animationSpec = animationSpec,
            label = "secondary"
        ).value,
        onSecondary = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onSecondary,
            animationSpec = animationSpec,
            label = "onSecondary"
        ).value,
        secondaryContainer = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.secondaryContainer,
            animationSpec = animationSpec,
            label = "secondaryContainer"
        ).value,
        onSecondaryContainer = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onSecondaryContainer,
            animationSpec = animationSpec,
            label = "onSecondaryContainer"
        ).value,
        tertiary = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.tertiary,
            animationSpec = animationSpec,
            label = "tertiary"
        ).value,
        onTertiary = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onTertiary,
            animationSpec = animationSpec,
            label = "onTertiary"
        ).value,
        tertiaryContainer = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.tertiaryContainer,
            animationSpec = animationSpec,
            label = "tertiaryContainer"
        ).value,
        onTertiaryContainer = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onTertiaryContainer,
            animationSpec = animationSpec,
            label = "onTertiaryContainer"
        ).value,
        error = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.error,
            animationSpec = animationSpec,
            label = "error"
        ).value,
        onError = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onError,
            animationSpec = animationSpec,
            label = "onError"
        ).value,
        errorContainer = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.errorContainer,
            animationSpec = animationSpec,
            label = "errorContainer"
        ).value,
        onErrorContainer = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onErrorContainer,
            animationSpec = animationSpec,
            label = "onErrorContainer"
        ).value,
        background = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.background,
            animationSpec = animationSpec,
            label = "background"
        ).value,
        onBackground = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onBackground,
            animationSpec = animationSpec,
            label = "onBackground"
        ).value,
        surface = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.surface,
            animationSpec = animationSpec,
            label = "surface"
        ).value,
        onSurface = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onSurface,
            animationSpec = animationSpec,
            label = "onSurface"
        ).value,
        surfaceVariant = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.surfaceVariant,
            animationSpec = animationSpec,
            label = "surfaceVariant"
        ).value,
        onSurfaceVariant = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.onSurfaceVariant,
            animationSpec = animationSpec,
            label = "onSurfaceVariant"
        ).value,
        outline = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.outline,
            animationSpec = animationSpec,
            label = "outline"
        ).value,
        outlineVariant = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.outlineVariant,
            animationSpec = animationSpec,
            label = "outlineVariant"
        ).value,
        scrim = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.scrim,
            animationSpec = animationSpec,
            label = "scrim"
        ).value,
        inverseSurface = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.inverseSurface,
            animationSpec = animationSpec,
            label = "inverseSurface"
        ).value,
        inverseOnSurface = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.inverseOnSurface,
            animationSpec = animationSpec,
            label = "inverseOnSurface"
        ).value,
        inversePrimary = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.inversePrimary,
            animationSpec = animationSpec,
            label = "inversePrimary"
        ).value,
        surfaceDim = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.surfaceDim,
            animationSpec = animationSpec,
            label = "surfaceDim"
        ).value,
        surfaceBright = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.surfaceBright,
            animationSpec = animationSpec,
            label = "surfaceBright"
        ).value,
        surfaceContainerLowest = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.surfaceContainerLowest,
            animationSpec = animationSpec,
            label = "surfaceContainerLowest"
        ).value,
        surfaceContainerLow = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.surfaceContainerLow,
            animationSpec = animationSpec,
            label = "surfaceContainerLow"
        ).value,
        surfaceContainer = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.surfaceContainer,
            animationSpec = animationSpec,
            label = "surfaceContainer"
        ).value,
        surfaceContainerHigh = androidx.compose.animation.animateColorAsState(
            targetValue = targetScheme.surfaceContainerHigh,
            animationSpec = animationSpec,
            label = "surfaceContainerHigh"
        ).value,
        surfaceContainerHighest = androidx.compose.animation.animateColorAsState(
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
    val animationSpec = if (LocalReduceMotion.current) {
        snap()
    } else {
        SpringSpecs.Gentle
    }

    return ExtendedColors(
        electricMint = androidx.compose.animation.animateColorAsState(
            targetValue = targetColors.electricMint,
            animationSpec = animationSpec,
            label = "electricMint"
        ).value,
        electricMintVariant = androidx.compose.animation.animateColorAsState(
            targetValue = targetColors.electricMintVariant,
            animationSpec = animationSpec,
            label = "electricMintVariant"
        ).value,
        cosmicPurple = androidx.compose.animation.animateColorAsState(
            targetValue = targetColors.cosmicPurple,
            animationSpec = animationSpec,
            label = "cosmicPurple"
        ).value,
        cosmicPurpleVariant = androidx.compose.animation.animateColorAsState(
            targetValue = targetColors.cosmicPurpleVariant,
            animationSpec = animationSpec,
            label = "cosmicPurpleVariant"
        ).value,
        deepNavy = androidx.compose.animation.animateColorAsState(
            targetValue = targetColors.deepNavy,
            animationSpec = animationSpec,
            label = "deepNavy"
        ).value,
        deepNavyVariant = androidx.compose.animation.animateColorAsState(
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
        morningAccent = androidx.compose.animation.animateColorAsState(
            targetValue = targetColors.morningAccent,
            animationSpec = animationSpec,
            label = "morningAccent"
        ).value,
        afternoonAccent = androidx.compose.animation.animateColorAsState(
            targetValue = targetColors.afternoonAccent,
            animationSpec = animationSpec,
            label = "afternoonAccent"
        ).value,
        eveningAccent = androidx.compose.animation.animateColorAsState(
            targetValue = targetColors.eveningAccent,
            animationSpec = animationSpec,
            label = "eveningAccent"
        ).value,
        nightAccent = androidx.compose.animation.animateColorAsState(
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

        // Auto switch to dark mode during night hours
        hour in 22..23 || hour in 0..6
    } else {
        false
    }
}

@Composable
private fun applyTimeBasedColors(baseScheme: ColorScheme): ColorScheme {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    val timeAccent = getTimeBasedPrimary(hour)

    return baseScheme.copy(
        primary = timeAccent,
        secondary = timeAccent.copy(alpha = 0.8f)
    )
}

/**
 * Theme configuration extension
 */
private fun ThemeConfig.shouldUseDarkTheme(): Boolean {
    return darkTheme || timeBasedColors
}

/**
 * System UI configuration
 */
@Composable
private fun ConfigureSystemUI(
    useDarkTheme: Boolean,
    colorScheme: ColorScheme
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }
}

/**
 * Convenience theme composables
 */
@Composable
fun ChampionCartLightTheme(content: @Composable () -> Unit) {
    ChampionCartTheme(
        darkTheme = false,
        timeBasedTheme = false,
        content = content
    )
}

@Composable
fun ChampionCartDarkTheme(content: @Composable () -> Unit) {
    ChampionCartTheme(
        darkTheme = true,
        timeBasedTheme = false,
        content = content
    )
}

@Composable
fun ChampionCartAutoTheme(content: @Composable () -> Unit) {
    ChampionCartTheme(
        darkTheme = isSystemInDarkTheme(),
        timeBasedTheme = true,
        content = content
    )
}

/**
 * Theme preference helpers
 */
@Composable
fun rememberThemePreference(): MutableState<ThemePreference> {
    return remember { mutableStateOf(ThemePreference.System) }
}

@Composable
fun applyThemePreference(
    preference: ThemePreference,
    content: @Composable () -> Unit
) {
    when (preference) {
        ThemePreference.System -> ChampionCartTheme(content = content)
        ThemePreference.Light -> ChampionCartLightTheme(content = content)
        ThemePreference.Dark -> ChampionCartDarkTheme(content = content)
        ThemePreference.Auto -> ChampionCartAutoTheme(content = content)
    }
}