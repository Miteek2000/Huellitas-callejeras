package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class RescateDTO(
    val id_rescate: UUID,
    val fecha_ingreso: String,
    val lugar: String,
    val descripcion: String,
    val animalito_id: UUID?
)

data class RescateRequest(
    val lugar: String,
    val descripcion: String,
    val animalito_id: UUID?
)