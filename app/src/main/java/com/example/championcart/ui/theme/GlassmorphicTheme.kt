package com.example.championcart.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Champion Cart - Glassmorphic Theme System
 * Complete glass effect modifiers and overlays for Electric Harmony design
 * Following 2025 design trends with performance optimization
 */

/**
 * Glassmorphic effect intensities
 */
enum class GlassIntensity {
    Light,      // Subtle glass effect - 7% opacity, 10px blur
    Medium,     // Standard glass effect - 12% opacity, 15px blur
    Heavy,      // Strong glass effect - 20% opacity, 20px blur
    Ultra       // Maximum glass effect - 30% opacity, 25px blur
}

/**
 * Glass surface types with predefined configurations
 */
enum class GlassSurfaceType {
    Card,           // Product cards, info cards
    Navigation,     // Bottom nav, top app bar
    Modal,          // Dialogs, bottom sheets
    Background,     // Screen backgrounds
    Interactive,    // Buttons, chips
    Overlay         // Loading overlays, tooltips
}

/**
 * Core glassmorphic modifier - applies glass effect to any composable
 */
fun Modifier.glassmorphic(
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp = 8.dp
): Modifier = composed {
    val colors = LocalExtendedColors.current

    val (backgroundAlpha, blurRadius, borderAlpha) = when (intensity) {
        GlassIntensity.Light -> Triple(0.07f, 10.dp, 0.15f)
        GlassIntensity.Medium -> Triple(0.12f, 15.dp, 0.20f)
        GlassIntensity.Heavy -> Triple(0.20f, 20.dp, 0.25f)
        GlassIntensity.Ultra -> Triple(0.30f, 25.dp, 0.30f)
    }

    this
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = backgroundAlpha),
                    Color.White.copy(alpha = backgroundAlpha * 0.8f)
                ),
                start = Offset(0f, 0f),
                end = Offset(1000f, 1000f)
            )
        )
        .border(
            width = borderWidth,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = borderAlpha),
                    Color.White.copy(alpha = borderAlpha * 0.5f)
                )
            ),
            shape = shape
        )
        .blur(
            radius = blurRadius * 0.1f, // Reduced blur for performance
            edgeTreatment = BlurredEdgeTreatment.Unbounded
        )
}

/**
 * Surface-specific glassmorphic modifiers
 */
fun Modifier.glassCard(
    intensity: GlassIntensity = GlassIntensity.Medium
): Modifier = glassmorphic(
    intensity = intensity,
    shape = GlassmorphicShapes.GlassCard,
    borderWidth = 1.dp,
    shadowElevation = 8.dp
)

fun Modifier.glassNavigation(): Modifier = glassmorphic(
    intensity = GlassIntensity.Heavy,
    shape = GlassmorphicShapes.BottomNavigation,
    borderWidth = 0.5.dp,
    shadowElevation = 12.dp
)

fun Modifier.glassModal(): Modifier = glassmorphic(
    intensity = GlassIntensity.Ultra,
    shape = GlassmorphicShapes.Dialog,
    borderWidth = 1.5.dp,
    shadowElevation = 16.dp
)

fun Modifier.glassButton(
    intensity: GlassIntensity = GlassIntensity.Medium
): Modifier = glassmorphic(
    intensity = intensity,
    shape = GlassmorphicShapes.Button,
    borderWidth = 1.dp,
    shadowElevation = 4.dp
)

fun Modifier.glassChip(): Modifier = glassmorphic(
    intensity = GlassIntensity.Light,
    shape = GlassmorphicShapes.Chip,
    borderWidth = 0.5.dp,
    shadowElevation = 2.dp
)

/**
 * Advanced glassmorphic effects with gradients
 */
fun Modifier.gradientGlass(
    gradientColors: List<Color>,
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier = composed {
    val (backgroundAlpha, blurRadius, borderAlpha) = when (intensity) {
        GlassIntensity.Light -> Triple(0.07f, 10.dp, 0.15f)
        GlassIntensity.Medium -> Triple(0.12f, 15.dp, 0.20f)
        GlassIntensity.Heavy -> Triple(0.20f, 20.dp, 0.25f)
        GlassIntensity.Ultra -> Triple(0.30f, 25.dp, 0.30f)
    }

    this
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                colors = gradientColors.map { it.copy(alpha = backgroundAlpha) }
            )
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = gradientColors.map { it.copy(alpha = borderAlpha) }
            ),
            shape = shape
        )
}

