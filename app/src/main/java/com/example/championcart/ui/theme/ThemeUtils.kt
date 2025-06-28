package com.example.championcart.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Calendar

/**
 * Extended Theme Properties
 * Additional theme properties for ChampionCart
 */

// Extended color properties
data class ExtendedColors(
    val bestPrice: Color = ChampionCartColors.Price.Best,
    val midPrice: Color = ChampionCartColors.Price.Mid,
    val highPrice: Color = ChampionCartColors.Price.High,
    val bestPriceGlow: Color = ChampionCartColors.Price.BestGlow,
    val midPriceGlow: Color = ChampionCartColors.Price.MidGlow,
    val highPriceGlow: Color = ChampionCartColors.Price.HighGlow,
    val glassSurface: Color = ChampionCartColors.Glass.Medium,
    val glassOverlay: Color = ChampionCartColors.Glass.Light,
    // New extended colors
    val shimmer: Color = ChampionCartColors.Overlay.shimmerMedium,
    val glow: Color = ChampionCartColors.Overlay.glowMedium,
    val electricBlue: Color = ChampionCartColors.Accent.ElectricBlue,
    val cyberYellow: Color = ChampionCartColors.Accent.CyberYellow,
    val holographicPink: Color = ChampionCartColors.Accent.HolographicPink
)

val LocalExtendedColors = staticCompositionLocalOf { ExtendedColors() }

object ExtendedTheme {
    val current: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}

/**
 * Calculate responsive configuration (moved to Theme.kt)
 * This function remains for backward compatibility
 */
@Composable
fun calculateResponsiveConfig(): ResponsiveConfig {
    val configuration = LocalConfiguration.current

    val screenWidthDp = configuration.screenWidthDp

    return ResponsiveConfig(
        isCompact = screenWidthDp < 600,
        isMedium = screenWidthDp in 600..839,
        isExpanded = screenWidthDp >= 840,
        screenWidthDp = screenWidthDp
    )
}

/**
 * Time of Day Helper
 */
fun getTimeOfDay(): TimeOfDay {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 6..11 -> TimeOfDay.MORNING
        in 12..17 -> TimeOfDay.AFTERNOON
        in 18..23 -> TimeOfDay.EVENING
        else -> TimeOfDay.NIGHT
    }
}

/**
 * Spacing Tokens
 * Convenience object for component-level spacing
 */
object SpacingTokens {
    val XS = Spacing.xs     // 4.dp
    val S = Spacing.s       // 8.dp
    val M = Spacing.m       // 12.dp
    val L = Spacing.l       // 16.dp
    val XL = Spacing.xl     // 20.dp
    val XXL = Spacing.xxl   // 24.dp
}

/**
 * Sizing Tokens
 * Convenience object for component-level sizing
 */
object SizingTokens {
    // Icon sizes
    val IconXS = Sizing.Icon.xs      // 16.dp
    val IconS = Sizing.Icon.s        // 20.dp
    val IconM = Sizing.Icon.m        // 24.dp
    val IconL = Sizing.Icon.l        // 28.dp
    val IconXL = Sizing.Icon.xl      // 32.dp

    // Button heights
    val ButtonHeightS = Sizing.Button.heightS    // 32.dp
    val ButtonHeightM = Sizing.Button.heightM    // 40.dp
    val ButtonHeightL = Sizing.Button.heightL    // 48.dp
    val ButtonHeightXL = Sizing.Button.heightXL  // 56.dp

    // Touch targets
    val MinTouchTarget = Sizing.minTouchTarget   // 48.dp
}

/**
 * Animation Tokens
 * Quick access to animation specs
 */
object AnimationTokens {
    val Quick = ChampionCartAnimations.Durations.Quick          // 200ms
    val Standard = ChampionCartAnimations.Durations.Standard    // 300ms
    val Medium = ChampionCartAnimations.Durations.Medium        // 400ms

