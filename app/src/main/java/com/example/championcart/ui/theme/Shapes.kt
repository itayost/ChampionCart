package com.example.championcart.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Champion Cart - Glassmorphic Shape System
 * Electric Harmony design with smooth organic shapes
 * Following 2025 mobile design trends with enhanced accessibility
 */

// Material3 Shapes - Updated for modern glassmorphic design
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Glassmorphic Component Shapes
object GlassmorphicShapes {
    // Cards - Smooth glass-like surfaces
    val GlassCardExtraSmall = RoundedCornerShape(8.dp)
    val GlassCardSmall = RoundedCornerShape(12.dp)
    val GlassCard = RoundedCornerShape(16.dp)
    val GlassCardLarge = RoundedCornerShape(20.dp)
    val GlassCardExtraLarge = RoundedCornerShape(24.dp)
    val GlassCardHero = RoundedCornerShape(28.dp)

    // Buttons - Modern pill shapes with varying roundness
    val ButtonSmall = RoundedCornerShape(18.dp)      // For 36dp height
    val Button = RoundedCornerShape(28.dp)           // For 56dp height (main CTA)
    val ButtonLarge = RoundedCornerShape(32.dp)      // For 64dp height
    val PillButton = RoundedCornerShape(50.dp)       // Full pill for any height
    val SquircleButton = RoundedCornerShape(20.dp)   // Squircle for compact buttons

    // Input Fields - Subtle glass effect
    val TextField = RoundedCornerShape(12.dp)
    val TextFieldSmall = RoundedCornerShape(8.dp)
    val TextFieldLarge = RoundedCornerShape(16.dp)
    val SearchField = RoundedCornerShape(28.dp)      // Pill-shaped search

    // Navigation & Containers
    val BottomNavigation = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val TopAppBar = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 20.dp,
        bottomEnd = 20.dp
    )

    // Bottom Sheets & Modals
    val BottomSheet = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val BottomSheetExpanded = RoundedCornerShape(
        topStart = 32.dp,
        topEnd = 32.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val Dialog = RoundedCornerShape(24.dp)
    val AlertDialog = RoundedCornerShape(20.dp)

    // Product & Store Cards
    val ProductCard = RoundedCornerShape(16.dp)
    val ProductCardLarge = RoundedCornerShape(20.dp)
    val StoreCard = RoundedCornerShape(12.dp)
    val PriceCard = RoundedCornerShape(8.dp)

    // Special Shapes
    val Chip = RoundedCornerShape(16.dp)
    val ChipSmall = RoundedCornerShape(12.dp)
    val Badge = RoundedCornerShape(8.dp)
    val FloatingActionButton = RoundedCornerShape(16.dp)
    val ExtendedFAB = RoundedCornerShape(16.dp)

    // Tab indicators and progress
    val TabIndicator = RoundedCornerShape(2.dp)
    val ProgressIndicator = RoundedCornerShape(4.dp)
    val Slider = RoundedCornerShape(12.dp)
}

// Organic Shapes - For 2025 design trends
object OrganicShapes {
    // Squircle shape - iOS-inspired smooth curves
    val Squircle = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(
                            offset = Offset.Zero,
                            size = size
                        ),
                        cornerRadius = CornerRadius(
                            x = size.minDimension * 0.2f,
                            y = size.minDimension * 0.2f
                        )
                    )
                )
            }
            return Outline.Generic(path)
        }
    }

    // Superellipse - Mathematical smooth curve
    val Superellipse = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val path = Path().apply {
                val width = size.width
                val height = size.height
                val radius = minOf(width, height) * 0.25f

                // Create superellipse using approximation
                addRoundRect(
                    RoundRect(
                        rect = Rect(Offset.Zero, size),
                        cornerRadius = CornerRadius(radius)
                    )
                )
            }
            return Outline.Generic(path)
        }
    }

    // Blob shape - Organic, asymmetric curves
    val BlobShape = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val path = Path().apply {
                val width = size.width
                val height = size.height

                // Create organic blob shape
                moveTo(width * 0.15f, height * 0.3f)
                cubicTo(
                    width * 0.1f, height * 0.1f,
                    width * 0.4f, height * 0.05f,
                    width * 0.7f, height * 0.2f
                )
                cubicTo(
                    width * 0.9f, height * 0.3f,
                    width * 0.95f, height * 0.6f,
                    width * 0.8f, height * 0.8f
                )
                cubicTo(
                    width * 0.6f, height * 0.95f,
                    width * 0.3f, height * 0.9f,
                    width * 0.15f, height * 0.7f
                )
                cubicTo(
                    width * 0.05f, height * 0.5f,
                    width * 0.1f, height * 0.35f,
                    width * 0.15f, height * 0.3f
                )
                close()
            }
            return Outline.Generic(path)
        }
    }

    // Wave shape - For decorative elements
    val WaveShape = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val path = Path().apply {
                val width = size.width
                val height = size.height
                val waveHeight = height * 0.2f

                moveTo(0f, height - waveHeight)
                quadraticBezierTo(
                    width * 0.25f, height,
                    width * 0.5f, height - waveHeight
                )
                quadraticBezierTo(
                    width * 0.75f, height - waveHeight * 2,
                    width, height - waveHeight
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            return Outline.Generic(path)
        }
    }
}

