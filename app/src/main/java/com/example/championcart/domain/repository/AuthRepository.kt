package com.example.championcart.domain.repository

import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.domain.models.Cart
import com.example.championcart.domain.models.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    // Add this property
    val isAuthenticated: StateFlow<Boolean>

    // Auth operations
    suspend fun register(email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun getAuthToken(): String?

    // Profile operations (optional - can be removed if not needed)
    suspend fun updateProfile(name: String?, phoneNumber: String?): Result<User>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>

    // Cart operations (optional - can be removed if not needed)
    suspend fun getSavedCarts(): Result<List<Cart>>
    suspend fun getUserSavedCarts(): Result<List<Cart>> // Add this method alias
    suspend fun saveCart(request: SaveCartRequest): Result<Unit>
    suspend fun deleteCart(cartName: String): Result<Unit>
}