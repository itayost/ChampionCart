package com.example.championcart.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.ui.theme.*

/**
 * Helper function to get store brand color - Updated for Shufersal and Victory only
 */
@Composable
private fun getStoreChainColor(storeName: String): Color {
    val colors = MaterialTheme.extendedColors
    return when (storeName.lowercase()) {
        "shufersal" -> colors.shufersal
        "victory" -> colors.victory
        else -> MaterialTheme.colorScheme.primary
    }
}

/**
 * Helper function to get store display name - Updated for Shufersal and Victory only
 */
private fun getStoreDisplayName(storeName: String): String {
    return when (storeName.lowercase()) {
        "shufersal" -> "Shufersal"
        "victory" -> "Victory"
        else -> storeName
    }
}

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
    onBestStoreClick: (() -> Unit)? = null,
    onWorstStoreClick: (() -> Unit)? = null
) {
    val savings = worstPrice - bestPrice
    val savingsPercent = ((savings / worstPrice) * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationMedium
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            // Header
            Text(
                text = "Price Comparison",
                style = AppTextStyles.priceDisplayLarge,
                fontWeight = FontWeight.Bold
            )

            // Best price
            PriceRow(
                storeName = bestStore,
                price = bestPrice,
                isLowest = true,
                onClick = onBestStoreClick
            )

            // Worst price (if provided)
            worstStore?.let {
                PriceRow(
                    storeName = it,
                    price = worstPrice,
                    isLowest = false,
                    onClick = onWorstStoreClick
                )
            }

            // Savings summary
            if (savings > 0) {
                Surface(
                    shape = ComponentShapes.Card,
                    color = MaterialTheme.extendedColors.savings.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.paddingMedium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "You save:",
                            style = AppTextStyles.priceDisplay,
                            color = MaterialTheme.extendedColors.savings
                        )
                        Text(
                            text = "₪${String.format("%.2f", savings)} ($savingsPercent%)",
                            style = AppTextStyles.priceDisplay,
                            color = MaterialTheme.extendedColors.savings,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual store price row
 */
@Composable
private fun PriceRow(
    storeName: String,
    price: Double,
    isLowest: Boolean,
    onClick: (() -> Unit)? = null
) {
    val storeColor = getStoreChainColor(storeName)
    val priceColor = if (isLowest) {
        MaterialTheme.extendedColors.bestPrice
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = ComponentShapes.CardSmall,
        color = if (isLowest) {
            MaterialTheme.extendedColors.bestPrice.copy(alpha = 0.05f)
        } else {
            Color.Transparent
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
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
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
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
                                    modifier = Modifier.padding(horizontal = Dimensions.paddingSmall, vertical = 2.dp),
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
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall),
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
                    modifier = Modifier.padding(horizontal = Dimensions.paddingSmall, vertical = 2.dp),
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
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall),
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