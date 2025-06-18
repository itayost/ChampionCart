package com.example.championcart.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

/**
 * Search Screen with Electric Harmony Design
 * Supports product search, filtering, and sorting
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onProductClick: (GroupedProduct) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()

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
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background gradient
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
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Search Header
            SearchHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                onSearch = {
                    viewModel.searchProducts(searchQuery)
                    keyboardController?.hide()
                },
                onBack = onNavigateBack,
                focusRequester = searchFocusRequester,
                modifier = Modifier.padding(SpacingTokens.L)
            )

            // Filter Section
            SearchFilterSection(
                selectedCity = selectedCity ?: "Tel Aviv",
                availableCities = uiState.availableCities,
                onCityChange = viewModel::selectCity,
                showIdenticalOnly = uiState.showIdenticalOnly,
                onToggleIdenticalOnly = viewModel::toggleIdenticalOnly,
                modifier = Modifier.padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.S)
            )

            // Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading -> {
                        LoadingContent(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.error != null -> {
                        EmptyState(
                            type = EmptyStateType.NETWORK_ERROR,
                            title = uiState.error,
                            actionLabel = "נסה שוב",
                            onAction = viewModel::retry,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(SpacingTokens.XL)
                        )
                    }

                    uiState.hasSearched && uiState.groupedProducts.isEmpty() -> {
                        EmptyState(
                            type = EmptyStateType.NO_RESULTS,
                            title = "לא נמצאו תוצאות עבור \"${searchQuery}\"",
                            actionLabel = "נקה חיפוש",
                            onAction = viewModel::clearResults,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(SpacingTokens.XL)
                        )
                    }

                    uiState.groupedProducts.isNotEmpty() -> {
                        SearchResultsContent(
                            products = uiState.groupedProducts,
                            sortOption = uiState.sortOption,
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
                            recentSearches = uiState.recentSearches,
                            popularSearches = uiState.popularSearches,
                            onSearchSuggestion = { query ->
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.searchFromSuggestion(query)
                            }
                        )
                    }
                }
            }
        }

        // Added to cart snackbar
        if (uiState.showAddedToCart) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(SpacingTokens.L)
                    .padding(bottom = 80.dp),
                action = {
                    TextButton(onClick = viewModel::dismissAddedToCart) {
                        Text("סגור")
                    }
                },
                containerColor = MaterialTheme.colorScheme.extended.cosmicPurple,
                contentColor = MaterialTheme.colorScheme.surface
            ) {
                Text("${uiState.lastAddedProduct} נוסף לעגלה")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        GlassmorphicIconButton(
            onClick = onBack,
            icon = Icons.Default.ArrowBack
        )

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            placeholder = {
                Text("חפש מוצרים...")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "נקה",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.extended.electricMint,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            shape = MaterialTheme.shapes.large
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchFilterSection(
    selectedCity: String,
    availableCities: List<String>,
    onCityChange: (String) -> Unit,
    showIdenticalOnly: Boolean,
    onToggleIdenticalOnly: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // City selector
        var expandedCity by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedCity,
            onExpandedChange = { expandedCity = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedCity,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity)
                },
                modifier = Modifier.menuAnchor(),
                label = { Text("עיר") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.extended.electricMint
                )
            )

            ExposedDropdownMenu(
                expanded = expandedCity,
                onDismissRequest = { expandedCity = false }
            ) {
                availableCities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            onCityChange(city)
                            expandedCity = false
                        }
                    )
                }
            }
        }

        // Identical only toggle
        FilterChip(
            selected = showIdenticalOnly,
            onClick = onToggleIdenticalOnly,
            label = { Text("זהים בלבד") },
            leadingIcon = if (showIdenticalOnly) {
                {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null
        )
    }
}

@Composable
private fun SearchResultsContent(
    products: List<GroupedProduct>,
    sortOption: String,
    onSortChange: (String) -> Unit,
    onProductClick: (GroupedProduct) -> Unit,
    onAddToCart: (GroupedProduct) -> Unit
) {
    Column {
        // Results header with sort
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${products.size} מוצרים נמצאו",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // Sort dropdown
            var expandedSort by remember { mutableStateOf(false) }

            Box {
                TextButton(
                    onClick = { expandedSort = true }
                ) {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(getSortLabel(sortOption))
                }

                DropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { expandedSort = false }
                ) {
                    listOf(
                        "price_low_high" to "מחיר: נמוך לגבוה",
                        "price_high_low" to "מחיר: גבוה לנמוך",
                        "name_a_z" to "שם: א-ת",
                        "name_z_a" to "שם: ת-א"
                    ).forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onSortChange(value)
                                expandedSort = false
                            }
                        )
                    }
                }
            }
        }

        // Results list
        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = SpacingTokens.L,
                vertical = SpacingTokens.L
            ),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onAddToCart = { onAddToCart(product) },
                    onFavoriteToggle = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProductClick(product) },
                    onProductClick = TODO()
                )
            }
        }
    }
}

@Composable
private fun SearchSuggestionsContent(
    recentSearches: List<String>,
    popularSearches: List<String>,
    onSearchSuggestion: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(SpacingTokens.L),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
    ) {
        // Recent searches
        if (recentSearches.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = SpacingTokens.M),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.extended.electricMint,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "חיפושים אחרונים",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    recentSearches.forEach { search ->
                        SuggestionItem(
                            text = search,
                            onClick = { onSearchSuggestion(search) }
                        )
                    }
                }
            }
        }

        // Popular searches
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SpacingTokens.M),
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.extended.electricMint,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "חיפושים פופולריים",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                popularSearches.forEach { search ->
                    SuggestionItem(
                        text = search,
                        onClick = { onSearchSuggestion(search) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    text: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = SpacingTokens.S),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.M),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                Icons.Default.NorthWest,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.extended.electricMint
        )
    }
}

private fun getSortLabel(sortOption: String): String {
    return when (sortOption) {
        "price_low_high" -> "מחיר: נמוך לגבוה"
        "price_high_low" -> "מחיר: גבוה לנמוך"
        "name_a_z" -> "שם: א-ת"
        "name_z_a" -> "שם: ת-א"
        else -> "מיין לפי"
    }
}