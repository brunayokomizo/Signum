package com.signum.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.signum.app.data.local.dao.DocumentDao
import com.signum.app.data.local.dao.SignatureFieldDao
import com.signum.app.data.local.dao.UserDao
import com.signum.app.data.local.entity.DocumentEntity
import com.signum.app.data.local.entity.SignatureFieldEntity
import com.signum.app.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, DocumentEntity::class, SignatureFieldEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SignumDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun documentDao(): DocumentDao
    abstract fun signatureFieldDao(): SignatureFieldDao

    companion object {
        @Volatile
        private var INSTANCE: SignumDatabase? = null

        fun getDatabase(context: Context): SignumDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SignumDatabase::class.java,
                    "signum_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}