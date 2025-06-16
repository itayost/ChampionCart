package com.example.championcart.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.R
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.presentation.components.EmptyState
import com.example.championcart.presentation.components.ErrorState
import com.example.championcart.presentation.utils.shimmerEffect
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // Request focus on search field when screen opens
    LaunchedEffect(Unit) {
        delay(300) // Small delay for animation
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                onSearch = {
                    keyboardController?.hide()
                    viewModel.searchProducts()
                },
                onBack = onNavigateBack,
                onVoiceSearch = { /* TODO: Implement voice search */ },
                focusRequester = focusRequester,
                isLoading = state.isLoading
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.groupedProducts.isEmpty() -> {
                    // Initial loading state
                    LoadingContent()
                }
                state.error != null && state.groupedProducts.isEmpty() -> {
                    // Error state
                    ErrorState(
                        message = state.error,
                        onRetry = { viewModel.retry() }
                    )
                }
                state.groupedProducts.isEmpty() && state.hasSearched -> {
                    // Empty state after search
                    EmptyState(
                        title = "No products found",
                        subtitle = "Try searching with different keywords\nor check your spelling",
                        icon = Icons.Default.SearchOff
                    )
                }
                state.groupedProducts.isEmpty() -> {
                    // Initial state before search
                    InitialSearchState(
                        recentSearches = state.recentSearches,
                        popularSearches = state.popularSearches,
                        onSearchClick = { query ->
                            viewModel.updateSearchQuery(query)
                            viewModel.searchProducts()
                        }
                    )
                }
                else -> {
                    // Results state
                    SearchResultsContent(
                        groupedProducts = state.groupedProducts,
                        onProductClick = onNavigateToProduct,
                        onAddToCart = { productId ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addToCart(productId)
                        },
                        sortOption = state.sortOption,
                        onSortChange = viewModel::updateSort,
                        isRefreshing = state.isLoading
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    onVoiceSearch: () -> Unit,
    focusRequester: FocusRequester,
    isLoading: Boolean
) {
    val haptics = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onBack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = "Search for products...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.extendedColors.electricMint
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                trailingIcon = {
                    Row {
                        // Clear button
                        AnimatedVisibility(
                            visible = searchQuery.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            IconButton(
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSearchQueryChange("")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                        // Voice search
                        IconButton(onClick = onVoiceSearch) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice search",
                                tint = MaterialTheme.extendedColors.electricMint
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch() }
                ),
                singleLine = true,
                shape = ComponentShapes.SearchBar,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.extendedColors.electricMint,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.extendedColors.glassFrosted,
                    unfocusedContainerColor = MaterialTheme.extendedColors.glassFrosted
                )
            )
        }
    }
}

@Composable
fun InitialSearchState(
    recentSearches: List<String>,
    popularSearches: List<String>,
    onSearchClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Recent searches
        if (recentSearches.isNotEmpty()) {
            item {
                SearchSection(
                    title = "Recent Searches",
                    icon = Icons.Default.History,
                    items = recentSearches,
                    onItemClick = onSearchClick
                )
            }
        }

        // Popular searches
        item {
            SearchSection(
                title = "Popular Right Now",
                icon = Icons.Default.TrendingUp,
                items = popularSearches,
                onItemClick = onSearchClick,
                highlightColor = MaterialTheme.extendedColors.neonCoral
            )
        }

        // Search tips
        item {
            SearchTipsCard()
        }
    }
}

