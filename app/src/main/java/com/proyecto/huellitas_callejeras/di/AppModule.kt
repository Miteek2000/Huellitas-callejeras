package com.proyecto.huellitas_callejeras.di

import com.proyecto.huellitas_callejeras.data.remote.RetrofitClient
import com.proyecto.huellitas_callejeras.remote.RetrofitClient
import com.proyecto.huellitas_callejeras.repository.AnimalRepository
import com.proyecto.huellitas_callejeras.viewmodels.GaleriaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { RetrofitClient.api }

    single { AnimalRepository(get()) }

    viewModel { GaleriaViewModel(get()) }
}