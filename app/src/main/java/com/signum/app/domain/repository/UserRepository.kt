package com.signum.app.domain.repository

import com.signum.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun authenticate(email: String, pass: String): User?
    suspend fun getUserById(id: String): User?
    fun getAllUsers(): Flow<List<User>>
    suspend fun insertUser(user: User)
}
