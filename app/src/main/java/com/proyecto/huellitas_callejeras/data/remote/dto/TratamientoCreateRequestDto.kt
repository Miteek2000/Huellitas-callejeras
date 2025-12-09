package com.proyecto.huellitas_callejeras.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.util.UUID


data class TratamientoCreateRequestDto(
    @SerializedName("animal_id") val animalId: UUID,
    @SerializedName("fecha_inicio") val fechaInicio: Instant,
    val medicamentos: List<MedicamentoInTratamientoDto>
)


data class MedicamentoInTratamientoDto(
    @SerializedName("medicamento_id") val medicamentoId: UUID,
    val nombre: String,
    val dosis: Float,
    val repeticion: Float,
    @SerializedName("fecha_conclusion") val fechaConclusion: Instant? = null
)
