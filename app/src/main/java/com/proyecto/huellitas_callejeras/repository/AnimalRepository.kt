package com.proyecto.huellitas_callejeras.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

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

    private fun uriToMultipartBody(uri: Uri?): MultipartBody.Part? {
        if (uri == null) return null

        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("imagen", file.name, requestFile)
        } catch (e: Exception) {
            null
        }
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
        rescateRequest: RescateRequest,
        imageUri: Uri? = null
    ): ApiResponse<AnimalRescateResponse> {
        return try {
            val service = getAuthenticatedService()

            if (imageUri != null) {
                val gson = Gson()
                val animalJson = gson.toJson(animalRequest)
                val rescateJson = gson.toJson(rescateRequest)

                val animalBody = animalJson.toRequestBody("text/plain".toMediaTypeOrNull())
                val rescateBody = rescateJson.toRequestBody("text/plain".toMediaTypeOrNull())
                val imagePart = uriToMultipartBody(imageUri)

                service.createAnimalitoConRescateConImagen(animalBody, rescateBody, imagePart)
            } else {
                val request = AnimalRescateRequest(animalRequest, rescateRequest)
                service.createAnimalitoConRescate(request)
            }
        } catch (e: Exception) {
            ApiResponse(success = false, message = "Error: ${e.message}")
        }
    }

    suspend fun updateConRescate(
        animalId: String,
        animalRequest: AnimalRequest,
        rescateRequest: RescateRequest,
        imageUri: Uri? = null
    ): ApiResponse<AnimalRescateResponse> {
        return try {
            val service = getAuthenticatedService()

            if (imageUri != null) {
                val gson = Gson()
                val animalJson = gson.toJson(animalRequest)
                val rescateJson = gson.toJson(rescateRequest)

                val animalBody = animalJson.toRequestBody("text/plain".toMediaTypeOrNull())
                val rescateBody = rescateJson.toRequestBody("text/plain".toMediaTypeOrNull())
                val imagePart = uriToMultipartBody(imageUri)

                service.updateAnimalitoConRescateConImagen(animalId, animalBody, rescateBody, imagePart)
            } else {
                val gson = Gson()
                val animalJson = gson.toJson(animalRequest)
                val rescateJson = gson.toJson(rescateRequest)
                val animalBody = animalJson.toRequestBody("text/plain".toMediaTypeOrNull())
                val rescateBody = rescateJson.toRequestBody("text/plain".toMediaTypeOrNull())
                service.updateAnimalitoConRescate(animalId, animalBody, rescateBody)
            }
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