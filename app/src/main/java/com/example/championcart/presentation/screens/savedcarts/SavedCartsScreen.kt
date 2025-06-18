package com.example.championcart.presentation.screens.savedcarts

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.models.Cart
import com.example.championcart.domain.models.SavedCart
import com.example.championcart.domain.models.SavedCartItem
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedCartsUiState(
    val savedCarts: List<Cart> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val expandedCartIndex: Int? = null
)

@HiltViewModel
class SavedCartsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedCartsUiState())
    val uiState: StateFlow<SavedCartsUiState> = _uiState.asStateFlow()

    init {
        loadSavedCarts()
    }

    private fun loadSavedCarts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.getSavedCarts().fold(
                onSuccess = { savedCarts ->
                    _uiState.value = _uiState.value.copy(
                        savedCarts = savedCarts,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to load saved carts",
                        isLoading = false
                    )
                }
            )
        }
    }

    fun toggleCartExpansion(index: Int) {
        _uiState.value = _uiState.value.copy(
            expandedCartIndex = if (_uiState.value.expandedCartIndex == index) null else index
        )
    }

    fun refresh() {
        loadSavedCarts()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCartsScreen(
    onNavigateBack: () -> Unit,
    onCartSelected: (SavedCart) -> Unit = {},
    viewModel: SavedCartsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            GlassmorphicTopAppBar(
                title = "Saved Carts",
                onNavigationClick = onNavigateBack,
                actions = {
                    GlassmorphicIconButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.refresh()
                        },
                        icon = Icons.Default.Refresh,
                        tint = MaterialTheme.colorScheme.extended.electricMint
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Floating orbs background
            FloatingOrbsBackground()

            // Content
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                uiState.error != null -> {
                    EmptyState(
                        onAction = { viewModel.refresh() },
                        modifier = Modifier.fillMaxSize(),
                        type = EmptyStateType.NETWORK_ERROR
                    )
                }
                uiState.savedCarts.isEmpty() -> {
                    EmptyState(
                        type = EmptyStateType.EMPTY_CART,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    SavedCartsContent(
                        savedCarts = uiState.savedCarts,
                        expandedCartIndex = uiState.expandedCartIndex,
                        onCartClick = { index ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.toggleCartExpansion(index)
                        },
                        onCartSelected = onCartSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingDialog(
            isLoading = true,
            message = "Loading saved carts..."
        )
    }
}

@Composable
private fun SavedCartsContent(
    savedCarts: List<Cart>,
    expandedCartIndex: Int?,
    onCartClick: (Int) -> Unit,
    onCartSelected: (SavedCart) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        contentPadding = PaddingValues(SpacingTokens.L),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = savedCarts,
            key = { savedCarts.indexOf(it) }
        ) { savedCart ->
            val index = savedCarts.indexOf(savedCart)
            SavedCartCard(
                savedCart = savedCart,
                isExpanded = expandedCartIndex == index,
                onClick = { onCartClick(index) },
                onSelect = { onCartSelected(savedCart) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedCartCard(
    savedCart: Cart,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onSelect: () -> Unit
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isExpanded) 8.dp else 2.dp,
        animationSpec = tween(300),
        label = "elevation"
    )

    AnimatedContent(
        targetState = isExpanded,
        label = "card_expansion"
    ) { expanded ->
        if (expanded) {
            // Expanded state - use GlassCard
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                onClick = onClick
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SavedCartHeader(
                        cartName = savedCart.name,
                        city = savedCart.city.toString(),
                        itemCount = savedCart.items.size,
                        totalPrice = savedCart.items.sumOf { it.price * it.quantity },
                        isExpanded = true
                    )

                    SavedCartDetails(
                        items = savedCart.items,
                        onSelect = onSelect
                    )
                }
            }
        } else {
            // Collapsed state - use GlassSelectableCard
            GlassSelectableCard(
                selected = false,
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                SavedCartHeader(
                    cartName = savedCart.cartName,
                    city = savedCart.city,
                    itemCount = savedCart.items.size,
                    totalPrice = savedCart.items.sumOf { it.price * it.quantity },
                    isExpanded = false
                )
            }
        }
    }
}

@Composable
private fun SavedCartHeader(
    cartName: String,
    city: String,
    itemCount: Int,
    totalPrice: Double,
    isExpanded: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpacingTokens.L),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - Cart info
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cart icon with glass background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.extended.electricMint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
            ) {
                Text(
                    text = cartName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // City chip
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.extended.electricMint
                        )
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "$itemCount items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Right side - Price and expand icon
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
        ) {
            AnimatedPriceCounter(
                targetValue = totalPrice,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.extended.electricMint
                )
            )

            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SavedCartDetails(
    items: List<SavedCartItem>,
    onSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L)
            .padding(bottom = SpacingTokens.L),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // Items list (show first 3, then "and X more")
        val displayItems = items.take(3)
        val remainingCount = items.size - 3

        Column(
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            displayItems.forEach { item ->
                SavedCartItemRow(item = item)
            }

            if (remainingCount > 0) {
                Text(
                    text = "and $remainingCount more items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = SpacingTokens.S)
                )
            }
        }

        // Action button
        PremiumButton(
            text = "Load Cart",
            onClick = onSelect,
            icon = {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SavedCartItemRow(
    item: SavedCartItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(GlassmorphicShapes.GlassCardSmall)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .padding(SpacingTokens.M),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            // Quantity badge
            GlassBadge(
                count = item.quantity,
                backgroundColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.2f),
                textColor = MaterialTheme.colorScheme.extended.electricMint,
                size = BadgeSize.MEDIUM
            )

            Text(
                text = item.itemName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "₪${String.format("%.2f", item.price * item.quantity)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}