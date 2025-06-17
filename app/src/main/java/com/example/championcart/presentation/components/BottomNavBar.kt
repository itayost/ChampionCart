package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.data.local.CartManager
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    val labelHebrew: String
) {
    object Home : BottomNavItem(
        Screen.Home.route,
        Icons.Outlined.Home,
        Icons.Filled.Home,
        "Home",
        "בית"
    )
    object Search : BottomNavItem(
        Screen.Search.route,
        Icons.Outlined.Search,
        Icons.Filled.Search,
        "Search",
        "חיפוש"
    )
    object Cart : BottomNavItem(
        Screen.Cart.route,
        Icons.Outlined.ShoppingCart,
        Icons.Filled.ShoppingCart,
        "Cart",
        "עגלה"
    )
    object Profile : BottomNavItem(
        Screen.Profile.route,
        Icons.Outlined.Person,
        Icons.Filled.Person,
        "Profile",
        "פרופיל"
    )
}

@Composable
fun BottomNavBar(navController: NavController) {
    val context = LocalContext.current
    val cartManager = CartManager.getInstance(context)
    val cartCount by cartManager.cartCount.collectAsState()
    val haptics = LocalHapticFeedback.current

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Cart,
        BottomNavItem.Profile
    )

    // Enhanced NavigationBar with glassmorphic effect
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .glassmorphic(
                intensity = GlassIntensity.Light,
                shape = ComponentShapes.Navigation.BottomNav,
                shadowElevation = 8.dp
            ),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            // Animated scale for selection
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "navItemScale"
            )

            // Animated colors
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.extended.electricMint
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(300),
                label = "iconColor"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Haptic feedback
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)

                    if (currentRoute != item.route) {
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
                    Box(
                        modifier = Modifier.scale(scale),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item == BottomNavItem.Cart && cartCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.extended.neonCoral,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = cartCount.toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                                    contentDescription = item.label,
                                    tint = iconColor
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                                tint = iconColor
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = item.labelHebrew, // Hebrew first
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = if (isSelected) 13.sp else 12.sp
                        ),
                        color = iconColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.extended.electricMint,
                    selectedTextColor = MaterialTheme.colorScheme.extended.electricMint,
                    indicatorColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// Optional: Enhanced navigation item with more animations
@Composable
fun EnhancedNavigationBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    cartCount: Int = 0,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    // Multiple animated properties
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioMediumBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    )

    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    )

    // Rest of implementation...
}