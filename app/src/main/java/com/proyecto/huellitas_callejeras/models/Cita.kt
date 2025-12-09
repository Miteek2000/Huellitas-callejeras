package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cita(
    val id: String,
    val titulo: String,
    val fechaRealizacion: String,
    val fechaCita: String,
    val motivo: String,
    val lugar: String,
    val animalId: String
) : Parcelable
