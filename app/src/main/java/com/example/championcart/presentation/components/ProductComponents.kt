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
    val averagePrice: Double = 0.0,
    val savingsFromBest: Double = 0.0
)

/**
 * Main Product Card - Electric Harmony Design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    productWithPrices: ProductWithPrices,
    onProductClick: (Product) -> Unit,
    onAddToList: (Product) -> Unit,
    onFavoriteToggle: (Product) -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    isCompact: Boolean = false
) {
    val product = productWithPrices.product

    Card(
        onClick = { onProductClick(product) },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(if (isCompact) 1f else 0.8f),
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
                .padding(SpacingTokens.M)
        ) {
            // Product Image with Favorite Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isCompact) 0.5f else 0.4f)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.nameHebrew ?: product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(GlassmorphicShapes.GlassCardSmall),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                )

                // Favorite Button
                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = { onFavoriteToggle(product) },
                    modifier = Modifier.align(Alignment.TopEnd)
                )

                // Product Badges
                ProductBadges(
                    isKosher = product.isKosher,
                    isOrganic = product.isOrganic,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            // Product Info
            ProductInfo(
                product = product,
                isCompact = isCompact,
                modifier = Modifier.weight(if (isCompact) 0.3f else 0.4f)
            )

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            // Price Section
            PriceSection(
                prices = productWithPrices.prices,
                bestPrice = productWithPrices.bestPrice,
                isCompact = isCompact,
                modifier = Modifier.weight(if (isCompact) 0.2f else 0.2f)
            )

            // Action Button
            ProductActionButton(
                onAddToList = { onAddToList(product) },
                isCompact = isCompact
            )
        }
    }
}

/**
 * Product Information Section
 */
@Composable
private fun ProductInfo(
    product: Product,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
    ) {
        // Product Name (Hebrew first)
        Text(
            text = product.nameHebrew ?: product.name,
            style = if (isCompact) {
                AppTextStyles.productNameSmall
            } else {
                AppTextStyles.productName
            }.withSmartHebrewSupport(product.nameHebrew ?: product.name),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (isCompact) 1 else 2,
            overflow = TextOverflow.Ellipsis
        )

        // Brand
        Text(
            text = product.brand,
            style = if (isCompact) {
                AppTextStyles.caption
            } else {
                AppTextStyles.storeNameSmall
            },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Unit
        Text(
            text = product.unit,
            style = AppTextStyles.caption,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Product Badges (Kosher, Organic, etc.)
 */
@Composable
private fun ProductBadges(
    isKosher: Boolean,
    isOrganic: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
    ) {
        if (isKosher) {
            Badge(
                modifier = Modifier.size(20.dp),
                containerColor = MaterialTheme.colorScheme.extended.kosher
            ) {
                Text(
                    "כ",
                    style = AppTextStyles.badge,
                    color = Color.White
                )
            }
        }

        if (isOrganic) {
            Badge(
                modifier = Modifier.size(20.dp),
                containerColor = MaterialTheme.colorScheme.extended.organic
            ) {
                Icon(
                    Icons.Default.Eco,
                    contentDescription = "Organic",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * Price Comparison Section
 */
@Composable
private fun PriceSection(
    prices: List<ProductPrice>,
    bestPrice: ProductPrice?,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
    ) {
        if (bestPrice != null) {
            // Best Price Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${bestPrice.price}${bestPrice.currency}",
                        style = if (isCompact) {
                            AppTextStyles.priceMedium
                        } else {
                            AppTextStyles.priceLarge
                        },
                        color = MaterialTheme.colorScheme.extended.bestPrice
                    )

                    Text(
                        text = bestPrice.storeNameHebrew,
                        style = AppTextStyles.caption.withSmartHebrewSupport(bestPrice.storeNameHebrew),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                if (bestPrice.isOnSale && bestPrice.originalPrice != null) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${bestPrice.originalPrice}${bestPrice.currency}",
                            style = AppTextStyles.priceSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )

                        val savings = ((bestPrice.originalPrice - bestPrice.price) / bestPrice.originalPrice * 100).toInt()
                        Text(
                            text = "-$savings%",
                            style = AppTextStyles.badge,
                            color = MaterialTheme.colorScheme.extended.bestPrice
                        )
                    }
                }
            }

            // Price Comparison Indicator
            if (prices.size > 1 && !isCompact) {
                Spacer(modifier = Modifier.height(SpacingTokens.XS))
                PriceComparisonIndicator(
                    prices = prices,
                    bestPrice = bestPrice
                )
            }
        }
    }
}

/**
 * Price Comparison Visual Indicator
 */
@Composable
private fun PriceComparisonIndicator(
    prices: List<ProductPrice>,
    bestPrice: ProductPrice
) {
    val maxPrice = prices.maxOfOrNull { it.price } ?: bestPrice.price
    val minPrice = prices.minOfOrNull { it.price } ?: bestPrice.price
    val priceRange = maxPrice - minPrice

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
    ) {
        items(prices.take(3)) { price ->
            val normalizedPrice: Float = if (priceRange > 0) {
                ((price.price - minPrice) / priceRange).toFloat()
            } else {
                0.5f
            }

            val priceLevel = when {
                price.price == minPrice -> com.example.championcart.ui.theme.PriceLevel.Best
                normalizedPrice < 0.5f -> com.example.championcart.ui.theme.PriceLevel.Mid
                else -> com.example.championcart.ui.theme.PriceLevel.High
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
            ) {
                val priceColor = ColorHelpers.getPriceColor(priceLevel)

                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(4.dp)
                        .background(
                            priceColor,
                            RoundedCornerShape(2.dp)
                        )
                )

                Text(
                    text = price.storeNameHebrew.take(3),
                    style = AppTextStyles.caption,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
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
            style = if (isCompact) {
                MaterialTheme.typography.bodySmall
            } else {
                AppTextStyles.buttonTextSmall
            }.withSmartHebrewSupport("הוסף לרשימה")
        )
    }
}

/**
 * Favorite Button with proper animation specs
 */
@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    // Float animation for scale - this works with SpringSpec<Float>
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioHighBounce,
            stiffness = SpringSpecs.StiffnessHigh
        ),
        label = "favoriteScale"
    )

    // Color animation - must use AnimationSpec<Color>
    val iconColor by animateColorAsState(
        targetValue = if (isFavorite) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
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
            )
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
 * Compact Product Card for grid views
 */
