package com.example.championcart.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Champion Cart - Electric Harmony Color System
 * Modern glassmorphic colors with Hebrew-first accessibility
 * Following Material Design 3 with custom brand extensions
 */

// Brand Core Colors - Electric Harmony Palette
object BrandColors {
    // Primary Brand Colors
    val ElectricMint = Color(0xFF00D9A3)
    val ElectricMintVariant = Color(0xFF00E676)
    val CosmicPurple = Color(0xFF7B3FF2)
    val CosmicPurpleVariant = Color(0xFF9C27B0)
    val DeepNavy = Color(0xFF0A0E27)
    val DeepNavyVariant = Color(0xFF1A1F3A)

    // Glassmorphic Glass Colors
    val GlassLight = Color(0x12FFFFFF)      // 7% white for light mode glass
    val GlassMedium = Color(0x1FFFFFFF)     // 12% white for medium glass
    val GlassHeavy = Color(0x33FFFFFF)      // 20% white for heavy glass
    val GlassDark = Color(0x0F000000)       // 6% black for dark mode glass
    val GlassDarkMedium = Color(0x1A000000) // 10% black for dark mode glass

    // Gradient Colors
    val GradientStart = Color(0xFF00D9A3)
    val GradientMiddle = Color(0xFF7B3FF2)
    val GradientEnd = Color(0xFF0A0E27)

    // Semantic Colors
    val Success = Color(0xFF00E676)
    val Warning = Color(0xFFFFB300)
    val Error = Color(0xFFFF5252)
    val Info = Color(0xFF448AFF)

    // Price Colors
    val BestPrice = Color(0xFF00E676)
    val MidPrice = Color(0xFFFFB300)
    val HighPrice = Color(0xFFFF5252)
}

// Time-Based Color Variations
object TimeBasedColors {
    // Morning (6am-12pm) - Warm & Energetic
    val MorningPrimary = Color(0xFFFF9066)
    val MorningSecondary = Color(0xFFFFD54F)
    val MorningTertiary = Color(0xFFFF8A65)

    // Afternoon (12pm-6pm) - Electric & Vibrant
    val AfternoonPrimary = BrandColors.ElectricMint
    val AfternoonSecondary = BrandColors.CosmicPurple
    val AfternoonTertiary = Color(0xFF00BCD4)

    // Evening (6pm-12am) - Deep & Cosmic
    val EveningPrimary = Color(0xFF9C27B0)
    val EveningSecondary = Color(0xFF673AB7)
    val EveningTertiary = Color(0xFF3F51B5)

    // Night (12am-6am) - Dark & Calm
    val NightPrimary = Color(0xFF455A64)
    val NightSecondary = Color(0xFF607D8B)
    val NightTertiary = Color(0xFF78909C)
}

// Material3 Light Color Scheme - Electric Harmony
val LightColorScheme = lightColorScheme(
    // Primary Colors - Electric Mint
    primary = BrandColors.ElectricMint,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7F1),
    onPrimaryContainer = Color(0xFF003D33),

    // Secondary Colors - Cosmic Purple
    secondary = BrandColors.CosmicPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1BEE7),
    onSecondaryContainer = Color(0xFF2E0A33),

    // Tertiary Colors - Balanced
    tertiary = Color(0xFF00BCD4),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2EBF2),
    onTertiaryContainer = Color(0xFF002B2E),

    // Background & Surface
    background = Color(0xFFF8F9FF),
    onBackground = BrandColors.DeepNavy,
    surface = Color.White,
    onSurface = BrandColors.DeepNavy,
    surfaceVariant = Color(0xFFF5F5F7),
    onSurfaceVariant = Color(0xFF5A6178),

    // Surface Containers
    surfaceContainer = Color(0xFFF8F9FF),
    surfaceContainerHigh = Color(0xFFF0F2F8),
    surfaceContainerHighest = Color(0xFFE8EBF2),
    surfaceContainerLow = Color(0xFFFCFCFD),
    surfaceContainerLowest = Color.White,

    // Outline & Borders
    outline = Color(0xFFCDD1D9),
    outlineVariant = Color(0xFFE5E7EB),

    // Inverse Colors
    inverseSurface = Color(0xFF2F3349),
    inverseOnSurface = Color(0xFFF0F2F8),
    inversePrimary = Color(0xFF80F5D4),

    // Semantic Colors
    error = BrandColors.Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFF5F0000),

    // Scrim
    scrim = Color(0x66000000)
)

