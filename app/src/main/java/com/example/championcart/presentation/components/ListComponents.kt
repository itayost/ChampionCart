package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

/**
 * List & Grid Components
 * Theme-aware animated lists with Electric Harmony styling
 */

/**
 * Animate DP with accessibility consideration
 */
@Composable
fun animateDpWithAccessibility(
    targetValue: Dp,
    animationSpec: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    ),
    label: String = "DpAnimation",
    finishedListener: ((Dp) -> Unit)? = null
): State<Dp> {
    val reduceMotion = LocalReduceMotion.current
    return animateDpAsState(
        targetValue = targetValue,
        animationSpec = if (reduceMotion) snap() else animationSpec,
        label = label,
        finishedListener = finishedListener
    )
}

/**
 * Product List Item - Theme-aware
 */
@Composable
fun ProductListItem(
    product: ProductItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    isVisible: Boolean = true
) {
    val darkTheme = isSystemInDarkTheme()
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = isVisible,
        enter = ChampionCartAnimations.List.staggeredSlideIn(index) +
                ChampionCartAnimations.List.staggeredFadeIn(index),
        exit = fadeOut() + slideOutHorizontally(),
        modifier = modifier
    ) {
        GlassCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            intensity = GlassIntensity.Light,
            elevated = !darkTheme // Better visibility in light theme
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Component.paddingM),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Image with theme-aware background
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(ComponentShapes.Product.Image)
                        .background(
                            if (darkTheme) {
                                Color.White.copy(alpha = 0.05f)
                            } else {
                                Color.Black.copy(alpha = 0.03f)
                            }
                        )
                ) {
                    // Placeholder for image
                    if (!product.imageUrl.isNullOrEmpty()) {
                        // Load image
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
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
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Price comparison with theme-aware styling
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PriceTag(
                            price = product.bestPrice,
                            priceLevel = PriceLevel.Best,
                            animate = false
                        )

                        Text(
                            text = "ב-${product.bestStore}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Favorite button with theme-aware colors
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
                        contentDescription = if (product.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (product.isFavorite) {
                            ChampionCartColors.Brand.NeonCoral
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Product Grid - Theme-aware
 */
@Composable
fun ProductGrid(
    products: List<ProductItemData>,
    onProductClick: (ProductItemData) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Spacing.m),
    isLoading: Boolean = false
) {
    val darkTheme = isSystemInDarkTheme()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        if (isLoading) {
            items(6) { index ->
                ProductCardSkeleton(darkTheme = darkTheme)
            }
        } else {
            itemsIndexed(
                items = products,
                key = { _, product -> product.id }
            ) { index, product ->
                var isVisible by remember { mutableStateOf(false) }

                LaunchedEffect(key1 = index) {
                    delay(index * 50L)
                    isVisible = true
                }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + scaleIn(initialScale = 0.9f)
                ) {
                    ProductGlassCard(
                        productName = product.name,
                        productImage = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (darkTheme) {
                                            Color.White.copy(alpha = 0.05f)
                                        } else {
                                            Color.Gray.copy(alpha = 0.05f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = Color.Gray.copy(alpha = 0.3f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        },
                        price = product.bestPrice,
                        storeName = product.bestStore,
                        priceLevel = PriceLevel.Best,
                        onClick = { onProductClick(product) },
                        isFavorite = product.isFavorite
                    )
                }
            }
        }
    }
}

/**
 * Store List - Theme-aware
 */
@Composable
fun StoreList(
    stores: List<StoreItemData>,
    onStoreClick: (StoreItemData) -> Unit,
    modifier: Modifier = Modifier,
    selectedStoreId: String? = null
) {
    val darkTheme = isSystemInDarkTheme()

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
                modifier = Modifier.padding(horizontal = Spacing.m),
                darkTheme = darkTheme
            )
        }
    }
}

/**
 * Store List Item - Theme-aware
 */
@Composable
fun StoreListItem(
    store: StoreItemData,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    darkTheme: Boolean
) {
    val animatedScale by animateFloatWithAccessibility(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = ChampionCartAnimations.Springs.smooth(),
        label = "storeScale"
    )

    AnimatedVisibility(
        visible = true,
        enter = ChampionCartAnimations.List.staggeredSlideIn(index, baseDelay = 50) +
                ChampionCartAnimations.List.staggeredFadeIn(index, baseDelay = 50),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
        ) {
            StoreGlassCard(
                storeName = store.name,
                storeIcon = {
                    Text(
                        text = store.name.take(2).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                itemCount = store.itemCount,
                totalPrice = store.totalPrice,
                onClick = onClick,
                modifier = Modifier
                    .then(
                        if (isSelected) {
                            if (darkTheme) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = ChampionCartColors.Brand.ElectricMint,
                                    shape = ComponentShapes.Store.Card
                                )
                            } else {
                                Modifier.border(
                                    width = 2.dp,
                                    color = ChampionCartColors.Brand.ElectricMint,
                                    shape = ComponentShapes.Store.Card
                                ).shadow(
                                    elevation = 4.dp,
                                    shape = ComponentShapes.Store.Card,
                                    ambientColor = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f)
                                )
                            }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

/**
 * Cart Item List - Theme-aware
 */
@Composable
fun CartItemList(
    items: List<CartItemData>,
    onQuantityChange: (CartItemData, Int) -> Unit,
    onRemoveItem: (CartItemData) -> Unit,
    modifier: Modifier = Modifier
) {
    val darkTheme = isSystemInDarkTheme()

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
                modifier = Modifier.padding(horizontal = Spacing.m),
                darkTheme = darkTheme
            )
        }

        // Total section
        item {
            Spacer(modifier = Modifier.height(Spacing.m))
            TotalPriceDisplay(
                totalPrice = "₪%.2f".format(
                    items.sumOf { item ->
                        (item.price.replace("₪", "").toDoubleOrNull() ?: 0.0) * item.quantity
                    }
                ),
                itemCount = items.sumOf { it.quantity },
                modifier = Modifier.padding(horizontal = Spacing.m)
            )
        }
    }
}

/**
 * Cart List Item - Theme-aware
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartListItem(
    item: CartItemData,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    darkTheme: Boolean
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
                    Text("הסר", color = ChampionCartColors.Semantic.Error)
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
        modifier = modifier,
        darkTheme = darkTheme
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            intensity = GlassIntensity.Light,
            elevated = !darkTheme
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
                        .background(
                            if (darkTheme) {
                                Color.White.copy(alpha = 0.05f)
                            } else {
                                Color.Black.copy(alpha = 0.03f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
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
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = item.price,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChampionCartColors.Brand.ElectricMint,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Quantity selector
                QuantityInput(
                    quantity = item.quantity,
                    onQuantityChange = onQuantityChange,
                    darkTheme = darkTheme
                )
            }
        }
    }
}

/**
 * Swipeable Cart Item - Theme-aware
 */
@Composable
fun SwipeableCartItem(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        // Delete background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    ChampionCartColors.Semantic.Error.copy(
                        alpha = if (darkTheme) 0.9f else 0.8f
                    ),
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
            modifier = Modifier.fillMaxWidth()
            // Add swipe gesture handling here if needed
        ) {
            content()
        }
    }
}

