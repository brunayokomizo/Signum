package com.signum.app.di

import android.content.Context
import com.signum.app.data.local.CryptoUtils
import com.signum.app.data.local.SignumDatabase
import com.signum.app.data.local.entity.UserEntity
import com.signum.app.data.repository.DocumentRepositoryImpl
import com.signum.app.data.repository.UserRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
    val database = SignumDatabase.getDatabase(context)

    val userDao = database.userDao()
    val documentDao = database.documentDao()
    val signatureFieldDao = database.signatureFieldDao()

    val userRepository = UserRepositoryImpl(userDao)
    val documentRepository = DocumentRepositoryImpl(
        documentDao = documentDao,
        signatureFieldDao = signatureFieldDao
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (userDao.getUserByEmail("admin@signum.com") == null) {
                userDao.insertUser(
                    UserEntity(
                        id = "admin_1",
                        fullName = "Administrador",
                        email = "admin@signum.com",
                        phone = "11999999999",
                        passwordHash = CryptoUtils.sha256("admin123"),
                        role = "ADMIN"
                    )
                )
            }
        }
    }
}
