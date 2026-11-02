package com.signum.app.data.repository

import com.signum.app.data.local.CryptoUtils
import com.signum.app.data.local.dao.UserDao
import com.signum.app.data.local.entity.UserEntity
import com.signum.app.domain.model.User
import com.signum.app.domain.model.UserRole
import com.signum.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun authenticate(email: String, pass: String): User? {
        val hashedPass = CryptoUtils.sha256(pass)
        val entity = userDao.authenticateUser(email, hashedPass) ?: return null
        return User(
            id = entity.id,
            name = entity.fullName,
            email = entity.email,
            role = runCatching { UserRole.valueOf(entity.role) }.getOrDefault(UserRole.SIGNER)
        )
    }

    override suspend fun getUserById(id: String): User? {
        val entity = userDao.getUserById(id) ?: return null
        return User(
            id = entity.id,
            name = entity.fullName,
            email = entity.email,
            role = runCatching { UserRole.valueOf(entity.role) }.getOrDefault(UserRole.SIGNER)
        )
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { list ->
            list.map { entity ->
                User(
                    id = entity.id,
                    name = entity.fullName,
                    email = entity.email,
                    role = runCatching { UserRole.valueOf(entity.role) }.getOrDefault(UserRole.SIGNER)
                )
            }
        }
    }

    override suspend fun insertUser(user: User) {
        userDao.insertUser(
            UserEntity(
                id = user.id,
                fullName = user.name,
                email = user.email,
                phone = "",
                passwordHash = "",
                role = user.role.name
            )
        )
    }
}
