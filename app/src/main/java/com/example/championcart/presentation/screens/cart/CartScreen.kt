package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.domain.models.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToStore: (String) -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val hapticFeedback = LocalHapticFeedback.current

    var showSaveDialog by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

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
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "חזור"
                        )
                    }
                },
                actions = listOf(
                    TopBarAction(
                        icon = Icons.Rounded.Save,
                        contentDescription = "שמור עגלה",
                        onClick = { showSaveDialog = true }
                    ),
                    TopBarAction(
                        icon = Icons.Rounded.Delete,
                        contentDescription = "נקה עגלה",
                        onClick = { showClearConfirmation = true },
                        tint = if (uiState.cartItems.isNotEmpty()) SemanticColors.Error else null
                    )
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.cartItems.isNotEmpty() && !uiState.isCalculating,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                BottomActionBar(
                    totalPrice = uiState.totalPrice,
                    onFindCheapestStore = { viewModel.calculateCheapestStore() },
                    isCalculating = uiState.isCalculating
                )
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
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            if (uiState.cartItems.isEmpty()) {
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
                        bottom = if (uiState.cartItems.isNotEmpty() && !uiState.isCalculating) {
                            // Account for bottom action bar + nav bar + extra spacing
                            Size.bottomNavHeight + 80.dp + Spacing.xl
                        } else {
                            // Just nav bar when no bottom action bar
                            Size.bottomNavHeight + Spacing.xl
                        }
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
                            modifier = Modifier
                                .padding(horizontal = Spacing.l)

                        )
                    }

                    // Cheapest store result
                    uiState.cheapestStoreResult?.let { result ->
                        item {
                            CheapestStoreCard(
                                result = result,
                                onNavigateToStore = onNavigateToStore,
                                modifier = Modifier.padding(horizontal = Spacing.l)
                            )
                        }
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
                    LoadingIndicator(
                        size = 64.dp,
                        strokeWidth = 6.dp
                    )
                }
            }
        }
    }

    // Save Cart Dialog
    if (showSaveDialog) {
        SaveCartDialog(
            onDismiss = { showSaveDialog = false },
            onSave = { cartName ->
                viewModel.saveCart(cartName)
                showSaveDialog = false
            }
        )
    }

    // Clear Cart Confirmation
    if (showClearConfirmation) {
        ChampionDialog(
            visible = true,
            onDismiss = { showClearConfirmation = false },
            title = "נקה עגלה?",
            text = "האם אתה בטוח שברצונך לנקות את כל הפריטים בעגלה?",
            icon = Icons.Rounded.DeleteForever,
            confirmButton = {
                PrimaryButton(
                    text = "נקה",
                    onClick = {
                        viewModel.clearCart()
                        showClearConfirmation = false
                    }
                )
            },
            dismissButton = {
                SecondaryButton(
                    text = "ביטול",
                    onClick = { showClearConfirmation = false }
                )
            }
        )
    }
}

@Composable
private fun CartSummaryContent(
    itemCount: Int,
    totalPrice: Double,
    potentialSavings: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "סיכום עגלה",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            ChampionChip(
                text = "$itemCount פריטים",
                selected = true
            )
        }

        ChampionDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "סה״כ משוער",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "₪${String.format("%.2f", totalPrice)}",
                style = TextStyles.price,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (potentialSavings > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "חיסכון פוטנציאלי",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₪${String.format("%.2f", potentialSavings)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PriceColors.Best,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.m),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            // Product Image Placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(Shapes.cardSmall)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingBag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₪${cartItem.product.bestPrice}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandColors.ElectricMint,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = " • ${cartItem.product.bestStore}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                IconButton(
                    onClick = { onQuantityChange(cartItem.quantity - 1) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = "הפחת",
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = cartItem.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(32.dp)
                )

                IconButton(
                    onClick = { onQuantityChange(cartItem.quantity + 1) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BrandColors.ElectricMint)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "הוסף",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Remove button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "הסר",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CheapestStoreCard(
    result: CheapestStoreResult,
    onNavigateToStore: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Text(
            text = "החנות הזולה ביותר",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        StoreCard(
            storeName = "${result.cheapestStore} - ${result.address ?: ""}",
            totalPrice = "₪${String.format("%.2f", result.totalPrice)}",
            itemCount = result.availableItems ?: result.storeTotals.size,
            distance = null,
            onClick = { onNavigateToStore(result.cheapestStore) },
            isRecommended = true
        )

        if ((result.totalMissingItems ?: 0) > 0 || result.missingItems.isNotEmpty()) {
            InfoCard(
                message = "${result.totalMissingItems ?: result.missingItems.size} מוצרים לא נמצאו בחנות זו",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    totalPrice: Double,
    onFindCheapestStore: () -> Unit,
    isCalculating: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.l),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "סה״כ לתשלום",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₪${String.format("%.2f", totalPrice)}",
                    style = TextStyles.price,
                    fontWeight = FontWeight.Bold
                )
            }

            PrimaryButton(
                text = "מצא חנות זולה",
                onClick = onFindCheapestStore,
                isLoading = isCalculating,
                icon = Icons.Rounded.Search
            )
        }
    }
}

@Composable
private fun SaveCartDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var cartName by remember { mutableStateOf("") }

    ChampionBottomSheet(
        visible = true,
        onDismiss = onDismiss,
        title = "שמור עגלה"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            ChampionTextField(
                value = cartName,
                onValueChange = { cartName = it },
                label = "שם העגלה",
                placeholder = "לדוגמה: קניות שבועיות",
                leadingIcon = Icons.Rounded.ShoppingCart,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                SecondaryButton(
                    text = "ביטול",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = "שמור",
                    onClick = { onSave(cartName) },
                    enabled = cartName.isNotBlank(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}