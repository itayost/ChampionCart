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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.presentation.components.*
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*
import java.time.LocalTime

/**
 * COMPLETELY TRANSFORMED HomeScreen
 * Now fully implementing your premium design system
 */

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

    // Dynamic greeting based on time
    val greeting = remember {
        val hour = LocalTime.now().hour
        when (hour) {
            in 6..11 -> "🌅 Good Morning"
            in 12..17 -> "☀️ Good Afternoon"
            in 18..22 -> "🌙 Good Evening"
            else -> "✨ Welcome"
        }
    }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.extendedColors.electricMint.copy(alpha = 0.03f),
                        MaterialTheme.extendedColors.cosmicPurple.copy(alpha = 0.02f)
                    )
                )
            )
    ) {
        // Floating orbs background
        FloatingOrbsBackground(
            modifier = Modifier.fillMaxSize(),
            orbCount = 4,
            alpha = 0.4f
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 24.dp
            )
        ) {
            // Hero Header Section
            item {
                GlassHeroSection(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundOrbs = true
                ) {
                    Column {
                        // Top row with greeting and profile
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = greeting,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${state.userName}!",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = MaterialTheme.extendedColors.electricMint
                                )
                            }

                            // Profile & Notifications
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Notification bell with badge
                                Box {
                                    IconButton(
                                        onClick = { haptics.performHapticFeedback(HapticFeedbackType.LongPress) }
                                    ) {
                                        Icon(
                                            Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    // Notification badge
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                MaterialTheme.extendedColors.neonCoral,
                                                CircleShape
                                            )
                                            .align(Alignment.TopEnd)
                                    )
                                }

                                // Profile avatar
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            brush = Brush.linearGradient(
                                                listOf(
                                                    MaterialTheme.extendedColors.electricMint,
                                                    MaterialTheme.extendedColors.cosmicPurple
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                            navController.navigate(Screen.Profile.route)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "Profile",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Location selector
                        GlassCard(
                            onClick = { viewModel.showCitySelector() },
                            glowColor = MaterialTheme.extendedColors.electricMintGlow
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = MaterialTheme.extendedColors.electricMint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = state.selectedCity,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Change",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Stats Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Monthly Savings
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        glowColor = MaterialTheme.extendedColors.successGlow
                    ) {
                        Column {
                            Text(
                                text = "This Month",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "₪247",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.extendedColors.success
                            )
                            Text(
                                text = "+23% vs last month",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.extendedColors.success.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Cart Items
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        glowColor = MaterialTheme.extendedColors.cosmicPurpleGlow,
                        onClick = { navController.navigate(Screen.Cart.route) }
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = MaterialTheme.extendedColors.cosmicPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "My Cart",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = "${state.cartItemCount}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.extendedColors.cosmicPurple
                            )
                            Text(
                                text = if (state.cartItemCount == 1) "item" else "items",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.extendedColors.cosmicPurple.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Search Section
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Search bar with glow effect
                    Box(
                        modifier = Modifier.glowEffect(
                            glowColor = MaterialTheme.extendedColors.electricMintGlow,
                            blurRadius = 1.dp
                        )
                    ) {
                        GlassSearchBar(
                            query = "",
                            onQueryChange = { },
                            placeholder = "Search for products...",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.extendedColors.electricMint
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        // Navigate to barcode scanner
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.QrCodeScanner,
                                        contentDescription = "Scan",
                                        tint = MaterialTheme.extendedColors.neonCoral
                                    )
                                }
                            }
                        )
                    }

                    // Quick actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCard(
                            title = "Smart Search",
                            subtitle = "AI-powered",
                            icon = Icons.Default.Psychology,
                            gradient = listOf(
                                MaterialTheme.extendedColors.electricMint,
                                MaterialTheme.extendedColors.success
                            ),
                            onClick = { navController.navigate(Screen.Search.route) },
                            modifier = Modifier.weight(1f)
                        )

                        QuickActionCard(
                            title = "Price Alerts",
                            subtitle = "Track favorites",
                            icon = Icons.Default.TrendingUp,
                            gradient = listOf(
                                MaterialTheme.extendedColors.cosmicPurple,
                                MaterialTheme.extendedColors.neonCoral
                            ),
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                // Navigate to price alerts
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Recent Searches
            if (state.recentSearches.isNotEmpty()) {
                item {
                    GlassCard(
                        glowColor = MaterialTheme.extendedColors.glassBorder.copy(alpha = 0.3f)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Searches",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                TextButton(
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        // Clear recent searches
                                    }
                                ) {
                                    Text(
                                        text = "Clear All",
                                        color = MaterialTheme.extendedColors.electricMint
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.recentSearches) { search ->
                                    SearchChip(
                                        text = search,
                                        onClick = {
                                            viewModel.onSearchQuerySelected(search)
                                            navController.navigate(Screen.Search.route)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Error handling
            state.error?.let { error ->
                item {
                    GlassCard(
                        glowColor = MaterialTheme.extendedColors.errorGlow
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.extendedColors.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.extendedColors.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    GlassCard(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier.aspectRatio(1f),
        glowColor = gradient.first().copy(alpha = 0.3f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(gradient),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SearchChip(
    text: String,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Surface(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.extendedColors.glass,
        border = BorderStroke(
            1.dp,
            MaterialTheme.extendedColors.glassBorder
        ),
        modifier = Modifier.clip(RoundedCornerShape(20.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        )
    }
}