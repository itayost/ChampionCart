package com.example.championcart.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.common.CategoryCard
import com.example.championcart.presentation.components.common.ChampionBadge
import com.example.championcart.presentation.components.common.ChampionChip
import com.example.championcart.presentation.components.common.ChampionSnackbar
import com.example.championcart.presentation.components.common.ChampionTopBar
import com.example.championcart.presentation.components.common.CitySelectionBottomSheet
import com.example.championcart.presentation.components.common.EmptySearchState
import com.example.championcart.presentation.components.common.GlassCard
import com.example.championcart.presentation.components.common.LoadingIndicator
import com.example.championcart.presentation.components.common.ProductCard
import com.example.championcart.presentation.components.common.SearchBar
import com.example.championcart.presentation.components.common.StoreCard
import com.example.championcart.presentation.components.common.TextButton
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.Padding
import com.example.championcart.ui.theme.PriceLevel
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Size
import com.example.championcart.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToSearch: (String) -> Unit,  // Now accepts String parameter for search query

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

    Scaffold(
        topBar = {
            ChampionTopBar(
                title = "סל ניצחון",
                actions = {
                    // City selector
                    ChampionChip(
                        text = uiState.selectedCity,
                        onClick = { showCitySelection = true },
                        leadingIcon = Icons.Rounded.LocationOn,
                        selected = true
                    )

                    Spacer(modifier = Modifier.width(Spacing.s))

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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            // Hero Section with Search
            item {
                HeroSection(
                    userName = if (uiState.isGuest) "אורח" else uiState.userName,
                    selectedCity = uiState.selectedCity,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onSearch = {
                        // Navigate to search screen with query
                        if (searchQuery.isNotEmpty()) {
                            onNavigateToSearch(searchQuery)
                        }
                    }
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
                            onNavigateToSearch(search)  // Pass the search query
                        }
                    )
                }
            }

            // Featured Products (show only when not searching)
            item {
                SectionHeader(
                    title = "מוצרים פופולריים",
                    modifier = Modifier.padding(horizontal = Spacing.l)
                )
            }

            item {
                when {
                    uiState.isFeaturedLoading -> {
                        // Show loading indicator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    }
                    uiState.featuredProducts.isEmpty() -> {
                        // Show empty state after loading completes
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "אין מוצרים פופולריים כרגע",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        // Show products
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
            viewModel.requestLocationBasedCity()
        },
        onDismiss = {
            showCitySelection = false
            viewModel.clearLocationError()
            viewModel.clearLocationSuccess()
        },
        isDetectingLocation = uiState.isDetectingLocation,
        locationError = uiState.locationError,
        locationDetectionSuccess = uiState.locationDetectionSuccess
    )
}

@Composable
private fun HeroSection(
    userName: String,
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