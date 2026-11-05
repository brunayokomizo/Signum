package com.signum.app.presentation.signer

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signum.app.data.local.dao.DocumentDao
import com.signum.app.data.local.dao.SignatureFieldDao
import com.signum.app.data.local.entity.DocumentEntity
import com.signum.app.data.local.entity.SignatureFieldEntity
import com.signum.app.domain.model.DocumentStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class SignerViewModel(
    private val documentDao: DocumentDao,
    private val signatureFieldDao: SignatureFieldDao
) : ViewModel() {

    var loggedInSignerName by mutableStateOf("Ana Aparecida Lisboa")
    var loggedInSignerEmail by mutableStateOf("ana.lisboa@email.com")

    var isDemoDocSigned by mutableStateOf(false)
    val signedSignerNames = mutableStateListOf<String>()

    fun getPendingDocumentsForSigner(signerId: String = "signer_1"): Flow<List<DocumentEntity>> {
        return documentDao.getAllDocuments()
    }

    fun getCompletedDocumentsForSigner(signerId: String = "signer_1"): Flow<List<DocumentEntity>> {
        return documentDao.getAllDocuments()
    }

    fun getSignatureFieldsForDocument(documentId: String, onResult: (List<SignatureFieldEntity>) -> Unit) {
        viewModelScope.launch {
            val fields = signatureFieldDao.getFieldsForDocument(documentId)
            onResult(fields)
        }
    }

    fun completeSignature(
        context: Context,
        documentId: String,
        signerId: String = "signer_1",
        signerName: String = "Ana Aparecida Lisboa",
        signatureBitmap: Bitmap? = null,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            isDemoDocSigned = true
            val activeName = signerName.ifBlank { loggedInSignerName }
            if (!signedSignerNames.contains(activeName)) {
                signedSignerNames.add(activeName)
            }

            var signaturePath: String? = null
            if (signatureBitmap != null) {
                val signaturesDir = File(context.filesDir, "signatures")
                if (!signaturesDir.exists()) signaturesDir.mkdirs()
                val file = File(signaturesDir, "sig_${documentId}_$signerId.png")
                FileOutputStream(file).use { out ->
                    signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                signaturePath = file.absolutePath
            }

            val fields = signatureFieldDao.getFieldsForDocument(documentId)
            fields.forEach { field ->
                val updatedField = field.copy(
                    isSigned = true,
                    signaturePath = signaturePath ?: field.signaturePath
                )
                signatureFieldDao.updateSignatureField(updatedField)
            }

            // Update status in Room DB
            documentDao.updateDocumentStatus(documentId, DocumentStatus.SIGNED.name)
            documentDao.updateDocumentStatus("doc_123", DocumentStatus.SIGNED.name)
            onComplete()
        }
    }
}
