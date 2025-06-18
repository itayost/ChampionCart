package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.example.championcart.R
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.presentation.components.*
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

/**
 * Modern Home Screen with Electric Harmony Design
 * Features glassmorphism, spring animations, and intelligent UI
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
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.extended.surfaceGlass
                    )
                )
            )
    ) {
        // Main content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp) // Space for bottom nav
        ) {
            // Refresh button at top
            if (!state.isRefreshing) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier
                                .size(40.dp)
                                .glassmorphic(
                                    intensity = GlassIntensity.Light,
                                    shape = CircleShape
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
            }
            // Hero Header Section
            item {
                HeroHeader(
                    greeting = greeting,
                    userName = state.userName ?: "חבר יקר",
                    selectedCity = state.selectedCity,
                    totalSaved = state.totalSaved,
                    onCityClick = { viewModel.showCitySelector() }
                )
            }

            // Loading state
            if (state.isRefreshing) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpacingTokens.L),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.extended.electricMint
                        )
                    }
                }
            }

            // Quick Stats Cards
            if (!state.isRefreshing) {
                item {
                    QuickStatsSection(
                        totalSaved = state.totalSaved,
                        itemsCompared = state.itemsCompared,
                        cheapestStore = state.cheapestStore
                    )
                }
            }

            // Quick Actions
            item {
                QuickActionsSection(
                    onScanClick = { navController.navigate(Screen.Search.route) },
                    onSearchClick = { navController.navigate(Screen.Search.route) },
                    onStoresClick = { navController.navigate(Screen.Search.route) },
                    onDealsClick = { /* TODO: Navigate to deals */ }
                )
            }

            // Featured Deals Section
            if (state.featuredDeals.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "מבצעים חמים 🔥",
                        action = "הצג הכל",
                        onActionClick = { /* TODO: Navigate to all deals */ }
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = SpacingTokens.L),
                        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
                    ) {
                        items(state.featuredDeals) { deal ->
                            DealCard(
                                product = deal,
                                onProductClick = {
                                    navController.navigate(
                                        Screen.ProductDetail.createRoute(deal.itemCode)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Recent Comparisons
            if (state.recentComparisons.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "השוואות אחרונות",
                        action = "היסטוריה",
                        onActionClick = { /* TODO: Navigate to history */ }
                    )
                }

                items(state.recentComparisons) { product ->
                    Spacer(modifier = Modifier.height(SpacingTokens.M))
                    GroupedProductCard(
                        product = product,
                        onAddToCart = { storePrice ->
                            viewModel.addToCart(product, storePrice)
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onProductClick = {
                            navController.navigate(
                                Screen.ProductDetail.createRoute(product.itemCode)
                            )
                        },
                        modifier = Modifier.padding(horizontal = SpacingTokens.L)
                    )
                }
            }

            // Empty state
            if (state.recentComparisons.isEmpty() && !state.isLoading) {
                item {
                    EmptyState(
                        type = EmptyStateType.FIRST_TIME,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        onAction = { navController.navigate(Screen.Search.route) }
                    )
                }
            }
        }

        // City selector dialog
        if (state.showCitySelector) {
            CitySelector(
                currentCity = state.selectedCity,
                cities = state.availableCities,
                onCitySelected = { city ->
                    viewModel.selectCity(city)
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                isLoading = state.isLoadingCities,
                recentCities = state.recentCities
            )
        }

        // Error snackbar
        state.error?.let { error ->
            LaunchedEffect(error) {
                scope.launch {
                    delay(3000)
                    viewModel.clearError()
                }
            }

            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(SpacingTokens.L),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("סגור")
                    }
                }
            ) {
                Text(error)
            }
        }
    }
}

/**
 * Hero header with glassmorphic design
 */
@Composable
private fun HeroHeader(
    greeting: String,
    userName: String,
    selectedCity: String,
    totalSaved: Double,
    onCityClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Background gradient with orbs
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        // Floating orbs animation
        FloatingOrbs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingTokens.L)
                .statusBarsPadding()
        ) {
            // Top bar with city selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // City selector button
                CitySelectorButton(
                    city = selectedCity,
                    onClick = onCityClick,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(SpacingTokens.M))

                // Notification button
                IconButton(
                    onClick = { /* TODO: Navigate to notifications */ },
                    modifier = Modifier
                        .size(48.dp)
                        .glassmorphic(
                            intensity = GlassIntensity.Light,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "התראות",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Greeting section
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.L))

            // Total saved with animation
            AnimatedSavingsDisplay(totalSaved = totalSaved)
        }
    }
}

/**
 * Animated savings display
 */
