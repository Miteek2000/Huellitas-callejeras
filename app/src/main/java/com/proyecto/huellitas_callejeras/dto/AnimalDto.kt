package com.proyecto.huellitas_callejeras.dto

data class AnimalDto(
    val id_animalito: String,
    val peso: Double,
    val raza: String?,
    val sexo: String,
    val fecha_salida: String?,
    val estado: String,
    val nombre: String,
    val edad: Int,
    val especie: String,
    val urlImagen: String
)