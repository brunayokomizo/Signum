package com.signum.app.domain.model

enum class DocumentStatus {
    DRAFT,
    PENDING_SIGNATURE,
    SIGNED,
    REJECTED
}

data class Document(
    val id: String,
    val title: String,
    val filePath: String,
    val status: DocumentStatus,
    val createdByUserId: String,
    val createdAt: Long
)
