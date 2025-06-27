package com.example.championcart.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Enhanced Glassmorphic Card Component
 * Theme-aware glass effect for better visibility
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ComponentShapes.Card.Medium,
    intensity: GlassIntensity = GlassIntensity.Medium,
    elevated: Boolean = false, // For important cards
    content: @Composable ColumnScope.() -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    val glassModifier = modifier.cardGlass(
        intensity = intensity,
        shape = shape,
        darkTheme = darkTheme
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
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
 * Enhanced Hero Glass Card with gradient overlay
 */
@Composable
fun HeroGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = ComponentShapes.Card.Hero,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
    ) {
        // Background gradient for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.05f),
                            ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.05f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                    )
                )
        )

        // Main glass card
        GlassCard(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            intensity = GlassIntensity.Heavy,
            content = content
        )
    }
}

/**
 * Product Glass Card with enhanced visibility
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
            .width(180.dp)
            .height(260.dp),
        shape = ComponentShapes.Product.Card,
        intensity = GlassIntensity.Medium
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

                // Favorite button with glass background
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(
                            if (isFavorite) {
                                ChampionCartColors.Brand.NeonCoral.copy(alpha = 0.2f)
                            } else {
                                Color.White.copy(alpha = 0.1f)
                            }
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            androidx.compose.material.icons.Icons.Filled.Favorite
                        } else {
                            androidx.compose.material.icons.Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) {
                            ChampionCartColors.Brand.NeonCoral
                        } else {
                            ChampionCartTheme.colors.onSurface.copy(alpha = 0.6f)
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

            // Price with glow effect
            val priceColor = when (priceLevel) {
                PriceLevel.Best -> ChampionCartColors.Price.Best
                PriceLevel.Mid -> ChampionCartColors.Price.Mid
                PriceLevel.High -> ChampionCartColors.Price.High
            }

            Box(
                modifier = Modifier
                    .clip(ComponentShapes.Product.Badge)
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
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Store name
            Text(
                text = storeName,
                style = MaterialTheme.typography.bodySmall,
                color = ChampionCartTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Stats Glass Card with enhanced contrast
 */
@Composable
fun StatsGlassCard(
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = ChampionCartTheme.colors.onSurface
                )
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
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        icon()
                    }
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = ChampionCartTheme.colors.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Store Glass Card with branded styling
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
    val storeColor = when (storeName.lowercase()) {
        "shufersal", "שופרסל" -> ChampionCartColors.Store.Shufersal
        "rami levi", "רמי לוי" -> ChampionCartColors.Store.RamiLevi
        "victory", "ויקטורי" -> ChampionCartColors.Store.Victory
        "mega", "מגה" -> ChampionCartColors.Store.Mega
        "osher ad", "אושר עד" -> ChampionCartColors.Store.OsherAd
        "coop", "קופ" -> ChampionCartColors.Store.Coop
        else -> ChampionCartColors.Brand.ElectricMint
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                // Store icon/logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(ComponentShapes.Store.Logo)
                        .background(Color.White.copy(alpha = 0.1f))
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
                    Text(
                        text = "$itemCount items",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChampionCartTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // Right side - Total price
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Total",
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