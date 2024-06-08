package com.krispy.kelompok1_jdih.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [Dokumen::class],
    version = 1
)

abstract class DokumenDB : RoomDatabase() {
    abstract fun DokumenDAO(): DokumenDAO

    companion object {
        @Volatile
        private var instance: DokumenDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
               instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            DokumenDB::class.java,
            "dokumen.db"
        ).build()

    }
}