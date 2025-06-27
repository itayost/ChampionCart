package com.example.championcart.domain.repository

import com.example.championcart.domain.models.User

interface UserRepository {
    fun getCurrentUser(): User?
    fun updateUser(user: User)
    fun clearUser()
}