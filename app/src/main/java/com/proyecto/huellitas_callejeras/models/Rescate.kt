package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rescate(
    val idRescate: String,
    val fechaIngreso: String?,
    val lugar: String,
    val descripcion: String,
    val animalitoId: String
) : Parcelable