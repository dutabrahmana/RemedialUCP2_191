package com.example.roomdatabase.view.uicontroller

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.roomdatabase.view.HalamanDetail
import com.example.roomdatabase.view.HalamanEdit
import com.example.roomdatabase.view.HalamanEntry
import com.example.roomdatabase.view.HalamanEntryKategori
import com.example.roomdatabase.view.HalamanHome
import com.example.roomdatabase.view.route.DestinasiDetail
import com.example.roomdatabase.view.route.DestinasiEdit
import com.example.roomdatabase.view.route.DestinasiEntry
import com.example.roomdatabase.view.route.DestinasiEntryKategori
import com.example.roomdatabase.view.route.DestinasiHome

@Composable
fun SiswaApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    PengelolaHalaman(navController = navController, modifier = modifier)
}

@Composable
fun PengelolaHalaman(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = modifier
    ) {
        composable(DestinasiHome.route) {
            HalamanHome(
                navigateToItemEntry = { navController.navigate(DestinasiEntry.route) },
                navigateToKategoriEntry = { navController.navigate(DestinasiEntryKategori.route) },
                onDetailClick = { itemId ->
                    navController.navigate("${DestinasiDetail.route}/$itemId")
                }
            )
        }
        composable(DestinasiEntry.route) {
            HalamanEntry(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(DestinasiEntryKategori.route) {
            HalamanEntryKategori(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = DestinasiDetail.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetail.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            HalamanDetail(
                navigateToEditItem = { itemId ->
                    navController.navigate("${DestinasiEdit.route}/$itemId")
                },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = DestinasiEdit.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEdit.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            HalamanEdit(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}