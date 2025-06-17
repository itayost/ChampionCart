package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.domain.models.*
import com.example.championcart.ui.theme.*

/**
 * Cart Screen - Modern Shopping Cart with Electric Harmony Design
 * Uses only server-provided data structure
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(),
    onNavigateToSearch: () -> Unit = {},
    onNavigateToResults: (city: String, items: List<CartProduct>) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        viewModel.loadSavedCarts()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.extendedColors.backgroundGradient.colors.last()
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimensions.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            // Header Section
            item {
                CartHeader(
                    cartTotal = state.totalPrice,
                    itemCount = state.cartItems.size,
                    selectedCity = state.selectedCity,
                    onCityClick = { viewModel.showCityDialog() }
                )
            }

            // Search Bar
            item {
                SearchAddProductBar(
                    onAddProductClick = onNavigateToSearch,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Cart Items Section
            if (state.cartItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Cart (${state.cartItems.size} items)",
                        style = AppTextStyles.hebrewHeadline,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = Dimensions.paddingSmall)
                    )
                }

                // Fixed: Proper items() usage with LocalCartItem objects
                items(
                    items = state.cartItems,
                    key = { cartItem -> cartItem.id }
                ) { cartItem ->
                    Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
                    CartItemCard(
                        item = cartItem,
                        onQuantityChange = { newQuantity ->
                            viewModel.updateQuantity(cartItem.id, newQuantity)
                        },
                        onRemoveItem = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.removeItem(cartItem.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Action Buttons
                item {
                    Spacer(modifier = Modifier.height(Dimensions.spacingLarge))
                    CartActionButtons(
                        isLoading = state.isFindingCheapest,
                        cartItems = state.cartItems,
                        selectedCity = state.selectedCity,
                        onFindBestPrices = {
                            val cartProducts = state.cartItems.map {
                                CartProduct(it.itemName, it.quantity)
                            }
                            viewModel.findCheapestCart()
                            onNavigateToResults(state.selectedCity, cartProducts)
                        },
                        onSaveCart = { viewModel.showSaveDialog() },
                        onClearCart = { viewModel.clearCart() }
                    )
                }
            } else {
                // Empty Cart State
                item {
                    EmptyCartState(
                        onAddProductClick = onNavigateToSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.paddingLarge)
                    )
                }
            }

            // Saved Carts Section
            if (state.savedCarts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))
                    Text(
                        text = "Saved Carts",
                        style = AppTextStyles.hebrewHeadline,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = Dimensions.paddingSmall)
                    )
                }

                // Fixed: Proper items() usage with SavedCart objects
                items(
                    items = state.savedCarts,
                    key = { savedCart -> "${savedCart.cartName}_${savedCart.city}" }
                ) { savedCart ->
                    SavedCartCard(
                        savedCart = savedCart,
                        onLoadCart = { viewModel.loadSavedCart(savedCart) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Loading Overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.extendedColors.glass),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = ComponentShapes.Dialog,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.extendedColors.glassFrosted
                    )
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(Dimensions.paddingLarge),
                        color = MaterialTheme.extendedColors.electricMint
                    )
                }
            }
        }
    }

    // Dialogs
    if (state.showSaveDialog) {
        SaveCartDialog(
            onSave = { cartName ->
                viewModel.saveCart(cartName)
                viewModel.hideSaveDialog()
            },
            onDismiss = { viewModel.hideSaveDialog() }
        )
    }

    if (state.showSavedCartsDialog) {
        SavedCartsDialog(
            savedCarts = state.savedCarts,
            onLoadCart = { cart ->
                viewModel.loadSavedCart(cart)
                viewModel.hideSavedCartsDialog()
            },
            onDismiss = { viewModel.hideSavedCartsDialog() }
        )
    }

    // Error Handling
    state.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
        }
    }
}

@Composable
private fun CartHeader(
    cartTotal: Double,
    itemCount: Int,
    selectedCity: String,
    onCityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationMedium
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            // City Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCityClick() }
                    .padding(Dimensions.paddingSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.extendedColors.electricMint,
                        modifier = Modifier.size(Dimensions.iconSizeMedium)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                    Text(
                        text = selectedCity,
                        style = AppTextStyles.hebrewBody,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Change city",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
            }

            HorizontalDivider(
                thickness = Dimensions.dividerThickness,
                color = MaterialTheme.extendedColors.glassBorder
            )

            // Cart Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cart Total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₪${String.format("%.2f", cartTotal)}",
                        style = AppTextStyles.priceLarge,
                        color = MaterialTheme.extendedColors.electricMint,
                        fontWeight = FontWeight.Bold
                    )
                }

                Card(
                    shape = ComponentShapes.Badge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.extendedColors.electricMint
                    )
                ) {
                    Text(
                        text = "$itemCount items",
                        style = AppTextStyles.badge,
                        color = Color.White,
                        modifier = Modifier.padding(
                            horizontal = Dimensions.paddingMedium,
                            vertical = Dimensions.paddingSmall
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchAddProductBar(
    onAddProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.searchBarHeight)
            .clickable { onAddProductClick() },
        shape = ComponentShapes.SearchBar,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glass
        ),
        border = BorderStroke(
            width = Dimensions.borderThin,
            color = MaterialTheme.extendedColors.glassBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimensions.searchBarIconSize)
            )
            Text(
                text = "Add products to your cart...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add product",
                tint = MaterialTheme.extendedColors.electricMint,
                modifier = Modifier.size(Dimensions.iconSizeMedium)
            )
        }
    }
}

@Composable
private fun CartItemCard(
    item: LocalCartItem,
    onQuantityChange: (Int) -> Unit,
    onRemoveItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(Dimensions.cartItemHeight),
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
                .fillMaxSize()
                .padding(Dimensions.listItemPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.itemName,
                    style = AppTextStyles.productName,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Dimensions.spacingExtraSmall))
                Text(
                    text = "₪${String.format("%.2f", item.price)}",
                    style = AppTextStyles.priceMedium,
                    color = MaterialTheme.extendedColors.electricMint
                )
                // Show chain if available
                item.chain?.let { chain ->
                    Text(
                        text = chain,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quantity Controls
            QuantityControls(
                quantity = item.quantity,
                onQuantityChange = onQuantityChange,
                onRemove = onRemoveItem
            )
        }
    }
}

@Composable
private fun QuantityControls(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
    ) {
        // Remove/Decrease Button
        IconButton(
            onClick = {
                if (quantity <= 1) {
                    onRemove()
                } else {
                    onQuantityChange(quantity - 1)
                }
            },
            modifier = Modifier.size(Dimensions.quantityButtonSize)
        ) {
            Icon(
                imageVector = if (quantity <= 1) Icons.Default.Delete else Icons.Default.Remove,
                contentDescription = if (quantity <= 1) "Remove item" else "Decrease quantity",
                tint = if (quantity <= 1)
                    MaterialTheme.extendedColors.errorRed
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimensions.iconSizeSmall)
            )
        }

        // Quantity Display
        Card(
            shape = ComponentShapes.Badge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.glass
            )
        ) {
            Text(
                text = quantity.toString(),
                style = AppTextStyles.badge,
                modifier = Modifier.padding(
                    horizontal = Dimensions.paddingMedium,
                    vertical = Dimensions.paddingSmall
                ),
                textAlign = TextAlign.Center,
                minLines = 1
            )
        }

        // Add Button
        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(Dimensions.quantityButtonSize)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase quantity",
                tint = MaterialTheme.extendedColors.electricMint,
                modifier = Modifier.size(Dimensions.iconSizeSmall)
            )
        }
    }
}

@Composable
private fun CartActionButtons(
    isLoading: Boolean,
    cartItems: List<LocalCartItem>,
    selectedCity: String,
    onFindBestPrices: () -> Unit,
    onSaveCart: () -> Unit,
    onClearCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        // Primary CTA - Find Best Prices
        Button(
            onClick = onFindBestPrices,
            enabled = !isLoading && cartItems.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.buttonHeight),
            shape = ComponentShapes.Button,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.extendedColors.electricMint,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = Dimensions.elevationMedium
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                Text(
                    text = "Find Best Prices",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Secondary Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            // Save Cart
            OutlinedButton(
                onClick = onSaveCart,
                enabled = cartItems.isNotEmpty(),
                modifier = Modifier.weight(1f),
                shape = ComponentShapes.Button,
                border = BorderStroke(
                    width = Dimensions.borderThin,
                    color = MaterialTheme.extendedColors.electricMint
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.electricMint,
                    modifier = Modifier.size(Dimensions.iconSizeExtraSmall)
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingExtraSmall))
                Text(
                    text = "Save",
                    color = MaterialTheme.extendedColors.electricMint
                )
            }

            // Clear Cart
            OutlinedButton(
                onClick = onClearCart,
                enabled = cartItems.isNotEmpty(),
                modifier = Modifier.weight(1f),
                shape = ComponentShapes.Button,
                border = BorderStroke(
                    width = Dimensions.borderThin,
                    color = MaterialTheme.extendedColors.errorRed
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.errorRed,
                    modifier = Modifier.size(Dimensions.iconSizeExtraSmall)
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingExtraSmall))
                Text(
                    text = "Clear",
                    color = MaterialTheme.extendedColors.errorRed
                )
            }
        }
    }
}

@Composable
private fun EmptyCartState(
    onAddProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingExtraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSizeHuge),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Your cart is empty",
                style = AppTextStyles.hebrewHeadline,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Add products to start comparing prices and find the best deals",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingSmall))

            Button(
                onClick = onAddProductClick,
                shape = ComponentShapes.Button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.extendedColors.electricMint
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconSizeExtraSmall)
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                Text(text = "Add Products")
            }
        }
    }
}

@Composable
private fun SavedCartCard(
    savedCart: SavedCart,
    onLoadCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onLoadCart() },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glass
        ),
        border = BorderStroke(
            width = Dimensions.borderThin,
            color = MaterialTheme.extendedColors.glassBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.listItemPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = savedCart.cartName,
                    style = AppTextStyles.productNameLarge,
                    fontWeight = FontWeight.Medium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconSizeExtraSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = savedCart.city,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "${savedCart.items.size} items • Total: ₪${savedCart.items.sumOf { it.price * it.quantity }.let { String.format("%.2f", it) }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SaveCartDialog(
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var cartName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = ComponentShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.glassFrosted
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.dialogPadding),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                Text(
                    text = "Save Cart",
                    style = AppTextStyles.hebrewHeadline,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = cartName,
                    onValueChange = { cartName = it },
                    label = { Text("Cart Name") },
                    placeholder = { Text("e.g., Weekly Shopping") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ComponentShapes.TextField,
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = ComponentShapes.Button
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (cartName.isNotBlank()) {
                                onSave(cartName.trim())
                            }
                        },
                        enabled = cartName.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape = ComponentShapes.Button,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.extendedColors.electricMint
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedCartsDialog(
    savedCarts: List<SavedCart>,
    onLoadCart: (SavedCart) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = ComponentShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.glassFrosted
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = Dimensions.modalMaxHeight)
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.dialogPadding)
            ) {
                Text(
                    text = "Saved Carts",
                    style = AppTextStyles.hebrewHeadline,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = Dimensions.spacingMedium)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
                ) {
                    items(savedCarts) { savedCart ->
                        SavedCartCard(
                            savedCart = savedCart,
                            onLoadCart = { onLoadCart(savedCart) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = ComponentShapes.Button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.electricMint
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
}