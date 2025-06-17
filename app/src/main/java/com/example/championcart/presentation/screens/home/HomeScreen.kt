package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.math.abs
import kotlin.math.sin
import androidx.compose.foundation.BorderStroke
import androidx.navigation.NavController
import com.example.championcart.ui.theme.extendedColors
import com.example.championcart.ui.theme.ComponentShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Time-based greeting and theme
    val (greeting, greetingIcon) = remember {
        val hour = LocalTime.now().hour
        when (hour) {
            in 6..11 -> "Good Morning" to "☀️"
            in 12..17 -> "Good Afternoon" to "🌤️"
            in 18..22 -> "Good Evening" to "🌙"
            else -> "Good Night" to "✨"
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            HomeTopBar(
                userName = state.userName,
                greeting = greeting,
                greetingIcon = greetingIcon,
                totalSavings = state.totalSavings,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.extendedColors.backgroundGradient.colors[0],
                            MaterialTheme.extendedColors.backgroundGradient.colors[1]
                        )
                    )
                )
        ) {
            // Animated background elements
            FloatingBackgroundElements()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Smart Search Section
                item {
                    SmartSearchSection(
                        recentSearches = state.recentSearches,
                        trendingItems = state.trendingItems,
                        onSearchClick = onNavigateToSearch,
                        onVoiceClick = { /* Handle voice search */ }
                    )
                }

                // City Selection Section
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CitySelectionSection(
                        selectedCity = state.selectedCity,
                        cities = state.availableCities,
                        onCitySelected = viewModel::selectCity
                    )
                }

                // Quick Actions
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    QuickActionsSection(
                        onNavigateToCart = onNavigateToCart,
                        cartItemCount = state.cartItemCount,
                        onQuickAction = { action ->
                            when (action) {
                                QuickAction.SCAN -> { /* Handle scan */ }
                                QuickAction.LISTS -> { /* Handle lists */ }
                                QuickAction.DEALS -> { /* Handle deals */ }
                            }
                        }
                    )
                }

                // Today's Best Deals (if city selected)
                if (state.selectedCity != null) {
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        SectionHeader(
                            title = "Today's Best Deals 🔥",
                            subtitle = "Limited time offers in ${state.selectedCity.name}"
                        )
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(5) { index ->
                                DealCard(
                                    productName = "Product ${index + 1}",
                                    originalPrice = 25.90 + (index * 3),
                                    dealPrice = 19.90 + (index * 2),
                                    storeName = if (index % 2 == 0) "Shufersal" else "Victory",
                                    expiresIn = "${3 - (index % 3)}h ${30 + (index * 10)}m"
                                )
                            }
                        }
                    }
                }

                // Savings Summary Card
                if (state.totalSavings > 0) {
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        SavingsSummaryCard(
                            totalSavings = state.totalSavings,
                            savingsThisMonth = state.savingsThisMonth,
                            comparisonsCount = state.comparisonsCount
                        )
                    }
                }

                // Recent Activity
                if (state.recentSearches.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        SectionHeader(
                            title = "Continue Shopping",
                            subtitle = "Based on your recent searches"
                        )
                    }

                    itemsIndexed(state.recentSearches.take(3)) { index, search ->
                        RecentSearchCard(
                            searchTerm = search,
                            onTap = { onNavigateToSearch() }
                        )
                        if (index < 2) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(
    userName: String,
    greeting: String,
    greetingIcon: String,
    totalSavings: Double,
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "$greeting $greetingIcon",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (totalSavings > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Total saved: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₪${String.format("%.2f", totalSavings)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.extendedColors.successGreen
                        )
                    }
                }
            }

            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.electricMint,
                                MaterialTheme.extendedColors.cosmicPurple
                            )
                        )
                    )
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SmartSearchSection(
    recentSearches: List<String>,
    trendingItems: List<String>,
    onSearchClick: () -> Unit,
    onVoiceClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Search Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSearchClick()
                },
            shape = ComponentShapes.SearchBar,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.glassFrosted
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.extendedColors.glassFrostedBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Search for products...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onVoiceClick) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice search",
                        tint = MaterialTheme.extendedColors.electricMint
                    )
                }
            }
        }

        // Quick suggestions
        if (trendingItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.extendedColors.neonCoral
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Trending")
                            }
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.extendedColors.neonCoral.copy(alpha = 0.1f)
                        )
                    )
                }

                items(trendingItems.take(3)) { item ->
                    SuggestionChip(
                        onClick = { onSearchClick() },
                        label = { Text(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun CitySelectionSection(
    selectedCity: City?,
    cities: List<City>,
    onCitySelected: (City) -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        SectionHeader(
            title = "Select Your City",
            subtitle = selectedCity?.let { "${it.storeCount} stores available" } ?: "Choose a city to see prices"
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cities) { city ->
                CityCard(
                    city = city,
                    isSelected = city == selectedCity,
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCitySelected(city)
                    }
                )
            }
        }
    }
}

@Composable
fun CityCard(
    city: City,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp)
            .scale(scale)
            .clickable { onClick() },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.extendedColors.electricMint
            } else {
                MaterialTheme.extendedColors.glass
            }
        ),
        border = if (isSelected) null else BorderStroke(
            1.dp,
            MaterialTheme.extendedColors.glassBorder
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = city.emoji,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${city.storeCount} stores",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        Color.White.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onNavigateToCart: () -> Unit,
    cartItemCount: Int,
    onQuickAction: (QuickAction) -> Unit
) {
    val actions = listOf(
        QuickActionItem(
            action = QuickAction.CART,
            icon = Icons.Default.ShoppingCart,
            label = "Cart",
            color = MaterialTheme.extendedColors.electricMint,
            badge = if (cartItemCount > 0) cartItemCount.toString() else null,
            onClick = onNavigateToCart
        ),
        QuickActionItem(
            action = QuickAction.SCAN,
            icon = Icons.Default.QrCodeScanner,
            label = "Scan",
            color = MaterialTheme.extendedColors.cosmicPurple,
            onClick = { onQuickAction(QuickAction.SCAN) }
        ),
        QuickActionItem(
            action = QuickAction.LISTS,
            icon = Icons.Default.ListAlt,
            label = "Lists",
            color = MaterialTheme.extendedColors.neonCoral,
            onClick = { onQuickAction(QuickAction.LISTS) }
        ),
        QuickActionItem(
            action = QuickAction.DEALS,
            icon = Icons.Default.LocalOffer,
            label = "Deals",
            color = MaterialTheme.extendedColors.warningAmber,
            onClick = { onQuickAction(QuickAction.DEALS) }
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.forEach { item ->
            QuickActionCard(
                item = item,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    item: QuickActionItem,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .scale(scale)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    isPressed = true
                    item.onClick()
                },
            shape = ComponentShapes.Card,
            colors = CardDefaults.cardColors(
                containerColor = item.color.copy(alpha = 0.1f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(28.dp),
                        tint = item.color
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = item.color,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Badge
        item.badge?.let { badge ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.extendedColors.errorRed),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
fun DealCard(
    productName: String,
    originalPrice: Double,
    dealPrice: Double,
    storeName: String,
    expiresIn: String
) {
    val savings = ((originalPrice - dealPrice) / originalPrice * 100).toInt()

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glass
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.extendedColors.glassBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Deal badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ComponentShapes.Badge)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.neonCoral,
                                MaterialTheme.extendedColors.neonCoralLight
                            )
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$savings% OFF",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product emoji placeholder
            Text(
                text = "🛒",
                fontSize = 32.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = productName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            // Prices
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₪${String.format("%.2f", originalPrice)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "₪${String.format("%.2f", dealPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.successGreen
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Store and expiry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.labelSmall,
                    color = when (storeName) {
                        "Shufersal" -> MaterialTheme.extendedColors.shufersal
                        "Victory" -> MaterialTheme.extendedColors.victory
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Text(
                    text = expiresIn,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.extendedColors.warningAmber
                )
            }
        }
    }
}

@Composable
fun SavingsSummaryCard(
    totalSavings: Double,
    savingsThisMonth: Double,
    comparisonsCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f),
                            MaterialTheme.extendedColors.successGreen.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.electricMint,
                                MaterialTheme.extendedColors.successGreen
                            )
                        )
                    ),
                    shape = ComponentShapes.CardLarge
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.successGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Your Savings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SavingsMetric(
                        label = "Total Saved",
                        value = "₪${String.format("%.2f", totalSavings)}",
                        icon = Icons.Default.AccountBalance
                    )
                    SavingsMetric(
                        label = "This Month",
                        value = "₪${String.format("%.2f", savingsThisMonth)}",
                        icon = Icons.Default.CalendarMonth
                    )
                    SavingsMetric(
                        label = "Comparisons",
                        value = comparisonsCount.toString(),
                        icon = Icons.Default.CompareArrows
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsMetric(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.electricMint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.successGreen
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RecentSearchCard(
    searchTerm: String,
    onTap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onTap() },
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glass
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.extendedColors.glassBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = searchTerm,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.electricMint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FloatingBackgroundElements() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    // Floating element 1
    val float1 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    Box(
        modifier = Modifier
            .offset(
                x = 50.dp + (30.dp * sin(float1.value * 2 * Math.PI.toFloat())),
                y = 100.dp + (20.dp * float1.value)
            )
            .size(120.dp)
            .clip(CircleShape)
            .background(
                MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
            )
            .blur(60.dp)
    )

    // Floating element 2
    val float2 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    Box(
        modifier = Modifier
            .offset(
                x = 250.dp - (40.dp * sin(float2.value * 2 * Math.PI.toFloat())),
                y = 300.dp + (30.dp * float2.value)
            )
            .size(80.dp)
            .clip(CircleShape)
            .background(
                MaterialTheme.extendedColors.cosmicPurple.copy(alpha = 0.1f)
            )
            .blur(40.dp)
    )
}

// Data classes
data class City(
    val name: String,
    val emoji: String,
    val storeCount: Int,
    val shufersalCount: Int,
    val victoryCount: Int
)

enum class QuickAction {
    CART, SCAN, LISTS, DEALS
}

data class QuickActionItem(
    val action: QuickAction,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val color: Color,
    val badge: String? = null,
    val onClick: () -> Unit
)