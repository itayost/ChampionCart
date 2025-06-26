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

    // Surface Variations
    val surfaceGlass: Color,
    val surfaceElevated: Color,
    val surfaceSubtle: Color,
    val surfaceOverlay: Color
)

// Light Theme Extended Colors
val LightExtendedColors = ExtendedColors(
    // Primary Brand
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

    neonCoral = BrandColors.NeonCoral,
    neonCoralLight = BrandColors.NeonCoralLight,
    neonCoralDark = BrandColors.NeonCoralDark,
    neonCoralGlow = BrandColors.NeonCoralGlow,

    deepNavy = BrandColors.DeepNavy,
    deepNavyVariant = BrandColors.DeepNavyVariant,

    // Glass
    glassLight = BrandColors.GlassLight,
    glassMedium = BrandColors.GlassMedium,
    glassHeavy = BrandColors.GlassHeavy,
    glassAccent = Color(0x1A00D9A3),

    // Semantic States
    success = BrandColors.Success,
    successContainer = BrandColors.SuccessContainer,
    onSuccess = Color.White,
    onSuccessContainer = Color(0xFF002106),

    warning = BrandColors.Warning,
    warningContainer = BrandColors.WarningContainer,
    onWarning = Color.Black,
    onWarningContainer = Color(0xFF2E1500),

    info = BrandColors.Info,
    infoContainer = BrandColors.InfoContainer,
    onInfo = Color.White,
    onInfoContainer = Color(0xFF001D36),

    // Price Comparison
    bestPrice = BrandColors.BestPrice,
    bestPriceContainer = Color(0xFFE8F5E9),
    bestPriceGlow = BrandColors.BestPriceGlow,
    onBestPrice = Color.White,

    midPrice = BrandColors.MidPrice,
    midPriceContainer = Color(0xFFFFF3E0),
    onMidPrice = Color.Black,

    highPrice = BrandColors.HighPrice,
    highPriceContainer = Color(0xFFFFEBEE),
    onHighPrice = Color.White,

    // Store Chains
    shufersal = StoreColors.Shufersal,
    ramiLevi = StoreColors.RamiLevi,
    victory = StoreColors.Victory,
    mega = StoreColors.Mega,
    osherAd = StoreColors.OsherAd,
    coop = StoreColors.Coop,

    // Categories
    dairy = CategoryColors.Dairy,
    meat = CategoryColors.Meat,
    produce = CategoryColors.Produce,
    bakery = CategoryColors.Bakery,
    frozen = CategoryColors.Frozen,
    household = CategoryColors.Household,
    kosher = CategoryColors.Kosher,
    organic = CategoryColors.Organic,

    // Time-Based
    morningAccent = TimeBasedColors.MorningAccent,
    afternoonAccent = TimeBasedColors.AfternoonAccent,
    eveningAccent = TimeBasedColors.EveningAccent,
    nightAccent = TimeBasedColors.NightAccent,

    // Gradients
    gradientStart = BrandColors.GradientStart,
    gradientMiddle = BrandColors.GradientMiddle,
    gradientEnd = BrandColors.GradientEnd,

    // Interactive
    interactiveDefault = BrandColors.ElectricMint,
    interactiveHover = BrandColors.ElectricMintLight,
    interactivePressed = BrandColors.ElectricMintDark,
    interactiveDisabled = Color(0xFF9E9E9E),
    interactiveFocus = BrandColors.ElectricMintVariant,

    // Surfaces
    surfaceGlass = Color(0x0AFFFFFF),
    surfaceElevated = Color(0xFFF8F9FA),
    surfaceSubtle = Color(0xFFF5F6F7),
    surfaceOverlay = Color(0x33000000)
)

