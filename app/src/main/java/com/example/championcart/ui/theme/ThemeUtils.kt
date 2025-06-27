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
    val glassOverlay: Color = ChampionCartColors.Glass.Light
)

val LocalExtendedColors = staticCompositionLocalOf { ExtendedColors() }

object ExtendedTheme {
    val current: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}

/**
 * Responsive Configuration
 */
data class ResponsiveConfig(
    val screenWidthDp: Dp,
    val screenHeightDp: Dp,
    val isCompact: Boolean,
    val isMedium: Boolean,
    val isExpanded: Boolean
)

val LocalResponsiveConfig = staticCompositionLocalOf {
    ResponsiveConfig(
        screenWidthDp = 360.dp,
        screenHeightDp = 640.dp,
        isCompact = true,
        isMedium = false,
        isExpanded = false
    )
}

@Composable
fun calculateResponsiveConfig(): ResponsiveConfig {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    return ResponsiveConfig(
        screenWidthDp = screenWidthDp,
        screenHeightDp = screenHeightDp,
        isCompact = screenWidthDp < 600.dp,
        isMedium = screenWidthDp >= 600.dp && screenWidthDp < 840.dp,
        isExpanded = screenWidthDp >= 840.dp
    )
}

/**
 * Reduce Motion Local
 */
val LocalReduceMotion = staticCompositionLocalOf { false }

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
 * Navigation Data Classes
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: Int,  // Drawable resource ID
    val badge: String? = null
)