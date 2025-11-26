package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class CitaDTO(
    val id_citas: String,
    val titulo: String,
    val fechaCita: String,
    val motivo: String,
    val lugar: String,
    val fechaRealizacion: String,
    val animalitoId: UUID?
)

data class CitaRequest(
    val titulo: String,
    val fechaCita: String,
    val motivo: String,
    val lugar: String,
    val fechaRealizacion: String,
    val animalitoId: UUID?
)