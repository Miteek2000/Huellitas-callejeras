package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TratamientoMedicamento(
    val tratamientoId: String,
    val medicamentoId: String,
    val dosis: Double,
    val fechaConclusion: String?,
    val repeticion: Double,
    val medicamento: Medicamento? = null
) : Parcelable