package com.signum.app.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signum.app.data.local.CryptoUtils
import com.signum.app.data.local.dao.UserDao
import com.signum.app.data.local.entity.UserEntity
import com.signum.app.data.repository.DocumentRepositoryImpl
import kotlinx.coroutines.launch
import java.util.UUID

class AdminViewModel(
    private val userDao: UserDao,
    private val documentRepository: DocumentRepositoryImpl
) : ViewModel() {

    fun registerSigner(fullName: String, email: String, phone: String, rawPassword: String) {
        viewModelScope.launch {
            val newUser = UserEntity(
                id = UUID.randomUUID().toString(),
                fullName = fullName,
                email = email,
                phone = phone,
                passwordHash = CryptoUtils.sha256(rawPassword),
                role = "SIGNER"
            )
            userDao.insertUser(newUser)
        }
    }

    val allSigners = userDao.getAllSigners()

    fun getAdminDocuments(adminId: String) = documentRepository.getDocumentsByAdmin(adminId)
}
