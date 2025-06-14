package com.example.championcart.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// Light Color Scheme
private val lightColorScheme = lightColorScheme(
    primary = ChampionCartColors.primary,           // Forest Green
    onPrimary = ChampionCartColors.onPrimary,
    primaryContainer = ChampionCartColors.primaryLight,
    onPrimaryContainer = ChampionCartColors.primaryDark,

    secondary = ChampionCartColors.secondary,        // Trust Blue
    onSecondary = ChampionCartColors.onSecondary,
    secondaryContainer = ChampionCartColors.secondaryLight,
    onSecondaryContainer = ChampionCartColors.secondaryDark,

    tertiary = ChampionCartColors.tertiary,          // Action Orange
    onTertiary = ChampionCartColors.onTertiary,
    tertiaryContainer = ChampionCartColors.tertiaryLight,
    onTertiaryContainer = ChampionCartColors.tertiaryDark,

    error = ChampionCartColors.error,
    errorContainer = ChampionCartColors.errorContainer,
    onError = ChampionCartColors.onError,
    onErrorContainer = ChampionCartColors.onErrorContainer,

    background = ChampionCartColors.background,
    onBackground = ChampionCartColors.onBackground,

    surface = ChampionCartColors.surface,
    onSurface = ChampionCartColors.onSurface,
    surfaceVariant = ChampionCartColors.surfaceVariant,
    onSurfaceVariant = ChampionCartColors.onSurfaceVariant,

    outline = ChampionCartColors.outline,
    outlineVariant = ChampionCartColors.outlineVariant,
    scrim = ChampionCartColors.scrim,

    inverseSurface = ChampionCartColors.onSurface,
    inverseOnSurface = ChampionCartColors.surface,
    inversePrimary = ChampionCartColors.primaryLight
)

// Dark Color Scheme
private val darkColorScheme = darkColorScheme(
    primary = ChampionCartColors.darkPrimary,
    onPrimary = ChampionCartColors.darkOnPrimary,
    primaryContainer = ChampionCartColors.darkPrimaryContainer,
    onPrimaryContainer = ChampionCartColors.darkOnPrimaryContainer,

    secondary = ChampionCartColors.darkSecondary,
    onSecondary = ChampionCartColors.darkOnSecondary,
    secondaryContainer = ChampionCartColors.darkSecondaryContainer,
    onSecondaryContainer = ChampionCartColors.darkOnSecondaryContainer,

    tertiary = ChampionCartColors.darkTertiary,
    onTertiary = ChampionCartColors.darkOnTertiary,
    tertiaryContainer = ChampionCartColors.darkTertiaryContainer,
    onTertiaryContainer = ChampionCartColors.darkOnTertiaryContainer,

    error = ChampionCartColors.darkError,
    errorContainer = ChampionCartColors.darkErrorContainer,
    onError = ChampionCartColors.darkOnError,
    onErrorContainer = ChampionCartColors.darkOnErrorContainer,

    background = ChampionCartColors.darkBackground,
    onBackground = ChampionCartColors.darkOnBackground,

    surface = ChampionCartColors.darkSurface,
    onSurface = ChampionCartColors.darkOnSurface,
    surfaceVariant = ChampionCartColors.darkSurfaceVariant,
    onSurfaceVariant = ChampionCartColors.darkOnSurfaceVariant,

    outline = ChampionCartColors.darkOutline,
    outlineVariant = ChampionCartColors.darkOutlineVariant,
    scrim = ChampionCartColors.scrim,

    inverseSurface = ChampionCartColors.darkOnSurface,
    inverseOnSurface = ChampionCartColors.darkSurface,
    inversePrimary = ChampionCartColors.primary
)

