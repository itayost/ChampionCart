package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.cart.*
import com.example.championcart.presentation.components.common.ChampionCartTopBar
import com.example.championcart.presentation.components.common.ConfirmationDialog
import com.example.championcart.presentation.components.common.ChampionSnackbar
import com.example.championcart.presentation.components.common.EmptyCartState
import com.example.championcart.presentation.components.common.GlassCard
import com.example.championcart.presentation.components.common.LoadingIndicator
import com.example.championcart.presentation.components.common.TopBarAction
import com.example.championcart.presentation.screens.cart.components.SaveCartDialog
import com.example.championcart.ui.theme.Padding
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Size
import com.example.championcart.ui.theme.Spacing
import android.content.Context
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Login
import androidx.compose.ui.platform.LocalContext
import com.example.championcart.ui.theme.BrandColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToStore: (String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isGuest = !viewModel.isLoggedIn()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val hapticFeedback = LocalHapticFeedback.current

    val selectedCity = remember {
        context.getSharedPreferences("champion_cart_prefs", Context.MODE_PRIVATE)
            .getString("selected_city", "תל אביב") ?: "תל אביב"
    }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    var showCheapestStoreSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
            )
            viewModel.clearMessage()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        ChampionSnackbar(snackbarData = data)
                    }
                )
            },
            topBar = {
                ChampionCartTopBar(
                    title = "העגלה שלי",
                    subtitle = if (uiState.cartItems.isNotEmpty()) {
                        "${uiState.cartItems.sumOf { it.quantity }} פריטים"
                    } else null,
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "חזור"
                            )
                        }
                    },
                    actions = listOf(
                        TopBarAction(
                            icon = Icons.Rounded.Save,
                            contentDescription = "שמור עגלה",
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                // Validate before showing dialog
                                if (viewModel.canSaveCart()) {
                                    showSaveDialog = true
                                }
                            }
                        ),
                        TopBarAction(
                            icon = Icons.Rounded.Delete,
                            contentDescription = "נקה עגלה",
                            onClick = {
                                if (uiState.cartItems.isNotEmpty()) {
                                    showClearConfirmation = true
                                }
                            },
                            tint = if (uiState.cartItems.isNotEmpty()) SemanticColors.Error else null
                        )
                    )
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = uiState.cartItems.isNotEmpty(),
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it },
                    modifier = Modifier.padding(bottom = Size.bottomNavHeight)
                ) {
                    if (isGuest) {
                        // Show login prompt FAB for guests
                        ExtendedFloatingActionButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToLogin()
                            },
                            containerColor = BrandColors.ElectricMint,
                            contentColor = Color.White,
                            modifier = Modifier.animateContentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Login,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "התחבר לחיסכון",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // Show regular FAB for logged-in users
                        ExtendedFloatingActionButton(
                            onClick = {
                                if (!uiState.isCalculating) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showCheapestStoreSheet = true
                                    viewModel.calculateCheapestStore()
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.animateContentSize()
                        ) {
                            if (uiState.isCalculating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "מחשב...",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.LocalOffer,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "מצא חנות זולה",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.surface,
                                colorScheme.surface.copy(alpha = 0.95f)
                            )
                        )
                    )
            ) {
                if (uiState.isLoading && uiState.cartItems.isEmpty()) {
                    // Show skeleton loaders while loading
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = Spacing.m,
                            bottom = Size.bottomNavHeight + Spacing.xl
                        ),
                        verticalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        // Summary skeleton
                        item {
                            GlassCard(
                                modifier = Modifier.padding(horizontal = Spacing.l)
                            ) {
                                CartSummarySkeleton(
                                    modifier = Modifier.padding(Padding.l)
                                )
                            }
                        }

                        // Cart item skeletons
                        items(3) {
                            CartItemSkeleton(
                                modifier = Modifier.padding(horizontal = Spacing.l)
                            )
                        }
                    }
                } else if (uiState.cartItems.isEmpty()) {
                    EmptyCartState(
                        onStartShopping = onNavigateToSearch,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(
                            top = Spacing.m,
                            bottom = Size.bottomNavHeight + Spacing.xl
                        ),
                        verticalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        // Summary Card
                        item {
                            GlassCard(
                                modifier = Modifier.padding(horizontal = Spacing.l)
                            ) {
                                CartSummaryContent(
                                    itemCount = uiState.cartItems.sumOf { it.quantity },
                                    totalPrice = uiState.totalPrice,
                                    potentialSavings = uiState.potentialSavings ?: 0.0,
                                    isGuest = isGuest,
                                    onLoginClick = {
                                        // Navigate to login screen
                                        // You'll need to add this navigation parameter to CartScreen
                                        onNavigateToLogin()
                                    },
                                    modifier = Modifier.padding(Padding.l)
                                )
                            }
                        }

                        // Cart Items
                        items(
                            items = uiState.cartItems,
                            key = { it.product.id }
                        ) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onQuantityChange = { newQuantity ->
                                    viewModel.updateQuantity(cartItem.product.id, newQuantity)
                                },
                                onRemove = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.removeFromCart(cartItem.product.id)
                                },
                                modifier = Modifier.padding(horizontal = Spacing.l)
                            )
                        }
                    }
                }

                // Loading overlay
                if (uiState.isCalculating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(enabled = false) { },
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 8.dp
                            )
                        ) {
                            LoadingIndicator(
                                size = 64.dp,
                                strokeWidth = 6.dp,
                                modifier = Modifier.padding(Spacing.xl)
                            )
                        }
                    }
                }
            }
        }

        // Save Cart Dialog
        SaveCartDialog(
            visible = showSaveDialog,
            onDismiss = { showSaveDialog = false },
            onSave = { cartName ->
                viewModel.saveCart(cartName)
                showSaveDialog = false  // Close dialog after initiating save
            },
            isLoading = uiState.isSaving
        )

        // Clear Cart Confirmation
        ConfirmationDialog(
            visible = showClearConfirmation,
            onConfirm = {
                viewModel.clearCart()
            },
            onDismiss = { showClearConfirmation = false },
            title = "נקה עגלה?",
            text = "האם אתה בטוח שברצונך לנקות את כל הפריטים בעגלה?",
            confirmText = "נקה",
            dismissText = "ביטול",
            isDangerous = true
        )

        // Cheapest Store Bottom Sheet
        CheapestStoreBottomSheet(
            visible = showCheapestStoreSheet,
            result = uiState.cheapestStoreResult,
            isCalculating = uiState.isCalculating,
            cartTotal = uiState.totalPrice,
            onDismiss = { showCheapestStoreSheet = false },
            onNavigateToStore = { storeName ->
                // Get the address from the result
                val address = uiState.cheapestStoreResult?.address ?: "$storeName, $selectedCity"
                onNavigateToStore(storeName, address)
            },
            onRecalculate = {
                viewModel.calculateCheapestStore()
            },
            storeDetails = uiState.storeComparisonData
        )
    }
}