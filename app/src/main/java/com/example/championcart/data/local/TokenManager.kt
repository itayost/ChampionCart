package com.example.championcart.data.local

import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_GUEST = "is_guest"
        private const val KEY_TOKEN_EXPIRY = "token_expiry" // New!
        private const val TAG = "TokenManager"

        // Buffer time - logout 5 minutes before actual expiry
        private const val EXPIRY_BUFFER_SECONDS = 300
    }

    /**
     * Saves the token and extracts its expiry time
     */
    fun saveToken(token: String) {
        try {
            // Extract expiry time from the JWT
            val expiryTime = extractTokenExpiry(token)

            sharedPreferences.edit()
                .putString(KEY_AUTH_TOKEN, token)
                .putLong(KEY_TOKEN_EXPIRY, expiryTime) // Also save the expiry time
                .putBoolean(KEY_IS_GUEST, false)
                .apply()

            Log.d(TAG, "Token saved with expiry: $expiryTime (${getTimeUntilExpiry()} seconds remaining)")
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting token expiry, saving token anyway", e)
            // If we failed to extract expiry, still save the token
            sharedPreferences.edit()
                .putString(KEY_AUTH_TOKEN, token)
                .putBoolean(KEY_IS_GUEST, false)
                .apply()
        }
    }

    /**
     * Returns the token only if it's still valid
     * If expired - clears everything and returns null
     */
    fun getToken(): String? {
        // Check if token is still valid
        if (!isTokenValid()) {
            Log.d(TAG, "Token expired, clearing authentication")
            clearToken()
            return null
        }

        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Checks if the token is still valid
     */
    fun isTokenValid(): Boolean {
        val token = sharedPreferences.getString(KEY_AUTH_TOKEN, null) ?: return false
        val expiryTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)

        // If we don't have a saved expiry time, try to extract it from the token
        if (expiryTime == 0L) {
            try {
                val extractedExpiry = extractTokenExpiry(token)
                sharedPreferences.edit()
                    .putLong(KEY_TOKEN_EXPIRY, extractedExpiry)
                    .apply()
                return isExpiryTimeValid(extractedExpiry)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to extract token expiry", e)
                // If we failed to extract, assume token is valid (to prevent locking out the user)
                return true
            }
        }

        return isExpiryTimeValid(expiryTime)
    }

    /**
     * Checks if the expiry time is still valid (with buffer)
     */
    private fun isExpiryTimeValid(expiryTime: Long): Boolean {
        val currentTime = System.currentTimeMillis() / 1000
        val isValid = currentTime < (expiryTime - EXPIRY_BUFFER_SECONDS)

        if (!isValid) {
            Log.d(TAG, "Token expired. Current: $currentTime, Expiry: $expiryTime")
        }

        return isValid
    }

    fun clearToken() {
        sharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_TOKEN_EXPIRY) // Also clear the expiry time
            .putBoolean(KEY_IS_GUEST, false)
            .apply()

        Log.d(TAG, "Token and user data cleared")
    }

    fun saveUserEmail(email: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun setGuestMode(isGuest: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_GUEST, isGuest)
            .apply()

        if (isGuest) {
            // In guest mode, clear all authentication data
            sharedPreferences.edit()
                .remove(KEY_AUTH_TOKEN)
                .remove(KEY_TOKEN_EXPIRY)
                .apply()
        }
    }

    fun isGuestMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false)
    }

    /**
     * Fixed logic - logged in only if:
     * 1. In guest mode, or
     * 2. Has a valid token
     */
    fun isLoggedIn(): Boolean {
        return isGuestMode() || (getToken() != null)
    }

    /**
     * Returns how much time is left until the token expires (in seconds)
     */
    fun getTimeUntilExpiry(): Long {
        val expiryTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
        if (expiryTime == 0L) return 0

        val currentTime = System.currentTimeMillis() / 1000
        return maxOf(0, expiryTime - currentTime - EXPIRY_BUFFER_SECONDS)
    }

    /**
     * Returns whether to show an expiry warning
     * (for example, if less than 10 minutes remain)
     */
    fun shouldShowExpiryWarning(): Boolean {
        val timeRemaining = getTimeUntilExpiry()
        return timeRemaining in 1..600 // Between 1 second and 10 minutes
    }

    /**
     * Extracts the expiry time from a JWT token
     * JWT consists of 3 parts: header.payload.signature
     * The payload contains the claims including 'exp' (expiration time)
     */
    private fun extractTokenExpiry(token: String): Long {
        try {
            val parts = token.split(".")
            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid JWT format - expected 3 parts, got ${parts.size}")
            }

            // Decode the payload (middle part)
            // JWT uses Base64 URL-safe encoding
            val payload = String(
                Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING)
            )

            val jsonObject = JSONObject(payload)

            // Extract the 'exp' claim (Unix timestamp in seconds)
            if (!jsonObject.has("exp")) {
                throw IllegalArgumentException("JWT doesn't contain 'exp' claim")
            }

            val expiry = jsonObject.getLong("exp")
            Log.d(TAG, "Extracted token expiry: $expiry")

            return expiry
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode JWT token", e)
            // If we failed, assume 24 hours from now (server default)
            return (System.currentTimeMillis() / 1000) + (24 * 60 * 60)
        }
    }
}