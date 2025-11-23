// app/src/main/java/com/proyecto/huellitas_callejeras/navegacion/NavManager.kt
package com.proyecto.huellitas_callejeras.navegacion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.proyecto.huellitas_callejeras.screens.EditarCitaScreen
import com.proyecto.huellitas_callejeras.screens.CitasMedicasScreen
import com.proyecto.huellitas_callejeras.screens.ExpedienteScreen
import com.proyecto.huellitas_callejeras.screens.GaleriaScreen
import com.proyecto.huellitas_callejeras.viewmodels.CitasMedicasViewModel
import com.proyecto.huellitas_callejeras.viewmodels.ExpedienteViewModel
import com.proyecto.huellitas_callejeras.viewmodels.GaleriaViewModel
import androidx.navigation.NavType
import com.mayte.huellitas_callejeras.screens.InicioSesionScreen

@Composable
fun NavManager() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.InicioSesion.route) {

        // Pantalla de Inicio de Sesión
        composable(AppScreens.InicioSesion.route) {
            InicioSesionScreen(navController)
        }

        // Pantalla de Galería
        composable(
            route = AppScreens.GaleriaScreen.route + "?from={from}",
            arguments = listOf(navArgument("from") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from")
            val galeriaViewModel: GaleriaViewModel = viewModel()
            GaleriaScreen(navController, galeriaViewModel, from = from)
        }

        // Pantalla de Expediente
        composable(
            route = AppScreens.ExpedienteScreen.route + "?patientId={patientId}&editable={editable}",
            arguments = listOf(
                navArgument("patientId") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },
                navArgument("editable") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            val editable = backStackEntry.arguments?.getBoolean("editable") ?: false
            val expedienteViewModel: ExpedienteViewModel = viewModel()
            ExpedienteScreen(navController, expedienteViewModel, patientId, editable)
        }

        // Pantalla de Citas Médicas
        composable(AppScreens.CitasMedicasScreen.route) {
            val citasMedicasViewModel: CitasMedicasViewModel = viewModel()
            CitasMedicasScreen(navController, citasMedicasViewModel)
        }

        // Pantalla de Editar Cita
        composable(AppScreens.EditarCitaScreen.route) {
            val citasMedicasViewModel: CitasMedicasViewModel = viewModel()
            EditarCitaScreen(navController, citasMedicasViewModel)
        }
    }
}