/**
 * Product Card Skeleton - Theme-aware
 */
@Composable
fun ProductCardSkeleton(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    Box(
        modifier = modifier
            .width(160.dp)
            .height(240.dp)
            .cardGlass(
                intensity = GlassIntensity.Light,
                shape = ComponentShapes.Product.Card,
                darkTheme = darkTheme
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Image skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(ComponentShapes.Product.Image)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

/**
 * Total Price Display - Theme-aware
 */
@Composable
fun TotalPriceDisplay(
    totalPrice: String,
    itemCount: Int,
    modifier: Modifier = Modifier
) {
    val darkTheme = isSystemInDarkTheme()

    GlassCard(
        modifier = modifier,
        intensity = GlassIntensity.Medium,
        elevated = !darkTheme
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.l),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "סה״כ לתשלום",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$itemCount פריטים",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = totalPrice,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ChampionCartColors.Brand.ElectricMint
            )
        }
    }
}

/**
 * Quantity Input - Theme-aware
 */
@Composable
fun QuantityInput(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            modifier = Modifier
                .size(32.dp)
                .glass(
                    intensity = GlassIntensity.Light,
                    style = if (darkTheme) GlassStyle.Subtle else GlassStyle.Default,
                    darkTheme = darkTheme
                ),
            enabled = quantity > 1
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Decrease quantity",
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.widthIn(min = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier
                .size(32.dp)
                .glass(
                    intensity = GlassIntensity.Light,
                    style = if (darkTheme) GlassStyle.Subtle else GlassStyle.Default,
                    darkTheme = darkTheme
                )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Increase quantity",
                modifier = Modifier.size(16.dp)
            )
        }
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
    val isFavorite: Boolean = false,
    val imageUrl: String? = null
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