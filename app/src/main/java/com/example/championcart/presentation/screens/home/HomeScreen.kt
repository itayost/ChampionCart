package com.example.championcart.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.presentation.ViewModelFactory
import com.example.championcart.presentation.components.CityIndicator
import com.example.championcart.presentation.components.rememberCitySelectionDialog
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.presentation.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val cartManager = remember { CartManager.getInstance(context) }

    val viewModel: HomeViewModel = remember {
        HomeViewModel(tokenManager, cartManager)
    }

    val uiState by viewModel.uiState.collectAsState()

    // City selection dialog
    val (currentCity, showCityDialog) = rememberCitySelectionDialog(
        tokenManager = tokenManager,
        onCitySelected = { city ->
            viewModel.onCityChange(city)
        }
    )

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${uiState.greeting}, ${uiState.userName}!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Champion Cart",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    CityIndicator(
                        city = uiState.selectedCity,
                        onClick = showCityDialog,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        // Let content go edge-to-edge
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    // Add navigation bar padding to bottom
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp
                )
            ) {
                // Quick Stats Section
                item {
                    QuickStatsSection(
                        quickStats = uiState.quickStats,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Quick Actions
                item {
                    QuickActionsSection(
                        onScanClick = { /* TODO: Implement barcode scanner */ },
                        onSearchClick = { navController.navigate(Screen.Search.route) },
                        onListsClick = { navController.navigate(Screen.Cart.route) },
                        onSavingsClick = { /* TODO: Navigate to savings */ },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Featured Deals
                if (uiState.featuredDeals.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Today's Best Deals",
                            onSeeAllClick = { /* TODO: Navigate to all deals */ },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    item {
                        FeaturedDealsCarousel(
                            deals = uiState.featuredDeals,
                            onDealClick = { /* TODO: Navigate to product */ }
                        )
                    }
                }

                // Recent Comparisons
                if (uiState.recentComparisons.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Recent Comparisons",
                            onSeeAllClick = { /* TODO: Navigate to history */ },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(uiState.recentComparisons) { comparison ->
                        RecentComparisonCard(
                            comparison = comparison,
                            onClick = { /* TODO: Navigate to product */ },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStatsSection(
    quickStats: QuickStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main savings card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = ComponentShapes.Card
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Total Saved This Month",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₪${String.format("%.2f", quickStats.totalSaved)}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${quickStats.savingsPercentage}% saved on average",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Secondary stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Items Tracked",
                value = quickStats.itemsTracked.toString(),
                icon = Icons.Default.Inventory,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Cheapest Today",
                value = quickStats.cheapestStore,
                icon = Icons.Default.Store,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = ComponentShapes.Card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onScanClick: () -> Unit,
    onSearchClick: () -> Unit,
    onListsClick: () -> Unit,
    onSavingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            icon = Icons.Default.QrCodeScanner,
            label = "Scan",
            onClick = onScanClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.Search,
            label = "Search",
            onClick = onSearchClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.ListAlt,
            label = "Lists",
            onClick = onListsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.Savings,
            label = "Savings",
            onClick = onSavingsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onSeeAllClick) {
            Text("See all")
        }
    }
}

@Composable
private fun FeaturedDealsCarousel(
    deals: List<FeaturedDeal>,
    onDealClick: (FeaturedDeal) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(deals) { deal ->
            DealCard(
                deal = deal,
                onClick = { onDealClick(deal) }
            )
        }
    }
}

@Composable
private fun DealCard(
    deal: FeaturedDeal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.bestDeal.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Placeholder for product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(ComponentShapes.Card)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = deal.productName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₪${String.format("%.2f", deal.discountedPrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.savings
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "₪${String.format("%.2f", deal.originalPrice)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = deal.storeName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = ComponentShapes.Badge,
                    color = MaterialTheme.extendedColors.bestDeal
                ) {
                    Text(
                        text = "-${deal.savingsPercentage.toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentComparisonCard(
    comparison: RecentComparison,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = ComponentShapes.Card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comparison.productName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "₪${String.format("%.2f", comparison.lowestPrice)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.extendedColors.priceLow,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = " - ₪${String.format("%.2f", comparison.highestPrice)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Best at ${comparison.bestStore} • ${comparison.comparedAt}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}