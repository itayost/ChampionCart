package com.example.championcart.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Champion Cart - Component Design Tokens
 * Consistent spacing, sizing, and layout patterns for Electric Harmony design
 * Following 2025 mobile design trends with Hebrew-first considerations
 */

/**
 * Core spacing system - 4dp base unit with modern scaling
 */
object SpacingTokens {
    // Base spacing units
    val None = 0.dp
    val XXS = 2.dp      // 0.5x
    val XS = 4.dp       // 1x base
    val S = 8.dp        // 2x base
    val M = 12.dp       // 3x base
    val L = 16.dp       // 4x base
    val XL = 20.dp      // 5x base
    val XXL = 24.dp     // 6x base
    val XXXL = 32.dp    // 8x base
    val Huge = 40.dp    // 10x base
    val Massive = 48.dp // 12x base
    val Giant = 64.dp   // 16x base

    // Content spacing
    val ContentXS = 8.dp
    val ContentS = 12.dp
    val ContentM = 16.dp
    val ContentL = 20.dp
    val ContentXL = 24.dp
    val ContentXXL = 32.dp

    // Screen margins
    val ScreenMarginS = 16.dp
    val ScreenMarginM = 20.dp
    val ScreenMarginL = 24.dp
    val ScreenMarginXL = 32.dp

    // Component internal padding
    val ComponentXS = 8.dp
    val ComponentS = 12.dp
    val ComponentM = 16.dp
    val ComponentL = 20.dp
    val ComponentXL = 24.dp

    // List item spacing
    val ListItemXS = 4.dp
    val ListItemS = 8.dp
    val ListItemM = 12.dp
    val ListItemL = 16.dp
    val ListItemXL = 20.dp
}

/**
 * Component sizing tokens
 */
object SizingTokens {
    // Icon sizes
    val IconXS = 16.dp
    val IconS = 20.dp
    val IconM = 24.dp
    val IconL = 28.dp
    val IconXL = 32.dp
    val IconXXL = 40.dp
    val IconHuge = 48.dp
    val IconMassive = 64.dp

    // Button heights
    val ButtonS = 32.dp
    val ButtonM = 40.dp
    val ButtonL = 48.dp
    val ButtonXL = 56.dp
    val ButtonXXL = 64.dp

    // Button widths (minimum)
    val ButtonMinWidthS = 64.dp
    val ButtonMinWidthM = 88.dp
    val ButtonMinWidthL = 112.dp
    val ButtonMinWidthXL = 140.dp

    // Input field heights
    val InputS = 40.dp
    val InputM = 48.dp
    val InputL = 56.dp
    val InputXL = 64.dp

    // Card dimensions
    val CardMinHeight = 80.dp
    val CardProductWidth = 160.dp
    val CardProductHeight = 200.dp
    val CardStoreWidth = 120.dp
    val CardStoreHeight = 80.dp

    // Navigation heights
    val NavigationBarHeight = 72.dp
    val TopAppBarHeight = 64.dp
    val TabBarHeight = 48.dp
    val SearchBarHeight = 56.dp

    // FAB sizes
    val FABMini = 40.dp
    val FABS = 48.dp
    val FABM = 56.dp
    val FABL = 64.dp
    val FABExtendedMinWidth = 96.dp

    // Avatar sizes
    val AvatarXS = 24.dp
    val AvatarS = 32.dp
    val AvatarM = 40.dp
    val AvatarL = 48.dp
    val AvatarXL = 64.dp
    val AvatarXXL = 96.dp

    // Divider thickness
    val DividerThin = 0.5.dp
    val DividerRegular = 1.dp
    val DividerThick = 2.dp
    val DividerBold = 4.dp
}

/**
 * Component-specific design tokens
 */
@Immutable
data class ComponentTokens(
    // Card tokens
    val cardPadding: Dp,
    val cardSpacing: Dp,
    val cardMinHeight: Dp,
    val cardElevation: Dp,

    // Button tokens
    val buttonHeight: Dp,
    val buttonPadding: PaddingValues,
    val buttonSpacing: Dp,
    val buttonMinWidth: Dp,

    // Input tokens
    val inputHeight: Dp,
    val inputPadding: PaddingValues,
    val inputSpacing: Dp,

    // List tokens
    val listItemHeight: Dp,
    val listItemPadding: PaddingValues,
    val listItemSpacing: Dp,

    // Navigation tokens
    val navigationHeight: Dp,
    val navigationPadding: PaddingValues,
    val navigationItemSpacing: Dp,

    // Modal tokens
    val modalPadding: PaddingValues,
    val modalSpacing: Dp,
    val modalMaxWidth: Dp,

    // Typography spacing
    val textSpacingTight: Dp,
    val textSpacingNormal: Dp,
    val textSpacingLoose: Dp,

    // Icon spacing
    val iconTextSpacing: Dp,
    val iconButtonSpacing: Dp,

    // Border widths
    val borderThin: Dp,
    val borderRegular: Dp,
    val borderThick: Dp
)

/**
 * Default component tokens
 */
