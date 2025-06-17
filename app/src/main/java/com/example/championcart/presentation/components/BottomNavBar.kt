package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Bottom Navigation Bar
 * Glassmorphic design with Electric Harmony theme
 * Hebrew-first with RTL support
 */

// Navigation destinations
sealed class BottomNavDestination(
    val route: String,
    val icon: ImageVector,
    val labelEnglish: String,
    val labelHebrew: String
) {
    object Home : BottomNavDestination(
        route = "home",
        icon = Icons.Default.Home,
        labelEnglish = "Home",
        labelHebrew = "בית"
    )

    object Search : BottomNavDestination(
        route = "search",
        icon = Icons.Default.Search,
        labelEnglish = "Search",
        labelHebrew = "חיפוש"
    )

    object Cart : BottomNavDestination(
        route = "cart",
        icon = Icons.Default.ShoppingCart,
        labelEnglish = "Cart",
        labelHebrew = "עגלה"
    )

    object Savings : BottomNavDestination(
        route = "savings",
        icon = Icons.Default.Star,
        labelEnglish = "Savings",
        labelHebrew = "חיסכון"
    )

    object Profile : BottomNavDestination(
        route = "profile",
        icon = Icons.Default.Person,
        labelEnglish = "Profile",
        labelHebrew = "פרופיל"
    )
}

@Composable
fun ChampionCartBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    cartItemCount: Int = 0
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val destinations = listOf(
        BottomNavDestination.Home,
        BottomNavDestination.Search,
        BottomNavDestination.Cart,
        BottomNavDestination.Savings,
        BottomNavDestination.Profile
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        // Glassmorphic background using theme modifier
        Box(
            modifier = Modifier
                .fillMaxSize()
                .glassmorphic(
                    intensity = GlassIntensity.Heavy,
                    shape = GlassmorphicShapes.BottomNavigation,
                    borderWidth = 0.5.dp,
                    shadowElevation = 16.dp
                )
        )

        // Navigation items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { destination ->
                BottomNavItem(
                    destination = destination,
                    selected = currentRoute == destination.route,
                    onClick = {
                        if (currentRoute != destination.route) {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    badge = if (destination is BottomNavDestination.Cart && cartItemCount > 0) {
                        cartItemCount
                    } else null
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    destination: BottomNavDestination,
    selected: Boolean,
    onClick: () -> Unit,
    badge: Int? = null
) {
    val haptics = LocalHapticFeedback.current
    val colors = LocalExtendedColors.current

    // Animated values using theme SpringSpecs
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = SpringSpecs.Bouncy,
        label = "nav_item_scale"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (selected) 360f else 0f,
        animationSpec = SpringSpecs.Smooth,
        label = "nav_item_rotation"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 0.6f else 0f,
        animationSpec = tween(300),
        label = "nav_item_glow"
    )

    // Color animation using animateColorAsState
    val itemColor by animateColorAsState(
        targetValue = if (selected) colors.electricMint else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "nav_item_color"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Glow effect for selected state
        if (selected) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                colors.electricMintGlow.copy(alpha = glowAlpha),
                                Color.Transparent
                            ),
                            radius = 100f
                        )
                    )
                    .blur(16.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.labelHebrew,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            rotationZ = if (selected) iconRotation else 0f
                        },
                    tint = itemColor
                )

                // Badge for cart
                badge?.let { count ->
                    Box(
                        modifier = Modifier
                            .offset(x = 12.dp, y = (-8).dp)
                            .size(18.dp)
                            .background(
                                color = colors.neonCoral,
                                shape = CircleShape
                            )
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                spotColor = colors.neonCoralGlow
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (count > 9) "9+" else count.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = destination.labelHebrew,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = itemColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

/**
 * Alternative floating action button style navigation
 */
@Composable
fun FloatingBottomNav(
    navController: NavController,
    modifier: Modifier = Modifier,
    cartItemCount: Int = 0
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val colors = LocalExtendedColors.current

    val destinations = listOf(
        BottomNavDestination.Home,
        BottomNavDestination.Search,
        BottomNavDestination.Cart,
        BottomNavDestination.Savings,
        BottomNavDestination.Profile
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Floating glass container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .glassmorphic(
                    intensity = GlassIntensity.Ultra,
                    shape = RoundedCornerShape(32.dp),
                    borderWidth = 1.dp,
                    shadowElevation = 24.dp
                )
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { destination ->
                FloatingNavItem(
                    destination = destination,
                    selected = currentRoute == destination.route,
                    onClick = {
                        if (currentRoute != destination.route) {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    badge = if (destination is BottomNavDestination.Cart && cartItemCount > 0) {
                        cartItemCount
                    } else null
                )
            }
        }
    }
}

@Composable
private fun FloatingNavItem(
    destination: BottomNavDestination,
    selected: Boolean,
    onClick: () -> Unit,
    badge: Int? = null
) {
    val haptics = LocalHapticFeedback.current
    val colors = LocalExtendedColors.current

    // Animated selection indicator
    val indicatorWidth by animateDpAsState(
        targetValue = if (selected) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "indicator_width"
    )

    val itemScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = SpringSpecs.Snappy,
        label = "item_scale"
    )

    // Color animation
    val itemColor by animateColorAsState(
        targetValue = when {
            selected -> colors.electricMint
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        },
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "item_color"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptics.performHapticFeedback(
                    if (selected) HapticFeedbackType.LongPress
                    else HapticFeedbackType.TextHandleMove
                )
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Selection indicator
        AnimatedVisibility(
            visible = selected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .width(indicatorWidth)
                    .height(40.dp)
                    .background(
                        color = colors.electricMintGlow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    )
            )
        }

        // Icon with badge
        Box(
            modifier = Modifier.scale(itemScale)
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.labelHebrew,
                modifier = Modifier.size(24.dp),
                tint = itemColor
            )

            // Badge
            badge?.let { count ->
                Box(
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-8).dp)
                        .size(16.dp)
                        .background(
                            color = colors.neonCoral,
                            shape = CircleShape
                        )
                        .pulsingGlass(
                            intensity = GlassIntensity.Light,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}