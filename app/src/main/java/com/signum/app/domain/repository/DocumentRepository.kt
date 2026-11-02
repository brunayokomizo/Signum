package com.signum.app.domain.repository

import com.signum.app.domain.model.Document
import com.signum.app.domain.model.SignatureField
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getDocumentsByAdmin(adminId: String): Flow<List<Document>>
    fun getDocumentsForUser(userId: String): Flow<List<Document>>
    fun getAllDocuments(): Flow<List<Document>>
    suspend fun getDocumentById(id: String): Document?
    suspend fun saveDocument(document: Document)
    suspend fun createDocument(document: Document, signatureFields: List<SignatureField>)
    suspend fun getSignatureFieldsForDocument(documentId: String): List<SignatureField>
    suspend fun updateSignatureField(field: SignatureField)
    suspend fun updateDocumentStatus(documentId: String, status: String)
}
