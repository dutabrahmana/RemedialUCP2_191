package com.example.roomdatabase.repositori

import android.app.Application
import android.content.Context
import com.example.roomdatabase.room.DatabasePerpustakaan

interface ContainerApp {
    val repositoriPerpustakaan: RepositoriPerpustakaan
}

class ContainerDataApp(private val context: Context) : ContainerApp {
    override val repositoriPerpustakaan: RepositoriPerpustakaan by lazy {
        val db = DatabasePerpustakaan.getDatabase(context)
        OfflineRepositoriPerpustakaan(
            db,
            db.kategoriDao(),
            db.bukuDao(),
            db.penulisDao(),
            db.bukuFisikDao(),
            db.auditLogDao()
        )
    }
}

class AplikasiSiswa : Application() {
    lateinit var container: ContainerApp

    override fun onCreate() {
        super.onCreate()
        container = ContainerDataApp(this)
    }
}