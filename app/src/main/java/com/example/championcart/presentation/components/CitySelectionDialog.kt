package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CitySelectionUiState(
    val cities: List<String> = emptyList(),
    val filteredCities: List<String> = emptyList(),
    val selectedCity: String = "Tel Aviv",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CitySelectionViewModel @Inject constructor(
    private val api: ChampionCartApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitySelectionUiState())
    val uiState: StateFlow<CitySelectionUiState> = _uiState.asStateFlow()

    init {
        loadCities()
        _uiState.value = _uiState.value.copy(
            selectedCity = tokenManager.getSelectedCity()
        )
    }

    private fun loadCities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val response = api.getCitiesList()
                if (response.isSuccessful) {
                    val cities: List<String> = response.body() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        cities = cities,
                        filteredCities = cities,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to load cities",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredCities = if (query.isEmpty()) {
                _uiState.value.cities
            } else {
                _uiState.value.cities.filter {
                    it.contains(query, ignoreCase = true)
                }
            }
        )
    }

    fun selectCity(city: String) {
        tokenManager.saveSelectedCity(city)
        _uiState.value = _uiState.value.copy(selectedCity = city)
    }
}

@Composable
fun CitySelectionBottomSheet(
    currentCity: String,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit,
    tokenManager: TokenManager
) {
    val viewModel: CitySelectionViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = ComponentShapes.BottomSheet,
        color = MaterialTheme.extendedColors.glassFrosted
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.paddingLarge)
        ) {
            // Header with animated icon
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Animated location icon
                    AnimatedLocationIcon()

                    Column {
                        Text(
                            text = "Select Your City",
                            style = AppTextStyles.hebrewHeadline,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Current: $currentCity",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

                // Search field
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search cities...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onSearchQueryChanged("") }
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    singleLine = true,
                    shape = ComponentShapes.TextField
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

            // Content based on state
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        // Loading state
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.extendedColors.electricMint
                            )
                            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
                            Text(
                                text = "Loading cities...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    uiState.error != null -> {
                        // Error state
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
                            Text(
                                text = uiState.error!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    uiState.filteredCities.isEmpty() -> {
                        // Empty state
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                        ) {
                            Icon(
                                Icons.Default.LocationOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "No cities found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> {
                        // Cities list
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall),
                            contentPadding = PaddingValues(vertical = Dimensions.spacingSmall)
                        ) {
                            items(uiState.filteredCities) { city ->
                                CityItem(
                                    city = city,
                                    isSelected = city == uiState.selectedCity,
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.selectCity(city)
                                        onCitySelected(city)
                                        onDismiss()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom action buttons
            if (!uiState.isLoading) {
                Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = ComponentShapes.Button
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onCitySelected(uiState.selectedCity)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.extendedColors.electricMint
                        ),
                        shape = ComponentShapes.Button
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedLocationIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "location_icon")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Icon(
        Icons.Default.LocationOn,
        contentDescription = null,
        modifier = Modifier
            .size(32.dp)
            .scale(scale),
        tint = MaterialTheme.extendedColors.electricMint
    )
}

@Composable
private fun CityItem(
    city: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(300),
        label = "background"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.extendedColors.electricMint
        } else {
            Color.Transparent
        },
        animationSpec = tween(300),
        label = "border"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = ComponentShapes.CardSmall,
        color = animatedBackgroundColor,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                animatedBorderColor
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = city,
                style = if (city.any { Character.UnicodeBlock.of(it) == Character.UnicodeBlock.HEBREW }) {
                    AppTextStyles.hebrewBody
                } else {
                    MaterialTheme.typography.bodyLarge
                },
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.extendedColors.electricMint
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.extendedColors.electricMint,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
            }
        }
    }
}

// Legacy Dialog version for backward compatibility
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectionDialog(
    currentCity: String,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit,
    tokenManager: TokenManager
) {
    // Redirect to modern bottom sheet
    CitySelectionBottomSheet(
        currentCity = currentCity,
        onCitySelected = onCitySelected,
        onDismiss = onDismiss,
        tokenManager = tokenManager
    )
}