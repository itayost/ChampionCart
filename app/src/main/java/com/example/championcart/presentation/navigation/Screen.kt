package com.example.championcart.presentation.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.championcart.ui.theme.ChampionCartIcons

/**
 * Sealed class representing all navigation destinations in the app
 */
sealed class Screen(
    val route: String,
    val arguments: List<androidx.navigation.NamedNavArgument> = emptyList()
) {
    // Auth flow screens
    object Splash : Screen("splash")
    object Auth : Screen("auth")

    // Main screens (bottom nav)
    object Home : Screen("home")
    object Search : Screen("search")
    object Cart : Screen("cart")
    object Stores : Screen("stores")
    object Profile : Screen("profile")

    // Detail screens
    object ProductDetail : Screen(
        route = "product/{productId}",
        arguments = listOf(
            navArgument("productId") { type = NavType.StringType }
        )
    ) {
        fun createRoute(productId: String) = "product/$productId"
    }

    object StoreDetail : Screen(
        route = "store/{storeId}",
        arguments = listOf(
            navArgument("storeId") { type = NavType.StringType }
        )
    ) {
        fun createRoute(storeId: String) = "store/$storeId"
    }

    // Settings and other screens
    object Settings : Screen("settings")
    object SavedCarts : Screen("saved_carts")
    object PriceAlerts : Screen("price_alerts")
    object About : Screen("about")

    // Checkout flow
    object Checkout : Screen("checkout")
    object CheckoutSuccess : Screen("checkout_success")
}

/**
 * Bottom navigation items configuration with custom ChampionCart icons
 */
enum class BottomNavItem(
    val screen: Screen,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val contentDescription: String
) {
    HOME(
        screen = Screen.Home,
        selectedIcon = ChampionCartIcons.Home,
        unselectedIcon = ChampionCartIcons.Home,
        label = "Home",
        contentDescription = "Home screen"
    ),
    SEARCH(
        screen = Screen.Search,
        selectedIcon = ChampionCartIcons.Search,
        unselectedIcon = ChampionCartIcons.Search,
        label = "Search",
        contentDescription = "Search products"
    ),
    CART(
        screen = Screen.Cart,
        selectedIcon = ChampionCartIcons.Cart,
        unselectedIcon = ChampionCartIcons.Cart,
        label = "Cart",
        contentDescription = "Shopping cart"
    ),
    PROFILE(
        screen = Screen.Profile,
        selectedIcon = ChampionCartIcons.Profile,
        unselectedIcon = ChampionCartIcons.Profile,
        label = "Profile",
        contentDescription = "User profile"
    )
}