package com.signum.app.domain.usecase

import com.signum.app.domain.model.SignatureField
import com.signum.app.domain.repository.DocumentRepository

class SignDocumentUseCase(
    private val documentRepository: DocumentRepository
) {
    suspend operator fun invoke(signatureField: SignatureField, signaturePath: String) {
        val updatedField = signatureField.copy(
            isSigned = true,
            signaturePath = signaturePath
        )
        documentRepository.updateSignatureField(updatedField)
    }
}
