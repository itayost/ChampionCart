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

    // Card dimensions - COMPLETED
    val productCardWidth = 180.dp   // Increased for better content
    val productCardHeight = 240.dp
    val productCardMinWidth = 160.dp
    val productCardMaxWidth = 200.dp
    val priceCardMinHeight = 100.dp
    val storeCardHeight = 80.dp
    val storeCardWidth = 160.dp
    val dealCardWidth = 160.dp
    val dealCardHeight = 200.dp
    val categoryCardSize = 120.dp
    val savingsCardHeight = 160.dp

    // Image dimensions
    val productImageSmall = 64.dp
    val productImageMedium = 96.dp
    val productImageLarge = 128.dp
    val productImageExtraLarge = 160.dp
    val avatarSizeSmall = 32.dp
    val avatarSizeMedium = 48.dp
    val avatarSizeLarge = 64.dp
    val storeLogoSize = 40.dp
    val brandLogoSize = 32.dp

    // Border widths - ADDED
    val borderNone = 0.dp
    val borderThin = 1.dp         // Standard border for glass effects
    val borderMedium = 2.dp       // For emphasized borders
    val borderThick = 3.dp        // For focus states and high contrast
    val borderExtraThick = 4.dp   // For accessibility and error states

    // Corner radius - For non-shape specific uses
    val cornerRadiusExtraSmall = 4.dp
    val cornerRadiusSmall = 8.dp
    val cornerRadiusMedium = 12.dp
    val cornerRadiusLarge = 16.dp
    val cornerRadiusExtraLarge = 24.dp
    val cornerRadiusHuge = 32.dp
    val cornerRadiusFull = 28.dp  // For pill shapes

    // Elevation/Shadow values - Enhanced
    val elevationNone = 0.dp
    val elevationExtraSmall = 1.dp    // For subtle depth
    val elevationSmall = 2.dp         // For chips and badges
    val elevationMedium = 8.dp        // Default for cards
    val elevationLarge = 16.dp        // For important elements
    val elevationExtraLarge = 24.dp   // For FABs and modals
    val elevationHuge = 32.dp         // For major overlays

    // Glass morphism blur values (reference for implementation)
    val blurSmall = 10.dp         // Converted to pixels in implementation
    val blurMedium = 20.dp        // Standard glass blur
    val blurLarge = 40.dp         // Frosted glass blur
    val blurExtraLarge = 60.dp    // Heavy frosted effects

    // Animation distances
    val animationDistanceSmall = 4.dp
    val animationDistanceMedium = 8.dp
    val animationDistanceLarge = 16.dp
    val animationDistanceHuge = 32.dp

    // Touch target sizes (minimum)
    val touchTargetMin = 48.dp    // Accessibility minimum
    val touchTarget = 56.dp       // Comfortable touch target
    val touchTargetLarge = 64.dp  // Large touch target for important actions

    // Swipe thresholds
    val swipeThreshold = 56.dp
    val swipeVelocityThreshold = 125.dp

    // Bottom sheet specific
    val bottomSheetPeekHeight = 64.dp
    val bottomSheetHandleWidth = 48.dp
    val bottomSheetHandleHeight = 4.dp
    val bottomSheetHandlePadding = 8.dp
    val bottomSheetCornerRadius = 32.dp

    // Dialog dimensions
    val dialogMinWidth = 280.dp
    val dialogMaxWidth = 560.dp
    val dialogMaxHeight = 600.dp
    val dialogCornerRadius = 24.dp

    // Chip dimensions
    val chipHeight = 32.dp
    val chipHeightSmall = 28.dp
    val chipMinWidth = 64.dp
    val chipMaxWidth = 200.dp

    // Badge dimensions
    val badgeSize = 20.dp
    val badgeSizeSmall = 16.dp
    val badgeSizeLarge = 24.dp
    val badgeOffset = 4.dp

    // Loading indicator sizes
    val loadingIndicatorSmall = 24.dp
    val loadingIndicatorMedium = 48.dp
    val loadingIndicatorLarge = 64.dp
    val loadingDotSize = 12.dp
    val loadingDotSpacing = 8.dp

    // Progress bar heights
    val progressBarHeight = 4.dp
    val progressBarHeightMedium = 6.dp
    val progressBarHeightLarge = 8.dp

    // Divider thickness
    val dividerThickness = 1.dp
    val dividerThicknessBold = 2.dp

    // Price display specific
    val priceTagHeight = 32.dp
    val priceTagWidth = 80.dp
    val priceComparisonBarHeight = 48.dp
    val savingsBadgeSize = 80.dp
    val discountBadgeSize = 24.dp

    // Navigation specific
    val navBarItemWidth = 64.dp
    val navBarItemHeight = 56.dp
    val navBarIndicatorHeight = 32.dp
    val navDrawerWidth = 280.dp
    val navRailWidth = 72.dp
    val tabHeight = 48.dp
    val tabMinWidth = 90.dp

    // Grid spacing
    val gridSpacingSmall = 8.dp
    val gridSpacing = 16.dp
    val gridSpacingLarge = 24.dp
    val gridSpacingHuge = 32.dp

    // List item dimensions
    val listItemMinHeight = 56.dp
    val listItemHeight = 72.dp
    val listItemHeightLarge = 88.dp
    val listItemIndent = 16.dp

    // Search specific
    val searchSuggestionHeight = 48.dp
    val searchFilterChipHeight = 32.dp
    val searchBarIconSize = 24.dp

    // Cart specific
    val cartItemHeight = 96.dp
    val cartItemImageSize = 64.dp
    val quantityButtonSize = 32.dp
    val checkoutButtonHeight = 56.dp

    // Store specific
    val storeListItemHeight = 80.dp
    val storeDistanceTagWidth = 60.dp
    val storeRatingSize = 16.dp

    // Profile specific
    val profileAvatarSize = 80.dp
    val profileAvatarLarge = 120.dp
    val settingsItemHeight = 56.dp

    // Responsive breakpoints (for reference)
    val compactWidthMax = 360.dp
    val mediumWidthMin = 360.dp
    val mediumWidthMax = 600.dp
    val expandedWidthMin = 600.dp
    val largeWidthMin = 840.dp

    // Safe area insets (typical values)
    val statusBarHeight = 24.dp
    val navigationBarHeight = 48.dp
    val keyboardHeight = 280.dp  // Typical soft keyboard height

    // Gesture zones
    val edgeSwipeZone = 20.dp
    val pullToRefreshTriggerDistance = 80.dp
    val dragHandleZone = 48.dp

    // Overlay dimensions
    val overlayBlurRadius = 20.dp
    val modalMaxWidth = 400.dp
    val modalMaxHeight = 600.dp

    // Notification dimensions
    val notificationHeight = 64.dp
    val notificationWidth = 280.dp
    val snackbarHeight = 48.dp
    val toastHeight = 56.dp
}

