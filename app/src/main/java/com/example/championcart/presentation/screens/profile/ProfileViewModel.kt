package com.example.championcart.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.UserRepository
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
    val availableCities: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingStats: Boolean = false,
    val isLoadingSavedCarts: Boolean = false,
    val isSavingPreferences: Boolean = false,
    val error: String? = null,
    val showLogoutDialog: Boolean = false,
    val showCitySelector: Boolean = false,
    val showLanguageSelector: Boolean = false,
    val showThemeSelector: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUserProfile()
        loadUserStats()
        loadSavedCarts()
        loadUserPreferences()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                val userEmail = tokenManager.getUserEmail()

                if (currentUser != null && userEmail != null) {
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

    private fun loadUserStats() {
        if (_state.value.isGuest) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingStats = true) }

            try {
                val userEmail = tokenManager.getUserEmail() ?: return@launch
                val result = userRepository.getUserStats(userEmail)

                result.fold(
                    onSuccess = { stats ->
                        _state.update {
                            it.copy(
                                userStats = stats,
                                isLoadingStats = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                userStats = UserStats(), // Default empty stats
                                isLoadingStats = false,
                                error = "Failed to load stats: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        userStats = UserStats(),
                        isLoadingStats = false,
                        error = "Error loading stats: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadSavedCarts() {
        if (_state.value.isGuest) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingSavedCarts = true) }

            try {
                val result = authRepository.getUserSavedCarts()

                result.fold(
                    onSuccess = { savedCarts ->
                        _state.update {
                            it.copy(
                                savedCarts = savedCarts,
                                isLoadingSavedCarts = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                savedCarts = emptyList(),
                                isLoadingSavedCarts = false,
                                error = "Failed to load saved carts: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        savedCarts = emptyList(),
                        isLoadingSavedCarts = false,
                        error = "Error loading saved carts: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadUserPreferences() {
        viewModelScope.launch {
            try {
                val result = userRepository.getUserPreferences()

                result.fold(
                    onSuccess = { preferences ->
                        _state.update {
                            it.copy(
                                userPreferences = preferences,
                                selectedCity = preferences.defaultCity,
                                error = null
                            )
                        }
                    },
                    onFailure = {
                        // Use default preferences if loading fails
                        _state.update {
                            it.copy(
                                userPreferences = UserPreferences(),
                                selectedCity = "Tel Aviv"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                // Continue with default preferences
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
            _state.update { it.copy(isSavingPreferences = true) }

            try {
                val updatedPreferences = _state.value.userPreferences.copy(
                    defaultCity = city
                )

                val result = userRepository.updateUserPreferences(updatedPreferences)

                result.fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                userPreferences = updatedPreferences,
                                selectedCity = city,
                                isSavingPreferences = false,
                                showCitySelector = false,
                                error = null
                            )
                        }

                        // Save to token manager as well
                        // tokenManager.saveSelectedCity(city)
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isSavingPreferences = false,
                                error = "Failed to save city preference: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSavingPreferences = false,
                        error = "Error saving preferences: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateLanguage(language: Language) {
        viewModelScope.launch {
            _state.update { it.copy(isSavingPreferences = true) }

            try {
                val updatedPreferences = _state.value.userPreferences.copy(
                    language = language
                )

                val result = userRepository.updateUserPreferences(updatedPreferences)

                result.fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                userPreferences = updatedPreferences,
                                isSavingPreferences = false,
                                showLanguageSelector = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isSavingPreferences = false,
                                error = "Failed to save language preference: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSavingPreferences = false,
                        error = "Error saving language: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateTheme(theme: ThemePreference) {
        viewModelScope.launch {
            _state.update { it.copy(isSavingPreferences = true) }

            try {
                val updatedPreferences = _state.value.userPreferences.copy(
                    theme = theme
                )

                val result = userRepository.updateUserPreferences(updatedPreferences)

                result.fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                userPreferences = updatedPreferences,
                                isSavingPreferences = false,
                                showThemeSelector = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isSavingPreferences = false,
                                error = "Failed to save theme preference: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSavingPreferences = false,
                        error = "Error saving theme: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isSavingPreferences = true) }

            try {
                val updatedPreferences = _state.value.userPreferences.copy(
                    notificationsEnabled = enabled
                )

                val result = userRepository.updateUserPreferences(updatedPreferences)

                result.fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                userPreferences = updatedPreferences,
                                isSavingPreferences = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isSavingPreferences = false,
                                error = "Failed to save notification preference: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSavingPreferences = false,
                        error = "Error saving notifications: ${e.message}"
                    )
                }
            }
        }
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

    fun refreshData() {
        loadUserStats()
        loadSavedCarts()
        clearError()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}