package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.presentation.components.CitySelectionDialog
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current

    // City selection dialog
    if (state.showCitySelector) {
        CitySelectionDialog(
            currentCity = state.selectedCity,
            onCitySelected = { city ->
                viewModel.selectCity(city)
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            onDismiss = { viewModel.hideCitySelector() },
            tokenManager = tokenManager
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
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingLarge),
        contentPadding = PaddingValues(vertical = Dimensions.spacingLarge)
    ) {
        item {
            // Welcome header with city selector
            WelcomeHeader(
                userName = state.userName,
                selectedCity = state.selectedCity,
                onCityClick = { viewModel.showCitySelector() }
            )
        }

        item {
            // Cart status card (only if has items)
            if (state.cartItemCount > 0) {
                CartStatusCard(
                    itemCount = state.cartItemCount,
                    onViewCartClick = {
                        navController.navigate(Screen.Cart.route)
                    },
                    modifier = Modifier.padding(horizontal = Dimensions.paddingMedium)
                )
            }
        }

        item {
            // Quick actions grid
            QuickActionsGrid(
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onSavedCartsClick = {
                    navController.navigate(Screen.SavedCarts.route)
                },
                modifier = Modifier.padding(horizontal = Dimensions.paddingMedium)
            )
        }

        item {
            // Recent searches section (only if has searches)
            if (state.recentSearches.isNotEmpty()) {
                RecentSearchesSection(
                    recentSearches = state.recentSearches,
                    onSearchClick = { query ->
                        viewModel.onSearchQuerySelected(query)
                        navController.navigate("${Screen.Search.route}?query=$query")
                    },
                    onRemoveSearch = { query ->
                        viewModel.removeRecentSearch(query)
                    },
                    onClearAll = {
                        viewModel.clearRecentSearches()
                    },
                    modifier = Modifier.padding(horizontal = Dimensions.paddingMedium)
                )
            }
        }

        item {
            // Getting started guide for new users
            GettingStartedCard(
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                modifier = Modifier.padding(horizontal = Dimensions.paddingMedium)
            )
        }
    }
}

@Composable
fun WelcomeHeader(
    userName: String,
    selectedCity: String,
    onCityClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge)
    ) {
        // Welcome message with emoji
        Text(
            text = "Hello, $userName! 👋",
            style = AppTextStyles.hebrewHeadline,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Dimensions.spacingSmall))

        Text(
            text = "Find the best prices for your groceries",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

        // City selector chip
        Surface(
            modifier = Modifier.clickable { onCityClick() },
            shape = ComponentShapes.Chip,
            color = MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, MaterialTheme.extendedColors.electricMint.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = Dimensions.paddingMedium,
                    vertical = Dimensions.paddingSmall
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconSizeSmall),
                    tint = MaterialTheme.extendedColors.electricMint
                )
                Text(
                    text = selectedCity,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.extendedColors.electricMint,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Change city",
                    modifier = Modifier.size(Dimensions.iconSizeSmall),
                    tint = MaterialTheme.extendedColors.electricMint
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
        border = BorderStroke(1.dp, MaterialTheme.extendedColors.glassBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.extendedColors.electricMint.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.electricMint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "Your Cart",
                        style = AppTextStyles.productNameLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$itemCount items waiting",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "View cart",
                tint = MaterialTheme.extendedColors.electricMint
            )
        }
    }
}

@Composable
fun QuickActionsGrid(
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSavedCartsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Quick Actions",
            style = AppTextStyles.hebrewHeadline,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = Dimensions.spacingMedium)
        )

        // 2x2 grid of action cards
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                QuickActionCard(
                    title = "Search Products",
                    subtitle = "Find best prices",
                    icon = Icons.Default.Search,
                    onClick = onSearchClick,
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "My Cart",
                    subtitle = "View items",
                    icon = Icons.Default.ShoppingCart,
                    onClick = onCartClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                QuickActionCard(
                    title = "Saved Carts",
                    subtitle = "Your lists",
                    icon = Icons.Default.Bookmark,
                    onClick = onSavedCartsClick,
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "Profile",
                    subtitle = "Settings",
                    icon = Icons.Default.Person,
                    onClick = onProfileClick,
                    modifier = Modifier.weight(1f)
                )
            }
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
                .padding(Dimensions.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.electricMint,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
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
fun RecentSearchesSection(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onRemoveSearch: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header with clear all button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = AppTextStyles.hebrewHeadline,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = onClearAll,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.extendedColors.electricMint
                )
            ) {
                Text(
                    text = "Clear All",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

        // Recent searches chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall),
            contentPadding = PaddingValues(horizontal = Dimensions.spacingSmall)
        ) {
            items(recentSearches.size) { index ->
                val search = recentSearches[index]
                RecentSearchChip(
                    text = search,
                    onSearchClick = { onSearchClick(search) },
                    onRemoveClick = { onRemoveSearch(search) }
                )
            }
        }
    }
}

@Composable
fun RecentSearchChip(
    text: String,
    onSearchClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    FilterChip(
        onClick = onSearchClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.extendedColors.electricMint
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )

                // Remove button
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove search",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        selected = false,
        enabled = true,
        shape = ComponentShapes.Chip,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.extendedColors.glass,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = false,
            borderColor = MaterialTheme.extendedColors.glassBorder
        )
    )
}

@Composable
fun GettingStartedCard(
    onSearchClick: () -> Unit,
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
            modifier = Modifier.padding(Dimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.electricMint,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Title
            Text(
                text = "Start Finding Better Prices!",
                style = AppTextStyles.productNameLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingSmall))

            // Description
            Text(
                text = "Search for products to compare prices across Shufersal and Victory stores",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

            // CTA Button
            Button(
                onClick = onSearchClick,
                modifier = Modifier.fillMaxWidth(),
                shape = ComponentShapes.Button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.extendedColors.electricMint
                )
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                Text(
                    text = "Start Searching",
                    style = AppTextStyles.buttonMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}