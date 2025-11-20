package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cita(
    val id: Int,
    val title: String,
    val date: String,
    val place: String,
    val realizationDate: String,
    val patientName: String,
    val patientId: Int? = null,
    val motive: String? = null // Motive is optional
) : Parcelable
