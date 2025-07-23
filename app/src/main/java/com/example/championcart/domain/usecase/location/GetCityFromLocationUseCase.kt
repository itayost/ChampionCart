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
                        Log.w(
                            TAG,
                            "City '$detectedCity' not found in available cities: $availableCities"
                        )

                        // Try to find the closest match
                        val closestMatch = findClosestMatch(detectedCity, availableCities)
                        if (closestMatch != null) {
                            Log.d(TAG, "Found closest match: $closestMatch")
                            emit(Result.success(closestMatch))
                        } else {
                            emit(
                                Result.failure(
                                    CityNotAvailableException(
                                        "העיר $detectedCity אינה זמינה באפליקציה"
                                    )
                                )
                            )
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
            emit(
                Result.failure(
                    LocationPermissionException("נדרשת הרשאת מיקום כדי לזהות את העיר שלך")
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during city detection", e)
            emit(
                Result.failure(
                    LocationException("שגיאה בזיהוי המיקום: ${e.message}")
                )
            )
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
            // Tel Aviv variations
            "tel aviv" to "תל אביב",
            "tel aviv-yafo" to "תל אביב",
            "tel aviv-jaffa" to "תל אביב",
            "תל אביב-יפו" to "תל אביב",
            "תל-אביב" to "תל אביב",

            // Jerusalem variations
            "jerusalem" to "ירושלים",
            "yerushalayim" to "ירושלים",

            // Haifa variations
            "haifa" to "חיפה",
            "hefa" to "חיפה",

            // Be'er Sheva variations
            "be'er sheva" to "באר שבע",
            "beer sheva" to "באר שבע",
            "beersheba" to "באר שבע",

            // Rishon LeZion variations
            "rishon lezion" to "ראשון לציון",
            "rishon le zion" to "ראשון לציון",
            "rishon letzion" to "ראשון לציון",

            // Petah Tikva variations
            "petah tikva" to "פתח תקווה",
            "petach tikva" to "פתח תקווה",
            "petah tikwa" to "פתח תקווה",

            // Ashdod
            "ashdod" to "אשדוד",

            // Netanya
            "netanya" to "נתניה",
            "netania" to "נתניה",

            // Bnei Brak variations
            "bnei brak" to "בני ברק",
            "bene beraq" to "בני ברק",
            "bnei berak" to "בני ברק",

            // Holon
            "holon" to "חולון",

            // Ramat Gan
            "ramat gan" to "רמת גן",

            // Ashkelon variations
            "ashkelon" to "אשקלון",
            "ashqelon" to "אשקלון",

            // Bat Yam
            "bat yam" to "בת ים",

            // Herzliya variations
            "herzliya" to "הרצליה",
            "herzelia" to "הרצליה",
            "hertzelia" to "הרצליה",
            "herzliyya" to "הרצליה",

            // Ra'anana variations
            "ra'anana" to "רעננה",
            "raanana" to "רעננה",

            // Kfar Saba variations
            "kfar saba" to "כפר סבא",
            "kfar sava" to "כפר סבא",
            "kefar saba" to "כפר סבא",

            // Modi'in variations
            "modi'in" to "מודיעין",
            "modiin" to "מודיעין",

            // Rehovot variations
            "rehovot" to "רחובות",
            "rechovot" to "רחובות",
            "rehovoth" to "רחובות",

            // Nazareth variations
            "nazareth" to "נצרת",
            "nazerat" to "נצרת",

            // Nazareth Illit
            "nazareth illit" to "נצרת עילית",
            "upper nazareth" to "נצרת עילית",
            "nof hagalil" to "נצרת עילית",

            // Eilat
            "eilat" to "אילת",
            "elat" to "אילת",

            // Acre variations
            "acre" to "עכו",
            "akko" to "עכו",
            "acco" to "עכו",

            // Hadera
            "hadera" to "חדרה",
            "hedera" to "חדרה",

            // Lod
            "lod" to "לוד",
            "lydda" to "לוד",

            // Ramla
            "ramla" to "רמלה",
            "ramle" to "רמלה",

            // Givatayim
            "givatayim" to "גבעתיים",
            "givataim" to "גבעתיים",

            // Ramat HaSharon
            "ramat hasharon" to "רמת השרון",
            "ramat ha-sharon" to "רמת השרון",

            // Beit Shemesh
            "beit shemesh" to "בית שמש",
            "bet shemesh" to "בית שמש",

            // Ness Ziona
            "ness ziona" to "נס ציונה",
            "nes ziona" to "נס ציונה",

            // Or Yehuda
            "or yehuda" to "אור יהודה",

            // Yavne
            "yavne" to "יבנה",
            "yabneh" to "יבנה",

            // Tiberias
            "tiberias" to "טבריה",
            "tverya" to "טבריה",
            "tveria" to "טבריה",

            // Afula
            "afula" to "עפולה",

            // Karmiel
            "karmiel" to "כרמיאל",
            "carmiel" to "כרמיאל",

            // Dimona
            "dimona" to "דימונה",

            // Kiryat Ata
            "kiryat ata" to "קרית אתא",
            "qiryat ata" to "קרית אתא",

            // Kiryat Gat
            "kiryat gat" to "קרית גת",
            "qiryat gat" to "קרית גת",

            // Kiryat Motzkin
            "kiryat motzkin" to "קרית מוצקין",
            "qiryat motzkin" to "קרית מוצקין",

            // Kiryat Bialik
            "kiryat bialik" to "קרית ביאליק",
            "qiryat bialik" to "קרית ביאליק",

            // Kiryat Ono
            "kiryat ono" to "קריית אונו",
            "qiryat ono" to "קריית אונו",

            // Kiryat Shmona
            "kiryat shmona" to "קרית שמונה",
            "qiryat shmona" to "קרית שמונה",

            // Hod HaSharon
            "hod hasharon" to "הוד השרון",
            "hod ha-sharon" to "הוד השרון",

            // Rosh HaAyin
            "rosh haayin" to "ראש העין",
            "rosh ha'ayin" to "ראש העין",

            // Zichron Ya'akov
            "zichron yaakov" to "זכרון יעקב",
            "zikhron yaakov" to "זכרון יעקב",

            // Harish
            "harish" to "חריש",

            // Yehud
            "yehud" to "יהוד",

            // Gan Yavne
            "gan yavne" to "גן יבנה",

            // Gedera
            "gedera" to "גדרה",

            // Nahariya
            "nahariya" to "נהריה",
            "nahariyya" to "נהריה",

            // Ma'ale Adumim
            "ma'ale adumim" to "מעלה אדומים",
            "maale adumim" to "מעלה אדומים",

            // Beitar Illit
            "beitar illit" to "ביתר עילית",
            "betar illit" to "ביתר עילית",

            // Modi'in Illit
            "modi'in illit" to "מודיעין עילית",
            "modiin illit" to "מודיעין עילית",

            // Arad
            "arad" to "ערד",

            // Safed/Tzfat
            "safed" to "צפת",
            "tzfat" to "צפת",
            "zefat" to "צפת",

            // Additional common English variations
            "tel-aviv" to "תל אביב",
            "telaviv" to "תל אביב"
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