package com.example.huellitas_callejeras

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.huellitas_callejeras.data.database.AppDatabase
import com.example.huellitas_callejeras.data.repository.TratamientoRepository
import com.example.huellitas_callejeras.ui.screens.MedicamentoFormScreen
import com.example.huellitas_callejeras.ui.viewmodel.MedicamentoViewModel
import com.example.huellitas_callejeras.ui.viewmodel.MedicamentoViewModelFactory

@Composable
fun NavigationGraph(
    database: AppDatabase = AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
) {
    val navController = rememberNavController()
    val repository = TratamientoRepository(
        tratamientoDao = database.tratamientoDao(),
        medicamentoDao = database.medicamentoDao()
    )

    NavHost(navController = navController, startDestination = "medicamento") {
        composable("medicamento") {
            val viewModel: MedicamentoViewModel = viewModel(
                factory = MedicamentoViewModelFactory(repository)
            )
            MedicamentoFormScreen(
                tratamientoId = 1,
                viewModel = viewModel,
                onNavigateBack = { }
            )
        }
    }
}