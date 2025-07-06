package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import com.example.championcart.domain.models.PriceLevel as DomainPriceLevel
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

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
            HomeTopBar(
                userName = uiState.userName,
                isGuest = uiState.isGuest,
                onProfileClick = onNavigateToProfile,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    ChampionSnackbar(snackbarData = data)
                }
            )
        },
        bottomBar = {
            HomeBottomBar(
                cartItemCount = uiState.cartItemCount,
                onCartClick = onNavigateToCart,
                onSearchClick = onNavigateToSearch
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
                bottom = paddingValues.calculateBottomPadding() + Spacing.xxl,
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
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onSearch = viewModel::onSearch
                )
            }

            // City Selection
            if (uiState.cities.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "בחר עיר",
                        modifier = Modifier.padding(horizontal = Spacing.l)
                    )
                }

                item {
                    if (uiState.isCitiesLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        CitySelector(
                            selectedCity = uiState.selectedCity,
                            cities = uiState.cities,
                            onCitySelected = viewModel::onCitySelected,
                            modifier = Modifier.padding(horizontal = Spacing.l)
                        )
                    }
                }
            }

            // Quick Actions
            item {
                QuickActionsSection(
                    cartItemCount = uiState.cartItemCount,
                    onScanClick = { /* TODO: Implement barcode scanning */ },
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
                                onClick = { /* Clear recent */ },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("נקה")
                            }
                        },
                        modifier = Modifier.padding(horizontal = Spacing.l)
                    )
                }

                item {
                    RecentSearchesSection(
                        searches = uiState.recentSearches,
                        onSearchClick = viewModel::onRecentSearchClick
                    )
                }
            }

            // Search Results
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
                        ProductListItem(
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
                            CircularProgressIndicator()
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
                                    modifier = Modifier.width(180.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Stats Section
            item {
                StatsSection(
                    totalSavings = uiState.totalSavings,
                    productsTracked = uiState.featuredProducts.size,
                    modifier = Modifier.padding(horizontal = Spacing.l)
                )
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    userName: String,
    isGuest: Boolean,
    onProfileClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = getGreeting(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isGuest) "אורח" else userName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "פרופיל",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun HeroSection(
    userName: String,
    totalSavings: Double,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(
                shape = Shapes.bottomSheet,
                elevation = 4.dp
            )
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // Savings Card
        AnimatedContent(
            targetState = totalSavings,
            transitionSpec = {
                slideInVertically { it } + fadeIn() with
                        slideOutVertically { -it } + fadeOut()
            }
        ) { savings ->
            if (savings > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.cardLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = BrandColors.ElectricMint.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = BrandColors.ElectricMint.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Padding.l),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "חסכת החודש",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₪${String.format("%.2f", savings)}",
                                style = TextStyles.priceLarge,
                                color = BrandColors.ElectricMint,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.Savings,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = BrandColors.ElectricMint
                        )
                    }
                }
            }
        }

        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = onSearch,
            placeholder = "חפש מוצרים, מותגים או ברקודים..."
        )
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
        QuickActionCard(
            icon = Icons.Rounded.ShoppingCart,
            label = "עגלה",
            backgroundColor = BrandColors.ElectricMint.copy(alpha = 0.1f),
            iconColor = BrandColors.ElectricMint,
            badge = if (cartItemCount > 0) cartItemCount else null,
            onClick = onCartClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            icon = Icons.Rounded.QrCodeScanner,
            label = "סרוק",
            backgroundColor = BrandColors.CosmicPurple.copy(alpha = 0.1f),
            iconColor = BrandColors.CosmicPurple,
            onClick = onScanClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            icon = Icons.Rounded.FormatListBulleted,
            label = "רשימות",
            backgroundColor = BrandColors.NeonCoral.copy(alpha = 0.1f),
            iconColor = BrandColors.NeonCoral,
            onClick = onListsClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            icon = Icons.Rounded.LocalOffer,
            label = "מבצעים",
            backgroundColor = SemanticColors.Warning.copy(alpha = 0.1f),
            iconColor = SemanticColors.Warning,
            onClick = onDealsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    backgroundColor: Color,
    iconColor: Color,
    badge: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(32.dp),
                        tint = iconColor
                    )
                    badge?.let {
                        Badge(
                            modifier = Modifier.align(Alignment.TopEnd),
                            containerColor = SemanticColors.Error
                        ) {
                            Text(
                                text = it.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = iconColor
                )
            }
        }
    }
}

@Composable
private fun RecentSearchesSection(
    searches: List<String>,
    onSearchClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        contentPadding = PaddingValues(horizontal = Spacing.l)
    ) {
        items(searches) { search ->
            Card(
                onClick = { onSearchClick(search) },
                shape = Shapes.chip,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = Spacing.m,
                        vertical = Spacing.s
                    ),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = search,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductListItem(
    product: com.example.championcart.domain.models.Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.m),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            // Product Image Placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(Shapes.card)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Product Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriceLevelIndicator(
                        priceLevel = when {
                            product.bestPrice == product.stores.minOfOrNull { it.price } -> PriceLevel.Best
                            product.bestPrice < product.stores.map { it.price }.average() -> PriceLevel.Mid
                            else -> PriceLevel.High
                        }
                    )
                    Text(
                        text = "₪${product.bestPrice}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (product.bestStore.isNotEmpty()) {
                    Text(
                        text = "הכי זול ב${product.bestStore}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SemanticColors.Success
                    )
                }
            }

            // Add to Cart Button
            IconButton(
                onClick = onAddToCart,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AddShoppingCart,
                    contentDescription = "הוסף לעגלה",
                    tint = BrandColors.ElectricMint
                )
            }
        }
    }
}

@Composable
private fun StatsSection(
    totalSavings: Double,
    productsTracked: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        StatCard(
            title = "חיסכון כולל",
            value = "₪${String.format("%.0f", totalSavings)}",
            icon = Icons.Rounded.Savings,
            trend = if (totalSavings > 0) 12.5f else null,
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "מוצרים במעקב",
            value = productsTracked.toString(),
            icon = Icons.Rounded.Visibility,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HomeBottomBar(
    cartItemCount: Int,
    onCartClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .glass(
                    shape = Shapes.bottomSheet,
                    elevation = 8.dp
                )
                .navigationBarsPadding()
                .padding(
                    horizontal = Spacing.l,
                    vertical = Spacing.m
                ),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Search FAB
            ExtendedFloatingActionButton(
                onClick = onSearchClick,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "חיפוש"
                )
                Spacer(modifier = Modifier.width(Spacing.s))
                Text("חפש מוצרים")
            }

            // Cart FAB
            ExtendedFloatingActionButton(
                onClick = onCartClick,
                containerColor = BrandColors.ElectricMint,
                contentColor = Color.White
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = SemanticColors.Error
                            ) {
                                Text(cartItemCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ShoppingCart,
                        contentDescription = "עגלה"
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.s))
                Text("העגלה שלי")
            }
        }
    }
}

    // Utility function for time-based greeting
    @Composable
    private fun getGreeting(): String {
        val hour = LocalTime.now().hour
        return when (hour) {
            in 5..11 -> "בוקר טוב"
            in 12..16 -> "צהריים טובים"
            in 17..20 -> "ערב טוב"
            else -> "לילה טוב"
        }
    }