package com.example.championcart.data.local

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "LocationManager"
        private const val LOCATION_TIMEOUT_MS = 10000L
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Gets the current device location
     * @return Location object or null if location cannot be obtained
     */
    suspend fun getCurrentLocation(): Location? = withContext(Dispatchers.IO) {
        // Check if location permissions are granted
        if (!hasLocationPermission()) {
            Log.w(TAG, "Location permission not granted")
            return@withContext null
        }

        try {
            val cancellationTokenSource = CancellationTokenSource()

            return@withContext suspendCancellableCoroutine { continuation ->
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    Log.d(TAG, "Location obtained: $location")
                    continuation.resume(location)
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to get location", exception)
                    continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception when getting location", e)
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Exception when getting location", e)
            return@withContext null
        }
    }

    /**
     * Gets address information from location coordinates
     * @param location The location to geocode
     * @return Address object or null if geocoding fails
     */
    suspend fun getAddressFromLocation(location: Location): Address? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale("he", "IL"))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13+, use the async method
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) { addresses ->
                        continuation.resume(addresses.firstOrNull())
                    }
                }
            } else {
                // For older versions, use the synchronous method
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                addresses?.firstOrNull()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Geocoding failed", e)
            null
        }
    }

    /**
     * Extracts city name from address
     * @param address The address to extract city from
     * @return City name in Hebrew or null if not found
     */
    fun getCityFromAddress(address: Address): String? {
        // Try different address fields to find the city
        val city = when {
            !address.locality.isNullOrBlank() -> address.locality
            !address.subAdminArea.isNullOrBlank() -> address.subAdminArea
            !address.adminArea.isNullOrBlank() -> address.adminArea
            else -> null
        }

        Log.d(TAG, "Extracted city: $city from address: ${address.getAddressLine(0)}")
        return city
    }

    /**
     * Check if location permissions are granted
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Gets the current city based on device location
     * @return City name or null if detection fails
     */
    suspend fun getCurrentCity(): String? {
        val location = getCurrentLocation() ?: return null
        val address = getAddressFromLocation(location) ?: return null
        return getCityFromAddress(address)
    }
}