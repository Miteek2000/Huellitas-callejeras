package com.proyecto.huellitas_callejeras.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.proyecto.huellitas_callejeras.remote.ApiService
import com.proyecto.huellitas_callejeras.remote.AuthService
import com.proyecto.huellitas_callejeras.remote.RetrofitClient
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoTratamientoDto
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoCreateRequestDto
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoDto
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoResumenDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TratamientoRepository(
    private val context: Context
) {
    private val authService = AuthService(context)

    private suspend fun getAuthenticatedService(): ApiService {
        val token = authService.getToken()
            ?: throw Exception("No hay sesión activa. Por favor inicia sesión.")
        return RetrofitClient.createAuthenticatedService(token)
    }

    private fun uriToMultipartBody(uri: Uri?, partName: String = "archivo"): MultipartBody.Part? {
        if (uri == null) return null

        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_receta_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("archivo", file.name, requestFile)
        } catch (e: Exception) {
            Log.e("TratamientoRepository", "Error convirtiendo URI a MultipartBody", e)
            null
        }
    }

    suspend fun createTratamiento(
        tratamientoRequest: TratamientoCreateRequestDto,
        recetaUri: Uri? = null
    ): Result<TratamientoDto> {
        return withContext(Dispatchers.IO) {
            try {

                val service = getAuthenticatedService()


                val gson = Gson()
                val tratamientoJson = gson.toJson(tratamientoRequest)

                Log.d("TratamientoRepo", "JSON a enviar: $tratamientoJson")

                val tratamientoBody = tratamientoJson.toRequestBody("application/json".toMediaTypeOrNull())
                val recetaPart = uriToMultipartBody(recetaUri, "archivo")


                val response = service.createTratamiento(tratamientoBody, recetaPart)


                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    Log.d("TratamientoRepo", "Success flag: ${apiResponse.success}")
                    Log.d("TratamientoRepo", "Message: ${apiResponse.message}")

                    if (apiResponse.success) {
                        apiResponse.data?.let { tratamientoDto ->
                            Log.d("TratamientoRepo", "✓ Tratamiento creado - ID: ${tratamientoDto.id}")
                            Result.success(tratamientoDto)
                        } ?: Result.failure(Exception("La API no devolvió datos del tratamiento"))
                    } else {
                        Log.e("TratamientoRepo", "API retornó success=false: ${apiResponse.message}")
                        Result.failure(IOException(apiResponse.message))

                    }
                } else {

                    val errorMsg = response.body()?.message ?: "Error de red: ${response.code()}"
                    val errorBody = response.errorBody()?.string()
                    Log.e("TratamientoRepo", "ERROR ${response.code()} BODY:")
                    Log.e("TratamientoRepo", errorBody ?: "No body")
                    Result.failure(IOException(errorMsg))
                }
            } catch (e: Exception) {
                Log.e("TratamientoRepo", "Excepción al crear tratamiento", e)
                Result.failure(e)
            }
        }
    }

    suspend fun updateTratamiento(
        tratamientoId: String,
        tratamientoRequest: TratamientoCreateRequestDto,
        recetaUri: Uri? = null
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val gson = Gson()
                val tratamientoJson = gson.toJson(tratamientoRequest)
                val tratamientoBody = tratamientoJson.toRequestBody("application/json".toMediaTypeOrNull())
                val recetaPart = uriToMultipartBody(recetaUri, "archivo")

                val response = service.updateTratamiento(tratamientoId, tratamientoBody, recetaPart)

                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    Result.success(Unit)
                } else {
                    val errorMsg = response.body()?.message ?: "Error de red: ${response.code()}"
                    Result.failure(IOException(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getAllTratamientosFromApi(): Result<List<TratamientoDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val response = service.getAllTratamientos()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.success) {
                        Result.success(response.body()!!.data ?: emptyList())
                    } else {
                        Result.failure(Exception(response.body()!!.message))
                    }
                } else {
                    Result.failure(IOException("Error de red: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getTratamientoById(id: String): Result<TratamientoDto> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val response = service.getTratamiento(id)
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    response.body()!!.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("La API no devolvió datos"))
                } else {
                    val errorMsg = response.body()?.message ?: "Error de red: ${response.code()}"
                    Result.failure(IOException(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteTratamiento(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val response = service.deleteTratamiento(id)
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    Result.success(Unit)
                } else {
                    val errorMsg = response.body()?.message ?: "Error de red: ${response.code()}"
                    Result.failure(IOException(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getTratamientoByAnimalId(animalId: String): Result<TratamientoResumenDto> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val response = service.getTratamientoByAnimalId(animalId)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success) {
                        val tratamientos = apiResponse.data ?: emptyList()
                        if (tratamientos.isNotEmpty()) {
                            val tratamiento = tratamientos.first()
                            val tratamientoUrlCompleta = if (tratamiento.recetaUrl?.startsWith("/") == true) {
                                tratamiento.copy(recetaUrl = "http://34.195.100.95:8080${tratamiento.recetaUrl}")}else{
                                    tratamiento
                                }
                            Result.success(tratamientoUrlCompleta)

                        } else {
                            Result.failure(Exception("No se encontraron tratamientos para este animal"))
                        }
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                    Result.failure(IOException(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getMedicamentosByTratamientoId(tratamientoId: String): Result<List<MedicamentoTratamientoDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val response = service.getMedicamentosByTratamientoId(tratamientoId)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success) {
                        Result.success(apiResponse.data ?: emptyList())
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                    Result.failure(IOException(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}