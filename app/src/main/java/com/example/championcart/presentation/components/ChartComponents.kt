package com.example.championcart.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
import com.example.championcart.ui.theme.ChampionCartAnimations.Transitions.fadeIn
import com.example.championcart.ui.theme.ChampionCartAnimations.Transitions.scaleIn
import kotlinx.coroutines.launch

/**
 * Modern Chart & Data Visualization Components
 * Electric Harmony Design System
 */

/**
 * Modern Price Comparison Bar Chart
 */
@Composable
fun ModernPriceComparisonChart(
    stores: List<StorePrice>,
    modifier: Modifier = Modifier,
    animateOnLoad: Boolean = true
) {
    val config = ChampionCartTheme.config
    var animationProgress by remember { mutableStateOf(if (animateOnLoad && !config.reduceMotion) 0f else 1f) }

    LaunchedEffect(stores) {
        if (animateOnLoad && !config.reduceMotion) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(1000, easing = FastOutSlowInEasing)
            ) { value, _ ->
                animationProgress = value
            }
        }
    }

    val maxPrice = stores.maxOfOrNull { it.price } ?: 0f
    val minPrice = stores.minOfOrNull { it.price } ?: 0f

    ModernGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.XL)
        ) {
            Text(
                text = "השוואת מחירים",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(SpacingTokens.L))

            stores.forEach { store ->
                PriceComparisonBar(
                    store = store,
                    progress = animationProgress,
                    maxPrice = maxPrice,
                    isLowest = store.price == minPrice,
                    isHighest = store.price == maxPrice
                )
                Spacer(modifier = Modifier.height(SpacingTokens.M))
            }
        }
    }
}

@Composable
private fun PriceComparisonBar(
    store: StorePrice,
    progress: Float,
    maxPrice: Float,
    isLowest: Boolean,
    isHighest: Boolean
) {
    val barWidth = (store.price / maxPrice) * progress
    val color = when {
        isLowest -> ChampionCartColors.Semantic.Success
        isHighest -> ChampionCartColors.Semantic.Error
        else -> ChampionCartColors.Semantic.Warning
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Store name
        Text(
            text = store.storeName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(100.dp),
            fontWeight = if (isLowest) FontWeight.Bold else FontWeight.Normal
        )

        // Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .padding(horizontal = SpacingTokens.S)
        ) {
            // Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            )

            // Animated bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(barWidth)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.8f),
                                color
                            )
                        )
                    )
                    .drawBehind {
                        if (isLowest) {
                            // Add shimmer effect for best price
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0f),
                                        Color.White.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0f)
                                    )
                                )
                            )
                        }
                    }
            )
        }

        // Price
        Text(
            text = "₪${store.price}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.width(70.dp),
            textAlign = TextAlign.End
        )
    }
}

/**
 * Modern Savings Trend Chart
 */
@Composable
fun ModernSavingsTrendChart(
    data: List<SavingsDataPoint>,
    modifier: Modifier = Modifier
) {
    val config = ChampionCartTheme.config
    var animationProgress by remember { mutableStateOf(if (!config.reduceMotion) 0f else 1f) }

    LaunchedEffect(data) {
        if (!config.reduceMotion) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(1500, easing = FastOutSlowInEasing)
            ) { value, _ ->
                animationProgress = value
            }
        }
    }

    val maxSavings = data.maxOfOrNull { it.amount } ?: 0f
    val totalSavings = data.sumOf { it.amount.toInt() }

    ModernGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.XL)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "חיסכון לאורך זמן",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₪$totalSavings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ChampionCartColors.Semantic.Success
                    )
                    Text(
                        text = "סה״כ חיסכון",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(SpacingTokens.M)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawTrendChart(
                        data = data,
                        maxValue = maxSavings,
                        progress = animationProgress
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingTokens.M))

            // X-axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEach { point ->
                    Text(
                        text = point.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawTrendChart(
    data: List<SavingsDataPoint>,
    maxValue: Float,
    progress: Float
) {
    val points = data.mapIndexed { index, point ->
        Offset(
            x = (index.toFloat() / (data.size - 1)) * size.width,
            y = size.height - ((point.amount / maxValue) * size.height * progress)
        )
    }

    // Draw gradient area
    val path = Path().apply {
        moveTo(0f, size.height)
        points.forEachIndexed { index, point ->
            if (index == 0) {
                lineTo(point.x, point.y)
            } else {
                val previousPoint = points[index - 1]
                val controlPoint1 = Offset(
                    (previousPoint.x + point.x) / 2,
                    previousPoint.y
                )
                val controlPoint2 = Offset(
                    (previousPoint.x + point.x) / 2,
                    point.y
                )
                cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    point.x, point.y
                )
            }
        }
        lineTo(size.width, size.height)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(
                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.3f),
                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.05f)
            )
        )
    )

    // Draw line
    points.forEachIndexed { index, point ->
        if (index > 0) {
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ChampionCartColors.Brand.ElectricMint,
                        ChampionCartColors.Brand.CosmicPurple
                    )
                ),
                start = points[index - 1],
                end = point,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }

    // Draw points with glow
    points.forEach { point ->
        // Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                radius = 12.dp.toPx(),
                center = point
            ),
            radius = 12.dp.toPx(),
            center = point
        )

        // Outer circle
        drawCircle(
            color = ChampionCartColors.Brand.ElectricMint,
            radius = 5.dp.toPx(),
            center = point
        )

        // Inner circle
        drawCircle(
            color = Color.White,
            radius = 3.dp.toPx(),
            center = point
        )
    }
}

