package com.example.championcart.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.R
import com.example.championcart.presentation.components.ModernGlassBottomNavigationBar
import com.example.championcart.presentation.components.BottomNavItem as ComponentBottomNavItem
import com.example.championcart.presentation.navigation.BottomNavItem

@Composable
fun ChampionCartBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: BottomBarViewModel = hiltViewModel()
) {
    val cartItemCount by viewModel.cartItemCount.collectAsState()
    val context = LocalContext.current

    // Map navigation items to component items
    val items = BottomNavItem.values().map { navItem ->
        ComponentBottomNavItem(
            route = navItem.screen.route,
            icon = when (navItem) {
                BottomNavItem.HOME -> Icons.Default.Home
                BottomNavItem.SEARCH -> Icons.Default.Search
                BottomNavItem.CART -> Icons.Default.ShoppingCart
                BottomNavItem.PROFILE -> Icons.Default.Person
            },
            label = context.getString(navItem.labelRes),
            badge = if (navItem == BottomNavItem.CART && cartItemCount > 0) {
                cartItemCount
            } else null
        )
    }

    // Use the modern glassmorphic bottom navigation with animated glow
    ModernGlassBottomNavigationBar(
        navController = navController,
        items = items,
        modifier = modifier
    )
}