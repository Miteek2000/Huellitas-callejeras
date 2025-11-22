package com.proyecto.huellitas_callejeras

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.proyecto.huellitas_callejeras.data.database.AppDatabase
import com.proyecto.huellitas_callejeras.data.repository.TratamientoRepository
import com.proyecto.huellitas_callejeras.ui.screens.MedicamentoFormScreen
import com.proyecto.huellitas_callejeras.ui.screens.TratamientoScreen
import com.proyecto.huellitas_callejeras.ui.viewmodel.MedicamentoViewModel
import com.proyecto.huellitas_callejeras.ui.viewmodel.MedicamentoViewModelFactory
import com.proyecto.huellitas_callejeras.ui.viewmodel.TratamientoViewModel
import com.proyecto.huellitas_callejeras.ui.viewmodel.TratamientoViewModelFactory

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    database: AppDatabase = AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
) {
    val repository = TratamientoRepository(
        tratamientoDao = database.tratamientoDao(),
        medicamentoDao = database.medicamentoDao()
    )

    NavHost(
        navController = navController,
        startDestination = "tratamiento"
    ) {
        composable("tratamiento") {
            val tratamientoViewModel: TratamientoViewModel = viewModel(
                factory = TratamientoViewModelFactory(repository)
            )
            TratamientoScreen(
                viewModel = tratamientoViewModel,
                onNavigateToMedicamento = { tratamientoId, medicamentoId ->
                    navController.navigate("medicamento/$tratamientoId/$medicamentoId")
                }
            )
        }

        composable(
            route = "medicamento/{tratamientoId}/{medicamentoId}",
            arguments = listOf(
                navArgument("tratamientoId") { type = NavType.IntType },
                navArgument("medicamentoId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val tratamientoId = backStackEntry.arguments?.getInt("tratamientoId") ?: 0
            val medicamentoId = backStackEntry.arguments?.getInt("medicamentoId") ?: -1

            val medicamentoViewModel: MedicamentoViewModel = viewModel(
                factory = MedicamentoViewModelFactory(repository)
            )

            if (medicamentoId > 0) {
                medicamentoViewModel.cargarMedicamento(medicamentoId)
            }

            MedicamentoFormScreen(
                tratamientoId = tratamientoId,
                viewModel = medicamentoViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
