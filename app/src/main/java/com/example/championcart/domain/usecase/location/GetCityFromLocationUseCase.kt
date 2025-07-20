package com.example.championcart.domain.usecase.location

import android.util.Log
import com.example.championcart.data.local.LocationManager
import com.example.championcart.domain.usecase.city.GetCitiesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCityFromLocationUseCase @Inject constructor(
    private val locationManager: LocationManager,
    private val getCitiesUseCase: GetCitiesUseCase
) {
    companion object {
        private const val TAG = "GetCityFromLocationUseCase"
    }

    suspend operator fun invoke(): Flow<Result<String>> = flow {
        try {
            // Emit loading state
            Log.d(TAG, "Starting city detection from location")

            // Get current city from location
            val detectedCity = locationManager.getCurrentCity()

            if (detectedCity == null) {
                Log.w(TAG, "Could not detect city from location")
                emit(Result.failure(LocationException("לא ניתן לזהות את המיקום הנוכחי")))
                return@flow
            }

            Log.d(TAG, "Detected city: $detectedCity")

            // Get available cities from API
            val availableCitiesResult = getCitiesUseCase().first()

            availableCitiesResult.fold(
                onSuccess = { availableCities ->
                    // Find matching city (case-insensitive, trim whitespace)
                    val matchedCity = findMatchingCity(detectedCity, availableCities)

                    if (matchedCity != null) {
                        Log.d(TAG, "Matched city: $matchedCity")
                        emit(Result.success(matchedCity))
                    } else {
                        Log.w(TAG, "City '$detectedCity' not found in available cities: $availableCities")

                        // Try to find the closest match
                        val closestMatch = findClosestMatch(detectedCity, availableCities)
                        if (closestMatch != null) {
                            Log.d(TAG, "Found closest match: $closestMatch")
                            emit(Result.success(closestMatch))
                        } else {
                            emit(Result.failure(
                                CityNotAvailableException(
                                    "העיר $detectedCity אינה זמינה באפליקציה"
                                )
                            ))
                        }
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to get available cities", error)
                    // Even if we can't get available cities, return the detected city
                    // The app can handle validation later
                    emit(Result.success(detectedCity))
                }
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception - missing location permission", e)
            emit(Result.failure(
                LocationPermissionException("נדרשת הרשאת מיקום כדי לזהות את העיר שלך")
            ))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during city detection", e)
            emit(Result.failure(
                LocationException("שגיאה בזיהוי המיקום: ${e.message}")
            ))
        }
    }

    /**
     * Finds an exact match for the detected city in the available cities list
     */
    private fun findMatchingCity(detectedCity: String, availableCities: List<String>): String? {
        val normalizedDetected = detectedCity.trim().lowercase()

        return availableCities.firstOrNull { availableCity ->
            availableCity.trim().lowercase() == normalizedDetected
        }
    }

    /**
     * Finds the closest match based on common city name variations
     */
    private fun findClosestMatch(detectedCity: String, availableCities: List<String>): String? {
        val normalizedDetected = detectedCity.trim().lowercase()

        // Check for common variations and mappings
        val cityMappings = mapOf(
            "tel aviv" to "תל אביב",
            "tel aviv-yafo" to "תל אביב",
            "תל אביב-יפו" to "תל אביב",
            "תל-אביב" to "תל אביב",
            "jerusalem" to "ירושלים",
            "haifa" to "חיפה",
            "be'er sheva" to "באר שבע",
            "beer sheva" to "באר שבע",
            "rishon lezion" to "ראשון לציון",
            "rishon le zion" to "ראשון לציון",
            "petah tikva" to "פתח תקווה",
            "petach tikva" to "פתח תקווה",
            "ashdod" to "אשדוד",
            "netanya" to "נתניה",
            "bnei brak" to "בני ברק",
            "bene beraq" to "בני ברק",
            "holon" to "חולון",
            "ramat gan" to "רמת גן",
            "ashkelon" to "אשקלון",
            "ashqelon" to "אשקלון"
        )

        // Check if the detected city matches any mapping
        cityMappings[normalizedDetected]?.let { mappedCity ->
            if (availableCities.any { it.trim().lowercase() == mappedCity.lowercase() }) {
                return mappedCity
            }
        }

        // Check for partial matches (contains)
        return availableCities.firstOrNull { availableCity ->
            val normalizedAvailable = availableCity.trim().lowercase()
            normalizedDetected.contains(normalizedAvailable) ||
                    normalizedAvailable.contains(normalizedDetected)
        }
    }
}

// Custom exceptions for better error handling
class LocationException(message: String) : Exception(message)
class LocationPermissionException(message: String) : Exception(message)
class CityNotAvailableException(message: String) : Exception(message)