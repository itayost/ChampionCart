package com.example.championcart.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.ui.theme.*

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
                .height(Dimensions.bottomNavHeight)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                        ),
                        startY = 0f,
                        endY = 100f
                    )
                )
        ) {
            // Glass morphism background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.bottomNavHeight)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(ComponentShapes.BottomSheet)
                    .background(MaterialTheme.extendedColors.glassFrosted)
                    .shadow(
                        elevation = Dimensions.elevationMedium,
                        shape = ComponentShapes.BottomSheet,
                        spotColor = MaterialTheme.extendedColors.shadowLight
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "rotation"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.extendedColors.electricMint
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "color"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Selected background glow
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.electricMintGlow,
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale)
        ) {
            Box {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.contentDescription,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            rotationZ = if (isSelected) iconRotation else 0f
                        },
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

            // Label with fade animation
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
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
            .background(MaterialTheme.extendedColors.errorRed)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
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
        Screen.Stores.route,
        Screen.Profile.route
    )
}