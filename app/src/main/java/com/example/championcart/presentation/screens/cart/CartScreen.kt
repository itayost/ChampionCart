package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.domain.models.*
import com.example.championcart.presentation.components.EmptyState
import com.example.championcart.presentation.components.ErrorState
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current

    // Save cart dialog
    if (state.showSaveDialog) {
        SaveCartDialog(
            cartName = state.saveCartName,
            onCartNameChange = viewModel::updateSaveCartName,
            onSave = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.saveCart()
            },
            onDismiss = viewModel::hideSaveDialog,
            isLoading = state.isSaving
        )
    }

    // Saved carts dialog
    if (state.showSavedCartsDialog) {
        SavedCartsDialog(
            savedCarts = state.savedCarts,
            onLoadCart = { cart ->
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.loadSavedCart(cart)
            },
            onDismiss = viewModel::hideSavedCartsDialog,
            isLoading = state.isLoadingSavedCarts
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.extendedColors.electricMint.copy(alpha = 0.02f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        // Cart header
        CartHeader(
            itemCount = state.cartItems.size,
            totalPrice = state.totalPrice,
            selectedCity = state.selectedCity,
            onCityChange = viewModel::selectCity,
            onClearCart = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.clearCart()
            },
            onSaveCart = viewModel::showSaveDialog,
            onLoadCart = viewModel::showSavedCartsDialog
        )

        when {
            state.cartItems.isEmpty() -> {
                EmptyCartState()
            }

            state.error != null -> {
                ErrorState(
                    message = state.error,
                    onRetry = viewModel::retryLastAction,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Cart items
                    items(
                        items = state.cartItems,
                        key = { it.id }
                    ) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateQuantity(cartItem.id, newQuantity)
                            },
                            onRemove = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.removeItem(cartItem.id)
                            }
                        )
                    }

                    // Cheapest cart result
                    state.cheapestCartResult?.let { result ->
                        item {
                            CheapestCartResultCard(
                                result = result,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Bottom action bar
                CartBottomBar(
                    totalPrice = state.totalPrice,
                    cheapestCartResult = state.cheapestCartResult,
                    onFindCheapest = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.findCheapestCart()
                    },
                    isFindingCheapest = state.isFindingCheapest,
                    hasItems = state.cartItems.isNotEmpty()
                )
            }
        }
    }
}

@Composable
fun CartHeader(
    itemCount: Int,
    totalPrice: Double,
    selectedCity: String,
    onCityChange: (String) -> Unit,
    onClearCart: () -> Unit,
    onSaveCart: () -> Unit,
    onLoadCart: () -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Cart",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$itemCount items • ₪${String.format("%.2f", totalPrice)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Actions dropdown
                Box {
                    IconButton(onClick = { showDropdown = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More actions"
                        )
                    }

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Save Cart") },
                            onClick = {
                                onSaveCart()
                                showDropdown = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Save, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Load Cart") },
                            onClick = {
                                onLoadCart()
                                showDropdown = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Folder, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear All") },
                            onClick = {
                                onClearCart()
                                showDropdown = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // City selector
            FilterChip(
                onClick = { /* TODO: City selection dialog */ },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Shopping in $selectedCity")
                    }
                },
                selected = false,
                shape = ComponentShapes.Chip
            )
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        border = BorderStroke(1.dp, MaterialTheme.extendedColors.glassBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "₪${String.format("%.2f", cartItem.price)} each",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                cartItem.selectedStore?.let { store ->
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.electricMint
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Quantity controls
            QuantityControls(
                quantity = cartItem.quantity,
                onQuantityChange = onQuantityChange,
                onRemove = onRemove
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Total price for this item
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₪${String.format("%.2f", cartItem.price * cartItem.quantity)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.successGreen
                )
            }
        }
    }
}

@Composable
fun QuantityControls(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Decrease button
        IconButton(
            onClick = {
                if (quantity > 1) {
                    onQuantityChange(quantity - 1)
                } else {
                    onRemove()
                }
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                contentDescription = if (quantity > 1) "Decrease quantity" else "Remove item",
                tint = if (quantity > 1) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }

        // Quantity display
        Surface(
            color = MaterialTheme.extendedColors.glass,
            shape = ComponentShapes.ButtonSmall,
            border = BorderStroke(1.dp, MaterialTheme.extendedColors.glassBorder)
        ) {
            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Increase button
        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase quantity",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun CheapestCartResultCard(
    result: CheapestCartResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.successGreen.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.extendedColors.successGreen.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.successGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Best Deal Found!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.successGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Best store info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = result.bestStore.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Total: ₪${String.format("%.2f", result.totalPrice)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (result.savings > 0) {
                    Surface(
                        color = MaterialTheme.extendedColors.successGreen,
                        shape = ComponentShapes.Badge
                    ) {
                        Text(
                            text = "Save ₪${String.format("%.2f", result.savings)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Store comparison
            if (result.allStores.isNotEmpty()) {
                Text(
                    text = "Price Comparison:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                result.allStores.sortedBy { it.totalPrice }.forEach { store ->
                    StoreComparisonRow(
                        store = store,
                        isBest = store.chain == result.chain,
                        savings = if (store.chain == result.chain) 0.0 else store.totalPrice - result.totalPrice
                    )
                }
            }
        }
    }
}

@Composable
fun StoreComparisonRow(
    store: StoreOption,
    isBest: Boolean,
    savings: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = store.chain.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isBest) FontWeight.SemiBold else FontWeight.Normal
            )

            if (isBest) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Best price",
                    tint = MaterialTheme.extendedColors.successGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = "₪${String.format("%.2f", store.totalPrice)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal,
            color = if (isBest) MaterialTheme.extendedColors.successGreen
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CartBottomBar(
    totalPrice: Double,
    cheapestCartResult: CheapestCartResult?,
    onFindCheapest: () -> Unit,
    isFindingCheapest: Boolean,
    hasItems: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.extendedColors.glassFrosted,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Total:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "₪${String.format("%.2f", totalPrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.successGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Find cheapest button
            Button(
                onClick = onFindCheapest,
                enabled = hasItems && !isFindingCheapest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.extendedColors.electricMint
                ),
                shape = ComponentShapes.Button
            ) {
                if (isFindingCheapest) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finding Best Prices...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (cheapestCartResult != null) "Find Again" else "Find Cheapest Store",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCartState() {
    EmptyState(
        icon = Icons.Default.ShoppingCart,
        title = "Your cart is empty",
        description = "Start adding products to see price comparisons and find the best deals!",
        actionText = "Start Shopping",
        onAction = { /* Navigate to search */ },
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    )
}

@Composable
fun SaveCartDialog(
    cartName: String,
    onCartNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Cart") },
        text = {
            Column {
                Text(
                    text = "Give your cart a name to save it for later:",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = cartName,
                    onValueChange = onCartNameChange,
                    label = { Text("Cart Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = cartName.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SavedCartsDialog(
    savedCarts: List<SavedCart>,
    onLoadCart: (SavedCart) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            shape = ComponentShapes.Dialog
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Saved Carts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    savedCarts.isEmpty() -> {
                        Text(
                            text = "No saved carts found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(savedCarts) { cart ->
                                SavedCartItem(
                                    cart = cart,
                                    onLoad = { onLoadCart(cart) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun SavedCartItem(
    cart: SavedCart,
    onLoad: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLoad() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glass
        ),
        border = BorderStroke(1.dp, MaterialTheme.extendedColors.glassBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cart.cartName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Load cart",
                    tint = MaterialTheme.extendedColors.electricMint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "${cart.items.size} items • ${cart.city}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}