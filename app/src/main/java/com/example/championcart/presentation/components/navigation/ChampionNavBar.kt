package com.example.championcart.presentation.components.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.R
import com.example.championcart.ui.theme.*

/**
 * Navigation bar for ChampionCart with Electric Harmony design
 * Uses the existing icons: home, search, scan, cart, profile
 */
@Composable
fun ChampionNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    cartItemCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .glass(
                shape = Shapes.bottomSheet,
                elevation = 8.dp
            ),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), // Less transparent
        tonalElevation = 0.dp
    ) {
        // Home
        NavigationBarItem(
            selected = selectedRoute == NavigationRoute.HOME,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onNavigate(NavigationRoute.HOME)
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_home),
                    contentDescription = stringResource(R.string.nav_home),
                    modifier = Modifier.size(Size.icon)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.nav_home),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandColors.ElectricMint,
                selectedTextColor = BrandColors.ElectricMint,
                indicatorColor = BrandColors.ElectricMint.copy(alpha = 0.12f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Cart with badge
        NavigationBarItem(
            selected = selectedRoute == NavigationRoute.CART,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onNavigate(NavigationRoute.CART)
            },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = BrandColors.NeonCoral,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cart),
                        contentDescription = stringResource(R.string.nav_cart),
                        modifier = Modifier.size(Size.icon)
                    )
                }
            },
            label = {
                Text(
                    text = stringResource(R.string.nav_cart),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandColors.ElectricMint,
                selectedTextColor = BrandColors.ElectricMint,
                indicatorColor = BrandColors.ElectricMint.copy(alpha = 0.12f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )


        // Scan
        NavigationBarItem(
            selected = selectedRoute == NavigationRoute.SCAN,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onNavigate(NavigationRoute.SCAN)
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_scan),
                    contentDescription = stringResource(R.string.nav_scan),
                    modifier = Modifier.size(Size.icon)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.nav_scan),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandColors.ElectricMint,
                selectedTextColor = BrandColors.ElectricMint,
                indicatorColor = BrandColors.ElectricMint.copy(alpha = 0.12f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Search
        NavigationBarItem(
            selected = selectedRoute == NavigationRoute.SEARCH,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onNavigate(NavigationRoute.SEARCH)
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.nav_search),
                    modifier = Modifier.size(Size.icon)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.nav_search),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandColors.ElectricMint,
                selectedTextColor = BrandColors.ElectricMint,
                indicatorColor = BrandColors.ElectricMint.copy(alpha = 0.12f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Profile
        NavigationBarItem(
            selected = selectedRoute == NavigationRoute.PROFILE,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onNavigate(NavigationRoute.PROFILE)
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = stringResource(R.string.nav_profile),
                    modifier = Modifier.size(Size.icon)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.nav_profile),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandColors.ElectricMint,
                selectedTextColor = BrandColors.ElectricMint,
                indicatorColor = BrandColors.ElectricMint.copy(alpha = 0.12f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

// Navigation routes
object NavigationRoute {
    const val HOME = "home"
    const val SEARCH = "search"
    const val SCAN = "scan"
    const val CART = "cart"
    const val PROFILE = "profile"
}