package com.example.championcart.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import java.util.*

/**
 * Champion Cart - Extended Semantic Color System
 * Brand-specific colors with contextual meanings for Electric Harmony theme
 * Supporting Hebrew-first design and glassmorphic aesthetics
 */

/**
 * Extended color definitions for Champion Cart brand
 */
@Immutable
data class ExtendedColors(
    // Primary Brand Colors
    val electricMint: Color,
    val electricMintVariant: Color,
    val electricMintLight: Color,
    val electricMintDark: Color,
    val electricMintGlow: Color,

    val cosmicPurple: Color,
    val cosmicPurpleVariant: Color,
    val cosmicPurpleLight: Color,
    val cosmicPurpleDark: Color,
    val cosmicPurpleGlow: Color,

    // NEW: Neon Coral colors
    val neonCoral: Color,
    val neonCoralLight: Color,
    val neonCoralDark: Color,
    val neonCoralGlow: Color,

    val deepNavy: Color,
    val deepNavyVariant: Color,

    // Glassmorphic Overlays
    val glassLight: Color,
    val glassMedium: Color,
    val glassHeavy: Color,
    val glassAccent: Color,

    // Semantic State Colors
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

    // Price Comparison Colors
    val bestPrice: Color,
    val bestPriceContainer: Color,
    val bestPriceGlow: Color,
    val onBestPrice: Color,

    val midPrice: Color,
    val midPriceContainer: Color,
    val onMidPrice: Color,

    val highPrice: Color,
    val highPriceContainer: Color,
    val onHighPrice: Color,

    // Store Chain Colors
    val shufersal: Color,
    val ramiLevi: Color,
    val victory: Color,
    val mega: Color,
    val osherAd: Color,
    val coop: Color,

    // Product Category Colors
    val dairy: Color,
    val meat: Color,
    val produce: Color,
    val bakery: Color,
    val frozen: Color,
    val household: Color,
    val kosher: Color,
    val organic: Color,

    // Time-Based Accent Colors
    val morningAccent: Color,
    val afternoonAccent: Color,
    val eveningAccent: Color,
    val nightAccent: Color,

    // Gradient Colors
    val gradientStart: Color,
    val gradientMiddle: Color,
    val gradientEnd: Color,

    // Interactive States
    val interactiveDefault: Color,
    val interactiveHover: Color,
    val interactivePressed: Color,
    val interactiveDisabled: Color,
    val interactiveFocus: Color,

    // Surface Variants
    val surfaceGlass: Color,
    val surfaceElevated: Color,
    val surfaceCard: Color,
    val surfaceModal: Color,
    val surfaceNavigation: Color,

    // Border & Outline Variants
    val borderSubtle: Color,
    val borderDefault: Color,
    val borderStrong: Color,
    val borderGlass: Color,

    // Text Variants
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textInverse: Color,
    val textOnGlass: Color,

    // Special Effects
    val shadow: Color,
    val glow: Color,
    val shimmer: Color,
    val highlight: Color
)

/**
 * Light theme extended colors
 */
