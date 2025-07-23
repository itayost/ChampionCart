package com.example.championcart.presentation.components.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
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
    onDeleteCart: (SavedCart) -> Unit
) {
    ChampionBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = "העגלות השמורות שלי"
    ) {
        // Apply RTL layout for the entire bottom sheet content
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            when {
                isLoading -> {
                    LoadingState()
                }
                savedCarts.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    SavedCartsList(
                        savedCarts = savedCarts,
                        onLoadCart = onLoadCart,
                        onDeleteCart = onDeleteCart
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = BrandColors.ElectricMint
        )
    }
}

@Composable
private fun EmptyState() {
    EmptyStateMessage(
        icon = Icons.Rounded.ShoppingCartCheckout,
        title = "אין עגלות שמורות",
        message = "שמור את העגלה הנוכחית כדי לגשת אליה מאוחר יותר",
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxl)
    )
}

@Composable
private fun SavedCartsList(
    savedCarts: List<SavedCart>,
    onLoadCart: (SavedCart) -> Unit,
    onDeleteCart: (SavedCart) -> Unit
) {
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
                onDeleteCart = { onDeleteCart(cart) }
            )
        }
    }
}

@Composable
private fun SavedCartItem(
    cart: SavedCart,
    onLoadCart: () -> Unit,
    onDeleteCart: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            // Cart info
            Column(
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
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${cart.itemCount} מוצרים",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                SecondaryButton(
                    text = "טען",
                    onClick = onLoadCart,
                    modifier = Modifier.weight(1f),
                )

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

@Composable
private fun EmptyStateMessage(
    icon: ImageVector,
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
        val outputFormat = SimpleDateFormat("d בMMMM", Locale("he", "IL"))
        val date = inputFormat.parse(isoDate)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        isoDate
    }
}