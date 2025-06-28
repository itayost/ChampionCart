package com.example.championcart.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.ChampionCartAnimations
import com.example.championcart.ui.theme.ChampionCartColors
import com.example.championcart.ui.theme.ChampionCartTheme
import com.example.championcart.ui.theme.ComponentShapes
import com.example.championcart.ui.theme.GlassIntensity
import com.example.championcart.ui.theme.LocalHazeState
import com.example.championcart.ui.theme.PriceLevel
import com.example.championcart.ui.theme.getPriceLevelColor
import com.example.championcart.ui.theme.getPriceLevelGlowColor
import com.example.championcart.ui.theme.getStoreColor
import com.example.championcart.ui.theme.modernGlass

/**
 * Enhanced Glassmorphic Card Component
 * Theme-aware glass effect with modern animations
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ComponentShapes.Card.Medium,
    intensity: GlassIntensity = GlassIntensity.Medium,
    elevated: Boolean = false,
    shimmer: Boolean = false, // New parameter for shimmer effect
    content: @Composable ColumnScope.() -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val config = ChampionCartTheme.config
    val haptics = LocalHapticFeedback.current
    val hazeState = LocalHazeState.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Animation for press state
    val animatedScale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed -> 0.98f
            isHovered -> 1.01f
            else -> 1f
        },
        animationSpec = if (!config.reduceMotion) {
            ChampionCartAnimations.Springs.CardInteraction
        } else {
            snap()
        },
        label = "cardScale"
    )

    // Shimmer animation
    val infiniteTransition = if (shimmer && !config.reduceMotion) {
        rememberInfiniteTransition(label = "shimmer")
    } else null

    val shimmerOffset by infiniteTransition?.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ChampionCartAnimations.Durations.Elaborate,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    ) ?: mutableStateOf(0f)

    val glassModifier = modifier
        .graphicsLayer {
            scaleX = animatedScale
            scaleY = animatedScale
        }
        .modernGlass(
            intensity = intensity,
            shape = shape,
            hazeState = hazeState,
            shimmer = shimmer && isHovered
        )

    if (onClick != null) {
        Card(
            onClick = {
                if (config.enableHaptics) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            },
            modifier = glassModifier,
            enabled = enabled,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = ChampionCartTheme.colors.onSurface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            ),
            interactionSource = interactionSource,
            content = content
        )
    } else {
        Surface(
            modifier = glassModifier,
            shape = shape,
            color = Color.Transparent,
            contentColor = ChampionCartTheme.colors.onSurface
        ) {
            Column(content = content)
        }
    }
}

/**
 * Enhanced Hero Glass Card with animated gradient
 */
@Composable
fun HeroGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = ComponentShapes.Card.Hero,
    animated: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val config = ChampionCartTheme.config

    // Animated gradient for hero cards
    val infiniteTransition = if (animated && !config.reduceMotion && config.enableMicroAnimations) {
        rememberInfiniteTransition(label = "heroGradient")
    } else null

    val gradientOffset by infiniteTransition?.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    ) ?: mutableStateOf(500f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
    ) {
        // Animated background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.08f),
                            ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.06f),
                            ChampionCartColors.Brand.NeonCoral.copy(alpha = 0.04f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(gradientOffset, gradientOffset)
                    )
                )
        )

        // Glow effect
        if (!config.reduceMotion) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 4.dp, y = 4.dp)
                    .blur(20.dp)
                    .clip(shape)
                    .background(
                        ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f)
                    )
            )
        }

        // Main glass card
        GlassCard(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            intensity = GlassIntensity.Heavy,
            shimmer = true,
            content = content
        )
    }
}

/**
 * Enhanced Product Glass Card with micro-animations
 */
