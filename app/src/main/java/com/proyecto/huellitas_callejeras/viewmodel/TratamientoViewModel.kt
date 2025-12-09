package com.proyecto.huellitas_callejeras.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.models.Tratamiento
import com.proyecto.huellitas_callejeras.models.TratamientoConMedicamentos
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoInTratamientoDto
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoCreateRequestDto
import com.proyecto.huellitas_callejeras.repository.TratamientoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

class TratamientoViewModel(
    private val repository: TratamientoRepository
) : ViewModel() {

    // --- Estados para la UI (Base de datos local) ---
    private val _tratamientosConMedicamentos = MutableStateFlow<List<TratamientoConMedicamentos>>(emptyList())
    val tratamientosConMedicamentos: StateFlow<List<TratamientoConMedicamentos>> = _tratamientosConMedicamentos.asStateFlow()

    private val _selectedTratamientoIndex = MutableStateFlow(0)
    val selectedTratamientoIndex: StateFlow<Int> = _selectedTratamientoIndex.asStateFlow()

    private val _showFormulario = MutableStateFlow(false)
    val showFormulario: StateFlow<Boolean> = _showFormulario.asStateFlow()

    // --- Estados para operaciones de Red ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    init {
        cargarTratamientosLocales()
    }

    // --- Operaciones de Red ---

    @RequiresApi(Build.VERSION_CODES.O)
    fun guardarTratamientoRemoto(
        animalId: String,
        fechaInicio: String,
        medicamentos: List<MedicamentoInTratamientoDto>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false

            try {
                val animalIdUUID = UUID.fromString(animalId)
                val fechaInicioInstant = LocalDate.parse(fechaInicio, DateTimeFormatter.ISO_LOCAL_DATE)
                    .atStartOfDay().toInstant(ZoneOffset.UTC)

                val request = TratamientoCreateRequestDto(
                    animalId = animalIdUUID,
                    fechaInicio = fechaInicioInstant,
                    medicamentos = medicamentos
                )

                Log.d("TratamientoViewModel", "Enviando al backend: $request")

                val result = repository.createTratamiento(request)

                result.onSuccess {
                    Log.d("TratamientoViewModel", "Respuesta exitosa del backend: $it")
                    _saveSuccess.value = true
                }.onFailure {
                    Log.e("TratamientoViewModel", "Error del backend: ${it.message}", it)
                    _error.value = it.message ?: "Ocurrió un error desconocido"
                }

            } catch (e: Exception) {
                Log.e("TratamientoViewModel", "Error preparando la petición: ${e.message}", e)
                _error.value = "Error de formato en los datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSaveStatus() {
        _saveSuccess.value = false
        _error.value = null
    }

    // --- Operaciones de Base de Datos Local ---

    private fun cargarTratamientosLocales() {
        viewModelScope.launch {
            repository.obtenerTodosLosTratamientosConMedicamentos().collect { tratamientos ->
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
                Tratamiento(nombre = "tratamiento $nuevoNumero", fechaInicio = "", fechaConclusion = "")
            )
            _selectedTratamientoIndex.value = _tratamientosConMedicamentos.value.size
            _showFormulario.value = true
        }
    }

    fun eliminarTratamiento(tratamientoId: Int) {
        viewModelScope.launch {
            repository.eliminarTratamientoYMedicamentos(tratamientoId)
        }
    }

    fun actualizarFechaInicio(tratamientoId: Int, fecha: String) {
        viewModelScope.launch {
            val tratamiento = _tratamientosConMedicamentos.value
                .find { it.tratamiento.id == tratamientoId }
                ?.tratamiento

            tratamiento?.let {
                repository.actualizarTratamiento(it.copy(fechaInicio = fecha))
            }
        }
    }
}

class TratamientoViewModelFactory(
    private val repository: TratamientoRepository
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TratamientoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TratamientoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