@Composable
fun SearchSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<String>,
    onItemClick: (String) -> Unit,
    highlightColor: Color = MaterialTheme.extendedColors.electricMint
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = highlightColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items.forEach { item ->
            SearchSuggestionItem(
                text = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
fun SearchSuggestionItem(
    text: String,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glass
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.extendedColors.glassBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.NorthEast,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SearchTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.electricMint
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Search Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val tips = listOf(
                "🔍 Use Hebrew for better results",
                "📦 Try generic terms like \"חלב\" or \"לחם\"",
                "🏷️ Brand names work too!",
                "💡 We search across Shufersal & Victory"
            )

            tips.forEach { tip ->
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SearchResultsContent(
    groupedProducts: List<GroupedProduct>,
    onProductClick: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    isRefreshing: Boolean
) {
    Column {
        // Results header with sort
        ResultsHeader(
            resultCount = groupedProducts.size,
            sortOption = sortOption,
            onSortChange = onSortChange
        )

        // Product list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = groupedProducts,
                key = { it.itemCode }
            ) { groupedProduct ->
                GroupedProductCard(
                    groupedProduct = groupedProduct,
                    onClick = { onProductClick(groupedProduct.itemCode) },
                    onAddToCart = { onAddToCart(groupedProduct.itemCode) }
                )
            }
        }
    }
}

@Composable
fun ResultsHeader(
    resultCount: Int,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$resultCount products found",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // Sort button
            Box {
                TextButton(
                    onClick = { showSortMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(sortOption.label)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(option.label)
                                    if (option == sortOption) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.extendedColors.electricMint,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onSortChange(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupedProductCard(
    groupedProduct: GroupedProduct,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val bestPrice = groupedProduct.lowestPrice ?: 0.0
    val worstPrice = groupedProduct.highestPrice ?: 0.0
    val savings = groupedProduct.savings
    val savingsPercent = if (worstPrice > 0) {
        ((worstPrice - bestPrice) / worstPrice * 100).toInt()
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                alignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Product name
                    Text(
                        text = groupedProduct.itemName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Item code
                    Text(
                        text = "Code: ${groupedProduct.itemCode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Savings badge
                if (savingsPercent > 0) {
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
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "SAVE $savingsPercent%",
                            style = MaterialTheme.typography.labelSmall,
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Best price
                Column {
                    Text(
                        text = "Best Price",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₪${String.format("%.2f", bestPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.successGreen
                    )
                }

                // Price range indicator
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PriceRangeIndicator(
                        lowestPrice = bestPrice,
                        highestPrice = worstPrice
                    )
                }

                // Worst price
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Highest",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₪${String.format("%.2f", worstPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = TextDecoration.LineThrough,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Store prices
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedProduct.storePrices.take(3).forEach { storePrice ->
                    StorePriceRow(
                        store = storePrice,
                        isBestPrice = storePrice.price == bestPrice
                    )
                }

                // Show more stores if available
                if (groupedProduct.storePrices.size > 3) {
                    Text(
                        text = "+ ${groupedProduct.storePrices.size - 3} more stores",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.electricMint,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // View details button
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    shape = ComponentShapes.Button,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.extendedColors.electricMint
                    )
                ) {
                    Text("View Details")
                }

                // Add to cart button
                Button(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onAddToCart()
                    },
                    modifier = Modifier.weight(1f),
                    shape = ComponentShapes.Button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.electricMint
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Cart")
                }
            }
        }
    }
}

@Composable
fun StorePriceRow(
    store: com.example.championcart.domain.models.ProductStorePrice,
    isBestPrice: Boolean
) {
    val storeColor = when (store.chain.lowercase()) {
        "shufersal" -> MaterialTheme.extendedColors.shufersal
        "victory" -> MaterialTheme.extendedColors.victory
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ComponentShapes.Badge)
            .background(
                if (isBestPrice) {
                    MaterialTheme.extendedColors.successGreen.copy(alpha = 0.1f)
                } else {
                    MaterialTheme.extendedColors.glass
                }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Store indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(storeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Store name
            Text(
                text = "${store.chain} - ${store.storeId}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isBestPrice) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isBestPrice) {
                    MaterialTheme.extendedColors.successGreen
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            // Best price indicator
            if (isBestPrice) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Best price",
                    tint = MaterialTheme.extendedColors.successGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Price
        Text(
            text = "₪${String.format("%.2f", store.price)}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isBestPrice) {
                MaterialTheme.extendedColors.successGreen
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun PriceRangeIndicator(
    lowestPrice: Double,
    highestPrice: Double
) {
    if (lowestPrice == highestPrice) {
        Text(
            text = "Same price",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(ComponentShapes.Badge)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.successGreen,
                                MaterialTheme.extendedColors.warningAmber
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun LoadingContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(5) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = ComponentShapes.Card
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect()
                )
            }
        }
    }
}

enum class SortOption(val label: String) {
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    SAVINGS_HIGHEST("Highest Savings"),
    NAME_A_TO_Z("Name: A to Z")
}