/**
 * Electric Harmony specific glass effects
 */
fun Modifier.electricGlass(): Modifier = composed {
    val colors = LocalExtendedColors.current
    gradientGlass(
        gradientColors = listOf(
            colors.electricMint,
            colors.cosmicPurple
        ),
        intensity = GlassIntensity.Medium,
        shape = GlassmorphicShapes.GlassCard
    )
}

fun Modifier.cosmicGlass(): Modifier = composed {
    val colors = LocalExtendedColors.current
    gradientGlass(
        gradientColors = listOf(
            colors.cosmicPurple,
            colors.deepNavy
        ),
        intensity = GlassIntensity.Heavy,
        shape = GlassmorphicShapes.GlassCardLarge
    )
}

fun Modifier.priceGlass(priceLevel: PriceLevel): Modifier = composed {
    val colors = LocalExtendedColors.current
    val priceColor = when (priceLevel) {
        PriceLevel.Best -> colors.bestPrice
        PriceLevel.Mid -> colors.midPrice
        PriceLevel.High -> colors.highPrice
    }

    gradientGlass(
        gradientColors = listOf(
            priceColor,
            priceColor.copy(alpha = 0.7f)
        ),
        intensity = GlassIntensity.Light,
        shape = GlassmorphicShapes.PriceCard
    )
}

/**
 * Animated glass effects
 */
fun Modifier.breathingGlass(
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier = composed {
    val colors = LocalExtendedColors.current

    // Breathing animation would be implemented with AnimatedVisibility
    // For now, providing static glass effect
    glassmorphic(intensity, shape)
}

fun Modifier.pulsingGlass(
    intensity: GlassIntensity = GlassIntensity.Medium,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier = composed {
    val colors = LocalExtendedColors.current

    // Pulsing animation would be implemented with AnimatedVisibility
    // For now, providing static glass effect with slightly higher intensity
    glassmorphic(
        intensity = when (intensity) {
            GlassIntensity.Light -> GlassIntensity.Medium
            GlassIntensity.Medium -> GlassIntensity.Heavy
            GlassIntensity.Heavy -> GlassIntensity.Ultra
            GlassIntensity.Ultra -> GlassIntensity.Ultra
        },
        shape = shape
    )
}

/**
 * Glass background overlays for screens
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.Light,
    gradientColors: List<Color>? = null,
    content: @Composable () -> Unit
) {
    val colors = LocalExtendedColors.current
    val defaultGradient = listOf(
        colors.gradientStart.copy(alpha = 0.05f),
        colors.gradientMiddle.copy(alpha = 0.03f),
        colors.gradientEnd.copy(alpha = 0.05f)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = gradientColors ?: defaultGradient,
                    radius = 1500f
                )
            )
    ) {
        content()
    }
}

/**
 * Floating glass orbs for background decoration
 */
@Composable
fun FloatingGlassOrbs(
    modifier: Modifier = Modifier,
    orbCount: Int = 3,
    intensity: GlassIntensity = GlassIntensity.Light
) {
    val colors = LocalExtendedColors.current

    Box(modifier = modifier.fillMaxSize()) {
        // Implementation would include actual floating orb animations
        // For now, providing static positioned glass elements
        repeat(orbCount) { index ->
            Box(
                modifier = Modifier
                    .glassmorphic(
                        intensity = intensity,
                        shape = RoundedCornerShape(50.dp)
                    )
            )
        }
    }
}

/**
 * Glass navbar with blur effect
 */
@Composable
fun GlassNavBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .glassNavigation()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        content()
    }
}

/**
 * Glass modal container
 */
@Composable
fun GlassModal(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .glassModal()
            .padding(24.dp)
    ) {
        content()
    }
}

/**
 * Glass product card
 */
