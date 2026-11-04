package com.signum.app.data.local.dao

import androidx.room.*
import com.signum.app.data.local.entity.SignatureFieldEntity

@Dao
interface SignatureFieldDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignatureFields(fields: List<SignatureFieldEntity>): List<Long>

    @Query("SELECT * FROM signature_fields WHERE documentId = :documentId")
    suspend fun getFieldsForDocument(documentId: String): List<SignatureFieldEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSignatureField(field: SignatureFieldEntity): Long

    @Query("DELETE FROM signature_fields WHERE id = :id")
    suspend fun deleteSignatureField(id: String): Int
}
