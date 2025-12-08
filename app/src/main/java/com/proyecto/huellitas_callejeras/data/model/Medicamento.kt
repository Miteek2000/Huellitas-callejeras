package com.proyecto.huellitas_callejeras.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    ],
    indices = [Index(value = ["tratamientoId"])]
)
data class Medicamento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var tratamientoId: Int,
    var nombre: String,
    var fechaInicio: String, // Mantener para la UI local si es necesario
    var fechaConclusion: String, // Mantener para la UI local si es necesario
    var dosis: String,
    var frecuencia: String, // Renombrado de 'repeticion'
    var enviado: Boolean = false
)