/**
 * Modern Category Spending Chart
 */
@Composable
fun ModernCategorySpendingChart(
    categories: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    val total = categories.sumOf { it.amount.toDouble() }.toFloat()
    val config = ChampionCartTheme.config
    var animationProgress by remember { mutableStateOf(if (!config.reduceMotion) 0f else 1f) }

    LaunchedEffect(categories) {
        if (!config.reduceMotion) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(1200, easing = FastOutSlowInEasing)
            ) { value, _ ->
                animationProgress = value
            }
        }
    }

    ModernGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.XL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "הוצאות לפי קטגוריה",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Donut Chart
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawDonutChart(
                        categories = categories,
                        total = total,
                        progress = animationProgress
                    )
                }

                // Center content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "₪${total.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "סה״כ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Legend with modern styling
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
            ) {
                categories.forEach { category ->
                    ModernCategoryLegendItem(
                        category = category,
                        percentage = ((category.amount / total) * 100).toInt(),
                        color = getCategoryColor(category.name)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawDonutChart(
    categories: List<CategorySpending>,
    total: Float,
    progress: Float
) {
    var startAngle = -90f
    val strokeWidth = 50.dp.toPx()
    val radius = (size.minDimension - strokeWidth) / 2
    val center = Offset(size.width / 2, size.height / 2)

    categories.forEach { category ->
        val sweepAngle = ((category.amount / total) * 360f) * progress
        val color = getCategoryColor(category.name)

        // Shadow layer
        drawArc(
            color = color.copy(alpha = 0.2f),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = center - Offset(radius - 2.dp.toPx(), radius - 2.dp.toPx()),
            size = Size((radius - 2.dp.toPx()) * 2, (radius - 2.dp.toPx()) * 2),
            style = Stroke(strokeWidth + 4.dp.toPx())
        )

        // Main arc
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    color,
                    color.copy(alpha = 0.8f),
                    color
                ),
                center = center
            ),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        startAngle += sweepAngle
    }
}

@Composable
private fun ModernCategoryLegendItem(
    category: CategorySpending,
    percentage: Int,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(SpacingTokens.M),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "₪${category.amount.toInt()}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Modern Quick Stats Grid
 */
@Composable
fun ModernQuickStatsGrid(
    stats: List<QuickStat>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        items(stats) { stat ->
            ModernQuickStatCard(stat = stat)
        }
    }
}

@Composable
private fun ModernQuickStatCard(stat: QuickStat) {
    val config = ChampionCartTheme.config
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(stat) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(initialScale = 0.9f)
    ) {
        ModernGlassCard(
            modifier = Modifier.height(120.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                stat.color.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(SpacingTokens.L)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = stat.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Icon(
                            imageVector = stat.icon,
                            contentDescription = null,
                            modifier = Modifier.size(SizingTokens.IconM),
                            tint = stat.color
                        )
                    }

                    Text(
                        text = stat.value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = stat.color
                    )
                }
            }
        }
    }
}

// Updated data classes
data class SavingsDataPoint(
    val label: String,
    val amount: Float
)

data class CategorySpending(
    val name: String,
    val amount: Float
)

data class QuickStat(
    val label: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color = ChampionCartColors.Brand.ElectricMint
)

// Helper function
private fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "dairy", "חלב", "מוצרי חלב" -> ChampionCartColors.Category.Dairy
        "meat", "בשר", "בשר ודגים" -> ChampionCartColors.Category.Meat
        "produce", "ירקות", "פירות", "פירות וירקות" -> ChampionCartColors.Category.Produce
        "bakery", "מאפה", "לחם", "מאפים" -> ChampionCartColors.Category.Bakery
        "frozen", "קפואים" -> ChampionCartColors.Category.Frozen
        "household", "ניקיון", "בית" -> ChampionCartColors.Category.Household
        else -> ChampionCartColors.Brand.ElectricMint
    }
}