package com.example.championcart.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.ChampionCartColors
import com.example.championcart.ui.theme.ChampionCartTheme
import com.example.championcart.ui.theme.ComponentShapes
import com.example.championcart.ui.theme.CustomTextStyles
import com.example.championcart.ui.theme.GlassIntensity
import com.example.championcart.ui.theme.PriceLevel
import com.example.championcart.ui.theme.Sizing
import com.example.championcart.ui.theme.Spacing
import com.example.championcart.ui.theme.glass

/**
 * Chart & Data Visualization Components
 * Price comparisons, savings tracking, and analytics
 */

/**
 * Price Comparison Bar Chart
 */
@Composable
fun PriceComparisonChart(
    stores: List<StorePrice>,
    modifier: Modifier = Modifier,
    animateOnLoad: Boolean = true
) {
    var animationProgress by remember { mutableStateOf(if (animateOnLoad) 0f else 1f) }

    LaunchedEffect(stores) {
        if (animateOnLoad) {
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

    GlassCard(
        modifier = modifier,
        intensity = GlassIntensity.Light
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Component.paddingL)
        ) {
            Text(
                text = "השוואת מחירים",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(Spacing.l))

            stores.forEach { store ->
                val barWidth = (store.price / maxPrice) * animationProgress
                val priceLevel = when (store.price) {
                    minPrice -> PriceLevel.Best
                    maxPrice -> PriceLevel.High
                    else -> PriceLevel.Mid
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.3f)
                    )

                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .height(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(barWidth)
                                .clip(ComponentShapes.Special.Indicator)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = when (priceLevel) {
                                            PriceLevel.Best -> listOf(
                                                ChampionCartColors.Price.Best.copy(alpha = 0.8f),
                                                ChampionCartColors.Price.Best
                                            )
                                            PriceLevel.Mid -> listOf(
                                                ChampionCartColors.Price.Mid.copy(alpha = 0.8f),
                                                ChampionCartColors.Price.Mid
                                            )
                                            PriceLevel.High -> listOf(
                                                ChampionCartColors.Price.High.copy(alpha = 0.8f),
                                                ChampionCartColors.Price.High
                                            )
                                        }
                                    )
                                )
                        )
                    }

                    Text(
                        text = "₪${store.price}",
                        style = CustomTextStyles.priceSmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.2f),
                        color = when (priceLevel) {
                            PriceLevel.Best -> ChampionCartColors.Semantic.Success
                            PriceLevel.Mid -> ChampionCartColors.Semantic.Warning
                            PriceLevel.High -> ChampionCartColors.Semantic.Error
                        }
                    )
                }
            }
        }
    }
}

/**
 * Savings Trend Line Chart
 */
@Composable
fun SavingsTrendChart(
    data: List<SavingsDataPoint>,
    modifier: Modifier = Modifier
) {
    val maxSavings = data.maxOfOrNull { it.amount } ?: 0f

    GlassCard(
        modifier = modifier,
        intensity = GlassIntensity.Light
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Component.paddingL)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "חיסכון לאורך זמן",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "₪${data.sumOf { it.amount.toInt() }}",
                    style = CustomTextStyles.price,
                    color = ChampionCartColors.Semantic.Success
                )
            }

            Spacer(modifier = Modifier.height(Spacing.l))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val points = data.mapIndexed { index, point ->
                        Offset(
                            x = (index.toFloat() / (data.size - 1)) * size.width,
                            y = size.height - (point.amount / maxSavings) * size.height
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
                                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )

                    // Draw line
                    points.forEachIndexed { index, point ->
                        if (index > 0) {
                            drawLine(
                                brush = Brush.linearGradient(
                                    colors = ChampionCartColors.Gradient.electricHarmony
                                ),
                                start = points[index - 1],
                                end = point,
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Draw points
                    points.forEach { point ->
                        drawCircle(
                            color = ChampionCartColors.Brand.ElectricMint,
                            radius = 4.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = point
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            // X-axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEach { point ->
                    Text(
                        text = point.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampionCartTheme.colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Category Spending Pie Chart
 */
@Composable
fun CategorySpendingChart(
    categories: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    val total = categories.sumOf { it.amount.toDouble() }.toFloat()
    var startAngle = -90f

    GlassCard(
        modifier = modifier,
        intensity = GlassIntensity.Light
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Component.paddingL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "הוצאות לפי קטגוריה",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(Spacing.l))

            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 40.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    categories.forEach { category ->
                        val sweepAngle = (category.amount / total) * 360f

                        drawArc(
                            color = getCategoryColor(category.name),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = center - Offset(radius, radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(strokeWidth)
                        )

                        startAngle += sweepAngle
                    }
                }

                // Center total
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "סה״כ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChampionCartTheme.colors.onSurfaceVariant
                    )
                    Text(
                        text = "₪${total.toInt()}",
                        style = CustomTextStyles.price,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.l))

            // Legend
            categories.forEach { category ->
                CategoryLegendItem(
                    category = category,
                    percentage = (category.amount / total * 100).toInt(),
                    modifier = Modifier.padding(vertical = Spacing.xs),
                    categoryColor = getCategoryColor(category.name)
                )
            }
        }
    }
}

/**
 * Category Legend Item
 */
@Composable
private fun CategoryLegendItem(
    category: CategorySpending,
    percentage: Int,
    categoryColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )

            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.bodySmall,
                color = ChampionCartTheme.colors.onSurfaceVariant
            )

            Text(
                text = "₪${category.amount}",
                style = CustomTextStyles.priceSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Quick Stats Grid
 */
@Composable
fun QuickStatsGrid(
    stats: List<QuickStat>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        items(stats) { stat ->
            QuickStatCard(stat = stat)
        }
    }
}

/**
 * Quick Stat Card
 */
@Composable
fun QuickStatCard(
    stat: QuickStat,
    modifier: Modifier = Modifier
) {
    val iconTint = stat.iconTint ?: ChampionCartTheme.colors.primary
    val valueColor = stat.valueColor ?: ChampionCartTheme.colors.onSurface

    GlassCard(
        modifier = modifier.height(100.dp),
        intensity = GlassIntensity.Light
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Component.paddingM),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = ChampionCartTheme.colors.onSurfaceVariant
                )

                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    modifier = Modifier.size(Sizing.Icon.s),
                    tint = iconTint
                )
            }

            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

// Data classes
data class StorePrice(
    val name: String,
    val price: Float
)

data class CategorySpending(
    val name: String,
    val amount: Float
)

data class SavingsDataPoint(
    val label: String,
    val amount: Float
)

data class QuickStat(
    val label: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconTint: Color? = null,
    val valueColor: Color? = null
)

// Helper function to get category color
private fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "dairy", "חלב", "מוצרי חלב" -> ChampionCartColors.Category.Dairy
        "meat", "בשר" -> ChampionCartColors.Category.Meat
        "produce", "ירקות", "פירות" -> ChampionCartColors.Category.Produce
        "bakery", "מאפה", "לחם" -> ChampionCartColors.Category.Bakery
        "frozen", "קפואים" -> ChampionCartColors.Category.Frozen
        "household", "ניקיון", "בית" -> ChampionCartColors.Category.Household
        else -> ChampionCartColors.Brand.ElectricMint
    }
}