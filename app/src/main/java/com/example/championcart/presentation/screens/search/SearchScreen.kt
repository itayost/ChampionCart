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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.SortOption
import com.example.championcart.domain.models.StorePrice
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
                modifier = Modifier.padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.S)
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
                            type = EmptyStateType.NETWORK_ERROR,
                            title = state.error,
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
                            title = "לא נמצאו תוצאות עבור \"${state.searchQuery}\"",
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
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "חזור",
                tint = MaterialTheme.colorScheme.onSurface
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
            shape = GlassmorphicShapes.SearchField,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.extended.electricMint,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchFilterSection(
    selectedCity: String,
    onCityChange: (String) -> Unit,
    showIdenticalOnly: Boolean,
    onToggleIdenticalOnly: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCityDialog by remember { mutableStateOf(false) }
    val cities = listOf("תל אביב", "ירושלים", "חיפה", "באר שבע", "ראשון לציון", "פתח תקווה")

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        // City selector
        FilterChip(
            selected = false,
            onClick = { showCityDialog = true },
            label = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.extended.electricMint
                    )
                    Text(selectedCity)
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            shape = GlassmorphicShapes.Chip,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.surface,
                labelColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // Identical products filter
        FilterChip(
            selected = showIdenticalOnly,
            onClick = onToggleIdenticalOnly,
            label = {
                Text("מוצרים זהים בלבד")
            },
            leadingIcon = if (showIdenticalOnly) {
                {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null,
            shape = GlassmorphicShapes.Chip,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.extended.electricMint,
                selectedLabelColor = Color.White
            )
        )
    }

    // City selection dialog
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("בחר עיר") },
            text = {
                LazyColumn {
                    items(cities) { city ->
                        TextButton(
                            onClick = {
                                onCityChange(city)
                                showCityDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = city,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("ביטול")
                }
            }
        )
    }
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
            contentPadding = PaddingValues(
                horizontal = SpacingTokens.L,
                vertical = SpacingTokens.M
            ),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            items(products) { product ->
                ListProductCard(
                    product = product,
                    onAddToCart = { storePrice ->
                        onAddToCart(product)
                    },
                    onProductClick = { onProductClick(product) },
                    onFavoriteToggle = {
                        // TODO: Implement favorite toggle
                    },
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
    var showSortMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.S),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "נמצאו $resultCount מוצרים",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Sort button
        TextButton(
            onClick = { showSortMenu = true }
        ) {
            Icon(
                Icons.Default.Sort,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = getSortOptionText(sortOption),
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Sort dropdown menu
        DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
        ) {
            SortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(getSortOptionText(option)) },
                    onClick = {
                        onSortChange(option)
                        showSortMenu = false
                    },
                    leadingIcon = if (option == sortOption) {
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

        // Search tips
        item {
            SearchTipsCard()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchSuggestionSection(
    title: String,
    icon: ImageVector,
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    label = { Text(suggestion) },
                    shape = GlassmorphicShapes.Chip,
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
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
        "לחם ומאפים" to Icons.Default.BakeryDining,
        "בשר ודגים" to Icons.Default.Restaurant,
        "פירות וירקות" to Icons.Default.Eco,
        "משקאות" to Icons.Default.LocalBar,
        "חטיפים" to Icons.Default.Cookie
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        Text(
            text = "קטגוריות פופולריות",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(100.dp),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.extended.electricMint
            )
            Spacer(modifier = Modifier.height(SpacingTokens.S))
            Text(
                text = category,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun SearchTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.extended.info.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.extended.info,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "טיפים לחיפוש",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
            ) {
                SearchTipItem("🔍 השתמש בעברית לתוצאות טובות יותר")
                SearchTipItem("📦 נסה מונחים כלליים כמו \"חלב\" או \"לחם\"")
                SearchTipItem("🏷️ שמות מותגים עובדים מצוין!")
                SearchTipItem("💡 אנחנו מחפשים ברמי לוי, שופרסל ועוד")
            }
        }
    }
}

@Composable
private fun SearchTipItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.extended.electricMint
        )
        Text(
            text = "מחפש מוצרים...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getSortOptionText(sortOption: SortOption): String {
    return when (sortOption) {
        SortOption.RELEVANCE -> "רלוונטיות"
        SortOption.PRICE_LOW_TO_HIGH -> "מחיר: נמוך לגבוה"
        SortOption.PRICE_HIGH_TO_LOW -> "מחיר: גבוה לנמוך"
        SortOption.NAME_A_TO_Z -> "שם: א-ת"
        SortOption.NAME_Z_TO_A -> "שם: ת-א"
        SortOption.SAVINGS_HIGH_TO_LOW -> "חיסכון: גבוה לנמוך"
    }
}