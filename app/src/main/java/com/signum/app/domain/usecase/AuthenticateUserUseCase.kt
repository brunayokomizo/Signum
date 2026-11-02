package com.signum.app.domain.usecase

import com.signum.app.domain.model.User
import com.signum.app.domain.repository.UserRepository

class AuthenticateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, pass: String): User? {
        return userRepository.authenticate(email, pass)
    }
}
