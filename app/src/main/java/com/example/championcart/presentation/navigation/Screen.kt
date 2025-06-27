package com.example.championcart.presentation.navigation

/**
 * Defines all navigation routes in the ChampionCart app
 */
sealed class Screen(val route: String) {
    // Core screens
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Profile : Screen("profile")

    // Product related screens
    object Search : Screen("search?query={query}") {
        fun createRoute(query: String? = null) =
            if (query != null) "search?query=$query" else "search"
    }

    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }

    object CategoryProducts : Screen("category/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: String, categoryName: String) =
            "category/$categoryId/$categoryName"
    }

    // Settings & preferences
    object Settings : Screen("settings")
    object CitySelection : Screen("city_selection")
    object SavedCarts : Screen("saved_carts")

    // Store specific
    object StoreComparison : Screen("store_comparison")
    object StoreDetail : Screen("store/{storeId}") {
        fun createRoute(storeId: String) = "store/$storeId"
    }

    // Utility screens
    object About : Screen("about")
    object Help : Screen("help")
    object PrivacyPolicy : Screen("privacy_policy")
    object TermsOfService : Screen("terms_of_service")
}

/**
 * Bottom navigation items
 */
enum class BottomNavItem(
    val screen: Screen,
    val iconRes: Int,
    val labelRes: Int
) {
    HOME(
        screen = Screen.Home,
        iconRes = com.example.championcart.R.drawable.ic_home,
        labelRes = com.example.championcart.R.string.nav_home
    ),
    SEARCH(
        screen = Screen.Search,
        iconRes = com.example.championcart.R.drawable.ic_search,
        labelRes = com.example.championcart.R.string.nav_search
    ),
    CART(
        screen = Screen.Cart,
        iconRes = com.example.championcart.R.drawable.ic_cart,
        labelRes = com.example.championcart.R.string.nav_cart
    ),
    PROFILE(
        screen = Screen.Profile,
        iconRes = com.example.championcart.R.drawable.ic_profile,
        labelRes = com.example.championcart.R.string.nav_profile
    )
}