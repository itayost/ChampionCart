package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Cart Components
 * Specialized components for shopping cart functionality
 */

/**
 * Cart item card component
 */
@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Using Card instead of SwipeableListItem to avoid parameter issues
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.S, vertical = SpacingTokens.XS),
        onClick = onProductClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.M),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl != null) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = "תמונת ${item.productName}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Product info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Show store chain if available
                item.selectedStore?.let { store ->
                    Text(
                        text = "${store.chain}${store.city?.let { " - $it" } ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Price and quantity controls
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
            ) {
                // Total price
                Text(
                    text = "₪${String.format("%.2f", item.price * item.quantity)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Unit price
                Text(
                    text = "₪${String.format("%.2f", item.price)} × ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quantity selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delete button
                    IconButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            showDeleteDialog = true
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "הסר מוצר",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Quantity controls
                    QuantitySelector(
                        quantity = item.quantity,
                        onQuantityChange = onQuantityChange,
                        minQuantity = 1,
                        maxQuantity = 99
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        ChampionCartAlertDialog(
            title = "הסר מהעגלה",
            text = "האם אתה בטוח שברצונך להסיר את ${item.productName} מהעגלה?",
            confirmButtonText = "הסר",
            dismissButtonText = "ביטול",
            onConfirm = {
                onRemove()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
            confirmButtonColor = MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Compact cart item for summary views
 */
@Composable
fun CompactCartItem(
    item: CartItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpacingTokens.XS),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            Text(
                text = "${item.quantity}×",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.productName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = "₪${String.format("%.2f", item.price * item.quantity)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Quantity selector component
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 0,
    maxQuantity: Int = 99
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease button
        IconButton(
            onClick = {
                if (quantity > minQuantity) {
                    onQuantityChange(quantity - 1)
                }
            },
            modifier = Modifier.size(32.dp),
            enabled = quantity > minQuantity
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "הפחת כמות",
                modifier = Modifier.size(18.dp)
            )
        }

        // Quantity display
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.widthIn(min = 32.dp),
            textAlign = TextAlign.Center
        )

        // Increase button
        IconButton(
            onClick = {
                if (quantity < maxQuantity) {
                    onQuantityChange(quantity + 1)
                }
            },
            modifier = Modifier.size(32.dp),
            enabled = quantity < maxQuantity
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "הוסף כמות",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Cart summary card
 */
@Composable
fun CartSummaryCard(
    itemCount: Int,
    subtotal: Double,
    savings: Double,
    total: Double,
    storeName: String,
    modifier: Modifier = Modifier,
    onCheckout: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L)
        ) {
            // Store info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$itemCount מוצרים",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.M))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(SpacingTokens.M))

            // Price breakdown
            PriceRow(label = "סכום ביניים", amount = subtotal)

            if (savings > 0) {
                PriceRow(
                    label = "חיסכון",
                    amount = savings,
                    isHighlight = true
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.S))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(SpacingTokens.S))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "סה״כ לתשלום",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₪${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.L))

            // Checkout button
            PrimaryButton(
                text = "המשך לתשלום",
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                icon = {
                    Icon(
                        Icons.Default.ShoppingCartCheckout,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

/**
 * Price row component
 */
@Composable
private fun PriceRow(
    label: String,
    amount: Double,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpacingTokens.XS),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isHighlight) {
                MaterialTheme.colorScheme.extended.electricMint
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Text(
            text = if (isHighlight) {
                "-₪${String.format("%.2f", amount)}"
            } else {
                "₪${String.format("%.2f", amount)}"
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Medium else FontWeight.Normal,
            color = if (isHighlight) {
                MaterialTheme.colorScheme.extended.electricMint
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

/**
 * Empty cart state
 */
@Composable
fun EmptyCartState(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        type = EmptyStateType.EMPTY_CART,
        title = "העגלה שלך ריקה",
        subtitle = "הוסף מוצרים כדי להתחיל לחסוך",
        actionLabel = "התחל לקנות",
        onAction = onStartShopping,
        modifier = modifier
    )
}

/**
 * Store selection card for cart
 */
@Composable
fun StoreSelectionCard(
    stores: List<StoreOption>,
    selectedStoreId: String?,
    onStoreSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        SectionHeader(
            title = "בחר חנות",
            subtitle = "השווה מחירים בין החנויות"
        )

        Spacer(modifier = Modifier.height(SpacingTokens.M))

        stores.forEach { store ->
            StoreOptionItem(
                store = store,
                isSelected = store.storeId == selectedStoreId,
                onClick = { onStoreSelected(store.storeId) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(SpacingTokens.S))
        }
    }
}

/**
 * Cart top bar component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar(
    itemCount: Int,
    onBackClick: () -> Unit,
    onClearCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = "העגלה שלי ($itemCount)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "חזור"
                )
            }
        },
        actions = {
            if (itemCount > 0) {
                IconButton(onClick = onClearCartClick) {
                    Icon(
                        Icons.Default.DeleteSweep,
                        contentDescription = "נקה עגלה",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        modifier = modifier
    )
}

/**
 * Store selector component for cart
 */
@Composable
fun StoreSelector(
    stores: List<StoreOption>,
    selectedStoreId: String?,
    onStoreSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        stores.forEach { store ->
            StoreOptionCard(
                store = store,
                isSelected = store.storeId == selectedStoreId,
                onClick = { onStoreSelected(store.storeId) }
            )
        }
    }
}

/**
 * Store option card
 */
@Composable
private fun StoreOptionCard(
    store: StoreOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.extended.electricMint)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.M),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (store.city != null) {
                    Text(
                        text = store.city,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₪${String.format("%.2f", store.totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (store.savings > 0) {
                    Text(
                        text = "חיסכון: ₪${String.format("%.2f", store.savings)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.extended.electricMint
                    )
                }
            }
        }
    }
}

/**
 * Summary row for cart totals
 */
@Composable
fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

/**
 * Store option item
 */
@Composable
private fun StoreOptionItem(
    store: StoreOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSelectableCard(
        selected = isSelected,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.M),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (store.city != null) {
                    Text(
                        text = store.city,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₪${String.format("%.2f", store.totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (store.savings > 0) {
                    Text(
                        text = "חיסכון: ₪${String.format("%.2f", store.savings)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.extended.electricMint
                    )
                }
            }
        }
    }
}

/**
 * Data class for store option
 */
data class StoreOption(
    val storeId: String,
    val name: String,
    val city: String?,
    val totalPrice: Double,
    val savings: Double = 0.0
)