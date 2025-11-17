package com.mayte.huellitas_callejeras.viewmodels

import androidx.lifecycle.ViewModel
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

    fun init(patientId: String?, isEditable: Boolean) {
        // TODO: Load patient from repository if patientId is not null
        // For now, this resets the state.
        _uiState.value = ExpedienteUiState(isEditing = isEditable)
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
        _uiState.update { it.copy(estadoSelected = estado) }
    }

    fun onEstadoExpandedChange(isExpanded: Boolean) {
        _uiState.update { it.copy(estadoExpanded = isExpanded) }
    }

    fun setEditing(isEditing: Boolean) {
        _uiState.update { it.copy(isEditing = isEditing) }
    }

    fun savePatient() {

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
    val nombre: String = "",
    val especie: String = "Perro",
    val raza: String = "",
    val edad: String = "",
    val sexo: String = "Macho",
    val peso: String = "",
    val fechaIngreso: String = "",
    val fechaSalida: String = "",
    val lugarRescate: String = "",
    val condicionesRescate: String = "",
    val estadoSelected: String = "En adopción",
    val estadoExpanded: Boolean = false,
    val isEditing: Boolean = false,
    val showDatePickerDialogFor: String? = null,
    val estadoOptions: List<String> = listOf("Adoptado", "En adopción", "En recuperación"),
    val especieOptions: List<String> = listOf("Perro", "Gato"),
    val sexoOptions: List<String> = listOf("Macho", "Hembra")
)
