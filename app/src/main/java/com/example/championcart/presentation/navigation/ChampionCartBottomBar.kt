package com.example.championcart.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.presentation.components.GlassBottomNavigationBar
import com.example.championcart.presentation.components.BottomNavItem as ComponentBottomNavItem

@Composable
fun ChampionCartBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: BottomBarViewModel = hiltViewModel()
) {
    val cartItemCount by viewModel.cartItemCount.collectAsState()

    // Map navigation items to component items
    val items = BottomNavItem.values().map { navItem ->
        ComponentBottomNavItem(
            route = navItem.screen.route,
            icon = navItem.iconRes,
            label = navItem.labelRes,
            badge = if (navItem == BottomNavItem.CART && cartItemCount > 0) {
                cartItemCount.toString()
            } else null
        )
    }

    GlassBottomNavigationBar(
        navController = navController,
        items = items,
        modifier = modifier
    )
}
