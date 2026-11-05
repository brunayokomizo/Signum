package com.signum.app.presentation.admin

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signum.app.data.local.CryptoUtils
import com.signum.app.data.local.dao.DocumentDao
import com.signum.app.data.local.dao.SignatureFieldDao
import com.signum.app.data.local.dao.UserDao
import com.signum.app.data.local.entity.DocumentEntity
import com.signum.app.data.local.entity.SignatureFieldEntity
import com.signum.app.data.local.entity.UserEntity
import com.signum.app.data.repository.DocumentRepositoryImpl
import com.signum.app.domain.model.DocumentStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

data class SignerSelection(
    val user: UserEntity,
    val role: String = "Contratante"
)

class AdminViewModel(
    private val userDao: UserDao,
    private val documentDao: DocumentDao,
    private val signatureFieldDao: SignatureFieldDao,
    private val documentRepository: DocumentRepositoryImpl
) : ViewModel() {

    // Draft State for Document Setup Flow
    var currentDocumentTitle by mutableStateOf("")
    var currentSignersInfo by mutableStateOf("")
    var currentSignaturePagesInfo by mutableStateOf("")
    val currentSignersList = mutableStateListOf<SignerAssignmentUI>()

    val allSigners: Flow<List<UserEntity>> = userDao.getAllSigners()

    fun resetDraftFlow() {
        currentDocumentTitle = ""
        currentSignersInfo = ""
        currentSignaturePagesInfo = ""
        currentSignersList.clear()
    }

    fun deleteDocument(documentId: String) {
        viewModelScope.launch {
            documentDao.deleteDocument(documentId)
        }
    }

    fun deleteSigner(signerId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            userDao.deleteUser(signerId)
            onComplete()
        }
    }

    fun registerOrUpdateSigner(
        id: String? = null,
        fullName: String,
        email: String,
        phone: String,
        rawPassword: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val passwordHash = if (rawPassword.isBlank() && id != null) {
                userDao.getUserById(id)?.passwordHash ?: CryptoUtils.sha256("123456")
            } else {
                CryptoUtils.sha256(rawPassword)
            }

            val signer = UserEntity(
                id = id ?: UUID.randomUUID().toString(),
                fullName = fullName,
                email = email,
                phone = phone,
                passwordHash = passwordHash,
                role = "SIGNER"
            )

            if (id != null) {
                userDao.updateUser(signer)
            } else {
                userDao.insertUser(signer)
            }
            onComplete()
        }
    }

    fun getAdminDocuments(adminId: String = "admin_1"): Flow<List<DocumentEntity>> {
        return documentDao.getDocumentsByAdmin(adminId)
    }

    fun validatePdfFile(context: Context, uri: Uri): Boolean {
        return try {
            val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return false
            val renderer = PdfRenderer(pfd)
            val isValid = renderer.pageCount > 0
            renderer.close()
            pfd.close()
            isValid
        } catch (e: Exception) {
            false
        }
    }

    fun saveDocumentFlow(
        context: Context,
        title: String,
        pdfUri: Uri?,
        signers: List<SignerSelection>,
        fields: List<SignatureFieldEntity>,
        adminId: String = "admin_1",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val docId = UUID.randomUUID().toString()
                var savedFilePath: String? = null
                var pdfBytes: ByteArray? = null

                if (pdfUri != null) {
                    val bytes = context.contentResolver.openInputStream(pdfUri)?.use { it.readBytes() }
                    pdfBytes = bytes
                    
                    val docsDir = File(context.filesDir, "documents")
                    if (!docsDir.exists()) docsDir.mkdirs()
                    val destFile = File(docsDir, "$docId.pdf")
                    if (bytes != null) {
                        destFile.writeBytes(bytes)
                        savedFilePath = destFile.absolutePath
                    }
                }

                val document = DocumentEntity(
                    id = docId,
                    title = title,
                    filePath = savedFilePath,
                    pdfBlob = pdfBytes,
                    status = DocumentStatus.PENDING_SIGNATURE.name,
                    adminId = adminId,
                    createdAt = System.currentTimeMillis(),
                    version = 1
                )

                documentDao.insertDocument(document)

                val updatedFields = fields.map { field ->
                    field.copy(
                        id = UUID.randomUUID().toString(),
                        documentId = docId
                    )
                }

                signatureFieldDao.insertSignatureFields(updatedFields)
                onSuccess(docId)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Erro ao salvar documento no banco de dados.")
            }
        }
    }
}
