package com.example.championcart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.championcart.presentation.ChampionCartApp
import com.example.championcart.ui.theme.ChampionCartTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge
        //enableEdgeToEdge()

        setContent {
            // ENABLE ALL YOUR EXISTING DESIGN SYSTEMS
            ChampionCartTheme(
                timeBasedTheme = true,      // ✅ ACTIVATES your time-based colors & greetings
                reduceMotion = false,       // ✅ ACTIVATES your advanced spring animations
                hapticsEnabled = true,      // ✅ ACTIVATES your haptic feedback system
                dynamicColor = true         // ✅ ACTIVATES Material You integration
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChampionCartApp()
                }
            }
        }
    }
}