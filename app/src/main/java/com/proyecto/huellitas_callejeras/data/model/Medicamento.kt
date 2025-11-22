package com.proyecto.huellitas_callejeras.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "medicamentos",
    foreignKeys = [
        ForeignKey(
            entity = Tratamiento::class,
            parentColumns = ["id"],
            childColumns = ["tratamientoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Medicamento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tratamientoId: Int,
    val nombre: String = "",
    val fechaInicio: String = "",
    val fechaConclusion: String = "",
    val dosis: String = "",
    val repeticion: String = ""
)