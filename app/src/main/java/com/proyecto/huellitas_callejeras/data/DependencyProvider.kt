package com.proyecto.huellitas_callejeras.data

import android.content.Context
import com.proyecto.huellitas_callejeras.remote.AuthService
import com.proyecto.huellitas_callejeras.remote.RetrofitClient
import com.proyecto.huellitas_callejeras.repository.AnimalRepository
import com.proyecto.huellitas_callejeras.repository.CitasRepository
import com.proyecto.huellitas_callejeras.repository.CitasRepositoryImpl

class DependencyProvider private constructor(private val context: Context) {

    val apiService by lazy { RetrofitClient.apiService }

    val authService by lazy { AuthService(context) }

    val animalRepository by lazy {
        AnimalRepository(apiService, context)
    }

    val citasRepository: CitasRepository by lazy {
        CitasRepositoryImpl(apiService, context)
    }

    companion object {
        @Volatile
        private var instance: DependencyProvider? = null


        fun getInstance(context: Context): DependencyProvider {
            return instance ?: synchronized(this) {
                instance ?: DependencyProvider(context.applicationContext).also {
                    instance = it
                }
            }
        }

        fun initialize(context: Context) {
            getInstance(context)
        }

        // Propiedades estáticas para acceso rápido desde ViewModels
        // Deben inicializarse llamando a initialize() primero

        lateinit var animalRepository: AnimalRepository
            private set

        lateinit var citasRepository: CitasRepository
            private set

        lateinit var authService: AuthService
            private set

        fun setup(context: Context) {
            val provider = getInstance(context)
            animalRepository = provider.animalRepository
            citasRepository = provider.citasRepository
            authService = provider.authService
        }
    }
}