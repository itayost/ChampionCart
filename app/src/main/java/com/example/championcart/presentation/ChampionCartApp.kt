package com.example.championcart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.championcart.presentation.navigation.ChampionCartBottomBar
import com.example.championcart.presentation.navigation.ChampionCartNavHost
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.LocalTimeOfDay
import com.example.championcart.ui.theme.Sizing
import com.example.championcart.ui.theme.getTimeOfDay
import kotlinx.coroutines.delay

@Composable
fun ChampionCartApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Time-based theming
    var currentTimeOfDay by remember { mutableStateOf(getTimeOfDay()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeOfDay = getTimeOfDay()
            delay(60_000) // Check every minute
        }
    }

    // Accessibility settings
    val reduceMotion = remember { context.isReduceMotionEnabled() }

    // Provide local compositions
    CompositionLocalProvider(
        LocalTimeOfDay provides currentTimeOfDay,
    ) {
        ChampionCartScaffold(navController = navController)
    }
}

@Composable
private fun ChampionCartScaffold(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if we should show the bottom bar
    val showBottomBar = when (currentDestination?.route) {
        Screen.Splash.route,
        Screen.Login.route,
        Screen.Register.route -> false
        else -> true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content - fills entire screen
        ChampionCartNavHost(
            navController = navController as NavHostController,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient fade effect at bottom (optional but nice)
        if (showBottomBar) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.2f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }

        // Floating bottom navigation bar
        if (showBottomBar) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding() // Respect system navigation
            ) {
                ChampionCartBottomBar(navController = navController)
            }
        }
    }
}

// Extension function to check system reduce motion setting
fun android.content.Context.isReduceMotionEnabled(): Boolean {
    return try {
        android.provider.Settings.Global.getFloat(
            contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE
        ) == 0f
    } catch (e: Exception) {
        false
    }
}