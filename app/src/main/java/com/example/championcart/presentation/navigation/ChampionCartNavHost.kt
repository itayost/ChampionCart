package com.example.championcart.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.championcart.presentation.screens.splash.SplashScreen
import com.example.championcart.presentation.screens.auth.LoginScreen
import com.example.championcart.presentation.screens.auth.RegisterScreen
import com.example.championcart.presentation.screens.home.HomeScreen
import com.example.championcart.presentation.screens.search.SearchScreen
import com.example.championcart.presentation.screens.product.ProductDetailScreen
import com.example.championcart.presentation.screens.category.CategoryProductsScreen
import com.example.championcart.presentation.screens.cart.CartScreen
import com.example.championcart.presentation.screens.profile.ProfileScreen
import com.example.championcart.presentation.screens.settings.SettingsScreen
import com.example.championcart.presentation.screens.settings.CitySelectionScreen
import com.example.championcart.presentation.screens.cart.SavedCartsScreen
import com.example.championcart.presentation.screens.store.StoreComparisonScreen
import com.example.championcart.presentation.screens.store.StoreDetailScreen
import com.example.championcart.presentation.screens.info.AboutScreen
import com.example.championcart.presentation.screens.info.HelpScreen
import com.example.championcart.presentation.screens.info.PrivacyPolicyScreen
import com.example.championcart.presentation.screens.info.TermsOfServiceScreen

@Composable
fun ChampionCartNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Splash Screen
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Authentication
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSkipLogin = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Main screens (bottom nav)
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { query ->
                    navController.navigate(Screen.Search.createRoute(query))
                },
                onNavigateToCategory = { categoryId, categoryName ->
                    navController.navigate(Screen.CategoryProducts.createRoute(categoryId, categoryName))
                },
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToCitySelection = {
                    navController.navigate(Screen.CitySelection.route)
                }
            )
        }

        composable(
            route = Screen.Search.route,
            arguments = listOf(navArgument("query") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query")
            SearchScreen(
                initialQuery = query,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        composable(route = Screen.Cart.route) {
            CartScreen(
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToStoreComparison = {
                    navController.navigate(Screen.StoreComparison.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToSavedCarts = {
                    navController.navigate(Screen.SavedCarts.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onNavigateToOrders = { /* order */ }
            )
        }

        // Product related
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.CategoryProducts.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            CategoryProductsScreen(
                categoryId = categoryId,
                categoryName = categoryName,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        // Settings & preferences
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCitySelection = {
                    navController.navigate(Screen.CitySelection.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                },
                onNavigateToHelp = {
                    navController.navigate(Screen.Help.route)
                },
                onNavigateToPrivacyPolicy = {
                    navController.navigate(Screen.PrivacyPolicy.route)
                },
                onNavigateToTermsOfService = {
                    navController.navigate(Screen.TermsOfService.route)
                }
            )
        }

        composable(route = Screen.CitySelection.route) {
            CitySelectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.SavedCarts.route) {
            SavedCartsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        // Store related
        composable(route = Screen.StoreComparison.route) {
            StoreComparisonScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToStore = { storeId ->
                    navController.navigate(Screen.StoreDetail.createRoute(storeId))
                }
            )
        }

        composable(
            route = Screen.StoreDetail.route,
            arguments = listOf(navArgument("storeId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
            StoreDetailScreen(
                storeId = storeId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Info screens
        composable(route = Screen.About.route) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Help.route) {
            HelpScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.TermsOfService.route) {
            TermsOfServiceScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}