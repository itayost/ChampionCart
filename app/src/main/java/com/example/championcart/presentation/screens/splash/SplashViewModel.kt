package com.example.championcart.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.local.TokenManager
import com.example.championcart.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {

            // Check if user is logged in
            val isLoggedIn = tokenManager.isLoggedIn()

            _uiState.value = SplashUiState(
                isLoading = false,
                isLoggedIn = isLoggedIn,
            )
        }
    }
}

data class SplashUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
)