val DefaultComponentTokens = ComponentTokens(
    // Card tokens
    cardPadding = SpacingTokens.L,
    cardSpacing = SpacingTokens.M,
    cardMinHeight = SizingTokens.CardMinHeight,
    cardElevation = 4.dp,

    // Button tokens
    buttonHeight = SizingTokens.ButtonXL,
    buttonPadding = PaddingValues(horizontal = SpacingTokens.XL, vertical = SpacingTokens.M),
    buttonSpacing = SpacingTokens.M,
    buttonMinWidth = SizingTokens.ButtonMinWidthL,

    // Input tokens
    inputHeight = SizingTokens.InputL,
    inputPadding = PaddingValues(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
    inputSpacing = SpacingTokens.L,

    // List tokens
    listItemHeight = 72.dp,
    listItemPadding = PaddingValues(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
    listItemSpacing = SpacingTokens.XS,

    // Navigation tokens
    navigationHeight = SizingTokens.NavigationBarHeight,
    navigationPadding = PaddingValues(horizontal = SpacingTokens.L, vertical = SpacingTokens.S),
    navigationItemSpacing = SpacingTokens.XS,

    // Modal tokens
    modalPadding = PaddingValues(SpacingTokens.XL),
    modalSpacing = SpacingTokens.L,
    modalMaxWidth = 400.dp,

    // Typography spacing
    textSpacingTight = SpacingTokens.XS,
    textSpacingNormal = SpacingTokens.M,
    textSpacingLoose = SpacingTokens.L,

    // Icon spacing
    iconTextSpacing = SpacingTokens.S,
    iconButtonSpacing = SpacingTokens.M,

    // Border widths
    borderThin = SizingTokens.DividerThin,
    borderRegular = SizingTokens.DividerRegular,
    borderThick = SizingTokens.DividerThick
)

/**
 * Compact component tokens for smaller screens
 */
val CompactComponentTokens = DefaultComponentTokens.copy(
    cardPadding = SpacingTokens.M,
    cardSpacing = SpacingTokens.S,
    buttonHeight = SizingTokens.ButtonL,
    buttonPadding = PaddingValues(horizontal = SpacingTokens.L, vertical = SpacingTokens.S),
    inputHeight = SizingTokens.InputM,
    inputPadding = PaddingValues(horizontal = SpacingTokens.M, vertical = SpacingTokens.S),
    listItemHeight = 64.dp,
    listItemPadding = PaddingValues(horizontal = SpacingTokens.M, vertical = SpacingTokens.S),
    modalPadding = PaddingValues(SpacingTokens.L),
    textSpacingNormal = SpacingTokens.S
)

/**
 * Expanded component tokens for larger screens
 */
val ExpandedComponentTokens = DefaultComponentTokens.copy(
    cardPadding = SpacingTokens.XL,
    cardSpacing = SpacingTokens.L,
    buttonHeight = SizingTokens.ButtonXXL,
    buttonPadding = PaddingValues(horizontal = SpacingTokens.XXL, vertical = SpacingTokens.L),
    inputHeight = SizingTokens.InputXL,
    inputPadding = PaddingValues(horizontal = SpacingTokens.XL, vertical = SpacingTokens.L),
    listItemHeight = 80.dp,
    listItemPadding = PaddingValues(horizontal = SpacingTokens.XL, vertical = SpacingTokens.L),
    modalPadding = PaddingValues(SpacingTokens.Huge),
    modalMaxWidth = 600.dp,
    textSpacingNormal = SpacingTokens.L
)

/**
 * Component-specific token collections
 */
object ProductTokens {
    val CardWidth = SizingTokens.CardProductWidth
    val CardHeight = SizingTokens.CardProductHeight
    val CardPadding = SpacingTokens.M
    val CardSpacing = SpacingTokens.S
    val ImageHeight = 120.dp
    val PriceTextSpacing = SpacingTokens.XS
    val ActionButtonHeight = SizingTokens.ButtonM
}

object StoreTokens {
    val CardWidth = SizingTokens.CardStoreWidth
    val CardHeight = SizingTokens.CardStoreHeight
    val CardPadding = SpacingTokens.S
    val LogoSize = SizingTokens.IconXL
    val TextSpacing = SpacingTokens.XS
}

object SearchTokens {
    val BarHeight = SizingTokens.SearchBarHeight
    val BarPadding = PaddingValues(horizontal = SpacingTokens.L, vertical = SpacingTokens.M)
    val FilterChipSpacing = SpacingTokens.S
    val FilterChipPadding = PaddingValues(horizontal = SpacingTokens.M, vertical = SpacingTokens.XS)
    val ResultCardSpacing = SpacingTokens.M
    val SuggestionItemHeight = 48.dp
}

object NavigationTokens {
    val BottomBarHeight = SizingTokens.NavigationBarHeight
    val BottomBarPadding = PaddingValues(horizontal = SpacingTokens.S, vertical = SpacingTokens.XS)
    val TabIndicatorHeight = 3.dp
    val IconSize = SizingTokens.IconM
    val LabelSpacing = SpacingTokens.XXS
    val ItemMinWidth = 64.dp
    val ItemMaxWidth = 120.dp
}

object ModalTokens {
    val BottomSheetPadding = PaddingValues(SpacingTokens.XL)
    val BottomSheetHeaderHeight = 64.dp
    val DialogPadding = PaddingValues(SpacingTokens.XL)
    val DialogMinWidth = 280.dp
    val DialogMaxWidth = 400.dp
    val DialogSpacing = SpacingTokens.L
    val ActionSpacing = SpacingTokens.M
}

object FormTokens {
    val FieldSpacing = SpacingTokens.L
    val LabelSpacing = SpacingTokens.XS
    val ErrorSpacing = SpacingTokens.XS
    val GroupSpacing = SpacingTokens.XL
    val SectionSpacing = SpacingTokens.XXL
    val SubmitButtonHeight = SizingTokens.ButtonXL
}

/**
 * Layout tokens for screen organization
 */
object LayoutTokens {
    // Screen margins
    val ScreenMarginCompact = SpacingTokens.ScreenMarginS
    val ScreenMarginMedium = SpacingTokens.ScreenMarginM
    val ScreenMarginExpanded = SpacingTokens.ScreenMarginL

    // Content max widths
    val ContentMaxWidthCompact = 400.dp
    val ContentMaxWidthMedium = 600.dp
    val ContentMaxWidthExpanded = 800.dp
    val ContentMaxWidthFull = 1200.dp

    // Grid spacing
    val GridSpacingTight = SpacingTokens.S
    val GridSpacingNormal = SpacingTokens.M
    val GridSpacingLoose = SpacingTokens.L

    // Section spacing
    val SectionSpacingS = SpacingTokens.L
    val SectionSpacingM = SpacingTokens.XL
    val SectionSpacingL = SpacingTokens.XXL
    val SectionSpacingXL = SpacingTokens.XXXL
}

/**
 * Animation timing tokens
 */
object AnimationTokens {
    // Duration tokens (in milliseconds)
    const val DurationInstant = 0
    const val DurationQuick = 100
    const val DurationFast = 150
    const val DurationStandard = 300
    const val DurationSlow = 500
    const val DurationComplex = 800

    // Delay tokens
    const val DelayNone = 0
    const val DelayQuick = 50
    const val DelayStandard = 100
    const val DelaySlow = 200

    // Stagger delays for list animations
    const val StaggerDelay = 50
    const val StaggerDelayLong = 100
}

/**
 * Accessibility tokens
 */
object A11yTokens {
    // Minimum touch targets
    val MinTouchTarget = 48.dp
    val RecommendedTouchTarget = 56.dp
    val LargeTouchTarget = 64.dp

    // Focus indicators
    val FocusIndicatorWidth = 2.dp
    val FocusIndicatorPadding = 2.dp

    // High contrast adjustments
    val HighContrastBorderWidth = 2.dp
    val HighContrastSpacing = SpacingTokens.M
}

/**
 * Performance tokens
 */
object PerformanceTokens {
    // Blur radii for glass effects
    val BlurRadiusLight = 8.dp
    val BlurRadiusMedium = 12.dp
    val BlurRadiusHeavy = 16.dp
    val BlurRadiusMax = 20.dp

    // Shadow elevations
    val ShadowElevationLight = 2.dp
    val ShadowElevationMedium = 4.dp
    val ShadowElevationHeavy = 8.dp
    val ShadowElevationMax = 12.dp
}

/**
 * Hebrew/RTL specific tokens
 */
object RTLTokens {
    // Text alignment adjustments
    val TextMarginStart = SpacingTokens.L
    val TextMarginEnd = SpacingTokens.S

    // Icon positioning
    val IconMarginStart = SpacingTokens.M
    val IconMarginEnd = SpacingTokens.XS

    // Navigation adjustments
    val NavItemPaddingStart = SpacingTokens.L
    val NavItemPaddingEnd = SpacingTokens.M
}

/**
 * Composition Local for component tokens
 */
val LocalComponentTokens = staticCompositionLocalOf { DefaultComponentTokens }

/**
 * Extension properties for easy access
 */
val ComponentTokens.current: ComponentTokens
    @Composable get() = LocalComponentTokens.current

/**
 * Responsive token selection helpers
 */
@Composable
fun rememberResponsiveTokens(
    compact: ComponentTokens = CompactComponentTokens,
    medium: ComponentTokens = DefaultComponentTokens,
    expanded: ComponentTokens = ExpandedComponentTokens
): ComponentTokens {
    // In a real implementation, this would check screen size
    // For now, returning default tokens
    return medium
}

/**
 * Token validation helpers
 */
object TokenValidation {
    fun validateSpacing(value: Dp): Dp {
        return maxOf(value, SpacingTokens.None)
    }

    fun validateSize(value: Dp): Dp {
        return maxOf(value, SizingTokens.IconXS)
    }

    fun validateTouchTarget(value: Dp): Dp {
        return maxOf(value, A11yTokens.MinTouchTarget)
    }
}