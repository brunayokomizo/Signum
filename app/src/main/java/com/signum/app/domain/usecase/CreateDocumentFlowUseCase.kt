package com.signum.app.domain.usecase

import com.signum.app.domain.model.Document
import com.signum.app.domain.model.SignatureField
import com.signum.app.domain.repository.DocumentRepository

class CreateDocumentFlowUseCase(
    private val documentRepository: DocumentRepository
) {
    suspend operator fun invoke(document: Document, signatureFields: List<SignatureField>) {
        documentRepository.createDocument(document, signatureFields)
    }
}