@Composable
fun GlassProductCard(
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.Medium,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .glassCard(intensity)
            .padding(16.dp)
    ) {
        content()
    }
}

/**
 * Glass search bar
 */
@Composable
fun GlassSearchBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .glassmorphic(
                intensity = GlassIntensity.Medium,
                shape = GlassmorphicShapes.SearchField,
                borderWidth = 1.dp,
                shadowElevation = 4.dp
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        content()
    }
}

/**
 * Glass tooltip/overlay
 */
@Composable
fun GlassTooltip(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .glassmorphic(
                intensity = GlassIntensity.Heavy,
                shape = RoundedCornerShape(8.dp),
                borderWidth = 0.5.dp,
                shadowElevation = 6.dp
            )
            .padding(12.dp)
    ) {
        content()
    }
}

/**
 * Glass loading overlay
 */
@Composable
fun GlassLoadingOverlay(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    content: @Composable () -> Unit
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .glassmorphic(
                    intensity = GlassIntensity.Ultra,
                    shape = RoundedCornerShape(0.dp)
                )
        ) {
            content()
        }
    }
}

/**
 * Time-based glass effects that change throughout the day
 */
@Composable
fun TimeBasedGlass(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit
) {
    val colors = LocalExtendedColors.current
    val timeAccent = ColorHelpers.getTimeBasedAccent()

    Box(
        modifier = modifier
            .gradientGlass(
                gradientColors = listOf(
                    timeAccent,
                    timeAccent.copy(alpha = 0.7f)
                ),
                intensity = GlassIntensity.Medium,
                shape = shape
            )
            .padding(16.dp)
    ) {
        content()
    }
}

/**
 * Store-specific glass effects
 */
@Composable
fun StoreGlass(
    storeName: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable () -> Unit
) {
    val storeColor = ColorHelpers.getStoreColor(storeName)

    Box(
        modifier = modifier
            .gradientGlass(
                gradientColors = listOf(
                    storeColor,
                    storeColor.copy(alpha = 0.6f)
                ),
                intensity = GlassIntensity.Light,
                shape = shape
            )
            .padding(12.dp)
    ) {
        content()
    }
}

/**
 * Category-specific glass effects
 */
@Composable
fun CategoryGlass(
    category: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable () -> Unit
) {
    val categoryColor = ColorHelpers.getCategoryColor(category)

    Box(
        modifier = modifier
            .gradientGlass(
                gradientColors = listOf(
                    categoryColor,
                    categoryColor.copy(alpha = 0.5f)
                ),
                intensity = GlassIntensity.Light,
                shape = shape
            )
            .padding(12.dp)
    ) {
        content()
    }
}

/**
 * Performance-optimized glass effects
 */
object OptimizedGlass {
    /**
     * Lightweight glass effect for lists and repeated elements
     */
    fun Modifier.lightweightGlass(): Modifier = composed {
        val colors = LocalExtendedColors.current
        this
            .background(colors.glassLight)
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
    }

    /**
     * Static glass effect without animations for better performance
     */
    fun Modifier.staticGlass(
        intensity: GlassIntensity = GlassIntensity.Medium
    ): Modifier = composed {
        val colors = LocalExtendedColors.current
        val backgroundAlpha = when (intensity) {
            GlassIntensity.Light -> 0.07f
            GlassIntensity.Medium -> 0.12f
            GlassIntensity.Heavy -> 0.20f
            GlassIntensity.Ultra -> 0.30f
        }

        this
            .background(Color.White.copy(alpha = backgroundAlpha))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            )
    }
}

/**
 * Glass theme configuration
 */
data class GlassThemeConfig(
    val defaultIntensity: GlassIntensity = GlassIntensity.Medium,
    val enableAnimations: Boolean = true,
    val enableBlur: Boolean = true,
    val performanceMode: Boolean = false
)

/**
 * Glass theme provider
 */
@Composable
fun ProvideGlassTheme(
    config: GlassThemeConfig = GlassThemeConfig(),
    content: @Composable () -> Unit
) {
    // In a real implementation, this would provide the glass theme configuration
    // through CompositionLocal for consistent glass effects throughout the app
    content()
}