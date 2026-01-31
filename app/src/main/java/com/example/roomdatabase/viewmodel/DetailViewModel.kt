package com.example.roomdatabase.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabase.repositori.RepositoriPerpustakaan
import com.example.roomdatabase.room.Buku
import com.example.roomdatabase.view.route.DestinasiDetail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface DetailUiState {
    data class Success(val buku: Buku) : DetailUiState
    object Error : DetailUiState
    object Loading : DetailUiState
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoriPerpustakaan: RepositoriPerpustakaan
) : ViewModel() {

    private val bukuId: Int = checkNotNull(savedStateHandle[DestinasiDetail.itemIdArg])

    val detailUiState: StateFlow<DetailUiState> = repositoriPerpustakaan.getBuku(bukuId)
        .filterNotNull()
        .map {
            DetailUiState.Success(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailUiState.Loading
        )

    suspend fun deleteBuku() {
        // Implementasi delete logic
        // repositoriPerpustakaan.deleteBuku(...)
        // Perlu mengambil current state buku
        val currentState = detailUiState.value
        if (currentState is DetailUiState.Success) {
            repositoriPerpustakaan.deleteBuku(currentState.buku)
        }
    }
}