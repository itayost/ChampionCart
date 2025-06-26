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
    val SuccessContainer = Color(0xFFE8F5E9)
    val Warning = Color(0xFFFFB300)
    val WarningContainer = Color(0xFFFFF3CD)
    val Error = Color(0xFFFF5252)
    val ErrorContainer = Color(0xFFFFEBEE)
    val Info = Color(0xFF448AFF)
    val InfoContainer = Color(0xFFE3F2FD)

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
    val MorningAccent = Color(0xFFFFF3E0)

    // Afternoon (12pm-6pm) - Electric & Vibrant with electric mint
    val AfternoonPrimary = BrandColors.ElectricMint
    val AfternoonSecondary = BrandColors.CosmicPurple
    val AfternoonTertiary = Color(0xFF00BCD4)
    val AfternoonAccent = Color(0xFFE0F7FA)

    // Evening (6pm-12am) - Deep purples with cosmic themes
    val EveningPrimary = BrandColors.CosmicPurple
    val EveningSecondary = Color(0xFF673AB7)
    val EveningTertiary = Color(0xFF3F51B5)
    val EveningAccent = Color(0xFFEDE7F6)

    // Night (12am-6am) - Dark mode auto-activated
    val NightPrimary = Color(0xFF455A64)
    val NightSecondary = Color(0xFF607D8B)
    val NightTertiary = Color(0xFF78909C)
    val NightAccent = Color(0xFF263238)
}

// Store Chain Colors
object StoreColors {
    val Shufersal = Color(0xFFE53935)  // Red
    val RamiLevi = Color(0xFF1E88E5)   // Blue
    val Victory = Color(0xFFFDD835)    // Yellow
    val Mega = Color(0xFFFF6F00)       // Orange
    val OsherAd = Color(0xFF43A047)    // Green
    val Coop = Color(0xFF8E24AA)       // Purple
}

// Product Category Colors
object CategoryColors {
    val Dairy = Color(0xFF81D4FA)      // Light Blue
    val Meat = Color(0xFFEF5350)       // Red
    val Produce = Color(0xFF66BB6A)    // Green
    val Bakery = Color(0xFFFFCA28)     // Amber
    val Frozen = Color(0xFF42A5F5)     // Blue
    val Household = Color(0xFFAB47BC)  // Purple
    val Kosher = Color(0xFF5C6BC0)     // Indigo
    val Organic = Color(0xFF9CCC65)    // Light Green
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
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF21005D),

    // Tertiary Colors - Neon Coral
    tertiary = BrandColors.NeonCoral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDAE6),
    onTertiaryContainer = Color(0xFF3E001D),

    // Error Colors
    error = BrandColors.Error,
    onError = Color.White,
    errorContainer = BrandColors.ErrorContainer,
    onErrorContainer = Color(0xFF410002),

    // Background Colors
    background = Color(0xFFFBFDF8),
    onBackground = Color(0xFF191C1A),

    // Surface Colors
    surface = Color(0xFFFBFDF8),
    onSurface = Color(0xFF191C1A),
    surfaceVariant = Color(0xFFDCE5DD),
    onSurfaceVariant = Color(0xFF414942),

    // Additional Colors
    surfaceTint = BrandColors.ElectricMint,
    inverseSurface = Color(0xFF2E312F),
    inverseOnSurface = Color(0xFFF0F1EC),
    outline = Color(0xFF717971),
    outlineVariant = Color(0xFFC0C9C1)
)

// Material3 Dark Color Scheme - Electric Harmony Night Mode
val DarkColorScheme = darkColorScheme(
    // Primary Colors - Electric Mint (adjusted for dark)
    primary = BrandColors.ElectricMintLight,
    onPrimary = Color(0xFF003A2F),
    primaryContainer = Color(0xFF005142),
    onPrimaryContainer = BrandColors.ElectricMintLight,

    // Secondary Colors - Cosmic Purple (adjusted for dark)
    secondary = BrandColors.CosmicPurpleLight,
    onSecondary = Color(0xFF3B0086),
    secondaryContainer = Color(0xFF5429A7),
    onSecondaryContainer = Color(0xFFE9DDFF),

    // Tertiary Colors - Neon Coral (adjusted for dark)
    tertiary = BrandColors.NeonCoralLight,
    onTertiary = Color(0xFF5D1133),
    tertiaryContainer = Color(0xFF7B2949),
    onTertiaryContainer = Color(0xFFFFDAE6),

    // Error Colors
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Background Colors
    background = BrandColors.DeepNavy,
    onBackground = Color(0xFFE2E3DE),

    // Surface Colors
    surface = BrandColors.DeepNavyVariant,
    onSurface = Color(0xFFE2E3DE),
    surfaceVariant = Color(0xFF414942),
    onSurfaceVariant = Color(0xFFC0C9C1),

    // Additional Colors
    surfaceTint = BrandColors.ElectricMintLight,
    inverseSurface = Color(0xFFE2E3DE),
    inverseOnSurface = BrandColors.DeepNavy,
    outline = Color(0xFF8B938B),
    outlineVariant = Color(0xFF414942)
)