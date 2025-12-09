package com.proyecto.huellitas_callejeras.navegation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.proyecto.huellitas_callejeras.screens.MedicamentoFormScreen
import com.proyecto.huellitas_callejeras.screens.TratamientoScreen
import com.proyecto.huellitas_callejeras.viewmodel.MedicamentoViewModel
import com.proyecto.huellitas_callejeras.viewmodel.TratamientoViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        navController = navController,
        startDestination = "tratamiento"
    ) {
        composable("tratamiento") {
            val tratamientoViewModel: TratamientoViewModel = viewModel(
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

            )

            medicamentoViewModel.cargarMedicamento(tratamientoId, medicamentoId)

            MedicamentoFormScreen(
                viewModel = medicamentoViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}