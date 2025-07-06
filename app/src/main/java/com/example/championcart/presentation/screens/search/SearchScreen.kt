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
            SearchTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onSearch = {
                    focusManager.clearFocus()
                    viewModel.onSearch()
                },
                onNavigateBack = onNavigateBack,
                focusRequester = focusRequester
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
            contentPadding = PaddingValues(bottom = Spacing.xxl),
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
                        CircularProgressIndicator()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    focusRequester: FocusRequester
) {
    TopAppBar(
        title = {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = onSearch,
                placeholder = "חפש מוצרים, מותגים או ברקודים...",
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "חזור"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
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
                TextButton(
                    onClick = { showSortMenu = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = BrandColors.ElectricMint
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Sort,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(selectedSortOption.displayName)
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                onSortOptionSelected(option)
                                showSortMenu = false
                            },
                            leadingIcon = if (option == selectedSortOption) {
                                {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = BrandColors.ElectricMint
                                    )
                                }
                            } else null
                        )
                    }
                }
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
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                val savings = ((maxPrice - product.bestPrice) / maxPrice * 100).toInt()
                if (savings > 0) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = SemanticColors.Success.copy(alpha = 0.1f),
                                shape = Shapes.badge
                            )
                            .padding(horizontal = Spacing.m, vertical = Spacing.xs)
                    ) {
                        Text(
                            text = "חסוך $savings%",
                            style = MaterialTheme.typography.labelMedium,
                            color = SemanticColors.Success,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            // Price Range Indicator
            PriceRangeIndicator(
                bestPrice = product.bestPrice,
                worstPrice = product.stores.maxOfOrNull { it.price } ?: product.bestPrice,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            // Store Prices
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                product.stores.forEach { storePrice ->
                    StorePriceRow(
                        store = storePrice,
                        isBest = storePrice.price == product.bestPrice
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    shape = Shapes.button,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text("פרטים נוספים")
                }

                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.weight(1f),
                    shape = Shapes.button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandColors.ElectricMint
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("הוסף לעגלה")
                }
            }
        }
    }
}

@Composable
private fun PriceRangeIndicator(
    bestPrice: Double,
    worstPrice: Double,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "הכי זול",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₪${String.format("%.2f", bestPrice)}",
                    style = TextStyles.priceSmall,
                    color = PriceColors.Best,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "הכי יקר",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₪${String.format("%.2f", worstPrice)}",
                    style = TextStyles.priceSmall,
                    color = PriceColors.High
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Price range bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(Shapes.badge)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val percentage = if (worstPrice > bestPrice) {
                ((worstPrice - bestPrice) / worstPrice).toFloat()
            } else 0f

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f - percentage)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                PriceColors.Best,
                                PriceColors.Mid
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun StorePriceRow(
    store: StorePrice,
    isBest: Boolean
) {
    Row(
        modifier = Modifier
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
            // Store indicator dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = when (store.storeName.lowercase()) {
                            "שופרסל", "shufersal" -> Color(0xFF0066CC)
                            "ויקטורי", "victory" -> Color(0xFFCC0000)
                            "רמי לוי", "rami levy" -> Color(0xFFFF6600)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )

            Text(
                text = store.storeName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal
            )

            if (isBest) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "הכי זול",
                    modifier = Modifier.size(16.dp),
                    tint = PriceColors.Best
                )
            }
        }

        Text(
            text = "₪${String.format("%.2f", store.price)}",
            style = TextStyles.priceSmall,
            color = when (store.priceLevel) {
                DomainPriceLevel.BEST -> PriceColors.Best
                DomainPriceLevel.MID -> PriceColors.Mid
                DomainPriceLevel.HIGH -> PriceColors.High
            },
            fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal
        )
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
                onClick = onClearAll,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("נקה הכל")
            }
        }

        Spacer(modifier = Modifier.height(Spacing.m))

        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            searches.forEach { search ->
                Card(
                    onClick = { onSearchClick(search) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.card,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.m),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = search,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.NorthEast,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = BrandColors.ElectricMint.copy(alpha = 0.08f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = BrandColors.ElectricMint.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lightbulb,
                    contentDescription = null,
                    tint = BrandColors.ElectricMint,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "טיפים לחיפוש",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                TipRow("חפש לפי שם מוצר: \"חלב\", \"לחם\"")
                TipRow("חפש לפי מותג: \"תנובה\", \"אסם\"")
                TipRow("חפש לפי ברקוד למציאת מוצר מדויק")
                TipRow("השתמש במילות מפתח בעברית או באנגלית")
            }
        }
    }
}

@Composable
private fun TipRow(tip: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = BrandColors.ElectricMint
        )
        Text(
            text = tip,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        Icon(
            imageVector = Icons.Rounded.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "לא נמצאו תוצאות עבור",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "\"$query\"",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = BrandColors.ElectricMint
        )

        Text(
            text = "נסה לחפש עם מילות מפתח אחרות\nאו בדוק את האיות",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}