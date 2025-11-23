package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cita(
    val idCitas: String,
    val titulo: String,
    val fechaRealizacion: String,
    val fechaCita: String,
    val motivo: String,
    val lugar: String,
    val animalitoId: String
) : Parcelable
