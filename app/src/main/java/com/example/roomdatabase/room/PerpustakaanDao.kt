package com.example.roomdatabase.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KategoriDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(kategori: Kategori)

    @Update
    suspend fun update(kategori: Kategori)

    @Delete
    suspend fun delete(kategori: Kategori)

    @Query("SELECT * FROM kategori WHERE idKategori = :id")
    fun getKategori(id: Int): Flow<Kategori>

    @Query("SELECT * FROM kategori WHERE isDeleted = 0 ORDER BY nama ASC")
    fun getAllKategori(): Flow<List<Kategori>>

    // Mencari semua kategori yang memiliki parent tertentu
    @Query("SELECT * FROM kategori WHERE parentKategoriId = :parentId AND isDeleted = 0")
    fun getSubKategori(parentId: Int): Flow<List<Kategori>>
}

@Dao
interface PenulisDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(penulis: Penulis)

    @Update
    suspend fun update(penulis: Penulis)

    @Query("SELECT * FROM penulis WHERE isDeleted = 0 ORDER BY nama ASC")
    fun getAllPenulis(): Flow<List<Penulis>>
    
    @Query("SELECT * FROM penulis WHERE idPenulis = :id")
    fun getPenulis(id: Int): Flow<Penulis>
}

@Dao
interface BukuDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(buku: Buku)

    @Update
    suspend fun update(buku: Buku)

    @Delete
    suspend fun delete(buku: Buku)

    @Query("SELECT * FROM buku WHERE idBuku = :id")
    fun getBuku(id: Int): Flow<Buku>

    @Query("SELECT * FROM buku WHERE isDeleted = 0 ORDER BY judul ASC")
    fun getAllBuku(): Flow<List<Buku>>

    @androidx.room.Transaction
    @Query("SELECT * FROM buku WHERE isDeleted = 0 ORDER BY judul ASC")
    fun getAllBukuWithKategori(): Flow<List<BukuWithKategori>>

    @Query("SELECT * FROM buku WHERE idKategori = :kategoriId AND isDeleted = 0")
    fun getBukuByKategori(kategoriId: Int): Flow<List<Buku>>

    // Insert CrossRef
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBukuPenulis(crossRef: BukuPenulisCrossRef)
}

@Dao
interface BukuFisikDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bukuFisik: BukuFisik)

    @Update
    suspend fun update(bukuFisik: BukuFisik)

    @Delete
    suspend fun delete(bukuFisik: BukuFisik)
    
    @Query("SELECT * FROM buku_fisik WHERE idBuku = :bukuId")
    fun getFisikByBuku(bukuId: Int): Flow<List<BukuFisik>>

    @Query("SELECT * FROM buku_fisik WHERE statusPeminjaman = 'Dipinjam' AND idBuku IN (SELECT idBuku FROM buku WHERE idKategori = :kategoriId)")
    suspend fun cekBukuDipinjamDiKategori(kategoriId: Int): List<BukuFisik>
}

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insert(log: AuditLog)
    
    @Query("SELECT * FROM audit_log ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLog>>
}
