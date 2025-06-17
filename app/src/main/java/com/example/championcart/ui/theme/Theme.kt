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
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.LocalTime

/**
 * Champion Cart - Electric Harmony Theme
 * Dynamic, vibrant theme with glassmorphism and time-based colors
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

// Extended theme colors
data class ExtendedColors(
    // Primary actions
    val electricMint: Color,
    val electricMintGlow: Color,
    val cosmicPurple: Color,
    val cosmicPurpleGlow: Color,
    val neonCoral: Color,
    val neonCoralGlow: Color,

    // Semantic colors
    val success: Color,
    val successGlow: Color,
    val warning: Color,
    val warningGlow: Color,
    val error: Color,
    val errorGlow: Color,
    val info: Color,
    val infoGlow: Color,

    // Price indicators
    val bestPrice: Color,
    val bestPriceGlow: Color,
    val midPrice: Color,
    val highPrice: Color,

    // Additional price color aliases
    val priceLow: Color,        // For lowest prices (alias for bestPrice)
    val priceHigh: Color,       // For highest prices (alias for highPrice)

    // Deal and savings colors
    val bestDeal: Color,        // For best deal badges
    val savings: Color,         // For savings indicators

    // Standard Material3 color for tertiary
    val tertiary: Color,        // For tertiary color reference

    // Error color alias
    val errorRed: Color,        // Alias for error

    // Glass effects
    val glass: Color,
    val glassBorder: Color,
    val glassFrosted: Color,
    val glassFrostedBorder: Color,

    // Store brands
    val shufersal: Color,
    val shufersalGlow: Color,
    val victory: Color,
    val victoryGlow: Color,

    // Gradients
    val primaryGradient: GradientColors,
    val premiumGradient: GradientColors,
    val dealsGradient: GradientColors,
    val backgroundGradient: GradientColors
)

// Light theme extended colors
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

    // Price color aliases
    priceLow = ChampionCartColors.bestPrice,      // Green for lowest prices
    priceHigh = ChampionCartColors.highPrice,     // Red for highest prices

    // Deal and savings colors
    bestDeal = ChampionCartColors.successGreen,   // Green for best deals
    savings = ChampionCartColors.electricMint,    // Electric mint for savings

    // Tertiary color from Material3 standard
    tertiary = ChampionCartColors.neonCoral,      // Use neon coral as tertiary

    // Error color alias
    errorRed = ChampionCartColors.errorRed,       // Direct alias

    glass = ChampionCartColors.glassLight,
    glassBorder = ChampionCartColors.glassLightBorder,
    glassFrosted = ChampionCartColors.glassFrosted,
    glassFrostedBorder = ChampionCartColors.glassFrostedBorder,

    shufersal = ChampionCartColors.shufersalBrand,
    shufersalGlow = ChampionCartColors.shufersalGlow,
    victory = ChampionCartColors.victoryBrand,
    victoryGlow = ChampionCartColors.victoryGlow,

    primaryGradient = ChampionCartGradients.primaryAction,
    premiumGradient = ChampionCartGradients.premium,
    dealsGradient = ChampionCartGradients.deals,
    backgroundGradient = ChampionCartGradients.lightBackground
)

// Dark theme extended colors
val darkExtendedColors = lightExtendedColors.copy(
    glass = ChampionCartColors.glassDark,
    glassBorder = ChampionCartColors.glassDarkBorder,
    backgroundGradient = ChampionCartGradients.darkBackground
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

val LocalExtendedColors = staticCompositionLocalOf {
    lightExtendedColors
}

// Motion preferences
val LocalReduceMotion = staticCompositionLocalOf { false }
val LocalHapticsEnabled = staticCompositionLocalOf { true }

// Theme configuration data class
private data class ThemeConfig(
    val darkTheme: Boolean,
    val dynamicColor: Boolean,
    val highContrast: Boolean,
    val timeBasedColors: TimeBasedColors?
) {
    fun shouldUseDarkTheme(): Boolean = darkTheme || timeBasedColors?.isDark == true
}

// MAIN THEME FUNCTION - Cognitive Complexity: ~5
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

// EXTRACTED HELPER FUNCTIONS - Low Cognitive Complexity

@Composable
private fun getTimeBasedColorsIfEnabled(timeBasedTheme: Boolean): TimeBasedColors? {
    return if (timeBasedTheme) {                                    // +1
        val currentHour = remember { LocalTime.now().hour }
        remember(currentHour) { getTimeBasedColors(currentHour) }
    } else {
        null
    }
}

@Composable
private fun getColorScheme(config: ThemeConfig): ColorScheme {
    return when {                                                   // +1
        config.highContrast -> getHighContrastColorScheme(config)   // +1
        config.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {  // +1
            getDynamicColorScheme(config)
        }
        config.shouldUseDarkTheme() -> darkColorScheme              // +1
        else -> lightColorScheme(config.timeBasedColors)
    }
}

@Composable
private fun getHighContrastColorScheme(config: ThemeConfig): ColorScheme {
    return if (config.darkTheme) darkColorScheme else lightColorScheme()  // +1
}

@Composable
private fun getDynamicColorScheme(config: ThemeConfig): ColorScheme {
    val context = LocalContext.current
    return if (config.shouldUseDarkTheme()) {                      // +1
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
    return when {                                                   // +1
        config.highContrast -> highContrastExtendedColors          // +1
        config.shouldUseDarkTheme() -> darkExtendedColors          // +1
        else -> lightExtendedColors
    }
}

@Composable
private fun createAnimatedColorScheme(colorScheme: ColorScheme): ColorScheme {
    return ColorScheme(
        primary = animateColorAsState(
            targetValue = colorScheme.primary,
            animationSpec = tween(1000),
            label = "primary"
        ).value,
        onPrimary = animateColorAsState(
            targetValue = colorScheme.onPrimary,
            animationSpec = tween(1000),
            label = "onPrimary"
        ).value,
        primaryContainer = animateColorAsState(
            targetValue = colorScheme.primaryContainer,
            animationSpec = tween(1000),
            label = "primaryContainer"
        ).value,
        onPrimaryContainer = animateColorAsState(
            targetValue = colorScheme.onPrimaryContainer,
            animationSpec = tween(1000),
            label = "onPrimaryContainer"
        ).value,
        secondary = animateColorAsState(
            targetValue = colorScheme.secondary,
            animationSpec = tween(1000),
            label = "secondary"
        ).value,
        onSecondary = animateColorAsState(
            targetValue = colorScheme.onSecondary,
            animationSpec = tween(1000),
            label = "onSecondary"
        ).value,
        secondaryContainer = animateColorAsState(
            targetValue = colorScheme.secondaryContainer,
            animationSpec = tween(1000),
            label = "secondaryContainer"
        ).value,
        onSecondaryContainer = animateColorAsState(
            targetValue = colorScheme.onSecondaryContainer,
            animationSpec = tween(1000),
            label = "onSecondaryContainer"
        ).value,
        tertiary = animateColorAsState(
            targetValue = colorScheme.tertiary,
            animationSpec = tween(1000),
            label = "tertiary"
        ).value,
        onTertiary = animateColorAsState(
            targetValue = colorScheme.onTertiary,
            animationSpec = tween(1000),
            label = "onTertiary"
        ).value,
        tertiaryContainer = animateColorAsState(
            targetValue = colorScheme.tertiaryContainer,
            animationSpec = tween(1000),
            label = "tertiaryContainer"
        ).value,
        onTertiaryContainer = animateColorAsState(
            targetValue = colorScheme.onTertiaryContainer,
            animationSpec = tween(1000),
            label = "onTertiaryContainer"
        ).value,
        error = animateColorAsState(
            targetValue = colorScheme.error,
            animationSpec = tween(1000),
            label = "error"
        ).value,
        errorContainer = animateColorAsState(
            targetValue = colorScheme.errorContainer,
            animationSpec = tween(1000),
            label = "errorContainer"
        ).value,
        onError = animateColorAsState(
            targetValue = colorScheme.onError,
            animationSpec = tween(1000),
            label = "onError"
        ).value,
        onErrorContainer = animateColorAsState(
            targetValue = colorScheme.onErrorContainer,
            animationSpec = tween(1000),
            label = "onErrorContainer"
        ).value,
        background = animateColorAsState(
            targetValue = colorScheme.background,
            animationSpec = tween(1000),
            label = "background"
        ).value,
        onBackground = animateColorAsState(
            targetValue = colorScheme.onBackground,
            animationSpec = tween(1000),
            label = "onBackground"
        ).value,
        surface = animateColorAsState(
            targetValue = colorScheme.surface,
            animationSpec = tween(1000),
            label = "surface"
        ).value,
        onSurface = animateColorAsState(
            targetValue = colorScheme.onSurface,
            animationSpec = tween(1000),
            label = "onSurface"
        ).value,
        surfaceVariant = animateColorAsState(
            targetValue = colorScheme.surfaceVariant,
            animationSpec = tween(1000),
            label = "surfaceVariant"
        ).value,
        onSurfaceVariant = animateColorAsState(
            targetValue = colorScheme.onSurfaceVariant,
            animationSpec = tween(1000),
            label = "onSurfaceVariant"
        ).value,
        outline = animateColorAsState(
            targetValue = colorScheme.outline,
            animationSpec = tween(1000),
            label = "outline"
        ).value,
        outlineVariant = animateColorAsState(
            targetValue = colorScheme.outlineVariant,
            animationSpec = tween(1000),
            label = "outlineVariant"
        ).value,
        scrim = animateColorAsState(
            targetValue = colorScheme.scrim,
            animationSpec = tween(1000),
            label = "scrim"
        ).value,
        inverseSurface = animateColorAsState(
            targetValue = colorScheme.inverseSurface,
            animationSpec = tween(1000),
            label = "inverseSurface"
        ).value,
        inverseOnSurface = animateColorAsState(
            targetValue = colorScheme.inverseOnSurface,
            animationSpec = tween(1000),
            label = "inverseOnSurface"
        ).value,
        inversePrimary = animateColorAsState(
            targetValue = colorScheme.inversePrimary,
            animationSpec = tween(1000),
            label = "inversePrimary"
        ).value,
        surfaceTint = animateColorAsState(
            targetValue = colorScheme.surfaceTint,
            animationSpec = tween(1000),
            label = "surfaceTint"
        ).value
    )
}

@Composable
private fun createAnimatedExtendedColors(extendedColors: ExtendedColors): ExtendedColors {
    return ExtendedColors(
        electricMint = animateColorAsState(
            targetValue = extendedColors.electricMint,
            animationSpec = tween(1000),
            label = "electricMint"
        ).value,
        electricMintGlow = animateColorAsState(
            targetValue = extendedColors.electricMintGlow,
            animationSpec = tween(1000),
            label = "electricMintGlow"
        ).value,
        cosmicPurple = animateColorAsState(
            targetValue = extendedColors.cosmicPurple,
            animationSpec = tween(1000),
            label = "cosmicPurple"
        ).value,
        cosmicPurpleGlow = animateColorAsState(
            targetValue = extendedColors.cosmicPurpleGlow,
            animationSpec = tween(1000),
            label = "cosmicPurpleGlow"
        ).value,
        neonCoral = animateColorAsState(
            targetValue = extendedColors.neonCoral,
            animationSpec = tween(1000),
            label = "neonCoral"
        ).value,
        neonCoralGlow = animateColorAsState(
            targetValue = extendedColors.neonCoralGlow,
            animationSpec = tween(1000),
            label = "neonCoralGlow"
        ).value,
        success = animateColorAsState(
            targetValue = extendedColors.success,
            animationSpec = tween(1000),
            label = "success"
        ).value,
        successGlow = animateColorAsState(
            targetValue = extendedColors.successGlow,
            animationSpec = tween(1000),
            label = "successGlow"
        ).value,
        warning = animateColorAsState(
            targetValue = extendedColors.warning,
            animationSpec = tween(1000),
            label = "warning"
        ).value,
        warningGlow = animateColorAsState(
            targetValue = extendedColors.warningGlow,
            animationSpec = tween(1000),
            label = "warningGlow"
        ).value,
        error = animateColorAsState(
            targetValue = extendedColors.error,
            animationSpec = tween(1000),
            label = "error"
        ).value,
        errorGlow = animateColorAsState(
            targetValue = extendedColors.errorGlow,
            animationSpec = tween(1000),
            label = "errorGlow"
        ).value,
        info = animateColorAsState(
            targetValue = extendedColors.info,
            animationSpec = tween(1000),
            label = "info"
        ).value,
        infoGlow = animateColorAsState(
            targetValue = extendedColors.infoGlow,
            animationSpec = tween(1000),
            label = "infoGlow"
        ).value,
        bestPrice = animateColorAsState(
            targetValue = extendedColors.bestPrice,
            animationSpec = tween(1000),
            label = "bestPrice"
        ).value,
        bestPriceGlow = animateColorAsState(
            targetValue = extendedColors.bestPriceGlow,
            animationSpec = tween(1000),
            label = "bestPriceGlow"
        ).value,
        midPrice = animateColorAsState(
            targetValue = extendedColors.midPrice,
            animationSpec = tween(1000),
            label = "midPrice"
        ).value,
        highPrice = animateColorAsState(
            targetValue = extendedColors.highPrice,
            animationSpec = tween(1000),
            label = "highPrice"
        ).value,
        priceLow = animateColorAsState(
            targetValue = extendedColors.priceLow,
            animationSpec = tween(1000),
            label = "priceLow"
        ).value,
        priceHigh = animateColorAsState(
            targetValue = extendedColors.priceHigh,
            animationSpec = tween(1000),
            label = "priceHigh"
        ).value,
        bestDeal = animateColorAsState(
            targetValue = extendedColors.bestDeal,
            animationSpec = tween(1000),
            label = "bestDeal"
        ).value,
        savings = animateColorAsState(
            targetValue = extendedColors.savings,
            animationSpec = tween(1000),
            label = "savings"
        ).value,
        tertiary = animateColorAsState(
            targetValue = extendedColors.tertiary,
            animationSpec = tween(1000),
            label = "tertiary"
        ).value,
        errorRed = animateColorAsState(
            targetValue = extendedColors.errorRed,
            animationSpec = tween(1000),
            label = "errorRed"
        ).value,
        glass = extendedColors.glass,
        glassBorder = extendedColors.glassBorder,
        glassFrosted = extendedColors.glassFrosted,
        glassFrostedBorder = extendedColors.glassFrostedBorder,
        shufersal = extendedColors.shufersal,
        shufersalGlow = extendedColors.shufersalGlow,
        victory = extendedColors.victory,
        victoryGlow = extendedColors.victoryGlow,
        primaryGradient = extendedColors.primaryGradient,
        premiumGradient = extendedColors.premiumGradient,
        dealsGradient = extendedColors.dealsGradient,
        backgroundGradient = extendedColors.backgroundGradient
    )
}

@Composable
private fun ConfigureSystemUI(
    useDarkTheme: Boolean,
    colorScheme: ColorScheme
) {
    val view = LocalView.current
    val systemUiController = rememberSystemUiController()

    if (!view.isInEditMode) {                                       // +1
        SideEffect {
            configureWindowInsets(view, useDarkTheme, colorScheme)
            configureSystemBars(systemUiController, useDarkTheme)
        }
    }
}

private fun configureWindowInsets(
    view: View,
    useDarkTheme: Boolean,
    colorScheme: ColorScheme
) {
    val activity = view.context as? Activity
    val window = activity?.window
    window?.let {                                                   // +1
        WindowCompat.setDecorFitsSystemWindows(it, false)
        it.statusBarColor = Color.Transparent.toArgb()
        it.navigationBarColor = getNavigationBarColor(useDarkTheme, colorScheme)
    }
}

private fun getNavigationBarColor(useDarkTheme: Boolean, colorScheme: ColorScheme): Int {
    return if (useDarkTheme) {                                      // +1
        Color.Black.copy(alpha = 0.3f).compositeOver(colorScheme.background).toArgb()
    } else {
        Color.Black.copy(alpha = 0.1f).compositeOver(colorScheme.background).toArgb()
    }
}

private fun configureSystemBars(
    systemUiController: SystemUiController,
    useDarkTheme: Boolean
) {
    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = !useDarkTheme
    )
    systemUiController.setNavigationBarColor(
        color = Color.Transparent,
        darkIcons = !useDarkTheme,
        navigationBarContrastEnforced = false
    )
}

// Extension properties to access extended colors and preferences
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current

val MaterialTheme.reduceMotion: Boolean
    @Composable
    get() = LocalReduceMotion.current

val MaterialTheme.hapticsEnabled: Boolean
    @Composable
    get() = LocalHapticsEnabled.current

/**
 * Preview helper for different theme variations
 */
@Composable
fun PreviewTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    ChampionCartTheme(
        darkTheme = darkTheme,
        dynamicColor = false,
        content = content
    )
}