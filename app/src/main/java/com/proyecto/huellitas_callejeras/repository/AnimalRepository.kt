package com.proyecto.huellitas_callejeras.repository

import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.remote.ApiService
import com.proyecto.huellitas_callejeras.remote.dto.AnimalDto
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRequest

class AnimalRepository(private val api: ApiService) {

    suspend fun getAll(): List<Animal> {
        println("Llamando endpoint GET /animalitos")
        return api.getAnimalitos()
    }


    suspend fun getById(id: String) = api.getAnimalito(id)

    suspend fun create(body: AnimalRequest) = api.createAnimalito(body)

    suspend fun update(id: String, body: AnimalRequest) = api.updateAnimalito(id, body)

    suspend fun delete(id: String) = api.deleteAnimalito(id)
}