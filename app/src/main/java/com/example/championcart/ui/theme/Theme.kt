package com.example.championcart.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import java.time.LocalTime

/**
 * Champion Cart - Electric Harmony Theme
 * Dynamic, vibrant theme with glassmorphism and time-based colors
 * FIXED VERSION - Compatible with existing Color.kt
 */

// Light Color Scheme - Electric Harmony
private fun lightColorScheme(timeBasedColors: TimeBasedColors? = null) = lightColorScheme(
    primary = timeBasedColors?.primary ?: ChampionCartColors.electricMint,
    onPrimary = ChampionCartColors.textOnPrimary,
    primaryContainer = ChampionCartColors.electricMintLight,
    onPrimaryContainer = ChampionCartColors.electricMintDark,

    secondary = ChampionCartColors.cosmicPurple,
    onSecondary = ChampionCartColors.textOnSecondary,
    secondaryContainer = ChampionCartColors.cosmicPurpleLight,
    onSecondaryContainer = ChampionCartColors.cosmicPurpleDark,

    tertiary = timeBasedColors?.accent ?: ChampionCartColors.neonCoral,
    onTertiary = ChampionCartColors.textOnAccent,
    tertiaryContainer = ChampionCartColors.neonCoralLight,
    onTertiaryContainer = ChampionCartColors.neonCoralDark,

    error = ChampionCartColors.errorRed,
    errorContainer = ChampionCartColors.errorRed.copy(alpha = 0.1f),
    onError = Color.White,
    onErrorContainer = ChampionCartColors.errorRed,

    background = timeBasedColors?.gradientStart ?: ChampionCartColors.backgroundLight,
    onBackground = ChampionCartColors.textPrimary,

    surface = ChampionCartColors.surfaceLight,
    onSurface = ChampionCartColors.textPrimary,
    surfaceVariant = ChampionCartColors.surfaceVariantLight,
    onSurfaceVariant = ChampionCartColors.textSecondary,

    outline = ChampionCartColors.textTertiary.copy(alpha = 0.5f),
    outlineVariant = ChampionCartColors.textTertiary.copy(alpha = 0.25f),
    scrim = ChampionCartColors.shadowDark,

    inverseSurface = ChampionCartColors.textPrimary,
    inverseOnSurface = ChampionCartColors.surfaceLight,
    inversePrimary = ChampionCartColors.electricMintLight
)

// Dark Color Scheme - Night Mode
private val darkColorScheme = darkColorScheme(
    primary = ChampionCartColors.electricMintLight,
    onPrimary = ChampionCartColors.electricMintDark,
    primaryContainer = ChampionCartColors.electricMint.copy(alpha = 0.3f),
    onPrimaryContainer = ChampionCartColors.electricMintLight,

    secondary = ChampionCartColors.cosmicPurpleLight,
    onSecondary = ChampionCartColors.cosmicPurpleDark,
    secondaryContainer = ChampionCartColors.cosmicPurple.copy(alpha = 0.3f),
    onSecondaryContainer = ChampionCartColors.cosmicPurpleLight,

    tertiary = ChampionCartColors.neonCoralLight,
    onTertiary = ChampionCartColors.neonCoralDark,
    tertiaryContainer = ChampionCartColors.neonCoral.copy(alpha = 0.3f),
    onTertiaryContainer = ChampionCartColors.neonCoralLight,

    error = ChampionCartColors.errorRed,
    errorContainer = ChampionCartColors.errorRed.copy(alpha = 0.2f),
    onError = ChampionCartColors.textPrimary,
    onErrorContainer = ChampionCartColors.errorRed,

    background = ChampionCartColors.backgroundDark,
    onBackground = ChampionCartColors.textPrimaryDark,

    surface = ChampionCartColors.surfaceDark,
    onSurface = ChampionCartColors.textPrimaryDark,
    surfaceVariant = ChampionCartColors.surfaceVariantDark,
    onSurfaceVariant = ChampionCartColors.textSecondaryDark,

    outline = ChampionCartColors.textSecondaryDark.copy(alpha = 0.5f),
    outlineVariant = ChampionCartColors.textSecondaryDark.copy(alpha = 0.25f),
    scrim = ChampionCartColors.shadowDark,

    inverseSurface = ChampionCartColors.textPrimaryDark,
    inverseOnSurface = ChampionCartColors.surfaceDark,
    inversePrimary = ChampionCartColors.electricMint
)

