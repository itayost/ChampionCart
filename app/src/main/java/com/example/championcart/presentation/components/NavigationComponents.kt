package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

/**
 * Champion Cart Navigation Components
 * Consistent navigation elements across the app
 */

/**
 * Main bottom navigation bar
 */
@Composable
fun ChampionCartBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 0.dp
    ) {
        Screen.getBottomNavItems().forEach { screen ->
            ChampionCartNavItem(
                screen = screen,
                isSelected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * Individual navigation item
 */
@Composable
private fun RowScope.ChampionCartNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "nav_item_scale"
    )

    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "nav_item_color"
    )

    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier.scale(animatedScale),
                contentAlignment = Alignment.Center
            ) {
                // Glow effect for selected item
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Icon(
                    imageVector = if (isSelected) {
                        screen.selectedIcon ?: screen.icon!!
                    } else {
                        screen.icon!!
                    },
                    contentDescription = screen.labelHebrew,
                    tint = animatedColor
                )
            }
        },
        label = {
            Text(
                text = screen.labelHebrew ?: "",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = animatedColor
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}

/**
 * Navigation rail for tablets
 */
@Composable
fun ChampionCartNavigationRail(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Spacer(modifier = Modifier.height(SpacingTokens.L))

        Screen.getBottomNavItems().forEach { screen ->
            ChampionCartNavRailItem(
                screen = screen,
                isSelected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * Navigation rail item
 */
@Composable
private fun ChampionCartNavRailItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationRailItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = if (isSelected) {
                    screen.selectedIcon ?: screen.icon!!
                } else {
                    screen.icon!!
                },
                contentDescription = screen.labelHebrew
            )
        },
        label = {
            Text(
                text = screen.labelHebrew ?: "",
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.extended.electricMint,
            selectedTextColor = MaterialTheme.colorScheme.extended.electricMint,
            indicatorColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
        )
    )
}

/**
 * Cart badge for navigation
 */
@Composable
fun CartBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    if (count > 0) {
        Box(
            modifier = modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.extended.neonCoral),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onError,
                fontWeight = FontWeight.Bold
            )
        }
    }
}