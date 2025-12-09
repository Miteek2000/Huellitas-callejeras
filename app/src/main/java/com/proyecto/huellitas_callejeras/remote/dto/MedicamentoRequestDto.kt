package com.proyecto.huellitas_callejeras.remote.dto

import com.google.gson.annotations.SerializedName

data class MedicamentoRequestDto(
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("dosis")
    val dosis: String,
    @SerializedName("repeticion")
    val repeticion: String
)
