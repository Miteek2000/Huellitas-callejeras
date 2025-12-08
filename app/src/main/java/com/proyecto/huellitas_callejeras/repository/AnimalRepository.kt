package com.proyecto.huellitas_callejeras.repository

import android.content.Context
import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.remote.ApiService
import com.proyecto.huellitas_callejeras.remote.AuthService
import com.proyecto.huellitas_callejeras.remote.RetrofitClient
import com.proyecto.huellitas_callejeras.remote.dto.AnimalDto
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRequest
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRescateRequest
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRescateResponse
import com.proyecto.huellitas_callejeras.remote.dto.ApiResponse
import com.proyecto.huellitas_callejeras.remote.dto.RescateRequest
import com.proyecto.huellitas_callejeras.remote.dto.toDomain

class AnimalRepository(
    private val api: ApiService,
    private val context: Context
) {
    private val authService = AuthService(context)

    private suspend fun getAuthenticatedService(): ApiService {
        val token = authService.getToken()
            ?: throw Exception("No hay sesión activa. Por favor inicia sesión.")
        return RetrofitClient.createAuthenticatedService(token)
    }

    suspend fun testConnection(): String {
        return try {
            val service = getAuthenticatedService()
            val response = service.getAnimalitos()
            val lista = response.data
            "Conexión exitosa - Se recibieron ${lista?.size} animales"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    suspend fun getAll(): List<Animal> {
        return try {
            val service = getAuthenticatedService()
            val response = service.getAnimalitos()
            response.data?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            throw Exception("Error al cargar los animales: ${e.message}")
        }
    }

    suspend fun getById(id: String): AnimalDto {
        val service = getAuthenticatedService()
        return service.getAnimalito(id)
    }

    suspend fun create(body: AnimalRequest): AnimalDto {
        val service = getAuthenticatedService()
        return service.createAnimalito(body)
    }

    suspend fun update(id: String, body: AnimalRequest): AnimalDto {
        val service = getAuthenticatedService()
        return service.updateAnimalito(id, body)
    }

    suspend fun delete(id: String) {
        try {
            val service = getAuthenticatedService()
            service.deleteAnimalito(id)
        } catch (e: Exception) {
            throw Exception("Error al eliminar el animal: ${e.message}")
        }
    }

    suspend fun createConRescate(
        animalRequest: AnimalRequest,
        rescateRequest: RescateRequest
    ): ApiResponse<AnimalRescateResponse> {
        return try {
            val service = getAuthenticatedService()
            val request = AnimalRescateRequest(animalRequest, rescateRequest)
            service.createAnimalitoConRescate(request)
        } catch (e: Exception) {
            ApiResponse(success = false, message = "Error: ${e.message}")
        }
    }

    suspend fun updateConRescate(
        animalId: String,
        animalRequest: AnimalRequest,
        rescateRequest: RescateRequest
    ): ApiResponse<AnimalRescateResponse> {
        return try {
            val service = getAuthenticatedService()
            val request = AnimalRescateRequest(animalRequest, rescateRequest)
            service.updateAnimalitoConRescate(animalId, request)
        } catch (e: Exception) {
            ApiResponse(success = false, message = "Error: ${e.message}")
        }
    }

    suspend fun getAnimalitoConRescate(animalId: String): ApiResponse<AnimalRescateResponse> {
        return try {
            val service = getAuthenticatedService()
            service.getAnimalitoConRescate(animalId)
        } catch (e: Exception) {
            ApiResponse(success = false, message = "Error: ${e.message}")
        }
    }
}