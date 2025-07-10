package com.example.championcart.presentation.components.cart

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.championcart.domain.models.CartItem
import com.example.championcart.presentation.components.common.ConfirmationDialog
import com.example.championcart.presentation.components.common.GlassCard
import com.example.championcart.presentation.components.common.QuantitySelector
import com.example.championcart.ui.theme.PriceColors
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    showDeleteConfirmation = true
                    false
                }
                else -> false
            }
        }
    )

    val scale by animateFloatAsState(
        targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart &&
            dismissState.progress > 0.5f) 0.95f else 1f,
        animationSpec = spring(),
        label = "itemScale"
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val backgroundColor by animateColorAsState(
                targetValue = when {
                    dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart &&
                            dismissState.progress > 0.1f -> SemanticColors.Error.copy(alpha = 0.8f)
                    else -> Color.Transparent
                },
                label = "backgroundColor"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(horizontal = Spacing.xl),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (dismissState.progress > 0.1f) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "מחק",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        content = {
            GlassCard(
                modifier = modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.m),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s)
                ) {
                    // Row 1: Product name
                    Text(
                        text = cartItem.product.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Row 2: Quantity selector and price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Quantity selector
                        QuantitySelector(
                            quantity = cartItem.quantity,
                            onQuantityChange = { newQuantity ->
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onQuantityChange(newQuantity)
                            }
                        )

                        // Right: Price info
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {

                            // Additional info
                            Column {
                                Text(
                                    text = "יח׳:₪${String.format("%.2f", cartItem.product.bestPrice)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                // Total price
                                Text(
                                    text = "₪${String.format("%.2f", cartItem.quantity * cartItem.product.bestPrice)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PriceColors.Best
                                )
                            }
                        }
                    }
                }
            }
        }
    )

    // Delete Confirmation Dialog
    ConfirmationDialog(
        visible = showDeleteConfirmation,
        onConfirm = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onRemove()
        },
        onDismiss = {
            showDeleteConfirmation = false
            scope.launch {
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        },
        title = "הסרת מוצר",
        text = "האם להסיר את ${cartItem.product.name} מהעגלה?",
        confirmText = "הסר",
        dismissText = "ביטול",
        isDangerous = true
    )
}