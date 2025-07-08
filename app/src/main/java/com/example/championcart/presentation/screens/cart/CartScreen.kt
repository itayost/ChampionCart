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
            TopAppBar(
                title = {
                    Column {
                        Text("העגלה שלי")
                        if (uiState.cartItems.isNotEmpty()) {
                            Text(
                                text = "${uiState.cartItems.sumOf { it.quantity }} פריטים",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    BackButton(onClick = onNavigateBack)
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        IconButton(onClick = { showSaveDialog = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Save,
                                contentDescription = "שמור עגלה"
                            )
                        }
                        IconButton(onClick = { showClearConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "נקה עגלה"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.cartItems.isNotEmpty(),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                CartBottomBar(
                    totalPrice = uiState.totalPrice,
                    onContinueShopping = onNavigateToSearch,
                    onFindBestStore = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.calculateCheapestStore()
                    },
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
                        bottom = Size.bottomNavHeight + Spacing.xl  // Account for both bottom bar and nav bar
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
                                isCalculating = uiState.isCalculating
                            )
                        }
                    }

                    // Store Recommendations
                    uiState.cheapestStoreResult?.let { result ->
                        item {
                            StoreRecommendationCard(
                                result = result,
                                totalPrice = uiState.totalPrice,
                                onStoreClick = onNavigateToStore,
                                modifier = Modifier
                                    .padding(horizontal = Spacing.l)
                                    .padding(top = Spacing.m)
                            )
                        }
                    }

                    // Cart Items
                    item {
                        SectionHeader(
                            title = "המוצרים שלך"
                        )
                    }

                    items(
                        items = uiState.cartItems,
                        key = { item -> item.product.id }
                    ) { cartItem ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            CartItemCard(
                                cartItem = cartItem,
                                onQuantityChange = { quantity ->
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.updateQuantity(cartItem.product.id, quantity)
                                },
                                onRemove = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.removeFromCart(cartItem.product.id)
                                },
                                modifier = Modifier.padding(horizontal = Spacing.l)
                            )
                        }
                    }

                    // Extra space at the bottom for smoother scrolling
                    item {
                        Spacer(modifier = Modifier.height(Spacing.xl))
                    }
                }
            }
        }

        // Loading overlay
        if (uiState.isCalculating) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {}

                Card(
                    shape = Shapes.card,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(Padding.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = BrandColors.ElectricMint
                        )
                        Spacer(modifier = Modifier.height(Spacing.l))
                        Text(
                            text = "מחשב את החנות הזולה ביותר...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showClearConfirmation) {
        ConfirmationDialog(
            visible = true,
            title = "ניקוי העגלה",
            text = "האם אתה בטוח שברצונך לנקות את כל המוצרים מהעגלה?",
            confirmText = "נקה",
            onConfirm = {
                viewModel.clearCart()
                showClearConfirmation = false
            },
            onDismiss = { showClearConfirmation = false },
            isDangerous = true
        )
    }

    if (showSaveDialog) {
        SaveCartDialog(
            visible = true,
            onSave = { name ->
                viewModel.saveCart(name)
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }
}



@Composable
private fun CartBottomBar(
    totalPrice: Double,
    onContinueShopping: () -> Unit,
    onFindBestStore: () -> Unit,
    isCalculating: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.l)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "סה״כ משוער",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₪${String.format("%.2f", totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                PrimaryButton(
                    text = "מצא חנות זולה",
                    onClick = onFindBestStore,
                    enabled = !isCalculating,
                    isLoading = isCalculating,
                    icon = Icons.Rounded.Store
                )
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            SecondaryButton(
                text = "המשך קניות",
                onClick = onContinueShopping,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.AddShoppingCart
            )
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.card
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
                    onClick = {
                        if (cartItem.quantity > 1) {
                            onQuantityChange(cartItem.quantity - 1)
                        } else {
                            onRemove()
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (cartItem.quantity == 1)
                            Icons.Rounded.Delete else Icons.Rounded.Remove,
                        contentDescription = "הקטן כמות",
                        tint = if (cartItem.quantity == 1)
                            SemanticColors.Error else MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${cartItem.quantity}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = { onQuantityChange(cartItem.quantity + 1) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "הגדל כמות"
                    )
                }
            }
        }
    }
}

@Composable
private fun CartSummaryContent(
    itemCount: Int,
    totalPrice: Double,
    potentialSavings: Double,
    isCalculating: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Padding.l)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                value = itemCount.toString(),
                label = "מוצרים",
                icon = Icons.Rounded.ShoppingCart,
                color = BrandColors.ElectricMint
            )

            StatCard(
                value = "₪${String.format("%.2f", totalPrice)}",
                label = "סה״כ משוער",
                icon = Icons.Rounded.AttachMoney,
                color = MaterialTheme.colorScheme.primary
            )

            if (potentialSavings > 0 && !isCalculating) {
                StatCard(
                    value = "₪${String.format("%.2f", potentialSavings)}",
                    label = "חיסכון אפשרי",
                    icon = Icons.Rounded.Savings,
                    color = PriceColors.Best
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StoreRecommendationCard(
    result: CheapestStoreResult,
    totalPrice: Double,
    onStoreClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        onClick = { onStoreClick(result.cheapestStore) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.l)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "החנות הזולה ביותר",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = result.cheapestStore,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₪${String.format("%.2f", result.totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = PriceColors.Best
                    )
                    val savings = (totalPrice - result.totalPrice).coerceAtLeast(0.0)
                    if (savings > 0) {
                        Text(
                            text = "חיסכון של ₪${String.format("%.2f", savings)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PriceColors.Best
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                SecondaryButton(
                    text = "פרטי החנות",
                    onClick = { onStoreClick(result.cheapestStore) },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.Info
                )

                PrimaryButton(
                    text = "נווט לחנות",
                    onClick = { /* TODO: Open in maps */ },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.Navigation
                )
            }
        }
    }
}

@Composable
private fun SaveCartDialog(
    visible: Boolean,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var cartName by remember { mutableStateOf("") }

    ChampionDialog(
        visible = visible,
        onDismiss = onDismiss,
        title = "שמור עגלה",
        icon = Icons.Rounded.Save,
        confirmButton = {
            PrimaryButton(
                text = "שמור",
                onClick = { onSave(cartName) },
                enabled = cartName.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(
                text = "ביטול",
                onClick = onDismiss
            )
        }
    )

    ChampionTextField(
        value = cartName,
        onValueChange = { cartName = it },
        label = "שם העגלה",
        placeholder = "לדוגמה: קניות שבועיות",
        leadingIcon = Icons.Rounded.ShoppingCart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.m)
    )
}