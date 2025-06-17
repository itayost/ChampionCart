package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Modern bottom navigation with curtain pattern and magnetic pull effect
 * Features advanced animations and haptic feedback
 */
@Composable
fun ModernBottomNavBar(
    navController: NavController,
    cartItemCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // State for curtain navigation
    var isCurtainOpen by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }

    // Animation values
    val curtainHeight by animateDpAsState(
        targetValue = if (isCurtainOpen) 400.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "curtainHeight"
    )

    val blurRadius by animateDpAsState(
        targetValue = if (isCurtainOpen) 20.dp else 0.dp,
        animationSpec = tween(300),
        label = "blurRadius"
    )

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Main navigation bar
        NavigationBarWithEffects(
            navController = navController,
            cartItemCount = cartItemCount,
            isCurtainOpen = isCurtainOpen,
            onMenuToggle = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                isCurtainOpen = !isCurtainOpen
            }
        )

        // Curtain overlay
        AnimatedVisibility(
            visible = isCurtainOpen,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            ) + fadeOut()
        ) {
            CurtainNavigationMenu(
                currentRoute = navController.currentDestination?.route,
                onNavigate = { route ->
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    isCurtainOpen = false
                },
                onDismiss = { isCurtainOpen = false },
                dragOffset = dragOffset,
                onDrag = { offset -> dragOffset = offset }
            )
        }
    }
}

@Composable
private fun NavigationBarWithEffects(
    navController: NavController,
    cartItemCount: Int,
    isCurtainOpen: Boolean,
    onMenuToggle: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Cart,
            BottomNavItem.Profile
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                spotColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .glassmorphic(
                    intensity = GlassIntensity.Medium,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
        ) {
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Menu button (for curtain navigation)
                MagneticIconButton(
                    icon = if (isCurtainOpen) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = "Menu",
                    onClick = onMenuToggle,
                    isSelected = isCurtainOpen,
                    modifier = Modifier.weight(1f)
                )

                // Navigation items
                items.forEach { item ->
                    MagneticNavItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        cartCount = if (item == BottomNavItem.Cart) cartItemCount else 0,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MagneticNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    cartCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var magnetOffset by remember { mutableStateOf(Offset.Zero) }
    val interactionSource = remember { MutableInteractionSource() }

    // Magnetic pull animation
    val animatedOffsetX by animateFloatAsState(
        targetValue = magnetOffset.x,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 800f
        ),
        label = "magnetX"
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = magnetOffset.y,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 800f
        ),
        label = "magnetY"
    )

    // Scale animation
    val scale by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.15f
            magnetOffset != Offset.Zero -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "itemScale"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .offset { IntOffset(animatedOffsetX.roundToInt(), animatedOffsetY.roundToInt()) }
            .scale(scale)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDragEnd = {
                        magnetOffset = Offset.Zero
                        onClick()
                    },
                    onDrag = { _, dragAmount ->
                        val distance = kotlin.math.sqrt(
                            dragAmount.x * dragAmount.x + dragAmount.y * dragAmount.y
                        )
                        if (distance < 100) {
                            magnetOffset = Offset(
                                magnetOffset.x + dragAmount.x * 0.3f,
                                magnetOffset.y + dragAmount.y * 0.3f
                            )
                        }
                    }
                )
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = item.labelHebrew,
                    tint = animateColorAsState(
                        targetValue = if (isSelected) {
                            MaterialTheme.colorScheme.extended.electricMint
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        },
                        animationSpec = SpringSpecs.A,
                        label = "iconColor"
                    ).value,
                    modifier = Modifier.size(24.dp)
                )

                // Cart badge
                if (cartCount > 0 && item == BottomNavItem.Cart) {
                    CartBadge(
                        count = cartCount,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-4).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.labelHebrew,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = if (isSelected) 11.sp else 10.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.extended.electricMint
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
        }
    }
}

@Composable
private fun CartBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = 0.3f,
            stiffness = 1000f
        ),
        label = "badgeScale"
    )

    Box(
        modifier = modifier
            .size(18.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.error,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}

@Composable
private fun MagneticIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "menuRotation"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                rotationZ = rotation
            }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isSelected) {
                MaterialTheme.colorScheme.extended.electricMint
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun CurtainNavigationMenu(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onDismiss: () -> Unit,
    dragOffset: Float,
    onDrag: (Float) -> Unit
) {
    val additionalItems = remember {
        listOf(
            CurtainNavItem(
                route = "saved_carts",
                icon = Icons.Default.BookmarkBorder,
                label = "עגלות שמורות",
                labelEn = "Saved Carts"
            ),
            CurtainNavItem(
                route = "price_alerts",
                icon = Icons.Default.NotificationsNone,
                label = "התראות מחיר",
                labelEn = "Price Alerts"
            ),
            CurtainNavItem(
                route = "stores",
                icon = Icons.Default.Store,
                label = "חנויות",
                labelEn = "Stores"
            ),
            CurtainNavItem(
                route = "savings",
                icon = Icons.Default.TrendingDown,
                label = "חסכונות",
                labelEn = "Savings"
            ),
            CurtainNavItem(
                route = "settings",
                icon = Icons.Default.Settings,
                label = "הגדרות",
                labelEn = "Settings"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .offset { IntOffset(0, dragOffset.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (abs(dragOffset) > 100) {
                            onDismiss()
                        } else {
                            onDrag(0f)
                        }
                    },
                    onDrag = { _, dragAmount ->
                        onDrag(dragOffset + dragAmount.y)
                    }
                )
            }
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                spotColor = MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .glassmorphic(
                intensity = GlassIntensity.Heavy,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            // Menu items
            additionalItems.forEach { item ->
                CurtainMenuItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun CurtainMenuItem(
    item: CurtainNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.15f)
        } else {
            Color.Transparent
        },
        animationSpec = SpringSpecs.ColorAnimation,
        label = "menuItemBg"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCard,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.extended.electricMint
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.extended.electricMint
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = item.labelEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.extended.electricMint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Data classes
private data class CurtainNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val labelEn: String
)

// Reuse existing BottomNavItem from the original component
private sealed class BottomNavItem(
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