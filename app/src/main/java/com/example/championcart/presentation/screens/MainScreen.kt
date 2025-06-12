package com.example.championcart.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.championcart.presentation.components.BottomNavBar
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.presentation.screens.cart.CartScreen
import com.example.championcart.presentation.screens.home.HomeScreen
import com.example.championcart.presentation.screens.profile.ProfileScreen
import com.example.championcart.presentation.screens.search.SearchScreen

@Composable
fun MainScreen(mainNavController: NavController) {  // Add parameter
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.Search.route) {
                SearchScreen()
            }
            composable(Screen.Cart.route) {
                CartScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(mainNavController)  // Pass main nav controller
            }
        }
    }
}