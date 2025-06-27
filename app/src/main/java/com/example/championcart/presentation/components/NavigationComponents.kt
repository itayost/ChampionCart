package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.ui.theme.*

/**
 * Navigation Components
 * Bottom bar, top bar, and navigation elements with Electric Harmony styling
 */

/**
 * Glass Bottom Navigation Bar
 */
@Composable
fun GlassBottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = Elevation.Component.navigationBar,
                shape = ComponentShapes.Navigation.BottomNavigation
            )
            .clip(ComponentShapes.Navigation.BottomNavigation)
            .gradientGlass(
                colors = listOf(
                    ChampionCartTheme.colors.surface.copy(alpha = 0.9f),
                    ChampionCartTheme.colors.surface.copy(alpha = 0.7f)
                ),
                intensity = GlassIntensity.Heavy,
                shape = ComponentShapes.Navigation.BottomNavigation
            )
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            contentColor = ChampionCartTheme.colors.onSurface,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                val selected = currentDestination?.route == item.route

                GlassNavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        GlassNavIcon(
                            icon = item.icon,
                            selected = selected,
                            badge = item.badge
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}

/**
 * Glass Navigation Bar Item
 */
@Composable
private fun RowScope.GlassNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val config = ChampionCartTheme.config

    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = ChampionCartAnimations.Springs.responsive(),
        label = "navItemScale"
    )

    NavigationBarItem(
        selected = selected,
        onClick = {
            if (config.enableHaptics) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        icon = {
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
            ) {
                icon()
            }
        },
        label = label,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ChampionCartColors.Brand.ElectricMint,
            selectedTextColor = ChampionCartColors.Brand.ElectricMint,
            indicatorColor = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f),
            unselectedIconColor = ChampionCartTheme.colors.onSurfaceVariant,
            unselectedTextColor = ChampionCartTheme.colors.onSurfaceVariant
        )
    )
}

/**
 * Navigation Icon with Badge
 */
@Composable
private fun GlassNavIcon(
    icon: ImageVector,
    selected: Boolean,
    badge: Int? = null
) {
    BadgedBox(
        badge = {
            if (badge != null && badge > 0) {
                GlassBadge(count = badge)
            }
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Sizing.Icon.m)
        )
    }
}

/**
 * Glass Badge
 */
@Composable
fun GlassBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    val displayCount = if (count > 99) "99+" else count.toString()

    Box(
        modifier = modifier
            .size(if (displayCount.length > 2) 24.dp else 20.dp)
            .glass(
                intensity = GlassIntensity.Heavy,
                shape = ComponentShapes.Special.Badge
            )
            .background(
                color = ChampionCartColors.Brand.NeonCoral,
                shape = ComponentShapes.Special.Badge
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayCount,
            style = CustomTextStyles.badge,
            color = Color.White
        )
    }
}

/**
 * Glass Top App Bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTopAppBar(
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
                fontWeight = FontWeight.SemiBold
            )
        },
        modifier = modifier
            .shadow(
                elevation = Elevation.Component.topAppBar,
                shape = ComponentShapes.Navigation.TopBar
            )
            .clip(ComponentShapes.Navigation.TopBar)
            .gradientGlass(
                colors = listOf(
                    ChampionCartTheme.colors.surface.copy(alpha = 0.95f),
                    ChampionCartTheme.colors.surface.copy(alpha = 0.85f)
                ),
                intensity = GlassIntensity.Medium,
                shape = ComponentShapes.Navigation.TopBar
            ),
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior
    )
}

/**
 * Search Top App Bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassSearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "חיפוש מוצרים...",
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            GlassSearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = placeholder,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
            .shadow(
                elevation = Elevation.Component.topAppBar,
                shape = ComponentShapes.Navigation.TopBar
            )
            .clip(ComponentShapes.Navigation.TopBar)
            .glass(
                intensity = GlassIntensity.Medium,
                shape = ComponentShapes.Navigation.TopBar
            ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

/**
 * Tab Row with Glass Effect
 */
@Composable
fun GlassTabRow(
    selectedTabIndex: Int,
    tabs: List<TabItem>,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val config = ChampionCartTheme.config

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier
            .fillMaxWidth()
            .glass(
                intensity = GlassIntensity.Light,
                shape = RoundedCornerShape(0.dp)
            ),
        containerColor = Color.Transparent,
        contentColor = ChampionCartTheme.colors.onSurface,
        edgePadding = Spacing.m,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = ChampionCartColors.Brand.ElectricMint,
                height = 3.dp
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    if (config.enableHaptics) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    tab.onClick()
                },
                text = {
                    Text(
                        text = tab.title,
                        fontWeight = if (selectedTabIndex == index) {
                            FontWeight.Medium
                        } else {
                            FontWeight.Normal
                        }
                    )
                },
                icon = tab.icon?.let {
                    {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.size(Sizing.Icon.s)
                        )
                    }
                },
                selectedContentColor = ChampionCartColors.Brand.ElectricMint,
                unselectedContentColor = ChampionCartTheme.colors.onSurfaceVariant
            )
        }
    }
}

/**
 * Navigation Rail for tablets
 */
@Composable
fun GlassNavigationRail(
    selectedItem: String,
    items: List<NavigationRailItem>,
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null
) {
    NavigationRail(
        modifier = modifier
            .glass(
                intensity = GlassIntensity.Medium,
                shape = RoundedCornerShape(0.dp)
            ),
        containerColor = Color.Transparent,
        contentColor = ChampionCartTheme.colors.onSurface,
        header = header
    ) {
        items.forEach { item ->
            val selected = selectedItem == item.route

            NavigationRailItem(
                selected = selected,
                onClick = item.onClick,
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badge != null && item.badge > 0) {
                                GlassBadge(count = item.badge)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = ChampionCartColors.Brand.ElectricMint,
                    selectedTextColor = ChampionCartColors.Brand.ElectricMint,
                    indicatorColor = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f),
                    unselectedIconColor = ChampionCartTheme.colors.onSurfaceVariant,
                    unselectedTextColor = ChampionCartTheme.colors.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Data classes for navigation items
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val badge: Int? = null
)

data class TabItem(
    val title: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit
)

data class NavigationRailItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val badge: Int? = null,
    val onClick: () -> Unit
)
