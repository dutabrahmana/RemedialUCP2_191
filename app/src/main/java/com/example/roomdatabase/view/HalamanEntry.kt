package com.example.roomdatabase.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomdatabase.room.Kategori
import com.example.roomdatabase.viewmodel.DetailBuku
import com.example.roomdatabase.viewmodel.EntryViewModel
import com.example.roomdatabase.viewmodel.provider.PenyediaViewModel
import com.example.roomdatabase.view.route.DestinasiEntry
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntry(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listKategori by viewModel.listKategori.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.snackbarMessage) {
        viewModel.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.resetSnackbarMessage()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SiswaTopAppBar(
                title = DestinasiEntry.titleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        EntryBody(
            uiStateBuku = viewModel.utStateBuku,
            onBukuValueChange = viewModel::updateUiStateBuku,
            onSaveClick = {
                coroutineScope.launch {
                    if (viewModel.saveBuku()) {
                        navigateBack()
                    }
                }
            },
            listKategori = listKategori,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun EntryBody(
    uiStateBuku: DetailBuku,
    onBukuValueChange: (DetailBuku) -> Unit,
    onSaveClick: () -> Unit,
    listKategori: List<Kategori>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.padding(16.dp)
    ) {
        // Form Entry Buku
        Text(text = "Input Buku", style = MaterialTheme.typography.titleMedium)
        
        FormInputBuku(
            detailBuku = uiStateBuku,
            onValueChange = onBukuValueChange,
            listKategori = listKategori, // Passing list kategori ke form
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Simpan Buku")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputBuku(
    detailBuku: DetailBuku,
    onValueChange: (DetailBuku) -> Unit,
    listKategori: List<Kategori>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // State untuk dropdown
    var expanded by remember { mutableStateOf(false) }
    
    // Cari nama kategori yang sedang dipilih berdasarkan ID
    val selectedKategori = listKategori.find { it.idKategori == detailBuku.idKategori }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = detailBuku.judul,
            onValueChange = { onValueChange(detailBuku.copy(judul = it)) },
            label = { Text("Judul Buku") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = detailBuku.deskripsi,
            onValueChange = { onValueChange(detailBuku.copy(deskripsi = it)) },
            label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = false
        )
        OutlinedTextField(
            value = detailBuku.tanggalTerbit,
            onValueChange = { onValueChange(detailBuku.copy(tanggalTerbit = it)) },
            label = { Text("Tanggal Terbit") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        
        // Dropdown Kategori
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedKategori?.nama ?: "",
                onValueChange = {}, // Read only, karena memilih dari list
                readOnly = true,
                label = { Text("Kategori") },
                placeholder = { Text("Pilih Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                enabled = enabled
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (listKategori.isEmpty()) {
                     DropdownMenuItem(
                        text = { Text("Belum ada kategori. Tambah dulu?") },
                        onClick = { expanded = false }
                    )
                } else {
                    listKategori.forEach { kategori ->
                        DropdownMenuItem(
                            text = { Text(text = kategori.nama) },
                            onClick = {
                                onValueChange(detailBuku.copy(idKategori = kategori.idKategori))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}