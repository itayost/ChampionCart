package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.domain.models.Store
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

/**
 * Redesigned Cart Screen using existing component library
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToResults: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    // Load cities on first composition
    LaunchedEffect(Unit) {
        viewModel.loadAvailableCities()
    }

    Scaffold(
        topBar = {
            GlassmorphicTopAppBar(
                title = "Shopping Cart",
                onNavigationClick = onNavigateBack,
                actions = {
                    // City selector chip
                    CityChip(
                        city = state.selectedCity,
                        onClick = { viewModel.showCityDialog() }
                    )

                    // Clear cart action
                    if (state.cartItems.isNotEmpty()) {
                        GlassmorphicIconButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.clearCart()
                            },
                            icon = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (state.cartItems.isNotEmpty()) {
                CartBottomActionBar(
                    totalPrice = state.totalPrice,
                    isLoading = state.isFindingCheapest,
                    onFindCheapest = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.findCheapestPrices()
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background orbs
            FloatingOrbsBackground()

            if (state.cartItems.isEmpty()) {
                // Empty state
                EmptyState(
                    type = EmptyStateType.EMPTY_CART,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onAction = onNavigateToSearch
                )
            } else {
                // Cart items list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(SpacingTokens.L),
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
                ) {
                    items(
                        items = state.cartItems,
                        key = { it.id }
                    ) { item ->
                        CartItemCard(
                            item = item,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateQuantity(item.id, newQuantity)
                            },
                            onRemove = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.removeFromCart(item.id)
                            }
                        )
                    }

                    // Add more items card
                    item {
                        AddMoreItemsCard(
                            onClick = onNavigateToSearch
                        )
                    }
                }
            }

            // Loading overlay
            LoadingDialog(
                isLoading = state.isFindingCheapest,
                message = "Finding best prices..."
            )
        }
    }

    // City selection dialog
    if (state.showCityDialog) {
        CitySelectionDialog(
            cities = state.availableCities.map { cityString ->
                parseCityString(cityString)
            },
            currentCity = state.selectedCity,
            recentCities = emptyList(), // You can add logic to track recent cities
            onCitySelected = viewModel::selectCity,
            onDismiss = viewModel::hideCityDialog
        )
    }

    // Store selector bottom sheet
    if (state.showStoreSelector) {
        GlassmorphicBottomSheet(
            onDismiss = viewModel::hideStoreSelector,
            content = {
                StoreSelectorContent(
                    stores = state.availableStores,
                    selectedStore = state.selectedStore,
                    potentialSavings = state.potentialSavings,
                    onStoreSelected = viewModel::selectStore
                )
            }
        )

        // Navigate to results after selection
        LaunchedEffect(state.selectedStore) {
            if (state.selectedStore != null) {
                onNavigateToResults()
            }
        }
    }

    // Error handling
    state.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar
            viewModel.clearError()
        }
    }
}

/**
 * City selector chip component
 */
@Composable
private fun CityChip(
    city: String,
    onClick: () -> Unit
) {
    GlassmorphicChip(
        selected = true,
        onClick = onClick,
        leadingIcon = {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    ) {
        Text(
            text = city,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Enhanced Cart Item Card using glass components
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartItemCard(
    item: LocalCartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { /* Handle item click */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.L)
        ) {
            // Product emoji/icon with glass background
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getProductEmoji(item.itemName),
                    fontSize = 28.sp
                )
            }

            // Product details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
            ) {
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                PriceDisplay(
                    price = item.price,
                    quantity = item.quantity,
                    total = item.price * item.quantity
                )
            }

            // Quantity controls using existing components
            QuantitySelector(
                quantity = item.quantity,
                onIncrease = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onQuantityChange(item.quantity + 1)
                },
                onDecrease = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (item.quantity > 1) {
                        onQuantityChange(item.quantity - 1)
                    } else {
                        onRemove()
                    }
                },
                showDeleteOnOne = true
            )
        }
    }
}

/**
 * Price display component
 */
@Composable
private fun PriceDisplay(
    price: Double,
    quantity: Int,
    total: Double
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "₪${String.format("%.2f", price)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.extended.electricMint,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "× $quantity",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "= ₪${String.format("%.2f", total)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Quantity selector component
 */
@Composable
private fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    showDeleteOnOne: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease/Delete button
        GlassmorphicIconButton(
            onClick = onDecrease,
            size = 36.dp,
            icon = if (quantity > 1 || !showDeleteOnOne) Icons.Default.Remove else Icons.Default.Delete,
            tint = if (quantity > 1 || !showDeleteOnOne) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.error
            },
            glassIntensity = if (quantity > 1) GlassIntensity.Medium else GlassIntensity.Light,
            backgroundColor = if (quantity <= 1 && showDeleteOnOne) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
            } else null
        )

        // Quantity display
        GlassBadge(
            count = quantity,
            backgroundColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f),
            textColor = MaterialTheme.colorScheme.extended.electricMint,
            size = BadgeSize.LARGE
        )

        // Increase button
        GlassmorphicIconButton(
            onClick = onIncrease,
            size = 36.dp,
            icon = Icons.Default.Add,
            tint = Color.White,
            backgroundColor = MaterialTheme.colorScheme.extended.electricMint
        )
    }
}

/**
 * Add more items card using glass components
 */
@Composable
private fun AddMoreItemsCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassOutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        borderColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.extended.electricMint
            )
            Spacer(modifier = Modifier.width(SpacingTokens.S))
            Text(
                text = "Add More Items",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.extended.electricMint
            )
        }
    }
}

