package com.example.championcart.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Champion Cart Color System
 * Electric Harmony Design Language
 */

object ChampionCartColors {
    // Primary Brand Colors - Electric Harmony
    object Brand {
        val ElectricMint = Color(0xFF00D9A3)
        val ElectricMintLight = Color(0xFF4DFFCE)
        val ElectricMintDark = Color(0xFF00A67C)

        val CosmicPurple = Color(0xFF7B3FF2)
        val CosmicPurpleLight = Color(0xFF9D6FFF)
        val CosmicPurpleDark = Color(0xFF5A2DB8)

        val NeonCoral = Color(0xFFFF6B9D)
        val NeonCoralLight = Color(0xFFFF8FB3)
        val NeonCoralDark = Color(0xFFE44D7A)

        val DeepNavy = Color(0xFF0A0E27)
        val DeepNavyVariant = Color(0xFF1A1F3A)
    }

    // Glassmorphic Overlays
    object Glass {
        val Light = Color(0x14FFFFFF)      // 8% white
        val Medium = Color(0x29FFFFFF)     // 16% white
        val Heavy = Color(0x33FFFFFF)      // 20% white
        val Ultra = Color(0x4DFFFFFF)      // 30% white

        val DarkLight = Color(0x14000000)  // 8% black
        val DarkMedium = Color(0x29000000) // 16% black
        val DarkHeavy = Color(0x33000000)  // 20% black
    }

    // Semantic Colors
    object Semantic {
        val Success = Color(0xFF00E676)
        val SuccessContainer = Color(0xFFE8F5E9)

        val Warning = Color(0xFFFFB300)
        val WarningContainer = Color(0xFFFFF8E1)

        val Error = Color(0xFFFF5252)
        val ErrorContainer = Color(0xFFFFEBEE)

        val Info = Color(0xFF448AFF)
        val InfoContainer = Color(0xFFE3F2FD)
    }

    // Price Comparison
    object Price {
        val Best = Color(0xFF00E676)
        val BestGlow = Color(0x5400E676)

        val Mid = Color(0xFFFFB300)
        val MidGlow = Color(0x54FFB300)

        val High = Color(0xFFFF5252)
        val HighGlow = Color(0x54FF5252)
    }

    // Store Chains
    object Store {
        val Shufersal = Color(0xFFE53935)
        val RamiLevi = Color(0xFF1976D2)
        val Victory = Color(0xFFFFD600)
        val Mega = Color(0xFFFF6F00)
        val OsherAd = Color(0xFF43A047)
        val Coop = Color(0xFF8E24AA)
    }

    // Product Categories
    object Category {
        val Dairy = Color(0xFF64B5F6)
        val Meat = Color(0xFFEF5350)
        val Produce = Color(0xFF66BB6A)
        val Bakery = Color(0xFFFFD54F)
        val Frozen = Color(0xFF42A5F5)
        val Household = Color(0xFFAB47BC)
        val Kosher = Color(0xFF5C6BC0)
        val Organic = Color(0xFF9CCC65)
    }

    // Time-Based Themes
    object Morning {
        val primary = Color(0xFFFFAB91)    // Peach
        val secondary = Color(0xFFFFD54F)  // Warm Yellow
        val tertiary = Color(0xFFFF8A65)   // Light Orange
    }

    object Evening {
        val primary = Brand.CosmicPurple
        val secondary = Color(0xFF673AB7)  // Deep Purple
        val tertiary = Color(0xFF3F51B5)   // Indigo
    }

    // Gradient Components
    object Gradient {
        val electricHarmony = listOf(
            Brand.ElectricMint,
            Brand.CosmicPurple,
            Brand.NeonCoral
        )

        val glass = listOf(
            Glass.Light,
            Glass.Medium
        )

        val success = listOf(
            Semantic.Success.copy(alpha = 0.1f),
            Semantic.Success.copy(alpha = 0.05f)
        )
    }
}

// Material3 Color Schemes
val lightColorScheme = lightColorScheme(
    primary = ChampionCartColors.Brand.ElectricMint,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7F1),
    onPrimaryContainer = Color(0xFF003D33),

    secondary = ChampionCartColors.Brand.CosmicPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF1A0033),

    tertiary = ChampionCartColors.Brand.NeonCoral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4EC),
    onTertiaryContainer = Color(0xFF3D0017),

    background = Color(0xFFFAFDFD),
    onBackground = Color(0xFF191C1D),
    surface = Color.White,
    onSurface = Color(0xFF191C1D),
    surfaceVariant = Color(0xFFF2F4F4),
    onSurfaceVariant = Color(0xFF3F4949),

    error = ChampionCartColors.Semantic.Error,
    onError = Color.White,
    errorContainer = ChampionCartColors.Semantic.ErrorContainer,
    onErrorContainer = Color(0xFF410002),

    outline = Color(0xFF6F7979),
    outlineVariant = Color(0xFFBFC9C9),
    inverseSurface = Color(0xFF2E3132),
    inverseOnSurface = Color(0xFFF0F2F2),
    inversePrimary = Color(0xFF4DFFCE),

    surfaceTint = ChampionCartColors.Brand.ElectricMint,
    scrim = Color(0xFF000000)
)

val darkColorScheme = darkColorScheme(
    primary = ChampionCartColors.Brand.ElectricMintLight,
    onPrimary = Color(0xFF003A2F),
    primaryContainer = Color(0xFF005142),
    onPrimaryContainer = Color(0xFF70F7D7),

    secondary = ChampionCartColors.Brand.CosmicPurpleLight,
    onSecondary = Color(0xFF2B0052),
    secondaryContainer = Color(0xFF432C7A),
    onSecondaryContainer = Color(0xFFE4DBFF),

    tertiary = ChampionCartColors.Brand.NeonCoralLight,
    onTertiary = Color(0xFF5D1133),
    tertiaryContainer = Color(0xFF7B2949),
    onTertiaryContainer = Color(0xFFFFD9E3),

    background = ChampionCartColors.Brand.DeepNavy,
    onBackground = Color(0xFFE1E3E3),
    surface = ChampionCartColors.Brand.DeepNavyVariant,
    onSurface = Color(0xFFE1E3E3),
    surfaceVariant = Color(0xFF3F4949),
    onSurfaceVariant = Color(0xFFBFC9C9),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF899393),
    outlineVariant = Color(0xFF3F4949),
    inverseSurface = Color(0xFFE1E3E3),
    inverseOnSurface = Color(0xFF1A1C1C),
    inversePrimary = ChampionCartColors.Brand.ElectricMint,

    surfaceTint = ChampionCartColors.Brand.ElectricMintLight,
    scrim = Color(0xFF000000)
)

val highContrastLightColorScheme = lightColorScheme.copy(
    primary = ChampionCartColors.Brand.ElectricMintDark,
    secondary = ChampionCartColors.Brand.CosmicPurpleDark,
    tertiary = ChampionCartColors.Brand.NeonCoralDark,
    outline = Color.Black,
    outlineVariant = Color(0xFF666666)
)

val highContrastDarkColorScheme = darkColorScheme.copy(
    primary = Color(0xFFB3FFE6),
    secondary = Color(0xFFE1BEE7),
    tertiary = Color(0xFFFFD6E6),
    outline = Color.White,
    outlineVariant = Color(0xFFCCCCCC),
    surface = Color.Black,
    background = Color.Black
)