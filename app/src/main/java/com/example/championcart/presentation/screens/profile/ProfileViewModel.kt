package com.example.championcart.presentation.screens.profile

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.UserRepository
import com.example.championcart.ui.theme.ChampionCartColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val userName: String = "Guest",
    val userEmail: String = "",
    val memberSince: String = "",
    val isGuest: Boolean = true,
    val totalSavings: Double = 0.0,
    val savingsThisMonth: Double = 0.0,
    val savingsThisYear: Double = 0.0,
    val comparisonsCount: Int = 0,
    val savedCartsCount: Int = 0,
    val priceAlertsCount: Int = 0,
    val achievements: List<Achievement> = emptyList(),
    val defaultCity: String = "Tel Aviv",
    val language: String = "English",
    val theme: String = "System Default",
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
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
        loadAchievements()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()

            if (currentUser != null) {
                // Extract user name from email
                val userName = currentUser.email
                    .substringBefore("@")
                    .split(".", "_", "-")
                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

                _state.update {
                    it.copy(
                        userName = userName,
                        userEmail = currentUser.email,
                        memberSince = "January 2024", // Mock data
                        isGuest = false
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        userName = "Guest",
                        isGuest = true
                    )
                }
            }
        }
    }

    private fun loadUserStats() {
        viewModelScope.launch {
            // In a real app, this would fetch from repository
            if (!_state.value.isGuest) {
                _state.update {
                    it.copy(
                        totalSavings = 1234.56,
                        savingsThisMonth = 89.12,
                        savingsThisYear = 567.89,
                        comparisonsCount = 45,
                        savedCartsCount = 8,
                        priceAlertsCount = 3
                    )
                }
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            val achievements = listOf(
                Achievement(
                    id = "first_save",
                    name = "First Save",
                    icon = "🎯",
                    color = ChampionCartColors.electricMint,
                    isUnlocked = true
                ),
                Achievement(
                    id = "super_saver",
                    name = "Super Saver",
                    icon = "💰",
                    color = ChampionCartColors.successGreen,
                    isUnlocked = true
                ),
                Achievement(
                    id = "price_hunter",
                    name = "Price Hunter",
                    icon = "🔍",
                    color = ChampionCartColors.cosmicPurple,
                    isUnlocked = false
                ),
                Achievement(
                    id = "champion",
                    name = "Champion",
                    icon = "🏆",
                    color = ChampionCartColors.neonCoral,
                    isUnlocked = false
                )
            )

            _state.update { it.copy(achievements = achievements) }
        }
    }

    fun toggleNotifications() {
        _state.update {
            it.copy(notificationsEnabled = !it.notificationsEnabled)
        }

        // Save preference
        viewModelScope.launch {
            // Save to preferences
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            tokenManager.clearToken()
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

// User repository interface (if not already defined)
interface UserRepository {
    suspend fun getUserStats(userId: String): Result<UserStats>
    suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit>
}

data class UserStats(
    val totalSavings: Double,
    val savingsThisMonth: Double,
    val savingsThisYear: Double,
    val comparisonsCount: Int
)

data class UserPreferences(
    val defaultCity: String,
    val language: String,
    val theme: String,
    val notificationsEnabled: Boolean
)

// Alternative ViewModel without Hilt for testing
class ProfileViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(authRepository, userRepository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}