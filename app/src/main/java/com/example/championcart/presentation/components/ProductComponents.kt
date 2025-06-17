package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Product Components
 * Electric Harmony glassmorphic product cards with price comparison
 */

// Data classes for products and prices
data class Product(
    val id: String,
    val name: String,
    val nameHebrew: String? = null,
    val brand: String,
    val imageUrl: String,
    val category: String,
    val unit: String = "יח'", // Hebrew unit
    val isKosher: Boolean = false,
    val isOrganic: Boolean = false
)

data class ProductPrice(
    val storeId: String,
    val storeName: String,
    val storeNameHebrew: String,
    val price: Double,
    val originalPrice: Double? = null,
    val isOnSale: Boolean = false,
    val currency: String = "₪"
)

data class ProductWithPrices(
    val product: Product,
    val prices: List<ProductPrice>,
    val bestPrice: ProductPrice? = null,
    val averagePrice: Double = 0.0
)

/**
 * Main Product Card Component
 */
@Composable
fun ProductCard(
    productWithPrices: ProductWithPrices,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    onAddToList: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    isFavorite: Boolean = false,
    showPriceComparison: Boolean = true,
    isCompact: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = SpringSpecs.Bouncy,
        label = "cardScale"
    )

    Card(
        modifier = modifier
            .width(if (isCompact) 160.dp else ProductTokens.CardWidth)
            .scale(scale)
            .bouncyClickable(
                onClick = onCardClick,
                onPressChange = { isPressed = it }
            )
            .glassCard(GlassIntensity.Medium),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.padding(ProductTokens.CardPadding)
        ) {
            // Product Image with badges
            ProductImageSection(
                product = productWithPrices.product,
                isFavorite = isFavorite,
                onFavoriteClick = onFavoriteClick,
                isCompact = isCompact
            )

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            // Product Info
            ProductInfoSection(
                product = productWithPrices.product,
                isCompact = isCompact
            )

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            // Price Section
            if (showPriceComparison && productWithPrices.prices.isNotEmpty()) {
                PriceSection(
                    prices = productWithPrices.prices,
                    bestPrice = productWithPrices.bestPrice,
                    isCompact = isCompact
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.M))

            // Action Button
            ProductActionButton(
                onAddToList = onAddToList,
                isCompact = isCompact
            )
        }
    }
}

/**
 * Product Image Section with badges
 */
@Composable
private fun ProductImageSection(
    product: Product,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    isCompact: Boolean
) {
    val imageHeight = if (isCompact) 80.dp else 120.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageHeight)
    ) {
        // Product Image
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxSize()
                .clip(GlassmorphicShapes.ProductCard),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
            error = painterResource(id = android.R.drawable.ic_menu_gallery)
        )

        // Favorite Button
        FavoriteButton(
            isFavorite = isFavorite,
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SpacingTokens.XS)
        )

        // Product Badges
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(SpacingTokens.XS),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
        ) {
            if (product.isKosher) {
                ProductBadge(
                    text = "כשר",
                    backgroundColor = MaterialTheme.colorScheme.extended.kosher
                )
            }
            if (product.isOrganic) {
                ProductBadge(
                    text = "אורגני",
                    backgroundColor = MaterialTheme.colorScheme.extended.organic
                )
            }
        }
    }
}

/**
 * Product Information Section
 */
