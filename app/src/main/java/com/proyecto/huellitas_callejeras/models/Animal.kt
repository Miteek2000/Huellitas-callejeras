package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Animal(
    val idAnimal: String,
    val peso: Double,
    val raza: String?,
    val sexo: String,
    val fechaSalida: String?,
    val estado: String,
    val nombre: String,
    val edad: Int,
    val especie: String,
    val urlImagen: String?,
    val rescatistaId: String
) : Parcelable
