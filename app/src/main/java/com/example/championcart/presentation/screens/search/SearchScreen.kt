package com.example.championcart.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.SortOption
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onProductClick: (GroupedProduct) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        searchFocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.02f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Header
            SearchHeader(
                searchQuery = state.searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                onSearch = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.searchProducts()
                },
                onBack = onNavigateBack,
                focusRequester = searchFocusRequester,
                modifier = Modifier.padding(SpacingTokens.L)
            )

            // Filter Section
            SearchFilterSection(
                selectedCity = state.selectedCity,
                onCityChange = viewModel::selectCity,
                showIdenticalOnly = state.showIdenticalOnly,
                onToggleIdenticalOnly = viewModel::toggleIdenticalOnly,
                modifier = Modifier.padding(bottom = SpacingTokens.M)
            )

            // Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    state.isLoading -> {
                        LoadingContent(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    state.error != null -> {
                        EmptyState(
                            type = EmptyStateType.EMPTY_CART,
                            title = "שגיאה",
                            subtitle = state.error,
                            actionLabel = "נסה שוב",
                            onAction = viewModel::retry,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(SpacingTokens.XL)
                        )
                    }

                    state.hasSearched && state.groupedProducts.isEmpty() -> {
                        EmptyState(
                            type = EmptyStateType.NO_RESULTS,
                            title = "לא נמצאו תוצאות",
                            subtitle = "נסה לחפש עם מילות מפתח אחרות",
                            actionLabel = "נקה חיפוש",
                            onAction = viewModel::clearResults,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(SpacingTokens.XL)
                        )
                    }

                    state.groupedProducts.isNotEmpty() -> {
                        SearchResultsContent(
                            products = state.groupedProducts,
                            sortOption = state.sortOption,
                            onSortChange = viewModel::updateSort,
                            onProductClick = onProductClick,
                            onAddToCart = { product ->
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.addToCart(product)
                            }
                        )
                    }

                    else -> {
                        SearchSuggestionsContent(
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
    }
}

@Composable
private fun SearchHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(SizingTokens.IconL)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "חזור",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Search Field
        SearchTextField(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = onSearch,
            placeholder = "חפש מוצרים...",
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
        )
    }
}

@Composable
private fun SearchFilterSection(
    selectedCity: String,
    onCityChange: (String) -> Unit,
    showIdenticalOnly: Boolean,
    onToggleIdenticalOnly: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = SpacingTokens.L),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        // City selector
        item {
            CityFilterChip(
                selectedCity = selectedCity,
                onClick = { /* Open city selector */ }
            )
        }

        // Identical products toggle
        item {
            FilterChip(
                selected = showIdenticalOnly,
                onClick = onToggleIdenticalOnly,
                label = {
                    Text(
                        "מוצרים זהים בלבד",
                        style = AppTextStyles.chipText
                    )
                },
                leadingIcon = if (showIdenticalOnly) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SizingTokens.IconXS)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.extended.electricMint,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = GlassmorphicShapes.Chip
            )
        }
    }
}

