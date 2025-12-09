package com.proyecto.huellitas_callejeras.repository

import com.proyecto.huellitas_callejeras.models.Tratamiento
import com.proyecto.huellitas_callejeras.models.TratamientoConMedicamentos
import com.proyecto.huellitas_callejeras.remote.ApiService
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoCreateRequestDto
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

class TratamientoRepository(
    private val apiService: ApiService,
    private val tratamientoDao: TratamientoDao,
    private val medicamentoDao: MedicamentoDao
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


    fun obtenerTodosLosTratamientosConMedicamentos(): Flow<List<TratamientoConMedicamentos>> {
        return tratamientoDao.obtenerTratamientosConMedicamentos()
    }

    suspend fun insertarTratamiento(tratamiento: Tratamiento) {
        tratamientoDao.insertarTratamiento(tratamiento)
    }

    suspend fun actualizarTratamiento(tratamiento: Tratamiento) {
        tratamientoDao.actualizarTratamiento(tratamiento)
    }

    suspend fun eliminarTratamientoYMedicamentos(tratamientoId: Int) {
        val tratamiento = Tratamiento(id = tratamientoId, nombre = "", fechaInicio = "", fechaConclusion = "")
        tratamientoDao.eliminarTratamiento(tratamiento)
    }

    suspend fun obtenerMedicamentoPorId(id: Int): Medicamento? {
        return medicamentoDao.obtenerMedicamentoPorId(id)
    }

    suspend fun insertarMedicamento(medicamento: Medicamento) {
        medicamentoDao.insertarMedicamento(medicamento)
    }

    suspend fun actualizarMedicamento(medicamento: Medicamento) {
        medicamentoDao.actualizarMedicamento(medicamento)
    }
}
