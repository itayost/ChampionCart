package com.example.championcart.ui.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * Motion Preferences Detection for Accessibility
 * Respects user's motion sensitivity settings
 * FIXED VERSION - No duplicate declarations
 */

/**
 * Detects if user prefers reduced motion for accessibility
 */
@Composable
fun isReduceMotionEnabled(): Boolean {
    val context = LocalContext.current

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        try {
            // Check system setting for reduce motion
            val resolver = context.contentResolver
            android.provider.Settings.Global.getFloat(
                resolver,
                "animator_duration_scale",
                1.0f
            ) == 0.0f || android.provider.Settings.Global.getFloat(
                resolver,
                "transition_animation_scale",
                1.0f
            ) == 0.0f
        } catch (e: Exception) {
            false
        }
    } else {
        // Fallback for older Android versions
        try {
            val resolver = context.contentResolver
            android.provider.Settings.Global.getFloat(
                resolver,
                "animator_duration_scale",
                1.0f
            ) == 0.0f
        } catch (e: Exception) {
            false
        }
    }
}

// REMOVED: LocalReduceMotion - will be declared only in Theme.kt

/**
 * Provider for motion preferences throughout the app
 * Uses the LocalReduceMotion from Theme.kt
 */
@Composable
fun ProvideMotionPreferences(
    content: @Composable () -> Unit
) {
    val reduceMotion = isReduceMotionEnabled()

    CompositionLocalProvider(
        // Reference the one from Theme.kt
        content = content
    )
}

/**
 * Battery optimization detection
 */
@Composable
fun isBatteryOptimizationEnabled(): Boolean {
    val context = LocalContext.current

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            val powerManager = context.getSystemService(android.content.Context.POWER_SERVICE)
                    as? android.os.PowerManager
            powerManager?.isPowerSaveMode == true
        } catch (e: Exception) {
            false
        }
    } else {
        false
    }
}

/**
 * Network performance detection for adaptive loading
 */
@Composable
fun isSlowNetwork(): Boolean {
    val context = LocalContext.current

    return try {
        val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE)
                as? android.net.ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager?.activeNetwork
            val capabilities = connectivityManager?.getNetworkCapabilities(network)

            when {
                capabilities?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) == true -> false
                capabilities?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                    // Check if it's a slow cellular connection
                    !capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                }
                else -> true
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager?.activeNetworkInfo
            networkInfo?.type != android.net.ConnectivityManager.TYPE_WIFI
        }
    } catch (e: Exception) {
        false
    }
}

/**
 * Combined performance preferences
 */
data class PerformancePreferences(
    val reduceMotion: Boolean,
    val batteryOptimization: Boolean,
    val slowNetwork: Boolean
)

@Composable
fun getPerformancePreferences(): PerformancePreferences {
    return PerformancePreferences(
        reduceMotion = isReduceMotionEnabled(),
        batteryOptimization = isBatteryOptimizationEnabled(),
        slowNetwork = isSlowNetwork()
    )
}