package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.data.local.CartItem
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.presentation.ViewModelFactory
import com.example.championcart.presentation.components.*
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

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
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    // Show result dialog if available
    uiState.cheapestCartResult?.let { result ->
        CheapestStoreDialog(
            result = result,
            onDismiss = { viewModel.dismissResult() },
            onNavigateToStore = {
                // TODO: Implement navigation to store
            }
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
                showSuccessSnackbar = true
            }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar(
                message = "Cart saved successfully!",
                duration = SnackbarDuration.Short
            )
            showSuccessSnackbar = false
        }
    }

    Scaffold(
        topBar = {
            CartTopBar(
                totalItems = uiState.totalItems,
                isNotEmpty = uiState.cartItems.isNotEmpty(),
                onSaveCart = { showSaveDialog = true },
                onClearCart = { viewModel.clearCart() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0)
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
                // City indicator section
                CityIndicatorSection(
                    city = uiState.selectedCity,
                    onCityClick = showCityDialog,
                    modifier = Modifier.padding(
                        horizontal = Dimensions.screenPadding,
                        vertical = Dimensions.paddingMedium
                    )
                )

                // Cart content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = Dimensions.screenPadding,
                        bottom = Dimensions.paddingLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                ) {
                    // Cart items
                    items(
                        items = uiState.cartItems,
                        key = { it.itemCode }
                    ) { cartItem ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
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

                    // Find cheapest store section
                    item {
                        Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
                        FindCheapestStoreSection(
                            city = uiState.selectedCity,
                            isAnalyzing = uiState.isAnalyzing,
                            error = uiState.error,
                            onAnalyze = { viewModel.findCheapestStore() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartTopBar(
    totalItems: Int,
    isNotEmpty: Boolean,
    onSaveCart: () -> Unit,
    onClearCart: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Shopping Cart",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (totalItems > 0) {
                        Text(
                            text = "$totalItems items • Ready to find savings? 💰",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        },
        actions = {
            if (isNotEmpty) {
                // Save cart button
                IconButton(onClick = onSaveCart) {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = "Save Cart",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                // Clear cart button
                TextButton(
                    onClick = onClearCart,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        "Clear",
                        style = AppTextStyles.buttonText
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun CityIndicatorSection(
    city: String,
    onCityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Finding best prices in",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        CityIndicator(
            city = city,
            onClick = onCityClick
        )
    }
}

@Composable
private fun EmptyCartContent(
    modifier: Modifier = Modifier
) {
    EmptyStates.EmptyCart(
        onStartShopping = {
            // TODO: Navigate to search
        },
        modifier = modifier
    )
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.cardPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Product info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cartItem.itemName,
                        style = if (isHebrewText(cartItem.itemName)) {
                            AppTextStyles.hebrewTextBold
                        } else {
                            AppTextStyles.productNameLarge
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(Dimensions.spacingExtraSmall))

                    // Price info
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        cartItem.selectedPrice?.let { price ->
                            Text(
                                text = "₪${String.format("%.2f", price * cartItem.quantity)}",
                                style = AppTextStyles.priceDisplay,
                                color = MaterialTheme.extendedColors.savings,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        cartItem.selectedChain?.let { chain ->
                            Card(
                                shape = ComponentShapes.Badge,
                                colors = CardDefaults.cardColors(
                                    containerColor = getStoreColor(chain).copy(alpha = 0.1f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = chain.uppercase(),
                                    style = AppTextStyles.chipText,
                                    color = getStoreColor(chain),
                                    modifier = Modifier.padding(
                                        horizontal = Dimensions.paddingSmall,
                                        vertical = 2.dp
                                    )
                                )
                            }
                        }
                    }
                }

                // Quantity controls column
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
                ) {
                    // Quantity controls
                    QuantityControls(
                        quantity = cartItem.quantity,
                        onDecrease = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onQuantityChange(cartItem.quantity - 1)
                        },
                        onIncrease = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onQuantityChange(cartItem.quantity + 1)
                        }
                    )

                    // Remove button
                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onRemove()
                        }
                    ) {
                        Text(
                            text = "[Remove]",
                            style = AppTextStyles.buttonText,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuantityControls(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
    ) {
        // Decrease button
        FilledIconButton(
            onClick = onDecrease,
            modifier = Modifier.size(32.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "−",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Quantity
        Text(
            text = quantity.toString(),
            style = AppTextStyles.priceDisplay,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center
        )

        // Increase button
        FilledIconButton(
            onClick = onIncrease,
            modifier = Modifier.size(32.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun FindCheapestStoreSection(
    city: String,
    isAnalyzing: Boolean,
    error: String?,
    onAnalyze: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        // Error message if any
        error?.let { errorMessage ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = ComponentShapes.Card
            ) {
                Row(
                    modifier = Modifier.padding(Dimensions.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Find cheapest store card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = ComponentShapes.Card,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.savings.copy(alpha = 0.08f)
            ),
            border = BorderStroke(
                width = Dimensions.borderThin,
                color = MaterialTheme.extendedColors.savings.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.extendedColors.savings.copy(alpha = 0.2f),
                                    MaterialTheme.extendedColors.savings.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.extendedColors.savings
                    )
                }

                // Title and description
                Text(
                    text = "🎯 Find Cheapest Store",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Compare prices across all stores in $city",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Analyze button
                ActionButton(
                    text = if (isAnalyzing) "ANALYZING..." else "ANALYZE NOW",
                    onClick = onAnalyze,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAnalyzing,
                    loading = isAnalyzing,
                    icon = if (!isAnalyzing) Icons.Default.Search else null,
                    size = ButtonSize.LARGE
                )
            }
        }
    }
}

// Helper function to get store brand color
@Composable
private fun getStoreColor(storeName: String): Color {
    return when (storeName.lowercase()) {
        "shufersal" -> ChampionCartColors.shufersal
        "victory" -> ChampionCartColors.victory
        else -> MaterialTheme.colorScheme.primary
    }
}

// Helper function to detect Hebrew text
private fun isHebrewText(text: String): Boolean {
    return text.any { char ->
        Character.UnicodeBlock.of(char) == Character.UnicodeBlock.HEBREW
    }
}