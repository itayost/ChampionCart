package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.ui.theme.*
import dev.chrisbanes.haze.hazeChild

/**
 * Modern Glassmorphic Bottom Navigation Bar
 * Enhanced with animated glow effects inspired by Sina Samaki's implementation
 * Uses the existing BottomNavItem data class structure
 */
@Composable
fun ModernGlassBottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val darkTheme = isSystemInDarkTheme()
    val haptics = LocalHapticFeedback.current
    val config = ChampionCartTheme.config
    val hazeState = LocalHazeState.current

    // Find selected index
    val selectedIndex = items.indexOfFirst { it.route == currentDestination?.route }
        .coerceAtLeast(0)

    // Animated values for the glow effect
    val animatedSelectedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "selectedTab"
    )

    // Color animation based on selected route
    val animatedColor by animateColorAsState(
        targetValue = when (currentDestination?.route) {
            "home" -> ChampionCartColors.Brand.ElectricMint
            "search" -> ChampionCartColors.Brand.CosmicPurple
            "cart" -> ChampionCartColors.Brand.NeonCoral
            "profile" -> ChampionCartColors.Store.Victory
            else -> ChampionCartColors.Brand.ElectricMint
        },
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tabColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(Sizing.Navigation.bottomBarHeight)
            .padding(horizontal = Spacing.l)
    ) {
        // Animated glow layer behind the navigation bar
        if (!config.reduceMotion && config.enableMicroAnimations) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .blur(40.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            ) {
                if (items.isNotEmpty()) {
                    val tabWidth = size.width / items.size
                    drawCircle(
                        color = animatedColor.copy(alpha = 0.5f),
                        radius = size.height / 2.5f,
                        center = Offset(
                            (tabWidth * animatedSelectedIndex) + tabWidth / 2,
                            size.height / 2
                        )
                    )
                }
            }

            // Bottom gleam effect
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            ) {
                if (items.isNotEmpty()) {
                    val path = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = size.toRect(),
                                cornerRadius = CornerRadius(size.height)
                            )
                        )
                    }
                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(path, false)
                    val pathLength = pathMeasure.length

                    val tabWidth = size.width / items.size

                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                animatedColor.copy(alpha = 0f),
                                animatedColor.copy(alpha = 0.6f),
                                animatedColor.copy(alpha = 0.6f),
                                animatedColor.copy(alpha = 0f)
                            ),
                            startX = tabWidth * animatedSelectedIndex,
                            endX = tabWidth * (animatedSelectedIndex + 1)
                        ),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(pathLength / 2, pathLength)
                            )
                        )
                    )
                }
            }
        }

        // Main navigation bar container
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (hazeState != null && config.enableBlurEffects) {
                        Modifier.hazeChild(
                            state = hazeState,
                            shape = CircleShape
                        )
                    } else {
                        Modifier.modernGlass(
                            intensity = GlassIntensity.Heavy,
                            shape = CircleShape,
                            hazeState = hazeState
                        )
                    }
                )
                .border(
                    width = Dp.Hairline,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (darkTheme) 0.2f else 0.8f),
                            Color.White.copy(alpha = if (darkTheme) 0.05f else 0.2f)
                        )
                    ),
                    shape = CircleShape
                ),
            shape = CircleShape,
            color = if (darkTheme) {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            },
            shadowElevation = 0.dp
        ) {
            NavigationBar(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp
            ) {
                items.forEachIndexed { index, item ->
                    val selected = currentDestination?.route == item.route

                    ModernNavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (config.enableHaptics) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
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
                            ModernNavIcon(
                                icon = item.icon,
                                selected = selected,
                                badge = item.badge as Int?,
                                index = index,
                                animatedSelectedIndex = animatedSelectedIndex,
                                animatedColor = animatedColor
                            )
                        },
                        label = {
                            AnimatedVisibility(
                                visible = selected,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = animatedColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Modern Navigation Bar Item with enhanced animations
 */
@Composable
private fun RowScope.ModernNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    val config = ChampionCartTheme.config

    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.9f,
        animationSpec = if (!config.reduceMotion) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        } else {
            snap()
        },
        label = "navItemScale"
    )

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
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
            selectedIconColor = MaterialTheme.colorScheme.onSurface,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            indicatorColor = Color.Transparent,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    )
}

/**
 * Modern Navigation Icon with proximity-based animations
 */
@Composable
private fun ModernNavIcon(
    icon: ImageVector,
    selected: Boolean,
    badge: Int?,
    index: Int,
    animatedSelectedIndex: Float,
    animatedColor: Color
) {
    val config = ChampionCartTheme.config

    // Calculate proximity to selected index for wave effect
    val distance = kotlin.math.abs(index - animatedSelectedIndex)
    val proximityScale = if (!config.reduceMotion && config.enableMicroAnimations) {
        1f + (0.1f * (1f - (distance / 2f).coerceIn(0f, 1f)))
    } else {
        1f
    }

    val iconColor by animateColorAsState(
        targetValue = if (selected) animatedColor else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "iconColor"
    )

    Box(
        modifier = Modifier.graphicsLayer {
            scaleX = proximityScale
            scaleY = proximityScale
        }
    ) {
        BadgedBox(
            badge = {
                if (badge != null && badge > 0) {
                    ModernGlassBadge(
                        count = badge,
                        color = if (selected) animatedColor else ChampionCartColors.Brand.NeonCoral
                    )
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
        }
    }
}

/**
 * Modern Glass Badge with animation
 */
@Composable
fun ModernGlassBadge(
    count: Int,
    color: Color = ChampionCartColors.Brand.NeonCoral,
    modifier: Modifier = Modifier
) {
    val displayCount = if (count > 99) "99+" else count.toString()
    val config = ChampionCartTheme.config

    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = if (!config.reduceMotion) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else {
            snap()
        },
        label = "badgeScale"
    )

    Box(
        modifier = modifier
            .size(if (displayCount.length > 2) 24.dp else 20.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color,
                        color.copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayCount,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontSize = if (displayCount.length > 2) 10.sp else 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val badge: Int? = null
)