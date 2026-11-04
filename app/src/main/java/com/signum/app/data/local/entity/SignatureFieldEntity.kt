package com.signum.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signature_fields")
data class SignatureFieldEntity(
    @PrimaryKey val id: String,
    val documentId: String,
    val signerId: String,
    val role: String,
    val pageNumber: Int,
    val positionX: Float,
    val positionY: Float,
    val width: Float = 0.2f,
    val height: Float = 0.08f,
    val isSigned: Boolean = false,
    val signaturePath: String? = null
)
