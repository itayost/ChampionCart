package com.example.championcart.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Champion Cart - Modern Dimensions System
 * Consistent spacing and sizing for Electric Harmony design
 */
object Dimensions {
    // Padding values - Updated for modern spacious design
    val paddingExtraSmall = 4.dp
    val paddingSmall = 8.dp
    val paddingMedium = 16.dp
    val paddingLarge = 24.dp
    val paddingExtraLarge = 32.dp
    val paddingHuge = 48.dp

    // Specific padding for glassmorphic components
    val screenPadding = 20.dp  // Increased for modern look
    val cardPadding = 20.dp    // More spacious cards
    val listItemPadding = 16.dp
    val chipPadding = 12.dp
    val dialogPadding = 24.dp
    val bottomSheetPadding = 24.dp

    // Spacing values - For gaps between elements
    val spacingExtraSmall = 4.dp
    val spacingSmall = 8.dp
    val spacingMedium = 16.dp
    val spacingLarge = 24.dp
    val spacingExtraLarge = 32.dp
    val spacingHuge = 48.dp

    // Icon sizes - Updated for better visibility
    val iconSizeExtraSmall = 16.dp
    val iconSizeSmall = 20.dp
    val iconSizeMedium = 24.dp
    val iconSizeLarge = 32.dp
    val iconSizeExtraLarge = 48.dp
    val iconSizeHuge = 64.dp

    // Component heights - Following modern design specs
    val buttonHeightSmall = 36.dp
    val buttonHeight = 56.dp      // Main CTA height from design
    val buttonHeightLarge = 64.dp

    val searchBarHeight = 56.dp
    val bottomNavHeight = 72.dp   // With gesture area
    val appBarHeight = 64.dp
    val extendedAppBarHeight = 128.dp

    // FAB sizes
    val fabSizeMini = 48.dp
    val fabSize = 64.dp           // Main FAB from design
    val fabSizeExtended = 120.dp  // Min width for extended FAB

    // Card dimensions
    val productCardWidth = 180.dp  // Increased for better content
    val productCardHeight = 240.dp
    val priceCardMinHeight = 100.dp
    val storeCardHeight = 80.dp
    val dealCardWidth = 160.dp
    val dealCardHeight = 200.dp

    // Image dimensions
    val productImageSmall = 64.dp
    val productImageMedium = 96.dp
    val productImageLarge = 128.dp
    val avatarSizeSmall = 32.dp
    val avatarSizeMedium = 48.dp
    val avatarSizeLarge = 64.dp

    // Corner radius - For non-shape specific uses
    val cornerRadiusExtraSmall = 4.dp
    val cornerRadiusSmall = 8.dp
    val cornerRadiusMedium = 12.dp
    val cornerRadiusLarge = 16.dp
    val cornerRadiusExtraLarge = 24.dp
    val cornerRadiusHuge = 32.dp
    val cornerRadiusFull = 28.dp  // For pill shapes

    // Elevation/Shadow values
    val elevationNone = 0.dp
    val elevationSmall = 2.dp
    val elevationMedium = 8.dp    // Default for cards
    val elevationLarge = 16.dp
    val elevationHuge = 24.dp     // For FABs and important elements

    // Glass morphism blur values (not directly in dp, but for reference)
    val blurSmall = 10.dp         // Converted to pixels in implementation
    val blurMedium = 20.dp        // Standard glass blur
    val blurLarge = 40.dp         // Frosted glass blur

    // Border widths
    val borderNone = 0.dp
    val borderThin = 1.dp
    val borderMedium = 2.dp
    val borderThick = 3.dp        // For focus states

    // Animation distances
    val animationDistanceSmall = 4.dp
    val animationDistanceMedium = 8.dp
    val animationDistanceLarge = 16.dp

    // Touch target sizes (minimum)
    val touchTargetMin = 48.dp    // Accessibility minimum
    val touchTarget = 56.dp        // Comfortable touch target

    // Swipe thresholds
    val swipeThreshold = 56.dp
    val swipeVelocityThreshold = 125.dp