val lightExtendedColors = ExtendedColors(
    // Primary Brand Colors
    electricMint = BrandColors.ElectricMint,
    electricMintVariant = BrandColors.ElectricMintVariant,
    electricMintLight = BrandColors.ElectricMintLight,
    electricMintDark = BrandColors.ElectricMintDark,
    electricMintGlow = BrandColors.ElectricMintGlow,

    cosmicPurple = BrandColors.CosmicPurple,
    cosmicPurpleVariant = BrandColors.CosmicPurpleVariant,
    cosmicPurpleLight = BrandColors.CosmicPurpleLight,
    cosmicPurpleDark = BrandColors.CosmicPurpleDark,
    cosmicPurpleGlow = BrandColors.CosmicPurpleGlow,

    // NEW: Neon Coral
    neonCoral = BrandColors.NeonCoral,
    neonCoralLight = BrandColors.NeonCoralLight,
    neonCoralDark = BrandColors.NeonCoralDark,
    neonCoralGlow = BrandColors.NeonCoralGlow,

    deepNavy = BrandColors.DeepNavy,
    deepNavyVariant = BrandColors.DeepNavyVariant,

    // Glassmorphic Overlays - Updated with correct values
    glassLight = BrandColors.GlassLight,     // 8% white
    glassMedium = BrandColors.GlassMedium,   // 16% white
    glassHeavy = BrandColors.GlassHeavy,     // 20% white
    glassAccent = Color(0x26FFFFFF),         // 15% white for accents

    // Semantic State Colors
    success = BrandColors.Success,
    successContainer = Color(0xFFE8F5E8),
    onSuccess = Color.White,
    onSuccessContainer = Color(0xFF1B5E20),

    warning = BrandColors.Warning,
    warningContainer = Color(0xFFFFF3E0),
    onWarning = Color.White,
    onWarningContainer = Color(0xFFE65100),

    info = BrandColors.Info,
    infoContainer = Color(0xFFE3F2FD),
    onInfo = Color.White,
    onInfoContainer = Color(0xFF0D47A1),

    // Price Comparison Colors
    bestPrice = BrandColors.BestPrice,
    bestPriceContainer = Color(0xFFE8F5E8),
    bestPriceGlow = BrandColors.BestPriceGlow,
    onBestPrice = Color.White,

    midPrice = BrandColors.MidPrice,
    midPriceContainer = Color(0xFFFFF8E1),
    onMidPrice = Color.White,

    highPrice = BrandColors.HighPrice,
    highPriceContainer = Color(0xFFFFEBEE),
    onHighPrice = Color.White,

    // Store Chain Colors (Based on actual brand colors)
    shufersal = Color(0xFF0066CC),     // Shufersal Blue
    ramiLevi = Color(0xFFFF6B35),      // Rami Levy Orange
    victory = Color(0xFF8BC34A),       // Victory Green
    mega = Color(0xFFE91E63),          // Mega Pink
    osherAd = Color(0xFFFF9800),       // Osher Ad Orange
    coop = Color(0xFF9C27B0),          // Coop Purple

    // Product Category Colors
    dairy = Color(0xFF81C784),         // Fresh Green
    meat = Color(0xFFE57373),          // Meat Red
    produce = Color(0xFF4CAF50),       // Produce Green
    bakery = Color(0xFFFFB74D),        // Bakery Orange
    frozen = Color(0xFF64B5F6),        // Frozen Blue
    household = Color(0xFF9575CD),     // Household Purple
    kosher = Color(0xFF5C6BC0),        // Kosher Blue
    organic = Color(0xFF8BC34A),       // Organic Green

    // Time-Based Accent Colors
    morningAccent = TimeBasedColors.MorningPrimary,
    afternoonAccent = TimeBasedColors.AfternoonPrimary,
    eveningAccent = TimeBasedColors.EveningPrimary,
    nightAccent = TimeBasedColors.NightPrimary,

    // Gradient Colors - Updated with Neon Coral
    gradientStart = BrandColors.ElectricMint,
    gradientMiddle = BrandColors.CosmicPurple,
    gradientEnd = BrandColors.NeonCoral,

    // Interactive States
    interactiveDefault = BrandColors.ElectricMint,
    interactiveHover = Color(0xFF00C893),
    interactivePressed = Color(0xFF00B085),
    interactiveDisabled = Color(0xFFBDBDBD),
    interactiveFocus = Color(0xFF00E6A8),

    // Surface Variants
    surfaceGlass = Color(0x0AFFFFFF),
    surfaceElevated = Color(0xFFFAFAFA),
    surfaceCard = Color.White,
    surfaceModal = Color(0xFFFEFEFE),
    surfaceNavigation = Color(0xFFFDFDFD),

    // Border & Outline Variants
    borderSubtle = Color(0xFFF0F0F0),
    borderDefault = Color(0xFFE0E0E0),
    borderStrong = Color(0xFFBDBDBD),
    borderGlass = Color(0x1AFFFFFF),

    // Text Variants
    textPrimary = BrandColors.DeepNavy,
    textSecondary = Color(0xFF5A6178),
    textTertiary = Color(0xFF9E9E9E),
    textInverse = Color.White,
    textOnGlass = Color(0xFF2A2F4A),

    // Special Effects
    shadow = Color(0x1A000000),
    glow = BrandColors.ElectricMintGlow,
    shimmer = Color(0x80FFFFFF),
    highlight = Color(0x4000E676)
)

/**
 * Dark theme extended colors
 */
