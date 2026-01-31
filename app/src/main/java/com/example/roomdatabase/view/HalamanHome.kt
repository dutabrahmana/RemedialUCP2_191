package com.example.roomdatabase.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomdatabase.room.BukuWithKategori
import com.example.roomdatabase.viewmodel.HomeUiState
import com.example.roomdatabase.viewmodel.HomeViewModel
import com.example.roomdatabase.viewmodel.provider.PenyediaViewModel
import com.example.roomdatabase.view.route.DestinasiHome
import com.example.roomdatabase.view.route.DestinasiEntry
import com.example.roomdatabase.view.route.DestinasiEntryKategori

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHome(
    navigateToItemEntry: () -> Unit,
    navigateToKategoriEntry: () -> Unit,
    onDetailClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SiswaTopAppBar(
                title = DestinasiHome.titleRes,
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                // Tombol Tambah Kategori
                ExtendedFloatingActionButton(
                    onClick = navigateToKategoriEntry,
                    icon = { Icon(Icons.Default.List, contentDescription = "Tambah Kategori") },
                    text = { Text(text = "Tambah Kategori") },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                // Tombol Tambah Buku
                ExtendedFloatingActionButton(
                    onClick = navigateToItemEntry,
                    icon = { Icon(Icons.Default.Add, contentDescription = "Tambah Buku") },
                    text = { Text(text = "Tambah Buku") }
                )
            }
        },
    ) { innerPadding ->
        when (val state = homeUiState) {
            is HomeUiState.Loading -> {
                // Show loading
                Text(text = "Loading...", modifier = Modifier.padding(innerPadding))
            }
            is HomeUiState.Error -> {
                Text(text = "Terjadi Kesalahan", modifier = Modifier.padding(innerPadding))
            }
            is HomeUiState.Success -> {
                 HomeBody(
                    listBuku = state.buku,
                    onBukuClick = onDetailClick,
                    modifier = modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun HomeBody(
    listBuku: List<BukuWithKategori>,
    onBukuClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (listBuku.isEmpty()) {
            Text(
                text = "Tidak ada data buku",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listBuku) { bukuWithKategori ->
                    DataBukuItem(
                        bukuWithKategori = bukuWithKategori,
                        onBukuClick = onBukuClick
                    )
                }
            }
        }
    }
}

@Composable
fun DataBukuItem(
    bukuWithKategori: BukuWithKategori,
    onBukuClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onBukuClick(bukuWithKategori.buku.idBuku) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = bukuWithKategori.buku.judul,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "Deskripsi: ${bukuWithKategori.buku.deskripsi}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Terbit: ${bukuWithKategori.buku.tanggalTerbit}",
                    style = MaterialTheme.typography.bodySmall
                )
                // Menampilkan Nama Kategori
                Text(
                    text = bukuWithKategori.kategori?.nama ?: "Tanpa Kategori",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}