package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class TratamientoDTO(
    val id_tratamiento: UUID,
    val fecha_inicio: String,
    val receta: String
)

data class TratamientoRequest(
    val fecha_inicio: String,
    val receta: String
)