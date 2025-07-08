package com.example.championcart.presentation.screens.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val priceRepository: PriceRepository,
    private val cartManager: CartManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    // Keep track of recent scans to avoid repeated processing
    private val recentlyScannedBarcodes = mutableSetOf<String>()
    private val SCAN_COOLDOWN_MS = 2000L

    fun processBarcode(barcode: String) {
        // Validate barcode format
        if (!isValidBarcode(barcode)) {
            showError("ברקוד לא תקין")
            return
        }

        // Check if this barcode was recently scanned
        if (recentlyScannedBarcodes.contains(barcode)) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(
                isProcessing = true,
                lastScannedBarcode = barcode,
                error = null
            ) }

            // Add to recently scanned with cooldown
            recentlyScannedBarcodes.add(barcode)
            viewModelScope.launch {
                delay(SCAN_COOLDOWN_MS)
                recentlyScannedBarcodes.remove(barcode)
            }

            // Search for product by barcode
            val city = preferencesManager.getSelectedCity()
            searchProductByBarcode(barcode, city)
        }
    }

    private suspend fun searchProductByBarcode(barcode: String, city: String) {
        priceRepository.getProductByBarcode(barcode, city).collect { result ->
            result.fold(
                onSuccess = { product ->
                    if (product != null) {
                        handleProductFound(product, barcode)
                    } else {
                        handleProductNotFound(barcode)
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(
                        isProcessing = false,
                        error = "שגיאה בחיפוש המוצר: ${error.message}"
                    ) }
                }
            )
        }
    }

    private fun handleProductFound(product: Product, barcode: String) {
        // Calculate price statistics from stores
        val priceRange = if (product.stores.isNotEmpty()) {
            PriceRange(
                min = product.bestPrice,
                max = product.stores.maxOf { it.price },
                avg = product.stores.map { it.price }.average()
            )
        } else null

        val scannedProduct = ScannedProduct(
            id = product.id,
            barcode = barcode,
            name = product.name,
            imageUrl = product.imageUrl,
            priceRange = priceRange,
            availableInStores = product.stores.size
        )

        // Add to recent scans
        addToRecentScans(scannedProduct)

        _uiState.update { it.copy(
            isProcessing = false,
            scannedProduct = scannedProduct,
            error = null
        ) }
    }

    private fun handleProductNotFound(barcode: String) {
        // Try alternative search or show not found message
        _uiState.update { it.copy(
            isProcessing = false,
            scannedProduct = null,
            error = "המוצר לא נמצא במאגר. נסה להקליד את שם המוצר בחיפוש"
        ) }
    }

    private fun addToRecentScans(product: ScannedProduct) {
        val recentScan = RecentScan(
            barcode = product.barcode,
            productName = product.name,
            timestamp = System.currentTimeMillis()
        )

        _uiState.update { state ->
            val updatedScans = (listOf(recentScan) + state.recentScans)
                .distinctBy { it.barcode }
                .take(5) // Keep only last 5 scans

            state.copy(recentScans = updatedScans)
        }

        // Save to preferences for persistence
        saveRecentScans()
    }

    private fun saveRecentScans() {
        // In a real app, you would save this to preferences or database
        // For now, they're just kept in memory
    }

    fun addToCart(product: ScannedProduct) {
        // Find the best price for this product
        val bestPrice = product.priceRange?.min ?: 0.0

        // Create a Product domain model
        val domainProduct = Product(
            id = product.id,
            barcode = product.barcode,
            name = product.name,
            imageUrl = product.imageUrl,
            category = "כללי", // Default category
            bestPrice = bestPrice,
            bestStore = "חנות", // We'll use a default since we don't have specific store info
            stores = emptyList() // We don't have detailed store info from barcode scan
        )

        // Add to cart
        cartManager.addToCart(domainProduct)

        // Clear the scanned product to allow new scans
        clearScannedProduct()
    }

    fun clearScannedProduct() {
        _uiState.update { it.copy(scannedProduct = null) }
    }

    fun toggleFlash() {
        _uiState.update { it.copy(isFlashOn = !it.isFlashOn) }
    }

    fun showManualEntry() {
        _uiState.update { it.copy(showManualEntry = true) }
    }

    fun hideManualEntry() {
        _uiState.update { it.copy(showManualEntry = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    private fun isValidBarcode(barcode: String): Boolean {
        // Basic validation - check if it's numeric and has reasonable length
        return barcode.matches(Regex("\\d+")) && barcode.length in 8..14
    }

    init {
        // Load recent scans from preferences
        loadRecentScans()
    }

    private fun loadRecentScans() {
        // In a real app, load from preferences or database
        // For demo purposes, we'll start with empty list
    }
}

data class ScanUiState(
    val isProcessing: Boolean = false,
    val isFlashOn: Boolean = false,
    val showManualEntry: Boolean = false,
    val lastScannedBarcode: String? = null,
    val scannedProduct: ScannedProduct? = null,
    val recentScans: List<RecentScan> = emptyList(),
    val error: String? = null
)