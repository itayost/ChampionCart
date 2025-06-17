package com.example.championcart.presentation.screens.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.SortOption
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.presentation.components.EmptyState
import com.example.championcart.presentation.components.ErrorState
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        searchFocusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.extendedColors.electricMint.copy(alpha = 0.02f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        // Search header with input and filters
        SearchHeader(
            searchQuery = state.searchQuery,
            onSearchQueryChange = viewModel::updateSearchQuery,
            onSearch = {
                keyboardController?.hide()
                focusManager.clearFocus()
                viewModel.searchProducts()
            },
            selectedCity = state.selectedCity,
            onCityChange = viewModel::selectCity,
            showIdenticalOnly = state.showIdenticalOnly,
            onToggleIdenticalOnly = viewModel::toggleIdenticalOnly,
            isLoading = state.isLoading,
            focusRequester = searchFocusRequester
        )

        // Content based on state
        when {
            state.isLoading -> {
                LoadingContent()
            }

            !state.error.isNullOrEmpty() -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = viewModel::retry,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimensions.paddingLarge)
                )
            }

            state.hasSearched && state.groupedProducts.isEmpty() -> {
                EmptySearchResults(
                    searchQuery = state.searchQuery,
                    onClearSearch = viewModel::clearResults
                )
            }

            state.groupedProducts.isNotEmpty() -> {
                SearchResults(
                    products = state.groupedProducts,
                    sortOption = state.sortOption,
                    onSortChange = viewModel::updateSort,
                    onAddToCart = { product ->
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.addToCart(product)
                    }
                )
            }

            else -> {
                SearchSuggestions(
                    recentSearches = state.recentSearches,
                    popularSearches = state.popularSearches,
                    onSearchSuggestion = { query ->
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.searchFromSuggestion(query)
                    }
                )
            }
        }
    }
}

@Composable
fun SearchHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    selectedCity: String,
    onCityChange: (String) -> Unit,
    showIdenticalOnly: Boolean,
    onToggleIdenticalOnly: () -> Unit,
    isLoading: Boolean,
    focusRequester: FocusRequester
) {
    Column(
        modifier = Modifier.padding(Dimensions.paddingMedium)
    ) {
        // Search input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    "Search for products...",
                    style = AppTextStyles.inputHint
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.electricMint,
                    modifier = Modifier.size(Dimensions.iconSizeMedium)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            modifier = Modifier.size(Dimensions.iconSizeMedium)
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            shape = ComponentShapes.SearchBar,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.extendedColors.electricMint,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            singleLine = true,
            textStyle = AppTextStyles.inputText
        )

        Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            item {
                FilterChip(
                    onClick = onToggleIdenticalOnly,
                    label = { Text("Identical Products", style = AppTextStyles.chip) },
                    selected = showIdenticalOnly,
                    enabled = true
                )
            }

            item {
                FilterChip(
                    onClick = { /* City selection */ },
                    label = { Text(selectedCity, style = AppTextStyles.chip) },
                    selected = false,
                    enabled = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SearchSuggestions(
    recentSearches: List<String>,
    popularSearches: List<String>,
    onSearchSuggestion: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingLarge)
    ) {
        if (recentSearches.isNotEmpty()) {
            item {
                SuggestionSection(
                    title = "Recent Searches",
                    icon = Icons.Default.History,
                    suggestions = recentSearches,
                    onSuggestionClick = onSearchSuggestion
                )
            }
        }

        if (popularSearches.isNotEmpty()) {
            item {
                SuggestionSection(
                    title = "Popular Searches",
                    icon = Icons.Default.TrendingUp,
                    suggestions = popularSearches,
                    onSuggestionClick = onSearchSuggestion
                )
            }
        }
    }
}

@Composable
fun SuggestionSection(
    title: String,
    icon: ImageVector,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = Dimensions.spacingMedium)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.electricMint,
                modifier = Modifier.size(Dimensions.iconSizeMedium)
            )
            Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
            Text(
                text = title,
                style = AppTextStyles.storeNameLarge
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            items(suggestions) { suggestion ->
                SuggestionChip(
                    text = suggestion,
                    onClick = { onSuggestionClick(suggestion) }
                )
            }
        }
    }
}

