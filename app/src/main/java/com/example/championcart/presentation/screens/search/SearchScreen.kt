package com.example.championcart.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.presentation.components.products.ProductComparisonCard
import com.example.championcart.domain.models.PriceLevel as DomainPriceLevel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQuery: String = "",  // ADDED: New parameter for navigation query
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // ADDED: Handle initial query from navigation
    LaunchedEffect(initialQuery) {
        if (initialQuery.isNotEmpty() && viewModel.searchQuery.value.isEmpty()) {
            viewModel.onSearchQueryChange(initialQuery)
            viewModel.onSearch()
        }
    }

    // Show snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    // Show errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    // CHANGED: Focus search bar on launch only if no initial query
    LaunchedEffect(Unit) {
        if (initialQuery.isEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            // Custom top bar with integrated search
            TopAppBar(
                title = {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        onSearch = {
                            focusManager.clearFocus()
                            viewModel.onSearch()
                        },
                        placeholder = "חפש מוצרים...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        enabled = true // Always enabled
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "חזור"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    ChampionSnackbar(snackbarData = data)
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                bottom = Size.bottomNavHeight + Spacing.xl
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            // Search Results Header
            if (uiState.searchResults.isNotEmpty()) {
                item {
                    SearchResultsHeader(
                        resultCount = uiState.searchResults.size,
                        selectedSortOption = uiState.selectedSortOption,
                        onSortOptionSelected = viewModel::onSortOptionSelected
                    )
                }
            }

            // Loading State
            if (uiState.isSearching) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xxl),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }
            }

            // Search Results
            if (!uiState.isSearching && uiState.searchResults.isNotEmpty()) {
                items(
                    items = uiState.searchResults,
                    key = { it.id }
                ) { product ->
                    ProductComparisonCard(
                        product = product,
                        onClick = {
                            viewModel.onProductClick(product)
                            onNavigateToProduct(product.id)
                        },
                        onAddToCart = { viewModel.onAddToCart(product) },
                        modifier = Modifier.padding(horizontal = Spacing.l)
                    )
                }
            }

            // Empty State
            if (!uiState.isSearching && searchQuery.isNotEmpty() && uiState.searchResults.isEmpty()) {
                item {
                    EmptySearchState(
                        query = searchQuery,
                        modifier = Modifier.padding(Spacing.xl)
                    )
                }
            }

            // Suggestions & Recent Searches
            if (searchQuery.isEmpty() && uiState.showSuggestions) {
                // Recent Searches
                if (uiState.recentSearches.isNotEmpty()) {
                    item {
                        RecentSearchesSection(
                            searches = uiState.recentSearches,
                            onSearchClick = viewModel::onRecentSearchClick,
                            onClearAll = viewModel::onClearRecentSearches,
                            modifier = Modifier.padding(horizontal = Spacing.l)
                        )
                    }
                }

                // Search Tips
                item {
                    SearchTipsCard(
                        modifier = Modifier.padding(horizontal = Spacing.l)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultsHeader(
    resultCount: Int,
    selectedSortOption: SortOption,
    onSortOptionSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.l, vertical = Spacing.s),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "נמצאו $resultCount מוצרים",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SortDropdown(
            selectedOption = selectedSortOption,
            onOptionSelected = onSortOptionSelected
        )
    }
}

@Composable
private fun SortDropdown(
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                contentColor = BrandColors.ElectricMint
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Sort,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = selectedOption.displayName,
                style = MaterialTheme.typography.bodySmall
            )
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    leadingIcon = if (option == selectedOption) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = BrandColors.ElectricMint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun RecentSearchesSection(
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "חיפושים אחרונים",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            TextButton(onClick = onClearAll) {
                Text(
                    text = "נקה הכל",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.s))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            items(searches) { search ->
                ChampionChip(
                    text = search,
                    onClick = { onSearchClick(search) },
                    leadingIcon = Icons.Rounded.History
                )
            }
        }
    }
}

@Composable
private fun SearchTipsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.m)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lightbulb,
                    contentDescription = null,
                    tint = BrandColors.ElectricMint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.s))
                Text(
                    text = "טיפים לחיפוש",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(Spacing.s))

            val tips = listOf(
                "חפש לפי שם מוצר, מותג או ברקוד",
                "השתמש במילות מפתח כמו \"אורגני\" או \"ללא גלוטן\"",
                "נסה לחפש קטגוריות כמו \"חלב\" או \"לחם\""
            )

            tips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = Spacing.xs)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Spacing.s))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(Spacing.m))

        Text(
            text = "לא נמצאו תוצאות עבור \"$query\"",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.s))

        Text(
            text = "נסה לחפש במילים אחרות או בדוק את האיות",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}