@Composable
fun ProductGlassCard(
    productName: String,
    productImage: @Composable () -> Unit,
    price: String,
    storeName: String,
    priceLevel: PriceLevel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    isNew: Boolean = false // New parameter for highlighting new products
) {
    val config = ChampionCartTheme.config

    // Pulse animation for best price
    val infiniteTransition = if (priceLevel == PriceLevel.Best && !config.reduceMotion) {
        rememberInfiniteTransition(label = "pricePulse")
    } else null

    val pulseScale by infiniteTransition?.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = ChampionCartAnimations.Easings.AccelerateDecelerate
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    ) ?: mutableStateOf(1f)

    GlassCard(
        onClick = onClick,
        modifier = modifier
            .width(180.dp)
            .height(260.dp),
        shape = ComponentShapes.Product.Card,
        intensity = GlassIntensity.Medium,
        shimmer = isNew
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Product Image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(ComponentShapes.Product.Image)
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                productImage()

                // New badge
                if (isNew) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(ComponentShapes.Special.Badge)
                            .background(ChampionCartColors.Accent.CyberYellow)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "חדש",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Enhanced favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .then(
                            if (isFavorite) {
                                Modifier.background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            ChampionCartColors.Brand.NeonCoral.copy(alpha = 0.3f),
                                            ChampionCartColors.Brand.NeonCoral.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                            } else {
                                Modifier.background(Color.White.copy(alpha = 0.1f))
                            }
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) {
                            ChampionCartColors.Brand.NeonCoral
                        } else {
                            ChampionCartTheme.colors.onSurface.copy(alpha = 0.6f)
                        },
                        modifier = Modifier.graphicsLayer {
                            if (isFavorite && !config.reduceMotion) {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product name
            Text(
                text = productName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                color = ChampionCartTheme.colors.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Enhanced price with animation
            val priceColor = getPriceLevelColor(priceLevel)
            val priceGlowColor = getPriceLevelGlowColor(priceLevel)

            Box(
                modifier = Modifier
                    .clip(ComponentShapes.Product.Badge)
                    .then(
                        if (priceLevel == PriceLevel.Best && !config.reduceMotion) {
                            Modifier.drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            priceGlowColor.copy(alpha = 0.3f),
                                            Color.Transparent
                                        ),
                                        radius = size.width
                                    )
                                )
                            }
                        } else Modifier
                    )
                    .background(priceColor.copy(alpha = 0.1f))
                    .border(
                        width = 1.dp,
                        color = priceColor.copy(alpha = 0.3f),
                        shape = ComponentShapes.Product.Badge
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleMedium,
                    color = priceColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Store name with store color
            val storeColor = getStoreColor(storeName)
            Text(
                text = storeName,
                style = MaterialTheme.typography.bodySmall,
                color = storeColor.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Enhanced Stats Glass Card with animated values
 */
@Composable
fun StatsGlassCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.Light,
    trend: TrendDirection? = null // New parameter for trend indication
) {
    val config = ChampionCartTheme.config

    GlassCard(
        modifier = modifier,
        intensity = intensity,
        shimmer = trend == TrendDirection.UP
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = ChampionCartTheme.colors.onSurface
                    )

                    // Trend indicator
                    trend?.let {
                        Icon(
                            imageVector = when (it) {
                                TrendDirection.UP -> Icons.Default.TrendingUp
                                TrendDirection.DOWN -> Icons.Default.TrendingDown
                                TrendDirection.STABLE -> Icons.Default.TrendingFlat
                            },
                            contentDescription = null,
                            tint = when (it) {
                                TrendDirection.UP -> ChampionCartColors.Semantic.Success
                                TrendDirection.DOWN -> ChampionCartColors.Semantic.Error
                                TrendDirection.STABLE -> ChampionCartTheme.colors.onSurfaceVariant
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChampionCartTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        icon()
                    }
                }

                // Animated value text
                AnimatedContent(
                    targetState = value,
                    transitionSpec = {
                        if (!config.reduceMotion) {
                            slideInVertically { -it } + fadeIn() togetherWith
                                    slideOutVertically { it } + fadeOut()
                        } else {
                            fadeIn() togetherWith fadeOut()
                        }
                    },
                    label = "valueAnimation"
                ) { targetValue ->
                    Text(
                        text = targetValue,
                        style = MaterialTheme.typography.headlineMedium,
                        color = ChampionCartTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Enhanced Store Glass Card with hover effects
 */
@Composable
fun StoreGlassCard(
    storeName: String,
    storeIcon: @Composable () -> Unit,
    itemCount: Int,
    totalPrice: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    discount: Float? = null // New parameter for showing discounts
) {
    val storeColor = getStoreColor(storeName)
    val config = ChampionCartTheme.config
    val haptics = LocalHapticFeedback.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = if (!config.reduceMotion) {
            ChampionCartAnimations.Springs.CardInteraction
        } else {
            snap()
        },
        label = "storeCardScale"
    )

    Card(
        onClick = {
            if (config.enableHaptics) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .shadow(
                elevation = 6.dp,
                shape = ComponentShapes.Store.Card,
                ambientColor = storeColor.copy(alpha = 0.1f),
                spotColor = storeColor.copy(alpha = 0.15f)
            )
            .clip(ComponentShapes.Store.Card)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        storeColor.copy(alpha = 0.1f),
                        storeColor.copy(alpha = 0.08f),
                        storeColor.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        storeColor.copy(alpha = 0.4f),
                        storeColor.copy(alpha = 0.2f)
                    )
                ),
                shape = ComponentShapes.Store.Card
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = ComponentShapes.Store.Card,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Store info
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced store icon with glow
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(ComponentShapes.Store.Logo)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    storeColor.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = storeColor.copy(alpha = 0.3f),
                            shape = ComponentShapes.Store.Logo
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    storeIcon()
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = storeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChampionCartTheme.colors.onSurface
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$itemCount פריטים",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ChampionCartTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        // Discount badge
                        discount?.let {
                            Box(
                                modifier = Modifier
                                    .clip(ComponentShapes.Special.Badge)
                                    .background(ChampionCartColors.Semantic.Success.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "-${it.toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ChampionCartColors.Semantic.Success,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Right side - Total price
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "סה״כ",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChampionCartTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = totalPrice,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ChampionCartTheme.colors.primary
                )
            }
        }
    }
}

/**
 * Trend direction for stats cards
 */
enum class TrendDirection {
    UP, DOWN, STABLE
}