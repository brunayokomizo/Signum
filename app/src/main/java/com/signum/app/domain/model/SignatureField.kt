package com.signum.app.domain.model

data class SignatureField(
    val id: String,
    val documentId: String,
    val signerUserId: String,
    val pageNumber: Int,
    val xPosition: Float,
    val yPosition: Float,
    val width: Float,
    val height: Float,
    val isSigned: Boolean = false,
    val signaturePath: String? = null
)
