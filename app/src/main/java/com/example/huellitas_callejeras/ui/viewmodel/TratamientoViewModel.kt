package com.example.huellitas_callejeras.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huellitas_callejeras.data.model.Tratamiento
import com.example.huellitas_callejeras.data.model.TratamientoConMedicamentos
import com.example.huellitas_callejeras.data.repository.TratamientoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TratamientoViewModel(
    private val repository: TratamientoRepository
) : ViewModel() {

    private val _tratamientosConMedicamentos = MutableStateFlow<List<TratamientoConMedicamentos>>(emptyList())
    val tratamientosConMedicamentos: StateFlow<List<TratamientoConMedicamentos>> = _tratamientosConMedicamentos.asStateFlow()

    private val _selectedTratamientoIndex = MutableStateFlow(0)
    val selectedTratamientoIndex: StateFlow<Int> = _selectedTratamientoIndex.asStateFlow()

    private val _showFormulario = MutableStateFlow(false)
    val showFormulario: StateFlow<Boolean> = _showFormulario.asStateFlow()

    init {
        cargarTratamientos()
    }

    private fun cargarTratamientos() {
        viewModelScope.launch {
            repository.obtenerTodosConMedicamentos().collect { tratamientos ->
                _tratamientosConMedicamentos.value = tratamientos
                if (tratamientos.isNotEmpty() && _selectedTratamientoIndex.value >= tratamientos.size) {
                    _selectedTratamientoIndex.value = tratamientos.size - 1
                }
            }
        }
    }

    fun seleccionarTratamiento(index: Int) {
        _selectedTratamientoIndex.value = index
        _showFormulario.value = false
    }

    fun mostrarFormulario(mostrar: Boolean) {
        _showFormulario.value = mostrar
    }

    fun agregarNuevoTratamiento() {
        viewModelScope.launch {
            val nuevoNumero = _tratamientosConMedicamentos.value.size + 1
            repository.insertarTratamiento(
                Tratamiento(nombre = "tratamiento $nuevoNumero")
            )
            _selectedTratamientoIndex.value = _tratamientosConMedicamentos.value.size
            _showFormulario.value = true
        }
    }

    fun eliminarTratamiento(tratamientoId: Int) {
        viewModelScope.launch {
            val tratamiento = _tratamientosConMedicamentos.value
                .find { it.tratamiento.id == tratamientoId }
                ?.tratamiento

            tratamiento?.let {
                repository.eliminarTratamiento(it)

                val tratamientos = _tratamientosConMedicamentos.value
                if (tratamientos.isEmpty()) {
                    _selectedTratamientoIndex.value = 0
                } else if (_selectedTratamientoIndex.value >= tratamientos.size) {
                    _selectedTratamientoIndex.value = tratamientos.size - 1
                }
            }
        }
    }

    fun actualizarFechaInicio(tratamientoId: Int, fecha: String) {
        viewModelScope.launch {
            val tratamiento = _tratamientosConMedicamentos.value
                .find { it.tratamiento.id == tratamientoId }
                ?.tratamiento

            tratamiento?.let {
                repository.actualizarTratamiento(
                    it.copy(fechaInicio = fecha)
                )
            }
        }
    }
}

class TratamientoViewModelFactory(
    private val repository: TratamientoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TratamientoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TratamientoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}