package com.signum.app.data.repository

import com.signum.app.data.local.dao.DocumentDao
import com.signum.app.data.local.dao.SignatureFieldDao
import com.signum.app.data.local.entity.DocumentEntity
import com.signum.app.data.local.entity.SignatureFieldEntity
import com.signum.app.domain.model.Document
import com.signum.app.domain.model.DocumentStatus
import com.signum.app.domain.model.SignatureField
import com.signum.app.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentRepositoryImpl(
    private val documentDao: DocumentDao,
    private val signatureFieldDao: SignatureFieldDao
) : DocumentRepository {

    override fun getDocumentsByAdmin(adminId: String): Flow<List<Document>> {
        return documentDao.getDocumentsByAdmin(adminId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDocumentsForUser(userId: String): Flow<List<Document>> {
        return documentDao.getAllDocuments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllDocuments(): Flow<List<Document>> {
        return documentDao.getAllDocuments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDocumentById(id: String): Document? {
        return documentDao.getDocumentById(id)?.toDomain()
    }

    override suspend fun saveDocument(document: Document) {
        documentDao.insertDocument(document.toEntity())
    }

    override suspend fun createDocument(document: Document, signatureFields: List<SignatureField>) {
        documentDao.insertDocument(document.toEntity())
        signatureFieldDao.insertSignatureFields(signatureFields.map { it.toEntity() })
    }

    override suspend fun getSignatureFieldsForDocument(documentId: String): List<SignatureField> {
        return signatureFieldDao.getFieldsForDocument(documentId).map { it.toDomain() }
    }

    override suspend fun updateSignatureField(field: SignatureField) {
        signatureFieldDao.updateSignatureField(field.toEntity())
    }

    override suspend fun updateDocumentStatus(documentId: String, status: String) {
        documentDao.updateDocumentStatus(documentId, status)
    }

    private fun DocumentEntity.toDomain() = Document(
        id = id,
        title = title,
        filePath = filePath ?: "",
        status = runCatching { DocumentStatus.valueOf(status) }.getOrDefault(DocumentStatus.DRAFT),
        createdByUserId = adminId,
        createdAt = createdAt
    )

    private fun Document.toEntity() = DocumentEntity(
        id = id,
        title = title,
        filePath = filePath,
        pdfBlob = null,
        status = status.name,
        adminId = createdByUserId,
        createdAt = createdAt
    )

    private fun SignatureFieldEntity.toDomain() = SignatureField(
        id = id,
        documentId = documentId,
        signerUserId = signerId,
        pageNumber = pageNumber,
        xPosition = positionX,
        yPosition = positionY,
        width = 0f,
        height = 0f,
        isSigned = isSigned,
        signaturePath = null
    )

    private fun SignatureField.toEntity() = SignatureFieldEntity(
        id = id,
        documentId = documentId,
        signerId = signerUserId,
        role = "SIGNER",
        pageNumber = pageNumber,
        positionX = xPosition,
        positionY = yPosition,
        isSigned = isSigned
    )
}
