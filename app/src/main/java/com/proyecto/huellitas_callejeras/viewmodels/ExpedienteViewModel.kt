package com.proyecto.huellitas_callejeras.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.proyecto.huellitas_callejeras.models.Patient
import com.proyecto.huellitas_callejeras.models.PatientRepository
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
        _uiState.update { it.copy(nombre = nombre, nombreError = false) }
    }

    fun onEspecieChange(especie: String) {
        _uiState.update { it.copy(especie = especie, especieError = false) }
    }

    fun onRazaChange(raza: String) {
        _uiState.update { it.copy(raza = raza, razaError = false) }
    }

    fun onEdadChange(edad: String) {
        _uiState.update { it.copy(edad = edad, edadError = false) }
    }

    fun onSexoChange(sexo: String) {
        _uiState.update { it.copy(sexo = sexo, sexoError = false) }
    }

    fun onPesoChange(peso: String) {
        _uiState.update { it.copy(peso = peso, pesoError = false) }
    }

    fun onFechaIngresoChange(fecha: String) {
        _uiState.update { it.copy(fechaIngreso = fecha, fechaIngresoError = false) }
    }

    fun onFechaSalidaChange(fecha: String) {
        _uiState.update { it.copy(fechaSalida = fecha) }
    }

    fun onLugarRescateChange(lugar: String) {
        _uiState.update { it.copy(lugarRescate = lugar, lugarRescateError = false) }
    }

    fun onCondicionesRescateChange(descripcion: String) {
        _uiState.update { it.copy(condicionesRescate = descripcion, condicionesRescateError = false) }
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

    fun savePatient(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        val nombreError = currentState.nombre.isBlank()
        val especieError = currentState.especie.isBlank()
        val razaError = currentState.raza.isBlank()
        val edadError = currentState.edad.isBlank()
        val sexoError = currentState.sexo.isBlank()
        val pesoError = currentState.peso.isBlank()
        val fechaIngresoError = currentState.fechaIngreso.isBlank()
        val lugarRescateError = currentState.lugarRescate.isBlank()
        val condicionesRescateError = currentState.condicionesRescate.isBlank()

        val hasError = nombreError || especieError || razaError || edadError || sexoError || pesoError || fechaIngresoError || lugarRescateError || condicionesRescateError

        if (!hasError) {
            val patient = Patient(
                id = currentState.id ?: 0,
                name = currentState.nombre,
                breed = currentState.raza,
                isAdopted = currentState.isAdopted,
                isRecovering = currentState.isRecovering,
                description = currentState.condicionesRescate,
                imageUrl = currentState.selectedImageUri.toString(),
            )
            PatientRepository.savePatient(patient)
            _uiState.update { it.copy(
                nombreError = false,
                especieError = false,
                razaError = false,
                edadError = false,
                sexoError = false,
                pesoError = false,
                fechaIngresoError = false,
                lugarRescateError = false,
                condicionesRescateError = false
            ) }
            onSuccess()
        } else {
            _uiState.update { it.copy(
                nombreError = nombreError,
                especieError = especieError,
                razaError = razaError,
                edadError = edadError,
                sexoError = sexoError,
                pesoError = pesoError,
                fechaIngresoError = fechaIngresoError,
                lugarRescateError = lugarRescateError,
                condicionesRescateError = condicionesRescateError
            ) }
        }
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
    val sexoOptions: List<String> = listOf("Macho", "Hembra"),
    val nombreError: Boolean = false,
    val especieError: Boolean = false,
    val razaError: Boolean = false,
    val edadError: Boolean = false,
    val sexoError: Boolean = false,
    val pesoError: Boolean = false,
    val fechaIngresoError: Boolean = false,
    val lugarRescateError: Boolean = false,
    val condicionesRescateError: Boolean = false
)
