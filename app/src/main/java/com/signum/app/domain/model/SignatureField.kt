package com.signum.app.domain.model

data class SignatureField(
    val id: String,
    val documentId: String,
    val signerUserId: String,
    val role: String = "Contratante",
    val pageNumber: Int,
    val xPosition: Float,
    val yPosition: Float,
    val width: Float = 0.2f,
    val height: Float = 0.08f,
    val isSigned: Boolean = false,
    val signaturePath: String? = null
)
