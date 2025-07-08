package com.example.championcart.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.example.championcart.domain.models.PriceLevel as DomainPriceLevel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
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

    // Focus search bar on launch
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            ChampionCartTopBar(
                title = null, // No title, using custom search bar
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "חזור"
                        )
                    }
                },
                actions = listOf(
                    TopBarAction(
                        icon = Icons.Rounded.FilterList,
                        contentDescription = "סנן",
                        onClick = { /* TODO: Implement filters */ }
                    )
                )
            ) {
                // Custom search bar in the title area
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onSearch = {
                        focusManager.clearFocus()
                        viewModel.onSearch()
                    },
                    placeholder = "חפש מוצרים, מותגים או ברקודים...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.m)
                        .focusRequester(focusRequester),
                    enabled = !uiState.isSearching
                )
            }
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
    onSortOptionSelected: (SortOption) -> Unit
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
                .padding(horizontal = Spacing.l, vertical = Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$resultCount מוצרים נמצאו",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Box {
                // Custom sort button with icon
                Surface(
                    onClick = { showSortMenu = true },
                    modifier = Modifier.clip(Shapes.button),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.m, vertical = Spacing.s),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Sort,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = BrandColors.ElectricMint
                        )
                        Text(
                            text = selectedSortOption.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            color = BrandColors.ElectricMint
                        )
                    }
                }

                PopupMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    items = SortOption.values().map { option ->
                        PopupMenuItem(
                            label = option.displayName,
                            icon = if (option == selectedSortOption) Icons.Rounded.Check else null,
                            onClick = { onSortOptionSelected(option) }
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ProductComparisonCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.l)
        ) {
            // Product Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Savings Badge
                val maxPrice = product.stores.maxOfOrNull { it.price } ?: product.bestPrice
                val savings = maxPrice - product.bestPrice
                if (savings > 0) {
                    ChampionChip(
                        text = "חסכון ₪${String.format("%.2f", savings)}",
                        selected = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            // Price Comparison List
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                product.stores.sortedBy { it.price }.forEachIndexed { index, storePrice ->
                    StorePriceRow(
                        store = storePrice,
                        isBest = index == 0,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            ChampionDivider(modifier = Modifier.padding(vertical = Spacing.m))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "המחיר הטוב ביותר:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₪${product.bestPrice}",
                        style = TextStyles.price,
                        color = PriceColors.Best,
                        fontWeight = FontWeight.Bold
                    )

                    PrimaryButton(
                        text = "הוסף",
                        onClick = onAddToCart,
                        icon = Icons.Rounded.AddShoppingCart,
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StorePriceRow(
    store: StorePrice,
    isBest: Boolean,
    modifier: Modifier = Modifier
) {
    val priceLevel = when (store.priceLevel) {
        DomainPriceLevel.BEST -> PriceLevel.Best
        DomainPriceLevel.MID -> PriceLevel.Mid
        DomainPriceLevel.HIGH -> PriceLevel.High
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.cardSmall)
            .background(
                if (isBest) {
                    PriceColors.Best.copy(alpha = 0.08f)
                } else {
                    Color.Transparent
                }
            )
            .padding(horizontal = Spacing.m, vertical = Spacing.s),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isBest) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "המחיר הטוב ביותר",
                    tint = PriceColors.Best,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = store.storeName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isBest) FontWeight.Medium else FontWeight.Normal
            )
        }

        Box(
            modifier = Modifier
                .priceGlass(priceLevel)
                .padding(horizontal = Spacing.m, vertical = Spacing.xs)
        ) {
            Text(
                text = "₪${store.price}",
                style = TextStyles.priceSmall,
                color = when (priceLevel) {
                    PriceLevel.Best -> PriceColors.Best
                    PriceLevel.Mid -> PriceColors.Mid
                    PriceLevel.High -> PriceColors.High
                },
                fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal
            )
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                text = "נקה הכל",
                onClick = onClearAll,
                color = SemanticColors.Error
            )
        }

        Spacer(modifier = Modifier.height(Spacing.m))

        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            searches.forEach { search ->
                ChampionListItem(
                    title = search,
                    leadingIcon = Icons.Rounded.History,
                    onClick = { onSearchClick(search) },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Rounded.NorthEast,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchTipsCard(
    modifier: Modifier = Modifier
) {
    InfoCard(
        message = "טיפים לחיפוש",
        icon = Icons.Rounded.Lightbulb,
        modifier = modifier,
        action = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier.padding(top = Spacing.s)
            ) {
                SearchTipItem("חפש לפי שם מוצר או מותג")
                SearchTipItem("סרוק ברקוד לתוצאות מדויקות")
                SearchTipItem("השתמש במסננים למציאת המחיר הטוב ביותר")
            }
        }
    )
}

@Composable
private fun SearchTipItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall,
            color = BrandColors.ElectricMint
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Extension to use ChampionCartTopBar with custom content
@Composable
private fun ChampionCartTopBar(
    title: String?,
    navigationIcon: @Composable (() -> Unit)?,
    actions: List<TopBarAction>,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xs, vertical = Spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navigationIcon?.invoke()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Spacing.xs)
                ) {
                    content()
                }

                actions.forEach { action ->
                    IconButton(onClick = action.onClick) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.contentDescription,
                            tint = action.tint ?: MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}