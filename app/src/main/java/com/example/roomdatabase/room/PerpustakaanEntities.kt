package com.example.roomdatabase.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "kategori")
data class Kategori(
    @PrimaryKey(autoGenerate = true)
    val idKategori: Int = 0,
    val nama: String,
    val deskripsi: String,
    val parentKategoriId: Int? = null, // Self-referencing untuk hierarki
    val isDeleted: Boolean = false
)

@Entity(tableName = "penulis")
data class Penulis(
    @PrimaryKey(autoGenerate = true)
    val idPenulis: Int = 0,
    val nama: String,
    val biografi: String,
    val isDeleted: Boolean = false
)

@Entity(
    tableName = "buku",
    foreignKeys = [
        ForeignKey(
            entity = Kategori::class,
            parentColumns = ["idKategori"],
            childColumns = ["idKategori"],
            onDelete = ForeignKey.RESTRICT // Prevent hard delete if linked
        )
    ],
    indices = [Index(value = ["idKategori"])]
)
data class Buku(
    @PrimaryKey(autoGenerate = true)
    val idBuku: Int = 0,
    val judul: String,
    val deskripsi: String,
    val tanggalTerbit: String,
    val idKategori: Int?, // Nullable jika "Tanpa Kategori" setelah delete parent
    val isDeleted: Boolean = false
)

@Entity(
    tableName = "buku_fisik",
    foreignKeys = [
        ForeignKey(
            entity = Buku::class,
            parentColumns = ["idBuku"],
            childColumns = ["idBuku"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idBuku"])]
)
data class BukuFisik(
    @PrimaryKey(autoGenerate = true)
    val idBukuFisik: Int = 0,
    val idBuku: Int,
    val kondisi: String, // "Baik", "Rusak"
    val statusPeminjaman: String, // "Tersedia", "Dipinjam", "Hilang"
    val lokasiRak: String
)

@Entity(
    tableName = "buku_penulis_cross_ref",
    primaryKeys = ["idBuku", "idPenulis"],
    indices = [Index(value = ["idPenulis"]), Index(value = ["idBuku"])]
)
data class BukuPenulisCrossRef(
    val idBuku: Int,
    val idPenulis: Int
)

@Entity(tableName = "audit_log")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val idAudit: Int = 0,
    val entityName: String,
    val entityId: Int,
    val action: String, // "INSERT", "UPDATE", "DELETE", "SOFT_DELETE"
    val timestamp: Long = System.currentTimeMillis(),
    val catatan: String
)

data class BukuWithKategori(
    @androidx.room.Embedded val buku: Buku,
    @androidx.room.Relation(
        parentColumn = "idKategori",
        entityColumn = "idKategori"
    )
    val kategori: Kategori?
)