@Composable
private fun AnimatedSavingsDisplay(totalSaved: Double) {
    val animatedValue by animateFloatAsState(
        targetValue = totalSaved.toFloat(),
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "savings"
    )

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        Text(
            text = "חסכת",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = "₪${String.format("%.0f", animatedValue)}",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.extended.electricMint
        )
        Text(
            text = "החודש",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

/**
 * Quick stats cards section
 */
@Composable
private fun QuickStatsSection(
    totalSaved: Double,
    itemsCompared: Int,
    cheapestStore: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            value = itemsCompared.toString(),
            label = "מוצרים הושוו",
            icon = Icons.Default.CompareArrows,
            color = MaterialTheme.colorScheme.extended.cosmicPurple
        )

        StatCard(
            modifier = Modifier.weight(1f),
            value = cheapestStore ?: "---",
            label = "הזול היום",
            icon = Icons.Default.Store,
            color = BrandColors.Success
        )
    }
}

/**
 * Individual stat card
 */
@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .glassmorphic(
                intensity = GlassIntensity.Medium,
                shape = GlassmorphicShapes.GlassCard
            ),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.1f),
                            color.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(SpacingTokens.M)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Quick actions section
 */
@Composable
private fun QuickActionsSection(
    onScanClick: () -> Unit,
    onSearchClick: () -> Unit,
    onStoresClick: () -> Unit,
    onDealsClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(SpacingTokens.L)
    ) {
        Text(
            text = "פעולות מהירות",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = SpacingTokens.M)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.QrCodeScanner,
                label = "סרוק",
                onClick = onScanClick,
                gradient = listOf(
                    MaterialTheme.colorScheme.extended.electricMint,
                    MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.8f)
                )
            )

            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Search,
                label = "חיפוש",
                onClick = onSearchClick,
                gradient = listOf(
                    MaterialTheme.colorScheme.extended.cosmicPurple,
                    MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.8f)
                )
            )

            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Store,
                label = "חנויות",
                onClick = onStoresClick,
                gradient = listOf(
                    BrandColors.Info,
                    BrandColors.Info.copy(alpha = 0.8f)
                )
            )

            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalOffer,
                label = "מבצעים",
                onClick = onDealsClick,
                gradient = listOf(
                    BrandColors.Warning,
                    BrandColors.Warning.copy(alpha = 0.8f)
                )
            )
        }
    }
}

/**
 * Quick action button with gradient
 */
@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "actionScale"
    )

    Card(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(gradient)
                )
                .padding(SpacingTokens.M),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(SpacingTokens.XS))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Deal card for featured deals
 */
@Composable
private fun DealCard(
    product: GroupedProduct,
    onProductClick: () -> Unit
) {
    val lowestPrice = product.prices.minByOrNull { it.price }
    val savings = product.savings

    Card(
        onClick = onProductClick,
        modifier = Modifier
            .width(200.dp)
            .height(240.dp),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Savings badge
            if (savings > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(SpacingTokens.S)
                        .clip(CircleShape)
                        .background(BrandColors.Success)
                        .padding(horizontal = SpacingTokens.M, vertical = SpacingTokens.S)
                ) {
                    Text(
                        text = "-${(savings / (lowestPrice?.price ?: 1.0) * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpacingTokens.M)
            ) {
                // Product image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(GlassmorphicShapes.GlassCardSmall)
                        .background(MaterialTheme.colorScheme.extended.surfaceGlass),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(SpacingTokens.M))

                // Product name
                Text(
                    text = product.itemName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                // Price info
                lowestPrice?.let { price ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "₪${price.price}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.extended.electricMint
                            )
                            Text(
                                text = price.chain,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        if (savings > 0) {
                            Text(
                                text = "חסכון ₪${String.format("%.2f", savings)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandColors.Success,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section header
 */
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

/**
 * City selector button (compact)
 */
@Composable
private fun CitySelectorButton(
    city: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = GlassmorphicShapes.Button,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SpacingTokens.M),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.extended.electricMint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = city,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Floating orbs animation
 */
@Composable
private fun FloatingOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")

    repeat(3) { index ->
        val offsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -30f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000 + index * 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orbY_$index"
        )

        val offsetX by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000 + index * 500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orbX_$index"
        )

        Box(
            modifier = Modifier
                .offset(
                    x = (offsetX + index * 100).dp,
                    y = (offsetY + index * 50).dp
                )
                .size((80 + index * 20).dp)
                .clip(CircleShape)
                .background(
                    when (index) {
                        0 -> MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
                        1 -> MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.08f)
                        else -> BrandColors.Info.copy(alpha = 0.06f)
                    }
                )
        )
    }
}