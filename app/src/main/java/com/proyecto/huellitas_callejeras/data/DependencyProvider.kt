package com.proyecto.huellitas_callejeras.data

import com.proyecto.huellitas_callejeras.remote.RetrofitClient
import com.proyecto.huellitas_callejeras.repository.AnimalRepository
import com.proyecto.huellitas_callejeras.repository.CitasRepository
import com.proyecto.huellitas_callejeras.repository.CitasRepositoryImpl

object DependencyProvider {
    val apiService by lazy { RetrofitClient.api }
    val animalRepository by lazy { AnimalRepository(apiService) }
    val citasRepository: CitasRepository by lazy { CitasRepositoryImpl(apiService) }
}