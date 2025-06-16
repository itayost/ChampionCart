package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.CheapestCartResult
import com.example.championcart.presentation.components.EmptyState
import com.example.championcart.presentation.components.LoadingDialog
import com.example.championcart.presentation.utils.shimmerEffect
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    viewModel: CartViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Bottom sheet state for store selection
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    var showStoreSelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CartTopBar(
                itemCount = state.cartItems.size,
                onNavigateBack = onNavigateBack,
                onClearCart = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.clearCart()
                }
            )
        },
        bottomBar = {
            if (state.cartItems.isNotEmpty()) {
                CartBottomBar(
                    totalPrice = state.totalPrice,
                    selectedStore = state.selectedStore,
                    onFindCheapest = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.findCheapestStore()
                    },
                    onCheckout = onNavigateToCheckout,
                    isLoading = state.isLoading
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.cartItems.isEmpty() -> {
                    EmptyCartState(
                        onStartShopping = onNavigateToSearch
                    )
                }
                else -> {
                    CartContent(
                        cartItems = state.cartItems,
                        cheapestResult = state.cheapestCartResult,
                        onQuantityChange = { itemId, quantity ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.updateQuantity(itemId, quantity)
                        },
                        onRemoveItem = { itemId ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.removeItem(itemId)
                        },
                        onStoreSelect = {
                            showStoreSelector = true
                        },
                        isLoading = state.isLoading
                    )
                }
            }

            // Success animation overlay
            AnimatedVisibility(
                visible = state.showSavingsAnimation,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                SavingsAnimationOverlay(
                    savings = state.cheapestCartResult?.savingsAmount ?: 0.0
                )
            }
        }
    }

    // Store selector bottom sheet
    if (showStoreSelector && state.cheapestCartResult != null) {
        ModalBottomSheet(
            onDismissRequest = { showStoreSelector = false },
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.extendedColors.glassFrosted,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 4.dp)
                    )
                }
            }
        ) {
            StoreSelectionContent(
                cheapestResult = state.cheapestCartResult!!,
                selectedStoreId = state.selectedStore?.id,
                onStoreSelected = { store ->
                    viewModel.selectStore(store)
                    showStoreSelector = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar(
    itemCount: Int,
    onNavigateBack: () -> Unit,
    onClearCart: () -> Unit
) {
    var showClearDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Shopping Cart",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (itemCount > 0) {
                    Text(
                        text = "$itemCount ${if (itemCount == 1) "item" else "items"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (itemCount > 0) {
                IconButton(onClick = { showClearDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear cart",
                        tint = MaterialTheme.extendedColors.errorRed
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )

    // Clear cart confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Cart?") },
            text = { Text("Are you sure you want to remove all items from your cart?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearCart()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.extendedColors.errorRed
                    )
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CartContent(
    cartItems: List<CartItem>,
    cheapestResult: CheapestCartResult?,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onStoreSelect: () -> Unit,
    isLoading: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cheapest store result card
        cheapestResult?.let { result ->
            item {
                CheapestStoreCard(
                    result = result,
                    onStoreSelect = onStoreSelect
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Cart items
        items(
            items = cartItems,
            key = { it.id }
        ) { item ->
            CartItemCard(
                item = item,
                onQuantityChange = onQuantityChange,
                onRemove = onRemoveItem
            )
        }

        // Add more items prompt
        item {
            AddMoreItemsCard(
                onAddMore = { /* Navigate to search */ }
            )
        }
    }
}

@Composable
fun CheapestStoreCard(
    result: CheapestCartResult,
    onStoreSelect: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val savingsPercent = (result.savingsPercentage * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onStoreSelect()
            },
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.extendedColors.successGreen,
                    MaterialTheme.extendedColors.electricMint
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.extendedColors.successGreen.copy(alpha = 0.1f),
                            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎉 Best Deal Found!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.bestStore.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (result.bestStore.chain.lowercase()) {
                                "shufersal" -> MaterialTheme.extendedColors.shufersal
                                "victory" -> MaterialTheme.extendedColors.victory
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        Text(
                            text = result.bestStore.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Savings badge
                    Box(
                        modifier = Modifier
                            .clip(ComponentShapes.Badge)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.extendedColors.successGreen,
                                        MaterialTheme.extendedColors.electricMint
                                    )
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "SAVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$savingsPercent%",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price comparison
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PriceComparisonItem(
                        label = "Your Total",
                        price = result.totalPrice,
                        isHighlight = true
                    )
                    PriceComparisonItem(
                        label = "Most Expensive",
                        price = result.totalPrice + result.savingsAmount,
                        isStrikethrough = true
                    )
                    PriceComparisonItem(
                        label = "You Save",
                        price = result.savingsAmount,
                        isSuccess = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Change store button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tap to see all store options",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.electricMint
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.electricMint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PriceComparisonItem(
    label: String,
    price: Double,
    isHighlight: Boolean = false,
    isStrikethrough: Boolean = false,
    isSuccess: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "₪${String.format("%.2f", price)}",
            style = MaterialTheme.typography.titleMedium.copy(
                textDecoration = if (isStrikethrough) TextDecoration.LineThrough else null
            ),
            fontWeight = if (isHighlight || isSuccess) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isSuccess -> MaterialTheme.extendedColors.successGreen
                isStrikethrough -> MaterialTheme.colorScheme.onSurfaceVariant
                isHighlight -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var showRemoveDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product emoji placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getProductEmoji(item.productName),
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Product info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₪${String.format("%.2f", item.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.electricMint
                    )
                    Text(
                        text = " × ${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "= ₪${String.format("%.2f", item.price * item.quantity)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quantity controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Decrease button
                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (item.quantity > 1) {
                            onQuantityChange(item.id, item.quantity - 1)
                        } else {
                            showRemoveDialog = true
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Quantity display
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(36.dp)
                        .clip(ComponentShapes.Badge)
                        .background(MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.electricMint
                    )
                }

                // Increase button
                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onQuantityChange(item.id, item.quantity + 1)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.extendedColors.electricMint)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Remove item dialog
    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Remove Item?") },
            text = { Text("Remove ${item.productName} from your cart?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemove(item.id)
                        showRemoveDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.extendedColors.errorRed
                    )
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddMoreItemsCard(
    onAddMore: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddMore() },
        shape = ComponentShapes.Card,
        border = BorderStroke(
            2.dp,
            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.electricMint
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add More Items",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.extendedColors.electricMint,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CartBottomBar(
    totalPrice: Double,
    selectedStore: com.example.championcart.domain.models.Store?,
    onFindCheapest: () -> Unit,
    onCheckout: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Total price row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "₪${String.format("%.2f", totalPrice)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.electricMint
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            if (selectedStore == null) {
                // Find cheapest button
                Button(
                    onClick = onFindCheapest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                    shape = ComponentShapes.Button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.electricMint
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
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
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Checkout button
                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = ComponentShapes.Button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.successGreen
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Shopping at ${selectedStore.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCartState(
    onStartShopping: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated empty cart icon
        val rotation by rememberInfiniteTransition(label = "rotation").animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotation"
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .rotate(rotation)
                .clip(CircleShape)
                .background(
                    MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.extendedColors.electricMint
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start adding items to compare\nprices across stores",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartShopping,
            shape = ComponentShapes.Button,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.extendedColors.electricMint
            )
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Shopping")
        }
    }
}

@Composable
fun StoreSelectionContent(
    cheapestResult: CheapestCartResult,
    selectedStoreId: String?,
    onStoreSelected: (com.example.championcart.domain.models.Store) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "Select Store",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Choose where you want to shop",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Best deal indicator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = ComponentShapes.Card,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.successGreen.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.successGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You save ₪${String.format("%.2f", cheapestResult.savingsAmount)} at ${cheapestResult.bestStore.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Store options
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cheapestResult.allStores ?: emptyList()) { store ->
                StoreOptionCard(
                    store = store,
                    isSelected = store.id == selectedStoreId,
                    isBestDeal = store.id == cheapestResult.bestStore.id,
                    totalPrice = cheapestResult.itemsBreakdown.sumOf { it.totalPrice },
                    onSelect = { onStoreSelected(store) }
                )
            }
        }
    }
}

@Composable
fun StoreOptionCard(
    store: com.example.championcart.domain.models.Store,
    isSelected: Boolean,
    isBestDeal: Boolean,
    totalPrice: Double,
    onSelect: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onSelect()
            },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.extendedColors.electricMint)
        } else if (isBestDeal) {
            BorderStroke(1.dp, MaterialTheme.extendedColors.successGreen)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (isBestDeal) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(ComponentShapes.Badge)
                                .background(MaterialTheme.extendedColors.successGreen)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "BEST DEAL",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = store.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "₪${String.format("%.2f", totalPrice)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isBestDeal) {
                    MaterialTheme.extendedColors.successGreen
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
fun SavingsAnimationOverlay(
    savings: Double
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = ComponentShapes.DialogSmall,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated checkmark or celebration icon
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer {
                            scaleX = 1.2f
                            scaleY = 1.2f
                        },
                    tint = MaterialTheme.extendedColors.successGreen
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "You're Saving!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "₪${String.format("%.2f", savings)}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.successGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Great job, Champion!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Helper function to get emoji for product
fun getProductEmoji(productName: String): String {
    return when {
        productName.contains("חלב", ignoreCase = true) -> "🥛"
        productName.contains("לחם", ignoreCase = true) -> "🍞"
        productName.contains("ביצים", ignoreCase = true) -> "🥚"
        productName.contains("במבה", ignoreCase = true) -> "🥜"
        productName.contains("קפה", ignoreCase = true) -> "☕"
        productName.contains("גבינה", ignoreCase = true) -> "🧀"
        productName.contains("יוגורט", ignoreCase = true) -> "🥛"
        productName.contains("עוף", ignoreCase = true) -> "🍗"
        productName.contains("בשר", ignoreCase = true) -> "🥩"
        productName.contains("דג", ignoreCase = true) -> "🐟"
        productName.contains("ירקות", ignoreCase = true) -> "🥬"
        productName.contains("פירות", ignoreCase = true) -> "🍎"
        else -> "📦"
    }
}