package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * List & Grid Components
 * Animated lists with Electric Harmony styling
 */

/**
 * Product List Item
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductListItem(
    product: ProductItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    isVisible: Boolean = true
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = isVisible,
        enter = ChampionCartAnimations.List.staggeredSlideIn(index) +
                ChampionCartAnimations.List.staggeredFadeIn(index),
        exit = fadeOut() + slideOutHorizontally(),
        modifier = modifier
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .animateItemPlacement()
                .glass(
                    intensity = GlassIntensity.Light,
                    shape = ComponentShapes.Card.Small
                ),
            shape = ComponentShapes.Card.Small,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Elevation.Component.card
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Component.paddingM),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Image
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(ComponentShapes.Product.Image)
                        .shimmerGlass(duration = 1000)
                ) {
                    // Placeholder for image
                }

                // Product Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = product.category,
                        style = CustomTextStyles.category,
                        color = ChampionCartTheme.colors.onSurfaceVariant
                    )

                    // Price comparison
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        PriceTag(
                            price = product.bestPrice,
                            priceLevel = PriceLevel.Best,
                            animate = false
                        )

                        Text(
                            text = "ב-${product.bestStore}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ChampionCartTheme.colors.onSurfaceVariant
                        )
                    }
                }

                // Favorite button
                IconButton(
                    onClick = { /* Toggle favorite */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = null,
                        tint = if (product.isFavorite) {
                            ChampionCartColors.Brand.NeonCoral
                        } else {
                            ChampionCartTheme.colors.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

/**
 * Product Grid
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductGrid(
    products: List<ProductItemData>,
    onProductClick: (ProductItemData) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Spacing.m),
    isLoading: Boolean = false
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = Sizing.Card.productWidth),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        if (isLoading) {
            items(6) { index ->
                ProductCardSkeleton(
                    modifier = Modifier.animateItemPlacement()
                )
            }
        } else {
            itemsIndexed(
                items = products,
                key = { _, product -> product.id }
            ) { index, product ->
                ProductGlassCard(
                    productName = product.name,
                    productImage = { /* Product image */ },
                    price = product.bestPrice,
                    storeName = product.bestStore,
                    priceLevel = PriceLevel.Best,
                    onClick = { onProductClick(product) },
                    isFavorite = product.isFavorite,
                    modifier = Modifier
                        .animateItemPlacement()
                        .graphicsLayer {
                            // Stagger animation
                            alpha = 1f
                        }
                )
            }
        }
    }
}

/**
 * Store List
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreList(
    stores: List<StoreItemData>,
    onStoreClick: (StoreItemData) -> Unit,
    modifier: Modifier = Modifier,
    selectedStoreId: String? = null
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = Spacing.m),
        verticalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        itemsIndexed(
            items = stores,
            key = { _, store -> store.id }
        ) { index, store ->
            StoreListItem(
                store = store,
                isSelected = store.id == selectedStoreId,
                onClick = { onStoreClick(store) },
                index = index,
                modifier = Modifier
                    .padding(horizontal = Spacing.m)
                    .animateItemPlacement()
            )
        }
    }
}

/**
 * Store List Item
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreListItem(
    store: StoreItemData,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val animatedElevation by animateDpWithAccessibility(
        targetValue = if (isSelected) {
            Elevation.Component.cardHover
        } else {
            Elevation.Component.card
        },
        animationSpec = ChampionCartAnimations.Springs.smooth(),
        label = "storeElevation"
    )

    AnimatedVisibility(
        visible = true,
        enter = ChampionCartAnimations.List.staggeredSlideIn(index, baseDelay = 50) +
                ChampionCartAnimations.List.staggeredFadeIn(index, baseDelay = 50),
        modifier = modifier
    ) {
        StoreGlassCard(
            storeName = store.name,
            storeIcon = { /* Store logo */ },
            itemCount = store.itemCount,
            totalPrice = store.totalPrice,
            onClick = onClick,
            modifier = Modifier
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = ChampionCartColors.Brand.ElectricMint,
                            shape = ComponentShapes.Store.Card
                        )
                    } else {
                        Modifier
                    }
                )
        )
    }
}

/**
 * Cart Item List
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartItemList(
    items: List<CartItemData>,
    onQuantityChange: (CartItemData, Int) -> Unit,
    onRemoveItem: (CartItemData) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = Spacing.m),
        verticalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            CartListItem(
                item = item,
                onQuantityChange = { newQuantity ->
                    onQuantityChange(item, newQuantity)
                },
                onRemove = { onRemoveItem(item) },
                index = index,
                modifier = Modifier
                    .padding(horizontal = Spacing.m)
                    .animateItemPlacement()
            )
        }

        // Total section
        item {
            Spacer(modifier = Modifier.height(Spacing.m))
            TotalPriceDisplay(
                totalPrice = items.sumOf {
                    it.price.toDoubleOrNull() ?: 0.0 * it.quantity
                }.toString(),
                itemCount = items.sumOf { it.quantity },
                modifier = Modifier.padding(horizontal = Spacing.m)
            )
        }
    }
}

/**
 * Cart List Item - Using Material 3 swipe to reveal pattern
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartListItem(
    item: CartItemData,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("הסרת מוצר")
            },
            text = {
                Text("האם אתה בטוח שברצונך להסיר את ${item.name} מהעגלה?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemove()
                        showDeleteDialog = false
                    }
                ) {
                    Text("הסר")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("ביטול")
                }
            }
        )
    }

    SwipeableCartItem(
        onDelete = { showDeleteDialog = true },
        modifier = modifier
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = ComponentShapes.Card.Small
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Component.paddingM),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product image
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(ComponentShapes.Product.Image)
                        .shimmerGlass()
                ) {
                    // Product image placeholder
                }

                // Product info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "₪${item.price}",
                        style = CustomTextStyles.priceSmall,
                        color = ChampionCartTheme.colors.primary
                    )
                }

                // Quantity selector
                QuantityInput(
                    quantity = item.quantity,
                    onQuantityChange = onQuantityChange
                )
            }
        }
    }
}

/**
 * Swipeable Cart Item using gesture detection
 */
@Composable
fun SwipeableCartItem(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        // Delete background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    ChampionCartColors.Semantic.Error,
                    shape = ComponentShapes.Card.Small
                )
                .padding(horizontal = Spacing.l),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove",
                tint = Color.White
            )
        }

        // Swipeable content
        Box(
            modifier = Modifier
                .fillMaxWidth()
            // Add swipe gesture handling here if needed
        ) {
            content()
        }
    }
}

/**
 * Product Card Skeleton
 */
@Composable
fun ProductCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(Sizing.Card.productWidth)
            .height(Sizing.Card.productHeight)
            .shimmerGlass(
                shape = ComponentShapes.Product.Card
            ),
        shape = ComponentShapes.Product.Card,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        // Skeleton content
    }
}

/**
 * Data classes for list items
 */
data class ProductItemData(
    val id: String,
    val name: String,
    val category: String,
    val bestPrice: String,
    val bestStore: String,
    val isFavorite: Boolean = false
)

data class StoreItemData(
    val id: String,
    val name: String,
    val itemCount: Int,
    val totalPrice: String
)

data class CartItemData(
    val id: String,
    val name: String,
    val price: String,
    val quantity: Int
)