@Composable
private fun ProductInfoSection(
    product: Product,
    isCompact: Boolean
) {
    Column {
        // Brand
        Text(
            text = product.brand,
            style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(SpacingTokens.XXS))

        // Product Name
        Text(
            text = product.nameHebrew ?: product.name,
            style = if (isCompact) AppTextStyles.productNameSmall else AppTextStyles.productName,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = if (isCompact) MaterialTheme.typography.bodySmall.lineHeight else MaterialTheme.typography.bodyMedium.lineHeight
        )

        Spacer(modifier = Modifier.height(SpacingTokens.XXS))

        // Unit
        Text(
            text = product.unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Price Comparison Section
 */
@Composable
private fun PriceSection(
    prices: List<ProductPrice>,
    bestPrice: ProductPrice?,
    isCompact: Boolean
) {
    val sortedPrices = prices.sortedBy { it.price }
    val displayPrices = if (isCompact) sortedPrices.take(2) else sortedPrices.take(3)

    Column {
        displayPrices.forEach { price ->
            PriceRow(
                price = price,
                isBestPrice = price == bestPrice,
                isCompact = isCompact
            )
            if (price != displayPrices.last()) {
                Spacer(modifier = Modifier.height(SpacingTokens.XS))
            }
        }

        if (prices.size > displayPrices.size) {
            Text(
                text = "+${prices.size - displayPrices.size} חנויות נוספות",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = SpacingTokens.XS)
            )
        }
    }
}

/**
 * Individual Price Row
 */
@Composable
private fun PriceRow(
    price: ProductPrice,
    isBestPrice: Boolean,
    isCompact: Boolean
) {
    val priceLevel = when {
        isBestPrice -> PriceLevel.Best
        else -> PriceLevel.Mid
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (isBestPrice) {
            MaterialTheme.colorScheme.extended.bestPriceContainer.copy(alpha = 0.2f)
        } else {
            Color.Transparent
        },
        animationSpec = SpringSpecs.Smooth,
        label = "priceBackground"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = SpacingTokens.XS, vertical = SpacingTokens.XXS),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Store Name
        Text(
            text = price.storeNameHebrew,
            style = if (isCompact) MaterialTheme.typography.bodySmall else AppTextStyles.storeNameSmall,
            color = ColorHelpers.getStoreColor(price.storeName),
            fontWeight = if (isBestPrice) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(SpacingTokens.XS))

        // Price
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (price.isOnSale && price.originalPrice != null) {
                Text(
                    text = "${price.originalPrice}${price.currency}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )
                Spacer(modifier = Modifier.width(SpacingTokens.XXS))
            }

            Text(
                text = "${price.price}${price.currency}",
                style = if (isCompact) AppTextStyles.priceSmall else AppTextStyles.priceMedium,
                color = ColorHelpers.getPriceColor(priceLevel),
                fontWeight = if (isBestPrice) FontWeight.Bold else FontWeight.Medium
            )

            if (isBestPrice) {
                Spacer(modifier = Modifier.width(SpacingTokens.XXS))
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Best Price",
                    tint = MaterialTheme.colorScheme.extended.bestPrice,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * Product Action Button
 */
@Composable
private fun ProductActionButton(
    onAddToList: () -> Unit,
    isCompact: Boolean
) {
    Button(
        onClick = onAddToList,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.extended.electricMint
        ),
        shape = GlassmorphicShapes.Button,
        contentPadding = PaddingValues(
            horizontal = SpacingTokens.M,
            vertical = if (isCompact) SpacingTokens.XS else SpacingTokens.S
        )
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(SizingTokens.IconS)
        )
        Spacer(modifier = Modifier.width(SpacingTokens.XS))
        Text(
            "הוסף לרשימה",
            style = if (isCompact) MaterialTheme.typography.bodySmall else AppTextStyles.buttonTextSmall
        )
    }
}

/**
 * Favorite Button with animation
 */
@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = SpringSpecs.Playful,
        label = "favoriteScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isFavorite) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        animationSpec = SpringSpecs.Smooth,
        label = "favoriteColor"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(32.dp)
            .scale(scale)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                CircleShape
            ),
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    ) {
        Icon(
            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * Product Badge
 */
@Composable
private fun ProductBadge(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = backgroundColor.copy(alpha = 0.9f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = SpacingTokens.XS, vertical = 2.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Product Grid Component
 */
@Composable
fun ProductGrid(
    products: List<ProductWithPrices>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    onProductClick: (ProductWithPrices) -> Unit = {},
    onAddToList: (ProductWithPrices) -> Unit = {},
    onFavoriteClick: (ProductWithPrices) -> Unit = {},
    favoriteProductIds: Set<String> = emptySet(),
    isCompact: Boolean = false
) {
    val chunkedProducts = products.chunked(columns)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        chunkedProducts.forEach { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
            ) {
                rowProducts.forEach { product ->
                    ProductCard(
                        productWithPrices = product,
                        modifier = Modifier.weight(1f),
                        onCardClick = { onProductClick(product) },
                        onAddToList = { onAddToList(product) },
                        onFavoriteClick = { onFavoriteClick(product) },
                        isFavorite = favoriteProductIds.contains(product.product.id),
                        isCompact = isCompact
                    )
                }

                // Add empty spaces for incomplete rows
                repeat(columns - rowProducts.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Extension function for bouncy clickable
 */
@Composable
private fun Modifier.bouncyClickable(
    onClick: () -> Unit,
    onPressChange: (Boolean) -> Unit = {}
): Modifier = this.clickable(
    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
    indication = null
) {
    onClick()
}

// Preview Data
private val sampleProduct = Product(
    id = "1",
    name = "Milk 3% 1L",
    nameHebrew = "חלב 3% ליטר",
    brand = "תנובה",
    imageUrl = "",
    category = "dairy",
    unit = "ליטר",
    isKosher = true,
    isOrganic = false
)

private val samplePrices = listOf(
    ProductPrice("1", "Shufersal", "שופרסל", 6.90, currency = "₪"),
    ProductPrice("2", "Rami Levy", "רמי לוי", 7.20, currency = "₪"),
    ProductPrice("3", "Victory", "ויקטורי", 7.50, currency = "₪")
)

private val sampleProductWithPrices = ProductWithPrices(
    product = sampleProduct,
    prices = samplePrices,
    bestPrice = samplePrices.first(),
    averagePrice = 7.2
)

@Preview(name = "Product Card - Light")
@Composable
private fun ProductCardPreview() {
    ChampionCartTheme {
        Surface {
            ProductCard(
                productWithPrices = sampleProductWithPrices,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Product Card - Compact")
@Composable
private fun ProductCardCompactPreview() {
    ChampionCartTheme {
        Surface {
            ProductCard(
                productWithPrices = sampleProductWithPrices,
                modifier = Modifier.padding(16.dp),
                isCompact = true
            )
        }
    }
}

@Preview(name = "Product Grid")
@Composable
private fun ProductGridPreview() {
    ChampionCartTheme {
        Surface {
            ProductGrid(
                products = listOf(sampleProductWithPrices, sampleProductWithPrices, sampleProductWithPrices),
                modifier = Modifier.padding(16.dp),
                columns = 2
            )
        }
    }
}