package com.example.championcart.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

/**
 * Bottom navigation items - 4 main screens
 */
enum class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    val labelHebrew: String
) {
    HOME(
        screen = Screen.Home,
        icon = Icons.Default.Home,
        selectedIcon = Icons.Filled.Home,
        label = "Home",
        labelHebrew = "בית"
    ),
    SEARCH(
        screen = Screen.Search,
        icon = Icons.Default.Search,
        selectedIcon = Icons.Filled.Search,
        label = "Search",
        labelHebrew = "חיפוש"
    ),
    CART(
        screen = Screen.Cart,
        icon = Icons.Default.ShoppingCart,
        selectedIcon = Icons.Filled.ShoppingCart,
        label = "Cart",
        labelHebrew = "עגלה"
    ),
    PROFILE(
        screen = Screen.Profile,
        icon = Icons.Default.Person,
        selectedIcon = Icons.Filled.Person,
        label = "Profile",
        labelHebrew = "פרופיל"
    )
}

/**
 * Modern bottom navigation bar with Electric Harmony design
 */
@Composable
fun ChampionCartBottomBar(
    navController: NavController,
    cartItemCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val haptics = LocalHapticFeedback.current

    // Only show bottom bar on main screens
    if (!shouldShowBottomBar(currentRoute)) return

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(NavigationTokens.BottomBarHeight)
        ) {
            // Glassmorphic background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .glassmorphic(
                        intensity = GlassIntensity.Heavy,
                        shape = GlassmorphicShapes.BottomNav
                    )
            )

            // Navigation items
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = SpacingTokens.M),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem.values().forEach { item ->
                    val isSelected = currentRoute == item.screen.route

                    BottomNavItemView(
                        item = item,
                        isSelected = isSelected,
                        cartBadgeCount = if (item == BottomNavItem.CART) cartItemCount else 0,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (currentRoute != item.screen.route) {
                                navController.navigate(item.screen.route) {
                                    // Pop up to home to avoid building up a large back stack
                                    popUpTo(Screen.Home.route) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    cartBadgeCount: Int,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioMediumBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "scale"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(),
        label = "contentColor"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(SpacingTokens.S),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
        ) {
            // Icon with badge
            Box {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = item.labelHebrew,
                    modifier = Modifier.size(SizingTokens.IconM),
                    tint = contentColor
                )

                // Cart badge
                if (item == BottomNavItem.CART && cartBadgeCount > 0) {
                    CartBadge(
                        count = cartBadgeCount,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp)
                    )
                }
            }

            // Label
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = item.labelHebrew,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CartBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    val displayCount = if (count > 99) "99+" else count.toString()

    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.extended.neonCoral)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = SpringSpecs.DampingRatioMediumBounce,
                    stiffness = SpringSpecs.StiffnessMedium
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayCount,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp
            ),
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Determines if bottom bar should be shown for the current route
 */
private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Cart.route,
        Screen.Profile.route
    )
}