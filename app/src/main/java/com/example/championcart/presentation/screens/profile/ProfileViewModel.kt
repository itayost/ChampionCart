package com.example.championcart.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    init {
        loadUserProfile()
        loadLocalPreferences()
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
                        .firstOrNull()
                        ?.replaceFirstChar { it.uppercase() }
                        ?: "User"

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
                val preferences = UserPreferences(
                    defaultCity = savedCity,
                    language = Language.ENGLISH, // Could add to TokenManager later
                    theme = ThemePreference.SYSTEM // Could add to TokenManager later
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

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                tokenManager.clearToken()

                _state.update {
                    it.copy(
                        showLogoutDialog = false,
                        userName = "Guest",
                        userEmail = "",
                        isGuest = true,
                        userStats = UserStats(),
                        savedCarts = emptyList(),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to logout: ${e.message}")
                }
            }
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
                // For now just update local state
                // Could add tokenManager.saveLanguage(language.code) later
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
                // For now just update local state
                // Could add tokenManager.saveTheme(theme) later
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
                    it.copy(error = "Error saving notification preference: ${e.message}")
                }
            }
        }
    }

    fun togglePriceAlerts(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val updatedPreferences = _state.value.userPreferences.copy(
                    priceAlertsEnabled = enabled
                )

                _state.update {
                    it.copy(
                        userPreferences = updatedPreferences,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error saving price alerts preference: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}