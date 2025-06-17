package com.example.championcart.presentation.screens.savedcarts

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.models.SavedCart
import com.example.championcart.domain.models.SavedCartItem
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.presentation.components.EmptyState
import com.example.championcart.presentation.components.ErrorState
import com.example.championcart.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedCartsUiState(
    val savedCarts: List<SavedCart> = emptyList(),
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

            authRepository.getUserSavedCarts().fold(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.extendedColors.backgroundGradient.colors[0],
                        MaterialTheme.extendedColors.backgroundGradient.colors[1]
                    )
                )
            )
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Saved Carts",
                    style = AppTextStyles.hebrewHeadline,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateBack()
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.refresh()
                    }
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.extendedColors.electricMint
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimensions.paddingMedium)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error!!,
                        onRetry = { viewModel.refresh() }
                    )
                }
                uiState.savedCarts.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Outlined.ShoppingCart,
                        title = "No Saved Carts",
                        message = "You haven't saved any shopping carts yet. Start shopping and save your carts for later!"
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.extendedColors.electricMint,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Loading saved carts...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SavedCartsContent(
    savedCarts: List<SavedCart>,
    expandedCartIndex: Int?,
    onCartClick: (Int) -> Unit,
    onCartSelected: (SavedCart) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium),
        contentPadding = PaddingValues(vertical = Dimensions.spacingMedium)
    ) {
        items(savedCarts.size) { index ->
            SavedCartCard(
                savedCart = savedCarts[index],
                isExpanded = expandedCartIndex == index,
                onClick = { onCartClick(index) },
                onSelect = { onCartSelected(savedCarts[index]) }
            )
        }
    }
}

@Composable
private fun SavedCartCard(
    savedCart: SavedCart,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onSelect: () -> Unit
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isExpanded) Dimensions.elevationLarge else Dimensions.elevationMedium,
        animationSpec = tween(300),
        label = "elevation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = animatedElevation
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Cart Header
            SavedCartHeader(
                cartName = savedCart.cartName,
                city = savedCart.city,
                itemCount = savedCart.items.size,
                totalPrice = savedCart.items.sumOf { it.price * it.quantity },
                isExpanded = isExpanded,
                onClick = onClick
            )

            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                SavedCartDetails(
                    items = savedCart.items,
                    onSelect = onSelect
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
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
            ) {
                Text(
                    text = cartName,
                    style = AppTextStyles.productNameLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconSizeSmall),
                        tint = MaterialTheme.extendedColors.electricMint
                    )
                    Text(
                        text = city,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "$itemCount items",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
            ) {
                Text(
                    text = "₪${String.format("%.2f", totalPrice)}",
                    style = AppTextStyles.priceHero,
                    color = MaterialTheme.extendedColors.electricMint,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
            }
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
            .padding(horizontal = Dimensions.paddingMedium)
            .padding(bottom = Dimensions.paddingMedium)
    ) {
        Divider(
            modifier = Modifier.padding(bottom = Dimensions.spacingMedium),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        // Items list (show first 3, then "and X more")
        val displayItems = items.take(3)
        val remainingCount = items.size - 3

        displayItems.forEach { item ->
            SavedCartItemRow(item = item)
        }

        if (remainingCount > 0) {
            Text(
                text = "and $remainingCount more items",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    vertical = Dimensions.spacingSmall,
                    horizontal = Dimensions.paddingSmall
                )
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

        // Action button
        Button(
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.extendedColors.electricMint
            ),
            shape = ComponentShapes.Button
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSizeSmall)
            )
            Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
            Text(
                text = "Load Cart",
                style = AppTextStyles.buttonMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SavedCartItemRow(
    item: SavedCartItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.spacingExtraSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            Text(
                text = "${item.quantity}x",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.electricMint,
                fontWeight = FontWeight.Bold
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
            style = AppTextStyles.priceSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}