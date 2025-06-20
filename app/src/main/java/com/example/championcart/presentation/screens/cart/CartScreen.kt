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
                    total = uiState.total,
                    itemCount = uiState.items.size,
                    onCheckout = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.proceedToCheckout()
                    },
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
                            navController.navigate(Screen.Search.route)
                        }
                    )
                }
            }

            // Cart with items
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(SpacingTokens.L),
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
                ) {
                    // Store selector section
                    if (uiState.availableStores.isNotEmpty()) {
                        item {
                            Column {
                                SectionHeader(
                                    title = "בחר חנות",
                                    subtitle = "השווה מחירים בין החנויות"
                                )

                                Spacer(modifier = Modifier.height(SpacingTokens.M))

                                StoreSelector(
                                    stores = uiState.availableStores,
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

                    // Recommended products section
                    if (uiState.recommendedProducts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(SpacingTokens.L))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(SpacingTokens.L))
                        }

                        item {
                            Column {
                                SectionHeader(
                                    title = "מומלצים עבורך",
                                    subtitle = "על סמך העגלה שלך"
                                )

                                Spacer(modifier = Modifier.height(SpacingTokens.M))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
                                    contentPadding = PaddingValues(horizontal = SpacingTokens.S)
                                ) {
                                    items(
                                        items = uiState.recommendedProducts,
                                        key = { it.itemCode }
                                    ) { product ->
                                        CompactProductCard(
                                            product = product,
                                            onProductClick = {
                                                navController.navigate(
                                                    Screen.ProductDetail.createRoute(product.itemCode)
                                                )
                                            },
                                            onAddToCart = { _ ->
                                                viewModel.addRecommendedProduct(product)
                                            },
                                            modifier = Modifier.width(160.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom spacing for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Dialogs
    if (showClearCartDialog) {
        ChampionCartAlertDialog(
            title = "נקה עגלה",
            text = "האם אתה בטוח שברצונך לנקות את כל העגלה?",
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

    // Checkout success dialog
    if (uiState.showCheckoutSuccess) {
        ChampionCartAlertDialog(
            title = "ההזמנה נשלחה!",
            text = "ההזמנה שלך התקבלה בהצלחה",
            confirmButtonText = "אישור",
            onConfirm = {
                viewModel.dismissCheckoutSuccess()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Cart.route) { inclusive = true }
                }
            },
            onDismiss = viewModel::dismissCheckoutSuccess,
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.extended.electricMint,
                    modifier = Modifier.size(48.dp)
                )
            }
        )
    }

    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
        }
    }
}