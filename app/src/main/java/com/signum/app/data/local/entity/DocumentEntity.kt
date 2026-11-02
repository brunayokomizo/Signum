package com.signum.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val filePath: String?,
    val pdfBlob: ByteArray?,
    val status: String,
    val adminId: String,
    val createdAt: Long,
    val version: Int = 1
)