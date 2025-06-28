package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Modern Card Components
 * Electric Harmony Design System
 * Matching the HTML showcase with ultra-rounded, glassmorphic styling
 */

/**
 * Base Glassmorphic Card
 * Foundation for all card components with modern glass effect
 */
@Composable
fun ModernGlassCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp), // Ultra rounded
    glassmorphic: Boolean = true,
    borderGradient: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val config = ChampionCartTheme.config
    val hapticFeedback = LocalHapticFeedback.current
    val hazeState = LocalHazeState.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = if (!config.reduceMotion) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else snap(),
        label = "cardScale"
    )

    val cardModifier = modifier
        .scale(scale)
        .clip(shape)
        .then(
            if (glassmorphic && hazeState != null) {
                Modifier.modernGlass(
                    intensity = GlassIntensity.Medium,
                    shape = shape,
                    hazeState = hazeState
                )
            } else if (glassmorphic) {
                Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.05f)
                    )
                    .blur(10.dp)
            } else {
                Modifier.background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            }
        )
        .then(
            if (borderGradient) {
                Modifier.border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    ),
                    shape = shape
                )
            } else Modifier
        )

    Surface(
        modifier = cardModifier,
        shape = shape,
        color = Color.Transparent,
        onClick = onClick?.let { {
            if (config.enableHaptics) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        } } ?: {},
        enabled = onClick != null,
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                ),
            content = content
        )
    }
}

/**
 * Product Price Card
 * Displays product with price comparison across stores
 */
@Composable
fun ProductPriceCard(
    productName: String,
    imageContent: @Composable (() -> Unit)? = null,
    prices: List<StorePrice>,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bestPrice = prices.minByOrNull { it.price }
    val priceSpread = prices.maxOf { it.price } - prices.minOf { it.price }

    ModernGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.L)
        ) {
            // Product Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    bestPrice?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = SpacingTokens.XS)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                modifier = Modifier.size(SizingTokens.IconXS),
                                tint = ChampionCartColors.Semantic.Warning
                            )
                            Spacer(modifier = Modifier.width(SpacingTokens.XS))
                            Text(
                                text = "חיסכון של ₪${String.format("%.2f", priceSpread)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Product Image
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    imageContent?.invoke() ?: Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingTokens.L))

            // Price Comparison
            prices.take(3).forEachIndexed { index, storePrice ->
                PriceRow(
                    storePrice = storePrice,
                    isBestPrice = storePrice == bestPrice,
                    modifier = Modifier.padding(vertical = SpacingTokens.XS)
                )
            }

            if (prices.size > 3) {
                Text(
                    text = "+${prices.size - 3} חנויות נוספות",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = SpacingTokens.XS)
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.M))

            // Add to Cart Button
            ElectricButton(
                onClick = onAddToCart,
                text = "הוסף לעגלה",
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                size = ButtonSize.Small,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Stats Card with animated number
 */
@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: ImageVector,
    trend: Float? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    var animatedValue by remember { mutableStateOf(0f) }
    val targetValue = remember(value) {
        value.filter { it.isDigit() }.toFloatOrNull() ?: 0f
    }
    val config = ChampionCartTheme.config

    LaunchedEffect(targetValue) {
        if (!config.reduceMotion) {
            animate(
                initialValue = 0f,
                targetValue = targetValue,
                animationSpec = tween(1500, easing = FastOutSlowInEasing)
            ) { animValue, _ ->
                animatedValue = animValue
            }
        } else {
            animatedValue = targetValue
        }
    }

    ModernGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SpacingTokens.L) // 16dp for stats cards
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.1f),
                            accentColor.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(SpacingTokens.L)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.padding(top = SpacingTokens.XS)
                    ) {
                        Text(
                            text = if (value.contains("₪")) {
                                "₪${animatedValue.toInt()}"
                            } else {
                                animatedValue.toInt().toString()
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )

                        trend?.let {
                            val trendColor = if (it > 0) ChampionCartColors.Semantic.Success
                            else ChampionCartColors.Semantic.Error
                            val trendIcon = if (it > 0) Icons.Filled.TrendingUp
                            else Icons.Filled.TrendingDown

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = SpacingTokens.S, bottom = SpacingTokens.XS)
                            ) {
                                Icon(
                                    imageVector = trendIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(SizingTokens.IconXS),
                                    tint = trendColor
                                )
                                Text(
                                    text = "${kotlin.math.abs(it).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = trendColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(SizingTokens.IconM),
                        tint = accentColor
                    )
                }
            }
        }
    }
}

/**
 * Category Card with gradient background
 */
@Composable
fun CategoryCard(
    title: String,
    icon: ImageVector,
    itemCount: Int,
    onClick: () -> Unit,
    gradient: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    ),
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val config = ChampionCartTheme.config
    val hapticFeedback = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = if (!config.reduceMotion) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else snap(),
        label = "categoryScale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .height(140.dp),
        shape = RoundedCornerShape(28.dp), // Very rounded
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        ),
        onClick = {
            if (config.enableHaptics) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradient,
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(SpacingTokens.XL) // 24dp padding
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )

                    Text(
                        text = "$itemCount פריטים",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Store Comparison Card
 * Shows store with total price for cart
 */
@Composable
fun StoreComparisonCard(
    storeName: String,
    totalPrice: String,
    itemCount: Int,
    savings: Float? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val storeColor = getStoreColor(storeName)
    val config = ChampionCartTheme.config

    val animatedBorder by animateFloatAsState(
        targetValue = if (isSelected) 3f else 1f,
        animationSpec = if (!config.reduceMotion) {
            spring(stiffness = Spring.StiffnessMedium)
        } else snap(),
        label = "borderWidth"
    )

    ModernGlassCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = animatedBorder.dp,
                        color = storeColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Store Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(storeColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = storeName.take(2),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = storeColor
                    )
                }

                Spacer(modifier = Modifier.width(SpacingTokens.M))

                Column {
                    Text(
                        text = storeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$itemCount פריטים",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Price Info
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = totalPrice,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (savings != null && savings > 0) {
                        ChampionCartColors.Semantic.Success
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                savings?.let {
                    if (it > 0) {
                        Text(
                            text = "חיסכון: ${it.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampionCartColors.Semantic.Success
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper Components
 */
@Composable
private fun PriceRow(
    storePrice: StorePrice,
    isBestPrice: Boolean,
    modifier: Modifier = Modifier
) {
    val priceColor = when {
        isBestPrice -> ChampionCartColors.Semantic.Success
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isBestPrice) {
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(ChampionCartColors.Semantic.Success.copy(alpha = 0.1f))
                        .padding(horizontal = SpacingTokens.S, vertical = SpacingTokens.XS)
                } else {
                    Modifier.padding(horizontal = SpacingTokens.S, vertical = SpacingTokens.XS)
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isBestPrice) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(SizingTokens.IconXS),
                    tint = ChampionCartColors.Semantic.Success
                )
                Spacer(modifier = Modifier.width(SpacingTokens.XS))
            }
            Text(
                text = storePrice.storeName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isBestPrice) FontWeight.Medium else FontWeight.Normal
            )
        }

        Text(
            text = "₪${String.format("%.2f", storePrice.price)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBestPrice) FontWeight.Bold else FontWeight.Normal,
            color = priceColor
        )
    }
}

/**
 * Data class for store prices
 */
data class StorePrice(
    val storeName: String,
    val price: Float
)