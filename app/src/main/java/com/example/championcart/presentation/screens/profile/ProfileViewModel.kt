package com.example.championcart.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.models.*
import com.example.championcart.presentation.screens.savedcarts.SavedCartsScreen
import com.example.championcart.ui.theme.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.text.substringBefore
import javax.inject.Inject

data class ProfileUiState(
    val email: String? = null,
    val name: String? = null,
    val selectedCity: String = "Tel Aviv",
    val language: String = "en",
    val theme: String = "system",
    val notificationsEnabled: Boolean = true,
    val isLoggedIn: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val error: String? = null
)

data class ProfileState(
    val userName: String = "Guest",
    val userEmail: String = "",
    val isGuest: Boolean = true,
    val userStats: UserStats = UserStats(),
    val savedCarts: List<SavedCart> = emptyList(),
    val userPreferences: UserPreferences = UserPreferences(),
    val selectedCity: String = "Tel Aviv",
    val availableCities: List<String> = listOf(
        "Tel Aviv", "Jerusalem", "Haifa", "Rishon LeZion",
        "Petah Tikva", "Ashdod", "Netanya", "Beer Sheva"
    ),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showLogoutDialog: Boolean = false,
    val showCitySelector: Boolean = false,
    val showLanguageSelector: Boolean = false,
    val showThemeSelector: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        loadLocalPreferences()
        loadSavedCarts()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val userEmail = tokenManager.getUserEmail()

                if (userEmail != null) {
                    // Extract user name from email
                    val userName = userEmail
                        .substringBefore("@")
                        .split(".", "_", "-")
                        .joinToString(" ") { part ->
                            part.replaceFirstChar { it.uppercase() }
                        }

                    _state.update {
                        it.copy(
                            userName = userName,
                            userEmail = userEmail,
                            isGuest = false,
                            // Local-only mock stats for logged in users
                            userStats = UserStats(
                                totalSavings = 156.50,
                                itemsTracked = 24,
                                cartsCreated = 8,
                                averageSavings = 19.56,
                                favoriteStore = "Shufersal",
                                thisMonthSavings = 45.20
                            ),
                            error = null
                        )
                    }
                } else {
                    // Guest mode
                    _state.update {
                        it.copy(
                            userName = "Guest",
                            userEmail = "",
                            isGuest = true,
                            userStats = UserStats(), // Empty stats for guests
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to load profile: ${e.message}")
                }
            }
        }
    }

    private fun loadLocalPreferences() {
        viewModelScope.launch {
            try {
                // Load from TokenManager/SharedPreferences
                val savedCity = tokenManager.getSelectedCity()
                val savedLanguage = tokenManager.getLanguage() ?: "en"
                val savedTheme = tokenManager.getTheme() ?: "system"
                val notificationsEnabled = tokenManager.getNotificationsEnabled()

                val preferences = UserPreferences(
                    defaultCity = savedCity,
                    language = Language.values().find { it.code == savedLanguage } ?: Language.ENGLISH,
                    theme = when (savedTheme) {
                        "light" -> ThemePreference.Light
                        "dark" -> ThemePreference.Dark
                        else -> ThemePreference.System
                    },
                    notificationsEnabled = notificationsEnabled,
                )

                _state.update {
                    it.copy(
                        userPreferences = preferences,
                        selectedCity = savedCity,
                        error = null
                    )
                }
            } catch (e: Exception) {
                // Use defaults if loading fails
                _state.update {
                    it.copy(
                        userPreferences = UserPreferences(),
                        selectedCity = "Tel Aviv"
                    )
                }
            }
        }
    }

    private fun loadSavedCarts() {
        if (_state.value.isGuest) return

        viewModelScope.launch {
            try {
                authRepository.getUserSavedCarts().fold(
                    onSuccess = { carts ->
                        // Convert List<Cart> to List<SavedCart> if needed
                        val savedCarts = carts.map { cart ->
                            SavedCart(
                                id = cart.name, // Using name as ID
                                name = cart.name,
                                itemCount = cart.items.size,
                                totalPrice = cart.items.sumOf { it.price * it.quantity },
                                lastUpdated = System.currentTimeMillis(),
                                city = cart.city.toString()
                            )
                        }

                        _state.update {
                            it.copy(savedCarts = savedCarts)  // Now the types match
                        }
                    },
                    onFailure = { error ->
                        // Don't show error for saved carts, just use empty list
                        _state.update {
                            it.copy(savedCarts = emptyList())
                        }
                    }
                )
            } catch (e: Exception) {
                // Silent fail for saved carts
            }
        }
    }

    fun showLogoutDialog() {
        _state.update { it.copy(showLogoutDialog = true) }
    }

    fun hideLogoutDialog() {
        _state.update { it.copy(showLogoutDialog = false) }
    }

    fun showCitySelector() {
        _state.update { it.copy(showCitySelector = true) }
    }

    fun hideCitySelector() {
        _state.update { it.copy(showCitySelector = false) }
    }

    fun showLanguageSelector() {
        _state.update { it.copy(showLanguageSelector = true) }
    }

    fun hideLanguageSelector() {
        _state.update { it.copy(showLanguageSelector = false) }
    }

    fun showThemeSelector() {
        _state.update { it.copy(showThemeSelector = true) }
    }

    fun hideThemeSelector() {
        _state.update { it.copy(showThemeSelector = false) }
    }


    private fun getSelectedCity(): String {
        return _uiState.value.selectedCity
    }

    private fun getLanguage(): String {
        return _uiState.value.language
    }

    private fun getTheme(): String {
        return _uiState.value.theme
    }

    private fun getNotificationsEnabled(): Boolean {
        return _uiState.value.notificationsEnabled
    }

    private suspend fun getUserSavedCarts(): Result<List<Cart>> {
        return authRepository.getSavedCarts()
    }

    private fun clearUserEmail() {
        // Clear from preferences if needed
        _uiState.value = _uiState.value.copy(email = null)
    }

    private fun saveSelectedCity(city: String) {
        // Save to preferences if needed
        _uiState.value = _uiState.value.copy(selectedCity = city)
    }

    private fun saveLanguage(language: String) {
        // Save to preferences if needed
        _uiState.value = _uiState.value.copy(language = language)
    }

    private fun saveTheme(theme: String) {
        // Save to preferences if needed
        _uiState.value = _uiState.value.copy(theme = theme)
    }

    private fun saveNotificationsEnabled(enabled: Boolean) {
        // Save to preferences if needed
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
    }

    fun getUserEmail(): String {
        return _uiState.value.email ?: ""
    }

    private fun updateUserName() {
        val email = _uiState.value.email
        if (email != null) {
            val userName = email.substringBefore("@")
            _uiState.value = _uiState.value.copy(name = userName)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(showLogoutDialog = false) }

            authRepository.logout().fold(
                onSuccess = {
                    // Clear user data
                    tokenManager.clearToken()

                    // Reset to guest state
                    _state.update {
                        ProfileState(
                            userName = "Guest",
                            userEmail = "",
                            isGuest = true,
                            userStats = UserStats(),
                            savedCarts = emptyList()
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            error = error.message ?: "Logout failed"  // Fix: Provide default value
                        )
                    }
                }
            )
        }
    }

    fun updateDefaultCity(city: String) {
        viewModelScope.launch {
            try {
                // Save locally only
                tokenManager.saveSelectedCity(city)

                val updatedPreferences = _state.value.userPreferences.copy(
                    defaultCity = city
                )

                _state.update {
                    it.copy(
                        userPreferences = updatedPreferences,
                        selectedCity = city,
                        showCitySelector = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error saving city preference: ${e.message}")
                }
            }
        }
    }

    fun updateLanguage(language: Language) {
        viewModelScope.launch {
            try {
                tokenManager.saveLanguage(language.code)

                val updatedPreferences = _state.value.userPreferences.copy(
                    language = language
                )

                _state.update {
                    it.copy(
                        userPreferences = updatedPreferences,
                        showLanguageSelector = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error saving language preference: ${e.message}")
                }
            }
        }
    }

    fun updateTheme(theme: ThemePreference) {
        viewModelScope.launch {
            try {
                val themeCode = when (theme) {
                    ThemePreference.Light -> "light"
                    ThemePreference.Dark -> "dark"
                    ThemePreference.System -> "system"
                    ThemePreference.Auto -> TODO()
                }
                tokenManager.saveTheme(themeCode)

                val updatedPreferences = _state.value.userPreferences.copy(
                    theme = theme
                )

                _state.update {
                    it.copy(
                        userPreferences = updatedPreferences,
                        showThemeSelector = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error saving theme preference: ${e.message}")
                }
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                tokenManager.saveNotificationsEnabled(enabled)

                val updatedPreferences = _state.value.userPreferences.copy(
                    notificationsEnabled = enabled
                )

                _state.update {
                    it.copy(
                        userPreferences = updatedPreferences,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error updating notification settings: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun refresh() {
        loadUserProfile()
        loadLocalPreferences()
        loadSavedCarts()
    }
}