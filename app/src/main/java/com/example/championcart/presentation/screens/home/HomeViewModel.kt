package com.example.championcart.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.repository.PriceRepository
import com.example.championcart.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val userName: String = "Champion",
    val selectedCity: City? = null,
    val availableCities: List<City> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val trendingItems: List<String> = emptyList(),
    val totalSavings: Double = 0.0,
    val savingsThisMonth: Double = 0.0,
    val comparisonsCount: Int = 0,
    val cartItemCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val priceRepository: PriceRepository,
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadUserData()
        loadCities()
        loadRecentActivity()
        observeCartCount()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            // Get user email and create display name
            val email = tokenManager.getUserEmail()
            val userName = if (email != null) {
                // Extract name from email or use first part
                val namePart = email.substringBefore("@")
                namePart.split(".", "_", "-")
                    .firstOrNull()
                    ?.replaceFirstChar { it.uppercase() }
                    ?: "Champion"
            } else {
                "Guest"
            }

            _state.update { it.copy(userName = userName) }

            // Load saved statistics from local storage
            loadSavingsData()
        }
    }

    private fun loadCities() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val result = priceRepository.getCitiesWithStores()

                result.fold(
                    onSuccess = { cityStrings ->
                        // Parse the city strings to extract counts
                        val cities = parseCities(cityStrings)

                        _state.update {
                            it.copy(
                                availableCities = cities,
                                isLoading = false,
                                error = null
                            )
                        }

                        // Auto-select Tel Aviv if available
                        cities.find { it.name == "Tel Aviv" }?.let { telAviv ->
                            selectCity(telAviv)
                        }
                    },
                    onFailure = { error ->
                        // Fallback to hardcoded cities if API fails
                        val fallbackCities = getDefaultCities()
                        _state.update {
                            it.copy(
                                availableCities = fallbackCities,
                                isLoading = false,
                                error = "Could not load cities. Using defaults."
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                val fallbackCities = getDefaultCities()
                _state.update {
                    it.copy(
                        availableCities = fallbackCities,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun parseCities(cityStrings: List<String>): List<City> {
        return cityStrings.mapNotNull { cityString ->
            // Parse format: "Tel Aviv: 45 shufersal, 12 victory"
            val parts = cityString.split(":")
            if (parts.size == 2) {
                val cityName = parts[0].trim()
                val stores = parts[1].trim()

                val shufersalCount = Regex("(\\d+)\\s*shufersal").find(stores)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                val victoryCount = Regex("(\\d+)\\s*victory").find(stores)?.groupValues?.get(1)?.toIntOrNull() ?: 0

                City(
                    name = cityName,
                    emoji = getCityEmoji(cityName),
                    storeCount = shufersalCount + victoryCount,
                    shufersalCount = shufersalCount,
                    victoryCount = victoryCount
                )
            } else {
                null
            }
        }.sortedByDescending { it.storeCount }
    }

    private fun getCityEmoji(cityName: String): String {
        return when (cityName.lowercase()) {
            "tel aviv" -> "🏙️"
            "jerusalem" -> "🕌"
            "haifa" -> "⚓"
            "beer sheva", "beer sheba" -> "🏜️"
            "eilat" -> "🏖️"
            "netanya" -> "🌊"
            "rishon lezion" -> "🍊"
            "petah tikva" -> "🌻"
            "ashdod" -> "🚢"
            "bnei brak" -> "📚"
            "ramat gan" -> "💎"
            "rehovot" -> "🔬"
            else -> "📍"
        }
    }

    private fun getDefaultCities(): List<City> {
        return listOf(
            City("Tel Aviv", "🏙️", 57, 45, 12),
            City("Jerusalem", "🕌", 40, 32, 8),
            City("Haifa", "⚓", 34, 28, 6),
            City("Beer Sheva", "🏜️", 19, 15, 4),
            City("Netanya", "🌊", 23, 18, 5),
            City("Rishon LeZion", "🍊", 21, 16, 5),
            City("Petah Tikva", "🌻", 18, 14, 4),
            City("Ashdod", "🚢", 17, 13, 4)
        )
    }

    private fun loadRecentActivity() {
        viewModelScope.launch {
            // Load from local storage or preferences
            val recentSearches = listOf("חלב", "לחם", "ביצים", "במבה", "קפה")
            val trendingItems = listOf("חלב 3%", "במבה", "שמן זית")

            _state.update {
                it.copy(
                    recentSearches = recentSearches,
                    trendingItems = trendingItems
                )
            }
        }
    }

    private fun loadSavingsData() {
        viewModelScope.launch {
            // In a real app, this would come from local database or API
            // For now, using mock data
            _state.update {
                it.copy(
                    totalSavings = 256.78,
                    savingsThisMonth = 45.32,
                    comparisonsCount = 23
                )
            }
        }
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            // In real app, observe cart repository
            // For now, mock data
            _state.update { it.copy(cartItemCount = 5) }
        }
    }

    fun selectCity(city: City) {
        _state.update {
            it.copy(
                selectedCity = city,
                error = null
            )
        }

        // Save selected city to preferences
        viewModelScope.launch {
            // tokenManager.saveSelectedCity(city.name)
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

// Alternative ViewModel without Hilt for testing
class HomeViewModelFactory(
    private val priceRepository: PriceRepository,
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(priceRepository, cartRepository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}