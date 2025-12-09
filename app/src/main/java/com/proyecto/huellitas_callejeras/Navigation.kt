package com.proyecto.huellitas_callejeras

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.proyecto.huellitas_callejeras.data.remote.ApiClient
import com.proyecto.huellitas_callejeras.data.remote.dto.NuevoMedicamentoResult
import com.proyecto.huellitas_callejeras.data.repository.MedicamentoRepository
import com.proyecto.huellitas_callejeras.data.repository.TratamientoRepository
import com.proyecto.huellitas_callejeras.ui.screens.MedicamentoFormScreen
import com.proyecto.huellitas_callejeras.ui.screens.TratamientoScreen
import com.proyecto.huellitas_callejeras.ui.viewmodel.MedicamentoViewModel
import com.proyecto.huellitas_callejeras.ui.viewmodel.MedicamentoViewModelFactory
import com.proyecto.huellitas_callejeras.ui.viewmodel.TratamientoViewModel
import com.proyecto.huellitas_callejeras.ui.viewmodel.TratamientoViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
) {
    val apiService = ApiClient.instance

    val tratamientoRepository = TratamientoRepository(apiService = apiService)
    val medicamentoRepository = MedicamentoRepository(apiService = apiService)

    NavHost(
        navController = navController,
        startDestination = "tratamiento/{animalId}"
    ) {
        composable(
            route = "tratamiento/{animalId}",
            arguments = listOf(navArgument("animalId") {
                type = NavType.StringType
                defaultValue = "5265a1b4-ca82-4c8c-b2aa-9fd968b49b50" // ID de ejemplo
            })
        ) {
            backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: ""
            val tratamientoViewModel: TratamientoViewModel = viewModel(
                factory = TratamientoViewModelFactory(tratamientoRepository)
            )

            val newMedicamentoResult = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<NuevoMedicamentoResult>("nuevo_medicamento_result")?.observeAsState()

            LaunchedEffect(newMedicamentoResult?.value) {
                newMedicamentoResult?.value?.let {
                    tratamientoViewModel.agregarMedicamentoLocal(it)
                    navController.currentBackStackEntry?.savedStateHandle?.remove<NuevoMedicamentoResult>("nuevo_medicamento_result")
                }
            }

            TratamientoScreen(
                animalId = animalId,
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
                factory = MedicamentoViewModelFactory(medicamentoRepository)
            )

            medicamentoViewModel.cargarMedicamento(tratamientoId, medicamentoId)

            MedicamentoFormScreen(
                viewModel = medicamentoViewModel,
                onNavigateBack = { medicamentoResult ->
                    medicamentoResult?.let {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("nuevo_medicamento_result", it)
                    }
                    navController.popBackStack()
                }
            )
        }
    }
}
