package com.example.championcart.domain.models

/**
 * User model - simplified to match server capabilities
 * Server only handles email/password authentication
 */
data class User(
    val id: String,
    val email: String,
    val isGuest: Boolean = false
) {
    val displayName: String
        get() = if (email.isNotBlank()) {
            email.substringBefore("@")
        } else {
            "User"
        }
}