/**
 * Cart bottom action bar using premium button
 */
@Composable
private fun CartBottomActionBar(
    totalPrice: Double,
    isLoading: Boolean,
    onFindCheapest: () -> Unit
) {
    GlassmorphicNavigationContainer(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            // Total row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                AnimatedPriceCounter(
                    targetValue = totalPrice,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.extended.electricMint
                    )
                )
            }

            // Find cheapest button using premium button
            PremiumButton(
                text = "Find Best Prices",
                onClick = onFindCheapest,
                loading = isLoading,
                enabled = !isLoading,
                icon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Store selector content for bottom sheet
 */
@Composable
private fun StoreSelectorContent(
    stores: List<StoreWithPrice>,
    selectedStore: Store?,
    potentialSavings: Double,
    onStoreSelected: (Store) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpacingTokens.L)
    ) {
        // Header
        Text(
            text = "Select Store",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = SpacingTokens.XXS)
        )
        Text(
            text = "Choose where you want to shop",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = SpacingTokens.L)
        )

        // Best deal indicator
        if (potentialSavings > 0) {
            BestDealCard(
                savings = potentialSavings,
                bestStore = stores.firstOrNull()?.store?.name ?: ""
            )
        }

        // Store list using LazyColumn to fix Composable context error
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            items(
                items = stores,
                key = { it.store.id }
            ) { storeWithPrice ->
                StoreOptionCard(
                    store = storeWithPrice.store,
                    totalPrice = storeWithPrice.totalPrice,
                    isSelected = selectedStore?.id == storeWithPrice.store.id,
                    isBestDeal = stores.indexOf(storeWithPrice) == 0,
                    onSelect = { onStoreSelected(storeWithPrice.store) }
                )
            }
        }
    }
}

/**
 * Best deal indicator card
 */
@Composable
private fun BestDealCard(
    savings: Double,
    bestStore: String
) {
    GlassInfoCard(
        icon = { Text("💰", fontSize = 20.sp) },
        title = "You save ₪${String.format("%.2f", savings)}",
        backgroundColor = MaterialTheme.colorScheme.extended.success.copy(alpha = 0.1f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = SpacingTokens.M)
    )
}

/**
 * Store option card
 */
@Composable
private fun StoreOptionCard(
    store: Store,
    totalPrice: Double,
    isSelected: Boolean,
    isBestDeal: Boolean,
    onSelect: () -> Unit
) {
    GlassSelectableCard(
        selected = isSelected,
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Store info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (isBestDeal) {
                        GlassBadge(
                            text = "BEST DEAL",
                            backgroundColor = MaterialTheme.colorScheme.extended.success,
                            textColor = Color.White,
                            size = BadgeSize.SMALL
                        )
                    }
                }
                Text(
                    text = store.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Price
            AnimatedPriceCounter(
                targetValue = totalPrice,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isBestDeal) {
                        MaterialTheme.colorScheme.extended.success
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            )
        }
    }
}

/**
 * Helper function to get emoji for products
 */
private fun getProductEmoji(productName: String): String {
    val name = productName.lowercase()
    return when {
        name.contains("חלב") || name.contains("milk") -> "🥛"
        name.contains("לחם") || name.contains("bread") -> "🍞"
        name.contains("ביצה") || name.contains("egg") -> "🥚"
        name.contains("במבה") || name.contains("bamba") -> "🥜"
        name.contains("גבינה") || name.contains("cheese") -> "🧀"
        name.contains("עגבניה") || name.contains("tomato") -> "🍅"
        name.contains("תפוח") || name.contains("apple") -> "🍎"
        name.contains("בננה") || name.contains("banana") -> "🍌"
        name.contains("בשר") || name.contains("meat") -> "🥩"
        name.contains("עוף") || name.contains("chicken") -> "🍗"
        name.contains("דג") || name.contains("fish") -> "🐟"
        else -> "📦"
    }
}

/**
 * Parse city string to CityInfo object
 */
private fun parseCityString(cityString: String): CityInfo {
    // Parse format: "Tel Aviv: 45 shufersal, 12 victory" or just "Tel Aviv"
    val parts = cityString.split(":")
    val cityName = parts[0].trim()

    val storeBreakdown = mutableMapOf<String, Int>()
    var totalStores = 0

    if (parts.size > 1) {
        val storeParts = parts[1].trim().split(",")
        storeParts.forEach { storePart ->
            val storeInfo = storePart.trim().split(" ")
            if (storeInfo.size >= 2) {
                val count = storeInfo[0].toIntOrNull() ?: 0
                val chain = storeInfo[1]
                storeBreakdown[chain] = count
                totalStores += count
            }
        }
    }

    return CityInfo(
        name = cityName,
        nameHebrew = translateCityName(cityName),
        totalStores = totalStores,
        storeBreakdown = storeBreakdown,
        isPopular = popularCities.contains(cityName)
    )
}

/**
 * Translate city name to Hebrew
 */
private fun translateCityName(cityName: String): String {
    return when (cityName.lowercase()) {
        "tel aviv" -> "תל אביב"
        "jerusalem" -> "ירושלים"
        "haifa" -> "חיפה"
        "beer sheva" -> "באר שבע"
        "rishon lezion" -> "ראשון לציון"
        "petah tikva" -> "פתח תקווה"
        "ashdod" -> "אשדוד"
        "netanya" -> "נתניה"
        else -> cityName
    }
}

// Popular cities list
private val popularCities = listOf("Tel Aviv", "Jerusalem", "Haifa", "Beer Sheva", "Rishon LeZion")