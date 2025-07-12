package com.example.championcart.presentation.components.products

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddShoppingCart
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.championcart.domain.models.Product
import com.example.championcart.ui.theme.*
import kotlin.math.roundToInt

/**
 * Improved ProductComparisonCard that shows price summary (lowest, average, highest)
 * instead of listing all store prices
 */
@Composable
fun ProductComparisonCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Calculate price statistics
    val prices = product.stores.map { it.price }
    val lowestPrice = prices.minOrNull() ?: product.bestPrice
    val highestPrice = prices.maxOrNull() ?: product.bestPrice
    val averagePrice = if (prices.isNotEmpty()) prices.average() else product.bestPrice

    // Calculate savings
    val savingsPercentage = if (highestPrice > 0) {
        ((highestPrice - lowestPrice) / highestPrice * 100).roundToInt()
    } else 0

    // Find cheapest store info
    val cheapestStore = product.stores.minByOrNull { it.price }
    val storeCount = product.stores.size

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.m)
        ) {
            // Product Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Product Name and Store Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (cheapestStore != null && storeCount > 0) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "${cheapestStore.storeName} • $storeCount חנויות",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Savings Badge
                if (savingsPercentage > 0) {
                    Surface(
                        shape = Shapes.badge,
                        color = PriceColors.Best.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Spacing.s, vertical = Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.TrendingDown,
                                contentDescription = null,
                                tint = PriceColors.Best,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "$savingsPercentage%",
                                style = MaterialTheme.typography.labelMedium,
                                color = PriceColors.Best,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            // Price Summary Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.cardSmall,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.s),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lowest Price
                    PriceSummaryItem(
                        label = "הזול",
                        price = lowestPrice,
                        priceColor = PriceColors.Best,
                        isHighlighted = true,
                        modifier = Modifier.weight(1f)
                    )

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    )

                    // Average Price
                    PriceSummaryItem(
                        label = "ממוצע",
                        price = averagePrice,
                        priceColor = PriceColors.Mid,
                        modifier = Modifier.weight(1f)
                    )

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    )

                    // Highest Price
                    PriceSummaryItem(
                        label = "היקר",
                        price = highestPrice,
                        priceColor = PriceColors.High,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandColors.ElectricMint
                ),
                contentPadding = PaddingValues(Spacing.m)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AddShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.s))
                Text(
                    text = "הוסף",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun PriceSummaryItem(
    label: String,
    price: Double,
    priceColor: Color,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false
) {
    val animatedColor by animateColorAsState(
        targetValue = priceColor,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "price_color_animation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "₪${String.format("%.1f", price)}",
            style = if (isHighlighted) TextStyles.price else TextStyles.priceSmall,
            color = animatedColor,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}