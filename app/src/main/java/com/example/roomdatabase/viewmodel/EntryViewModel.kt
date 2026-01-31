package com.example.roomdatabase.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabase.repositori.RepositoriPerpustakaan
import com.example.roomdatabase.room.Buku
import com.example.roomdatabase.room.Kategori
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// State untuk Input Buku
data class DetailBuku(
    val idBuku: Int = 0,
    val judul: String = "",
    val deskripsi: String = "",
    val tanggalTerbit: String = "",
    val idKategori: Int? = null,
    val status: String = "Tersedia"
)

// State untuk Input Kategori
data class DetailKategori(
    val idKategori: Int = 0,
    val nama: String = "",
    val deskripsi: String = "",
    val parentKategoriId: Int? = null
)

class EntryViewModel(private val repositoriPerpustakaan: RepositoriPerpustakaan) : ViewModel() {

    // --- State Buku ---
    var utStateBuku by mutableStateOf(DetailBuku())
        private set
    
    fun updateUiStateBuku(detailBuku: DetailBuku) {
        utStateBuku = detailBuku
    }
    
    // --- State Kategori ---
    var uiStateKategori by mutableStateOf(DetailKategori())
        private set

    fun updateUiStateKategori(detailKategori: DetailKategori) {
        uiStateKategori = detailKategori
    }
    
    // --- UI Message State ---
    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    fun resetSnackbarMessage() {
        snackbarMessage = null
    }

    // --- Dropdown Data List ---
    val listKategori: StateFlow<List<Kategori>> = repositoriPerpustakaan.getAllKategori()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    // --- Actions ---
    
    // Mengembalikan Boolean: true jika berhasil, false jika gagal
    suspend fun saveBuku(): Boolean {
        if (validasiInputBuku()) {
            return try {
                repositoriPerpustakaan.insertBuku(utStateBuku.toBuku())
                true // Sukses
            } catch (e: Exception) {
                // Menangani error seperti Foreign Key Constraint
                if (e.message?.contains("FOREIGN KEY") == true || e.cause?.message?.contains("FOREIGN KEY") == true) {
                    snackbarMessage = "Gagal: ID Kategori '${utStateBuku.idKategori}' tidak ditemukan. Pastikan kategori sudah dibuat."
                } else {
                    snackbarMessage = "Gagal menyimpan buku: ${e.message}"
                }
                false // Gagal
            }
        } else {
            snackbarMessage = "Input tidak valid. Pastikan Judul & Kategori terisi."
            return false
        }
    }
    
    suspend fun saveKategori() {
        if (validasiInputKategori()) {
            try {
                repositoriPerpustakaan.insertKategori(uiStateKategori.toKategori())
                snackbarMessage = "Kategori berhasil disimpan!"
                // Reset form kategori opsional
                uiStateKategori = DetailKategori() 
            } catch (e: Exception) {
                snackbarMessage = "Gagal simpan Kategori: ${e.message}"
            }
        } else {
            snackbarMessage = "Nama Kategori tidak boleh kosong."
        }
    }
    
    // Validation Logic
    private fun validasiInputBuku(): Boolean {
        return utStateBuku.judul.isNotBlank() && utStateBuku.idKategori != null
    }
    
    private fun validasiInputKategori(): Boolean {
        return uiStateKategori.nama.isNotBlank()
    }
}

// Konversi Extension Functions
fun DetailBuku.toBuku(): Buku = Buku(
    idBuku = idBuku,
    judul = judul,
    deskripsi = deskripsi,
    tanggalTerbit = tanggalTerbit,
    idKategori = idKategori
)

fun DetailKategori.toKategori(): Kategori = Kategori(
    idKategori = idKategori,
    nama = nama,
    deskripsi = deskripsi,
    parentKategoriId = parentKategoriId
)