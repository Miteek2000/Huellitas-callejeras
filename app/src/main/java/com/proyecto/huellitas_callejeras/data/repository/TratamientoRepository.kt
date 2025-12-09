package com.proyecto.huellitas_callejeras.data.repository

import com.proyecto.huellitas_callejeras.data.remote.ApiService
import com.proyecto.huellitas_callejeras.data.remote.dto.TratamientoCreateRequestDto
import com.proyecto.huellitas_callejeras.data.remote.dto.TratamientoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class TratamientoRepository(
    private val apiService: ApiService
) {

    suspend fun createTratamiento(tratamientoRequest: TratamientoCreateRequestDto): Result<TratamientoDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createTratamiento(tratamientoRequest)

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

    suspend fun updateTratamiento(tratamientoId: String, tratamientoRequest: TratamientoCreateRequestDto): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateTratamiento(tratamientoId, tratamientoRequest)

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
