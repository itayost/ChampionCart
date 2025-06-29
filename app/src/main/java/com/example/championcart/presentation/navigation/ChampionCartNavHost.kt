package com.example.championcart.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.local.TokenManager
import com.example.championcart.presentation.screens.showcase.ComponentShowcaseScreen
import com.example.championcart.presentation.screens.home.SimpleHomeScreen
import com.example.championcart.presentation.screens.auth.*
import com.example.championcart.presentation.screens.splash.SplashScreen

@Composable
fun ChampionCartNavHost(
    navController: NavHostController = rememberNavController(),
    tokenManager: TokenManager,
    preferencesManager: PreferencesManager
) {
    // Check authentication state
    val isLoggedIn = remember { tokenManager.isLoggedIn() }
    val isFirstLaunch = remember { preferencesManager.isFirstLaunch() }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route // Always start with splash
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

        // Home Screen
        composable(route = Screen.Home.route) {
            SimpleHomeScreen(
                onNavigateToShowcase = {
                    navController.navigate(Screen.ComponentShowcase.route)
                },
                onLogout = {
                    // Clear token and navigate to login
                    tokenManager.clearToken()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
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