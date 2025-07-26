package com.example.championcart.presentation.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.Padding
import com.example.championcart.ui.theme.Shapes
import com.example.championcart.ui.theme.Size
import com.example.championcart.ui.theme.TextStyles
import com.example.championcart.ui.theme.glass


/**
 * Navigation components for ChampionCart
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    )
}

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "חזור",
            modifier = Modifier.size(Size.icon)
        )
    }
}

@Composable
fun ChampionBottomNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .glass(
                shape = Shapes.bottomSheet,
                elevation = 8.dp
            ),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    BadgedBox(
                        badge = {
                            item.badge?.let { badgeText ->
                                Badge(
                                    containerColor = BrandColors.NeonCoral,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = badgeText,
                                        style = TextStyles.badge
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(Size.icon)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandColors.ElectricMint,
                    selectedTextColor = BrandColors.ElectricMint,
                    indicatorColor = BrandColors.ElectricMint.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val badge: String? = null
)

@Composable
fun TabBar(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                height = 3.dp,
                color = BrandColors.ElectricMint
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                selectedContentColor = BrandColors.ElectricMint,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SegmentedButton(
    selectedIndex: Int,
    options: List<String>,
    onSelectionChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .glass(
                shape = Shapes.button,
                elevation = 0.dp
            )
            .padding(Padding.xs)
    ) {
        options.forEachIndexed { index, option ->
            OutlinedButton(
                onClick = { onSelectionChange(index) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = when (index) {
                    0 -> RoundedCornerShape(
                        topStart = 20.dp,
                        bottomStart = 20.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                    options.lastIndex -> RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = 20.dp,
                        bottomEnd = 20.dp
                    )
                    else -> RoundedCornerShape(0.dp)
                },
                border = if (selectedIndex == index) {
                    BorderStroke(0.dp, Color.Transparent)
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                },
                colors = if (selectedIndex == index) {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = BrandColors.ElectricMint,
                        contentColor = Color.White
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}