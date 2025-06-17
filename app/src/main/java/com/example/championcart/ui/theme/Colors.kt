package com.example.championcart.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
    val ElectricMintLight = Color(0xFF4DFFCE)
    val ElectricMintDark = Color(0xFF00A67C)
    val ElectricMintVariant = Color(0xFF00E676)

    val CosmicPurple = Color(0xFF7B3FF2)
    val CosmicPurpleLight = Color(0xFF9D6FFF)
    val CosmicPurpleDark = Color(0xFF5A2DB8)
    val CosmicPurpleVariant = Color(0xFF9C27B0)

    // NEW: Neon Coral - Third primary brand color
    val NeonCoral = Color(0xFFFF6B9D)
    val NeonCoralLight = Color(0xFFFF8FB3)
    val NeonCoralDark = Color(0xFFE44D7A)

    val DeepNavy = Color(0xFF0A0E27)
    val DeepNavyVariant = Color(0xFF1A1F3A)

    // Glassmorphic Glass Colors - FIXED: Corrected opacity values
    val GlassLight = Color(0x14FFFFFF)      // 8% white for light mode glass
    val GlassMedium = Color(0x29FFFFFF)     // 16% white for medium glass
    val GlassHeavy = Color(0x33FFFFFF)      // 20% white for heavy glass
    val GlassDark = Color(0x14000000)       // 8% black for dark mode glass
    val GlassDarkMedium = Color(0x29000000) // 16% black for dark mode glass

    // Gradient Colors
    val GradientStart = Color(0xFF00D9A3)
    val GradientMiddle = Color(0xFF7B3FF2)
    val GradientEnd = Color(0xFFFF6B9D)  // Updated to include Neon Coral

    // Semantic Colors
    val Success = Color(0xFF00E676)
    val Warning = Color(0xFFFFB300)
    val Error = Color(0xFFFF5252)
    val Info = Color(0xFF448AFF)

    // Price Colors
    val BestPrice = Color(0xFF00E676)
    val BestPriceGlow = Color(0x5400E676)  // 33% opacity glow
    val MidPrice = Color(0xFFFFB300)
    val HighPrice = Color(0xFFFF5252)

    // Glow Effects - NEW
    val ElectricMintGlow = Color(0x5400D9A3)  // 33% opacity
    val CosmicPurpleGlow = Color(0x547B3FF2)  // 33% opacity
    val NeonCoralGlow = Color(0x54FF6B9D)     // 33% opacity
}

// Time-Based Color Variations
object TimeBasedColors {
    // Morning (6am-12pm) - Warm & Energetic with peach accents
    val MorningPrimary = Color(0xFFFFAB91)  // Peach
    val MorningSecondary = Color(0xFFFFD54F)
    val MorningTertiary = Color(0xFFFF8A65)

    // Afternoon (12pm-6pm) - Electric & Vibrant with electric mint
    val AfternoonPrimary = BrandColors.ElectricMint
    val AfternoonSecondary = BrandColors.CosmicPurple
    val AfternoonTertiary = Color(0xFF00BCD4)

    // Evening (6pm-12am) - Deep purples with cosmic themes
    val EveningPrimary = BrandColors.CosmicPurple
    val EveningSecondary = Color(0xFF673AB7)
    val EveningTertiary = Color(0xFF3F51B5)

    // Night (12am-6am) - Dark mode auto-activated
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

    // Tertiary Colors - Neon Coral
    tertiary = BrandColors.NeonCoral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4EC),
    onTertiaryContainer = Color(0xFF3F0019),

    // Background & Surface
    background = Color(0xFFF8F9FF),  // Subtle blue tint as per design system
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

    // Tertiary Colors - Neon Coral (adjusted for dark)
    tertiary = Color(0xFFFFB3CC),
    onTertiary = Color(0xFF5F1F33),
    tertiaryContainer = Color(0xFF7B2C49),
    onTertiaryContainer = Color(0xFFFFE4EC),

    // Background & Surface
    background = BrandColors.DeepNavy,  // #0A0E27
    onBackground = Color(0xFFE8EBF2),
    surface = BrandColors.DeepNavyVariant,  // #1A1F3A
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
val HighContrastLightColorScheme = LightColorScheme.copy(
    primary = BrandColors.ElectricMintDark,
    secondary = BrandColors.CosmicPurpleDark,
    tertiary = BrandColors.NeonCoralDark,
    outline = Color.Black,
    outlineVariant = Color(0xFF666666)
)

// High Contrast Dark Color Scheme
val HighContrastDarkColorScheme = DarkColorScheme.copy(
    primary = Color(0xFFB3FFE6),
    secondary = Color(0xFFE1BEE7),
    tertiary = Color(0xFFFFD6E6),
    outline = Color.White,
    outlineVariant = Color(0xFFCCCCCC)
)