    // Bottom sheet specific
    val bottomSheetPeekHeight = 64.dp
    val bottomSheetHandleWidth = 48.dp
    val bottomSheetHandleHeight = 4.dp
    val bottomSheetHandlePadding = 8.dp

    // Dialog dimensions
    val dialogMinWidth = 280.dp
    val dialogMaxWidth = 560.dp
    val dialogMaxHeight = 600.dp

    // Chip dimensions
    val chipHeight = 32.dp
    val chipMinWidth = 64.dp

    // Badge dimensions
    val badgeSize = 20.dp
    val badgeSizeSmall = 16.dp
    val badgeOffset = 4.dp

    // Loading indicator sizes
    val loadingIndicatorSmall = 24.dp
    val loadingIndicatorMedium = 48.dp
    val loadingIndicatorLarge = 64.dp

    // Progress bar heights
    val progressBarHeight = 4.dp
    val progressBarHeightLarge = 8.dp

    // Divider thickness
    val dividerThickness = 1.dp
    val dividerThicknessBold = 2.dp

    // Price display specific
    val priceTagHeight = 32.dp
    val priceComparisonBarHeight = 48.dp
    val savingsBadgeSize = 80.dp

    // Navigation specific
    val navBarItemWidth = 64.dp
    val navBarIndicatorHeight = 32.dp
    val navDrawerWidth = 280.dp
    val navRailWidth = 72.dp

    // Grid spacing
    val gridSpacingSmall = 8.dp
    val gridSpacing = 16.dp
    val gridSpacingLarge = 24.dp

    // Responsive breakpoints (for reference)
    val compactWidthMax = 360.dp
    val mediumWidthMin = 360.dp
    val mediumWidthMax = 600.dp
    val expandedWidthMin = 600.dp

    // Safe area insets (typical values)
    val statusBarHeight = 24.dp
    val navigationBarHeight = 48.dp

    // Gesture zones
    val edgeSwipeZone = 20.dp
    val pullToRefreshTriggerDistance = 80.dp
}

/**
 * Responsive dimensions based on screen size
 */
object ResponsiveDimensions {
    // Compact phone adjustments
    val compactPadding = Dimensions.paddingMedium - 4.dp  // 12.dp
    val compactSpacing = Dimensions.spacingMedium - 4.dp  // 12.dp
    val compactButtonHeight = 48.dp

    // Large phone/tablet adjustments
    val expandedPadding = Dimensions.paddingLarge        // 24.dp
    val expandedSpacing = Dimensions.spacingLarge        // 24.dp
    val expandedButtonHeight = 64.dp
}

/**
 * Animation durations (in milliseconds)
 */
object AnimationDurations {
    const val instant = 0
    const val fast = 150
    const val medium = 300
    const val slow = 600
    const val verySlow = 1000

    // Specific animations
    const val colorTransition = 600
    const val shapeTransition = 300
    const val fadeIn = 300
    const val fadeOut = 150
    const val slideIn = 400
    const val slideOut = 250
    const val scaleIn = 300
    const val scaleOut = 200
    const val rotation = 400
    const val shimmer = 1200
    const val priceCount = 800
    const val glowPulse = 2000
}

/**
 * Spring animation specs
 */
object SpringSpecs {
    const val stiffnessVeryLow = 50f
    const val stiffnessLow = 200f
    const val stiffnessMedium = 400f
    const val stiffnessMediumHigh = 600f
    const val stiffnessHigh = 800f
    const val stiffnessVeryHigh = 1000f

    const val dampingRatioNoBounce = 1f
    const val dampingRatioLowBounce = 0.85f
    const val dampingRatioMediumBounce = 0.65f
    const val dampingRatioHighBounce = 0.45f
}

/**
 * Glass morphism parameters
 */
object GlassParams {
    const val blurLight = 20f        // pixels
    const val blurMedium = 40f
    const val blurHeavy = 60f

    const val saturationDefault = 1.8f
    const val saturationHigh = 2.2f

    const val noiseOpacity = 0.02f   // Subtle texture
}