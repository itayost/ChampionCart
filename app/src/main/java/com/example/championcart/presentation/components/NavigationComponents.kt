package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.ui.theme.*

/**
 * Navigation Components
 * Theme-aware bottom bar, top bar, and navigation elements with Electric Harmony styling
 */

/**
 * Theme-Aware Glass Bottom Navigation Bar
 */
@Composable
fun GlassBottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val darkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (darkTheme) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = ComponentShapes.Navigation.BottomNavigation,
                        ambientColor = Color.Black.copy(alpha = 0.2f),
                        spotColor = Color.Black.copy(alpha = 0.3f)
                    )
                } else {
                    Modifier.shadow(
                        elevation = 4.dp,
                        shape = ComponentShapes.Navigation.BottomNavigation
                    )
                }
            ),
        shape = ComponentShapes.Navigation.BottomNavigation,
        color = if (darkTheme) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (darkTheme) {
                        Modifier.glass(
                            intensity = GlassIntensity.Heavy,
                            style = GlassStyle.Subtle,
                            shape = ComponentShapes.Navigation.BottomNavigation,
                            darkTheme = darkTheme
                        )
                    } else {
                        Modifier.background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.98f),
                                    Color.White.copy(alpha = 0.95f)
                                )
                            )
                        )
                    }
                )
        ) {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
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
                                badge = item.badge,
                                darkTheme = darkTheme
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
                        },
                        darkTheme = darkTheme
                    )
                }
            }
        }
    }
}

/**
 * Theme-Aware Glass Navigation Bar Item
 */
@Composable
private fun RowScope.GlassNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    darkTheme: Boolean
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
            indicatorColor = if (darkTheme) {
                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.15f)
            } else {
                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.08f)
            },
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

/**
 * Theme-Aware Navigation Icon with Badge
 */
@Composable
private fun GlassNavIcon(
    icon: ImageVector,
    selected: Boolean,
    badge: Int? = null,
    darkTheme: Boolean
) {
    BadgedBox(
        badge = {
            if (badge != null && badge > 0) {
                GlassBadge(count = badge, darkTheme = darkTheme)
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
 * Theme-Aware Glass Badge
 */
@Composable
fun GlassBadge(
    count: Int,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val displayCount = if (count > 99) "99+" else count.toString()

    Box(
        modifier = modifier
            .size(if (displayCount.length > 2) 24.dp else 20.dp)
            .background(
                color = ChampionCartColors.Brand.NeonCoral,
                shape = ComponentShapes.Special.Badge
            )
            .then(
                if (darkTheme) {
                    Modifier
                } else {
                    Modifier.border(
                        width = 0.5.dp,
                        color = ChampionCartColors.Brand.NeonCoral.copy(alpha = 0.3f),
                        shape = ComponentShapes.Special.Badge
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayCount,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontSize = if (displayCount.length > 2) 10.sp else 11.sp
        )
    }
}

/**
 * Theme-Aware Glass Top App Bar
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
    val darkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (darkTheme) {
                    Modifier
                } else {
                    Modifier.shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(0.dp)
                    )
                }
            ),
        color = if (darkTheme) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        } else {
            MaterialTheme.colorScheme.background
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (darkTheme) {
                        Modifier.glass(
                            intensity = GlassIntensity.Medium,
                            style = GlassStyle.Subtle,
                            shape = RoundedCornerShape(0.dp),
                            darkTheme = darkTheme
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = navigationIcon ?: {},
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )
        }
    }
}

/**
 * Top Search Bar (already theme-aware)
 */
@Composable
fun TopSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkTheme = isSystemInDarkTheme()

    GlassSearchTopBar(
        searchQuery = query,
        onSearchQueryChange = onQueryChange,
        onBackClick = onBackClick,
        modifier = modifier,
        darkTheme = darkTheme
    )
}

/**
 * Theme-Aware Search Top App Bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassSearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "חיפוש מוצרים...",
    darkTheme: Boolean = isSystemInDarkTheme(),
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (darkTheme) {
                    Modifier
                } else {
                    Modifier.shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(0.dp)
                    )
                }
            ),
        color = if (darkTheme) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        } else {
            MaterialTheme.colorScheme.background
        }
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
}

/**
 * Theme-Aware Tab Row
 */
@Composable
fun GlassTabRow(
    selectedTabIndex: Int,
    tabs: List<TabItem>,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val config = ChampionCartTheme.config
    val darkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (darkTheme) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (darkTheme) {
                        Modifier.glass(
                            intensity = GlassIntensity.Light,
                            style = GlassStyle.Subtle,
                            shape = RoundedCornerShape(0.dp),
                            darkTheme = darkTheme
                        )
                    } else {
                        Modifier.background(
                            Color.White.copy(alpha = 0.98f)
                        )
                    }
                ),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            edgePadding = Spacing.m,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = ChampionCartColors.Brand.ElectricMint,
                    height = 3.dp
                )
            },
            divider = {
                if (!darkTheme) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
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
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Theme-Aware Navigation Rail for tablets
 */
@Composable
fun GlassNavigationRail(
    selectedItem: String,
    items: List<NavigationRailItem>,
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null
) {
    val darkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier,
        color = if (darkTheme) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        NavigationRail(
            modifier = Modifier
                .then(
                    if (darkTheme) {
                        Modifier.glass(
                            intensity = GlassIntensity.Medium,
                            style = GlassStyle.Subtle,
                            shape = RoundedCornerShape(0.dp),
                            darkTheme = darkTheme
                        )
                    } else {
                        Modifier.background(
                            Color.White.copy(alpha = 0.98f)
                        )
                    }
                ),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
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
                                    GlassBadge(count = item.badge, darkTheme = darkTheme)
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
                        indicatorColor = if (darkTheme) {
                            ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.15f)
                        } else {
                            ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.08f)
                        },
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
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