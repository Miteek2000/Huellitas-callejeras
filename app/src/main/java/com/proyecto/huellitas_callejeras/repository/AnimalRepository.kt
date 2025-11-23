package com.proyecto.huellitas_callejeras.repository

import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.remote.ApiService
import com.proyecto.huellitas_callejeras.remote.dto.AnimalDto
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRequest
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRescateRequest
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRescateResponse
import com.proyecto.huellitas_callejeras.remote.dto.ApiResponse
import com.proyecto.huellitas_callejeras.remote.dto.RescateRequest
import com.proyecto.huellitas_callejeras.remote.dto.toDomain


class AnimalRepository(
    private val api: ApiService
) {
    suspend fun testConnection(): String {
        return try {
            val response = api.getAnimalitos()

            val lista = response.data ?: emptyList()
            "Conexión exitosa - Se recibieron ${lista.size} animales"
        } catch (e: Exception) {
            " Error: ${e.message}"
        }
    }
    suspend fun getAll(): List<Animal> {
        println("Llamando endpoint GET /animalitos")

        val response = api.getAnimalitos()
        return response.data?.map { it.toDomain() } ?: emptyList()
    }

    suspend fun getById(id: String): AnimalDto =
        api.getAnimalito(id)

    suspend fun create(body: AnimalRequest): AnimalDto =
        api.createAnimalito(body)

    suspend fun update(id: String, body: AnimalRequest): AnimalDto =
        api.updateAnimalito(id, body)

    suspend fun delete(id: String) =
        api.deleteAnimalito(id)

    suspend fun createConRescate(animalRequest: AnimalRequest, rescateRequest: RescateRequest): ApiResponse<AnimalRescateResponse> {
        return try {
            println("🐛 DEBUG -> Repository.createConRescate INICIADO")
            println("🐛 DEBUG -> AnimalRequest: $animalRequest")
            println("🐛 DEBUG -> RescateRequest: $rescateRequest")

            val request = AnimalRescateRequest(animalRequest, rescateRequest)
            println("🐛 DEBUG -> AnimalRescateRequest: $request")

            println("🐛 DEBUG -> Llamando a api.createAnimalitoConRescate...")
            val response = api.createAnimalitoConRescate(request)
            println("🐛 DEBUG -> API Response recibida: $response")
            println("🐛 DEBUG -> Response success: ${response.success}")
            println("🐛 DEBUG -> Response message: ${response.message}")
            println("🐛 DEBUG -> Response data: ${response.data}")

            response
        } catch (e: Exception) {
            println("🐛 DEBUG -> 💥 EXCEPCIÓN en createConRescate: ${e.message}")
            println("🐛 DEBUG -> Stack trace: ${e.stackTraceToString()}")
            ApiResponse(success = false, message = "Error: ${e.message}")
        }
    }

    suspend fun updateConRescate(animalId: String, animalRequest: AnimalRequest, rescateRequest: RescateRequest): ApiResponse<AnimalRescateResponse> {
        return try {
            val request = AnimalRescateRequest(animalRequest, rescateRequest)
            api.updateAnimalitoConRescate(animalId, request)
        } catch (e: Exception) {
            ApiResponse(success = false, message = "Error: ${e.message}")
        }
    }

    suspend fun getAnimalitoConRescate(animalId: String): ApiResponse<AnimalRescateResponse> {
        return try {
            println("DEBUG -> API GET animalitoConRescate: id=$animalId")
            val result =  api.getAnimalitoConRescate(animalId)
            println("DEBUG -> API RESULT: $result")
            return result
        } catch (e: Exception) {
            ApiResponse(success = false, message = "Error: ${e.message}")
        }
    }
}


