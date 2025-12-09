package com.proyecto.huellitas_callejeras.data.model

data class Tratamiento(
    val id: Int = 0,
    var nombre: String,
    var fechaInicio: String = "",
    var fechaConclusion: String = ""
)
