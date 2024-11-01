package com.application.lose_animals.domain.repository

import com.application.lose_animals.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(email: String, password: String, username: String): Boolean
    suspend fun loginUser(email: String, password: String): Boolean
    suspend fun getCurrentUser(): User?
    suspend fun updateUserProfile(user: User): Boolean // Добавлено
    fun logout()
}
