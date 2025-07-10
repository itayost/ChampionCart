package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.presentation.components.common.ChampionBadge
import com.example.championcart.presentation.components.common.ChampionBottomSheet
import com.example.championcart.presentation.components.common.ChampionCartTopBar
import com.example.championcart.presentation.components.common.ChampionChip
import com.example.championcart.presentation.components.common.ChampionDialog
import com.example.championcart.presentation.components.common.ChampionDivider
import com.example.championcart.presentation.components.common.ChampionSnackbar
import com.example.championcart.presentation.components.common.ChampionTextField
import com.example.championcart.presentation.components.common.ConfirmationDialog
import com.example.championcart.presentation.components.common.EmptyCartState
import com.example.championcart.presentation.components.common.EmptyState
import com.example.championcart.presentation.components.common.GlassCard
import com.example.championcart.presentation.components.common.InfoCard
import com.example.championcart.presentation.components.common.LoadingIndicator
import com.example.championcart.presentation.components.common.PrimaryButton
import com.example.championcart.presentation.components.common.QuantitySelector
import com.example.championcart.presentation.components.common.SecondaryButton
import com.example.championcart.presentation.components.common.TopBarAction
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.Padding
import com.example.championcart.ui.theme.PriceColors
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Shapes
import com.example.championcart.ui.theme.Size
import com.example.championcart.ui.theme.Spacing
import com.example.championcart.ui.theme.TextStyles
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
    var showCheapestStoreSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
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
            floatingActionButton = {
                // Show FAB only when cart has items
                AnimatedVisibility(
                    visible = uiState.cartItems.isNotEmpty(),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (uiState.cheapestStoreResult != null) {
                                showCheapestStoreSheet = true
                            } else {
                                viewModel.calculateCheapestStore()
                                scope.launch {
                                    // Wait for calculation to complete
                                    delay(100)
                                    showCheapestStoreSheet = true
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(bottom = Size.bottomNavHeight)
                            .animateContentSize(),
                        containerColor = BrandColors.ElectricMint,
                        contentColor = Color.White,
                        shape = Shapes.button
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.isCalculating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.LocalOffer,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = if (uiState.isCalculating) "מחשב..." else "מצא חנות זולה",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
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

        // Cheapest Store Bottom Sheet
        CheapestStoreBottomSheet(
            visible = showCheapestStoreSheet,
            result = uiState.cheapestStoreResult,
            isCalculating = uiState.isCalculating,
            cartTotal = uiState.totalPrice,
            onDismiss = { showCheapestStoreSheet = false },
            onNavigateToStore = { storeName ->
                showCheapestStoreSheet = false
                onNavigateToStore(storeName)
            },
            onRecalculate = {
                viewModel.calculateCheapestStore()
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
        val hapticFeedback = LocalHapticFeedback.current
        var showDeleteConfirmation by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        // Swipe to delete state
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { dismissValue ->
                when (dismissValue) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        showDeleteConfirmation = true
                        false // Don't dismiss immediately, wait for confirmation
                    }
                    else -> false
                }
            }
        )

        SwipeToDismissBox(
            state = dismissState,
            modifier = modifier,
            backgroundContent = {
                // Animated delete background
                val animatedColor by animateColorAsState(
                    targetValue = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> SemanticColors.Error
                        else -> Color.Transparent
                    },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "deleteBackground"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    animatedColor.copy(alpha = 0.9f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = Spacing.xl),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "מחק",
                            tint = Color.White,
                            modifier = Modifier
                                .size(Size.iconLarge)
                                .scale(
                                    animateFloatAsState(
                                        targetValue = if (dismissState.progress > 0.5f) 1.2f else 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy
                                        ),
                                        label = "deleteIconScale"
                                    ).value
                                )
                        )
                    }
                }
            },
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = false
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Padding.m),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    // Enhanced Product Image with Badge
                    Box(
                        modifier = Modifier.size(80.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            shape = Shapes.card,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ShoppingBag,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Quantity Badge
                        if (cartItem.quantity > 1) {
                            ChampionBadge(
                                count = cartItem.quantity,
                                backgroundColor = BrandColors.ElectricMint,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-8).dp)
                            )
                        }
                    }

                    // Product Details with Enhanced Layout
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = Spacing.xs),
                        verticalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        // Product Name
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = cartItem.product.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Store count indicator
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
                                    text = "זמין ב-${cartItem.product.stores.size} חנויות",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Price Range with Animation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Price Display
                            Column {
                                AnimatedContent(
                                    targetState = cartItem.product.bestPrice * cartItem.quantity,
                                    transitionSpec = {
                                        fadeIn() + slideInVertically { it / 2 } togetherWith
                                                fadeOut() + slideOutVertically { -it / 2 }
                                    },
                                    label = "priceAnimation"
                                ) { totalPrice ->
                                    Text(
                                        text = "₪${String.format("%.2f", totalPrice)}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = PriceColors.Best
                                    )
                                }

                                // Unit price
                                Text(
                                    text = "₪${String.format("%.2f", cartItem.product.bestPrice)} ליחידה",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Quantity Selector
                            QuantitySelector(
                                quantity = cartItem.quantity,
                                onQuantityChange = { newQuantity ->
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onQuantityChange(newQuantity)
                                },
                                modifier = Modifier.scale(0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog using existing component
        ConfirmationDialog(
            visible = showDeleteConfirmation,
            onConfirm = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onRemove()
            },
            onDismiss = {
                showDeleteConfirmation = false
                scope.launch {
                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                }
            },
            title = "הסרת מוצר",
            text = "האם להסיר את ${cartItem.product.name} מהעגלה?",
            confirmText = "הסר",
            dismissText = "ביטול",
            isDangerous = true // This will show the warning icon
        )
    }

    @Composable
    private fun CheapestStoreBottomSheet(
        visible: Boolean,
        result: CheapestStoreResult?,
        isCalculating: Boolean,
        cartTotal: Double,
        onDismiss: () -> Unit,
        onNavigateToStore: (String) -> Unit,
        onRecalculate: () -> Unit
    ) {
        ChampionBottomSheet(
            visible = visible,
            onDismiss = onDismiss,
            title = "החנות הזולה ביותר"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.l)
                    .padding(bottom = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.l)
            ) {
                when {
                    isCalculating -> {
                        // Loading state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Spacing.m)
                            ) {
                                LoadingIndicator(size = 64.dp)
                                Text(
                                    text = "מחפש את המחירים הטובים ביותר...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    result != null -> {
                        // Result content
                        // Winner store card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                BrandColors.ElectricMint.copy(alpha = 0.1f),
                                                BrandColors.ElectricMint.copy(alpha = 0.05f)
                                            )
                                        )
                                    )
                                    .padding(Padding.l),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Total price
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "₪${String.format("%.2f", result.totalPrice)}",
                                        style = TextStyles.price,
                                        color = PriceColors.Best,
                                        fontWeight = FontWeight.Bold
                                    )
                                    // Savings amount
                                    val savings = cartTotal - result.totalPrice
                                    if (savings > 0) {
                                        Text(
                                            text = "חיסכון: ₪${String.format("%.2f", savings)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SemanticColors.Success
                                        )
                                    }
                                }
                                // Store details
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.m)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "חנות:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = result.cheapestStore,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    result.address?.let { address ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "כתובת:",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = address,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Store comparison
                        if (result.storeTotals.size > 1) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Spacing.s)
                            ) {
                                Text(
                                    text = "השוואת מחירים",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )

                                result.storeTotals.entries
                                    .sortedBy { it.value }
                                    .take(4) // Show top 4 stores
                                    .forEach { (store, price) ->
                                        StoreComparisonRow(
                                            storeName = store,
                                            price = price,
                                            isCheapest = store == result.cheapestStore,
                                            difference = price - result.totalPrice
                                        )
                                    }
                            }
                        }

                        // Missing items info
                        if (result.missingItems.isNotEmpty()) {
                            InfoCard(
                                message = "${result.missingItems.size} מוצרים לא נמצאו בחנות הזולה",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                        ) {
                            PrimaryButton(
                                text = "נווט לחנות",
                                onClick = { onNavigateToStore(result.cheapestStore) },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Rounded.Navigation
                            )
                        }
                    }

                    else -> {
                        // Empty state
                        EmptyState(
                            icon = Icons.Rounded.ShoppingCart,
                            title = "לא נמצאו תוצאות",
                            message = "נסה לחשב מחדש",
                            actionText = "סגור",
                            onAction = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun StoreComparisonRow(
        storeName: String,
        price: Double,
        isCheapest: Boolean,
        difference: Double,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(Shapes.cardSmall)
                .background(
                    if (isCheapest) {
                        BrandColors.ElectricMint.copy(alpha = 0.1f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                )
                .padding(Padding.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCheapest) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = BrandColors.ElectricMint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCheapest) FontWeight.Medium else FontWeight.Normal
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (difference > 0) {
                    Text(
                        text = "+₪${String.format("%.2f", difference)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "₪${String.format("%.2f", price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCheapest) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCheapest) PriceColors.Best else MaterialTheme.colorScheme.onSurface
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