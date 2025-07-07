package com.example.championcart.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.championcart.ui.theme.Size

/*
 * NOTE: For screens with bottom navigation, make sure to add bottom padding
 * to prevent content from being hidden behind the nav bar.
 *
 * Use one of these approaches:
 * 1. In LazyColumn: contentPadding = PaddingValues(bottom = Size.bottomNavHeight)
 * 2. In regular layouts: Modifier.padding(bottom = Size.bottomNavHeight)
 * 3. Or use the predefined: Padding.screenWithBottomNav
 *
 * The HomeScreen and SearchScreen already handle this internally.
 */

@Composable
fun ChampionCartNavHost(
    navController: NavHostController = rememberNavController(),
    tokenManager: TokenManager,
    preferencesManager: PreferencesManager,
    cartManager: CartManager = CartManager() // This will be injected in ViewModels
) {
    // Check authentication state
    val isLoggedIn = remember { tokenManager.isLoggedIn() }
    val isFirstLaunch = remember { preferencesManager.isFirstLaunch() }

    // Get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    // Determine if we should show bottom navigation
    val showBottomNav = currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Scan.route,
        Screen.Cart.route,
        Screen.Profile.route
    )

    // Get cart item count from the singleton CartManager
    // In real app, this would come from a shared ViewModel or repository
    val cartItems by cartManager.cartItems.collectAsState()
    val cartItemCount = cartItems.sumOf { it.quantity }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content - takes full screen
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.fillMaxSize()
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
                    isLoggedIn = isLoggedIn,
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
                    }
                )
            }

            composable(route = Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
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
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }

            composable(route = Screen.Search.route) {
                SearchScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }

            composable(route = Screen.Scan.route) {
                // TODO: Implement barcode scanning screen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = Size.bottomNavHeight), // Add bottom padding for nav bar
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "סורק ברקודים",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(com.example.championcart.ui.theme.Spacing.m))
                        Text(
                            text = "בקרוב!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            composable(route = Screen.Cart.route) {
                CartScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToStore = { storeName ->
                        // TODO: Navigate to store details or map screen
                        // For now, just log or show a toast
                        android.util.Log.d("Navigation", "Navigate to store: $storeName")
                    }
                )
            }

            composable(route = Screen.Profile.route) {
                // TODO: Implement profile screen
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Profile Screen - Coming Soon!")
                }
            }

            // Product Detail
            composable(
                route = Screen.ProductDetail.route,
                arguments = Screen.ProductDetail.arguments
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Implement product detail screen
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Product Detail: $productId")
                }
            }

            // Settings Screens
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
                    selectedRoute = when (currentRoute) {
                        Screen.Home.route -> NavigationRoute.HOME
                        Screen.Search.route -> NavigationRoute.SEARCH
                        Screen.Scan.route -> NavigationRoute.SCAN
                        Screen.Cart.route -> NavigationRoute.CART
                        Screen.Profile.route -> NavigationRoute.PROFILE
                        else -> NavigationRoute.HOME
                    },
                    onNavigate = { route ->
                        val screen = when (route) {
                            NavigationRoute.HOME -> Screen.Home.route
                            NavigationRoute.SEARCH -> Screen.Search.route
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