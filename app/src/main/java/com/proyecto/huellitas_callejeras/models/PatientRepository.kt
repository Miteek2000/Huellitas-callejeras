package com.proyecto.huellitas_callejeras.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object PatientRepository {
    private val _patients = MutableStateFlow(
        mutableListOf(
            Animal(1, "Puppy", "Mestizo", isAdopted = true, description = "Juguetón y amigable", imageUrl = ""),
            Animal(2, "Manchas", "Dálmata", isAdopted = false, description = "Leal y protector", isRecovering = true, imageUrl = ""),
            Animal(3, "Luna", "Siames", isAdopted = false, description = "Independiente y cariñosa", imageUrl = ""),
            Animal(4, "Max", "Labrador", isAdopted = true, description = "Energético y obediente", imageUrl = ""),
            Animal(5, "Rocky", "Bulldog", isAdopted = false, description = "Tranquilo y valiente", imageUrl = ""),
            Animal(6, "Bella", "Poodle", isAdopted = false, description = "Inteligente y elegante", isRecovering = true, imageUrl = ""),
        )
    )

    val patients = _patients.asStateFlow()

    fun addPatient(animal: Animal) {
        _patients.update { (it + animal).toMutableList() }
    }

    fun getPatientById(id: Int): Animal? {
        return _patients.value.find { it.id == id }
    }

    fun removePatient(id: Int) {
        _patients.update { patients ->
            patients.filter { it.id != id }.toMutableList()
        }
    }

    fun savePatient(animal: Animal) {
        _patients.update { patients ->
            val index = patients.indexOfFirst { it.id == animal.id }
            val newList = patients.toMutableList()
            if (index != -1) {
                newList[index] = animal
            } else {
                val newId = (patients.maxOfOrNull { it.id } ?: 0) + 1
                newList.add(animal.copy(id = newId))
            }
            newList
        }
    }
}
