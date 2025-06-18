package com.example.championcart.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.presentation.screens.auth.LoginRegisterScreen
import com.example.championcart.presentation.screens.cart.CartScreen
import com.example.championcart.presentation.screens.home.ModernHomeScreen
import com.example.championcart.presentation.screens.profile.ProfileScreen
import com.example.championcart.presentation.screens.search.SearchScreen
import com.example.championcart.presentation.screens.product.ProductDetailScreen
import com.example.championcart.presentation.screens.splash.ModernSplashScreen

/**
 * Main navigation host for the Champion Cart app
 */
@Composable
fun ChampionCartNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)

    // Check if user is logged in
    val startDestination = if (tokenManager.getToken() != null) {
        Screen.Home.route
    } else {
        Screen.Splash.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) }
    ) {
        // Splash Screen
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            ModernSplashScreen(
                onSplashComplete = {
                    // Check if user is logged in
                    if (tokenManager.getToken() != null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Auth Screen (Login/Register)
        composable(
            route = Screen.Auth.route,
            enterTransition = {
                fadeIn(animationSpec = tween(500)) +
                        slideInVertically(
                            initialOffsetY = { it / 20 },
                            animationSpec = tween(500)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            LoginRegisterScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onGuestMode = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(
            route = Screen.Home.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Auth.route -> fadeIn(animationSpec = tween(600)) +
                            expandIn(
                                expandFrom = Alignment.Center,
                                animationSpec = tween(600, easing = FastOutSlowInEasing)
                            )
                    else -> defaultEnterTransition()
                }
            }
        ) {
            ModernHomeScreen(navController)
        }

        // Search Screen
        composable(
            route = Screen.Search.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            SearchScreen()
        }

        // Cart Screen
        composable(
            route = Screen.Cart.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() }
        ) {
            CartScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToResults = {
                    // Navigate to search results or a results screen
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        // Profile Screen
        composable(
            route = Screen.Profile.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        ) {
            ProfileScreen(navController)
        }

        // Product Detail Screen
        composable(
            route = Screen.ProductDetail.route,
            arguments = Screen.ProductDetail.arguments,
            enterTransition = {
                scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = { navController.navigateUp() },
                onAddToCart = {
                    // Show success animation then navigate
                    navController.navigate(Screen.Cart.route)
                }
            )
        }
    }
}

// Default enter transition
@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultEnterTransition(): EnterTransition {
    return slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(300))
}

// Default exit transition
@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultExitTransition(): ExitTransition {
    return slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(300))
}