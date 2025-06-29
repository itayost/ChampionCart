package com.example.championcart.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        loadCities()
    }

    private fun loadCities() {
        // In a real app, this would come from the API
        _uiState.update { it.copy(
            cities = listOf(
                "תל אביב",
                "ירושלים",
                "חיפה",
                "ראשון לציון",
                "פתח תקווה",
                "אשדוד",
                "נתניה",
                "באר שבע",
                "בני ברק",
                "חולון",
                "רמת גן",
                "אשקלון"
            )
        ) }
    }

    fun selectCity(city: String) {
        _uiState.update { it.copy(selectedCity = city) }
    }

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            // Save preferences
            _uiState.value.selectedCity?.let { city ->
                preferencesManager.setSelectedCity(city)
            }

            preferencesManager.setNotificationsEnabled(_uiState.value.notificationsEnabled)

            // Mark onboarding as completed
            preferencesManager.setFirstLaunch(false)
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            // Set default city if none selected
            if (_uiState.value.selectedCity == null) {
                preferencesManager.setSelectedCity("תל אביב") // Default city
            }

            // Keep notifications enabled by default
            preferencesManager.setNotificationsEnabled(true)

            // Mark onboarding as completed
            preferencesManager.setFirstLaunch(false)
        }
    }
}

data class OnboardingUiState(
    val cities: List<String> = emptyList(),
    val selectedCity: String? = null,
    val notificationsEnabled: Boolean = true
)