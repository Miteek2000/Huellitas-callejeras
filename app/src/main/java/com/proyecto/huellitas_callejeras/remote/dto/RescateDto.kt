package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class RescateDTO(
    val id: String,
    val fechaIngreso: String,
    val lugar: String,
    val descripcion: String,
    val animalId: UUID?
)

