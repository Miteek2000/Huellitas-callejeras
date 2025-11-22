package com.proyecto.huellitas_callejeras.data.repository

import com.proyecto.huellitas_callejeras.data.dao.MedicamentoDao
import com.proyecto.huellitas_callejeras.data.dao.TratamientoDao
import com.proyecto.huellitas_callejeras.data.model.Medicamento
import com.proyecto.huellitas_callejeras.data.model.Tratamiento
import com.proyecto.huellitas_callejeras.data.model.TratamientoConMedicamentos
import kotlinx.coroutines.flow.Flow

class TratamientoRepository(
    private val tratamientoDao: TratamientoDao,
    private val medicamentoDao: MedicamentoDao
) {
    fun obtenerTodosConMedicamentos(): Flow<List<TratamientoConMedicamentos>> {
        return tratamientoDao.obtenerTratamientosConMedicamentos()
    }

    suspend fun insertarTratamiento(tratamiento: Tratamiento): Long {
        return tratamientoDao.insertarTratamiento(tratamiento)
    }

    suspend fun actualizarTratamiento(tratamiento: Tratamiento) {
        tratamientoDao.actualizarTratamiento(tratamiento)
    }

    suspend fun eliminarTratamiento(tratamiento: Tratamiento) {
        tratamientoDao.eliminarTratamiento(tratamiento)
    }

    suspend fun insertarMedicamento(medicamento: Medicamento) {
        medicamentoDao.insertarMedicamento(medicamento)
    }

    suspend fun actualizarMedicamento(medicamento: Medicamento) {
        medicamentoDao.actualizarMedicamento(medicamento)
    }

    suspend fun obtenerMedicamentoPorId(id: Int): Medicamento? {
        return medicamentoDao.obtenerMedicamentoPorId(id)
    }
}