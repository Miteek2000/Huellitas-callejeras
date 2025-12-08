package com.proyecto.huellitas_callejeras.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tratamientos")
data class Tratamiento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var nombre: String,
    var fechaInicio: String = "",
    var fechaConclusion: String = ""
)
