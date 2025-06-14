package com.example.championcart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.championcart.di.NetworkModule
import com.example.championcart.presentation.navigation.ChampionCartNavigation
import com.example.championcart.presentation.theme.ChampionCartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge
        enableEdgeToEdge()

        // Alternative method if enableEdgeToEdge() is not available:
        // WindowCompat.setDecorFitsSystemWindows(window, false)

        // Initialize NetworkModule with context
        NetworkModule.initialize(applicationContext)

        setContent {
            ChampionCartTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChampionCartNavigation()
                }
            }
        }
    }
}