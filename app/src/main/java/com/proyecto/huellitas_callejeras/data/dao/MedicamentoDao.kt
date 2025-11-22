package com.proyecto.huellitas_callejeras.data.dao

import androidx.room.*
import com.proyecto.huellitas_callejeras.data.model.Medicamento

@Dao
interface MedicamentoDao {
    @Insert
    suspend fun insertarMedicamento(medicamento: Medicamento)

    @Update
    suspend fun actualizarMedicamento(medicamento: Medicamento)

    @Delete
    suspend fun eliminarMedicamento(medicamento: Medicamento)

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun obtenerMedicamentoPorId(id: Int): Medicamento?

    @Query("SELECT * FROM medicamentos WHERE tratamientoId = :tratamientoId")
    suspend fun obtenerMedicamentosPorTratamiento(tratamientoId: Int): List<Medicamento>
}