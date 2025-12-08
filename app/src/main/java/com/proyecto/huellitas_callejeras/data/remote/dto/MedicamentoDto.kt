package com.proyecto.huellitas_callejeras.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MedicamentoDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("nombre")
    val nombre: String
)
