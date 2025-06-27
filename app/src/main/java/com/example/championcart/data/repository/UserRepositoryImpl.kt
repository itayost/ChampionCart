package com.example.championcart.data.repository

import com.example.championcart.data.local.TokenManager
import com.example.championcart.domain.models.User
import com.example.championcart.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager
) : UserRepository {

    private var currentUser: User? = null

    override fun getCurrentUser(): User? {
        if (currentUser == null && tokenManager.isLoggedIn()) {
            // Create user from stored data
            val email = tokenManager.getUserEmail()
            if (email != null) {
                currentUser = User(
                    id = email.hashCode().toString(),
                    email = email,
                    name = email.substringBefore("@"),
                    isGuest = tokenManager.isGuestMode()
                )
            } else if (tokenManager.isGuestMode()) {
                currentUser = User(
                    id = "guest",
                    email = "guest@championcart.com",
                    name = "אורח",
                    isGuest = true
                )
            }
        }
        return currentUser
    }

    override fun updateUser(user: User) {
        currentUser = user
        if (!user.isGuest) {
            tokenManager.saveUserEmail(user.email)
        }
    }

    override fun clearUser() {
        currentUser = null
        tokenManager.clearToken()
    }
}