package com.example.huellitas_callejeras.data.dao

import androidx.room.*
import com.example.huellitas_callejeras.data.model.Medicamento

@Dao
interface MedicamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMedicamento(medicamento: Medicamento): Long

    @Update
    suspend fun actualizarMedicamento(medicamento: Medicamento)

    @Delete
    suspend fun eliminarMedicamento(medicamento: Medicamento)

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun obtenerMedicamentoPorId(id: Int): Medicamento?
}