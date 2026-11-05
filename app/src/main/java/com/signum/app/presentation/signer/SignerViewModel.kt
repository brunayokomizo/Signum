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
import kotlinx.coroutines.flow.firstOrNull
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

            // Update only the signature field belonging to the active signer
            val fields = signatureFieldDao.getFieldsForDocument(documentId)
            fields.forEach { field ->
                if (field.signerId == signerId || activeName.contains(field.signerId, ignoreCase = true)) {
                    val updatedField = field.copy(
                        isSigned = true,
                        signaturePath = signaturePath ?: field.signaturePath
                    )
                    signatureFieldDao.updateSignatureField(updatedField)
                }
            }

            // Check if ALL fields or demo signers (at least 2 signers) have signed
            val allFields = signatureFieldDao.getFieldsForDocument(documentId)
            val isAllSigned = (allFields.isNotEmpty() && allFields.all { it.isSigned }) || signedSignerNames.size >= 2
            isDemoDocSigned = isAllSigned

            val statusName = if (isAllSigned) DocumentStatus.SIGNED.name else DocumentStatus.PENDING_SIGNATURE.name
            documentDao.updateDocumentStatus(documentId, statusName)
            documentDao.updateDocumentStatus("doc_123", statusName)

            if (isAllSigned) {
                val allDocs = documentDao.getAllDocuments().firstOrNull() ?: emptyList()
                allDocs.forEach { doc ->
                    documentDao.updateDocumentStatus(doc.id, DocumentStatus.SIGNED.name)
                }
            }

            onComplete()
        }
    }
}
