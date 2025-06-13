package com.example.championcart.presentation.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.data.local.CartItem
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.presentation.ViewModelFactory
import com.example.championcart.presentation.components.CityIndicator
import com.example.championcart.presentation.components.rememberCitySelectionDialog
import com.example.championcart.presentation.theme.ChampionCartColors
import com.example.championcart.presentation.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen() {
    val context = LocalContext.current
    val viewModel: CartViewModel = viewModel(factory = ViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()

    // City selection
    val tokenManager = remember { TokenManager(context) }
    val (currentCity, showCityDialog) = rememberCitySelectionDialog(
        tokenManager = tokenManager,
        onCitySelected = { city ->
            viewModel.onCityChange(city)
        }
    )

    // Save cart dialog
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Show result dialog if available
    uiState.cheapestCartResult?.let { result ->
        CheapestStoreDialog(
            result = result,
            onDismiss = { viewModel.dismissResult() }
        )
    }

    // Show save cart dialog
    if (showSaveDialog) {
        SaveCartDialog(
            cartItems = uiState.cartItems,
            city = uiState.selectedCity,
            tokenManager = tokenManager,
            onDismiss = { showSaveDialog = false },
            onSaved = {
                showSuccessMessage = true
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("My Cart")
                        if (uiState.totalItems > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Text(uiState.totalItems.toString())
                            }
                        }
                    }
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        // Save cart button
                        IconButton(onClick = { showSaveDialog = true }) {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Save Cart",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        // Clear cart button
                        TextButton(
                            onClick = { viewModel.clearCart() },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Clear")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() }) { data ->
                if (showSuccessMessage) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(
                                onClick = { showSuccessMessage = false }
                            ) {
                                Text("Dismiss")
                            }
                        }
                    ) {
                        Text("Cart saved successfully!")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.cartItems.isEmpty()) {
            // Empty cart state
            EmptyCartContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            // Cart with items
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // City indicator at the top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Finding best prices in",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        CityIndicator(
                            city = uiState.selectedCity,
                            onClick = showCityDialog
                        )
                    }
                }

                // Cart items list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.cartItems,
                        key = { it.itemCode }
                    ) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateQuantity(cartItem.itemCode, newQuantity)
                            },
                            onRemove = {
                                viewModel.removeFromCart(cartItem.itemCode)
                            }
                        )
                    }
                }

                // Cart Summary
                CartSummaryCard(
                    totalItems = uiState.totalItems,
                    selectedCity = uiState.selectedCity,
                    isAnalyzing = uiState.isAnalyzing,
                    error = uiState.error,
                    onFindCheapestStore = { viewModel.findCheapestStore() }
                )
            }
        }
    }
}

@Composable
private fun EmptyCartContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your cart is empty",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start adding products from search",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Product info
            Column(
                modifier = Modifier.weight(1f).padding(end = 16.dp)
            ) {
                Text(
                    text = cartItem.itemName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    cartItem.selectedPrice?.let { price ->
                        Text(
                            text = "₪${String.format("%.2f", price)}",
                            fontSize = 14.sp,
                            color = MaterialTheme.extendedColors.savings,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    cartItem.selectedChain?.let { chain ->
                        Text(
                            text = chain.uppercase(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Code: ${cartItem.itemCode}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Quantity controls
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Decrease button
                    FilledIconButton(
                        onClick = { onQuantityChange(cartItem.quantity - 1) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease quantity",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Quantity text
                    Text(
                        text = cartItem.quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.Center
                    )

                    // Increase button
                    FilledIconButton(
                        onClick = { onQuantityChange(cartItem.quantity + 1) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase quantity",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                // Remove button
                TextButton(
                    onClick = onRemove,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove from cart",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Remove",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun CartSummaryCard(
    totalItems: Int,
    selectedCity: String,
    isAnalyzing: Boolean,
    error: String?,
    onFindCheapestStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large.copy(
            bottomEnd = androidx.compose.foundation.shape.CornerSize(0.dp),
            bottomStart = androidx.compose.foundation.shape.CornerSize(0.dp)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Error message if any
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp
                    )
                }
            }

            // Summary info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Items",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = totalItems.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "City",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedCity,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Find cheapest store button
            Button(
                onClick = onFindCheapestStore,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isAnalyzing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.extendedColors.savings
                )
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Find Cheapest Store",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}