val darkExtendedColors = ExtendedColors(
    // Primary Brand Colors
    electricMint = Color(0xFF80F5D4),  // Lighter for dark mode
    electricMintVariant = Color(0xFF00E676),
    electricMintLight = Color(0xFFB3FFE6),
    electricMintDark = Color(0xFF4DCCB3),
    electricMintGlow = Color(0x5480F5D4),

    cosmicPurple = Color(0xFF9D6FFF),  // Lighter for dark mode
    cosmicPurpleVariant = Color(0xFFCE93D8),
    cosmicPurpleLight = Color(0xFFBE9FFF),
    cosmicPurpleDark = Color(0xFF7B3FF2),
    cosmicPurpleGlow = Color(0x549D6FFF),

    // NEW: Neon Coral for dark theme
    neonCoral = Color(0xFFFF8FB3),  // Lighter for dark mode
    neonCoralLight = Color(0xFFFFB3CC),
    neonCoralDark = Color(0xFFFF6B9D),
    neonCoralGlow = Color(0x54FF8FB3),

    deepNavy = Color(0xFF2A2F4A),
    deepNavyVariant = Color(0xFF353A54),

    // Glassmorphic Overlays - Dark mode
    glassLight = BrandColors.GlassDark,       // 8% black
    glassMedium = BrandColors.GlassDarkMedium, // 16% black
    glassHeavy = Color(0x26FFFFFF),           // 15% white for dark mode
    glassAccent = Color(0x33FFFFFF),          // 20% white for accents

    // Semantic State Colors
    success = Color(0xFF81C784),
    successContainer = Color(0xFF2E7D32),
    onSuccess = Color.White,
    onSuccessContainer = Color(0xFFC8E6C9),

    warning = Color(0xFFFFB74D),
    warningContainer = Color(0xFFEF6C00),
    onWarning = Color.White,
    onWarningContainer = Color(0xFFFFE0B2),

    info = Color(0xFF64B5F6),
    infoContainer = Color(0xFF1565C0),
    onInfo = Color.White,
    onInfoContainer = Color(0xFFBBDEFB),

    // Price Comparison Colors
    bestPrice = Color(0xFF81C784),
    bestPriceContainer = Color(0xFF2E7D32),
    bestPriceGlow = Color(0x5481C784),
    onBestPrice = Color.White,

    midPrice = Color(0xFFFFB74D),
    midPriceContainer = Color(0xFFEF6C00),
    onMidPrice = Color.White,

    highPrice = Color(0xFFE57373),
    highPriceContainer = Color(0xFFC62828),
    onHighPrice = Color.White,

    // Store Chain Colors (Adjusted for dark theme)
    shufersal = Color(0xFF42A5F5),
    ramiLevi = Color(0xFFFF8A65),
    victory = Color(0xFFA5D6A7),
    mega = Color(0xFFF48FB1),
    osherAd = Color(0xFFFFB74D),
    coop = Color(0xFFCE93D8),

    // Product Category Colors (Adjusted for dark theme)
    dairy = Color(0xFFA5D6A7),
    meat = Color(0xFFEF9A9A),
    produce = Color(0xFF81C784),
    bakery = Color(0xFFFFCC02),
    frozen = Color(0xFF90CAF9),
    household = Color(0xFFB39DDB),
    kosher = Color(0xFF7986CB),
    organic = Color(0xFFA5D6A7),

    // Time-Based Accent Colors
    morningAccent = TimeBasedColors.MorningSecondary,
    afternoonAccent = TimeBasedColors.AfternoonSecondary,
    eveningAccent = TimeBasedColors.EveningSecondary,
    nightAccent = TimeBasedColors.NightSecondary,

    // Gradient Colors - Dark mode
    gradientStart = Color(0xFF80F5D4),
    gradientMiddle = Color(0xFF9D6FFF),
    gradientEnd = Color(0xFFFF8FB3),

    // Interactive States
    interactiveDefault = Color(0xFF80F5D4),
    interactiveHover = Color(0xFF9FF7DE),
    interactivePressed = Color(0xFF66F2CF),
    interactiveDisabled = Color(0xFF4A4F66),
    interactiveFocus = Color(0xFFB3F5E6),

    // Surface Variants
    surfaceGlass = Color(0x0AFFFFFF),
    surfaceElevated = Color(0xFF2A2F4A),
    surfaceCard = Color(0xFF1A1F3A),
    surfaceModal = Color(0xFF353A54),
    surfaceNavigation = Color(0xFF2A2F4A),

    // Border & Outline Variants
    borderSubtle = Color(0xFF2A2F4A),
    borderDefault = Color(0xFF4A4F66),
    borderStrong = Color(0xFF5A6178),
    borderGlass = Color(0x1AFFFFFF),

    // Text Variants
    textPrimary = Color(0xFFE8EBF2),
    textSecondary = Color(0xFFB8BCC8),
    textTertiary = Color(0xFF757575),
    textInverse = BrandColors.DeepNavy,
    textOnGlass = Color(0xFFE8EBF2),

    // Special Effects
    shadow = Color(0x40000000),
    glow = Color(0x6680F5D4),
    shimmer = Color(0x40FFFFFF),
    highlight = Color(0x6000E676)
)

