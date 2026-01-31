package com.example.roomdatabase.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Kategori::class,
        Penulis::class,
        Buku::class,
        BukuFisik::class,
        BukuPenulisCrossRef::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DatabasePerpustakaan : RoomDatabase() {
    abstract fun kategoriDao(): KategoriDao
    abstract fun penulisDao(): PenulisDao
    abstract fun bukuDao(): BukuDao
    abstract fun bukuFisikDao(): BukuFisikDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var Instance: DatabasePerpustakaan? = null

        fun getDatabase(context: Context): DatabasePerpustakaan {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    DatabasePerpustakaan::class.java,
                    "perpustakaan_database"
                )
                    .fallbackToDestructiveMigration() // Hapus database lama jika schema berubah (untuk dev)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
