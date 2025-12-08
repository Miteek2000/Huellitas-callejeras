package com.proyecto.huellitas_callejeras.data.repository

import com.proyecto.huellitas_callejeras.data.dao.MedicamentoDao
import com.proyecto.huellitas_callejeras.data.model.Medicamento
import com.proyecto.huellitas_callejeras.data.remote.ApiService
import com.proyecto.huellitas_callejeras.data.remote.dto.MedicamentoDto
import com.proyecto.huellitas_callejeras.data.remote.dto.MedicamentoRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class MedicamentoRepository(
    private val apiService: ApiService,
    private val medicamentoDao: MedicamentoDao
) {


    suspend fun getAllMedicamentosFromApi(): Result<List<MedicamentoDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllMedicamentos()
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

    suspend fun createMedicamento(request: MedicamentoRequestDto): Result<MedicamentoDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createMedicamento(request)
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

    suspend fun updateMedicamento(id: String, request: MedicamentoRequestDto): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateMedicamento(id, request)
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

    suspend fun deleteMedicamento(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteMedicamento(id)
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



    suspend fun obtenerMedicamentoPorId(id: Int): Medicamento? {
        return medicamentoDao.obtenerMedicamentoPorId(id)
    }

    suspend fun insertarMedicamentoLocal(medicamento: Medicamento) {
        medicamentoDao.insertarMedicamento(medicamento)
    }

    suspend fun actualizarMedicamentoLocal(medicamento: Medicamento) {
        medicamentoDao.actualizarMedicamento(medicamento)
    }

    suspend fun eliminarMedicamentoLocal(medicamento: Medicamento){
        medicamentoDao.eliminarMedicamento(medicamento)
    }
}
