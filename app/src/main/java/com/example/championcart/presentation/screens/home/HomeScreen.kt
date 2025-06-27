package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onNavigateToSearch: (String?) -> Unit,
    onNavigateToCategory: (String, String) -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCitySelection: () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    // Time-based greeting
    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 6..11 -> "בוקר טוב"
            in 12..17 -> "צהריים טובים"
            in 18..22 -> "ערב טוב"
            else -> "לילה טוב"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle animated background gradient
        if (darkTheme) {
            AnimatedBackgroundGradient()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.m)
        ) {
            Spacer(modifier = Modifier.height(Spacing.m))

            // Enhanced Header - using elevated style for light theme
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.l),
                intensity = GlassIntensity.Medium,
                elevated = !darkTheme // Elevated in light theme for better visibility
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.l)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "$greeting, Champion! 🏆",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "מוכנים לחסוך היום?",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // City selector with proper theme handling
                        CitySelector(
                            currentCity = "תל אביב",
                            onClick = onNavigateToCitySelection
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.m))

                    // Quick savings summary with theme-aware indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        SavingsIndicator(
                            label = "חסכת החודש",
                            amount = "₪523",
                            trend = 12.5f,
                            modifier = Modifier.weight(1f),
                            darkTheme = darkTheme
                        )
                        ActiveCartIndicator(
                            itemCount = 12,
                            totalSavings = "₪47",
                            modifier = Modifier.weight(1f),
                            darkTheme = darkTheme
                        )
                    }
                }
            }

            // Enhanced Search Bar with proper visibility
            SearchBarGlass(
                onClick = { onNavigateToSearch(null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.l),
                darkTheme = darkTheme
            )

            // Featured Deals Section
            FeaturedDealsSection(
                onProductClick = onNavigateToProduct,
                modifier = Modifier.padding(bottom = Spacing.l)
            )

            // Categories Grid with animations
            Text(
                text = "קטגוריות פופולריות",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Spacing.m)
            )

            AnimatedCategoriesGrid(
                onNavigateToCategory = onNavigateToCategory,
                darkTheme = darkTheme
            )

            // Bottom spacing
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AnimatedBackgroundGradient() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val offset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.02f),
                        ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.02f),
                        Color.Transparent
                    ),
                    start = androidx.compose.ui.geometry.Offset(offset.value, 0f),
                    end = androidx.compose.ui.geometry.Offset(offset.value + 1000f, 1000f)
                )
            )
    )
}

