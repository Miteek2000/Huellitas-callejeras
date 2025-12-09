package com.proyecto.huellitas_callejeras.remote.dto

import com.proyecto.huellitas_callejeras.models.Animal

fun AnimalDto.toDomain(): Animal {
    return Animal(
        idAnimal = id,
        nombre = nombre,
        peso = peso,
        raza = raza,
        sexo = sexo,
        edad = edad,
        especie = especie,
        estado = estado,
        fechaSalida = fechaSalida,
        urlImagen = urlImage,
        rescatistaId = rescatistaId,
    )
}