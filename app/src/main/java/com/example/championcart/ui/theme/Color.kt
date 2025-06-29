package com.example.championcart.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Champion Cart Color System - Simplified
 * Electric Harmony Design Language
 */

// Primary Brand Colors
object BrandColors {
    val ElectricMint = Color(0xFF00D9A3)
    val CosmicPurple = Color(0xFF7B3FF2)
    val NeonCoral = Color(0xFFFF6B9D)
    val DeepNavy = Color(0xFF0A0E27)
}

// Semantic Colors
object SemanticColors {
    val Success = Color(0xFF00E676)
    val Warning = Color(0xFFFFB300)
    val Error = Color(0xFFFF5252)
    val Info = Color(0xFF448AFF)
}

// Price Comparison Colors
object PriceColors {
    val Best = Color(0xFF00E676)
    val Mid = Color(0xFFFFB300)
    val High = Color(0xFFFF5252)
}

// Store Brand Colors
object StoreColors {
    val Shufersal = Color(0xFFE53935)
    val RamiLevi = Color(0xFF1976D2)
    val Victory = Color(0xFFFFD600)
    val Mega = Color(0xFFFF6F00)
    val OsherAd = Color(0xFF43A047)
    val Coop = Color(0xFF8E24AA)
}

// Glass Effects
object GlassColors {
    val Light = Color(0x14FFFFFF)      // 8% white
    val Medium = Color(0x29FFFFFF)     // 16% white
    val Heavy = Color(0x33FFFFFF)      // 20% white

    val DarkLight = Color(0x14000000)  // 8% black
    val DarkMedium = Color(0x29000000) // 16% black
}

// Material3 Color Schemes
val LightColorScheme = lightColorScheme(
    primary = BrandColors.ElectricMint,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7F1),
    onPrimaryContainer = Color(0xFF003D33),

    secondary = BrandColors.CosmicPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF1A0033),

    tertiary = BrandColors.NeonCoral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4EC),
    onTertiaryContainer = Color(0xFF3D0017),

    background = Color(0xFFFAFDFD),
    onBackground = Color(0xFF191C1D),
    surface = Color.White,
    onSurface = Color(0xFF191C1D),
    surfaceVariant = Color(0xFFF2F4F4),
    onSurfaceVariant = Color(0xFF3F4949),

    error = SemanticColors.Error,
    errorContainer = Color(0xFFFFEBEE),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),

    outline = Color(0xFF6F7979),
    outlineVariant = Color(0xFFBFC9C9)
)

val DarkColorScheme = darkColorScheme(
    primary = BrandColors.ElectricMint,
    onPrimary = Color(0xFF003A2F),
    primaryContainer = Color(0xFF005142),
    onPrimaryContainer = Color(0xFF70F7D7),

    secondary = BrandColors.CosmicPurple,
    onSecondary = Color(0xFF2B0052),
    secondaryContainer = Color(0xFF432C7A),
    onSecondaryContainer = Color(0xFFE4DBFF),

    tertiary = BrandColors.NeonCoral,
    onTertiary = Color(0xFF5D1133),
    tertiaryContainer = Color(0xFF7B2949),
    onTertiaryContainer = Color(0xFFFFD9E3),

    background = BrandColors.DeepNavy,
    onBackground = Color(0xFFE1E3E3),
    surface = Color(0xFF1A1F3A),
    onSurface = Color(0xFFE1E3E3),
    surfaceVariant = Color(0xFF3F4949),
    onSurfaceVariant = Color(0xFFBFC9C9),

    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF899393),
    outlineVariant = Color(0xFF3F4949)
)