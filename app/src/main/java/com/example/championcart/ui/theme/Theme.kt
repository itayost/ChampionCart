package com.example.championcart.ui.theme

import android.app.Activity
import android.os.Build
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

@Composable
fun ChampionCartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    highContrast: Boolean = false,
    reduceMotion: Boolean = false,
    hapticsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    // Get current time for dynamic theming
    val currentHour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalTime.now().hour
    } else {
        12 // Default to afternoon theme
    }

    val timeBasedColors = getTimeBasedColors(currentHour)

    // Determine if we should use dark theme based on time or system
    val useDarkTheme = darkTheme || timeBasedColors.isDark

    // Animate color transitions
    val animatedColorScheme = when {
        highContrast -> {
            // High contrast mode - no gradients or transparency
            lightColorScheme().copy(
                primary = Color.Black,
                secondary = Color.Black,
                tertiary = Color.Black,
                background = Color.White,
                surface = Color.White,
                error = Color.Black
            )
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> darkColorScheme
        else -> lightColorScheme(timeBasedColors)
    }

    // Extended colors with animation
    val targetExtendedColors = when {
        highContrast -> highContrastExtendedColors
        useDarkTheme -> darkExtendedColors
        else -> lightExtendedColors
    }

    // Animate extended colors
    val animatedExtendedColors = ExtendedColors(
        electricMint = animateColorAsState(
            targetValue = targetExtendedColors.electricMint,
            animationSpec = tween(600),
            label = "electricMint"
        ).value,
        electricMintGlow = targetExtendedColors.electricMintGlow,
        cosmicPurple = animateColorAsState(
            targetValue = targetExtendedColors.cosmicPurple,
            animationSpec = tween(600),
            label = "cosmicPurple"
        ).value,
        cosmicPurpleGlow = targetExtendedColors.cosmicPurpleGlow,
        neonCoral = animateColorAsState(
            targetValue = targetExtendedColors.neonCoral,
            animationSpec = tween(600),
            label = "neonCoral"
        ).value,
        neonCoralGlow = targetExtendedColors.neonCoralGlow,
        success = targetExtendedColors.success,
        successGlow = targetExtendedColors.successGlow,
        warning = targetExtendedColors.warning,
        warningGlow = targetExtendedColors.warningGlow,
        error = targetExtendedColors.error,
        errorGlow = targetExtendedColors.errorGlow,
        info = targetExtendedColors.info,
        infoGlow = targetExtendedColors.infoGlow,
        bestPrice = targetExtendedColors.bestPrice,
        bestPriceGlow = targetExtendedColors.bestPriceGlow,
        midPrice = targetExtendedColors.midPrice,
        highPrice = targetExtendedColors.highPrice,
        glass = targetExtendedColors.glass,
        glassBorder = targetExtendedColors.glassBorder,
        glassFrosted = targetExtendedColors.glassFrosted,
        glassFrostedBorder = targetExtendedColors.glassFrostedBorder,
        shufersal = targetExtendedColors.shufersal,
        shufersalGlow = targetExtendedColors.shufersalGlow,
        victory = targetExtendedColors.victory,
        victoryGlow = targetExtendedColors.victoryGlow,
        primaryGradient = targetExtendedColors.primaryGradient,
        premiumGradient = targetExtendedColors.premiumGradient,
        dealsGradient = targetExtendedColors.dealsGradient,
        backgroundGradient = targetExtendedColors.backgroundGradient
    )

    // System UI Controller for edge-to-edge
    val systemUiController = rememberSystemUiController()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                // Enable edge-to-edge
                WindowCompat.setDecorFitsSystemWindows(it, false)

                // Set status bar color
                it.statusBarColor = Color.Transparent.toArgb()

                // Set navigation bar color with scrim for contrast
                it.navigationBarColor = if (useDarkTheme) {
                    Color.Black.copy(alpha = 0.3f).compositeOver(animatedColorScheme.background).toArgb()
                } else {
                    Color.Black.copy(alpha = 0.1f).compositeOver(animatedColorScheme.background).toArgb()
                }
            }

            // Set system bar appearance
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
    }

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

// Extension property to access extended colors
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current

// Extension property to check motion preferences
val MaterialTheme.reduceMotion: Boolean
    @Composable
    get() = LocalReduceMotion.current

// Extension property to check haptics preferences
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