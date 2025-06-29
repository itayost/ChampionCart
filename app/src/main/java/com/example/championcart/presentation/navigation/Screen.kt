package com.example.championcart.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    // Splash Screen
    object Splash : Screen("splash")

    // Main Screens
    object Home : Screen("home")
    object Search : Screen("search")
    object Cart : Screen("cart")
    object Profile : Screen("profile")

    // Auth Screens
    object Login : Screen("login")
    object Register : Screen("register")
    object Onboarding : Screen("onboarding")

    // Product Screens
    object ProductDetail : Screen(
        route = "product/{productId}",
        arguments = listOf(
            navArgument("productId") { type = NavType.StringType }
        )
    ) {
        fun createRoute(productId: String) = "product/$productId"
    }

    // Settings Screens
    object Settings : Screen("settings")
    object SavedCarts : Screen("saved_carts")

    // Development
    object ComponentShowcase : Screen("component_showcase")
}