package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.domain.models.PriceComparison
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Product Components
 * Updated to work with API data models (GroupedProduct, StorePrice)
 */

/**
 * Main Product Card - Electric Harmony Design
 * Works with GroupedProduct from API
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: GroupedProduct,
    onProductClick: () -> Unit,
    onAddToCart: (StorePrice) -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    isCompact: Boolean = false,
    selectedStoreId: String? = null
) {
    val haptics = LocalHapticFeedback.current
    val lowestPrice = product.prices.minByOrNull { it.price }
    val highestPrice = product.prices.maxByOrNull { it.price }

    Card(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onProductClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .then(if (isCompact) Modifier.aspectRatio(0.75f) else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.extended.surfaceGlass
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        shape = GlassmorphicShapes.GlassCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) SpacingTokens.S else SpacingTokens.M)
        ) {
            // Product Icon/Image placeholder with Favorite Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isCompact) 0.4f else 0.3f)
            ) {
                // Product Icon based on category
                ProductIconPlaceholder(
                    productName = product.itemName,
                    modifier = Modifier.fillMaxSize()
                )

                // Favorite Button
                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = onFavoriteToggle,
                    modifier = Modifier.align(Alignment.TopEnd)
                )

                // Discount Badge if applicable
                if (product.savings > 5) {
                    DiscountBadge(
                        savingsPercent = ((product.savings / (highestPrice?.price ?: 1.0)) * 100).toInt(),
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            // Product Info
            ProductInfo(
                product = product,
                isCompact = isCompact,
                modifier = Modifier.weight(if (isCompact) 0.3f else 0.35f)
            )

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            // Price Section with store selection
            PriceSection(
                product = product,
                selectedStoreId = selectedStoreId,
                isCompact = isCompact,
                onStoreSelected = { storePrice ->
                    onAddToCart(storePrice)
                },
                modifier = Modifier.weight(if (isCompact) 0.3f else 0.35f)
            )
        }
    }
}

/**
 * Compact Product Card for grid views
 */
@Composable
fun CompactProductCard(
    product: GroupedProduct,
    onProductClick: () -> Unit,
    onAddToCart: (StorePrice) -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false
) {
    ProductCard(
        product = product,
        onProductClick = onProductClick,
        onAddToCart = onAddToCart,
        onFavoriteToggle = { /* Handle favorite */ },
        modifier = modifier,
        isFavorite = isFavorite,
        isCompact = true
    )
}

/**
 * List Product Card for list views
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListProductCard(
    product: GroupedProduct,
    onProductClick: () -> Unit,
    onAddToCart: (StorePrice) -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false
) {
    val lowestPrice = product.prices.minByOrNull { it.price }
    val haptics = LocalHapticFeedback.current

    Card(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onProductClick()
        },
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.extended.surfaceGlass
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = GlassmorphicShapes.GlassCardSmall
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.M),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            // Product Icon
            ProductIconPlaceholder(
                productName = product.itemName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(GlassmorphicShapes.GlassCardSmall)
            )

            // Product Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
            ) {
                Text(
                    text = product.itemName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Weight/Unit info if available
                if (product.weight != null && product.unit != null) {
                    Text(
                        text = "${product.weight}${product.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Price range
                lowestPrice?.let { price ->
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
                    ) {
                        Text(
                            text = "₪${String.format("%.2f", price.price)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.extended.electricMint,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ב-${price.chain}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.S),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = onFavoriteToggle
                )

                lowestPrice?.let { price ->
                    FilledIconButton(
                        onClick = { onAddToCart(price) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.extended.electricMint
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "הוסף לעגלה",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Product Icon Placeholder based on product name
 */
@Composable
fun ProductIconPlaceholder(
    productName: String,
    modifier: Modifier = Modifier
) {
    val icon = getProductIcon(productName)
    val backgroundColor = getProductCategoryColor(productName)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = backgroundColor,
            modifier = Modifier.size(if (modifier == Modifier) 48.dp else 32.dp)
        )
    }
}

/**
 * Product Information Section
 */
