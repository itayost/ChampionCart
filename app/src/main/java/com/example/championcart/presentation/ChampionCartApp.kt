package com.example.championcart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.championcart.presentation.navigation.ChampionCartBottomBar
import com.example.championcart.presentation.navigation.ChampionCartNavHost
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.LocalReduceMotion
import com.example.championcart.ui.theme.LocalResponsiveConfig
import com.example.championcart.ui.theme.LocalTimeOfDay
import com.example.championcart.ui.theme.ResponsiveConfig
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                ChampionCartBottomBar(navController = navController)
            }
        }
    ) { paddingValues ->
        ChampionCartNavHost(
            navController = navController as NavHostController,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
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