@Composable
private fun CityFilterChip(
    selectedCity: String,
    onClick: () -> Unit
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(SizingTokens.IconXS),
                    tint = MaterialTheme.colorScheme.extended.electricMint
                )
                Text(
                    text = selectedCity,
                    style = AppTextStyles.chipText
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(SizingTokens.IconXS)
                )
            }
        },
        shape = GlassmorphicShapes.Chip,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun SearchResultsContent(
    products: List<GroupedProduct>,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    onProductClick: (GroupedProduct) -> Unit,
    onAddToCart: (GroupedProduct) -> Unit
) {
    Column {
        // Results header with sort
        SearchResultsHeader(
            resultCount = products.size,
            sortOption = sortOption,
            onSortChange = onSortChange
        )

        // Product list
        LazyColumn(
            contentPadding = PaddingValues(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            items(products) { product ->
                GroupedProductCard(
                    product = product,
                    onAddToCart = { storePrice ->
                        // Pass the selected store price to the parent
                        onAddToCart(product)
                    },
                    onProductClick = { onProductClick(product) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SearchResultsHeader(
    resultCount: Int,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "נמצאו $resultCount מוצרים",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Sort dropdown
        SortDropdown(
            currentSort = sortOption,
            onSortChange = onSortChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortDropdown(
    currentSort: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.extended.electricMint
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = null,
                    modifier = Modifier.size(SizingTokens.IconS)
                )
                Text(
                    text = getSortDisplayName(currentSort),
                    style = AppTextStyles.buttonText
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(SizingTokens.IconXS)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            getSortDisplayName(option),
                            style = AppTextStyles.hebrewBodyMedium
                        )
                    },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    },
                    leadingIcon = if (option == currentSort) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.extended.electricMint
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchSuggestionsContent(
    recentSearches: List<String>,
    popularSearches: List<String>,
    onSearchSuggestion: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(SpacingTokens.L),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.XL)
    ) {
        // Recent searches
        if (recentSearches.isNotEmpty()) {
            item {
                SearchSuggestionSection(
                    title = "חיפושים אחרונים",
                    icon = Icons.Default.History,
                    suggestions = recentSearches,
                    onSuggestionClick = onSearchSuggestion
                )
            }
        }

        // Popular searches
        if (popularSearches.isNotEmpty()) {
            item {
                SearchSuggestionSection(
                    title = "חיפושים פופולריים",
                    icon = Icons.Default.TrendingUp,
                    suggestions = popularSearches,
                    onSuggestionClick = onSearchSuggestion
                )
            }
        }

        // Categories
        item {
            PopularCategoriesSection(
                onCategoryClick = { category ->
                    onSearchSuggestion(category)
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchSuggestionSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        // Section header
        Row(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.extended.electricMint,
                modifier = Modifier.size(SizingTokens.IconS)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Suggestion chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            suggestions.forEach { suggestion ->
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = {
                        Text(
                            suggestion,
                            style = AppTextStyles.chipText
                        )
                    },
                    shape = GlassmorphicShapes.Chip,
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.extended.borderDefault
                    )
                )
            }
        }
    }
}

@Composable
private fun PopularCategoriesSection(
    onCategoryClick: (String) -> Unit
) {
    val categories = listOf(
        "חלב ומוצריו" to Icons.Default.LocalDrink,
        "פירות וירקות" to Icons.Default.Eco,
        "לחם ומאפים" to Icons.Default.Cookie,
        "בשר ודגים" to Icons.Default.Restaurant,
        "מוצרי ניקיון" to Icons.Default.CleaningServices,
        "חטיפים" to Icons.Default.Cookie
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        Text(
            text = "קטגוריות פופולריות",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            items(categories) { (category, icon) ->
                CategoryCard(
                    category = category,
                    icon = icon,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryCard(
    category: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.extended.surfaceGlass
        ),
        shape = GlassmorphicShapes.GlassCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingTokens.M),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(SizingTokens.IconL),
                tint = MaterialTheme.colorScheme.extended.electricMint
            )
            Spacer(modifier = Modifier.height(SpacingTokens.S))
            Text(
                text = category,
                style = AppTextStyles.caption,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.extended.electricMint,
                strokeWidth = 3.dp
            )
            Text(
                text = "מחפש מוצרים...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper function for sort display names
private fun getSortDisplayName(sortOption: SortOption): String {
    return when (sortOption) {
        SortOption.RELEVANCE -> "רלוונטיות"
        SortOption.PRICE_LOW_TO_HIGH -> "מחיר: נמוך לגבוה"
        SortOption.PRICE_HIGH_TO_LOW -> "מחיר: גבוה לנמוך"
        SortOption.NAME_A_TO_Z -> "שם: א-ת"
        SortOption.NAME_Z_TO_A -> "שם: ת-א"
        SortOption.SAVINGS_HIGH_TO_LOW -> "חיסכון מרבי"
    }
}