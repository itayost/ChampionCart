package com.example.championcart.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.championcart.presentation.navigation.ChampionCartBottomBar
import com.example.championcart.presentation.navigation.ChampionCartNavHost
import com.example.championcart.ui.theme.ChampionCartTheme

/**
 * Main app composable that sets up the navigation structure
 */
@Composable
fun ChampionCartApp() {
    ChampionCartTheme {
        val navController = rememberNavController()

        // Mock cart item count - in real app, this would come from a ViewModel
        var cartItemCount by remember { mutableStateOf(5) }

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.navigationBars,
            bottomBar = {
                ChampionCartBottomBar(
                    navController = navController,
                    cartItemCount = cartItemCount
                )
            }
        ) { paddingValues ->
            ChampionCartNavHost(
                navController = navController
            )
        }
    }
}

/**
 * Preview-safe version of the app for development
 */
@Composable
fun ChampionCartAppPreview() {
    ChampionCartTheme {
        ChampionCartApp()
    }
}