package com.example.championcart.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.local.TokenManager
import com.example.championcart.presentation.components.navigation.ChampionNavBar
import com.example.championcart.presentation.components.navigation.NavigationRoute
import com.example.championcart.presentation.screens.auth.LoginScreen
import com.example.championcart.presentation.screens.auth.OnboardingScreen
import com.example.championcart.presentation.screens.auth.RegisterScreen
import com.example.championcart.presentation.screens.home.HomeScreen
import com.example.championcart.presentation.screens.search.SearchScreen
import com.example.championcart.presentation.screens.showcase.ComponentShowcaseScreen
import com.example.championcart.presentation.screens.splash.SplashScreen
import com.example.championcart.presentation.screens.cart.CartScreen
import com.example.championcart.presentation.screens.profile.ProfileScreen
import com.example.championcart.presentation.screens.scan.ScanScreen
import com.example.championcart.ui.theme.Spacing
import com.example.championcart.presentation.screens.info.TermsOfServiceScreen
import com.example.championcart.presentation.screens.info.PrivacyPolicyScreen
import com.example.championcart.presentation.screens.product.ProductDetailScreen
import com.example.championcart.utils.NavigationUtils.openMapForNavigation


@Composable
fun ChampionCartNavHost(
    navController: NavHostController = rememberNavController(),
    tokenManager: TokenManager,
    preferencesManager: PreferencesManager,
    cartManager: CartManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Get cart item count for bottom nav badge
    val cartItems by cartManager.cartItems.collectAsState()
    val cartItemCount = cartItems.sumOf { it.quantity }

    // Determine start destination based on authentication state
    val hasToken = remember { tokenManager.getToken() != null }
    val isFirstLaunch = remember { preferencesManager.isFirstLaunch() }

    val startDestination = when {
        isFirstLaunch -> Screen.Onboarding.route
        hasToken -> Screen.Home.route
        else -> Screen.Login.route
    }

    // Determine if bottom nav should be shown
    val showBottomNav = currentRoute?.let { route ->
        route == Screen.Home.route ||
                route.startsWith("search") || // This will match both "search" and "search?query=..."
                route == Screen.Cart.route ||
                route == Screen.Profile.route
    } ?: false

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            // Splash Screen
            composable(route = Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    isLoggedIn = hasToken,
                    isFirstLaunch = isFirstLaunch
                )
            }

            // Auth Screens
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToForgotPassword = {
                        // TODO: Implement forgot password
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToTermsOfService = {
                        navController.navigate(Screen.TermsOfService.route)
                    },
                    onNavigateToPrivacyPolicy = {
                        navController.navigate(Screen.PrivacyPolicy.route)
                    }
                )
            }

            composable(route = Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        preferencesManager.setFirstLaunch(false)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onSkip = {
                        preferencesManager.setFirstLaunch(false)
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Main Screens
            composable(route = Screen.Home.route) {
                HomeScreen(
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToCart = {
                        navController.navigate(Screen.Cart.route)
                    },
                    onNavigateToSearch = { query ->
                        navController.navigate(Screen.Search.createRoute(query))
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToScan = {
                        navController.navigate(Screen.Scan.route)
                    }
                )
            }

            composable(
                route = Screen.Search.route,
                arguments = Screen.Search.arguments
            ) { backStackEntry ->
                val initialQuery = backStackEntry.arguments?.getString("query") ?: ""

                SearchScreen(
                    initialQuery = initialQuery,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }

            composable(Screen.Scan.route) {
                ScanScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            composable(route = Screen.Cart.route) {
                CartScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToStore = { storeName, address ->
                        // Now we receive both store name and address
                        val context = navController.context
                        openMapForNavigation(context, address, storeName)
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            composable(route = Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToTermsOfService = {
                        navController.navigate(Screen.TermsOfService.route)
                    },
                    onNavigateToPrivacyPolicy = {
                        navController.navigate(Screen.PrivacyPolicy.route)
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            // Product Detail Screen
            composable(
                route = Screen.ProductDetail.route,
                arguments = Screen.ProductDetail.arguments
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""

                ProductDetailScreen(
                    productId = productId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToStore = { storeName ->
                        // You can implement store navigation if needed
                        // For now, we'll just show a toast or do nothing
                    },
                    onNavigateToScan = {
                        navController.navigate(Screen.Scan.route)
                    },
                    innerPadding = if (showBottomNav) {
                        PaddingValues(bottom = 80.dp) // Account for bottom nav height
                    } else {
                        PaddingValues()
                    }
                )
            }

            // Settings Screen
            composable(route = Screen.Settings.route) {
                // TODO: Implement settings screen
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Settings Screen - Coming Soon!")
                }
            }

            composable(route = Screen.SavedCarts.route) {
                // TODO: Implement saved carts screen
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Saved Carts Screen - Coming Soon!")
                }
            }

            // Info Screens
            composable(route = Screen.TermsOfService.route) {
                TermsOfServiceScreen(
                    navController = navController
                )
            }

            composable(route = Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    navController = navController
                )
            }

            // Development
            composable(route = Screen.ComponentShowcase.route) {
                ComponentShowcaseScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Navigation bar overlay - positioned at bottom
        if (showBottomNav) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                ChampionNavBar(
                    selectedRoute = when {
                        currentRoute == Screen.Home.route -> NavigationRoute.HOME
                        currentRoute?.startsWith("search") == true -> NavigationRoute.SEARCH // FIXED
                        currentRoute == Screen.Scan.route -> NavigationRoute.SCAN
                        currentRoute == Screen.Cart.route -> NavigationRoute.CART
                        currentRoute == Screen.Profile.route -> NavigationRoute.PROFILE
                        else -> NavigationRoute.HOME
                    },
                    onNavigate = { route ->
                        val screen = when (route) {
                            NavigationRoute.HOME -> Screen.Home.route
                            NavigationRoute.SEARCH -> Screen.Search.createRoute() // FIXED: Use createRoute()
                            NavigationRoute.SCAN -> Screen.Scan.route
                            NavigationRoute.CART -> Screen.Cart.route
                            NavigationRoute.PROFILE -> Screen.Profile.route
                            else -> Screen.Home.route
                        }

                        navController.navigate(screen) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    cartItemCount = cartItemCount
                )
            }
        }
    }
}