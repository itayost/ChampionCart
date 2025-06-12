package com.example.championcart.presentation.screens.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.di.NetworkModule
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ApiTestState(
    val baseUrl: String = "",
    val testResults: List<TestResult> = emptyList(),
    val isLoading: Boolean = false
)

data class TestResult(
    val endpoint: String,
    val success: Boolean,
    val message: String,
    val responseData: String? = null
)

class ApiTestViewModel : ViewModel() {
    private val _state = MutableStateFlow(ApiTestState())
    val state: StateFlow<ApiTestState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(
            baseUrl = com.example.championcart.utils.Constants.BASE_URL
        )
    }

    fun runTests() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, testResults = emptyList())
            val results = mutableListOf<TestResult>()

            // Test 1: Basic connectivity
            try {
                val response = NetworkModule.priceApi.getCitiesList()
                results.add(
                    TestResult(
                        endpoint = "GET /cities-list",
                        success = response.isSuccessful,
                        message = "Status: ${response.code()}",
                        responseData = response.body()?.take(5)?.toString()
                    )
                )
            } catch (e: Exception) {
                results.add(
                    TestResult(
                        endpoint = "GET /cities-list",
                        success = false,
                        message = "Error: ${e.message}"
                    )
                )
            }

            // Test 2: Search products
            try {
                val response = NetworkModule.priceApi.searchProducts(
                    city = "Tel Aviv",
                    itemName = "milk",
                    groupByCode = true,
                    limit = 5
                )
                results.add(
                    TestResult(
                        endpoint = "GET /prices/by-item/Tel Aviv/milk",
                        success = response.isSuccessful,
                        message = "Status: ${response.code()}",
                        responseData = if (response.isSuccessful) {
                            "Found ${response.body()?.size ?: 0} products"
                        } else {
                            response.errorBody()?.string()
                        }
                    )
                )
            } catch (e: Exception) {
                results.add(
                    TestResult(
                        endpoint = "GET /prices/by-item/Tel Aviv/milk",
                        success = false,
                        message = "Error: ${e.message}"
                    )
                )
            }

            try {
                val loginRequest = com.example.championcart.data.models.request.LoginRequest(
                    email = "test@example.com",
                    password = "test123"
                )
                val response = NetworkModule.authApi.login(loginRequest)
                results.add(
                    TestResult(
                        endpoint = "POST /login",
                        success = response.isSuccessful,
                        message = "Status: ${response.code()}",
                        responseData = when {
                            response.isSuccessful -> "Token received: ${response.body()?.accessToken?.take(20)}..."
                            response.code() == 401 -> "Invalid credentials (expected for test account)"
                            else -> response.errorBody()?.string()
                        }
                    )
                )
            } catch (e: Exception) {
                results.add(
                    TestResult(
                        endpoint = "POST /login",
                        success = false,
                        message = "Error: ${e.message}"
                    )
                )
            }

// Test 4: Register test (optional - only if you want to test registration)
            try {
                val timestamp = System.currentTimeMillis()
                val registerRequest = com.example.championcart.data.models.request.RegisterRequest(
                    email = "test${timestamp}@example.com",
                    password = "test123"
                )
                val response = NetworkModule.authApi.register(registerRequest)
                results.add(
                    TestResult(
                        endpoint = "POST /register",
                        success = response.isSuccessful,
                        message = "Status: ${response.code()}",
                        responseData = when {
                            response.isSuccessful -> "Registration successful"
                            response.code() == 400 -> "Email already exists"
                            else -> response.errorBody()?.string()
                        }
                    )
                )
            } catch (e: Exception) {
                results.add(
                    TestResult(
                        endpoint = "POST /register",
                        success = false,
                        message = "Error: ${e.message}"
                    )
                )
            }

            _state.value = _state.value.copy(
                isLoading = false,
                testResults = results
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTestScreen() {
    val viewModel = remember { ApiTestViewModel() }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Connection Test") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Show current base URL
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Base URL:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = state.baseUrl,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Run tests button
            Button(
                onClick = { viewModel.runTests() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Run API Tests")
                }
            }

            // Test results
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.testResults.size) { index ->
                    val result = state.testResults[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (result.success) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = result.endpoint,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = result.message,
                                fontSize = 14.sp
                            )
                            result.responseData?.let { data ->
                                Text(
                                    text = data,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}