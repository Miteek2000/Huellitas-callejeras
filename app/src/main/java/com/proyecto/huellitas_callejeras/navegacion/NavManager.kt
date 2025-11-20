package com.proyecto.huellitas_callejeras.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.proyecto.huellitas_callejeras.models.Animal
import com.mayte.huellitas_callejeras.screens.CitasMedicasScreen
import com.mayte.huellitas_callejeras.screens.EditarCitaScreen
import com.mayte.huellitas_callejeras.screens.ExpedienteScreen
import com.mayte.huellitas_callejeras.screens.GaleriaScreen
import com.mayte.huellitas_callejeras.screens.InicioSesionScreen
import com.proyecto.huellitas_callejeras.viewmodels.CitasMedicasViewModel
import com.proyecto.huellitas_callejeras.viewmodels.ExpedienteViewModel

@Composable
fun NavManager() {
    val navController = rememberNavController()
    val citasMedicasViewModel: CitasMedicasViewModel = viewModel()
    val expedienteViewModel: ExpedienteViewModel = viewModel()

    NavHost(navController = navController, startDestination = AppScreens.InicioSesion.route) {
        composable(AppScreens.InicioSesion.route) {
            InicioSesionScreen(navController)
        }
        composable(
            route = AppScreens.GaleriaScreen.route + "?from={from}",
            arguments = listOf(navArgument("from") { type = NavType.StringType; nullable = true })
        ) {
            val from = it.arguments?.getString("from")
            GaleriaScreen(navController, from = from)
        }
        composable(
            route = AppScreens.ExpedienteScreen.route + "?patientId={patientId}&editable={editable}",
            arguments = listOf(
                navArgument("patientId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("editable") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val patientId = it.arguments?.getInt("patientId")
            val editable = it.arguments?.getBoolean("editable") ?: false
            val patientIdOrNull = if (patientId == -1) null else patientId
            ExpedienteScreen(navController, expedienteViewModel, patientIdOrNull, editable)
        }
        composable(AppScreens.CitasMedicasScreen.route) {
            val animal = navController.currentBackStackEntry?.savedStateHandle?.get<Animal>("patient")
            LaunchedEffect(animal) {
                animal?.let {
                    citasMedicasViewModel.onPatientSelected(it)
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Animal>("patient")
                }
            }
            CitasMedicasScreen(navController, citasMedicasViewModel)
        }
        composable(AppScreens.EditarCitaScreen.route) {
            EditarCitaScreen(navController, citasMedicasViewModel)
        }
    }
}
