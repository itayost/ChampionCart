package com.example.championcart.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.local.TokenManager
import com.example.championcart.domain.repository.UserRepository
import com.example.championcart.domain.usecase.city.GetCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val preferencesManager: PreferencesManager,
    private val cartManager: CartManager,
    private val userRepository: UserRepository,
    private val getCitiesUseCase: GetCitiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadCities()
        loadStatistics()
    }

    private fun loadUserData() {
        val user = userRepository.getCurrentUser()
        val isGuest = tokenManager.isGuestMode()

        _uiState.update { state ->
            state.copy(
                userName = user?.name ?: if (isGuest) "אורח" else "משתמש",
                userEmail = user?.email ?: "",
                isGuest = isGuest,
                selectedCity = preferencesManager.getSelectedCity(),
                selectedLanguage = preferencesManager.getLanguage(),
                notificationsEnabled = preferencesManager.areNotificationsEnabled(),
                darkModeEnabled = preferencesManager.isDarkModeEnabled(),
                notificationSettings = NotificationSettings(
                    priceAlerts = preferencesManager.getPriceAlertsEnabled(),
                    newDeals = preferencesManager.getNewDealsEnabled(),
                    cartReminders = preferencesManager.getCartRemindersEnabled(),
                    monthlySummary = preferencesManager.getMonthlySummaryEnabled()
                )
            )
        }
    }

    private fun loadCities() {
        viewModelScope.launch {
            getCitiesUseCase().collect { result ->
                result.fold(
                    onSuccess = { cities ->
                        _uiState.update { it.copy(availableCities = cities) }
                    },
                    onFailure = { /* Handle error */ }
                )
            }
        }
    }

    private fun loadStatistics() {
        // Calculate statistics from local data
        val savedCarts = preferencesManager.getSavedCartsCount()
        val totalSavings = preferencesManager.getTotalSavings()
        val monthlyData = calculateMonthlyStats()

        _uiState.update { state ->
            state.copy(
                savedCartsCount = savedCarts,
                totalSavings = formatPrice(totalSavings),
                savingsThisMonth = formatPrice(monthlyData.savingsThisMonth),
                averageSavingsPerCart = formatPrice(monthlyData.averageSavings),
                productsTracked = preferencesManager.getTrackedProductsCount(),
                favoriteStore = preferencesManager.getFavoriteStore() ?: "טרם נקבע",
                shoppingFrequency = "${monthlyData.shoppingCount} פעמים בחודש"
            )
        }
    }

    fun updateCity(city: String) {
        preferencesManager.saveSelectedCity(city)
        _uiState.update { it.copy(selectedCity = city) }
    }

    fun updateLanguage(language: String) {
        preferencesManager.saveLanguage(language)
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        preferencesManager.saveDarkModeEnabled(enabled)
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        preferencesManager.apply {
            savePriceAlertsEnabled(settings.priceAlerts)
            saveNewDealsEnabled(settings.newDeals)
            saveCartRemindersEnabled(settings.cartReminders)
            saveMonthlySummaryEnabled(settings.monthlySummary)
        }
        _uiState.update { it.copy(notificationSettings = settings) }
    }

    fun logout() {
        tokenManager.clearToken()
        userRepository.clearUser()
        cartManager.clearCart()
        preferencesManager.clearUserData()
    }

    private fun calculateMonthlyStats(): MonthlyStats {
        // In a real app, this would query from a local database
        // For now, we'll use mock data
        return MonthlyStats(
            savingsThisMonth = 458.50,
            averageSavings = 76.42,
            shoppingCount = 6
        )
    }

    private fun formatPrice(price: Double): String {
        return "₪%.2f".format(price)
    }

    private data class MonthlyStats(
        val savingsThisMonth: Double,
        val averageSavings: Double,
        val shoppingCount: Int
    )
}

data class ProfileUiState(
    // User Info
    val userName: String = "",
    val userEmail: String = "",
    val isGuest: Boolean = true,

    // Statistics
    val savingsThisMonth: String = "₪0",
    val totalSavings: String = "₪0",
    val averageSavingsPerCart: String = "₪0",
    val productsTracked: Int = 0,
    val savedCartsCount: Int = 0,
    val favoriteStore: String = "טרם נקבע",
    val shoppingFrequency: String = "0 פעמים בחודש",

    // Preferences
    val selectedCity: String = "תל אביב",
    val selectedLanguage: String = "עברית",
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val notificationSettings: NotificationSettings = NotificationSettings(),

    // Available Options
    val availableCities: List<String> = emptyList()
)