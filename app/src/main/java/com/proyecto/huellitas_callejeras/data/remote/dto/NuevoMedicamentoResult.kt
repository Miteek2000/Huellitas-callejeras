package com.proyecto.huellitas_callejeras.data.remote.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NuevoMedicamentoResult(
    val id: String,
    val nombre: String,
    val dosis: String,
    val frecuencia: String
) : Parcelable