    val SpringGentle = ChampionCartAnimations.Springs.gentle<Float>()
    val SpringSmooth = ChampionCartAnimations.Springs.smooth<Float>()
    val SpringResponsive = ChampionCartAnimations.Springs.responsive<Float>()
    val SpringBouncy = ChampionCartAnimations.Springs.bouncy<Float>()
}

/**
 * Theme Extension Functions
 */

// Get store color by name
fun getStoreColor(storeName: String): Color {
    return when (storeName.lowercase()) {
        "shufersal", "שופרסל" -> ChampionCartColors.Store.Shufersal
        "rami levi", "רמי לוי" -> ChampionCartColors.Store.RamiLevi
        "victory", "ויקטורי" -> ChampionCartColors.Store.Victory
        "mega", "מגה" -> ChampionCartColors.Store.Mega
        "osher ad", "אושר עד" -> ChampionCartColors.Store.OsherAd
        "coop", "קואופ", "co-op" -> ChampionCartColors.Store.Coop
        else -> ChampionCartColors.Brand.ElectricMint
    }
}

// Get category color by name
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "dairy", "חלב ומוצריו", "מוצרי חלב" -> ChampionCartColors.Category.Dairy
        "meat", "בשר", "בשר ועוף" -> ChampionCartColors.Category.Meat
        "produce", "ירקות ופירות", "פירות וירקות" -> ChampionCartColors.Category.Produce
        "bakery", "מאפיה", "לחם ומאפיה" -> ChampionCartColors.Category.Bakery
        "frozen", "קפואים", "מוצרים קפואים" -> ChampionCartColors.Category.Frozen
        "household", "ניקיון", "מוצרי ניקיון" -> ChampionCartColors.Category.Household
        "kosher", "כשר", "כשרות" -> ChampionCartColors.Category.Kosher
        "organic", "אורגני", "מוצרים אורגניים" -> ChampionCartColors.Category.Organic
        else -> ChampionCartColors.Brand.CosmicPurple
    }
}

// Get price level color
fun getPriceLevelColor(priceLevel: PriceLevel): Color {
    return when (priceLevel) {
        PriceLevel.Best -> ChampionCartColors.Price.Best
        PriceLevel.Mid -> ChampionCartColors.Price.Mid
        PriceLevel.High -> ChampionCartColors.Price.High
    }
}

// Get price level glow color
fun getPriceLevelGlowColor(priceLevel: PriceLevel): Color {
    return when (priceLevel) {
        PriceLevel.Best -> ChampionCartColors.Price.BestGlow
        PriceLevel.Mid -> ChampionCartColors.Price.MidGlow
        PriceLevel.High -> ChampionCartColors.Price.HighGlow
    }
}

// Calculate price level based on price range
fun calculatePriceLevel(price: Float, minPrice: Float, maxPrice: Float): PriceLevel {
    val range = maxPrice - minPrice
    val threshold = range * 0.15f // 15% threshold

    return when {
        price <= minPrice + threshold -> PriceLevel.Best
        price >= maxPrice - threshold -> PriceLevel.High
        else -> PriceLevel.Mid
    }
}

/**
 * Get animated color for time of day
 */
fun getTimeBasedColor(timeOfDay: TimeOfDay, isDark: Boolean): Color {
    return when (timeOfDay) {
        TimeOfDay.MORNING -> if (isDark) ChampionCartColors.Brand.ElectricMint else ChampionCartColors.Morning.primary
        TimeOfDay.AFTERNOON -> ChampionCartColors.Brand.ElectricMint
        TimeOfDay.EVENING -> if (isDark) ChampionCartColors.Brand.CosmicPurpleLight else ChampionCartColors.Evening.primary
        TimeOfDay.NIGHT -> ChampionCartColors.Brand.ElectricMintLight
    }
}

/**
 * Navigation Data Classes
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: Int,  // Drawable resource ID
    val badge: String? = null,
    val color: Color = ChampionCartColors.Brand.ElectricMint // Associated color
)