package com.proyecto.huellitas_callejeras.data.dao

import androidx.room.*
import com.proyecto.huellitas_callejeras.data.model.Tratamiento
import com.proyecto.huellitas_callejeras.data.model.TratamientoConMedicamentos
import kotlinx.coroutines.flow.Flow

@Dao
interface TratamientoDao {
    @Transaction
    @Query("SELECT * FROM tratamientos ORDER BY id ASC")
    fun obtenerTratamientosConMedicamentos(): Flow<List<TratamientoConMedicamentos>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTratamiento(tratamiento: Tratamiento): Long

    @Update
    suspend fun actualizarTratamiento(tratamiento: Tratamiento)

    @Delete
    suspend fun eliminarTratamiento(tratamiento: Tratamiento)
}