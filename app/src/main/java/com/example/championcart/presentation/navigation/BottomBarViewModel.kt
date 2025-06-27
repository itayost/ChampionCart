package com.example.championcart.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.championcart.data.local.CartManager

@HiltViewModel
class BottomBarViewModel @Inject constructor(
    private val cartManager: CartManager
) : ViewModel() {

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    init {
        observeCartItems()
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            cartManager.cartItems.collect { items ->
                _cartItemCount.value = items.sumOf { it.quantity }
            }
        }
    }
}