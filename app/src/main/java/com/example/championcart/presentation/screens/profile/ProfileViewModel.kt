package com.example.championcart.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.local.TokenManager
import com.example.championcart.domain.models.SavedCart
import com.example.championcart.domain.usecase.cart.GetSavedCartsUseCase
import com.example.championcart.domain.usecase.cart.LoadSavedCartUseCase
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
    private val loadSavedCartUseCase: LoadSavedCartUseCase,
    private val getCitiesUseCase: GetCitiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var hasLoadedSavedCartsDetails = false

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

    fun loadSavedCartsDetails() {
        if (tokenManager.isGuestMode()) {
            _uiState.update { it.copy(
                savedCarts = emptyList(),
                isLoadingSavedCarts = false
            ) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSavedCarts = true) }

            getSavedCartsUseCase().collect { result ->
                result.fold(
                    onSuccess = { savedCarts ->
                        _uiState.update { it.copy(
                            savedCarts = savedCarts,
                            savedCartsCount = savedCarts.size,
                            isLoadingSavedCarts = false
                        ) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(
                            savedCarts = emptyList(),
                            isLoadingSavedCarts = false,
                            message = "שגיאה בטעינת העגלות השמורות"
                        ) }
                    }
                )
            }
        }
    }

    fun onSavedCartsRequested() {
        if (!hasLoadedSavedCartsDetails && !tokenManager.isGuestMode()) {
            loadSavedCartsDetails()
            hasLoadedSavedCartsDetails = true
        }
    }

    fun loadSavedCart(cart: SavedCart) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            loadSavedCartUseCase(cart.id).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(
                            isLoading = false,
                            message = "העגלה '${cart.name}' נטענה בהצלחה"
                        ) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            message = "שגיאה בטעינת העגלה"
                        ) }
                    }
                )
            }
        }
    }

    fun deleteSavedCart(cart: SavedCart) {
        viewModelScope.launch {
            // TODO: Implement delete cart use case
            // For now, just remove from local list
            _uiState.update { state ->
                state.copy(
                    savedCarts = state.savedCarts.filter { it.id != cart.id },
                    savedCartsCount = state.savedCartsCount - 1,
                    message = "העגלה נמחקה"
                )
            }
        }
    }

    fun compareSavedCart(cart: SavedCart) {
        viewModelScope.launch {
            _uiState.update { it.copy(message = "השוואת מחירים לעגלה '${cart.name}'...") }
            // TODO: Implement cart comparison logic
            // This would load the cart and navigate to price comparison screen
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

    // Saved carts
    val savedCarts: List<SavedCart> = emptyList(),
    val isLoadingSavedCarts: Boolean = false,

    // Preferences
    val selectedCity: String = "תל אביב",
    val availableCities: List<String> = emptyList(),
    val notificationsEnabled: Boolean = true,

    // UI states
    val isLoading: Boolean = false,
    val message: String? = null
)