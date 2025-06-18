package com.example.championcart.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Champion Cart - Responsive Theme System
 * Adaptive layouts for phones, tablets, foldables, and desktop
 * Electric Harmony design scales beautifully across all screen sizes
 */

/**
 * Screen size classifications following Material Design 3
 */
enum class ScreenSize {
    Compact,    // Phone portrait, small tablets
    Medium,     // Phone landscape, medium tablets
    Expanded    // Large tablets, desktop, foldables unfolded
}

/**
 * Screen orientation types
 */
enum class ScreenOrientation {
    Portrait,
    Landscape
}

/**
 * Device form factor classifications
 */
enum class DeviceFormFactor {
    Phone,
    Tablet,
    Foldable,
    Desktop
}

/**
 * Navigation layout types for different screen sizes
 */
enum class NavigationLayout {
    BottomNavigation,     // Compact screens - traditional bottom nav
    NavigationRail,       // Medium screens - side rail navigation
    NavigationDrawer,     // Expanded screens - persistent drawer
    DualPane             // Very large screens - dual navigation
}

/**
 * Content layout configurations
 */
enum class ContentLayout {
    SinglePane,          // Traditional single column
    DualPane,           // Two column layout
    TriplePane,         // Three column layout (very wide screens)
    AdaptivePane        // Dynamic based on content
}

/**
 * Responsive configuration data class
 */
@Immutable
data class ResponsiveConfig(
    val screenSize: ScreenSize,
    val orientation: ScreenOrientation,
    val formFactor: DeviceFormFactor,
    val navigationLayout: NavigationLayout,
    val contentLayout: ContentLayout,
    val windowSizeClass: WindowSizeClass,
    val contentPadding: PaddingValues,
    val componentTokens: ComponentTokens,
    val gridColumns: Int,
    val maxContentWidth: Dp,
    val isLandscape: Boolean,
    val hasNavigationRail: Boolean,
    val hasNavigationDrawer: Boolean,
    val supportsMultipleWindows: Boolean
)

/**
 * Responsive breakpoints
 */
object ResponsiveBreakpoints {
    // Width breakpoints
    val CompactMaxWidth = 600.dp
    val MediumMaxWidth = 840.dp
    val ExpandedMinWidth = 840.dp

    // Height breakpoints
    val CompactMaxHeight = 480.dp
    val MediumMaxHeight = 900.dp
    val ExpandedMinHeight = 900.dp

    // Content max widths
    val ContentMaxWidthCompact = 400.dp
    val ContentMaxWidthMedium = 600.dp
    val ContentMaxWidthExpanded = 800.dp
    val ContentMaxWidthWide = 1200.dp

    // Grid breakpoints
    val GridCompactColumns = 2
    val GridMediumColumns = 3
    val GridExpandedColumns = 4
    val GridWideColumns = 6
}

/**
 * Main responsive configuration provider
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberResponsiveConfig(): ResponsiveConfig {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Determine screen size
    val screenSize = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> ScreenSize.Compact
        WindowWidthSizeClass.Medium -> ScreenSize.Medium
        WindowWidthSizeClass.Expanded -> ScreenSize.Expanded
        else -> ScreenSize.Compact
    }

    // Determine device form factor
    val formFactor = determineFormFactor(screenWidthDp, screenHeightDp, isLandscape)

    // Determine navigation layout
    val navigationLayout = determineNavigationLayout(screenSize, formFactor, isLandscape)

    // Determine content layout
    val contentLayout = determineContentLayout(screenSize, formFactor, isLandscape)

    // Get appropriate component tokens
    val componentTokens = when (screenSize) {
        ScreenSize.Compact -> CompactComponentTokens
        ScreenSize.Medium -> DefaultComponentTokens
        ScreenSize.Expanded -> ExpandedComponentTokens
    }

    // Calculate content padding
    val contentPadding = calculateContentPadding(
        screenSize = screenSize,
        navigationLayout = navigationLayout,
        layoutDirection = layoutDirection
    )

    // Grid columns
    val gridColumns = when (screenSize) {
        ScreenSize.Compact -> ResponsiveBreakpoints.GridCompactColumns
        ScreenSize.Medium -> ResponsiveBreakpoints.GridMediumColumns
        ScreenSize.Expanded -> ResponsiveBreakpoints.GridExpandedColumns
    }

    // Max content width
    val maxContentWidth = when (screenSize) {
        ScreenSize.Compact -> ResponsiveBreakpoints.ContentMaxWidthCompact
        ScreenSize.Medium -> ResponsiveBreakpoints.ContentMaxWidthMedium
        ScreenSize.Expanded -> ResponsiveBreakpoints.ContentMaxWidthExpanded
    }

    return ResponsiveConfig(
        screenSize = screenSize,
        orientation = if (isLandscape) ScreenOrientation.Landscape else ScreenOrientation.Portrait,
        formFactor = formFactor,
        navigationLayout = navigationLayout,
        contentLayout = contentLayout,
        windowSizeClass = windowSizeClass,
        contentPadding = contentPadding,
        componentTokens = componentTokens,
        gridColumns = gridColumns,
        maxContentWidth = maxContentWidth,
        isLandscape = isLandscape,
        hasNavigationRail = navigationLayout == NavigationLayout.NavigationRail,
        hasNavigationDrawer = navigationLayout == NavigationLayout.NavigationDrawer,
        supportsMultipleWindows = screenSize == ScreenSize.Expanded && formFactor != DeviceFormFactor.Phone
    )
}

/**
 * Determine device form factor
 */
