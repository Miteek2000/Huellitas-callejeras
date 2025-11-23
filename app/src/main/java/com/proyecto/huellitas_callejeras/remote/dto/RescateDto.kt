package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class RescateDTO(
    val id_rescate: UUID,
    val fechaIngreso: String,
    val lugar: String,
    val descripcion: String,
    val animalito_id: UUID?
)

