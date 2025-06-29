package com.example.championcart.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.championcart.presentation.screens.showcase.ComponentShowcaseScreen
import com.example.championcart.presentation.screens.home.SimpleHomeScreen

@Composable
fun ChampionCartNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen (temporary simple version)
        composable(route = Screen.Home.route) {
            SimpleHomeScreen(
                onNavigateToShowcase = {
                    navController.navigate(Screen.ComponentShowcase.route)
                }
            )
        }

        // Component Showcase Screen
        composable(route = Screen.ComponentShowcase.route) {
            ComponentShowcaseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}