/**
 * High contrast light extended colors
 */
val highContrastLightExtendedColors = lightExtendedColors.copy(
    // Enhanced contrast for accessibility
    textPrimary = Color.Black,
    textSecondary = Color(0xFF333333),
    borderDefault = Color.Black,
    borderStrong = Color.Black,
    interactiveDefault = BrandColors.ElectricMintDark,
    interactiveHover = Color(0xFF00A67C),
    bestPrice = Color(0xFF1B5E20),
    midPrice = Color(0xFFE65100),
    highPrice = Color(0xFFB71C1C)
)

/**
 * High contrast dark extended colors
 */
val highContrastDarkExtendedColors = darkExtendedColors.copy(
    // Enhanced contrast for accessibility
    textPrimary = Color.White,
    textSecondary = Color(0xFFCCCCCC),
    borderDefault = Color.White,
    borderStrong = Color.White,
    interactiveDefault = Color(0xFFB3F5E6),
    interactiveHover = Color(0xFFCCF7ED),
    bestPrice = Color(0xFFC8E6C9),
    midPrice = Color(0xFFFFE0B2),
    highPrice = Color(0xFFFFCDD2)
)

/**
 * Composition Local for Extended Colors
 */
val LocalExtendedColors = staticCompositionLocalOf { lightExtendedColors }

/**
 * Extension property for easy access from MaterialTheme
 */
val androidx.compose.material3.ColorScheme.extended: ExtendedColors
    @Composable get() = LocalExtendedColors.current

/**
 * Gradient definitions using extended colors
 */
object ExtendedGradients {
    @Composable
    fun electricGradient(): Brush = Brush.linearGradient(
        colors = listOf(
            LocalExtendedColors.current.electricMint,
            LocalExtendedColors.current.cosmicPurple
        )
    )

    @Composable
    fun neonGradient(): Brush = Brush.linearGradient(
        colors = listOf(
            LocalExtendedColors.current.cosmicPurple,
            LocalExtendedColors.current.neonCoral
        )
    )

    @Composable
    fun fullSpectrumGradient(): Brush = Brush.linearGradient(
        colors = listOf(
            LocalExtendedColors.current.electricMint,
            LocalExtendedColors.current.cosmicPurple,
            LocalExtendedColors.current.neonCoral
        )
    )

    @Composable
    fun timeBasedGradient(hour: Int): Brush {
        val colors = LocalExtendedColors.current
        val (start, end) = when (hour) {
            in 6..11 -> colors.morningAccent to colors.afternoonAccent
            in 12..17 -> colors.afternoonAccent to colors.eveningAccent
            in 18..23 -> colors.eveningAccent to colors.nightAccent
            else -> colors.nightAccent to colors.morningAccent
        }

        return Brush.linearGradient(
            colors = listOf(start, end),
            tileMode = TileMode.Clamp
        )
    }

    @Composable
    fun glassGradient(): Brush = Brush.verticalGradient(
        colors = listOf(
            LocalExtendedColors.current.glassLight,
            LocalExtendedColors.current.glassMedium
        )
    )

    @Composable
    fun priceGradient(): Brush = Brush.horizontalGradient(
        colors = listOf(
            LocalExtendedColors.current.bestPrice,
            LocalExtendedColors.current.midPrice,
            LocalExtendedColors.current.highPrice
        )
    )
}