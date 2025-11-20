package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tratamiento(
    val idTratamiento: String,
    val fechaInicio: String,
    val receta: String,
    val medicamentos: List<TratamientoMedicamento>? = null
) : Parcelable