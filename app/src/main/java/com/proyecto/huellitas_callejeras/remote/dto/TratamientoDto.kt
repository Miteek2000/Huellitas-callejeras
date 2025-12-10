package com.proyecto.huellitas_callejeras.remote.dto

import java.time.Instant
import java.util.UUID


data class TratamientoCreateRequestDto(
    val animalId: String,
    val fechaInicio: String,
    val medicamentos: List<MedicamentoTratamientoDto>
)

// DTO de medicamento dentro del tratamiento
data class MedicamentoTratamientoDto(
    val medicamentoId: String,
    val nombre: String,
    val dosis: Float,
    val repeticion: Float,
    val fechaConclusion: String?
)

// Response del tratamiento
data class TratamientoDto(
    val id: String,
    val animalId: String,
    val fechaInicio: String,
    val recetaUrl: String?,
    val medicamentos: List<MedicamentoTratamientoDto>
)

data class TratamientoResumenDto(
    val id: String,
    val fechaInicio: String, // "2025-12-10T00:00:00Z"
    val recetaUrl: String? // "/uploads/tratamientos/c38d1064-bfe4-4235-a8fd-5d9eb38a87c7.bin"
)