// Material3 Dark Color Scheme - Electric Harmony
val DarkColorScheme = darkColorScheme(
    // Primary Colors - Electric Mint (adjusted for dark)
    primary = Color(0xFF80F5D4),
    onPrimary = Color(0xFF003D33),
    primaryContainer = Color(0xFF005B4C),
    onPrimaryContainer = Color(0xFFB3F5E6),

    // Secondary Colors - Cosmic Purple (adjusted for dark)
    secondary = Color(0xFFCE93D8),
    onSecondary = Color(0xFF3E1A47),
    secondaryContainer = Color(0xFF5A2D66),
    onSecondaryContainer = Color(0xFFE1BEE7),

    // Tertiary Colors
    tertiary = Color(0xFF4DD0E1),
    onTertiary = Color(0xFF002B2E),
    tertiaryContainer = Color(0xFF004B52),
    onTertiaryContainer = Color(0xFFB2EBF2),

    // Background & Surface
    background = BrandColors.DeepNavy,
    onBackground = Color(0xFFE8EBF2),
    surface = BrandColors.DeepNavyVariant,
    onSurface = Color(0xFFE8EBF2),
    surfaceVariant = Color(0xFF2A2F4A),
    onSurfaceVariant = Color(0xFFB8BCC8),

    // Surface Containers
    surfaceContainer = Color(0xFF1A1F3A),
    surfaceContainerHigh = Color(0xFF2A2F4A),
    surfaceContainerHighest = Color(0xFF353A54),
    surfaceContainerLow = Color(0xFF0F1429),
    surfaceContainerLowest = Color(0xFF0A0E27),

    // Outline & Borders
    outline = Color(0xFF4A4F66),
    outlineVariant = Color(0xFF2A2F4A),

    // Inverse Colors
    inverseSurface = Color(0xFFE8EBF2),
    inverseOnSurface = Color(0xFF2F3349),
    inversePrimary = BrandColors.ElectricMint,

    // Semantic Colors
    error = Color(0xFFFF8A80),
    onError = Color(0xFF5F0000),
    errorContainer = Color(0xFF8C0000),
    onErrorContainer = Color(0xFFFFDAD4),

    // Scrim
    scrim = Color(0x80000000)
)

// High Contrast Light Color Scheme
val HighContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF003D33),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF00796B),
    onPrimaryContainer = Color.White,

    secondary = Color(0xFF2E0A33),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF6A1B9A),
    onSecondaryContainer = Color.White,

    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,

    outline = Color.Black,
    outlineVariant = Color(0xFF666666),

    error = Color(0xFFB71C1C),
    onError = Color.White
)

// High Contrast Dark Color Scheme
val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color(0xFFB3F5E6),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF80F5D4),
    onPrimaryContainer = Color.Black,

    secondary = Color(0xFFE1BEE7),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFCE93D8),
    onSecondaryContainer = Color.Black,

    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,

    outline = Color.White,
    outlineVariant = Color(0xFFCCCCCC),

    error = Color(0xFFFF8A80),
    onError = Color.Black
)

// Helper functions for color generation
@Composable
fun getTimeBasedPrimary(hour: Int): Color {
    return when (hour) {
        in 6..11 -> TimeBasedColors.MorningPrimary
        in 12..17 -> TimeBasedColors.AfternoonPrimary
        in 18..23 -> TimeBasedColors.EveningPrimary
        else -> TimeBasedColors.NightPrimary
    }
}

@Composable
fun getGlassmorphicOverlay(isLight: Boolean, opacity: Float = 0.08f): Color {
    return if (isLight) {
        Color.White.copy(alpha = opacity)
    } else {
        Color.White.copy(alpha = opacity * 0.7f)
    }
}