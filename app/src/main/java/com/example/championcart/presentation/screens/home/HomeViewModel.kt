package com.example.championcart.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.repository.PriceRepository
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val userName: String = "Champion",
    val userEmail: String = "",
    val selectedCity: String = "Tel Aviv",
    val availableCities: List<String> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val cartItemCount: Int = 0,
    val totalSavings: Double = 0.0,
    val savingsThisMonth: Double = 0.0,
    val comparisonsCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCitySelector: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val priceRepository: PriceRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadUserData()
        loadCities()
        loadUserStats()
        observeCartCount()
        loadRecentSearches()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                // Get user email from token manager
                val email = tokenManager.getUserEmail()
                val userName = if (email != null) {
                    // Extract name from email
                    email.substringBefore("@")
                        .split(".", "_", "-")
                        .firstOrNull()
                        ?.replaceFirstChar { it.uppercase() }
                        ?: "Champion"
                } else {
                    "Champion" // Guest mode
                }

                // Get saved city preference
                val savedCity = "Tel Aviv" // Default city, could come from tokenManager.getSelectedCity()

                _state.update {
                    it.copy(
                        userName = userName,
                        userEmail = email ?: "",
                        selectedCity = savedCity
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to load user data: ${e.message}")
                }
            }
        }
    }

    private fun loadCities() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val result = priceRepository.getCitiesList()
                result.fold(
                    onSuccess = { cities ->
                        _state.update {
                            it.copy(
                                availableCities = cities,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                availableCities = listOf("Tel Aviv", "Jerusalem", "Haifa"), // Fallback cities
                                isLoading = false,
                                error = "Failed to load cities: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        availableCities = listOf("Tel Aviv", "Jerusalem", "Haifa"),
                        isLoading = false,
                        error = "Network error: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadUserStats() {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserEmail() ?: return@launch

                // Get user stats from repository
                val statsResult = userRepository.getUserStats(userId)
                statsResult.fold(
                    onSuccess = { stats ->
                        _state.update {
                            it.copy(
                                totalSavings = stats.totalSavings,
                                savingsThisMonth = stats.savingsThisMonth,
                                comparisonsCount = stats.comparisonsCount
                            )
                        }
                    },
                    onFailure = {
                        // Use default values if stats loading fails
                        _state.update {
                            it.copy(
                                totalSavings = 0.0,
                                savingsThisMonth = 0.0,
                                comparisonsCount = 0
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                // Continue with default values
            }
        }
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            cartRepository.getCartItems()
                .catch { /* Handle error silently */ }
                .collect { cartItems ->
                    _state.update {
                        it.copy(cartItemCount = cartItems.size)
                    }
                }
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            // Load recent searches from local storage
            // For now, using mock data - could be from TokenManager or local database
            val recentSearches = listOf(
                "חלב תנובה",
                "לחם",
                "ביצים",
                "במבה",
                "קפה"
            )

            _state.update {
                it.copy(recentSearches = recentSearches)
            }
        }
    }

    fun selectCity(city: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedCity = city,
                    showCitySelector = false,
                    error = null
                )
            }

            // Save selected city to preferences
            try {
                // tokenManager.saveSelectedCity(city)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to save city preference")
                }
            }
        }
    }

    fun showCitySelector() {
        _state.update {
            it.copy(showCitySelector = true)
        }
    }

    fun hideCitySelector() {
        _state.update {
            it.copy(showCitySelector = false)
        }
    }

    fun onSearchClicked(query: String = "") {
        // This will be handled by navigation in the UI
        if (query.isNotEmpty()) {
            addToRecentSearches(query)
        }
    }

    fun onRecentSearchClicked(searchTerm: String) {
        // This will be handled by navigation in the UI
        addToRecentSearches(searchTerm)
    }

    private fun addToRecentSearches(searchTerm: String) {
        viewModelScope.launch {
            val currentSearches = _state.value.recentSearches.toMutableList()

            // Remove if already exists
            currentSearches.remove(searchTerm)

            // Add to beginning
            currentSearches.add(0, searchTerm)

            // Keep only last 10
            val updatedSearches = currentSearches.take(10)

            _state.update {
                it.copy(recentSearches = updatedSearches)
            }

            // Save to local storage
            // tokenManager.saveRecentSearches(updatedSearches)
        }
    }

    fun refreshData() {
        loadCities()
        loadUserStats()
        clearError()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}