package com.example.championcart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.championcart.ui.theme.ChampionCartTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ChampionCartTheme {
                // System UI controller for status bar colors
                val systemUiController = rememberSystemUiController()
                val isDarkTheme = isSystemInDarkTheme()
                val statusBarColor = ChampionCartTheme.colors.background

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = statusBarColor,
                        darkIcons = !isDarkTheme
                    )
                    systemUiController.setNavigationBarColor(
                        color = statusBarColor,
                        darkIcons = !isDarkTheme
                    )
                }

                ChampionCartApp()
            }
        }
    }
}