package com.example.championcart.domain.models

/**
 * User model - simplified to match server capabilities
 * Server only handles email/password authentication
 */
data class User(
    val id: String = "",
    val email: String,
    val token: String,
    val tokenType: String = "Bearer",
    val name: String? = null,
    val phoneNumber: String? = null
)