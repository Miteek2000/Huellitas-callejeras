package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class TratamientoMedicamentoDTO(
    val tratamiento_id: String,
    val medicamento_id: String,
    val dosis: Double,
    val fecha_conclusion: String?,
    val repeticion: Double
)

data class TratamientoMedicamentoRequest(
    val medicamento_id: String,
    val dosis: Double,
    val repeticion: Double,
    val fecha_conclusion: String?
)

