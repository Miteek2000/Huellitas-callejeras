package com.proyecto.huellitas_callejeras.repository

import android.content.Context
import com.proyecto.huellitas_callejeras.remote.ApiService
import com.proyecto.huellitas_callejeras.remote.AuthService
import com.proyecto.huellitas_callejeras.remote.RetrofitClient
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoDTO
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class MedicamentoRepository(
    private val context: Context
) {
    private val authService = AuthService(context)

    private suspend fun getAuthenticatedService(): ApiService {
        val token = authService.getToken()
            ?: throw Exception("No hay sesión activa. Por favor inicia sesión.")
        return RetrofitClient.createAuthenticatedService(token)
    }

    suspend fun getAllMedicamentosFromApi(): Result<List<MedicamentoDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val response = service.getAllMedicamentos()
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

    suspend fun createMedicamento(request: MedicamentoRequest): Result<MedicamentoDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val response = service.createMedicamento(request)
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    response.body()!!.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("La API no devolvió datos."))
                } else {
                    val errorMsg = response.body()?.message ?: "Error de red: ${response.code()}"
                    Result.failure(IOException(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
/*
    suspend fun updateMedicamento(id: String, request: MedicamentoRequest): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val response = service.updateTratamiento(id, request)
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
*/
    suspend fun deleteMedicamento(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getAuthenticatedService()
                val response = service.deleteMedicamento(id)
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
}