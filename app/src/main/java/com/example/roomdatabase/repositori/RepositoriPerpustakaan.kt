package com.example.roomdatabase.repositori

import androidx.room.withTransaction
import com.example.roomdatabase.room.AuditLog
import com.example.roomdatabase.room.AuditLogDao
import com.example.roomdatabase.room.Buku
import com.example.roomdatabase.room.BukuDao
import com.example.roomdatabase.room.BukuFisik
import com.example.roomdatabase.room.BukuFisikDao
import com.example.roomdatabase.room.BukuWithKategori
import com.example.roomdatabase.room.DatabasePerpustakaan
import com.example.roomdatabase.room.Kategori
import com.example.roomdatabase.room.KategoriDao
import com.example.roomdatabase.room.Penulis
import com.example.roomdatabase.room.PenulisDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface RepositoriPerpustakaan {
    fun getAllKategori(): Flow<List<Kategori>>
    fun getKategori(id: Int): Flow<Kategori>
    suspend fun insertKategori(kategori: Kategori)
    suspend fun updateKategori(kategori: Kategori)
    suspend fun deleteKategori(kategoriId: Int, deleteBooks: Boolean)

    fun getAllBuku(): Flow<List<Buku>>
    fun getAllBukuWithKategori(): Flow<List<BukuWithKategori>>
    fun getBuku(id: Int): Flow<Buku>
    fun getBukuByKategori(kategoriId: Int): Flow<List<Buku>>
    suspend fun insertBuku(buku: Buku)
    suspend fun updateBuku(buku: Buku)
    suspend fun deleteBuku(buku: Buku)

    fun getAllPenulis(): Flow<List<Penulis>>
    suspend fun insertPenulis(penulis: Penulis)
    
    fun getAllAuditLogs(): Flow<List<AuditLog>>
}

class OfflineRepositoriPerpustakaan(
    private val db: DatabasePerpustakaan,
    private val kategoriDao: KategoriDao,
    private val bukuDao: BukuDao,
    private val penulisDao: PenulisDao,
    private val bukuFisikDao: BukuFisikDao,
    private val auditLogDao: AuditLogDao
) : RepositoriPerpustakaan {

    override fun getAllKategori(): Flow<List<Kategori>> = kategoriDao.getAllKategori()
    override fun getKategori(id: Int): Flow<Kategori> = kategoriDao.getKategori(id)

    override suspend fun insertKategori(kategori: Kategori) {
        validateKategori(kategori)
        db.withTransaction {
            kategoriDao.insert(kategori)
            auditLogDao.insert(
                AuditLog(
                    entityName = "Kategori",
                    entityId = 0,
                    action = "INSERT",
                    catatan = "Menambahkan kategori: ${kategori.nama}"
                )
            )
        }
    }

    override suspend fun updateKategori(kategori: Kategori) {
        validateKategori(kategori)
        checkForCycles(kategori.idKategori, kategori.parentKategoriId)
        
        db.withTransaction {
            kategoriDao.update(kategori)
            auditLogDao.insert(
                AuditLog(
                    entityName = "Kategori",
                    entityId = kategori.idKategori,
                    action = "UPDATE",
                    catatan = "Update kategori: ${kategori.nama}"
                )
            )
        }
    }

    override suspend fun deleteKategori(kategoriId: Int, deleteBooks: Boolean) {
        db.withTransaction {
            val bukuDipinjam = bukuFisikDao.cekBukuDipinjamDiKategori(kategoriId)
            if (bukuDipinjam.isNotEmpty()) {
                throw Exception("Gagal menghapus! Ada buku yang sedang dipinjam dalam kategori ini.")
            }

            val kategori = kategoriDao.getKategori(kategoriId).first()

            val bukuList = bukuDao.getBukuByKategori(kategoriId).first()
            if (bukuList.isNotEmpty()) {
                if (deleteBooks) {
                    bukuList.forEach { buku ->
                        bukuDao.update(buku.copy(isDeleted = true))
                    }
                } else {
                    bukuList.forEach { buku ->
                        bukuDao.update(buku.copy(idKategori = null))
                    }
                }
            }

            kategoriDao.update(kategori.copy(isDeleted = true))
            
            auditLogDao.insert(
                AuditLog(
                    entityName = "Kategori",
                    entityId = kategoriId,
                    action = "SOFT_DELETE",
                    catatan = "Kategori dihapus. Buku ikut dihapus: $deleteBooks"
                )
            )
        }
    }
    
    private fun validateKategori(kategori: Kategori) {
        if (kategori.nama.isBlank()) throw IllegalArgumentException("Nama Kategori tidak boleh kosong")
    }

    private suspend fun checkForCycles(childId: Int, parentId: Int?) {
        if (parentId == null) return
        if (childId == parentId) throw IllegalArgumentException("Cyclic Reference: Kategori tidak bisa menjadi induk dirinya sendiri")
        
        var currentParentId = parentId
        while (currentParentId != null) {
            if (currentParentId == childId) {
                throw IllegalArgumentException("Cyclic Reference Detected! Loop dalam hierarki kategori.")
            }
            val parentNode = kategoriDao.getKategori(currentParentId).first()
            currentParentId = parentNode.parentKategoriId
        }
    }

    override fun getAllBuku(): Flow<List<Buku>> = bukuDao.getAllBuku()
    override fun getAllBukuWithKategori(): Flow<List<BukuWithKategori>> = bukuDao.getAllBukuWithKategori()
    override fun getBuku(id: Int): Flow<Buku> = bukuDao.getBuku(id)
    override fun getBukuByKategori(kategoriId: Int): Flow<List<Buku>> = bukuDao.getBukuByKategori(kategoriId)

    override suspend fun insertBuku(buku: Buku) {
        if (buku.judul.isBlank()) throw IllegalArgumentException("Judul Buku tidak boleh kosong")
        db.withTransaction {
            bukuDao.insert(buku)
            auditLogDao.insert(AuditLog(entityName = "Buku", entityId = 0, action = "INSERT", catatan = "Tambah buku: ${buku.judul}"))
        }
    }

    override suspend fun updateBuku(buku: Buku) {
        db.withTransaction {
            bukuDao.update(buku)
            auditLogDao.insert(AuditLog(entityName = "Buku", entityId = buku.idBuku, action = "UPDATE", catatan = "Update buku: ${buku.judul}"))
        }
    }

    override suspend fun deleteBuku(buku: Buku) {
        db.withTransaction {
            bukuDao.update(buku.copy(isDeleted = true))
            auditLogDao.insert(AuditLog(entityName = "Buku", entityId = buku.idBuku, action = "SOFT_DELETE", catatan = "Hapus buku: ${buku.judul}"))
        }
    }

    override fun getAllPenulis(): Flow<List<Penulis>> = penulisDao.getAllPenulis()
    
    override suspend fun insertPenulis(penulis: Penulis) {
         db.withTransaction {
            penulisDao.insert(penulis)
         }
    }
    
    override fun getAllAuditLogs(): Flow<List<AuditLog>> = auditLogDao.getAllLogs()
}
