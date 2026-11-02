package com.signum.app.data.local.dao

import androidx.room.*
import com.signum.app.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Query("SELECT * FROM documents WHERE adminId = :adminId ORDER BY createdAt DESC")
    fun getDocumentsByAdmin(adminId: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: String): DocumentEntity?

    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("UPDATE documents SET status = :status WHERE id = :documentId")
    suspend fun updateDocumentStatus(documentId: String, status: String): Int
}
