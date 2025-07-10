package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import com.example.championcart.domain.models.Product
import com.example.championcart.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCitySelection by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            ChampionTopBar(
                title = "ChampionCart",
                actions = {
                    // Location button
                    IconButton(
                        onClick = { showCitySelection = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = "בחר עיר",
                            tint = BrandColors.ElectricMint
                        )
                    }

                    // Notifications button (optional)
                    IconButton(
                        onClick = { /* TODO: Navigate to notifications */ }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = "התראות"
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    )
                ),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + Size.bottomNavHeight + Spacing.xl,
                start = 0.dp,
                end = 0.dp
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            // Hero Section with Greeting
            item {
                HeroSection(
                    userName = uiState.userName,
                    totalSavings = uiState.totalSavings,
                    selectedCity = uiState.selectedCity,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onSearch = {
                        // Navigate to search screen instead of searching in-place
                        onNavigateToSearch()
                    }
                )
            }

            // Quick Actions
            item {
                QuickActionsSection(
                    cartItemCount = uiState.cartItemCount,
                    onScanClick = onNavigateToScan,
                    onCartClick = onNavigateToCart,
                    onDealsClick = { /* TODO: Navigate to deals */ },
                    onListsClick = { /* TODO: Navigate to saved lists */ }
                )
            }

            // Recent Searches
            if (uiState.recentSearches.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "חיפושים אחרונים",
                        action = {
                            TextButton(
                                text = "נקה",
                                onClick = { viewModel.onClearRecentSearches() }
                            )
                        },
                        modifier = Modifier.padding(horizontal = Spacing.l)
                    )
                }

                item {
                    RecentSearchesSection(
                        searches = uiState.recentSearches,
                        onSearchClick = { search ->
                            viewModel.onRecentSearchClick(search)
                            onNavigateToSearch()
                        }
                    )
                }
            }

            // Search Results - Using StoreCard as product display
            if (uiState.searchResults.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "תוצאות חיפוש",
                        modifier = Modifier.padding(horizontal = Spacing.l)
                    )
                }

                items(
                    items = uiState.searchResults,
                    key = { it.id }
                ) { product ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        // Using StoreCard to display product with best price info
                        StoreCard(
                            storeName = product.name,
                            totalPrice = "₪${product.bestPrice}",
                            itemCount = product.stores.size,
                            distance = product.bestStore,
                            onClick = {
                                viewModel.onProductClick(product)
                                onNavigateToProduct(product.id)
                            },
                            modifier = Modifier.padding(horizontal = Spacing.l),
                            isRecommended = true
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.s))
                }
            }

            // Featured Products
            if (uiState.featuredProducts.isNotEmpty() && uiState.searchResults.isEmpty()) {
                item {
                    SectionHeader(
                        title = "מוצרים פופולריים",
                        modifier = Modifier.padding(horizontal = Spacing.l)
                    )
                }

                item {
                    if (uiState.isFeaturedLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                            contentPadding = PaddingValues(horizontal = Spacing.l)
                        ) {
                            items(
                                items = uiState.featuredProducts,
                                key = { it.id }
                            ) { product ->
                                ProductCard(
                                    name = product.name,
                                    imageUrl = product.imageUrl,
                                    price = "₪${product.bestPrice}",
                                    storeName = product.bestStore,
                                    priceLevel = PriceLevel.Best,
                                    onClick = {
                                        viewModel.onProductClick(product)
                                        onNavigateToProduct(product.id)
                                    },
                                    onAddToCart = { viewModel.onAddToCart(product) },
                                    modifier = Modifier.width(160.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Empty State if needed
            if (uiState.searchResults.isEmpty() &&
                uiState.featuredProducts.isEmpty() &&
                !uiState.isSearching &&
                !uiState.isFeaturedLoading) {
                item {
                    EmptySearchState(
                        query = "",
                        modifier = Modifier.padding(horizontal = Spacing.l, vertical = Spacing.xxl)
                    )
                }
            }
        }
    }

    // City Selection Bottom Sheet
    CitySelectionBottomSheet(
        visible = showCitySelection,
        selectedCity = uiState.selectedCity,
        cities = uiState.cities,
        onCitySelected = { city ->
            viewModel.onCitySelected(city)
            showCitySelection = false
        },
        onRequestLocation = {
            // TODO: Implement location permission and detection
            showCitySelection = false
        },
        onDismiss = { showCitySelection = false }
    )
}

@Composable
private fun HeroSection(
    userName: String,
    totalSavings: Double,
    selectedCity: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.l, vertical = Spacing.m)
    ) {
        Column(
            modifier = Modifier.padding(Padding.l)
        ) {
            // Greeting and City
            val greeting = when (java.time.LocalTime.now().hour) {
                in 6..11 -> "בוקר טוב"
                in 12..17 -> "צהריים טובים"
                in 18..21 -> "ערב טוב"
                else -> "לילה טוב"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$greeting, $userName!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "מחפש מחירים ב$selectedCity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Savings Badge
                if (totalSavings > 0) {
                    ChampionBadge(
                        count = totalSavings.toInt()
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.l))

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = onSearch,
                placeholder = "חפש מוצרים...",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    cartItemCount: Int,
    onScanClick: () -> Unit,
    onCartClick: () -> Unit,
    onDealsClick: () -> Unit,
    onListsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.l),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        CategoryCard(
            name = "עגלה",
            icon = Icons.Rounded.ShoppingCart,
            color = BrandColors.ElectricMint,
            onClick = onCartClick,
            modifier = Modifier.weight(1f)
        )

        CategoryCard(
            name = "סרוק",
            icon = Icons.Rounded.QrCodeScanner,
            color = BrandColors.CosmicPurple,
            onClick = onScanClick,
            modifier = Modifier.weight(1f)
        )

        CategoryCard(
            name = "רשימות",
            icon = Icons.Rounded.FormatListBulleted,
            color = BrandColors.NeonCoral,
            onClick = onListsClick,
            modifier = Modifier.weight(1f)
        )

        CategoryCard(
            name = "מבצעים",
            icon = Icons.Rounded.LocalOffer,
            color = SemanticColors.Warning,
            onClick = onDealsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RecentSearchesSection(
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        contentPadding = PaddingValues(horizontal = Spacing.l)
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

@Composable
private fun SectionHeader(
    title: String,
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        action?.invoke()
    }
}