package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Modern List & Grid Components
 * Electric Harmony Design System
 */

/**
 * Modern Product List Item
 */
@Composable
fun ModernProductListItem(
    product: ProductItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    isVisible: Boolean = true
) {
    val config = ChampionCartTheme.config

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = index * 50
            )
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = index * 50
            )
        ),
        exit = fadeOut() + slideOutHorizontally(),
        modifier = modifier
    ) {
        ModernGlassCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingTokens.L),
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Image
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(SizingTokens.IconM)
                    )
                }

                // Product Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Price with store
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.bestPrice,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ChampionCartColors.Semantic.Success
                        )
                        Text(
                            text = "ב-${product.bestStore}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Favorite button
                var isFavorite by remember { mutableStateOf(product.isFavorite) }

                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = if (isFavorite) "הסר מהמועדפים" else "הוסף למועדפים",
                        tint = if (isFavorite) {
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
 * Modern Product Grid
 */
@Composable
fun ModernProductGrid(
    products: List<ProductItemData>,
    onProductClick: (ProductItemData) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(SpacingTokens.M),
    isLoading: Boolean = false
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        if (isLoading) {
            items(6) { index ->
                ProductCardSkeleton()
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
                    val mockPrices = listOf(
                        StorePrice(product.bestStore, product.bestPrice.replace("₪", "").toFloatOrNull() ?: 0f),
                        StorePrice("חנות אחרת", (product.bestPrice.replace("₪", "").toFloatOrNull() ?: 0f) * 1.1f)
                    )

                    ProductPriceCard(
                        productName = product.name,
                        prices = mockPrices,
                        onAddToCart = { onProductClick(product) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Modern Store List
 */
@Composable
fun ModernStoreList(
    stores: List<StoreItemData>,
    onStoreClick: (StoreItemData) -> Unit,
    modifier: Modifier = Modifier,
    selectedStoreId: String? = null
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = SpacingTokens.L,
            vertical = SpacingTokens.M
        ),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        itemsIndexed(
            items = stores,
            key = { _, store -> store.id }
        ) { index, store ->
            var isVisible by remember { mutableStateOf(false) }

            LaunchedEffect(index) {
                delay(index * 50L)
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInHorizontally { -it / 4 }
            ) {
                StoreComparisonCard(
                    storeName = store.name,
                    totalPrice = store.totalPrice,
                    itemCount = store.itemCount,
                    savings = if (index == 0) 15f else null, // Mock savings for demo
                    onClick = { onStoreClick(store) },
                    isSelected = store.id == selectedStoreId,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Modern Cart Item List
 */
@Composable
fun ModernCartItemList(
    items: List<CartItemData>,
    onQuantityChange: (CartItemData, Int) -> Unit,
    onRemoveItem: (CartItemData) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = SpacingTokens.L,
            vertical = SpacingTokens.M
        ),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            var isVisible by remember { mutableStateOf(false) }

            LaunchedEffect(index) {
                delay(index * 50L)
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { it / 4 }
            ) {
                ModernCartListItem(
                    item = item,
                    onQuantityChange = { newQuantity ->
                        onQuantityChange(item, newQuantity)
                    },
                    onRemove = { onRemoveItem(item) }
                )
            }
        }

        // Total section
        item {
            Spacer(modifier = Modifier.height(SpacingTokens.M))
            ModernTotalPriceDisplay(
                totalPrice = "₪%.2f".format(
                    items.sumOf { item ->
                        (item.price.replace("₪", "").toDoubleOrNull() ?: 0.0) * item.quantity
                    }
                ),
                itemCount = items.sumOf { it.quantity }
            )
        }
    }
}

/**
 * Modern Cart List Item
 */
@Composable
fun ModernCartListItem(
    item: CartItemData,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
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

    ModernGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(SizingTokens.IconS)
                )
            }

            // Product info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = item.price,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ChampionCartColors.Brand.ElectricMint,
                    fontWeight = FontWeight.Medium
                )
            }

            // Quantity selector
            ModernQuantityInput(
                quantity = item.quantity,
                onQuantityChange = onQuantityChange
            )

            // Delete button
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "הסר מוצר",
                    tint = ChampionCartColors.Semantic.Error.copy(alpha = 0.7f),
                    modifier = Modifier.size(SizingTokens.IconS)
                )
            }
        }
    }
}

/**
 * Modern Quantity Input
 */
@Composable
fun ModernQuantityInput(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            enabled = quantity > 1
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "הפחת כמות",
                    modifier = Modifier.size(SizingTokens.IconXS),
                    tint = if (quantity > 1) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    }
                )
            }
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.widthIn(min = 32.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Surface(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "הוסף כמות",
                    modifier = Modifier.size(SizingTokens.IconXS),
                    tint = ChampionCartColors.Brand.ElectricMint
                )
            }
        }
    }
}

/**
 * Modern Total Price Display
 */
@Composable
fun ModernTotalPriceDisplay(
    totalPrice: String,
    itemCount: Int,
    modifier: Modifier = Modifier
) {
    ModernGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f),
                            ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(SpacingTokens.XL)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "סה״כ לתשלום",
                        style = MaterialTheme.typography.titleMedium
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
}

/**
 * Product Card Skeleton
 */
@Composable
fun ProductCardSkeleton(
    modifier: Modifier = Modifier
) {
    ModernGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.L)
        ) {
            // Image skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
            )

            Spacer(modifier = Modifier.height(SpacingTokens.M))

            // Text skeletons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
            )

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
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