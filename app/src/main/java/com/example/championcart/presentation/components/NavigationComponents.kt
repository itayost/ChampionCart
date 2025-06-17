package com.example.championcart.presentation.components

package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Navigation Components
 * Glassmorphic navigation system with Electric Harmony design
 */

// Navigation destinations - 4 core pages
sealed class NavigationDestination(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val label: String,
    val labelHebrew: String,
    val hasNotification: Boolean = false,
    val notificationCount: Int = 0
) {
    object Home : NavigationDestination(
        route = "home",
        icon = Icons.Default.Home,
        selectedIcon = Icons.Default.Home,
        label = "Home",
        labelHebrew = "בית"
    )

    object Search : NavigationDestination(
        route = "search",
        icon = Icons.Default.Search,
        selectedIcon = Icons.Default.Search,
        label = "Search",
        labelHebrew = "חיפוש"
    )

    object Cart : NavigationDestination(
        route = "cart",
        icon = Icons.Default.ShoppingCart,
        selectedIcon = Icons.Default.ShoppingCart,
        label = "Cart",
        labelHebrew = "עגלה",
        hasNotification = true,
        notificationCount = 3
    )

    object Profile : NavigationDestination(
        route = "profile",
        icon = Icons.Default.Person,
        selectedIcon = Icons.Default.Person,
        label = "Profile",
        labelHebrew = "פרופיל"
    )
}

/**
 * Main Glassmorphic Bottom Navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier,
    destinations: List<NavigationDestination> = listOf(
        NavigationDestination.Home,
        NavigationDestination.Search,
        NavigationDestination.Cart,
        NavigationDestination.Profile
    )
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    GlassNavBar(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingTokens.S),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { destination ->
                val isSelected = currentRoute == destination.route

                BottomNavigationItem(
                    destination = destination,
                    isSelected = isSelected,
                    onClick = {
                        if (currentRoute != destination.route) {
                            navController.navigate(destination.route) {
                                // Pop up to the start destination to avoid building a large stack
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Individual Bottom Navigation Item
 */
@Composable
private fun BottomNavigationItem(
    destination: NavigationDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = SpringSpecs.Playful,
        label = "navItemScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        animationSpec = SpringSpecs.Smooth,
        label = "navItemColor"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        animationSpec = SpringSpecs.Smooth,
        label = "navItemBackground"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clip(ComponentShapes.Navigation.BottomNav)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(vertical = SpacingTokens.S, horizontal = SpacingTokens.XS),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
    ) {
        // Icon with notification badge
        Box {
            Icon(
                imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                contentDescription = destination.labelHebrew,
                tint = iconColor,
                modifier = Modifier.size(NavigationTokens.IconSize)
            )

            // Notification badge
            if (destination.hasNotification && destination.notificationCount > 0) {
                NotificationBadge(
                    count = destination.notificationCount,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }

        // Label
        AnimatedVisibility(
            visible = isSelected,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Text(
                text = destination.labelHebrew,
                style = MaterialTheme.typography.labelSmall,
                color = iconColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Notification Badge
 */
@Composable
private fun NotificationBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    if (count > 0) {
        val displayText = if (count > 99) "99+" else count.toString()

        Surface(
            modifier = modifier
                .offset(x = 4.dp, y = (-4).dp)
                .size(if (count > 9) 20.dp else 16.dp),
            color = MaterialTheme.colorScheme.error,
            shape = CircleShape
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize(Alignment.Center),
                fontSize = if (count > 9) 8.sp else 10.sp
            )
        }
    }
}

/**
 * Glassmorphic Top App Bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    showSearch: Boolean = true,
    showNotifications: Boolean = true,
    showProfile: Boolean = true,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = null,
    notificationCount: Int = 0,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val timeBasedGreeting = getTimeBasedGreeting()
    val actualTitle = if (title.isEmpty()) timeBasedGreeting else title

    TopAppBar(
        title = {
            Column {
                Text(
                    text = actualTitle,
                    style = AppTextStyles.hebrewHeadline.withSmartHebrewSupport(actualTitle),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                subtitle?.let { sub ->
                    Text(
                        text = sub,
                        style = AppTextStyles.hebrewBodyMedium.withSmartHebrewSupport(sub),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = navigationIcon ?: {},
        actions = {
            TopAppBarActions(
                showSearch = showSearch,
                showNotifications = showNotifications,
                showProfile = showProfile,
                onSearchClick = onSearchClick,
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick,
                notificationCount = notificationCount
            )
        },
        modifier = modifier.glassNavigation(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = scrollBehavior
    )
}

/**
 * Top App Bar Actions
 */
@Composable
private fun TopAppBarActions(
    showSearch: Boolean,
    showNotifications: Boolean,
    showProfile: Boolean,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    notificationCount: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showSearch) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.glassButton(GlassIntensity.Light)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.extended.electricMint
                )
            }
        }

        if (showNotifications) {
            Box {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.glassButton(GlassIntensity.Light)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.extended.cosmicPurple
                    )
                }

                if (notificationCount > 0) {
                    NotificationBadge(
                        count = notificationCount,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        }

        if (showProfile) {
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.glassButton(GlassIntensity.Light)
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Navigation Drawer for Tablets
 */
@Composable
fun NavigationDrawer(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    destinations: List<NavigationDestination> = listOf(
        NavigationDestination.Home,
        NavigationDestination.Search,
        NavigationDestination.Cart,
        NavigationDestination.Profile
    ),
    header: @Composable () -> Unit = { DrawerHeader() }
) {
    PermanentNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            PermanentDrawerSheet(
                modifier = Modifier
                    .width(280.dp)
                    .glassNavigation(),
                drawerContainerColor = Color.Transparent,
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpacingTokens.L)
                ) {
                    // Header
                    header()

                    Spacer(modifier = Modifier.height(SpacingTokens.XL))

                    // Navigation Items
                    destinations.forEach { destination ->
                        val isSelected = currentRoute == destination.route

                        DrawerNavigationItem(
                            destination = destination,
                            isSelected = isSelected,
                            onClick = { onNavigate(destination.route) }
                        )

                        Spacer(modifier = Modifier.height(SpacingTokens.S))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Footer actions
                    DrawerFooter()
                }
            }
        },
        content = {}
    )
}

