package com.example.championcart.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Unified navigation definition for Champion Cart
 * Single source of truth for all routes and navigation items
 */
sealed class Screen(
    val route: String,
    val arguments: List<androidx.navigation.NamedNavArgument> = emptyList(),
    // Bottom nav specific properties
    val icon: ImageVector? = null,
    val selectedIcon: ImageVector? = null,
    val labelEnglish: String? = null,
    val labelHebrew: String? = null,
    val showInBottomNav: Boolean = false
) {
    // ===== BOTTOM NAV SCREENS =====
    object Home : Screen(
        route = "home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        labelEnglish = "Home",
        labelHebrew = "בית",
        showInBottomNav = true
    )

    object Search : Screen(
        route = "search",
        icon = Icons.Outlined.Search,
        selectedIcon = Icons.Filled.Search,
        labelEnglish = "Search",
        labelHebrew = "חיפוש",
        showInBottomNav = true
    )

    object Cart : Screen(
        route = "cart",
        icon = Icons.Outlined.ShoppingCart,
        selectedIcon = Icons.Filled.ShoppingCart,
        labelEnglish = "Cart",
        labelHebrew = "עגלה",
        showInBottomNav = true
    )

    object Profile : Screen(
        route = "profile",
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person,
        labelEnglish = "Profile",
        labelHebrew = "פרופיל",
        showInBottomNav = true
    )

    // ===== AUTH FLOW SCREENS =====
    object Splash : Screen(
        route = "splash",
        showInBottomNav = false
    )

    object Auth : Screen(
        route = "auth",
        showInBottomNav = false
    )

    // ===== DETAIL SCREENS =====
    object ProductDetail : Screen(
        route = "product/{productId}",
        arguments = listOf(
            navArgument("productId") { type = NavType.StringType }
        ),
        showInBottomNav = false
    ) {
        fun createRoute(productId: String) = "product/$productId"
    }

    // ===== SETTINGS & OTHER SCREENS =====
    object Settings : Screen(
        route = "settings",
        showInBottomNav = false
    )

    object SavedCarts : Screen(
        route = "saved_carts",
        showInBottomNav = false
    )
    companion object {
        /**
         * Get all screens that should appear in bottom navigation
         * Using explicit list instead of reflection to avoid runtime dependency
         */
        fun getBottomNavItems(): List<Screen> {
            return listOf(
                Home,
                Search,
                Cart,
                Profile
            ).filter { it.showInBottomNav }
        }

        /**
         * Check if bottom bar should be shown for current route
         */
        fun shouldShowBottomBar(currentRoute: String?): Boolean {
            return getBottomNavItems().any { it.route == currentRoute }
        }

        /**
         * Get all available screens (if needed elsewhere)
         */
        fun getAllScreens(): List<Screen> {
            return listOf(
                // Bottom nav screens
                Home,
                Search,
                Cart,
                Profile,
                // Auth flow
                Splash,
                Auth,
                // Settings & other
                Settings,
                SavedCarts,
                // Note: ProductDetail and StoreDetail are not included as they are parameterized
            )
        }
    }
}