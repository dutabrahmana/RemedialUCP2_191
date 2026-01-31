package com.example.roomdatabase.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabase.repositori.RepositoriPerpustakaan
import com.example.roomdatabase.room.Buku
import com.example.roomdatabase.room.BukuWithKategori
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

sealed class HomeUiState {
    data class Success(val buku: List<BukuWithKategori>) : HomeUiState()
    object Error : HomeUiState()
    object Loading : HomeUiState()
}

class HomeViewModel(private val repositoriPerpustakaan: RepositoriPerpustakaan) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState> = repositoriPerpustakaan.getAllBukuWithKategori()
        .filterNotNull()
        .map { HomeUiState.Success(it) as HomeUiState }
        .onStart { emit(HomeUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )
}
