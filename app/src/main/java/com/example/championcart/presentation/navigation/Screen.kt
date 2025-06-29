package com.example.championcart.presentation.navigation

sealed class Screen(val route: String) {
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
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }

    // Settings Screens
    object Settings : Screen("settings")
    object SavedCarts : Screen("saved_carts")

    // Development
    object ComponentShowcase : Screen("component_showcase")
}