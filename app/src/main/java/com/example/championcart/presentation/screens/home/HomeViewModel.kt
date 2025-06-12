package com.example.championcart.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.preferences.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Guest",
    val selectedCity: String = "Tel Aviv",
    val totalSavedThisMonth: Double = 0.0,
    val itemsInCart: Int = 0,
    val favoriteStore: String? = null,
    val recentSearches: List<String> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val tokenManager: TokenManager,
    private val cartManager: CartManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        observeCart()
    }

    private fun loadUserData() {
        val email = tokenManager.getUserEmail()
        val city = tokenManager.getSelectedCity()

        _uiState.value = _uiState.value.copy(
            userName = email?.substringBefore("@") ?: "Guest",
            selectedCity = city
        )

        // TODO: Load actual savings data from API or local storage
        // For now, using mock data
        _uiState.value = _uiState.value.copy(
            totalSavedThisMonth = 156.50,
            favoriteStore = "Rami Levy",
            recentSearches = listOf("milk", "bread", "eggs", "chicken")
        )
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartManager.cartCount.collect { count ->
                _uiState.value = _uiState.value.copy(itemsInCart = count)
            }
        }
    }

    fun onCityChange(city: String) {
        tokenManager.saveSelectedCity(city)
        _uiState.value = _uiState.value.copy(selectedCity = city)
    }
}