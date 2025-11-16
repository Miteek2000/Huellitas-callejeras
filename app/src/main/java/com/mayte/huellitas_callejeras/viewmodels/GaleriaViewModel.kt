package com.mayte.huellitas_callejeras.viewmodels

import androidx.lifecycle.ViewModel
import com.mayte.huellitas_callejeras.models.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GaleriaViewModel : ViewModel() {

    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> = _patients

    init {
        // Datos de ejemplo
        _patients.value = listOf(
            Patient(1, "Max", "Chihuahua", "", isAdopted = true),
            Patient(2, "Max", "Chihuahua", ""),
            Patient(3, "Max", "Chihuahua", ""),
            Patient(4, "Max", "Chihuahua", ""),
            Patient(5, "Max", "Chihuahua", "", isAdopted = true),
            Patient(6, "Max", "Chihuahua", ""),
        )
    }

    fun addPatient() {
        val newId = (_patients.value.maxOfOrNull { it.id } ?: 0) + 1
        val newPatient = Patient(newId, "Nuevo", "Mestizo", "", isAdopted = false)
        _patients.value = _patients.value + newPatient
    }

    fun removePatient(patient: Patient) {
        _patients.value = _patients.value.filter { it.id != patient.id }
    }
}
