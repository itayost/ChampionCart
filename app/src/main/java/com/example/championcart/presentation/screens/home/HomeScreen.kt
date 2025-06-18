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
    val state by viewModel.state.collectAsState()
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
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 80.dp // Space for bottom nav
            )
        ) {
            // Fixed Header Section without glass effect issues
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = 200f
                            )
                        )
                ) {
                    // Top bar with refresh button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo or brand icon
                        Card(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.extended.electricMint
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Refresh button
                        if (!state.isRefreshing) {
                            IconButton(
                                onClick = { viewModel.refresh() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "רענן",
                                    tint = MaterialTheme.colorScheme.extended.electricMint
                                )
                            }
                        }
                    }

                    // Hero Header
                    HeroHeader(
                        greeting = greeting,
                        userName = state.userName ?: tokenManager.getUserEmail()?.substringBefore("@"),
                        isGuest = state.isGuest,
                        onLoginClick = { navController.navigate(Screen.Auth.route) },
                        modifier = Modifier.padding(horizontal = SpacingTokens.L)
                    )

                    Spacer(modifier = Modifier.height(SpacingTokens.M))
                }
            }

            // Search bar section
            item {
                QuickSearchBar(
                    onSearchClick = { navController.navigate(Screen.Search.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpacingTokens.L)
                )

                Spacer(modifier = Modifier.height(SpacingTokens.L))
            }

            // Category chips
            item {
                CategoryChips(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = viewModel::selectCategory,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(SpacingTokens.L))
            }

            // Featured deals section (instead of special offers)
            if (state.featuredDeals.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "מוצרים עם הפרשי מחיר גדולים 💰",
                        action = "הצג הכל",
                        onActionClick = { /* Navigate to deals */ }
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = SpacingTokens.L),
                        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
                    ) {
                        items(state.featuredDeals) { product ->
                            CompactProductCard(
                                product = product,
                                onProductClick = {
                                    navController.navigate("${Screen.ProductDetail.route}/${product.itemCode}")
                                },
                                onAddToCart = { storePrice ->
                                    viewModel.addToCart(product, storePrice)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(SpacingTokens.L))
                }
            }

            // Popular products section
            if (state.popularProducts.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "פופולריים השבוע",
                        action = "עוד",
                        onActionClick = { /* Navigate to popular */ }
                    )
                }

                items(state.popularProducts) { product ->
                    ListProductCard(
                        product = product,
                        onAddToCart = { storePrice ->
                            viewModel.addToCart(product, storePrice)
                        },
                        onProductClick = {
                            navController.navigate("${Screen.ProductDetail.route}/${product.itemCode}")
                        },
                        onFavoriteToggle = {
                            // TODO: Implement favorite toggle
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SpacingTokens.L)
                            .padding(bottom = SpacingTokens.M),
                        isFavorite = false // TODO: Get from state
                    )
                }
            }

            // Empty state
            if (!state.isLoading && state.featuredDeals.isEmpty() && state.popularProducts.isEmpty()) {
                item {
                    EmptyState(
                        type = EmptyStateType.NO_RESULTS,
                        title = "אין מוצרים להצגה כרגע",
                        actionLabel = "רענן",
                        onAction = { viewModel.refresh() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpacingTokens.XL)
                    )
                }
            }

            // Loading state
            if (state.isLoading && state.popularProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.extended.electricMint
                        )
                    }
                }
            }
        }

        // Pull to refresh indicator
        if (state.isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.extended.electricMint
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        // Error Snackbar
        state.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .padding(SpacingTokens.M)
                    .align(Alignment.BottomCenter),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("סגור", color = MaterialTheme.colorScheme.extended.electricMint)
                    }
                },
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ) {
                Text(text = error)
            }
        }
    }
}

@Composable
private fun HeroHeader(
    greeting: String,
    userName: String?,
    isGuest: Boolean,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (isGuest) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
            ) {
                Text(
                    text = "צ'מפיון! 🏆",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "התחבר",
                        color = MaterialTheme.colorScheme.extended.electricMint
                    )
                }
            }
        } else {
            Text(
                text = "$userName, צ'מפיון! 🏆",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun QuickSearchBar(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onSearchClick,
        modifier = modifier.height(56.dp),
        shape = GlassmorphicShapes.SearchField,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SpacingTokens.M),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "חפש מוצרים...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = "סרוק ברקוד",
                tint = MaterialTheme.colorScheme.extended.electricMint
            )
        }
    }
}

@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = SpacingTokens.L),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        // All categories chip
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("הכל") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.extended.electricMint,
                    selectedLabelColor = Color.White
                )
            )
        }

        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.extended.electricMint,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        action?.let {
            TextButton(onClick = onActionClick ?: {}) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.extended.electricMint
                )
            }
        }
    }
}