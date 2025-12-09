package com.proyecto.huellitas_callejeras.remote.dto

import com.google.gson.annotations.SerializedName

data class TratamientoDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("fechaInicio")
    val fechaInicio: String,

    @SerializedName("receta")
    val receta: String,

    @SerializedName("animalId")
    val animalId: String,

    @SerializedName("medicamentos")
    val medicamentos: List<MedicamentoDto>
)
