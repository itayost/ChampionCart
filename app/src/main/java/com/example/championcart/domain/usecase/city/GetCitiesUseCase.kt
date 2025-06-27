package com.example.championcart.domain.usecase.city

import com.example.championcart.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCitiesUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(): Flow<Result<List<String>>> {
        return cityRepository.getCities()
    }
}