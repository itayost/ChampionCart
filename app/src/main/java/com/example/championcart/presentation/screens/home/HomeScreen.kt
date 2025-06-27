package com.example.championcart.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToSearch: (String?) -> Unit,
    onNavigateToCategory: (String, String) -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCitySelection: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.m)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "שלום, משתמש!",
                style = CustomTextStyles.price
            )

            SecondaryGlassButton(
                onClick = onNavigateToCitySelection,
                text = "תל אביב",
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

        // Search bar
        GlassCard(
            onClick = { onNavigateToSearch(null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.m)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = ChampionCartTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(Spacing.s))
                Text(
                    text = "חפש מוצרים...",
                    style = CustomTextStyles.priceSmall,
                    color = ChampionCartTheme.colors.onSurfaceVariant
                )
            }
        }

        // Quick Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.m),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            QuickStatCard(
                title = "חיסכון החודש",
                value = "₪245",
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "מוצרים בעגלה",
                value = "12",
                icon = Icons.Default.ShoppingCart,
                modifier = Modifier.weight(1f)
            )
        }

        // Categories
        Text(
            text = "קטגוריות",
            style = CustomTextStyles.badge,
            modifier = Modifier.padding(bottom = Spacing.s)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            item {
                CategoryCard(
                    name = "פירות וירקות",
                    icon = Icons.Default.LocalFlorist,
                    onClick = { onNavigateToCategory("1", "פירות וירקות") }
                )
            }
            item {
                CategoryCard(
                    name = "מוצרי חלב",
                    icon = Icons.Default.Kitchen,
                    onClick = { onNavigateToCategory("2", "מוצרי חלב") }
                )
            }
            item {
                CategoryCard(
                    name = "בשר ודגים",
                    icon = Icons.Default.Restaurant,
                    onClick = { onNavigateToCategory("3", "בשר ודגים") }
                )
            }
            item {
                CategoryCard(
                    name = "מאפים",
                    icon = Icons.Default.Cake,
                    onClick = { onNavigateToCategory("4", "מאפים") }
                )
            }
            item {
                CategoryCard(
                    name = "משקאות",
                    icon = Icons.Default.LocalDrink,
                    onClick = { onNavigateToCategory("5", "משקאות") }
                )
            }
            item {
                CategoryCard(
                    name = "ניקיון",
                    icon = Icons.Default.CleaningServices,
                    onClick = { onNavigateToCategory("6", "ניקיון") }
                )
            }
        }
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        intensity = GlassIntensity.Light
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ChampionCartColors.Brand.ElectricMint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.s))
            Column {
                Text(
                    text = value,
                    style = CustomTextStyles.price,
                    color = ChampionCartTheme.colors.primary
                )
                Text(
                    text = title,
                    style = CustomTextStyles.badge,
                    color = ChampionCartTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f),
        intensity = GlassIntensity.Light
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.s),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ChampionCartColors.Brand.ElectricMint,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = name,
                style = CustomTextStyles.category,
                color = ChampionCartTheme.colors.onSurface
            )
        }
    }
}