package com.example.championcart.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Champion Cart - Glassmorphic Shape System
 * Modern shapes with smooth corners for glass effects
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
    val FabExtended = RoundedCornerShape(28.dp)

    // Glass containers
    val GlassContainer = RoundedCornerShape(20.dp)
    val GlassContainerSmall = RoundedCornerShape(16.dp)
    val GlassContainerLarge = RoundedCornerShape(24.dp)

    // Price tags with unique shape
    val PriceTag = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 16.dp,
        bottomEnd = 16.dp,
        bottomStart = 4.dp
    )

    // Store card shape
    val StoreCard = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomEnd = 16.dp,
        bottomStart = 16.dp
    )

    // Navigation bar items
    val NavBarItem = RoundedCornerShape(12.dp)
    val NavBarIndicator = RoundedCornerShape(24.dp)

    // Tooltips and popovers
    val Tooltip = RoundedCornerShape(8.dp)
    val Popover = RoundedCornerShape(12.dp)

    // Image containers
    val ImageSmall = RoundedCornerShape(8.dp)
    val ImageMedium = RoundedCornerShape(12.dp)
    val ImageLarge = RoundedCornerShape(16.dp)

    // Special shapes
    val Circle = CircleShape
    val Squircle = RoundedCornerShape(25)  // Super ellipse approximation
}

/**
 * Custom shape for animated morphing effects
 */
class MorphShape(
    private val cornerRadius: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = cornerRadius,
                    radiusY = cornerRadius
                )
            )
        }
        return Outline.Generic(path)
    }
}

/**
 * Animated shape that can transition between different corner radii
 */
@Composable
fun animatedShape(
    targetRadius: Float,
    animationSpec: androidx.compose.animation.core.AnimationSpec<Float> =
        androidx.compose.animation.core.spring()
): Shape {
    val animatedRadius = androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetRadius,
        animationSpec = animationSpec,
        label = "shape_animation"
    ).value
    return MorphShape(animatedRadius)
}

/**
 * Special shape for price comparison cards with cut corner
 */
class PriceCardShape(
    private val cutCornerSize: Float = 20f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            // Start from top-left with cut corner
            moveTo(cutCornerSize, 0f)
            lineTo(size.width - cutCornerSize, 0f)

            // Top-right corner
            cubicTo(
                size.width, 0f,
                size.width, cutCornerSize,
                size.width, cutCornerSize
            )

            // Right edge
            lineTo(size.width, size.height - cutCornerSize)

            // Bottom-right corner
            cubicTo(
                size.width, size.height,
                size.width - cutCornerSize, size.height,
                size.width - cutCornerSize, size.height
            )

            // Bottom edge
            lineTo(cutCornerSize, size.height)

            // Bottom-left corner
            cubicTo(
                0f, size.height,
                0f, size.height - cutCornerSize,
                0f, size.height - cutCornerSize
            )

            // Left edge
            lineTo(0f, cutCornerSize)

            // Top-left cut corner
            lineTo(cutCornerSize, 0f)

            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Squircle shape for modern app icons and special buttons
 */
class SquircleShape(
    private val cornerSmoothing: Float = 0.6f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = createSquirclePath(
            width = size.width,
            height = size.height,
            smoothing = cornerSmoothing
        )
        return Outline.Generic(path)
    }

    private fun createSquirclePath(
        width: Float,
        height: Float,
        smoothing: Float
    ): Path {
        val path = Path()
        val radius = minOf(width, height) * 0.5f * smoothing

        // This is a simplified squircle approximation
        path.addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = 0f,
                top = 0f,
                right = width,
                bottom = height,
                radiusX = radius,
                radiusY = radius
            )
        )

        return path
    }
}

/**
 * Ticket shape with scalloped edges for deals/coupons
 */
class TicketShape(
    private val cornerRadius: Float = 16f,
    private val scallopsCount: Int = 8
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val scallopRadius = size.height / (scallopsCount * 4f)
            val scallopDiameter = scallopRadius * 2

            // Top edge with rounded corners
            moveTo(cornerRadius, 0f)
            lineTo(size.width - cornerRadius, 0f)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    size.width - cornerRadius * 2, 0f,
                    size.width, cornerRadius * 2
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Right edge with scallops
            var y = cornerRadius
            while (y < size.height - cornerRadius) {
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        size.width - scallopDiameter, y,
                        size.width, y + scallopDiameter
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                y += scallopDiameter
            }

            // Bottom edge
            lineTo(size.width, size.height - cornerRadius)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    size.width - cornerRadius * 2, size.height - cornerRadius * 2,
                    size.width, size.height
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(cornerRadius, size.height)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    0f, size.height - cornerRadius * 2,
                    cornerRadius * 2, size.height
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Left edge with scallops
            y = size.height - cornerRadius
            while (y > cornerRadius) {
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        -scallopDiameter, y - scallopDiameter,
                        0f, y
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                y -= scallopDiameter
            }

            // Complete top-left corner
            lineTo(0f, cornerRadius)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    0f, 0f,
                    cornerRadius * 2, cornerRadius * 2
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            close()
        }
        return Outline.Generic(path)
    }
}

// Pre-defined shape instances for common use
object AppShapes {
    val SmallGlass = ComponentShapes.GlassContainerSmall
    val MediumGlass = ComponentShapes.GlassContainer
    val LargeGlass = ComponentShapes.GlassContainerLarge

    val PriceCard = PriceCardShape()
    val DealTicket = TicketShape()
    val AppIcon = SquircleShape()

    val BottomSheetLarge = RoundedCornerShape(
        topStart = 40.dp,
        topEnd = 40.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
}