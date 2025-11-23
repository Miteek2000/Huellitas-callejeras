package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Medicamento(
    val idMedicamento: String,
    val nombre: String
) : Parcelable