@Composable
fun CitySelector(
    currentCity: String,
    onClick: () -> Unit
) {
    SecondaryGlassButton(
        onClick = onClick,
        text = currentCity,
        icon = {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        size = ButtonSize.Small
    )
}

@Composable
fun SavingsIndicator(
    label: String,
    amount: String,
    trend: Float,
    modifier: Modifier = Modifier,
    darkTheme: Boolean
) {
    val backgroundColor = if (darkTheme) {
        ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.15f)
    } else {
        ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.08f)
    }

    Surface(
        modifier = modifier
            .cardGlass(
                intensity = GlassIntensity.Light,
                shape = ComponentShapes.Card.Small,
                darkTheme = darkTheme
            ),
        color = backgroundColor,
        shape = ComponentShapes.Card.Small
    ) {
        Row(
            modifier = Modifier.padding(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                tint = ChampionCartColors.Brand.ElectricMint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.s))
            Column {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChampionCartColors.Brand.ElectricMint
                )
                Text(
                    text = "$label (+${trend}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ActiveCartIndicator(
    itemCount: Int,
    totalSavings: String,
    modifier: Modifier = Modifier,
    darkTheme: Boolean
) {
    val backgroundColor = if (darkTheme) {
        ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.15f)
    } else {
        ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.08f)
    }

    Surface(
        modifier = modifier
            .cardGlass(
                intensity = GlassIntensity.Light,
                shape = ComponentShapes.Card.Small,
                darkTheme = darkTheme
            ),
        color = backgroundColor,
        shape = ComponentShapes.Card.Small
    ) {
        Row(
            modifier = Modifier.padding(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = ChampionCartColors.Brand.CosmicPurple,
                    modifier = Modifier.size(20.dp)
                )
                if (itemCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(12.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(ChampionCartColors.Brand.NeonCoral)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacing.s))
            Column {
                Text(
                    text = "$itemCount פריטים",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChampionCartColors.Brand.CosmicPurple
                )
                Text(
                    text = "חוסכים $totalSavings",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchBarGlass(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean
) {
    GlassCard(
        onClick = onClick,
        modifier = modifier,
        intensity = GlassIntensity.Medium,
        elevated = !darkTheme // Better visibility in light theme
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.l),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = ChampionCartColors.Brand.ElectricMint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.m))
            Column {
                Text(
                    text = "חפש מוצרים, מותגים או חנויות",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "נסה: \"חלב\", \"במבה\" או \"שופרסל\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.PhotoCamera,
                contentDescription = "Scan barcode",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun FeaturedDealsSection(
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "מבצעים חמים 🔥",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = { /* Navigate to all deals */ }
            ) {
                Text("ראה הכל")
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(5) { index ->
                FeaturedDealCard(
                    productName = when (index) {
                        0 -> "חלב תנובה 3%"
                        1 -> "לחם אחיד פרוס"
                        2 -> "במבה אוסם"
                        3 -> "קוטג' תנובה"
                        else -> "ביצים L"
                    },
                    originalPrice = when (index) {
                        0 -> "₪8.90"
                        1 -> "₪12.50"
                        2 -> "₪7.90"
                        3 -> "₪6.90"
                        else -> "₪24.90"
                    },
                    discountedPrice = when (index) {
                        0 -> "₪5.90"
                        1 -> "₪8.90"
                        2 -> "₪4.90"
                        3 -> "₪4.50"
                        else -> "₪19.90"
                    },
                    storeName = when (index) {
                        0 -> "שופרסל"
                        1 -> "רמי לוי"
                        2 -> "ויקטורי"
                        3 -> "מגה"
                        else -> "אושר עד"
                    },
                    onClick = { onProductClick("product_$index") }
                )
            }
        }
    }
}

@Composable
fun FeaturedDealCard(
    productName: String,
    originalPrice: String,
    discountedPrice: String,
    storeName: String,
    onClick: () -> Unit
) {
    ProductGlassCard(
        productName = productName,
        productImage = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Gray.copy(alpha = 0.05f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray.copy(alpha = 0.3f)
                )
            }
        },
        price = discountedPrice,
        storeName = storeName,
        priceLevel = PriceLevel.Best,
        onClick = onClick,
        modifier = Modifier.width(160.dp)
    )
}

@Composable
fun AnimatedCategoriesGrid(
    onNavigateToCategory: (String, String) -> Unit,
    darkTheme: Boolean
) {
    val categories = remember {
        listOf(
            CategoryData("1", "פירות וירקות", Icons.Default.LocalFlorist, ChampionCartColors.Category.Produce),
            CategoryData("2", "מוצרי חלב", Icons.Default.Kitchen, ChampionCartColors.Category.Dairy),
            CategoryData("3", "בשר ודגים", Icons.Default.Restaurant, ChampionCartColors.Category.Meat),
            CategoryData("4", "מאפים", Icons.Default.Cake, ChampionCartColors.Category.Bakery),
            CategoryData("5", "משקאות", Icons.Default.LocalDrink, ChampionCartColors.Brand.CosmicPurple),
            CategoryData("6", "ניקיון", Icons.Default.CleaningServices, ChampionCartColors.Category.Household)
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        modifier = Modifier.height(250.dp)
    ) {
        items(categories.size) { index ->
            var isVisible by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = index) {
                delay(index * 50L)
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                EnhancedCategoryCard(
                    category = categories[index],
                    onClick = {
                        onNavigateToCategory(categories[index].id, categories[index].name)
                    },
                    darkTheme = darkTheme
                )
            }
        }
    }
}

@Composable
fun EnhancedCategoryCard(
    category: CategoryData,
    onClick: () -> Unit,
    darkTheme: Boolean
) {
    val categoryAlpha = if (darkTheme) 0.15f else 0.08f

    GlassCard(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f),
        intensity = GlassIntensity.Light
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    category.color.copy(alpha = categoryAlpha)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = if (darkTheme) {
                        category.color
                    } else {
                        category.color.copy(alpha = 0.8f)
                    },
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(Spacing.s))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }
        }
    }
}

data class CategoryData(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)