private fun determineFormFactor(
    screenWidth: Dp,
    screenHeight: Dp,
    isLandscape: Boolean
): DeviceFormFactor {
    val minDimension = minOf(screenWidth, screenHeight)
    val maxDimension = maxOf(screenWidth, screenHeight)

    return when {
        // Desktop-like dimensions
        minDimension > 840.dp && maxDimension > 1200.dp -> DeviceFormFactor.Desktop

        // Tablet-like dimensions
        minDimension > 600.dp -> DeviceFormFactor.Tablet

        // Foldable detection (heuristic based on unusual aspect ratios)
        isLandscape && maxDimension / minDimension > 2.1f -> DeviceFormFactor.Foldable

        // Default to phone
        else -> DeviceFormFactor.Phone
    }
}

/**
 * Determine navigation layout based on screen characteristics
 */
private fun determineNavigationLayout(
    screenSize: ScreenSize,
    formFactor: DeviceFormFactor,
    isLandscape: Boolean
): NavigationLayout {
    return when {
        screenSize == ScreenSize.Expanded -> NavigationLayout.NavigationDrawer
        screenSize == ScreenSize.Medium && formFactor != DeviceFormFactor.Phone -> NavigationLayout.NavigationRail
        screenSize == ScreenSize.Medium && isLandscape -> NavigationLayout.NavigationRail
        else -> NavigationLayout.BottomNavigation
    }
}

/**
 * Determine content layout based on screen characteristics
 */
private fun determineContentLayout(
    screenSize: ScreenSize,
    formFactor: DeviceFormFactor,
    isLandscape: Boolean
): ContentLayout {
    return when {
        screenSize == ScreenSize.Expanded && formFactor == DeviceFormFactor.Desktop -> ContentLayout.TriplePane
        screenSize == ScreenSize.Expanded -> ContentLayout.DualPane
        screenSize == ScreenSize.Medium && isLandscape -> ContentLayout.DualPane
        else -> ContentLayout.SinglePane
    }
}

/**
 * Calculate content padding based on layout configuration
 */
private fun calculateContentPadding(
    screenSize: ScreenSize,
    navigationLayout: NavigationLayout,
    layoutDirection: LayoutDirection
): PaddingValues {
    val horizontal = when (screenSize) {
        ScreenSize.Compact -> SpacingTokens.M
        ScreenSize.Medium -> SpacingTokens.L
        ScreenSize.Expanded -> SpacingTokens.XL
    }

    val vertical = when (screenSize) {
        ScreenSize.Compact -> SpacingTokens.S
        ScreenSize.Medium -> SpacingTokens.M
        ScreenSize.Expanded -> SpacingTokens.L
    }

    val startPadding = when (navigationLayout) {
        NavigationLayout.NavigationRail -> SpacingTokens.XXL
        NavigationLayout.NavigationDrawer -> 0.dp
        else -> horizontal
    }

    return PaddingValues(
        start = startPadding,
        top = vertical,
        end = horizontal,
        bottom = when (navigationLayout) {
            NavigationLayout.BottomNavigation -> SpacingTokens.XXL
            else -> vertical
        }
    )
}

/**
 * Product grid responsive configurations
 */
object ProductGridResponsive {
    @Composable
    fun getColumns(): Int {
        val config = LocalResponsiveConfig.current
        return when (config.screenSize) {
            ScreenSize.Compact -> if (config.isLandscape) 3 else 2
            ScreenSize.Medium -> if (config.isLandscape) 4 else 3
            ScreenSize.Expanded -> if (config.isLandscape) 6 else 4
        }
    }

