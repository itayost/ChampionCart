package com.example.championcart.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Champion Cart - Complete Glassmorphic Shape System
 * Modern shapes with smooth corners for glass effects and organic designs
 * FIXED VERSION - No compilation errors
 */

// Material3 Shapes - Updated for modern design
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Component-specific shapes for glassmorphic design
object ComponentShapes {
    // Cards with smooth corners
    val CardSmall = RoundedCornerShape(12.dp)
    val Card = RoundedCornerShape(16.dp)
    val CardLarge = RoundedCornerShape(24.dp)

    // Buttons - Pill shapes for CTAs
    val ButtonSmall = RoundedCornerShape(20.dp)
    val Button = RoundedCornerShape(28.dp)  // Pill shape for 56dp height
    val ButtonLarge = RoundedCornerShape(32.dp)
    val PillButton = RoundedCornerShape(50.dp) // Full pill for any height

    // Input fields with subtle rounding
    val TextField = RoundedCornerShape(12.dp)
    val TextFieldSmall = RoundedCornerShape(8.dp)

    // Bottom sheets with top corners only
    val BottomSheet = RoundedCornerShape(
        topStart = 32.dp,
        topEnd = 32.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    // Dialogs and modals
    val Dialog = RoundedCornerShape(24.dp)
    val DialogSmall = RoundedCornerShape(16.dp)

    // Search bar - Full pill shape
    val SearchBar = RoundedCornerShape(28.dp)

    // Chips and badges
    val Chip = RoundedCornerShape(16.dp)
    val ChipSmall = RoundedCornerShape(12.dp)
    val Badge = RoundedCornerShape(8.dp)
    val BadgeSmall = RoundedCornerShape(6.dp)

    // FAB shapes
    val Fab = RoundedCornerShape(16.dp)
    val FabLarge = RoundedCornerShape(28.dp)
    val FabExtended = RoundedCornerShape(24.dp)

    // Price tags and labels
    val PriceTag = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 12.dp,
        bottomEnd = 12.dp,
        bottomStart = 0.dp
    )
    val Label = RoundedCornerShape(8.dp)

    // Glass containers - FIXED MISSING SHAPE
    val GlassContainer = RoundedCornerShape(20.dp)
    val GlassCard = RoundedCornerShape(16.dp)
    val GlassPanel = RoundedCornerShape(24.dp)

    // Navigation specific
    val BottomNavContainer = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    val TabIndicator = RoundedCornerShape(16.dp)

    // Product specific
    val ProductCard = RoundedCornerShape(16.dp)
    val ProductImage = RoundedCornerShape(12.dp)
    val StoreCard = RoundedCornerShape(12.dp)

    // Special effect shapes
    val Glow = RoundedCornerShape(20.dp)
    val Shimmer = RoundedCornerShape(8.dp)

    // Organic shapes for 2025 design trends (simplified versions)
    val OrganicCard = RoundedCornerShape(20.dp) // Simplified to rounded corners
    val FlowingContainer = RoundedCornerShape(24.dp) // Simplified to rounded corners
}

/**
 * Organic shape following 2025 design trends
 * Creates flowing, natural curves instead of perfect rectangles
 * SIMPLIFIED VERSION to avoid compilation issues
 */
class OrganicShape(
    private val curviness: Float = 0.1f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val curve = width * curviness

            // Start from top-left with organic curve
            moveTo(curve, 0f)

            // Top edge with subtle waves
            cubicTo(
                width * 0.3f, -curve * 0.5f,
                width * 0.7f, curve * 0.5f,
                width - curve, 0f
            )

            // Top-right corner
            cubicTo(width, 0f, width, curve, width, curve)

            // Right edge
            cubicTo(
                width + curve * 0.3f, height * 0.3f,
                width - curve * 0.3f, height * 0.7f,
                width, height - curve
            )

            // Bottom-right corner
            cubicTo(width, height, width - curve, height, width - curve, height)

            // Bottom edge
            cubicTo(
                width * 0.7f, height + curve * 0.5f,
                width * 0.3f, height - curve * 0.5f,
                curve, height
            )

            // Bottom-left corner
            cubicTo(0f, height, 0f, height - curve, 0f, height - curve)

            // Left edge
            cubicTo(
                -curve * 0.3f, height * 0.7f,
                curve * 0.3f, height * 0.3f,
                0f, curve
            )

            // Close path
            cubicTo(0f, 0f, curve, 0f, curve, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Flowing shape that mimics liquid or soft materials
 * SIMPLIFIED VERSION to avoid compilation issues
 */
class FlowingShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height

            // Create flowing blob-like shape
            moveTo(width * 0.2f, 0f)

            // Top flowing curve
            cubicTo(
                width * 0.4f, -height * 0.1f,
                width * 0.8f, height * 0.1f,
                width, height * 0.3f
            )

            // Right side flow
            cubicTo(
                width * 1.1f, height * 0.6f,
                width * 0.9f, height * 0.9f,
                width * 0.7f, height
            )

            // Bottom flow
            cubicTo(
                width * 0.4f, height * 1.1f,
                width * 0.1f, height * 0.9f,
                0f, height * 0.6f
            )

            // Left side flow back to start
            cubicTo(
                -width * 0.1f, height * 0.3f,
                width * 0.1f, height * 0.1f,
                width * 0.2f, 0f
            )

            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Simple morphing shape that transitions between rectangle and circle
 * SIMPLIFIED VERSION - no compilation errors
 */
class SimpleMorphingShape(
    private val morphProgress: Float = 0f // 0f = rectangle, 1f = circle
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val minSize = kotlin.math.min(size.width, size.height)
        val maxCornerRadius = minSize / 2f
        val cornerRadius = 16f + (maxCornerRadius - 16f) * morphProgress

        val shape = RoundedCornerShape(cornerRadius)
        return shape.createOutline(size, layoutDirection, density)
    }
}