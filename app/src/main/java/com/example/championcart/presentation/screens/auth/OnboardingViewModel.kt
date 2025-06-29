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

    fun toggleStore(store: String) {
        _uiState.update { state ->
            val updatedStores = if (store in state.selectedStores) {
                state.selectedStores - store
            } else {
                state.selectedStores + store
            }
            state.copy(selectedStores = updatedStores)
        }
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

            // Note: Store preferences could be saved to a separate preference
            // For now, we'll just log them
            val stores = _uiState.value.selectedStores.joinToString(",")
            // Could save this to preferences or send to backend
        }
    }
}

data class OnboardingUiState(
    val cities: List<String> = emptyList(),
    val selectedCity: String? = null,
    val selectedStores: Set<String> = emptySet(),
    val notificationsEnabled: Boolean = true
)