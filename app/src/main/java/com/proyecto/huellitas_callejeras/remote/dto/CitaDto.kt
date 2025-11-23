package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class CitaDTO(
    val id_citas: UUID,
    val  titulo: String,
    val fecha_realizacion: String,
    val fecha_cita: String,
    val motivo: String,
    val lugar: String,
    val url_imagen: String,
    val animalito_id: UUID?
)

data class CitaRequest(
    val titulo: String,
    val fecha_cita: String,
    val motivo: String,
    val lugar: String,
    val url_imagen: String,
    val animalito_id: UUID?
)