/**
 * Responsive dimensions based on screen size
 */
object ResponsiveDimensions {
    // Screen size categories
    enum class ScreenSize {
        COMPACT,    // < 360dp
        MEDIUM,     // 360dp - 600dp
        EXPANDED,   // 600dp - 840dp
        LARGE       // > 840dp
    }

    /**
     * Get responsive padding based on screen size
     */
    fun getScreenPadding(screenSize: ScreenSize) = when (screenSize) {
        ScreenSize.COMPACT -> 12.dp
        ScreenSize.MEDIUM -> Dimensions.screenPadding
        ScreenSize.EXPANDED -> 24.dp
        ScreenSize.LARGE -> 32.dp
    }

    /**
     * Get responsive card dimensions
     */
    fun getProductCardWidth(screenSize: ScreenSize) = when (screenSize) {
        ScreenSize.COMPACT -> 140.dp
        ScreenSize.MEDIUM -> Dimensions.productCardWidth
        ScreenSize.EXPANDED -> 200.dp
        ScreenSize.LARGE -> 220.dp
    }

    /**
     * Get grid columns based on screen size
     */
    fun getGridColumns(screenSize: ScreenSize) = when (screenSize) {
        ScreenSize.COMPACT -> 2
        ScreenSize.MEDIUM -> 2
        ScreenSize.EXPANDED -> 3
        ScreenSize.LARGE -> 4
    }
}

/**
 * Accessibility dimensions for different needs
 */
object AccessibilityDimensions {
    // Large text scaling
    val largeTextScaleFactor = 1.3f
    val extraLargeTextScaleFactor = 1.5f

    // Touch target scaling
    val largeTouchTargetScaleFactor = 1.2f
    val extraLargeTouchTargetScaleFactor = 1.4f

    // High contrast borders
    val highContrastBorderWidth = Dimensions.borderThick
    val highContrastFocusWidth = 4.dp

    // Motion reduced alternatives
    val reducedMotionAnimationDistance = 2.dp
    val reducedMotionScale = 0.99f
}