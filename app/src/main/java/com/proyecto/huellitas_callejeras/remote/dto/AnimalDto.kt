package com.proyecto.huellitas_callejeras.remote.dto

data class AnimalDto(
    val id: String,
    val peso: Double,
    val raza: String?,
    val sexo: String,
    val fechaSalida: String?,
    val estado: String,
    val nombre: String,
    val edad: Int,
    val especie: String,
    val urlImage: String?,
    val rescatistaId: String
)

data class AnimalRequest(
    val peso: Double,
    val raza: String,
    val sexo: String,
    val estado: String,
    val nombre: String,
    val edad: Int,
    val especie: String,
    val rescatistaId: String,
    val fechaSalida: String? = null
)

data class AnimalRescateRequest(
    val animal: AnimalRequest,
    val rescate: RescateRequest
)

data class AnimalRescateResponse(
    val animal: AnimalDto,
    val rescate: RescateDTO
)

data class RescateRequest(
    val lugar: String,
    val descripcion: String
)


