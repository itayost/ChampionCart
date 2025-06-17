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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.di.NetworkModule
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CitySelectionUiState(
    val cities: List<String> = emptyList(),
    val filteredCities: List<String> = emptyList(),
    val selectedCity: String = "Tel Aviv",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class CitySelectionViewModel(
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
                        isLoading = false,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load cities"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message}",
                    // Fallback cities
                    cities = listOf(
                        "Tel Aviv", "Jerusalem", "Haifa", "Rishon LeZion",
                        "Petah Tikva", "Ashdod", "Netanya", "Beer Sheva",
                        "Bnei Brak", "Ramat Gan", "Ashkelon", "Rehovot"
                    ).also { fallback ->
                        _uiState.value = _uiState.value.copy(filteredCities = fallback)
                    }
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        val filtered = if (query.isEmpty()) {
            _uiState.value.cities
        } else {
            _uiState.value.cities.filter { city ->
                city.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(filteredCities = filtered)
    }

    fun selectCity(city: String) {
        tokenManager.saveSelectedCity(city)
        _uiState.value = _uiState.value.copy(selectedCity = city)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectionBottomSheet(
    currentCity: String,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit,
    tokenManager: TokenManager
) {
    val viewModel = remember {
        CitySelectionViewModel(NetworkModule.api, tokenManager)
    }
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = ComponentShapes.BottomSheet,
        containerColor = MaterialTheme.extendedColors.glassFrosted,
        dragHandle = {
            // Custom drag handle with glass effect
            Surface(
                modifier = Modifier
                    .padding(top = Dimensions.paddingMedium)
                    .size(40.dp, 4.dp)
                    .clip(ComponentShapes.Button),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            ) {}
        },
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.paddingLarge)
                .padding(bottom = Dimensions.paddingLarge)
        ) {
            // Header with gradient background
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = ComponentShapes.CardSmall,
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f),
                                    MaterialTheme.extendedColors.cosmicPurple.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .padding(Dimensions.paddingLarge)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                    ) {
                        // Animated location icon
                        val infiniteTransition = rememberInfiniteTransition(label = "location")
                        val iconScale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        )

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.2f),
                                            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.05f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.extendedColors.electricMint,
                                modifier = Modifier
                                    .size(Dimensions.iconSizeMedium)
                                    .scale(iconScale)
                            )
                        }

                        Column {
                            Text(
                                text = "Select Your City",
                                style = AppTextStyles.hebrewHeadline,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Choose your location for accurate prices",
                                style = AppTextStyles.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

            // Search field with glass effect
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .glassEffect(),
                placeholder = {
                    Text(
                        "Search cities...",
                        style = AppTextStyles.inputHint,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                textStyle = AppTextStyles.bodyLarge,
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.electricMint
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.onSearchQueryChange("")
                                keyboardController?.hide()
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = ComponentShapes.TextField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.extendedColors.electricMint,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.extendedColors.glass,
                    unfocusedContainerColor = MaterialTheme.extendedColors.glass
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                )
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Error message with glass styling
            uiState.error?.let { error ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ComponentShapes.Card,
                    color = MaterialTheme.extendedColors.errorRed.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(Dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.errorRed,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = error,
                            style = AppTextStyles.bodyLarge,
                            color = MaterialTheme.extendedColors.errorRed
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
            }

            // Cities list with loading state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .heightIn(max = 400.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        // Modern loading state
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.extendedColors.electricMint,
                                strokeWidth = 3.dp
                            )
                            Text(
                                text = "Loading cities...",
                                style = AppTextStyles.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    uiState.filteredCities.isEmpty() -> {
                        // Empty state
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimensions.paddingXLarge),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "No cities found",
                                style = AppTextStyles.hebrewHeadline,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Try adjusting your search",
                                style = AppTextStyles.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> {
                        // Cities list
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
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
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            "Cancel",
                            style = AppTextStyles.buttonText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CityItem(
    city: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.15f)
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
                    AppTextStyles.hebrewText
                } else {
                    AppTextStyles.bodyLarge
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