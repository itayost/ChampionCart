package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.presentation.components.*
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    ChampionCartScreen(
        topBar = {
            HomeTopBar(
                selectedCity = uiState.selectedCity,
                onCityClick = { viewModel.showCitySelector() },
                onSearchClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    navController.navigate(Screen.Search.route)
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = SpacingTokens.XL),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
        ) {
            // Welcome section
            item {
                WelcomeCard(
                    userName = uiState.userName,
                    isGuest = uiState.isGuest,
                    onLoginClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }

            // Quick stats
            if (!uiState.isGuest && uiState.quickStats != null) {
                item {
                    QuickStatsSection(stats = uiState.quickStats)
                }
            }

            // Featured deals
            if (uiState.featuredDeals.isNotEmpty()) {
                item {
                    FeaturedDealsSection(
                        deals = uiState.featuredDeals,
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
            }

            // Popular products
            item {
                SectionHeader(
                    title = "פופולרי עכשיו",
                    subtitle = "המוצרים הכי נרכשים",
                    modifier = Modifier.padding(horizontal = SpacingTokens.L)
                )
            }

            items(uiState.popularProducts) { product ->
                ProductCard(
                    product = product,
                    onAddToCart = { storePrice ->
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.addToCart(product)
                    },
                    onFavoriteToggle = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        // TODO: Implement favorites
                    },
                    onProductClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.navigate(
                            Screen.ProductDetail.createRoute(product.itemCode)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpacingTokens.L)
                )
            }

            // Empty state
            if (!uiState.isLoading &&
                uiState.featuredDeals.isEmpty() &&
                uiState.popularProducts.isEmpty()) {
                item {
                    EmptyState(
                        type = EmptyStateType.NO_RESULTS,
                        title = "אין מוצרים",
                        subtitle = "לא נמצאו מוצרים באזור שנבחר",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpacingTokens.XL)
                    )
                }
            }

            // Loading state
            if (uiState.isLoading && uiState.popularProducts.isEmpty()) {
                items(5) {
                    LoadingProductCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SpacingTokens.L)
                    )
                }
            }
        }

        // Dialogs
        if (uiState.showCitySelector) {
            SelectionDialog(
                title = "בחר עיר",
                items = uiState.availableCities,
                selectedItem = uiState.selectedCity,
                onItemSelected = { city ->
                    viewModel.updateCity(city)
                },
                onDismiss = { viewModel.hideCitySelector() }
            )
        }

        // Error handling
        uiState.error?.let { error ->
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(error) {
                snackbarHostState.showSnackbar(
                    message = error,
                    actionLabel = "נסה שוב",
                    duration = SnackbarDuration.Long
                )
                viewModel.clearError()
            }

            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    selectedCity: String,
    onCityClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    ChampionCartTopBar(
        title = selectedCity,
        showBackButton = false,
        onBackClick = {},
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "חיפוש",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
private fun WelcomeCard(
    userName: String,
    isGuest: Boolean,
    onLoginClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L)
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.XL),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            Text(
                text = if (isGuest) "שלום אורח" else "שלום $userName",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "מה תרצה לקנות היום?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isGuest) {
                PrimaryButton(
                    text = "התחבר כדי לשמור עגלות",
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = Icons.Default.Login
                )
            }
        }
    }
}

@Composable
private fun QuickStatsSection(stats: QuickStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        QuickStatCard(
            title = "נחסך החודש",
            value = "₪${stats.savedThisMonth}",
            icon = Icons.Default.Savings,
            modifier = Modifier.weight(1f)
        )

        QuickStatCard(
            title = "עגלות שמורות",
            value = stats.savedCarts.toString(),
            icon = Icons.Default.ShoppingCart,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        onClick = null // Make it non-clickable
    ) {
        Row(
            modifier = Modifier.padding(SpacingTokens.M),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.extended.electricMint,
                    modifier = Modifier.size(SizingTokens.IconS)
                )
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.extended.electricMint
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FeaturedDealsSection(
    deals: List<GroupedProduct>,
    onProductClick: (GroupedProduct) -> Unit,
    onAddToCart: (GroupedProduct) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        SectionHeader(
            title = "מבצעי היום",
            subtitle = "החיסכון הכי גדול",
            modifier = Modifier.padding(horizontal = SpacingTokens.L),
            action = {
                TextButton(
                    onClick = { /* Navigate to all deals */ },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.extended.electricMint
                    )
                ) {
                    Text("ראה הכל")
                }
            }
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
            contentPadding = PaddingValues(horizontal = SpacingTokens.L)
        ) {
            items(deals) { product ->
                CompactProductCard(
                    product = product,
                    onProductClick = { onProductClick(product) },
                    onAddToCart = { storePrice -> onAddToCart(product) },
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingProductCard(
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(120.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }
    }
}

// Data class for quick stats
data class QuickStats(
    val savedThisMonth: Double,
    val savedCarts: Int
)