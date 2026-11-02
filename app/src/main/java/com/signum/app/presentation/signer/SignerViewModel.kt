package com.signum.app.presentation.signer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signum.app.data.local.dao.DocumentDao
import kotlinx.coroutines.launch

class SignerViewModel(
    private val documentDao: DocumentDao
) : ViewModel() {

    fun completeSignature(documentId: String) {
        viewModelScope.launch {
            documentDao.updateDocumentStatus(documentId, "COMPLETED")
        }
    }
}
