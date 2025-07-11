package com.example.championcart.presentation.components.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.championcart.domain.models.SavedCart
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedCartsBottomSheet(
    visible: Boolean,
    savedCarts: List<SavedCart>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onLoadCart: (SavedCart) -> Unit,
    onDeleteCart: (SavedCart) -> Unit,
    onCompareCart: (SavedCart) -> Unit
) {
    ChampionBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = "העגלות השמורות שלי"
    ) {
        when {
            isLoading -> {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = BrandColors.ElectricMint
                    )
                }
            }
            savedCarts.isEmpty() -> {
                // Empty state
                EmptyStateMessage(
                    icon = Icons.Rounded.ShoppingCartCheckout,
                    title = "אין עגלות שמורות",
                    message = "שמור את העגלה הנוכחית כדי לגשת אליה מאוחר יותר",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xxl)
                )
            }
            else -> {
                // Saved carts list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    contentPadding = PaddingValues(
                        horizontal = Spacing.l,
                        vertical = Spacing.m
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    items(savedCarts) { cart ->
                        SavedCartItem(
                            cart = cart,
                            onLoadCart = { onLoadCart(cart) },
                            onDeleteCart = { onDeleteCart(cart) },
                            onCompareCart = { onCompareCart(cart) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedCartItem(
    cart: SavedCart,
    onLoadCart: () -> Unit,
    onDeleteCart: () -> Unit,
    onCompareCart: () -> Unit
) {
    var showActions by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showActions = !showActions }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.l)
        ) {
            // Cart header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Cart info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = cart.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Item count
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${cart.itemCount} מוצרים",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Total quantity
                        Text(
                            text = "• ${cart.totalItems} פריטים",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Date
                    Text(
                        text = formatDate(cart.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Expand/collapse icon
                Icon(
                    imageVector = if (showActions) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (showActions) "הסתר אפשרויות" else "הצג אפשרויות",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action buttons
            AnimatedVisibility(
                visible = showActions,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.m),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.xs),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        // Load cart button
                        OutlinedButton(
                            onClick = onLoadCart,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = BrandColors.ElectricMint
                            ),
                            border = BorderStroke(1.dp, BrandColors.ElectricMint.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text("טען")
                        }

                        // Compare prices button
                        OutlinedButton(
                            onClick = onCompareCart,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CompareArrows,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text("השווה")
                        }

                        // Delete button
                        IconButton(
                            onClick = onDeleteCart,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "מחק עגלה"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDate(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("d בMMMM, HH:mm", Locale("he", "IL"))
        val date = inputFormat.parse(isoDate)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        isoDate
    }
}