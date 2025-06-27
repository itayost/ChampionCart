package com.example.championcart.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

/**
 * Glassmorphic Card Component
 * Core card component with Electric Harmony glass effects
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ComponentShapes.Card.Medium,
    intensity: GlassIntensity = GlassIntensity.Medium,
    elevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = Elevation.Component.card,
        pressedElevation = Elevation.Component.cardPressed,
        hoveredElevation = Elevation.Component.cardHover
    ),
    colors: CardColors = CardDefaults.cardColors(
        containerColor = Color.Transparent,
        contentColor = ChampionCartTheme.colors.onSurface
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    val glassModifier = modifier.glass(
        intensity = intensity,
        shape = shape
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = glassModifier,
            enabled = enabled,
            shape = shape,
            colors = colors,
            elevation = elevation,
            content = content
        )
    } else {
        Card(
            modifier = glassModifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            content = content
        )
    }
}

/**
 * Hero Glass Card for featured content
 */
@Composable
fun HeroGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = ComponentShapes.Card.Hero,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Sizing.Card.heroMinHeight)
            .gradientGlass(
                colors = ChampionCartColors.Gradient.electricHarmony.map {
                    it.copy(alpha = 0.1f)
                },
                intensity = GlassIntensity.Heavy,
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Elevation.Component.card
        ),
        content = content
    )
}

/**
 * Product Glass Card
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
    onFavoriteClick: () -> Unit = {}
) {
    GlassCard(
        onClick = onClick,
        modifier = modifier
            .width(Sizing.Card.productWidth)
            .height(Sizing.Card.productHeight),
        shape = ComponentShapes.Product.Card
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Component.paddingM)
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                productImage()

                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = null,
                        tint = if (isFavorite) {
                            ChampionCartColors.Brand.NeonCoral
                        } else {
                            ChampionCartTheme.colors.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.s))

            // Product Name
            Text(
                text = productName,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Store Name
            Text(
                text = storeName,
                style = CustomTextStyles.storeName,
                color = ChampionCartTheme.colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.s))

            // Price
            PriceTag(
                price = price,
                priceLevel = priceLevel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Store Glass Card
 */
@Composable
fun StoreGlassCard(
    storeName: String,
    storeIcon: @Composable () -> Unit,
    itemCount: Int,
    totalPrice: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        onClick = onClick,
        modifier = modifier
            .storeGlass(storeName)
            .fillMaxWidth(),
        shape = ComponentShapes.Store.Card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Component.paddingM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Store Icon
                Box(
                    modifier = Modifier.size(Sizing.Icon.xl)
                ) {
                    storeIcon()
                }

                // Store Info
                Column {
                    Text(
                        text = storeName,
                        style = CustomTextStyles.storeName
                    )
                    Text(
                        text = "$itemCount פריטים",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChampionCartTheme.colors.onSurfaceVariant
                    )
                }
            }

            // Total Price
            Text(
                text = totalPrice,
                style = CustomTextStyles.price,
                color = ChampionCartTheme.colors.primary
            )
        }
    }
}

/**
 * Summary Glass Card
 */
@Composable
fun SummaryGlassCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.Light
) {
    GlassCard(
        modifier = modifier,
        intensity = intensity
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Component.paddingL),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChampionCartTheme.colors.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    icon()
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = ChampionCartTheme.colors.primary
                )
            }
        }
    }
}