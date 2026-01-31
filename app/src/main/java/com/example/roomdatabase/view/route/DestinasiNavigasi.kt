package com.example.roomdatabase.view.route

interface DestinasiNavigasi {
    val route: String
    val titleRes: String
}

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Home"
}

object DestinasiEntry : DestinasiNavigasi {
    override val route = "item_entry"
    override val titleRes = "Entry Buku"
}

object DestinasiEntryKategori : DestinasiNavigasi {
    override val route = "item_entry_kategori"
    override val titleRes = "Entry Kategori"
}

object DestinasiDetail : DestinasiNavigasi {
    override val route = "item_details"
    override val titleRes = "Detail Buku"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

object DestinasiEdit : DestinasiNavigasi {
    override val route = "item_edit"
    override val titleRes = "Edit Buku"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}