// Dark Theme Extended Colors
val DarkExtendedColors = ExtendedColors(
    // Primary Brand (adjusted for dark)
    electricMint = BrandColors.ElectricMintLight,
    electricMintVariant = BrandColors.ElectricMint,
    electricMintLight = Color(0xFF80FFE4),
    electricMintDark = BrandColors.ElectricMintDark,
    electricMintGlow = Color(0x8000D9A3),

    cosmicPurple = BrandColors.CosmicPurpleLight,
    cosmicPurpleVariant = BrandColors.CosmicPurple,
    cosmicPurpleLight = Color(0xFFBB86FC),
    cosmicPurpleDark = BrandColors.CosmicPurpleDark,
    cosmicPurpleGlow = Color(0x807B3FF2),

    neonCoral = BrandColors.NeonCoralLight,
    neonCoralLight = Color(0xFFFFB4C8),
    neonCoralDark = BrandColors.NeonCoralDark,
    neonCoralGlow = Color(0x80FF6B9D),

    deepNavy = BrandColors.DeepNavy,
    deepNavyVariant = BrandColors.DeepNavyVariant,

    // Glass (dark variants)
    glassLight = BrandColors.GlassDark,
    glassMedium = BrandColors.GlassDarkMedium,
    glassHeavy = Color(0x40000000),
    glassAccent = Color(0x267B3FF2),

    // Semantic States (adjusted for dark)
    success = Color(0xFF69F0AE),
    successContainer = Color(0xFF003A20),
    onSuccess = Color(0xFF003A20),
    onSuccessContainer = Color(0xFF69F0AE),

    warning = Color(0xFFFFD54F),
    warningContainer = Color(0xFF624000),
    onWarning = Color(0xFF3E2800),
    onWarningContainer = Color(0xFFFFDF9D),

    info = Color(0xFF82B1FF),
    infoContainer = Color(0xFF004B8F),
    onInfo = Color(0xFF003062),
    onInfoContainer = Color(0xFFD1E4FF),

    // Price Comparison (dark adjusted)
    bestPrice = Color(0xFF69F0AE),
    bestPriceContainer = Color(0xFF00382B),
    bestPriceGlow = Color(0x8069F0AE),
    onBestPrice = Color(0xFF003A20),

    midPrice = Color(0xFFFFD54F),
    midPriceContainer = Color(0xFF624000),
    onMidPrice = Color(0xFF3E2800),

    highPrice = Color(0xFFFF8A80),
    highPriceContainer = Color(0xFF930000),
    onHighPrice = Color(0xFF690000),

    // Store Chains (slightly adjusted for dark)
    shufersal = Color(0xFFEF5350),
    ramiLevi = Color(0xFF42A5F5),
    victory = Color(0xFFFFEE58),
    mega = Color(0xFFFF9800),
    osherAd = Color(0xFF66BB6A),
    coop = Color(0xFFAB47BC),

    // Categories (adjusted for dark)
    dairy = Color(0xFF90CAF9),
    meat = Color(0xFFEF5350),
    produce = Color(0xFF81C784),
    bakery = Color(0xFFFFD54F),
    frozen = Color(0xFF64B5F6),
    household = Color(0xFFBA68C8),
    kosher = Color(0xFF7986CB),
    organic = Color(0xFFAED581),

    // Time-Based (dark variants)
    morningAccent = Color(0xFFFFCCBC),
    afternoonAccent = Color(0xFF80DEEA),
    eveningAccent = Color(0xFFB39DDB),
    nightAccent = Color(0xFF546E7A),

    // Gradients (dark adjusted)
    gradientStart = BrandColors.ElectricMintLight,
    gradientMiddle = BrandColors.CosmicPurpleLight,
    gradientEnd = BrandColors.NeonCoralLight,

    // Interactive (dark adjusted)
    interactiveDefault = BrandColors.ElectricMintLight,
    interactiveHover = Color(0xFF80FFE4),
    interactivePressed = BrandColors.ElectricMint,
    interactiveDisabled = Color(0xFF424242),
    interactiveFocus = Color(0xFF4DFFCE),

    // Surfaces (dark)
    surfaceGlass = Color(0x1AFFFFFF),
    surfaceElevated = Color(0xFF1E1E1E),
    surfaceSubtle = Color(0xFF121212),
    surfaceOverlay = Color(0x80000000)
)

// Composition Local
val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/**
 * Extension property for easy access from MaterialTheme
 * This allows using MaterialTheme.colorScheme.extended in composables
 */
val ColorScheme.extended: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

// Helper Functions
@Composable
fun getTimeBasedAccentColor(): Color {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val colors = LocalExtendedColors.current

    return when (hour) {
        in 6..11 -> colors.morningAccent
        in 12..17 -> colors.afternoonAccent
        in 18..23 -> colors.eveningAccent
        else -> colors.nightAccent
    }
}

@Composable
fun getStoreColor(storeName: String): Color {
    val colors = LocalExtendedColors.current

    return when (storeName.lowercase()) {
        "shufersal", "שופרסל" -> colors.shufersal
        "rami levi", "רמי לוי" -> colors.ramiLevi
        "victory", "ויקטורי" -> colors.victory
        "mega", "מגה" -> colors.mega
        "osher ad", "אושר עד" -> colors.osherAd
        "coop", "קופ", "כל-בו חצי חינם" -> colors.coop
        else -> colors.interactiveDefault
    }
}

@Composable
fun getCategoryColor(category: String): Color {
    val colors = LocalExtendedColors.current

    return when (category.lowercase()) {
        "dairy", "חלב", "מוצרי חלב" -> colors.dairy
        "meat", "בשר" -> colors.meat
        "produce", "ירקות", "פירות" -> colors.produce
        "bakery", "מאפה", "לחם" -> colors.bakery
        "frozen", "קפואים" -> colors.frozen
        "household", "ניקיון", "בית" -> colors.household
        "kosher", "כשר" -> colors.kosher
        "organic", "אורגני" -> colors.organic
        else -> colors.interactiveDefault
    }
}

// Gradient Brushes
fun getElectricGradient(
    colors: ExtendedColors = LightExtendedColors
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            colors.gradientStart,
            colors.gradientMiddle,
            colors.gradientEnd
        ),
        tileMode = TileMode.Clamp
    )
}

fun getGlassmorphicGradient(
    intensity: GlassIntensity = GlassIntensity.Medium,
    isDark: Boolean = false
): Brush {
    val baseColor = if (isDark) Color.Black else Color.White
    val alpha = when (intensity) {
        GlassIntensity.Light -> 0.05f
        GlassIntensity.Medium -> 0.10f
        GlassIntensity.Heavy -> 0.15f
        GlassIntensity.Ultra -> 0.20f
    }

    return Brush.linearGradient(
        colors = listOf(
            baseColor.copy(alpha = alpha),
            baseColor.copy(alpha = alpha * 0.5f)
        )
    )
}