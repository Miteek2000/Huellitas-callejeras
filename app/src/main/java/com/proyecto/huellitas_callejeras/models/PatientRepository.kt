package com.proyecto.huellitas_callejeras.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object PatientRepository {
    private val _patients = MutableStateFlow(
        mutableListOf(
            Patient(1, "Puppy", "Mestizo", isAdopted = true, description = "Juguetón y amigable", imageUrl = ""),
            Patient(2, "Manchas", "Dálmata", isAdopted = false, description = "Leal y protector", isRecovering = true, imageUrl = ""),
            Patient(3, "Luna", "Siames", isAdopted = false, description = "Independiente y cariñosa", imageUrl = ""),
            Patient(4, "Max", "Labrador", isAdopted = true, description = "Energético y obediente", imageUrl = ""),
            Patient(5, "Rocky", "Bulldog", isAdopted = false, description = "Tranquilo y valiente", imageUrl = ""),
            Patient(6, "Bella", "Poodle", isAdopted = false, description = "Inteligente y elegante", isRecovering = true, imageUrl = ""),
        )
    )

    val patients = _patients.asStateFlow()

    fun addPatient(patient: Patient) {
        _patients.update { (it + patient).toMutableList() }
    }

    fun getPatientById(id: Int): Patient? {
        return _patients.value.find { it.id == id }
    }

    fun removePatient(id: Int) {
        _patients.update { patients ->
            patients.filter { it.id != id }.toMutableList()
        }
    }

    fun savePatient(patient: Patient) {
        _patients.update { patients ->
            val index = patients.indexOfFirst { it.id == patient.id }
            val newList = patients.toMutableList()
            if (index != -1) {
                newList[index] = patient
            } else {
                val newId = (patients.maxOfOrNull { it.id } ?: 0) + 1
                newList.add(patient.copy(id = newId))
            }
            newList
        }
    }
}