@Composable
fun SuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text,
                style = AppTextStyles.chip
            )
        },
        selected = false,
        enabled = true,
        shape = ComponentShapes.Chip,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.extendedColors.glass,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.extendedColors.glassBorder,
            enabled = TODO(),
            selected = TODO()
        )
    )
}

@Composable
fun SearchResults(
    products: List<GroupedProduct>,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    onAddToCart: (GroupedProduct) -> Unit
) {
    Column {
        // Results header with sort
        ResultsHeader(
            resultCount = products.size,
            sortOption = sortOption,
            onSortChange = onSortChange
        )

        // Products list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium),
            contentPadding = PaddingValues(Dimensions.paddingMedium)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onAddToCart = { onAddToCart(product) }
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimensions.paddingMedium,
                vertical = Dimensions.paddingSmall
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$resultCount results",
            style = AppTextStyles.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box {
            FilterChip(
                onClick = { showSortMenu = true },
                label = { Text(sortOption.displayName, style = AppTextStyles.chip) },
                selected = false,
                enabled = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                SortOption.values().forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option.displayName,
                                style = AppTextStyles.hebrewBody
                            )
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

@Composable
fun ProductCard(
    product: GroupedProduct,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationMedium
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium)
        ) {
            // Product name
            Text(
                text = product.itemName,
                style = AppTextStyles.productName,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingSmall))

            // Product details
            if (product.unit != null || product.weight != null) {
                Text(
                    text = buildString {
                        product.unit?.let { append(it) }
                        if (product.unit != null && product.weight != null) append(" • ")
                        product.weight?.let { append("${it}g") }
                    },
                    style = AppTextStyles.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
            }

            // Price comparison
            if (product.prices.isNotEmpty()) {
                PriceComparison(
                    prices = product.prices,
                    savings = product.savings,
                    onAddToCart = onAddToCart
                )
            }
        }
    }
}

@Composable
fun PriceComparison(
    prices: List<StorePrice>,
    savings: Double,
    onAddToCart: () -> Unit
) {
    val bestPrice = prices.minByOrNull { it.price }
    val worstPrice = prices.maxByOrNull { it.price }

    Column {
        // Best price row
        bestPrice?.let { price ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = price.chain,
                        style = AppTextStyles.buttonSmall,
                        color = MaterialTheme.extendedColors.electricMint,
                        fontWeight = FontWeight.Bold
                    )
                    if (savings > 0) {
                        Surface(
                            shape = ComponentShapes.Badge,
                            color = MaterialTheme.extendedColors.success
                        ) {
                            Text(
                                text = "Save ₪${String.format("%.2f", savings)}",
                                style = AppTextStyles.badge,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₪${String.format("%.2f", price.price)}",
                            style = AppTextStyles.priceLarge,
                            color = MaterialTheme.extendedColors.bestPrice,
                            fontWeight = FontWeight.Bold
                        )
                        if (worstPrice != null && worstPrice.price > price.price) {
                            Text(
                                text = "₪${String.format("%.2f", worstPrice.price)}",
                                style = AppTextStyles.priceSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.extendedColors.electricMint
                        ),
                        shape = ComponentShapes.Button
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptySearchResults(
    searchQuery: String,
    onClearSearch: () -> Unit
) {
    EmptyState(
        title = "No products found",
        subtitle = "We couldn't find any products matching \"$searchQuery\"",
        icon = Icons.Default.SearchOff,
        actionLabel = "Clear Search",
        onAction = onClearSearch,
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingLarge),
        message = "You haven't saved any shopping carts yet. Start shopping and save your carts for later!"
    )
}

@Composable
fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        repeat(5) {
            SearchResultSkeleton()
        }
    }
}

@Composable
fun SearchResultSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glassEffect(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(24.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        ComponentShapes.ButtonSmall
                    )
            )

            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            ComponentShapes.ButtonSmall
                        )
                )
            }
        }
    }
}