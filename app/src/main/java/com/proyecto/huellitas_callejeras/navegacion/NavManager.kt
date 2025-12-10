package com.proyecto.huellitas_callejeras.navegacion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mayte.huellitas_callejeras.screens.InicioSesionScreen
import com.proyecto.huellitas_callejeras.screens.*
import com.proyecto.huellitas_callejeras.viewmodels.CitasMedicasViewModel
import com.proyecto.huellitas_callejeras.viewmodels.ExpedienteViewModel
import com.proyecto.huellitas_callejeras.viewmodels.GaleriaViewModel
import com.proyecto.huellitas_callejeras.viewmodel.TratamientoViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.InicioSesion.route) {

        composable(AppScreens.InicioSesion.route) {
            InicioSesionScreen(navController)
        }

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

        composable(AppScreens.CitasMedicasScreen.route) {
            val citasMedicasViewModel: CitasMedicasViewModel = viewModel()
            CitasMedicasScreen(navController, citasMedicasViewModel)
        }

        composable(
            AppScreens.EditarCitaScreen.route + "?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val citasMedicasViewModel: CitasMedicasViewModel = viewModel()

            LaunchedEffect(id) {
                if (id.isNotEmpty()) {
                    citasMedicasViewModel.getCitaPorId(id) { cita ->
                        cita?.let {
                            citasMedicasViewModel.setEditingCita(it)
                        }
                    }
                } else {
                    citasMedicasViewModel.setEditingCita(citasMedicasViewModel.nuevaCitaVacia())
                }
            }

            EditarCitaScreen(navController, citasMedicasViewModel, id)
        }

        composable(
            route = AppScreens.TratamientoScreen.route + "/{animalId}",
            arguments = listOf(
                navArgument("animalId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: ""

            val tratamientoViewModel: TratamientoViewModel = viewModel()

            TratamientoScreen(
                viewModel = tratamientoViewModel,
                animalId = animalId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToMedicamento = { medicamentoId ->

                    navController.navigate(
                        AppScreens.MedicamentoFormScreen.route + "/$animalId/$medicamentoId"
                    )
                }
            )
        }

        composable(
            route = AppScreens.MedicamentoFormScreen.route + "/{animalId}/{medicamentoId}",
            arguments = listOf(
                navArgument("animalId") {
                    type = NavType.StringType
                },
                navArgument("medicamentoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: ""
            val medicamentoId = backStackEntry.arguments?.getInt("medicamentoId") ?: -1

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(
                    "${AppScreens.TratamientoScreen.route}/$animalId"
                )
            }
            val tratamientoViewModel: TratamientoViewModel = viewModel(parentEntry)

            MedicamentoFormScreen(
                viewModel = tratamientoViewModel,
                medicamentoId = medicamentoId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}