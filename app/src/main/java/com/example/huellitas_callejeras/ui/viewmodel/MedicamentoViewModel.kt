package com.example.huellitas_callejeras.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huellitas_callejeras.data.model.Medicamento
import com.example.huellitas_callejeras.data.repository.TratamientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MedicamentoFormState(
    val medicamentoId: Int? = null,
    val nombre: String = "",
    val fechaConclusion: String = "",
    val dosis: String = "",
    val repeticion: String = "",
    val nombreError: String = "",
    val fechaError: String = "",
    val dosisError: String = "",
    val repeticionError: String = ""
) {
    fun isValid(): Boolean {
        return nombre.isNotEmpty() &&
                fechaConclusion.isNotEmpty() &&
                dosis.isNotEmpty() &&
                repeticion.isNotEmpty()
    }
}

class MedicamentoViewModel(
    private val repository: TratamientoRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(MedicamentoFormState())
    val formState: StateFlow<MedicamentoFormState> = _formState.asStateFlow()

    fun cargarMedicamento(medicamentoId: Int) {
        viewModelScope.launch {
            val medicamento = repository.obtenerMedicamentoPorId(medicamentoId)
            medicamento?.let {
                _formState.value = MedicamentoFormState(
                    medicamentoId = it.id,
                    nombre = it.nombre,
                    fechaConclusion = it.fechaConclusion,
                    dosis = it.dosis,
                    repeticion = it.repeticion
                )
                Log.d("MedicamentoViewModel", "Medicamento cargado: ${it.nombre}")
            }
        }
    }

    fun actualizarNombre(valor: String) {
        _formState.value = _formState.value.copy(
            nombre = valor,
            nombreError = ""
        )
    }

    fun actualizarFechaConclusion(valor: String) {
        _formState.value = _formState.value.copy(
            fechaConclusion = valor,
            fechaError = ""
        )
    }

    fun actualizarDosis(valor: String) {
        _formState.value = _formState.value.copy(
            dosis = valor,
            dosisError = ""
        )
    }

    fun actualizarRepeticion(valor: String) {
        _formState.value = _formState.value.copy(
            repeticion = valor,
            repeticionError = ""
        )
    }

    fun validarTodo(): Boolean {
        val state = _formState.value

        val nombreError = if (state.nombre.isEmpty()) "Campo obligatorio" else ""
        val fechaError = if (state.fechaConclusion.isEmpty()) "Campo obligatorio" else ""
        val dosisError = if (state.dosis.isEmpty()) "Campo obligatorio" else ""
        val repeticionError = if (state.repeticion.isEmpty()) "Campo obligatorio" else ""

        _formState.value = state.copy(
            nombreError = nombreError,
            fechaError = fechaError,
            dosisError = dosisError,
            repeticionError = repeticionError
        )

        return nombreError.isEmpty() &&
                fechaError.isEmpty() &&
                dosisError.isEmpty() &&
                repeticionError.isEmpty()
    }

    fun guardarMedicamento(tratamientoId: Int, onSuccess: () -> Unit) {
        Log.d("MedicamentoViewModel", "guardarMedicamento llamado")

        if (validarTodo()) {
            Log.d("MedicamentoViewModel", "Validación OK")
            viewModelScope.launch {
                try {
                    val medicamento = Medicamento(
                        id = _formState.value.medicamentoId ?: 0,
                        tratamientoId = tratamientoId,
                        nombre = _formState.value.nombre,
                        fechaConclusion = _formState.value.fechaConclusion,
                        dosis = _formState.value.dosis,
                        repeticion = _formState.value.repeticion
                    )

                    if (_formState.value.medicamentoId == null) {
                        repository.insertarMedicamento(medicamento)
                        Log.d("MedicamentoViewModel", "Medicamento insertado")
                    } else {
                        repository.actualizarMedicamento(medicamento)
                        Log.d("MedicamentoViewModel", "Medicamento actualizado")
                    }

                    limpiarFormulario()
                    onSuccess()
                } catch (e: Exception) {
                    Log.e("MedicamentoViewModel", "Error: ${e.message}", e)
                }
            }
        }
    }

    fun limpiarFormulario() {
        _formState.value = MedicamentoFormState()
    }
}

class MedicamentoViewModelFactory(
    private val repository: TratamientoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicamentoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicamentoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}