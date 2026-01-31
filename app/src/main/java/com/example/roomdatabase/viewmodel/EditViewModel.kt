package com.example.roomdatabase.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabase.repositori.RepositoriPerpustakaan
import com.example.roomdatabase.room.Kategori
import com.example.roomdatabase.view.route.DestinasiEdit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoriPerpustakaan: RepositoriPerpustakaan
) : ViewModel() {

    var bukuUiState by mutableStateOf(DetailBuku())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[DestinasiEdit.itemIdArg])
    
    // Provide categories list for Dropdown in Edit Form
    val listKategori: StateFlow<List<Kategori>> = repositoriPerpustakaan.getAllKategori()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    init {
        viewModelScope.launch {
            val buku = repositoriPerpustakaan.getBuku(itemId)
                .filterNotNull()
                .first()
            bukuUiState = buku.toDetailBuku()
        }
    }

    fun updateUiState(detailBuku: DetailBuku) {
        bukuUiState = detailBuku
    }

    suspend fun updateBuku() {
        if (validasiInput(bukuUiState)) {
            repositoriPerpustakaan.updateBuku(bukuUiState.toBuku())
        }
    }
    
    private fun validasiInput(uiState: DetailBuku = bukuUiState): Boolean {
        // Minimal ada Judul. Kategori optional tapi sebaiknya ada.
        return uiState.judul.isNotBlank()
    }
}

fun com.example.roomdatabase.room.Buku.toDetailBuku(): DetailBuku = DetailBuku(
    idBuku = idBuku,
    judul = judul,
    deskripsi = deskripsi,
    tanggalTerbit = tanggalTerbit,
    idKategori = idKategori,
)