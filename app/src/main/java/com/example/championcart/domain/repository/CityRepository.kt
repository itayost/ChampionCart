package com.example.championcart.domain.repository

import kotlinx.coroutines.flow.Flow

interface CityRepository {
    suspend fun getCities(): Flow<Result<List<String>>>
}