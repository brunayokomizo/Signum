package com.signum.app.di

import android.content.Context
import com.signum.app.data.local.SignumDatabase
import com.signum.app.data.repository.DocumentRepositoryImpl

class AppContainer(context: Context) {
    private val database = SignumDatabase.getDatabase(context)

    val userDao = database.userDao()
    val documentRepository = DocumentRepositoryImpl(
        documentDao = database.documentDao(),
        signatureFieldDao = database.signatureFieldDao()
    )
    val signatureFieldDao = database.signatureFieldDao()
}
