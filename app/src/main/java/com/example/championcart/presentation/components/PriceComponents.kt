package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.presentation.theme.*

/**
 * Main price comparison card showing best/worst prices with savings
 */
@Composable
fun PriceComparisonCard(
    bestPrice: Double,
    worstPrice: Double,
    bestStore: String,
    worstStore: String? = null,
    modifier: Modifier = Modifier,
    onBestStoreClick: (() -> Unit)? = null
) {
    val savings = worstPrice - bestPrice
    val savingsPercent = if (worstPrice > 0) ((savings / worstPrice) * 100) else 0.0
    val colors = MaterialTheme.extendedColors

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = colors.savings.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.Card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.card)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Best Price
                Column {
                    Text(
                        text = "Best Price",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₪${String.format("%.2f", bestPrice)}",
                        style = AppTextStyles.priceDisplayLarge,
                        color = colors.priceLow,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        onClick = { onBestStoreClick?.invoke() },
                        enabled = onBestStoreClick != null,
                        shape = ComponentShapes.Chip,
                        color = getStoreChainColor(bestStore).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = getStoreDisplayName(bestStore),
                            style = AppTextStyles.storeName,
                            color = getStoreChainColor(bestStore),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                // Savings Badge
                if (savings > 0) {
                    SavingsBadge(
                        savings = savings,
                        savingsPercent = savingsPercent
                    )
                }
            }

            // Comparison with worst price
            if (worstStore != null && savings > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Compared to ${getStoreDisplayName(worstStore)}:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₪${String.format("%.2f", worstPrice)}",
                        style = AppTextStyles.priceDisplay,
                        textDecoration = TextDecoration.LineThrough,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Animated savings badge showing amount and percentage saved
 */
@Composable
fun SavingsBadge(
    savings: Double,
    savingsPercent: Double,
    modifier: Modifier = Modifier
) {
    var isAnimated by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0.8f,
        animationSpec = tween(500),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        isAnimated = true
    }

    Surface(
        modifier = modifier.scale(scale),
        shape = ComponentShapes.DealBadge,
        color = MaterialTheme.extendedColors.bestDeal,
        shadowElevation = Elevation.Level3
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SAVE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "₪${String.format("%.2f", savings)}",
                style = AppTextStyles.priceDisplay,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${savingsPercent.toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Individual store price row with color coding
 */
@Composable
fun StorePriceRow(
    storeName: String,
    price: Double,
    isLowest: Boolean = false,
    isHighest: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val storeColor = getStoreChainColor(storeName)
    val priceColor = when {
        isLowest -> MaterialTheme.extendedColors.priceLow
        isHighest -> MaterialTheme.extendedColors.priceHigh
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.ListItem,
        color = if (isLowest) {
            MaterialTheme.extendedColors.savings.copy(alpha = 0.08f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Store info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Store color indicator
                Box(
                    modifier = Modifier
                        .size(4.dp, 32.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(storeColor)
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = getStoreDisplayName(storeName).uppercase(),
                            style = AppTextStyles.storeName,
                            color = storeColor,
                            fontWeight = if (isLowest) FontWeight.Bold else FontWeight.Medium
                        )

                        if (isLowest) {
                            Surface(
                                shape = ComponentShapes.Badge,
                                color = MaterialTheme.extendedColors.bestDeal
                            ) {
                                Text(
                                    text = "BEST",
                                    style = AppTextStyles.badgeText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Price
            Text(
                text = "₪${String.format("%.2f", price)}",
                style = AppTextStyles.priceDisplay,
                color = priceColor,
                fontWeight = if (isLowest) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

/**
 * Compact price tag for product cards
 */
@Composable
fun PriceTag(
    price: Double,
    originalPrice: Double? = null,
    modifier: Modifier = Modifier
) {
    val hasDiscount = originalPrice != null && originalPrice > price
    val discountPercent = if (hasDiscount && originalPrice != null) {
        ((originalPrice - price) / originalPrice * 100).toInt()
    } else 0

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Current price
        Text(
            text = "₪${String.format("%.2f", price)}",
            style = AppTextStyles.priceDisplay,
            color = if (hasDiscount) {
                MaterialTheme.extendedColors.savings
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        // Original price (strikethrough)
        if (hasDiscount && originalPrice != null) {
            Text(
                text = "₪${String.format("%.2f", originalPrice)}",
                style = AppTextStyles.priceDisplaySmall,
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        // Discount badge
        if (hasDiscount && discountPercent > 0) {
            Surface(
                shape = ComponentShapes.Badge,
                color = MaterialTheme.extendedColors.bestDeal
            ) {
                Text(
                    text = "-$discountPercent%",
                    style = AppTextStyles.badgeText,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Mini price indicator for lists
 */
@Composable
fun PriceIndicator(
    lowestPrice: Double,
    highestPrice: Double,
    modifier: Modifier = Modifier
) {
    val hasPriceRange = highestPrice > lowestPrice

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "₪${String.format("%.2f", lowestPrice)}",
            style = AppTextStyles.priceDisplaySmall,
            color = MaterialTheme.extendedColors.priceLow,
            fontWeight = FontWeight.Bold
        )

        if (hasPriceRange) {
            Text(
                text = "-",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "₪${String.format("%.2f", highestPrice)}",
                style = AppTextStyles.priceDisplaySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}