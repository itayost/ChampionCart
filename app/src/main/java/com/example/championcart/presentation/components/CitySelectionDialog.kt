package com.example.championcart.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.data.api.PriceApi
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.di.NetworkModule
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
    private val priceApi: PriceApi,
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
                val response = priceApi.getCitiesList()
                if (response.isSuccessful) {
                    val cities = response.body() ?: emptyList()
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
fun CitySelectionDialog(
    currentCity: String,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit,
    tokenManager: TokenManager
) {
    val viewModel = remember {
        CitySelectionViewModel(NetworkModule.priceApi, tokenManager)
    }
    val uiState by viewModel.uiState.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Select City",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search field
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search cities...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error message
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Cities list
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(uiState.filteredCities) { city ->
                            CityItem(
                                city = city,
                                isSelected = city == uiState.selectedCity,
                                onClick = {
                                    viewModel.selectCity(city)
                                    onCitySelected(city)
                                    onDismiss()
                                }
                            )
                        }

                        if (uiState.filteredCities.isEmpty()) {
                            item {
                                Text(
                                    text = "No cities found",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Extension function to show city selection from any Composable
@Composable
fun rememberCitySelectionDialog(
    tokenManager: TokenManager,
    onCitySelected: (String) -> Unit = {}
): Pair<String, () -> Unit> {
    var showDialog by remember { mutableStateOf(false) }
    var currentCity by remember { mutableStateOf(tokenManager.getSelectedCity()) }

    if (showDialog) {
        CitySelectionDialog(
            currentCity = currentCity,
            onCitySelected = { city ->
                currentCity = city
                onCitySelected(city)
            },
            onDismiss = { showDialog = false },
            tokenManager = tokenManager
        )
    }

    return currentCity to { showDialog = true }
}