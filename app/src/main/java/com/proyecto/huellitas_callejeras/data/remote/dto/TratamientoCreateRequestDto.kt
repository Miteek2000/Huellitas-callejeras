package com.proyecto.huellitas_callejeras.data.remote.dto

import java.time.Instant
import java.util.UUID


data class TratamientoCreateRequestDto(
    val animalId: UUID,
    val fechaInicio: Instant,
    val medicamentos: List<MedicamentoInTratamientoDto>
)


data class MedicamentoInTratamientoDto(
    val nombre: String,
    val dosis: String,
    val repeticion: String,
    val fechaInicio: Instant,
    val fechaConclusion: Instant
)
