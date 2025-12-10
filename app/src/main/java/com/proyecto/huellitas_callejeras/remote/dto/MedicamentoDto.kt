package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class MedicamentoDTO(
    val id: String,
    val nombre: String
)

data class MedicamentoRequest(
    val nombre: String
)

