package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Price Display Components
 * Specialized components for showing prices with Electric Harmony styling
 */

/**
 * Price Tag Component
 */
@Composable
fun PriceTag(
    price: String,
    priceLevel: PriceLevel,
    modifier: Modifier = Modifier,
    showCurrency: Boolean = true,
    animate: Boolean = true
) {
    val priceColor = when (priceLevel) {
        PriceLevel.Best -> ChampionCartColors.Price.Best
        PriceLevel.Mid -> ChampionCartColors.Price.Mid
        PriceLevel.High -> ChampionCartColors.Price.High
    }

    val glowColor = when (priceLevel) {
        PriceLevel.Best -> ChampionCartColors.Price.BestGlow
        PriceLevel.Mid -> ChampionCartColors.Price.MidGlow
        PriceLevel.High -> ChampionCartColors.Price.HighGlow
    }

    val animatedColor by animateColorAsState(
        targetValue = if (animate && priceLevel == PriceLevel.Best) {
            priceColor
        } else {
            priceColor
        },
        animationSpec = tween(600),
        label = "priceColor"
    )

    Box(
        modifier = modifier
            .priceGlass(priceLevel)
            .clip(PriceShapes.Container),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Spacing.m,
                vertical = Spacing.s
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showCurrency) {
                Text(
                    text = "₪",
                    style = CustomTextStyles.priceSmall,
                    color = animatedColor
                )
            }
            Text(
                text = price,
                style = CustomTextStyles.price,
                color = animatedColor,
                fontWeight = if (priceLevel == PriceLevel.Best) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
            )
        }

        // Best price indicator
        if (priceLevel == PriceLevel.Best && animate) {
            BestPriceIndicator()
        }
    }
}

/**
 * Best Price Indicator Animation
 */
@Composable
private fun BestPriceIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "bestPrice")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = 0.3f
            }
            .background(
                ChampionCartColors.Price.BestGlow,
                shape = PriceShapes.Container
            )
    )
}

/**
 * Price Comparison Row
 */
@Composable
fun PriceComparisonRow(
    storeName: String,
    price: String,
    priceLevel: PriceLevel,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = ChampionCartColors.Brand.ElectricMint,
                        shape = ComponentShapes.Card.Small
                    )
                } else {
                    Modifier
                }
            ),
        shape = ComponentShapes.Card.Small,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f)
            } else {
                Color.Transparent
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .storeGlass(storeName)
                .padding(Spacing.Component.paddingM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Store name
            Text(
                text = storeName,
                style = CustomTextStyles.storeName,
                modifier = Modifier.weight(1f)
            )

            // Price with indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (priceLevel == PriceLevel.Best) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Best price",
                        tint = ChampionCartColors.Price.Best,
                        modifier = Modifier.size(Sizing.Icon.s)
                    )
                }

                PriceTag(
                    price = price,
                    priceLevel = priceLevel,
                    animate = false
                )
            }
        }
    }
}

/**
 * Total Price Display
 */
@Composable
fun TotalPriceDisplay(
    totalPrice: String,
    savings: String? = null,
    itemCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .gradientGlass(
                colors = ChampionCartColors.Gradient.electricHarmony.map {
                    it.copy(alpha = 0.1f)
                },
                intensity = GlassIntensity.Medium
            ),
        shape = ComponentShapes.Card.Large,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Component.paddingL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "סה״כ לתשלום",
                style = MaterialTheme.typography.titleMedium,
                color = ChampionCartTheme.colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.s))

            Text(
                text = "₪$totalPrice",
                style = CustomTextStyles.priceLarge,
                color = ChampionCartTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "$itemCount פריטים",
                style = MaterialTheme.typography.bodySmall,
                color = ChampionCartTheme.colors.onSurfaceVariant
            )

            if (savings != null) {
                Spacer(modifier = Modifier.height(Spacing.m))

                Row(
                    modifier = Modifier
                        .successGlass()
                        .clip(ComponentShapes.Special.Chip)
                        .padding(
                            horizontal = Spacing.m,
                            vertical = Spacing.s
                        ),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = ChampionCartColors.Semantic.Success,
                        modifier = Modifier.size(Sizing.Icon.s)
                    )
                    Text(
                        text = "חסכת ₪$savings",
                        style = MaterialTheme.typography.labelLarge,
                        color = ChampionCartColors.Semantic.Success,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Price History Indicator
 */
@Composable
fun PriceHistoryIndicator(
    currentPrice: String,
    previousPrice: String?,
    trend: PriceTrend,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "₪$currentPrice",
            style = CustomTextStyles.price
        )

        if (previousPrice != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (trend) {
                        PriceTrend.UP -> Icons.Default.TrendingUp
                        PriceTrend.DOWN -> Icons.Default.TrendingDown
                        PriceTrend.STABLE -> Icons.Default.TrendingFlat
                    },
                    contentDescription = null,
                    tint = when (trend) {
                        PriceTrend.UP -> ChampionCartColors.Semantic.Error
                        PriceTrend.DOWN -> ChampionCartColors.Semantic.Success
                        PriceTrend.STABLE -> ChampionCartTheme.colors.onSurfaceVariant
                    },
                    modifier = Modifier.size(Sizing.Icon.s)
                )

                Text(
                    text = previousPrice,
                    style = MaterialTheme.typography.bodySmall,
                    color = ChampionCartTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}

enum class PriceTrend {
    UP, DOWN, STABLE
}