// Extended colors data class
data class ExtendedColors(
    val savings: Color,
    val savingsLight: Color,
    val bestDeal: Color,
    val compare: Color,
    val priceHigh: Color,
    val priceLow: Color,
    val success: Color,
    val successContainer: Color,
    val onSuccess: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val onWarning: Color,
    val onWarningContainer: Color,
    val info: Color,
    val infoContainer: Color,
    val onInfo: Color,
    val onInfoContainer: Color,
    val shufersal: Color,
    val victory: Color,
    val ramiLevy: Color,
    val mega: Color,
    val tertiary: Color,
    val tertiaryContainer: Color,
    val onTertiary: Color,
    val onTertiaryContainer: Color
)

val lightExtendedColors = ExtendedColors(
    savings = ChampionCartColors.savings,
    savingsLight = ChampionCartColors.savingsLight,
    bestDeal = ChampionCartColors.bestDeal,
    compare = ChampionCartColors.compare,
    priceHigh = ChampionCartColors.priceHigh,
    priceLow = ChampionCartColors.priceLow,
    success = ChampionCartColors.success,
    successContainer = ChampionCartColors.successContainer,
    onSuccess = ChampionCartColors.onSuccess,
    onSuccessContainer = ChampionCartColors.onSuccessContainer,
    warning = ChampionCartColors.warning,
    warningContainer = ChampionCartColors.warningContainer,
    onWarning = ChampionCartColors.onWarning,
    onWarningContainer = ChampionCartColors.onWarningContainer,
    info = ChampionCartColors.info,
    infoContainer = ChampionCartColors.infoContainer,
    onInfo = ChampionCartColors.onInfo,
    onInfoContainer = ChampionCartColors.onInfoContainer,
    shufersal = ChampionCartColors.shufersal,
    victory = ChampionCartColors.victory,
    ramiLevy = ChampionCartColors.ramiLevy,
    mega = ChampionCartColors.mega,
    tertiary = ChampionCartColors.tertiary,
    tertiaryContainer = ChampionCartColors.tertiaryLight,
    onTertiary = ChampionCartColors.onTertiary,
    onTertiaryContainer = ChampionCartColors.tertiaryDark
)

val darkExtendedColors = ExtendedColors(
    savings = Color(0xFF69F0AE),
    savingsLight = Color(0xFF69F0AE),
    bestDeal = Color(0xFFFFD54F),
    compare = Color(0xFF64B5F6),
    priceHigh = Color(0xFFFF8A80),
    priceLow = Color(0xFF69F0AE),
    success = Color(0xFF69F0AE),
    successContainer = Color(0xFF003A0F),
    onSuccess = Color(0xFF003A0F),
    onSuccessContainer = Color(0xFF69F0AE),
    warning = Color(0xFFFFAB40),
    warningContainer = Color(0xFF5A3100),
    onWarning = Color(0xFF3E2000),
    onWarningContainer = Color(0xFFFFD699),
    info = Color(0xFF4FC3F7),
    infoContainer = Color(0xFF004A77),
    onInfo = Color(0xFF003355),
    onInfoContainer = Color(0xFFC5E4FF),
    shufersal = Color(0xFF4D94FF),
    victory = Color(0xFFFF6B6B),
    ramiLevy = Color(0xFFFF8C42),
    mega = Color(0xFF66D966),
    tertiary = ChampionCartColors.darkTertiary,
    tertiaryContainer = ChampionCartColors.darkTertiaryContainer,
    onTertiary = ChampionCartColors.darkOnTertiary,
    onTertiaryContainer = ChampionCartColors.darkOnTertiaryContainer
)

val LocalExtendedColors = staticCompositionLocalOf {
    lightExtendedColors
}

@Composable
fun ChampionCartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val extendedColors = if (darkTheme) darkExtendedColors else lightExtendedColors

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    // System UI Controller for edge-to-edge
    val systemUiController = rememberSystemUiController()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            // Set system bars to be transparent for edge-to-edge
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = !darkTheme
            )

            systemUiController.setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = !darkTheme,
                navigationBarContrastEnforced = false
            )
        }
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
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