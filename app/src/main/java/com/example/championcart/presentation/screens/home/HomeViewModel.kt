package com.example.championcart.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Simplified HomeState with only available data
data class HomeState(
    val userName: String = "Champion",
    val selectedCity: String = "Tel Aviv",
    val cartItemCount: Int = 0,
    val recentSearches: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCitySelector: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadUserData()
        observeCartCount()
        loadRecentSearches()
    }

    // ============ DATA LOADING METHODS ============

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                // Get user email and extract name
                val email = tokenManager.getUserEmail()
                val userName = if (email != null) {
                    // Extract name from email (before @)
                    email.substringBefore("@")
                        .split(".", "_", "-")
                        .firstOrNull()
                        ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        ?: "Champion"
                } else {
                    "Champion" // Guest mode default
                }

                // Get saved city preference
                val savedCity = tokenManager.getSelectedCity()

                _state.update {
                    it.copy(
                        userName = userName,
                        selectedCity = savedCity,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load user data: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            try {
                cartRepository.getCartItems()
                    .catch {
                        // Handle error silently, cart count will remain 0
                    }
                    .collect { cartItems ->
                        _state.update {
                            it.copy(cartItemCount = cartItems.size)
                        }
                    }
            } catch (e: Exception) {
                // Cart observation failed, keep count at 0
            }
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            try {
                // Load real recent searches from TokenManager
                val recentSearches = tokenManager.getRecentSearches()

                _state.update {
                    it.copy(recentSearches = recentSearches)
                }
            } catch (e: Exception) {
                // Use empty list if loading fails
                _state.update {
                    it.copy(recentSearches = emptyList())
                }
            }
        }
    }

    // ============ USER INTERACTION METHODS ============

    fun selectCity(city: String) {
        viewModelScope.launch {
            try {
                // Save to TokenManager
                tokenManager.saveSelectedCity(city)

                _state.update {
                    it.copy(
                        selectedCity = city,
                        showCitySelector = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to save city preference: ${e.message}"
                    )
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

    // ============ SEARCH RELATED METHODS ============

    fun onSearchQuerySelected(query: String) {
        viewModelScope.launch {
            try {
                // Add to recent searches when user selects a search
                tokenManager.addRecentSearch(query)

                // Reload recent searches to update UI immediately
                loadRecentSearches()
            } catch (e: Exception) {
                // If saving fails, continue silently
            }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            try {
                tokenManager.clearRecentSearches()

                _state.update {
                    it.copy(recentSearches = emptyList())
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to clear search history")
                }
            }
        }
    }

    fun removeRecentSearch(search: String) {
        viewModelScope.launch {
            try {
                tokenManager.removeRecentSearch(search)

                // Reload to update UI
                loadRecentSearches()
            } catch (e: Exception) {
                // If removal fails, continue silently
            }
        }
    }

    // ============ ERROR HANDLING ============

    fun clearError() {
        _state.update {
            it.copy(error = null)
        }
    }

    fun refresh() {
        _state.update {
            it.copy(isLoading = true, error = null)
        }

        loadUserData()
        loadRecentSearches()
        // Cart count is observed automatically
    }
}