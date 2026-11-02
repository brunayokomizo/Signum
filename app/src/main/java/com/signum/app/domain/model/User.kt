package com.signum.app.domain.model

enum class UserRole {
    ADMIN,
    SIGNER
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole
)
