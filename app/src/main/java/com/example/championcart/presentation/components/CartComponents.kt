package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Cart Specific Components
 */

/**
 * Cart item card with swipe to delete
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onRemove()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error)
                    .padding(horizontal = SpacingTokens.L),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        },
        modifier = modifier
    ) {
        GlassCard(
            onClick = onProductClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingTokens.M),
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
            ) {
                // Product image
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )

                // Product info and controls
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
                ) {
                    // Name and brand
                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        item.brand?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Price and quantity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Price
                        Column {
                            Text(
                                text = "₪${String.format("%.2f", item.price * item.quantity)}",
                                style = AppTextStyles.priceLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.extended.electricMint
                            )
                            Text(
                                text = "₪${String.format("%.2f", item.price)} ליחידה",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Quantity selector
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
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease button
        QuantityButton(
            onClick = {
                if (quantity > minQuantity) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onQuantityChange(quantity - 1)
                }
            },
            enabled = quantity > minQuantity
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Decrease",
                modifier = Modifier.size(16.dp)
            )
        }

        // Quantity display
        Box(
            modifier = Modifier
                .widthIn(min = 40.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = SpacingTokens.S, vertical = SpacingTokens.XS),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        // Increase button
        QuantityButton(
            onClick = {
                if (quantity < maxQuantity) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onQuantityChange(quantity + 1)
                }
            },
            enabled = quantity < maxQuantity
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Increase",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun QuantityButton(
    onClick: () -> Unit,
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                if (enabled) {
                    MaterialTheme.colorScheme.extended.electricMint
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides if (enabled) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            }
        ) {
            content()
        }
    }
}

/**
 * Cart summary card
 */
@Composable
fun CartSummaryCard(
    subtotal: Double,
    savings: Double = 0.0,
    deliveryFee: Double = 0.0,
    total: Double,
    itemCount: Int,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            // Title
            Text(
                text = "סיכום הזמנה",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider()

            // Price breakdown
            Column(
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
            ) {
                SummaryRow(
                    label = "סה\"כ מוצרים ($itemCount)",
                    value = "₪${String.format("%.2f", subtotal)}"
                )

                if (savings > 0) {
                    SummaryRow(
                        label = "חיסכון",
                        value = "-₪${String.format("%.2f", savings)}",
                        valueColor = MaterialTheme.colorScheme.extended.electricMint
                    )
                }

                if (deliveryFee > 0) {
                    SummaryRow(
                        label = "משלוח",
                        value = "₪${String.format("%.2f", deliveryFee)}"
                    )
                }
            }

            HorizontalDivider()

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "סה\"כ לתשלום",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₪${String.format("%.2f", total)}",
                    style = AppTextStyles.priceLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.extended.electricMint
                )
            }

            // Checkout button
            PrimaryButton(
                text = "המשך לתשלום",
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && itemCount > 0,
                leadingIcon = Icons.Default.ShoppingCartCheckout
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
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
 * Store selector for multi-store carts
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
        Text(
            text = "בחר חנות",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )

        stores.forEach { store ->
            StoreSelectorItem(
                store = store,
                isSelected = store.id == selectedStoreId,
                onClick = { onStoreSelected(store.id) }
            )
        }
    }
}

@Composable
private fun StoreSelectorItem(
    store: StoreOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        },
        animationSpec = tween(300),
        label = "border_color"
    )

    GlassCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = animatedBorderColor,
                shape = GlassmorphicShapes.GlassCard
            )
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
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
                Text(
                    text = store.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₪${String.format("%.2f", store.totalPrice)}",
                    style = AppTextStyles.priceMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.extended.electricMint
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
 * Empty cart component
 */
@Composable
fun EmptyCartState(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacingTokens.XL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.extended.electricMint,
                modifier = Modifier.size(60.dp)
            )
        }

        // Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            Text(
                text = "העגלה שלך ריקה",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "הוסף מוצרים כדי להתחיל לחסוך",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Action button
        PrimaryButton(
            text = "התחל לקנות",
            onClick = onStartShopping,
            modifier = Modifier.fillMaxWidth(0.8f),
            leadingIcon = Icons.Default.Search
        )
    }
}

// Data classes for cart components
data class StoreOption(
    val id: String,
    val name: String,
    val address: String,
    val totalPrice: Double,
    val savings: Double = 0.0
)