// Extended theme colors - matching your existing structure
val lightExtendedColors = ExtendedColors(
    electricMint = ChampionCartColors.electricMint,
    electricMintGlow = ChampionCartColors.electricMintGlow,
    cosmicPurple = ChampionCartColors.cosmicPurple,
    cosmicPurpleGlow = ChampionCartColors.cosmicPurpleGlow,
    neonCoral = ChampionCartColors.neonCoral,
    neonCoralGlow = ChampionCartColors.neonCoralGlow,

    success = ChampionCartColors.successGreen,
    successGlow = ChampionCartColors.successGreenGlow,
    warning = ChampionCartColors.warningAmber,
    warningGlow = ChampionCartColors.warningAmberGlow,
    error = ChampionCartColors.errorRed,
    errorGlow = ChampionCartColors.errorRedGlow,
    info = ChampionCartColors.infoBlue,
    infoGlow = ChampionCartColors.infoBlueGlow,

    bestPrice = ChampionCartColors.bestPrice,
    bestPriceGlow = ChampionCartColors.bestPriceGlow,
    midPrice = ChampionCartColors.midPrice,
    highPrice = ChampionCartColors.highPrice,
    priceLow = ChampionCartColors.bestPrice,
    priceHigh = ChampionCartColors.highPrice,
    bestDeal = ChampionCartColors.bestPrice,
    savings = ChampionCartColors.electricMint,
    tertiary = ChampionCartColors.neonCoral,
    errorRed = ChampionCartColors.errorRed,

    glass = ChampionCartColors.glassLight,
    glassBorder = ChampionCartColors.glassLightBorder,
    glassDark = ChampionCartColors.glassDark,
    glassDarkBorder = ChampionCartColors.glassDarkBorder,
    glassFrosted = ChampionCartColors.glassFrosted,
    glassFrostedBorder = ChampionCartColors.glassFrostedBorder,

    shufersal = ChampionCartColors.shufersalBrand,
    shufersalGlow = ChampionCartColors.shufersalGlow,
    victory = ChampionCartColors.victoryBrand,
    victoryGlow = ChampionCartColors.victoryGlow,
    ramiLevy = ChampionCartColors.ramiLevyBrand,
    ramiLevyGlow = ChampionCartColors.ramiLevyGlow,
    mega = ChampionCartColors.megaBrand,
    megaGlow = ChampionCartColors.megaGlow,
    genericStore = ChampionCartColors.genericStore,

    morningPrimary = ChampionCartColors.morningPrimary,
    morningAccent = ChampionCartColors.morningAccent,
    morningGradientStart = ChampionCartColors.morningGradientStart,
    morningGradientEnd = ChampionCartColors.morningGradientEnd,
    eveningPrimary = ChampionCartColors.eveningPrimary,
    eveningAccent = ChampionCartColors.eveningAccent,
    eveningGradientStart = ChampionCartColors.eveningGradientStart,
    eveningGradientEnd = ChampionCartColors.eveningGradientEnd,
    nightPrimary = ChampionCartColors.nightPrimary,
    nightAccent = ChampionCartColors.nightAccent,

    shimmerHighlight = ChampionCartColors.shimmerHighlight,
    shadowLight = ChampionCartColors.shadowLight,
    shadowMedium = ChampionCartColors.shadowMedium,
    shadowDark = ChampionCartColors.shadowDark
)

// Dark theme extended colors
val darkExtendedColors = lightExtendedColors.copy(
    glass = ChampionCartColors.glassDark,
    glassBorder = ChampionCartColors.glassDarkBorder,
    glassDark = ChampionCartColors.glassDark,
    glassDarkBorder = ChampionCartColors.glassDarkBorder
)

// High contrast extended colors
val highContrastExtendedColors = lightExtendedColors.copy(
    electricMint = Color.Black,
    electricMintGlow = Color.Transparent,
    cosmicPurple = Color.Black,
    cosmicPurpleGlow = Color.Transparent,
    neonCoral = Color.Black,
    neonCoralGlow = Color.Transparent,
    tertiary = Color.Black,
    glass = Color.Transparent,
    glassBorder = Color.Black,
    glassFrosted = Color.White.copy(alpha = 0.9f),
    glassFrostedBorder = Color.Black
)

// Composition locals
val LocalExtendedColors = staticCompositionLocalOf {
    lightExtendedColors
}

val LocalReduceMotion = staticCompositionLocalOf { false }
val LocalHapticsEnabled = staticCompositionLocalOf { true }

// Extension property to access extended colors from MaterialTheme
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

