package com.example.championcart.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                brush = MaterialTheme.extendedColors.backgroundGradient
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

            state.error != null -> {
                ErrorState(
                    message = state.error,
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
                    style = AppTextStyles.searchPlaceholder
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
            textStyle = AppTextStyles.searchInput
        )

        Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

        // Filter row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // City selector chip
            FilterChip(
                onClick = { /* TODO: Implement city selection dialog */ },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Spacer(modifier = Modifier.width(Dimensions.spacingExtraSmall))
                        Text(
                            selectedCity,
                            style = AppTextStyles.chipText
                        )
                    }
                },
                selected = false,
                shape = ComponentShapes.Chip,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.extendedColors.glass,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = MaterialTheme.extendedColors.glassBorder
                )
            )

            // Identical products only toggle
            FilterChip(
                onClick = onToggleIdenticalOnly,
                label = {
                    Text(
                        "Cross-chain only",
                        style = AppTextStyles.chipText
                    )
                },
                selected = showIdenticalOnly,
                shape = ComponentShapes.Chip,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.extendedColors.electricMint,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.extendedColors.glass,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                leadingIcon = if (showIdenticalOnly) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                    }
                } else null
            )

            Spacer(modifier = Modifier.weight(1f))

            // Search button
            Button(
                onClick = onSearch,
                enabled = searchQuery.isNotEmpty() && !isLoading,
                modifier = Modifier.height(Dimensions.buttonHeightSmall),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.extendedColors.electricMint
                ),
                shape = ComponentShapes.ButtonSmall
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimensions.iconSizeSmall),
                        strokeWidth = Dimensions.borderThin,
                        color = Color.White
                    )
                } else {
                    Text(
                        "Search",
                        style = AppTextStyles.buttonTextSmall
                    )
                }
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
        modifier = Modifier.padding(horizontal = Dimensions.paddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingLarge)
    ) {
        if (recentSearches.isNotEmpty()) {
            item {
                SuggestionSection(
                    title = "Recent Searches",
                    suggestions = recentSearches,
                    onSuggestionClick = onSearchSuggestion,
                    icon = Icons.Default.History
                )
            }
        }

        if (popularSearches.isNotEmpty()) {
            item {
                SuggestionSection(
                    title = "Popular Searches",
                    suggestions = popularSearches,
                    onSuggestionClick = onSearchSuggestion,
                    icon = Icons.Default.TrendingUp
                )
            }
        }
    }
}

@Composable
fun SuggestionSection(
    title: String,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    icon: ImageVector
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
                style = AppTextStyles.sectionHeader
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
                style = AppTextStyles.chipText
            )
        },
        selected = false,
        shape = ComponentShapes.Chip,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.extendedColors.glass,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.extendedColors.glassBorder
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
            text = "$resultCount products found",
            style = AppTextStyles.resultCount
        )

        Box {
            FilterChip(
                onClick = { showSortMenu = true },
                label = {
                    Text(
                        "Sort: ${sortOption.displayName}",
                        style = AppTextStyles.chipText
                    )
                },
                selected = false,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.extendedColors.glass,
                    labelColor = MaterialTheme.colorScheme.onSurface
                )
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
                                style = AppTextStyles.dropdownItem
                            )
                        },
                        onClick = {
                            onSortChange(option)
                            showSortMenu = false
                        },
                        leadingIcon = if (option == sortOption) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                                )
                            }
                        } else null
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
        modifier = Modifier
            .fillMaxWidth()
            .glassEffect(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // glassEffect handles background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationMedium)
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingMedium)
        ) {
            // Product header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.itemName,
                        style = AppTextStyles.productTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (product.weight != null && product.unit != null) {
                        Text(
                            text = "${product.weight} ${product.unit}",
                            style = AppTextStyles.productDetails
                        )
                    }
                }

                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.height(Dimensions.buttonHeightSmall),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.electricMint
                    ),
                    shape = ComponentShapes.ButtonSmall
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to cart",
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingExtraSmall))
                    Text(
                        "Add",
                        style = AppTextStyles.buttonTextSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Price comparison
            PriceComparison(
                storePrices = product.prices,
                savings = product.savings
            )
        }
    }
}

@Composable
fun PriceComparison(
    storePrices: List<StorePrice>,
    savings: Double
) {
    Column {
        // Savings badge if applicable
        if (savings > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
            ) {
                Surface(
                    color = MaterialTheme.extendedColors.successGreen,
                    shape = ComponentShapes.Badge
                ) {
                    Text(
                        text = "Save ₪${String.format("%.2f", savings)}",
                        style = AppTextStyles.savingsBadge,
                        color = Color.White,
                        modifier = Modifier.padding(
                            horizontal = Dimensions.paddingSmall,
                            vertical = Dimensions.paddingExtraSmall
                        )
                    )
                }
            }
        }

        // Store prices
        storePrices.sortedBy { it.price }.forEachIndexed { index, storePrice ->
            StorePriceRow(
                storePrice = storePrice,
                isBestPrice = index == 0,
                modifier = Modifier.padding(vertical = Dimensions.spacingExtraSmall)
            )
        }
    }
}

@Composable
fun StorePriceRow(
    storePrice: StorePrice,
    isBestPrice: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = storePrice.chain.replaceFirstChar { it.uppercase() },
                style = if (isBestPrice) AppTextStyles.bestPriceStore else AppTextStyles.priceStore
            )

            if (isBestPrice) {
                Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                Surface(
                    color = MaterialTheme.extendedColors.bestPrice,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Best price",
                        tint = Color.White,
                        modifier = Modifier
                            .size(Dimensions.iconSizeSmall)
                            .padding(Dimensions.spacingExtraSmall)
                    )
                }
            }
        }

        Text(
            text = "₪${String.format("%.2f", storePrice.price)}",
            style = if (isBestPrice) AppTextStyles.bestPrice else AppTextStyles.regularPrice
        )
    }
}

@Composable
fun EmptySearchResults(
    searchQuery: String,
    onClearSearch: () -> Unit
) {
    EmptyState(
        icon = Icons.Outlined.SearchOff,
        title = "No products found",
        description = "We couldn't find any products matching \"$searchQuery\".\nTry different keywords or check spelling.",
        actionText = "Clear Search",
        onAction = onClearSearch,
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingLarge)
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
                    .height(Dimensions.buttonHeightSmall)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        ComponentShapes.ButtonSmall
                    )
            )

            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.spacingMedium)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            ComponentShapes.ButtonSmall
                        )
                )
            }
        }
    }
}

// Extension for SortOption display names
private val SortOption.displayName: String
    get() = when (this) {
        SortOption.RELEVANCE -> "Relevance"
        SortOption.PRICE_LOW_TO_HIGH -> "Price: Low to High"
        SortOption.PRICE_HIGH_TO_LOW -> "Price: High to Low"
        SortOption.NAME_A_TO_Z -> "Name: A to Z"
        SortOption.NAME_Z_TO_A -> "Name: Z to A"
        SortOption.SAVINGS_HIGH_TO_LOW -> "Highest Savings"
    }