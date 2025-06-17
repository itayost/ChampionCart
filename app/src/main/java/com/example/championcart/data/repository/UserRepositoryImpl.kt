package com.example.championcart.data.repository

import com.example.championcart.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    // No dependencies needed since server doesn't support user features
) : UserRepository {

    override suspend fun isServerBacked(): Boolean {
        // Server doesn't provide user management endpoints
        return false
    }
}