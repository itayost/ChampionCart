package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.presentation.components.*
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*
import java.time.LocalTime

/**
 * Modern Home Screen with Electric Harmony Design
 * Fixed edge-to-edge layout with proper glass morphism
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current

    // Dynamic greeting
    val greeting = remember {
        val hour = LocalTime.now().hour
        when (hour) {
            in 5..11 -> "בוקר טוב"
            in 12..17 -> "צהריים טובים"
            in 18..21 -> "ערב טוב"
            else -> "לילה טוב"
        }
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
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.extended.surfaceGlass
                        )
                    )
                )
        )

        // Main content - Edge to edge
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            // Header Section
            item {
                HomeHeader(
                    greeting = greeting,
                    userName = uiState.userName,
                    onSearchClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.navigate(Screen.Search.route)
                    },
                    onNotificationClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        // TODO: Navigate to notifications
                    }
                )
            }

            // Stats Card
            item {
                StatsCard(
                    totalSavings = uiState.totalSavings,
                    modifier = Modifier.padding(horizontal = SpacingTokens.L)
                )
            }

            // Quick Actions
            item {
                QuickActionsRow(
                    onScanClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        // TODO: Navigate to scanner
                    },
                    onListClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        // TODO: Navigate to lists
                    },
                    onCompareClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.navigate(Screen.Cart.route)
                    }
                )
            }

            // Pull to refresh indicator
            if (uiState.isRefreshing) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpacingTokens.M),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.extended.electricMint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Categories Section
            if (uiState.categories.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "קטגוריות",
                        modifier = Modifier.padding(horizontal = SpacingTokens.L)
                    )
                }

                item {
                    CategoriesRow(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategoryClick = { category ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.selectCategory(category.name)
                        }
                    )
                }
            }

            // Featured Deals Section
            if (uiState.featuredDeals.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "מבצעים חמים 🔥",
                        action = "ראה הכל" to {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            // Navigate to deals
                        },
                        modifier = Modifier.padding(horizontal = SpacingTokens.L)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
                        contentPadding = PaddingValues(horizontal = SpacingTokens.L)
                    ) {
                        items(uiState.featuredDeals) { deal ->
                            ProductCard(
                                product = deal,
                                onAddToCart = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.addToCart(deal)
                                },
                                onFavoriteToggle = { /* TODO */ },
                                modifier = Modifier.width(200.dp),
                                onProductClick = TODO(),
                                isFavorite = TODO(),
                                isCompact = TODO(),
                                selectedStoreId = TODO()
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(SpacingTokens.M))
                }
            }

            // Popular Products Section
            if (uiState.popularProducts.isNotEmpty()) {
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
                        onAddToCart = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addToCart(product)
                        },
                        onFavoriteToggle = { /* TODO */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SpacingTokens.L)
                            .padding(bottom = SpacingTokens.M),
                        onProductClick = TODO(),
                        isFavorite = TODO(),
                        isCompact = TODO(),
                        selectedStoreId = TODO()
                    )
                }
            }

            // Empty state
            if (!uiState.isLoading && uiState.featuredDeals.isEmpty() && uiState.popularProducts.isEmpty()) {
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
                    ShimmerProductCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SpacingTokens.L)
                            .padding(bottom = SpacingTokens.M)
                    )
                }
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(SpacingTokens.L)
                .padding(bottom = 80.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    navController.navigate(Screen.Search.route)
                },
                containerColor = MaterialTheme.colorScheme.extended.electricMint,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Black
                )
            }
        }

        // Error handling
        uiState.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpacingTokens.XL),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    type = EmptyStateType.NETWORK_ERROR,
                    title = "שגיאה",
                    subtitle = error,
                    actionLabel = "נסה שוב",
                    onAction = viewModel::clearError
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    greeting: String,
    userName: String,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L)
            .padding(top = SpacingTokens.L),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            GlassmorphicIconButton(
                onClick = onSearchClick,
                icon = Icons.Default.Search
            )
            GlassmorphicIconButton(
                onClick = onNotificationClick,
                icon = Icons.Default.NotificationsNone,
                badge = true
            )
        }
    }
}

@Composable
private fun StatsCard(
    totalSavings: Double,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.L)
        ) {
            Text(
                text = "החיסכון שלך החודש",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(SpacingTokens.XS))
            Text(
                text = "₪${String.format("%.2f", totalSavings)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.extended.electricMint
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onScanClick: () -> Unit,
    onListClick: () -> Unit,
    onCompareClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        QuickActionButton(
            icon = Icons.Default.QrCodeScanner,
            label = "סרוק",
            onClick = onScanClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.ListAlt,
            label = "רשימות",
            onClick = onListClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.CompareArrows,
            label = "השווה",
            onClick = onCompareClick,
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
    GlassmorphicCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.M),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.extended.electricMint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(SpacingTokens.XS))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CategoriesRow(
    categories: List<ProductCategory>,
    selectedCategory: String?,
    onCategoryClick: (ProductCategory) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        contentPadding = PaddingValues(horizontal = SpacingTokens.L)
    ) {
        items(categories) { category ->
            CategoryChip(
                category = category,
                isSelected = category.name == selectedCategory,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    category: ProductCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = "${category.name} (${category.productCount})",
                style = MaterialTheme.typography.bodySmall
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.extended.electricMint,
            selectedLabelColor = Color.Black
        )
    )
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: Pair<String, () -> Unit>? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        action?.let { (text, onClick) ->
            TextButton(onClick = onClick) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.extended.electricMint
                )
            }
        }
    }
}