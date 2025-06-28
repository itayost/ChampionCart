package com.example.championcart.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun HomeScreen(
    onNavigateToSearch: (String?) -> Unit,
    onNavigateToCategory: (String, String) -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCitySelection: () -> Unit
) {
    // Time-based greeting
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 6..11 -> "בוקר טוב"
            in 12..17 -> "צהריים טובים"
            in 18..22 -> "ערב טוב"
            else -> "לילה טוב"
        }
    }

    val config = ChampionCartTheme.config

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle animated background gradient
        if (!config.reduceMotion && config.enableMicroAnimations) {
            AnimatedBackgroundGradient()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SpacingTokens.L)
                .padding(
                    bottom = 80.dp + // Bottom nav height
                            SpacingTokens.L +
                            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
        ) {
            Spacer(modifier = Modifier.height(SpacingTokens.M))

            // Modern Header Section
            ModernHeaderSection(
                greeting = greeting,
                onCityClick = onNavigateToCitySelection
            )

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Quick Stats Row
            QuickStatsRow()

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Enhanced Search Bar
            ModernSearchBar(
                onClick = { onNavigateToSearch(null) }
            )

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Featured Deals Section
            FeaturedDealsSection(
                onProductClick = onNavigateToProduct
            )

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Categories Grid
            Text(
                text = "קטגוריות פופולריות",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = SpacingTokens.M)
            )

            ModernCategoriesGrid(
                onNavigateToCategory = onNavigateToCategory
            )

            // Bottom spacing
            Spacer(modifier = Modifier.height(SpacingTokens.XXL))
        }
    }
}

@Composable
private fun AnimatedBackgroundGradient() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val offset by infiniteTransition.animateFloat(
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
                    start = Offset(offset, 0f),
                    end = Offset(offset + 1000f, 1000f)
                )
            )
    )
}

@Composable
private fun ModernHeaderSection(
    greeting: String,
    onCityClick: () -> Unit
) {
    ModernGlassCard(
        shape = RoundedCornerShape(32.dp),
        borderGradient = true
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.XL)
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
                    Spacer(modifier = Modifier.height(SpacingTokens.XS))
                    Text(
                        text = "מוכנים לחסוך היום?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // City selector using new GlassButton
                GlassButton(
                    onClick = onCityClick,
                    text = "תל אביב",
                    icon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(SizingTokens.IconXS)
                        )
                    },
                    size = ButtonSize.Small
                )
            }
        }
    }
}

@Composable
private fun QuickStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        StatsCard(
            title = "חיסכון החודש",
            value = "₪523",
            icon = Icons.Default.Savings,
            trend = 12.5f,
            accentColor = ChampionCartColors.Semantic.Success,
            modifier = Modifier.weight(1f)
        )

        StatsCard(
            title = "פריטים בעגלה",
            value = "12",
            icon = Icons.Default.ShoppingCart,
            accentColor = ChampionCartColors.Brand.CosmicPurple,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ModernSearchBar(
    onClick: () -> Unit
) {
    ModernGlassCard(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = ChampionCartColors.Brand.ElectricMint,
                modifier = Modifier.size(SizingTokens.IconM)
            )
            Spacer(modifier = Modifier.width(SpacingTokens.M))
            Column(modifier = Modifier.weight(1f)) {
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
            GlowingIconButton(
                onClick = { /* Barcode scanner */ },
                icon = {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = "Scan barcode"
                    )
                },
                glowColor = ChampionCartColors.Brand.ElectricMint
            )
        }
    }
}

@Composable
private fun FeaturedDealsSection(
    onProductClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = SpacingTokens.M),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "מבצעים חמים 🔥",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            ElectricTextButton(
                onClick = { /* Navigate to all deals */ },
                text = "ראה הכל",
                icon = {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(SizingTokens.IconXS)
                    )
                }
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
            contentPadding = PaddingValues(horizontal = SpacingTokens.XS)
        ) {
            items(featuredDeals) { deal ->
                var isVisible by remember { mutableStateOf(false) }

                LaunchedEffect(deal) {
                    isVisible = true
                }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally()
                ) {
                    ProductPriceCard(
                        productName = deal.productName,
                        prices = deal.prices,
                        onAddToCart = { onProductClick(deal.id) },
                        modifier = Modifier.width(300.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernCategoriesGrid(
    onNavigateToCategory: (String, String) -> Unit
) {
    val categories = remember { categoryData }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        modifier = Modifier.height(320.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            var isVisible by remember { mutableStateOf(false) }

            LaunchedEffect(category) {
                delay(categories.indexOf(category) * 50L)
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
                CategoryCard(
                    title = category.name,
                    icon = category.icon,
                    itemCount = category.itemCount,
                    onClick = {
                        onNavigateToCategory(category.id, category.name)
                    },
                    gradient = listOf(
                        category.color,
                        category.color.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

// Data Models
data class CategoryData(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val itemCount: Int
)

data class FeaturedDeal(
    val id: String,
    val productName: String,
    val prices: List<StorePrice>
)

// Sample Data
private val categoryData = listOf(
    CategoryData(
        "1",
        "פירות וירקות",
        Icons.Default.Spa,
        ChampionCartColors.Category.Produce,
        89
    ),
    CategoryData(
        "2",
        "מוצרי חלב",
        Icons.Default.LocalDrink,
        ChampionCartColors.Category.Dairy,
        45
    ),
    CategoryData(
        "3",
        "בשר ודגים",
        Icons.Default.Restaurant,
        ChampionCartColors.Category.Meat,
        67
    ),
    CategoryData(
        "4",
        "מאפים",
        Icons.Default.Cake,
        ChampionCartColors.Category.Bakery,
        34
    ),
    CategoryData(
        "5",
        "משקאות",
        Icons.Default.WineBar,
        ChampionCartColors.Brand.CosmicPurple,
        78
    ),
    CategoryData(
        "6",
        "ניקיון",
        Icons.Default.CleaningServices,
        ChampionCartColors.Category.Household,
        56
    )
)

private val featuredDeals = listOf(
    FeaturedDeal(
        "1",
        "חלב תנובה 3% שומן 1 ליטר",
        listOf(
            StorePrice("רמי לוי", 5.90f),
            StorePrice("שופרסל", 6.90f),
            StorePrice("ויקטורי", 6.50f)
        )
    ),
    FeaturedDeal(
        "2",
        "לחם אחיד פרוס",
        listOf(
            StorePrice("ויקטורי", 8.90f),
            StorePrice("רמי לוי", 9.50f),
            StorePrice("מגה", 10.90f)
        )
    ),
    FeaturedDeal(
        "3",
        "במבה אוסם 80 גרם",
        listOf(
            StorePrice("אושר עד", 4.90f),
            StorePrice("שופרסל", 5.90f),
            StorePrice("רמי לוי", 5.50f)
        )
    )
)