// Theme configuration data class
private data class ThemeConfig(
    val darkTheme: Boolean,
    val dynamicColor: Boolean,
    val highContrast: Boolean,
    val timeBasedColors: TimeBasedColors?
) {
    fun shouldUseDarkTheme(): Boolean = darkTheme || timeBasedColors?.isDark == true
}

// EXTRACTED HELPER FUNCTIONS - Low Cognitive Complexity

@Composable
private fun getTimeBasedColorsIfEnabled(timeBasedTheme: Boolean): TimeBasedColors? {
    return if (timeBasedTheme) {
        val currentHour = remember { LocalTime.now().hour }
        remember(currentHour) { getTimeBasedColors(currentHour) }
    } else {
        null
    }
}

@Composable
private fun getColorScheme(config: ThemeConfig): ColorScheme {
    return when {
        config.highContrast -> getHighContrastColorScheme(config)
        config.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            getDynamicColorScheme(config)
        }
        config.shouldUseDarkTheme() -> darkColorScheme
        else -> lightColorScheme(config.timeBasedColors)
    }
}

@Composable
private fun getHighContrastColorScheme(config: ThemeConfig): ColorScheme {
    return if (config.darkTheme) darkColorScheme else lightColorScheme()
}

@Composable
private fun getDynamicColorScheme(config: ThemeConfig): ColorScheme {
    val context = LocalContext.current
    return if (config.shouldUseDarkTheme()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicDarkColorScheme(context)
        } else {
            darkColorScheme
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicLightColorScheme(context)
        } else {
            lightColorScheme(config.timeBasedColors)
        }
    }
}

private fun getExtendedColors(config: ThemeConfig): ExtendedColors {
    return when {
        config.highContrast -> highContrastExtendedColors
        config.shouldUseDarkTheme() -> darkExtendedColors
        else -> lightExtendedColors
    }
}

// Overload for compatibility with new files
@Composable
fun getExtendedColors(isDark: Boolean): ExtendedColors {
    return if (isDark) darkExtendedColors else lightExtendedColors
}

@Composable
private fun createAnimatedColorScheme(colorScheme: ColorScheme): ColorScheme {
    return colorScheme.copy(
        primary = animateColorAsState(
            targetValue = colorScheme.primary,
            animationSpec = tween(1000),
            label = "primary"
        ).value,
        background = animateColorAsState(
            targetValue = colorScheme.background,
            animationSpec = tween(1000),
            label = "background"
        ).value
        // Add more animated colors as needed
    )
}

@Composable
private fun createAnimatedExtendedColors(extendedColors: ExtendedColors): ExtendedColors {
    return extendedColors.copy(
        electricMint = animateColorAsState(
            targetValue = extendedColors.electricMint,
            animationSpec = tween(1000),
            label = "electricMint"
        ).value
        // Add more animated colors as needed
    )
}

@Composable
private fun ConfigureSystemUI(
    useDarkTheme: Boolean,
    colorScheme: ColorScheme
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !useDarkTheme
        }
    }
}

// MAIN THEME FUNCTION
@Composable
fun ChampionCartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    timeBasedTheme: Boolean = true,
    highContrast: Boolean = false,
    reduceMotion: Boolean = false,
    hapticsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    // Get time-based colors
    val timeBasedColors = getTimeBasedColorsIfEnabled(timeBasedTheme)

    // Determine theme configuration
    val themeConfig = ThemeConfig(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        highContrast = highContrast,
        timeBasedColors = timeBasedColors
    )

    // Get color schemes
    val colorScheme = getColorScheme(themeConfig)
    val extendedColors = getExtendedColors(themeConfig)

    // Create animated colors
    val animatedColorScheme = createAnimatedColorScheme(colorScheme)
    val animatedExtendedColors = createAnimatedExtendedColors(extendedColors)

    // Configure system UI
    ConfigureSystemUI(
        useDarkTheme = themeConfig.shouldUseDarkTheme(),
        colorScheme = animatedColorScheme
    )

    // Apply theme
    CompositionLocalProvider(
        LocalExtendedColors provides animatedExtendedColors,
        LocalReduceMotion provides reduceMotion,
        LocalHapticsEnabled provides hapticsEnabled
    ) {
        MaterialTheme(
            colorScheme = animatedColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

// Provider composable for extended colors
@Composable
fun ProvideExtendedColors(
    colors: ExtendedColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalExtendedColors provides colors,
        content = content
    )
}

// Convenience theme composables
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