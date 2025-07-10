package com.example.championcart.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.local.TokenManager
import com.example.championcart.domain.usecase.cart.GetSavedCartsUseCase
import com.example.championcart.domain.usecase.city.GetCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val preferencesManager: PreferencesManager,
    private val cartManager: CartManager,
    private val getSavedCartsUseCase: GetSavedCartsUseCase,
    private val getCitiesUseCase: GetCitiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadSavedCarts()
        loadCities()
        observeCartChanges()
    }

    private fun loadUserData() {
        val email = tokenManager.getUserEmail()
        val isGuest = tokenManager.isGuestMode()

        // Calculate member since date (mock for now - in real app, get from server)
        val memberSince = if (!isGuest && email != null) {
            // For demo, show current year
            SimpleDateFormat("MMMM yyyy", Locale("he")).format(Date())
        } else ""

        // Load preferences
        val selectedCity = preferencesManager.getSelectedCity()
        val notificationsEnabled = preferencesManager.areNotificationsEnabled()
        val totalSavings = preferencesManager.getTotalSavings()

        _uiState.update { state ->
            state.copy(
                userEmail = email ?: "",
                isGuest = isGuest,
                memberSince = memberSince,
                selectedCity = selectedCity,
                notificationsEnabled = notificationsEnabled,
                totalSavingsFormatted = "₪${String.format("%.0f", totalSavings)}"
            )
        }
    }

    private fun loadSavedCarts() {
        if (tokenManager.isGuestMode()) {
            _uiState.update { it.copy(savedCartsCount = 0) }
            return
        }

        viewModelScope.launch {
            getSavedCartsUseCase().collect { result ->
                result.fold(
                    onSuccess = { savedCarts ->
                        _uiState.update { it.copy(
                            savedCartsCount = savedCarts.size
                        ) }
                    },
                    onFailure = { error ->
                        // If error, just show 0
                        _uiState.update { it.copy(savedCartsCount = 0) }
                    }
                )
            }
        }
    }

    private fun loadCities() {
        viewModelScope.launch {
            getCitiesUseCase().collect { result ->
                result.fold(
                    onSuccess = { cities ->
                        _uiState.update { it.copy(availableCities = cities) }
                    },
                    onFailure = {
                        // Use default cities if API fails
                        _uiState.update { it.copy(
                            availableCities = listOf("תל אביב", "ירושלים", "חיפה", "באר שבע")
                        ) }
                    }
                )
            }
        }
    }

    private fun observeCartChanges() {
        viewModelScope.launch {
            cartManager.cartItems.collect { items ->
                _uiState.update { it.copy(
                    currentCartItems = items.sumOf { item -> item.quantity }
                ) }
            }
        }
    }

    fun updateCity(city: String) {
        preferencesManager.setSelectedCity(city)
        _uiState.update { it.copy(
            selectedCity = city,
            message = "העיר עודכנה ל$city"
        ) }
    }

    fun toggleNotifications(enabled: Boolean) {
        preferencesManager.setNotificationsEnabled(enabled)
        _uiState.update { it.copy(
            notificationsEnabled = enabled,
            message = if (enabled) "התראות הופעלו" else "התראות כובו"
        ) }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Clear all local data
            tokenManager.clearToken()
            cartManager.clearCart()
            // Don't clear preferences - keep city selection and other settings

            _uiState.update { it.copy(
                isLoading = false,
                message = "התנתקת בהצלחה"
            ) }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

data class ProfileUiState(
    // User info
    val userEmail: String = "",
    val isGuest: Boolean = true,
    val memberSince: String = "",

    // Stats from local data
    val savedCartsCount: Int = 0,
    val currentCartItems: Int = 0,
    val totalSavingsFormatted: String = "₪0",

    // Preferences
    val selectedCity: String = "תל אביב",
    val availableCities: List<String> = emptyList(),
    val notificationsEnabled: Boolean = true,

    // UI states
    val isLoading: Boolean = false,
    val message: String? = null
)