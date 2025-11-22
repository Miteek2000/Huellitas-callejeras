package com.proyecto.huellitas_callejeras.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.proyecto.huellitas_callejeras.models.Animal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ExpedienteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExpedienteUiState())
    val uiState = _uiState.asStateFlow()

    fun init(patientId: Int?, isEditable: Boolean) {
        if (patientId != null) {
            val patient = PatientRepository.getPatientById(patientId)
            if (patient != null) {
                _uiState.value = ExpedienteUiState(
                    id = patient.id,
                    nombre = patient.name,
                    raza = patient.breed,
                    isAdopted = patient.isAdopted,
                    isRecovering = patient.isRecovering,
                    condicionesRescate = patient.description,
                    estadoSelected = when {
                        patient.isAdopted -> "Adoptado"
                        patient.isRecovering -> "En recuperación"
                        else -> "En adopción"
                    },
                    selectedImageUri = if (patient.imageUrl.isNotEmpty()) Uri.parse(patient.imageUrl) else null,
                    isEditing = isEditable
                )
            }
        } else {
            _uiState.value = ExpedienteUiState(isEditing = isEditable)
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre) }
    }

    fun onEspecieChange(especie: String) {
        _uiState.update { it.copy(especie = especie) }
    }

    fun onRazaChange(raza: String) {
        _uiState.update { it.copy(raza = raza) }
    }

    fun onEdadChange(edad: String) {
        _uiState.update { it.copy(edad = edad) }
    }

    fun onSexoChange(sexo: String) {
        _uiState.update { it.copy(sexo = sexo) }
    }

    fun onPesoChange(peso: String) {
        _uiState.update { it.copy(peso = peso) }
    }

    fun onFechaIngresoChange(fecha: String) {
        _uiState.update { it.copy(fechaIngreso = fecha) }
    }

    fun onFechaSalidaChange(fecha: String) {
        _uiState.update { it.copy(fechaSalida = fecha) }
    }

    fun onLugarRescateChange(lugar: String) {
        _uiState.update { it.copy(lugarRescate = lugar) }
    }

    fun onCondicionesRescateChange(descripcion: String) {
        _uiState.update { it.copy(condicionesRescate = descripcion) }
    }

    fun onEstadoSelected(estado: String) {
        _uiState.update { it.copy(
            estadoSelected = estado,
            isAdopted = estado == "Adoptado",
            isRecovering = estado == "En recuperación"
        ) }
    }

    fun onEstadoExpandedChange(isExpanded: Boolean) {
        _uiState.update { it.copy(estadoExpanded = isExpanded) }
    }

    fun setEditing(isEditing: Boolean) {
        _uiState.update { it.copy(isEditing = isEditing) }
    }

    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun savePatient() {
        val currentState = _uiState.value
        val animal = Animal(
            id = currentState.id ?: 0,
            name = currentState.nombre,
            breed = currentState.raza,
            isAdopted = currentState.isAdopted,
            isRecovering = currentState.isRecovering,
            description = currentState.condicionesRescate,
            imageUrl = currentState.selectedImageUri.toString(),
        )
        PatientRepository.savePatient(animal)
    }

    fun onShowDatePickerDialog(forField: String?) {
        _uiState.update { it.copy(showDatePickerDialogFor = forField) }
    }

    fun onDateSelected(millis: Long?) {
        millis?.let {
            val date = Date(it + TimeZone.getDefault().getOffset(it))
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = format.format(date)

            when (_uiState.value.showDatePickerDialogFor) {
                "ingreso" -> onFechaIngresoChange(formattedDate)
                "salida" -> onFechaSalidaChange(formattedDate)
            }
        }
        onShowDatePickerDialog(null)
    }
}

data class ExpedienteUiState(
    val id: Int? = null,
    val nombre: String = "",
    val especie: String = "",
    val raza: String = "",
    val edad: String = "",
    val sexo: String = "",
    val peso: String = "",
    val fechaIngreso: String = "",
    val fechaSalida: String = "",
    val lugarRescate: String = "",
    val condicionesRescate: String = "",
    val estadoSelected: String = "En adopción",
    val estadoExpanded: Boolean = false,
    val isAdopted: Boolean = false,
    val isRecovering: Boolean = false,
    val isEditing: Boolean = false,
    val selectedImageUri: Uri? = null,
    val showDatePickerDialogFor: String? = null,
    val estadoOptions: List<String> = listOf("Adoptado", "En adopción", "En recuperación"),
    val especieOptions: List<String> = listOf("Perro", "Gato"),
    val sexoOptions: List<String> = listOf("Macho", "Hembra")
)
