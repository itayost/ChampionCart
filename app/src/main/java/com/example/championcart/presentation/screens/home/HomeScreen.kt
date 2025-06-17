package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.championcart.presentation.components.CitySelectionDialog
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current

    // City selection dialog
    if (state.showCitySelector) {
        CitySelectionDialog(
            cities = state.availableCities,
            selectedCity = state.selectedCity,
            onCitySelected = { city ->
                viewModel.selectCity(city)
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            onDismiss = { viewModel.hideCitySelector() }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.extendedColors.electricMint.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.extendedColors.cosmicPurple.copy(alpha = 0.02f)
                    )
                )
            ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        item {
            // Header with greeting and city selector
            HomeHeader(
                userName = state.userName,
                selectedCity = state.selectedCity,
                onCityClick = { viewModel.showCitySelector() }
            )
        }

        item {
            // Cart status card (if has items)
            if (state.cartItemCount > 0) {
                CartStatusCard(
                    itemCount = state.cartItemCount,
                    onViewCartClick = {
                        navController.navigate(Screen.Cart.route)
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item {
            // Quick actions
            QuickActionsSection(
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            // User stats (if logged in)
            if (state.userEmail.isNotEmpty()) {
                UserStatsCard(
                    totalSavings = state.totalSavings,
                    savingsThisMonth = state.savingsThisMonth,
                    comparisonsCount = state.comparisonsCount,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item {
            // Recent searches
            if (state.recentSearches.isNotEmpty()) {
                RecentSearchesSection(
                    recentSearches = state.recentSearches,
                    onSearchClick = { searchTerm ->
                        viewModel.onRecentSearchClicked(searchTerm)
                        navController.navigate("${Screen.Search.route}?query=$searchTerm")
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item {
            // Error handling
            state.error?.let { error ->
                ErrorCard(
                    message = error,
                    onRetry = { viewModel.refreshData() },
                    onDismiss = { viewModel.clearError() },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        // Bottom spacing for navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HomeHeader(
    userName: String,
    selectedCity: String,
    onCityClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Greeting
        Text(
            text = "Welcome back,",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // City selector
        Card(
            modifier = Modifier.clickable { onCityClick() },
            shape = ComponentShapes.Chip,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.glass
            ),
            border = BorderStroke(1.dp, MaterialTheme.extendedColors.glassBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.electricMint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedCity,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Change city",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CartStatusCard(
    itemCount: Int,
    onViewCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewCartClick() },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        border = BorderStroke(1.dp, MaterialTheme.extendedColors.glassBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cart icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.electricMint,
                                MaterialTheme.extendedColors.cosmicPurple
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Cart info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your Active Cart",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$itemCount items • Ready to find best prices",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View cart",
                tint = MaterialTheme.extendedColors.electricMint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search products
            QuickActionCard(
                title = "Search",
                subtitle = "Find products",
                icon = Icons.Default.Search,
                onClick = onSearchClick,
                modifier = Modifier.weight(1f)
            )

            // View cart
            QuickActionCard(
                title = "Cart",
                subtitle = "View items",
                icon = Icons.Default.ShoppingCart,
                onClick = onCartClick,
                modifier = Modifier.weight(1f)
            )

            // Profile
            QuickActionCard(
                title = "Profile",
                subtitle = "Your stats",
                icon = Icons.Default.Person,
                onClick = onProfileClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glass
        ),
        border = BorderStroke(1.dp, MaterialTheme.extendedColors.glassBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.electricMint,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun UserStatsCard(
    totalSavings: Double,
    savingsThisMonth: Double,
    comparisonsCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        border = BorderStroke(1.dp, MaterialTheme.extendedColors.glassBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Your Savings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Total Saved",
                    value = "₪${String.format("%.2f", totalSavings)}",
                    icon = Icons.Default.Savings
                )
                StatItem(
                    label = "This Month",
                    value = "₪${String.format("%.2f", savingsThisMonth)}",
                    icon = Icons.Default.CalendarMonth
                )
                StatItem(
                    label = "Comparisons",
                    value = comparisonsCount.toString(),
                    icon = Icons.Default.Compare
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.successGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.successGreen
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RecentSearchesSection(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Clear All",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.electricMint,
                modifier = Modifier.clickable { /* TODO: Clear searches */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(recentSearches) { searchTerm ->
                RecentSearchChip(
                    searchTerm = searchTerm,
                    onClick = { onSearchClick(searchTerm) }
                )
            }
        }
    }
}

@Composable
fun RecentSearchChip(
    searchTerm: String,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = searchTerm,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        selected = false,
        shape = ComponentShapes.Chip,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.extendedColors.glass,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.extendedColors.glassBorder
        )
    )
}

@Composable
fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            TextButton(onClick = onRetry) {
                Text("Retry")
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss"
                )
            }
        }
    }
}