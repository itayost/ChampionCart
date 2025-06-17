package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.unit.sp
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Navigation Components
 * Hebrew-first navigation with glassmorphic Electric Harmony design
 * Optimized for RTL layout and Israeli shopping patterns
 */

/**
 * Navigation destination data class
 */
data class NavigationDestination(
    val route: String,
    val labelEnglish: String,
    val labelHebrew: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val hasNotification: Boolean = false,
    val notificationCount: Int = 0
)

/**
 * Bottom Navigation Item with Electric Harmony animations
 */
@Composable
fun BottomNavigationItem(
    destination: NavigationDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "navItemScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "navItemColor"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "navItemBackground"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clip(ComponentShapes.Navigation.BottomNav)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
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
        Text(
            text = destination.labelHebrew,
            style = MaterialTheme.typography.labelSmall,
            color = iconColor,
            maxLines = 1
        )
    }
}

/**
 * Notification Badge Component
 */
@Composable
fun NotificationBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    if (count > 0) {
        Box(
            modifier = modifier
                .size(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.error,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onError,
                maxLines = 1
            )
        }
    }
}

/**
 * Navigation Rail Item for medium screens
 */
@Composable
fun NavigationRailItem(
    destination: NavigationDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.12f)
        } else {
            Color.Transparent
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "railItemBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "railItemContent"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ComponentShapes.Home.WelcomeCard)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(SpacingTokens.M),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
    ) {
        // Icon with notification
        Box {
            Icon(
                imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                contentDescription = destination.labelHebrew,
                tint = contentColor,
                modifier = Modifier.size(NavigationTokens.IconSize)
            )

            if (destination.hasNotification && destination.notificationCount > 0) {
                NotificationBadge(
                    count = destination.notificationCount,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }

        // Label
        Text(
            text = destination.labelHebrew,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1
        )
    }
}

/**
 * Navigation Drawer Item for large screens
 */
@Composable
fun NavigationDrawerItem(
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
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "drawerItemBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "drawerItemContent"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ComponentShapes.Navigation.FAB)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        // Icon
        Box {
            Icon(
                imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                contentDescription = destination.labelHebrew,
                tint = contentColor,
                modifier = Modifier.size(NavigationTokens.IconSize)
            )

            if (destination.hasNotification && destination.notificationCount > 0) {
                NotificationBadge(
                    count = destination.notificationCount,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }

        // Label
        Text(
            text = destination.labelHebrew,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )

        // Trailing notification count for large numbers
        if (destination.hasNotification && destination.notificationCount > 99) {
            Text(
                text = "${destination.notificationCount}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Glassmorphic Navigation Container
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicNavigationContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.extended.surfaceGlass,
        tonalElevation = NavigationTokens.ContainerElevation,
        shadowElevation = NavigationTokens.ContainerElevation,
        content = content
    )
}

/**
 * Drawer Header Component
 */
@Composable
fun DrawerHeader() {
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
 * Navigation Tokens for consistent spacing and sizing
 */
object NavigationTokens {
    val IconSize = 24.dp
    val ContainerElevation = 3.dp
    val ItemMinHeight = 56.dp
    val BadgeSize = 16.dp
    val BadgeOffset = 8.dp
}