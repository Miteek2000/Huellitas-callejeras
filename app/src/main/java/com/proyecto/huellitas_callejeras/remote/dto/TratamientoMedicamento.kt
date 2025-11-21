package com.proyecto.huellitas_callejeras.remote.dto

import java.util.UUID

data class TratamientoMedicamentoDTO(
    val tratamiento_id: UUID,
    val medicamento_id: UUID,
    val dosis: Double,
    val fecha_conclusion: String?,
    val repeticion: Double
)

data class TratamientoMedicamentoRequest(
    val medicamento_id: UUID,
    val dosis: Double,
    val repeticion: Double,
    val fecha_conclusion: String?
)