// Responsive Shapes - Adapt to screen size
object ResponsiveShapes {
    // Adaptive corner radius based on screen density
    fun adaptiveCard(density: Density): RoundedCornerShape {
        return with(density) {
            val baseRadius = 16.dp
            val scaleFactor = density.density
            RoundedCornerShape((baseRadius * scaleFactor).toPx().dp)
        }
    }

    fun adaptiveButton(density: Density): RoundedCornerShape {
        return with(density) {
            val baseRadius = 28.dp
            val scaleFactor = density.density
            RoundedCornerShape((baseRadius * scaleFactor).toPx().dp)
        }
    }

    // Shapes that scale with content
    fun scalableShape(baseRadius: Float, scale: Float): RoundedCornerShape {
        return RoundedCornerShape((baseRadius * scale).dp)
    }
}

// Component-Specific Shape Collections
object ComponentShapes {
    // Home Screen Components
    object Home {
        val WelcomeCard = GlassmorphicShapes.GlassCardLarge
        val QuickStatsCard = GlassmorphicShapes.GlassCard
        val FeatureDealCard = GlassmorphicShapes.ProductCardLarge
        val QuickActionButton = GlassmorphicShapes.SquircleButton
    }

    // Search Screen Components
    object Search {
        val SearchBar = GlassmorphicShapes.SearchField
        val FilterChip = GlassmorphicShapes.ChipSmall
        val ResultCard = GlassmorphicShapes.ProductCard
        val PriceComparisonCard = GlassmorphicShapes.PriceCard
    }

    // Product Components
    object Product {
        val ProductImage = RoundedCornerShape(12.dp)
        val ProductCard = GlassmorphicShapes.ProductCard
        val PriceTag = GlassmorphicShapes.Badge
        val AddToCartButton = GlassmorphicShapes.Button
    }

    // Navigation Components
    object Navigation {
        val BottomNav = GlassmorphicShapes.BottomNavigation
        val TopAppBar = GlassmorphicShapes.TopAppBar
        val TabIndicator = GlassmorphicShapes.TabIndicator
        val FAB = GlassmorphicShapes.FloatingActionButton
    }

    // Form Components
    object Forms {
        val InputField = GlassmorphicShapes.TextField
        val InputFieldSmall = GlassmorphicShapes.TextFieldSmall
        val SubmitButton = GlassmorphicShapes.Button
        val SecondaryButton = GlassmorphicShapes.SquircleButton
    }

    // Modal Components
    object Modals {
        val Dialog = GlassmorphicShapes.Dialog
        val BottomSheet = GlassmorphicShapes.BottomSheet
        val AlertDialog = GlassmorphicShapes.AlertDialog
        val Tooltip = RoundedCornerShape(8.dp)
    }
}

// Accessibility Shapes - High contrast mode
object AccessibilityShapes {
    // High contrast shapes with sharper corners for better definition
    val HighContrastCard = RoundedCornerShape(8.dp)
    val HighContrastButton = RoundedCornerShape(4.dp)
    val HighContrastInput = RoundedCornerShape(4.dp)
    val HighContrastDialog = RoundedCornerShape(8.dp)

    // No rounded corners for maximum clarity
    val SharpCard = RoundedCornerShape(0.dp)
    val SharpButton = RoundedCornerShape(0.dp)
}

// Animation Shapes - For morphing animations
object AnimationShapes {
    // Shapes for smooth morphing between states
    val MorphFromCircle = RoundedCornerShape(50.dp)
    val MorphToSquare = RoundedCornerShape(4.dp)
    val MorphIntermediate = RoundedCornerShape(24.dp)

    // Expandable shapes
    val ExpandableCardCollapsed = RoundedCornerShape(12.dp)
    val ExpandableCardExpanded = RoundedCornerShape(20.dp)
}

// Gradient-Compatible Shapes
object GradientShapes {
    // Shapes optimized for glassmorphic gradients
    val GlassOverlay = RoundedCornerShape(16.dp)
    val GradientCard = RoundedCornerShape(20.dp)
    val GradientButton = RoundedCornerShape(28.dp)
    val GradientBackground = RoundedCornerShape(24.dp)
}

// RTL Support Shapes
object RTLShapes {
    // Asymmetric shapes that adapt to RTL layout
    fun directionalCard(isRtl: Boolean): RoundedCornerShape {
        return if (isRtl) {
            RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 16.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 4.dp,
                bottomStart = 16.dp,
                bottomEnd = 4.dp
            )
        }
    }

    fun directionalBottomSheet(isRtl: Boolean): RoundedCornerShape {
        return RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    }
}