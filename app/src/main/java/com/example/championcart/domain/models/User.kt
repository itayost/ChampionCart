package com.example.championcart.domain.models

data class User(
    val id: String,
    val email: String,
    val name: String,
    val isGuest: Boolean = false
)