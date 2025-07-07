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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
            CartTopBar(
                itemCount = uiState.cartItems.sumOf { it.quantity },
                onNavigateBack = onNavigateBack,
                onClearCart = { showClearConfirmation = true },
                onSaveCart = { showSaveDialog = true }
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
                    state = listState,
                    contentPadding = PaddingValues(
                        top = Spacing.m,
                        bottom = Spacing.xl
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s)
                ) {
                    // Summary Card
                    item {
                        CartSummaryCard(
                            itemCount = uiState.cartItems.sumOf { it.quantity },
                            totalPrice = uiState.totalPrice,
                            potentialSavings = uiState.potentialSavings,
                            isCalculating = uiState.isCalculating,
                            modifier = Modifier.padding(horizontal = Spacing.l)
                        )
                    }

                    // Store Recommendations
                    uiState.cheapestStoreResult?.let { result ->
                        item {
                            StoreRecommendations(
                                result = result,
                                onStoreClick = onNavigateToStore,
                                modifier = Modifier.padding(top = Spacing.m)
                            )
                        }
                    }

                    // Cart Items
                    item {
                        SectionHeader(
                            title = "המוצרים שלך",
                            modifier = Modifier.padding(
                                horizontal = Spacing.l,
                                vertical = Spacing.m
                            )
                        )
                    }

                    itemsIndexed(
                        items = uiState.cartItems,
                        key = { _, item -> item.product.id }
                    ) { index, cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onUpdateQuantity = { quantity ->
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.updateQuantity(cartItem.product.id, quantity)
                            },
                            onRemove = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.removeFromCart(cartItem.product.id)
                            },
                            animationDelay = index * 50
                        )
                    }
                }
            }
        }
    }

    // Save Cart Dialog
    if (showSaveDialog) {
        SaveCartDialog(
            onDismiss = { showSaveDialog = false },
            onSave = { name ->
                viewModel.saveCart(name)
                showSaveDialog = false
            }
        )
    }

    // Clear Cart Confirmation
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("נקה עגלה?") },
            text = { Text("האם אתה בטוח שברצונך למחוק את כל המוצרים מהעגלה?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCart()
                        showClearConfirmation = false
                    }
                ) {
                    Text("מחק", color = SemanticColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("ביטול")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartTopBar(
    itemCount: Int,
    onNavigateBack: () -> Unit,
    onClearCart: () -> Unit,
    onSaveCart: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Text("העגלה שלי")
                AnimatedContent(
                    targetState = itemCount,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn() togetherWith
                                slideOutVertically { it } + fadeOut()
                    }
                ) { count ->
                    if (count > 0) {
                        ChampionBadge(
                            count = count,
                            backgroundColor = BrandColors.ElectricMint
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "חזור"
                )
            }
        },
        actions = {
            if (itemCount > 0) {
                IconButton(onClick = onSaveCart) {
                    Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = "שמור עגלה"
                    )
                }
                IconButton(onClick = onClearCart) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteSweep,
                        contentDescription = "נקה עגלה",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    )
}

@Composable
private fun CartSummaryCard(
    itemCount: Int,
    totalPrice: Double,
    potentialSavings: Double?,
    isCalculating: Boolean,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "סיכום עגלה",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$itemCount פריטים",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    AnimatedContent(
                        targetState = totalPrice,
                        transitionSpec = {
                            slideInVertically { -it } + fadeIn() togetherWith
                                    slideOutVertically { it } + fadeOut()
                        }
                    ) { price ->
                        Text(
                            text = "₪${String.format("%.2f", price)}",
                            style = TextStyles.priceLarge,
                            color = BrandColors.ElectricMint
                        )
                    }
                    Text(
                        text = "מחיר משוער",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            potentialSavings?.let { savings ->
                if (savings > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = SemanticColors.Success.copy(alpha = 0.1f),
                                shape = Shapes.cardSmall
                            )
                            .padding(horizontal = Spacing.m, vertical = Spacing.s),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Savings,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = SemanticColors.Success
                            )
                            Text(
                                text = "חיסכון פוטנציאלי",
                                style = MaterialTheme.typography.bodySmall,
                                color = SemanticColors.Success
                            )
                        }
                        Text(
                            text = "₪${String.format("%.2f", savings)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SemanticColors.Success
                        )
                    }
                }
            }

            if (isCalculating) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = BrandColors.ElectricMint,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit,
    animationDelay: Int = 0
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally { -it / 2 }
    ) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value == SwipeToDismissBoxValue.EndToStart) {
                    onRemove()
                    true
                } else {
                    false
                }
            }
        )

        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SemanticColors.Error)
                        .padding(horizontal = Spacing.l),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "מחק",
                        tint = Color.White
                    )
                }
            },
            enableDismissFromEndToStart = true,
            enableDismissFromStartToEnd = false
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.l),
                shape = Shapes.card,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Padding.m),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    // Product Image
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = Shapes.cardSmall,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ShoppingBag,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Product Details
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Text(
                            text = cartItem.product.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Store,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = cartItem.product.bestStore,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Price
                        AnimatedContent(
                            targetState = cartItem.product.bestPrice * cartItem.quantity,
                            transitionSpec = {
                                slideInVertically { -it } + fadeIn() togetherWith
                                        slideOutVertically { it } + fadeOut()
                            }
                        ) { totalPrice ->
                            Text(
                                text = "₪${String.format("%.2f", totalPrice)}",
                                style = TextStyles.price,
                                color = BrandColors.ElectricMint
                            )
                        }
                    }

                    // Quantity Selector
                    QuantitySelector(
                        quantity = cartItem.quantity,
                        onIncrease = { onUpdateQuantity(cartItem.quantity + 1) },
                        onDecrease = { onUpdateQuantity(cartItem.quantity - 1) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        IconButton(
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onDecrease()
            },
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = "הפחת",
                modifier = Modifier.size(16.dp)
            )
        }

        AnimatedContent(
            targetState = quantity,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { -it } + fadeIn() togetherWith
                            slideOutVertically { it } + fadeOut()
                } else {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                }
            }
        ) { count ->
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.widthIn(min = 24.dp),
                textAlign = TextAlign.Center
            )
        }

        IconButton(
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onIncrease()
            },
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(BrandColors.ElectricMint)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "הוסף",
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun StoreRecommendations(
    result: CheapestStoreResult,
    onStoreClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        SectionHeader(
            title = "החנויות הזולות ביותר",
            modifier = Modifier.padding(horizontal = Spacing.l)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.l),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            items(result.storeTotals.entries.sortedBy { it.value }.take(3)) { (store, price) ->
                StoreRecommendationCard(
                    storeName = store,
                    totalPrice = price,
                    isBest = store == result.cheapestStore,
                    missingItems = if (store == result.cheapestStore) 0 else result.missingItems.size,
                    onClick = { onStoreClick(store) }
                )
            }
        }
    }
}

