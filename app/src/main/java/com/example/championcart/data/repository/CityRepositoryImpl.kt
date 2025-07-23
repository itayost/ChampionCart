package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.CityApi
import com.example.championcart.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val cityApi: CityApi
) : CityRepository {

    companion object {
        private const val TAG = "CityRepository"

        // Default cities fallback
        private val DEFAULT_CITIES = listOf(
            "תל אביב",
            "ירושלים",
            "חיפה",
            "ראשון לציון",
            "פתח תקווה",
            "אשדוד",
            "נתניה",
            "באר שבע",
            "בני ברק",
            "רמת גן"
        )
    }

    override suspend fun getCities(): Flow<Result<List<String>>> = flow {
        val cities = try {
            Log.d(TAG, "Fetching cities list")
            val citiesList = cityApi.getCities()
            Log.d(TAG, "Found ${citiesList.size} cities")
            citiesList
        } catch (e: Exception) {
            Log.e(TAG, "Get cities error, using default cities", e)
            DEFAULT_CITIES
        }

        // Single emit outside try-catch
        emit(Result.success(cities))
    }
}