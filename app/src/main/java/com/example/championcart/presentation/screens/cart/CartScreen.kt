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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.domain.models.*
import com.example.championcart.ui.theme.*
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
                        modifier = Modifier.size(width = 48.dp, height = 4.dp)
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
                        tint = MaterialTheme.extendedColors.error
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
                        contentColor = MaterialTheme.extendedColors.error
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
    val savingsPercent = result.savingsPercent.toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onStoreSelect()
            },
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glass
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Best Deal Found!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.success
                    )
                    Text(
                        text = "Shop at ${result.bestStore.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Savings badge
                Surface(
                    shape = ComponentShapes.Badge,
                    color = MaterialTheme.extendedColors.success
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SAVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₪${String.format("%.2f", result.savingsAmount)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$savingsPercent%",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
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
                PriceColumn(
                    label = "Your Cart",
                    price = result.worstPrice,
                    isStrikethrough = true
                )
                PriceColumn(
                    label = "Best Price",
                    price = result.totalPrice,
                    isSuccess = true
                )
                PriceColumn(
                    label = "You Save",
                    price = result.savingsAmount,
                    isHighlight = true,
                    isSuccess = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Store comparison button
            OutlinedButton(
                onClick = onStoreSelect,
                modifier = Modifier.fillMaxWidth(),
                shape = ComponentShapes.Button,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.extendedColors.success
                )
            ) {
                Text(
                    text = "Compare All Stores",
                    color = MaterialTheme.extendedColors.success
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.success
                )
            }
        }
    }
}

@Composable
fun PriceColumn(
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
                isSuccess -> MaterialTheme.extendedColors.success
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
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Product details
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                    Text(
                        text = " = ₪${String.format("%.2f", item.price * item.quantity)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quantity controls
            QuantityControls(
                quantity = item.quantity,
                onQuantityChange = { newQuantity ->
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (newQuantity <= 0) {
                        showRemoveDialog = true
                    } else {
                        onQuantityChange(item.id, newQuantity)
                    }
                }
            )
        }
    }

    // Remove confirmation dialog
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
                        contentColor = MaterialTheme.extendedColors.error
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
fun QuantityControls(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Decrease button
        IconButton(
            onClick = { onQuantityChange(quantity - 1) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease quantity",
                tint = MaterialTheme.extendedColors.electricMint
            )
        }

        // Quantity display
        Surface(
            shape = ComponentShapes.Chip,
            color = MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
        ) {
            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.electricMint
            )
        }

        // Increase button
        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase quantity",
                tint = MaterialTheme.extendedColors.electricMint
            )
        }
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
    selectedStore: Store?,
    onFindCheapest: () -> Unit,
    onCheckout: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Total price display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₪${String.format("%.2f", totalPrice)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.electricMint
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action button
            if (selectedStore == null) {
                // Find cheapest button
                Button(
                    onClick = onFindCheapest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = ComponentShapes.Button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.electricMint
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
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
                        containerColor = MaterialTheme.extendedColors.success
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Shop at ${selectedStore.name}",
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
            )
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
    onStoreSelected: (Store) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Choose Your Store",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Compare total prices across all stores",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Best deal indicator
        Surface(
            shape = ComponentShapes.Card,
            color = MaterialTheme.extendedColors.success.copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.success
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Best deal: Save ₪${String.format("%.2f", cheapestResult.savingsAmount)} (${cheapestResult.savingsPercent.toInt()}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.success,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Store options
        cheapestResult.allStores.forEach { storeOption ->
            val store = storeOption.toStore()
            val isBestDeal = storeOption.totalPrice == cheapestResult.totalPrice
            val isSelected = selectedStoreId == store.id

            StoreOptionCard(
                store = store,
                totalPrice = storeOption.totalPrice,
                isBestDeal = isBestDeal,
                isSelected = isSelected,
                onClick = { onStoreSelected(store) }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun StoreOptionCard(
    store: Store,
    totalPrice: Double,
    isBestDeal: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
                isBestDeal -> MaterialTheme.extendedColors.success.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.extendedColors.electricMint)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isBestDeal) {
                    Text(
                        text = "Best Price",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.success,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = "₪${String.format("%.2f", totalPrice)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isBestDeal) {
                    MaterialTheme.extendedColors.success
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
                // Animated celebration icon
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer {
                            scaleX = 1.2f
                            scaleY = 1.2f
                        },
                    tint = MaterialTheme.extendedColors.success
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
                    color = MaterialTheme.extendedColors.success
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Great job, Champion!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Helper function to get emoji for products
private fun getProductEmoji(productName: String): String {
    return when {
        productName.contains("חלב", ignoreCase = true) -> "🥛"
        productName.contains("לחם", ignoreCase = true) -> "🍞"
        productName.contains("ביצים", ignoreCase = true) -> "🥚"
        productName.contains("במבה", ignoreCase = true) -> "🥜"
        productName.contains("יוגורט", ignoreCase = true) -> "🥛"
        productName.contains("גבינה", ignoreCase = true) -> "🧀"
        productName.contains("עגבניות", ignoreCase = true) -> "🍅"
        productName.contains("מלפפון", ignoreCase = true) -> "🥒"
        productName.contains("בננה", ignoreCase = true) -> "🍌"
        productName.contains("תפוח", ignoreCase = true) -> "🍎"
        else -> "🛒"
    }
}