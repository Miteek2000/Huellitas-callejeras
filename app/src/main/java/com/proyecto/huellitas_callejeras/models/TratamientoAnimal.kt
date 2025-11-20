package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TratamientoAnimal(
    val animalitoId: String,
    val tratamientoId: String
) : Parcelable