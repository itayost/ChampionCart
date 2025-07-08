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
    }

    override suspend fun getCities(): Flow<Result<List<String>>> = flow {
        try {
            Log.d(TAG, "Fetching cities list")

            val cities = cityApi.getCities()

            Log.d(TAG, "Found ${cities.size} cities")
            emit(Result.success(cities))

        } catch (e: Exception) {
            Log.e(TAG, "Get cities error (Ask Gemini)", e)
            // Return default cities if API fails
            emit(Result.success(
                listOf(
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
            ))
        }
    }
}