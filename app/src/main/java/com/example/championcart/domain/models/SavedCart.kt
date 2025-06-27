package com.example.championcart.domain.models

data class SavedCart(
    val id: String,
    val name: String,
    val itemCount: Int,
    val totalItems: Int,
    val createdAt: String
)