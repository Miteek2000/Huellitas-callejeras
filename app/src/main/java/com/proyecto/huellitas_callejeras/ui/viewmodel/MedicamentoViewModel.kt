package com.proyecto.huellitas_callejeras.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.data.model.Medicamento
import com.proyecto.huellitas_callejeras.data.repository.MedicamentoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FormState(
    val tratamientoId: Int? = null,
    val medicamentoId: Int? = null,
    val nombre: String = "",
    val dosis: String = "",
    val frecuencia: String = "",
    val fechaInicio: String = "",
    val fechaConclusion: String = "",
    val nombreError: String = "",
    val dosisError: String = "",
    val frecuenciaError: String = ""
)

@RequiresApi(Build.VERSION_CODES.O)
class MedicamentoViewModel(
    private val repository: MedicamentoRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarMedicamento(tratamientoId: Int, medicamentoId: Int) {
        viewModelScope.launch {
            val med = if (medicamentoId != -1 && medicamentoId != 0) {
                repository.obtenerMedicamentoPorId(medicamentoId)
            } else {
                null
            }

            if (med != null) {
                _formState.value = FormState(
                    tratamientoId = tratamientoId,
                    medicamentoId = medicamentoId,
                    nombre = med.nombre,
                    dosis = med.dosis,
                    frecuencia = med.frecuencia,
                    fechaInicio = med.fechaInicio,
                    fechaConclusion = med.fechaConclusion
                )
            } else {
                limpiarFormulario(tratamientoId = tratamientoId)
            }
        }
    }

    fun limpiarFormulario(tratamientoId: Int? = _formState.value.tratamientoId) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        _formState.value = FormState(
            tratamientoId = tratamientoId,
            fechaInicio = today,
            fechaConclusion = today
        )
    }

    fun actualizarNombre(nombre: String) {
        _formState.update { it.copy(nombre = nombre, nombreError = "") }
    }

    fun actualizarDosis(dosis: String) {
        _formState.update { it.copy(dosis = dosis, dosisError = "") }
    }

    fun actualizarFrecuencia(frecuencia: String) {
        _formState.update { it.copy(frecuencia = frecuencia, frecuenciaError = "") }
    }

    fun actualizarFechaConclusion(fecha: String) {
        _formState.update { it.copy(fechaConclusion = fecha) }
    }

    fun guardarMedicamento(onSuccess: () -> Unit) {
        if (!validarFormulario()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val state = _formState.value
                val medicamento = Medicamento(
                    id = state.medicamentoId ?: 0,
                    tratamientoId = state.tratamientoId ?: throw IllegalStateException("Tratamiento ID no puede ser nulo"),
                    nombre = state.nombre,
                    dosis = state.dosis,
                    frecuencia = state.frecuencia,
                    fechaInicio = state.fechaInicio,
                    fechaConclusion = state.fechaConclusion
                )

                if (medicamento.id == 0) {
                    repository.insertarMedicamentoLocal(medicamento)
                } else {
                    repository.actualizarMedicamentoLocal(medicamento)
                }
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Error al guardar el medicamento: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val state = _formState.value
        var esValido = true

        if (state.nombre.isBlank()) {
            _formState.update { it.copy(nombreError = "El nombre no puede estar vacío") }
            esValido = false
        }
        if (state.dosis.isBlank()) {
            _formState.update { it.copy(dosisError = "La dosis no puede estar vacía") }
            esValido = false
        }
        if (state.frecuencia.isBlank()) {
            _formState.update { it.copy(frecuenciaError = "La frecuencia no puede estar vacía") }
            esValido = false
        }

        return esValido
    }

    fun resetError() {
        _error.value = null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class MedicamentoViewModelFactory(private val repository: MedicamentoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicamentoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicamentoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
