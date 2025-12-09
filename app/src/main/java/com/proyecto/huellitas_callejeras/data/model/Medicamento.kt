package com.proyecto.huellitas_callejeras.data.model

import java.util.UUID

data class Medicamento(
    val id: Int = 0,
    var tratamientoId: Int,
    var nombre: String,
    var fechaInicio: String,
    var fechaConclusion: String,
    var dosis: String,
    var frecuencia: String,
    var enviado: Boolean = false,
    val idApi: UUID? = null
)
