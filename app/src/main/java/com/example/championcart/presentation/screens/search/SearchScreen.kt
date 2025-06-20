package com.example.championcart.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.presentation.components.*
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val haptics = LocalHapticFeedback.current

    var showSortDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    ChampionCartScreen(
        topBar = {
            // Search bar as top bar
            ChampionCartSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = { viewModel.searchProducts(searchQuery) },
                placeholder = "חפש מוצרים...",
                onBackClick = { navController.popBackStack() },
                showVoiceSearch = false // Can be enabled later
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filters and city selector
            SearchFiltersRow(
                selectedCity = uiState.selectedCity ?: "בחר עיר",
                showIdenticalOnly = uiState.showIdenticalOnly,
                onCityClick = { showFilterDialog = true },
                onToggleIdenticalOnly = viewModel::toggleIdenticalOnly,
                onSortClick = { showSortDialog = true },
                sortOption = getSortDisplayName(uiState.sortOption)
            )

            // Content
            when {
                // Initial state - show suggestions
                !uiState.hasSearched && searchQuery.isEmpty() -> {
                    InitialSearchContent(
                        recentSearches = uiState.recentSearches,
                        popularSearches = uiState.popularSearches,
                        onSearchClick = { query ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.searchFromSuggestion(query)
                        }
                    )
                }

                // Loading state
                uiState.isSearching -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator(size = 60.dp)
                    }
                }

                // No results
                uiState.hasSearched && uiState.groupedProducts.isEmpty() -> {
                    EmptySearchState(
                        query = searchQuery,
                        onClearSearch = {
                            viewModel.updateSearchQuery("")
                            viewModel.clearResults()
                        }
                    )
                }

                // Results
                uiState.groupedProducts.isNotEmpty() -> {
                    SearchResultsList(
                        products = uiState.groupedProducts,
                        resultCount = uiState.groupedProducts.size,
                        onProductClick = { product ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            navController.navigate(
                                Screen.ProductDetail.createRoute(product.itemCode)
                            )
                        },
                        onAddToCart = { product ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addToCart(product)
                        }
                    )
                }

                // Error state
                uiState.error != null -> {
                    EmptyState(
                        type = EmptyStateType.NETWORK_ERROR,
                        title = "שגיאה בחיפוש",
                        subtitle = uiState.error,
                        actionLabel = "נסה שוב",
                        onAction = viewModel::retry,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpacingTokens.XL)
                    )
                }
            }
        }

        // Dialogs
        if (showSortDialog) {
            SortOptionsDialog(
                currentSort = uiState.sortOption,
                onSortSelected = { sortOption ->
                    viewModel.updateSort(sortOption)
                    showSortDialog = false
                },
                onDismiss = { showSortDialog = false }
            )
        }

        if (showFilterDialog) {
            SelectionDialog(
                title = "בחר עיר",
                items = uiState.availableCities,
                selectedItem = uiState.selectedCity,
                onItemSelected = { city ->
                    viewModel.selectCity(city)
                    showFilterDialog = false
                },
                onDismiss = { showFilterDialog = false }
            )
        }

        // Snackbar for add to cart confirmation
        if (uiState.showAddedToCart) {
            LaunchedEffect(uiState.lastAddedProduct) {
                // Auto dismiss after 2 seconds
                kotlinx.coroutines.delay(2000)
                viewModel.dismissAddedToCart()
            }

            Snackbar(
                modifier = Modifier.padding(SpacingTokens.L),
                action = {
                    TextButton(onClick = viewModel::dismissAddedToCart) {
                        Text("סגור")
                    }
                }
            ) {
                Text("${uiState.lastAddedProduct} נוסף לעגלה")
            }
        }
    }
}

@Composable
private fun SearchFiltersRow(
    selectedCity: String,
    showIdenticalOnly: Boolean,
    onCityClick: () -> Unit,
    onToggleIdenticalOnly: () -> Unit,
    onSortClick: () -> Unit,
    sortOption: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // City selector chip
        FilterChip(
            selected = false,
            onClick = onCityClick,
            label = { Text(selectedCity) },
            leadingIcon = {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            modifier = Modifier.weight(1f)
        )

        // Identical products toggle
        FilterChip(
            selected = showIdenticalOnly,
            onClick = onToggleIdenticalOnly,
            label = { Text("זהים בלבד") }
        )

        // Sort button
        AssistChip(
            onClick = onSortClick,
            label = { Text(sortOption) },
            leadingIcon = {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}

@Composable
private fun InitialSearchContent(
    recentSearches: List<String>,
    popularSearches: List<String>,
    onSearchClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(SpacingTokens.L),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
    ) {
        // Recent searches
        if (recentSearches.isNotEmpty()) {
            item {
                Column {
                    SectionHeader(
                        title = "חיפושים אחרונים",
                        modifier = Modifier.padding(bottom = SpacingTokens.M)
                    )

                    SearchSuggestions(
                        suggestions = recentSearches,
                        onSuggestionClick = onSearchClick
                    )
                }
            }
        }

        // Popular searches
        item {
            Column {
                SectionHeader(
                    title = "חיפושים פופולריים",
                    modifier = Modifier.padding(bottom = SpacingTokens.M)
                )

                SearchSuggestions(
                    suggestions = popularSearches,
                    onSuggestionClick = onSearchClick
                )
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    products: List<GroupedProduct>,
    resultCount: Int,
    onProductClick: (GroupedProduct) -> Unit,
    onAddToCart: (GroupedProduct) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(SpacingTokens.L),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        // Results header
        item {
        }

        // Product list
        items(
            items = products,
            key = { it.itemCode }
        ) { product ->
            ProductCard(
                product = product,
                onProductClick = { onProductClick(product) },
                onAddToCart = { onAddToCart(product) },
                onFavoriteToggle = { /* TODO: Implement favorites */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SortOptionsDialog(
    currentSort: String,
    onSortSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sortOptions = listOf(
        "price_low_high" to "מחיר - נמוך לגבוה",
        "price_high_low" to "מחיר - גבוה לנמוך",
        "name_a_z" to "שם - א' עד ת'",
        "name_z_a" to "שם - ת' עד א'"
    )

    ChampionCartAlertDialog(
        title = "מיין לפי",
        text = null,
        confirmButtonText = "בחר",
        dismissButtonText = "ביטול",
        onConfirm = { /* Handled by radio selection */ },
        onDismiss = onDismiss
    ) {
        Column {
            sortOptions.forEach { (value, label) ->
                RadioButtonListItem(
                    title = label,
                    selected = currentSort == value,
                    onClick = {
                        onSortSelected(value)
                        onDismiss()
                    }
                )
            }
        }
    }
}

private fun getSortDisplayName(sortOption: String): String {
    return when (sortOption) {
        "price_low_high" -> "מחיר ↑"
        "price_high_low" -> "מחיר ↓"
        "name_a_z" -> "שם א-ת"
        "name_z_a" -> "שם ת-א"
        else -> "מיון"
    }
}