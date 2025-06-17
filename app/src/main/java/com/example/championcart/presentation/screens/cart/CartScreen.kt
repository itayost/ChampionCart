package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.domain.models.*
import com.example.championcart.presentation.components.EmptyState
import com.example.championcart.presentation.components.ErrorState
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel()
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

            else -> {
                val errorMessage = state.error
                if (errorMessage != null) {
                    ErrorState(
                        message = errorMessage,
                        onRetry = viewModel::retryLastAction
                    )
                } else {
                    // Cart content
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = Dimensions.paddingMedium),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
                    ) {
                        items(state.cartItems) { cartItem ->
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

                        // Show cheapest cart result if available
                        state.cheapestCartResult?.let { result ->
                            item {
                                CheapestCartResultCard(
                                    result = result,
                                    modifier = Modifier.padding(vertical = Dimensions.spacingMedium)
                                )
                            }
                        }
                    }

                    // Bottom bar
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
            modifier = Modifier.padding(Dimensions.paddingLarge)
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
                        style = AppTextStyles.hebrewHeadline,
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
                            text = { Text("Clear Cart") },
                            onClick = {
                                onClearCart()
                                showDropdown = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // City selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.electricMint,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
                Text(
                    text = "Shopping in $selectedCity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyCartState() {
    EmptyState(
        title = "Your cart is empty",
        subtitle = "Add products to find the best prices",
        icon = Icons.Default.ShoppingCart,
        actionLabel = "Start Shopping",
        onAction = { /* Navigate to search */ }
    )
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationSmall
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            // Product image placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(ComponentShapes.CardSmall)
                    .background(MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.electricMint,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Product details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
            ) {
                Text(
                    text = cartItem.productName,
                    style = AppTextStyles.productName,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "₪${String.format("%.2f", cartItem.price)}",
                    style = AppTextStyles.priceMedium,
                    color = MaterialTheme.extendedColors.success
                )

                cartItem.selectedStore?.let { store ->
                    Text(
                        text = "from ${store.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quantity controls
            QuantityControls(
                quantity = cartItem.quantity,
                onQuantityChange = onQuantityChange,
                onRemove = onRemove
            )
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
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
    ) {
        // Decrease/Remove button
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
            containerColor = MaterialTheme.extendedColors.success.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.extendedColors.success.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingLarge)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.success,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Best Deal Found!",
                    style = AppTextStyles.productNameLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.success
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Store breakdown
            result.allStores.forEach { storeOption ->
                StoreRow(
                    storeOption = storeOption,
                    isBest = storeOption.chain == result.chain
                )
                Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Savings summary
            Surface(
                shape = ComponentShapes.Card,
                color = MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.paddingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Savings:",
                        style = AppTextStyles.priceMedium,
                        color = MaterialTheme.extendedColors.electricMint
                    )
                    Text(
                        text = "₪${String.format("%.2f", result.savings)}",
                        style = AppTextStyles.priceMedium,
                        color = MaterialTheme.extendedColors.electricMint,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StoreRow(
    storeOption: StoreOption,
    isBest: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            Text(
                text = storeOption.chain.uppercase(),
                style = AppTextStyles.storeName,
                fontWeight = if (isBest) FontWeight.Bold else FontWeight.Medium,
                color = if (isBest) MaterialTheme.extendedColors.success
                else MaterialTheme.colorScheme.onSurface
            )

            if (isBest) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Best price",
                    tint = MaterialTheme.extendedColors.success,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = "₪${String.format("%.2f", storeOption.totalPrice)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal,
            color = if (isBest) MaterialTheme.extendedColors.success
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
            modifier = Modifier.padding(Dimensions.paddingLarge)
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
                    color = MaterialTheme.extendedColors.success
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Find cheapest button
            Button(
                onClick = onFindCheapest,
                enabled = hasItems && !isFindingCheapest,
                modifier = Modifier.fillMaxWidth(),
                shape = ComponentShapes.Button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.extendedColors.electricMint
                )
            ) {
                if (isFindingCheapest) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isFindingCheapest) "Finding..." else "Find Cheapest Cart",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun SaveCartDialog(
    cartName: String,
    onCartNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = ComponentShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.glassFrosted
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                Text(
                    text = "Save Cart",
                    style = AppTextStyles.hebrewHeadline,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = cartName,
                    onValueChange = onCartNameChange,
                    label = { Text("Cart Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onSave,
                        enabled = cartName.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.extendedColors.electricMint
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
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
            shape = ComponentShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.glassFrosted
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                Text(
                    text = "Saved Carts",
                    style = AppTextStyles.hebrewHeadline,
                    fontWeight = FontWeight.Bold
                )

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.extendedColors.electricMint
                            )
                        }
                    }
                    savedCarts.isEmpty() -> {
                        Text(
                            text = "No saved carts found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
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
            modifier = Modifier.padding(Dimensions.paddingMedium)
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