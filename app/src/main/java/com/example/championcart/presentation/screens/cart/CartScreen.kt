package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.presentation.components.*
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current

    var showStoreSelector by remember { mutableStateOf(false) }
    var showClearCartDialog by remember { mutableStateOf(false) }

    ChampionCartScreen(
        topBar = {
            CartTopBar(
                itemCount = uiState.items.size,
                onBackClick = { navController.popBackStack() },
                onClearCartClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showClearCartDialog = true
                }
            )
        },
        bottomBar = {
            // Show summary card at bottom on mobile
            if (uiState.items.isNotEmpty()) {
                CartSummaryCard(
                    subtotal = uiState.subtotal,
                    savings = uiState.savings,
                    deliveryFee = uiState.deliveryFee,
                    total = uiState.total,
                    itemCount = uiState.items.size,
                    onCheckoutClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.proceedToCheckout()
                    },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.padding(SpacingTokens.L)
                )
            }
        }
    ) { paddingValues ->
        when {
            // Loading state
            uiState.isLoading && uiState.items.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator(size = 60.dp)
                }
            }

            // Empty cart
            uiState.items.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyCartState(
                        onStartShopping = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            navController.navigate(Screen.Search.route)
                        }
                    )
                }
            }

            // Cart items
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(
                        start = SpacingTokens.L,
                        end = SpacingTokens.L,
                        top = SpacingTokens.L,
                        bottom = 200.dp // Space for summary card
                    ),
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
                ) {
                    // Store selector (if multiple stores available)
                    if (uiState.availableStores.size > 1) {
                        item {
                            Column {
                                SectionHeader(
                                    title = "בחר חנות",
                                    subtitle = "השווה מחירים בין חנויות"
                                )

                                Spacer(modifier = Modifier.height(SpacingTokens.S))

                                StoreSelector(
                                    stores = uiState.availableStores.map { store ->
                                        StoreOption(
                                            id = store.id,
                                            name = store.name,
                                            address = store.address,
                                            totalPrice = store.totalPrice,
                                            savings = store.savings
                                        )
                                    },
                                    selectedStoreId = uiState.selectedStoreId,
                                    onStoreSelected = { storeId ->
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.selectStore(storeId)
                                    }
                                )
                            }
                        }

                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = SpacingTokens.M)
                            )
                        }
                    }

                    // Cart items header
                    item {
                        SectionHeader(
                            title = "המוצרים שלך",
                            subtitle = "${uiState.items.size} מוצרים"
                        )
                    }

                    // Cart items
                    items(
                        items = uiState.items,
                        key = { it.id }
                    ) { item ->
                        CartItemCard(
                            item = item,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateQuantity(item.id, newQuantity)
                            },
                            onRemove = {
                                viewModel.removeItem(item.id)
                            },
                            onProductClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                navController.navigate(
                                    Screen.ProductDetail.createRoute(item.productId)
                                )
                            }
                        )
                    }

                    // Recommended products
                    if (uiState.recommendedProducts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(SpacingTokens.L))
                            SectionHeader(
                                title = "מומלץ עבורך",
                                subtitle = "על סמך העגלה שלך"
                            )
                        }

                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
                                contentPadding = PaddingValues(horizontal = SpacingTokens.L)
                            ) {
                                items(uiState.recommendedProducts) { product ->
                                    CompactProductCard(
                                        product = product,
                                        onProductClick = {
                                            navController.navigate(
                                                Screen.ProductDetail.createRoute(product.itemCode)
                                            )
                                        },
                                        onAddToCart = {
                                            viewModel.addRecommendedProduct(product)
                                        },
                                        modifier = Modifier.width(180.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (showClearCartDialog) {
            ChampionCartAlertDialog(
                title = "נקה עגלה",
                text = "האם אתה בטוח שברצונך להסיר את כל המוצרים מהעגלה?",
                confirmButtonText = "נקה",
                dismissButtonText = "ביטול",
                onConfirm = {
                    viewModel.clearCart()
                    showClearCartDialog = false
                },
                onDismiss = { showClearCartDialog = false },
                confirmButtonColor = MaterialTheme.colorScheme.error
            )
        }

        // Success message for checkout
        if (uiState.showCheckoutSuccess) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                viewModel.dismissCheckoutSuccess()
                // Navigate to checkout screen
            }

            Snackbar(
                modifier = Modifier.padding(SpacingTokens.L),
                containerColor = MaterialTheme.colorScheme.extended.electricMint,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("מעבר לתשלום...")
            }
        }

        // Error handling
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Show error snackbar
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartTopBar(
    itemCount: Int,
    onBackClick: () -> Unit,
    onClearCartClick: () -> Unit
) {
    ChampionCartTopBar(
        title = "העגלה שלי",
        showBackButton = true,
        onBackClick = onBackClick,
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
        }
    )
}