package com.proyecto.huellitas_callejeras.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.data.model.Medicamento
import com.proyecto.huellitas_callejeras.data.model.Tratamiento
import com.proyecto.huellitas_callejeras.data.model.TratamientoConMedicamentos
import com.proyecto.huellitas_callejeras.data.remote.dto.MedicamentoInTratamientoDto
import com.proyecto.huellitas_callejeras.data.remote.dto.NuevoMedicamentoResult
import com.proyecto.huellitas_callejeras.data.remote.dto.TratamientoCreateRequestDto
import com.proyecto.huellitas_callejeras.data.repository.TratamientoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

class TratamientoViewModel(
    private val repository: TratamientoRepository
) : ViewModel() {

    private val _tratamientosConMedicamentos = MutableStateFlow<List<TratamientoConMedicamentos>>(emptyList())
    val tratamientosConMedicamentos: StateFlow<List<TratamientoConMedicamentos>> = _tratamientosConMedicamentos.asStateFlow()

    private val _selectedTratamientoIndex = MutableStateFlow(0)
    val selectedTratamientoIndex: StateFlow<Int> = _selectedTratamientoIndex.asStateFlow()

    private val _showFormulario = MutableStateFlow(false)
    val showFormulario: StateFlow<Boolean> = _showFormulario.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

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
                    // Podríamos re-cargar los tratamientos desde la API aquí si es necesario
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarMedicamentoLocal(medicamentoResult: NuevoMedicamentoResult) {
        val tratamientoActual = _tratamientosConMedicamentos.value.getOrNull(_selectedTratamientoIndex.value)
        if (tratamientoActual != null) {
            val nuevoMedicamento = Medicamento(
                tratamientoId = tratamientoActual.tratamiento.id,
                nombre = medicamentoResult.nombre,
                fechaInicio = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                fechaConclusion = "",
                dosis = medicamentoResult.dosis,
                frecuencia = medicamentoResult.frecuencia,
                idApi = UUID.fromString(medicamentoResult.id)
            )

            val medicamentosActualizados = tratamientoActual.medicamentos + nuevoMedicamento
            val tratamientoActualizado = tratamientoActual.copy(medicamentos = medicamentosActualizados)

            val listaActualizada = _tratamientosConMedicamentos.value.toMutableList()
            listaActualizada[_selectedTratamientoIndex.value] = tratamientoActualizado
            _tratamientosConMedicamentos.value = listaActualizada
        }
    }

    fun resetSaveStatus() {
        _saveSuccess.value = false
        _error.value = null
    }

    fun seleccionarTratamiento(index: Int) {
        _selectedTratamientoIndex.value = index
        _showFormulario.value = false
    }

    fun mostrarFormulario(mostrar: Boolean) {
        _showFormulario.value = mostrar
    }

    fun eliminarTratamiento(tratamientoId: Int) {
        val updatedList = _tratamientosConMedicamentos.value.filterNot { it.tratamiento.id == tratamientoId }
        _tratamientosConMedicamentos.value = updatedList
        if (_selectedTratamientoIndex.value >= updatedList.size && updatedList.isNotEmpty()) {
            _selectedTratamientoIndex.value = updatedList.size - 1
        } else if (updatedList.isEmpty()) {
            _selectedTratamientoIndex.value = 0
        }
    }

    fun agregarNuevoTratamiento() {
       val nuevoNumero = _tratamientosConMedicamentos.value.size + 1
       val nuevoTratamiento = TratamientoConMedicamentos(
           tratamiento = Tratamiento(id = (0..Int.MAX_VALUE).random(), nombre = "tratamiento $nuevoNumero", fechaInicio = "", fechaConclusion = ""),
           medicamentos = emptyList()
       )
        _tratamientosConMedicamentos.value += nuevoTratamiento
       _selectedTratamientoIndex.value = _tratamientosConMedicamentos.value.lastIndex
       _showFormulario.value = true
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
