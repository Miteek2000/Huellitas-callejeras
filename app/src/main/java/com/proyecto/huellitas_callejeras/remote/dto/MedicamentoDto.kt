package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class MedicamentoDTO(
    val id_medicamento: UUID,
    val nombre: String
)

data class MedicamentoRequest(
    val nombre: String
)