/**
 * Drawer Header
 */
@Composable
private fun DrawerHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        // App Logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.extended.electricMint,
                            MaterialTheme.colorScheme.extended.cosmicPurple
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Champion Cart",
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
        }

        // App Name and Version
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Champion Cart",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "גרסה 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Drawer Navigation Item
 */
@Composable
private fun DrawerNavigationItem(
    destination: NavigationDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.15f)
        } else {
            Color.Transparent
        },
        animationSpec = SpringSpecs.Smooth,
        label = "drawerItemBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = SpringSpecs.Smooth,
        label = "drawerItemContent"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(GlassmorphicShapes.GlassCard)
            .clickable { onClick() },
        color = backgroundColor,
        shape = GlassmorphicShapes.GlassCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.M),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            Box {
                Icon(
                    imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                    contentDescription = destination.labelHebrew,
                    tint = contentColor,
                    modifier = Modifier.size(SizingTokens.IconL)
                )

                // Notification badge
                if (destination.hasNotification && destination.notificationCount > 0) {
                    NotificationBadge(
                        count = destination.notificationCount,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }

            Text(
                text = destination.labelHebrew,
                style = AppTextStyles.hebrewBodyLarge.withSmartHebrewSupport(destination.labelHebrew),
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

/**
 * Drawer Footer
 */
@Composable
private fun DrawerFooter() {
    Column(
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { /* Settings */ },
                modifier = Modifier.glassButton(GlassIntensity.Light)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = { /* Help */ },
                modifier = Modifier.glassButton(GlassIntensity.Light)
            ) {
                Icon(
                    Icons.Default.Help,
                    contentDescription = "Help",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = { /* Dark Mode Toggle */ },
                modifier = Modifier.glassButton(GlassIntensity.Light)
            ) {
                Icon(
                    Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Navigation Rail for Medium Screens
 */
@Composable
fun NavigationRail(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    destinations: List<NavigationDestination> = listOf(
        NavigationDestination.Home,
        NavigationDestination.Search,
        NavigationDestination.Cart,
        NavigationDestination.Profile
    )
) {
    androidx.compose.material3.NavigationRail(
        modifier = modifier.glassNavigation(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        header = {
            // FAB or Logo
            FloatingActionButton(
                onClick = { onNavigate(NavigationDestination.Search.route) },
                modifier = Modifier.padding(bottom = SpacingTokens.L),
                containerColor = MaterialTheme.colorScheme.extended.electricMint
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    ) {
        destinations.forEach { destination ->
            val isSelected = currentRoute == destination.route

            NavigationRailItem(
                selected = isSelected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Box {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                            contentDescription = destination.labelHebrew
                        )

                        if (destination.hasNotification && destination.notificationCount > 0) {
                            NotificationBadge(
                                count = destination.notificationCount,
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = destination.labelHebrew,
                        style = MaterialTheme.typography.labelSmall.withSmartHebrewSupport(destination.labelHebrew)
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.extended.electricMint,
                    selectedTextColor = MaterialTheme.colorScheme.extended.electricMint,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
                )
            )
        }
    }
}

// Preview Data and Composables
@Preview(name = "Bottom Navigation")
@Composable
private fun BottomNavigationPreview() {
    ChampionCartTheme {
        Surface {
            // Mock NavController for preview
            Box(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color.Transparent
                ) {
                    // Simple preview without NavController
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassNavigation()
                            .padding(SpacingTokens.S),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            NavigationDestination.Home,
                            NavigationDestination.Search,
                            NavigationDestination.Cart,
                            NavigationDestination.Profile
                        ).forEachIndexed { index, destination ->
                            BottomNavigationItem(
                                destination = destination,
                                isSelected = index == 0,
                                onClick = {},
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Top App Bar")
@Composable
private fun TopAppBarPreview() {
    ChampionCartTheme {
        GlassmorphicTopAppBar(
            title = "",
            subtitle = "חסכו כסף על הקניות שלכם",
            notificationCount = 5
        )
    }
}

@Preview(name = "Navigation Drawer", widthDp = 400)
@Composable
private fun NavigationDrawerPreview() {
    ChampionCartTheme {
        Surface {
            PermanentDrawerSheet(
                modifier = Modifier
                    .width(280.dp)
                    .glassNavigation(),
                drawerContainerColor = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpacingTokens.L)
                ) {
                    DrawerHeader()
                    Spacer(modifier = Modifier.height(SpacingTokens.XL))

                    listOf(
                        NavigationDestination.Home,
                        NavigationDestination.Search,
                        NavigationDestination.Cart,
                        NavigationDestination.Profile
                    ).forEachIndexed { index, destination ->
                        DrawerNavigationItem(
                            destination = destination,
                            isSelected = index == 0,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(SpacingTokens.S))
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    DrawerFooter()
                }
            }
        }
    }
}