@Composable
fun CompactProductCard(
    productWithPrices: ProductWithPrices,
    onProductClick: (Product) -> Unit,
    onAddToList: (Product) -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false
) {
    ProductCard(
        productWithPrices = productWithPrices,
        onProductClick = onProductClick,
        onAddToList = onAddToList,
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
    productWithPrices: ProductWithPrices,
    onProductClick: (Product) -> Unit,
    onAddToList: (Product) -> Unit,
    onFavoriteToggle: (Product) -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false
) {
    val product = productWithPrices.product

    Card(
        onClick = { onProductClick(product) },
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
            // Product Image
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.nameHebrew ?: product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(GlassmorphicShapes.GlassCardSmall),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
            )

            // Product Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
            ) {
                Text(
                    text = product.nameHebrew ?: product.name,
                    style = AppTextStyles.productName.withSmartHebrewSupport(
                        product.nameHebrew ?: product.name
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.brand,
                    style = AppTextStyles.storeNameSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                productWithPrices.bestPrice?.let { price ->
                    Text(
                        text = "${price.price}${price.currency}",
                        style = AppTextStyles.priceMedium,
                        color = MaterialTheme.colorScheme.extended.bestPrice
                    )
                }
            }

            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.S),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = { onFavoriteToggle(product) }
                )

                Button(
                    onClick = { onAddToList(product) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.extended.electricMint
                    ),
                    shape = GlassmorphicShapes.ButtonSmall,
                    contentPadding = PaddingValues(SpacingTokens.S)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add to list",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Product-specific Price Level Helper (for internal component use)
 */
enum class ProductPriceLevel { Best, Mid, High }

/**
 * Preview for Product Card
 */
@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    ChampionCartTheme {
        val sampleProduct = Product(
            id = "1",
            name = "Milk 3% Fat",
            nameHebrew = "חלב 3% שומן",
            brand = "Tnuva",
            imageUrl = "",
            category = "Dairy",
            unit = "1L",
            isKosher = true,
            isOrganic = false
        )

        val samplePrices = listOf(
            ProductPrice(
                storeId = "1",
                storeName = "Shufersal",
                storeNameHebrew = "שופרסל",
                price = 5.90,
                originalPrice = 6.90,
                isOnSale = true
            ),
            ProductPrice(
                storeId = "2",
                storeName = "Rami Levy",
                storeNameHebrew = "רמי לוי",
                price = 6.20
            )
        )

        val productWithPrices = ProductWithPrices(
            product = sampleProduct,
            prices = samplePrices,
            bestPrice = samplePrices[0]
        )

        ProductCard(
            productWithPrices = productWithPrices,
            onProductClick = {},
            onAddToList = {},
            onFavoriteToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}