@Composable
private fun StoreRecommendationCard(
    storeName: String,
    totalPrice: Double,
    isBest: Boolean,
    missingItems: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(200.dp),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = if (isBest) {
                BrandColors.ElectricMint.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isBest) {
            BorderStroke(2.dp, BrandColors.ElectricMint)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isBest) {
                    ChampionChip(
                        text = "הכי זול"
                    )
                }
            }

            Text(
                text = "₪${String.format("%.2f", totalPrice)}",
                style = TextStyles.priceLarge,
                color = if (isBest) BrandColors.ElectricMint else MaterialTheme.colorScheme.onSurface
            )

            if (missingItems > 0) {
                Text(
                    text = "$missingItems מוצרים חסרים",
                    style = MaterialTheme.typography.bodySmall,
                    color = SemanticColors.Warning
                )
            }

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBest) BrandColors.ElectricMint else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Navigation,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text("נווט לחנות")
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    totalPrice: Double,
    onContinueShopping: () -> Unit,
    onFindBestStore: () -> Unit,
    isCalculating: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "סה״כ משוער",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AnimatedContent(
                    targetState = totalPrice,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn() togetherWith
                                slideOutVertically { it } + fadeOut()
                    }
                ) { price ->
                    Text(
                        text = "₪${String.format("%.2f", price)}",
                        style = TextStyles.priceLarge,
                        color = BrandColors.ElectricMint
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                OutlinedButton(
                    onClick = onContinueShopping,
                    modifier = Modifier.weight(1f),
                    shape = Shapes.button
                ) {
                    Text("המשך קניות")
                }

                Button(
                    onClick = onFindBestStore,
                    modifier = Modifier.weight(1f),
                    shape = Shapes.button,
                    enabled = !isCalculating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandColors.ElectricMint
                    )
                ) {
                    if (isCalculating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text("מצא חנות זולה")
                    }
                }
            }
        }
    }
}

@Composable
private fun SaveCartDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var cartName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("שמור עגלה") },
        text = {
            Column {
                Text("תן שם לעגלה שלך כדי לשמור אותה לשימוש עתידי")
                Spacer(modifier = Modifier.height(Spacing.m))
                ChampionTextField(
                    value = cartName,
                    onValueChange = { cartName = it },
                    label = "שם העגלה",
                    placeholder = "לדוגמה: קניות שבועיות",
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(cartName) },
                enabled = cartName.isNotBlank()
            ) {
                Text("שמור")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ביטול")
            }
        }
    )
}