    @Composable
    fun getItemAspectRatio(): Float {
        val config = LocalResponsiveConfig.current
        return when (config.formFactor) {
            DeviceFormFactor.Phone -> 0.8f
            DeviceFormFactor.Tablet -> 0.9f
            DeviceFormFactor.Foldable -> 1.0f
            DeviceFormFactor.Desktop -> 1.0f
        }
    }

    @Composable
    fun getSpacing(): Dp {
        val config = LocalResponsiveConfig.current
        return when (config.screenSize) {
            ScreenSize.Compact -> SpacingTokens.S
            ScreenSize.Medium -> SpacingTokens.M
            ScreenSize.Expanded -> SpacingTokens.L
        }
    }
}

/**
 * Search layout responsive configurations
 */
object SearchResponsive {
    @Composable
    fun shouldUseSearchSuggestions(): Boolean {
        val config = LocalResponsiveConfig.current
        return config.screenSize != ScreenSize.Compact || config.isLandscape
    }

    @Composable
    fun shouldShowFilterChips(): Boolean {
        val config = LocalResponsiveConfig.current
        return config.screenSize >= ScreenSize.Medium
    }

    @Composable
    fun getSearchBarHeight(): Dp {
        val config = LocalResponsiveConfig.current
        return when (config.screenSize) {
            ScreenSize.Compact -> 48.dp
            ScreenSize.Medium -> 56.dp
            ScreenSize.Expanded -> 64.dp
        }
    }
}

/**
 * Modal responsive configurations
 */
object ModalResponsive {
    @Composable
    fun shouldUseFullScreenModal(): Boolean {
        val config = LocalResponsiveConfig.current
        return config.screenSize == ScreenSize.Compact && !config.isLandscape
    }

    @Composable
    fun getModalMaxWidth(): Dp {
        val config = LocalResponsiveConfig.current
        return when (config.screenSize) {
            ScreenSize.Compact -> Dp.Unspecified
            ScreenSize.Medium -> 500.dp
            ScreenSize.Expanded -> 600.dp
        }
    }

    @Composable
    fun getModalPadding(): PaddingValues {
        val config = LocalResponsiveConfig.current
        return config.componentTokens.modalPadding
    }
}

/**
 * Composition Local for responsive configuration
 */
val LocalResponsiveConfig = staticCompositionLocalOf<ResponsiveConfig> {
    error("ResponsiveConfig not provided")
}

/**
 * Responsive theme provider
 */
@Composable
fun ProvideResponsiveTheme(
    content: @Composable () -> Unit
) {
    val responsiveConfig = rememberResponsiveConfig()

    CompositionLocalProvider(
        LocalResponsiveConfig provides responsiveConfig,
        LocalComponentTokens provides responsiveConfig.componentTokens
    ) {
        content()
    }
}

/**
 * Adaptive content container
 */
@Composable
fun AdaptiveContentContainer(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    content: @Composable () -> Unit
) {
    val config = LocalResponsiveConfig.current

    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.systemBars)
            .then(
                if (config.maxContentWidth != Dp.Unspecified) {
                    androidx.compose.ui.Modifier.widthIn(max = config.maxContentWidth)
                } else {
                    androidx.compose.ui.Modifier
                }
            )
    ) {
        content()
    }
}

/**
 * Responsive grid configuration
 */
@Composable
fun rememberResponsiveGridConfig(
    minItemWidth: Dp = 160.dp,
    maxColumns: Int = 6
): ResponsiveGridConfig {
    val config = LocalResponsiveConfig.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidth = with(density) { configuration.screenWidthDp.dp }
    val availableWidth = screenWidth - config.contentPadding.calculateStartPadding(LocalLayoutDirection.current) -
            config.contentPadding.calculateEndPadding(LocalLayoutDirection.current)

    val calculatedColumns = (availableWidth / minItemWidth).toInt().coerceAtMost(maxColumns)
    val finalColumns = calculatedColumns.coerceAtLeast(1)

    return ResponsiveGridConfig(
        columns = finalColumns,
        spacing = when (config.screenSize) {
            ScreenSize.Compact -> SpacingTokens.S
            ScreenSize.Medium -> SpacingTokens.M
            ScreenSize.Expanded -> SpacingTokens.L
        },
        contentPadding = config.contentPadding
    )
}

/**
 * Responsive grid configuration data class
 */
@Immutable
data class ResponsiveGridConfig(
    val columns: Int,
    val spacing: Dp,
    val contentPadding: PaddingValues
)