@Composable
private fun ProductInfo(
    product: GroupedProduct,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
    ) {
        // Product Name
        Text(
            text = product.itemName,
            style = if (isCompact) {
                MaterialTheme.typography.bodyMedium
            } else {
                MaterialTheme.typography.bodyLarge
            },
            fontWeight = FontWeight.Medium,
            maxLines = if (isCompact) 1 else 2,
            overflow = TextOverflow.Ellipsis
        )

        // Item code (as "brand" substitute)
        Text(
            text = "קוד: ${product.itemCode}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        // Weight/Unit if available
        if (product.weight != null && product.unit != null) {
            Text(
                text = "${product.weight}${product.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Number of stores
        Text(
            text = "זמין ב-${product.prices.size} חנויות",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.extended.electricMint
        )
    }
}

/**
 * Price Section with store selection
 */
@Composable
private fun PriceSection(
    product: GroupedProduct,
    selectedStoreId: String?,
    isCompact: Boolean,
    onStoreSelected: (StorePrice) -> Unit,
    modifier: Modifier = Modifier
) {
    val lowestPrice = product.prices.minByOrNull { it.price }
    val highestPrice = product.prices.maxByOrNull { it.price }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
    ) {
        // Price range indicator
        if (lowestPrice != null && highestPrice != null && lowestPrice.price < highestPrice.price) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lowest price
                Column {
                    Text(
                        text = "₪${String.format("%.2f", lowestPrice.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.extended.electricMint,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lowestPrice.chain,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Savings badge
                val savingsPercent = ((product.savings / highestPrice.price) * 100).toInt()
                if (savingsPercent > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.extended.success.copy(alpha = 0.15f),
                        contentColor = MaterialTheme.colorScheme.extended.success
                    ) {
                        Text(
                            text = "חסכון $savingsPercent%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Single price
            lowestPrice?.let { price ->
                Text(
                    text = "₪${String.format("%.2f", price.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Add to cart button
        if (!isCompact && lowestPrice != null) {
            Button(
                onClick = { onStoreSelected(lowestPrice) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.extended.electricMint
                ),
                shape = GlassmorphicShapes.ButtonSmall
            ) {
                Icon(
                    Icons.Default.AddShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(SpacingTokens.XS))
                Text("הוסף לעגלה", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/**
 * Favorite button component
 */
@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "favoriteScale"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(32.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
    ) {
        Icon(
            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) "הסר מהמועדפים" else "הוסף למועדפים",
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.extended.neonCoral
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * Discount badge component
 */
@Composable
private fun DiscountBadge(
    savingsPercent: Int,
    modifier: Modifier = Modifier
) {
    if (savingsPercent > 0) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
            color = MaterialTheme.colorScheme.extended.success
        ) {
            Text(
                text = "-$savingsPercent%",
                modifier = Modifier.padding(horizontal = SpacingTokens.S, vertical = SpacingTokens.XS),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.onSuccess,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Helper functions
 */
private fun getProductIcon(productName: String): ImageVector {
    return when {
        productName.contains("חלב") -> Icons.Default.LocalDrink
        productName.contains("לחם") -> Icons.Default.BakeryDining
        productName.contains("בשר") || productName.contains("עוף") -> Icons.Default.Restaurant
        productName.contains("פירות") || productName.contains("ירקות") -> Icons.Default.Eco
        productName.contains("משקה") || productName.contains("מיץ") -> Icons.Default.LocalBar
        productName.contains("ביצ") -> Icons.Default.Egg
        productName.contains("גבינ") -> Icons.Default.LocalPizza
        productName.contains("יוגורט") -> Icons.Default.Icecream
        productName.contains("ניקוי") -> Icons.Default.CleaningServices
        productName.contains("נייר") -> Icons.Default.Article
        productName.contains("שמפו") || productName.contains("סבון") -> Icons.Default.Soap
        else -> Icons.Default.ShoppingBasket
    }
}

@Composable
private fun getProductCategoryColor(productName: String): Color {
    val colors = MaterialTheme.colorScheme.extended
    return when {
        productName.contains("חלב") || productName.contains("גבינ") || productName.contains("יוגורט") -> colors.dairy
        productName.contains("לחם") || productName.contains("מאפ") -> colors.bakery
        productName.contains("בשר") || productName.contains("עוף") -> colors.meat
        productName.contains("ירק") || productName.contains("פיר") -> colors.produce
        productName.contains("משקה") || productName.contains("מיץ") -> colors.frozen
        productName.contains("ניקוי") -> colors.household
        else -> colors.electricMint
    }
}