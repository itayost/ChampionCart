package com.example.championcart.presentation.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.championcart.ui.theme.*

/**
 * Card components for ChampionCart
 */

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.glass(
                shape = Shapes.card,
                elevation = 2.dp
            ),
            content = content
        )
    }
}

@Composable
fun ProductCard(
    name: String,
    imageUrl: String?,
    price: String,
    storeName: String,
    priceLevel: PriceLevel,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .width(Size.productCardWidth)
            .height(Size.productCardHeight),
        onClick = onClick
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Padding.m)
            ) {
                // Product Image
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = Shapes.cardSmall,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.s))

                // Product Name
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Store Name
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.s))

                // Price and Add Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .priceGlass(priceLevel)
                            .padding(horizontal = Spacing.m, vertical = Spacing.xs)
                    ) {
                        Text(
                            text = price,
                            style = TextStyles.priceSmall,
                            color = when (priceLevel) {
                                PriceLevel.Best -> PriceColors.Best
                                PriceLevel.Mid -> PriceColors.Mid
                                PriceLevel.High -> PriceColors.High
                            }
                        )
                    }

                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddShoppingCart,
                            contentDescription = "הוסף לעגלה",
                            tint = BrandColors.ElectricMint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Best Price Badge
            if (priceLevel == PriceLevel.Best) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Spacing.s)
                        .priceGlass(PriceLevel.Best)
                        .padding(horizontal = Spacing.s, vertical = Spacing.xs)
                ) {
                    Text(
                        text = "מחיר הזול",
                        style = TextStyles.badge,
                        color = PriceColors.Best
                    )
                }
            }
        }
    }
}

@Composable
fun StoreCard(
    storeName: String,
    totalPrice: String,
    itemCount: Int,
    distance: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isRecommended: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = Shapes.card,
        colors = if (isRecommended) {
            CardDefaults.cardColors(
                containerColor = BrandColors.ElectricMint.copy(alpha = 0.08f)
            )
        } else {
            CardDefaults.cardColors()
        },
        border = if (isRecommended) {
            BorderStroke(2.dp, BrandColors.ElectricMint)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.l),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                ) {
                    Text(
                        text = storeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isRecommended) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = BrandColors.ElectricMint,
                                    shape = Shapes.badge
                                )
                                .padding(horizontal = Spacing.s, vertical = 2.dp)
                        ) {
                            Text(
                                text = "מומלץ",
                                style = TextStyles.badge,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$itemCount פריטים",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    distance?.let {
                        Text(
                            text = "• $it ק״מ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = totalPrice,
                    style = TextStyles.price,
                    color = if (isRecommended) {
                        PriceColors.Best
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

@Composable
fun PriceCard(
    storeName: String,
    price: String,
    priceLevel: PriceLevel,
    distance: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = when (priceLevel) {
                PriceLevel.Best -> PriceColors.Best.copy(alpha = 0.08f)
                PriceLevel.Mid -> MaterialTheme.colorScheme.surface
                PriceLevel.High -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.titleSmall
                )
                distance?.let {
                    Text(
                        text = "$it ק״מ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .priceGlass(priceLevel)
                        .padding(horizontal = Spacing.m, vertical = Spacing.xs)
                ) {
                    Text(
                        text = price,
                        style = TextStyles.priceSmall,
                        color = when (priceLevel) {
                            PriceLevel.Best -> PriceColors.Best
                            PriceLevel.Mid -> PriceColors.Mid
                            PriceLevel.High -> PriceColors.High
                        }
                    )
                }
            }
        }
    }
}