package com.example.huellitas_callejeras.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TratamientoConMedicamentos(
    @Embedded val tratamiento: Tratamiento,
    @Relation(
        parentColumn = "id",
        entityColumn = "tratamientoId"
    )
    val medicamentos: List<Medicamento>
)