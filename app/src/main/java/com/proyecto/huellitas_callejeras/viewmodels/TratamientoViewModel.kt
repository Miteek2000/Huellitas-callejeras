package com.proyecto.huellitas_callejeras.viewmodel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.data.DependencyProvider
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoRequest
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoTratamientoDto
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoCreateRequestDto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class MedicamentoLocal(
    val id: Int = 0,
    val nombre: String = "",
    val dosis: String = "",
    val frecuencia: String = "",
    val fechaConclusion: String = ""
)

class TratamientoViewModel : ViewModel() {

    private val tratamientoRepository = DependencyProvider.tratamientoRepository
    private val medicamentoRepository = DependencyProvider.medicamentoRepository


    private val _medicamentos = MutableStateFlow<List<MedicamentoLocal>>(emptyList())
    val medicamentos: StateFlow<List<MedicamentoLocal>> = _medicamentos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _recetaUri = MutableStateFlow<Uri?>(null)
    val recetaUri: StateFlow<Uri?> = _recetaUri.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parsearFecha(fechaStr: String): String {
        return try {

            if (fechaStr.isBlank()) {
                ""
            } else {

                val fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE)
                fecha.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
        } catch (e: Exception) {
            Log.e("TratamientoViewModel", "Error parseando fecha: $fechaStr", e)
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun guardarTratamientoRemoto(
        animalId: String,
        fechaInicio: String,
        medicamentosLocales: List<MedicamentoLocal>,
        recetaUri: Uri? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false

            try {
                Log.d("TratamientoViewModel", "=== INICIANDO FLUJO DE GUARDADO ===")

                if (medicamentosLocales.isEmpty()) {
                    _error.value = "Debes agregar al menos un medicamento"
                    _isLoading.value = false
                    return@launch
                }

                if (fechaInicio.isBlank()) {
                    _error.value = "Fecha de inicio inválida o vacía"
                    _isLoading.value = false
                    return@launch
                }

                val medicamentosConId = mutableListOf<MedicamentoTratamientoDto>()

                for (medicamentoLocal in medicamentosLocales) {
                    Log.d("TratamientoViewModel", "Creando medicamento: ${medicamentoLocal.nombre}")

                    val medicamentoRequest = MedicamentoRequest(
                        nombre = medicamentoLocal.nombre
                    )

                    val resultadoMedicamento = medicamentoRepository.createMedicamento(medicamentoRequest)

                    resultadoMedicamento.onSuccess { medicamentoDTO ->
                        Log.d("TratamientoViewModel", "Medicamento creado con ID: ${medicamentoDTO.id}")

                        val fechaConclusion = medicamentoLocal.fechaConclusion

                        val dosis = medicamentoLocal.dosis.toFloatOrNull() ?: 0f
                        val repeticion = medicamentoLocal.frecuencia.toFloatOrNull() ?: 0f

                        medicamentosConId.add(
                            MedicamentoTratamientoDto(
                                medicamentoId = medicamentoDTO.id,
                                nombre = medicamentoDTO.nombre,
                                dosis = dosis,
                                repeticion = repeticion,
                                fechaConclusion = fechaConclusion
                            )
                        )
                        Log.d("TratamientoViewModel", "Medicamento preparado para tratamiento: ${medicamentoDTO.nombre} - Fecha conclusión: $fechaConclusion")
                    }.onFailure { error ->
                        Log.e("TratamientoViewModel", "Error creando medicamento: ${error.message}")
                        throw error
                    }
                }

                Log.d("TratamientoViewModel", "Todos los medicamentos creados: ${medicamentosConId.size}")


                val tratamientoRequest = TratamientoCreateRequestDto(
                    animalId = animalId,
                    fechaInicio = fechaInicio,
                    medicamentos = medicamentosConId
                )

                Log.d("TratamientoViewModel", "Creando tratamiento con request:")
                Log.d("TratamientoViewModel", "- Animal ID: $animalId")
                Log.d("TratamientoViewModel", "- Fecha inicio: $fechaInicio")
                Log.d("TratamientoViewModel", "- Medicamentos: ${medicamentosConId.size}")
                Log.d("TratamientoViewModel", "- Con imagen: ${recetaUri != null}")

                val resultadoTratamiento = tratamientoRepository.createTratamiento(
                    tratamientoRequest,
                    recetaUri
                )

                resultadoTratamiento.onSuccess { tratamientoDto ->
                    Log.d("TratamientoViewModel", "Tratamiento creado exitosamente: ${tratamientoDto.id}")
                    _saveSuccess.value = true
                    _recetaUri.value = null
                    _medicamentos.value = emptyList()
                }.onFailure { error ->
                    Log.e("TratamientoViewModel", "Error creando tratamiento: ${error.message}")
                    throw error
                }

            } catch (e: Exception) {
                Log.e("TratamientoViewModel", "Error en el flujo completo: ${e.message}", e)
                _error.value = "Error al guardar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun agregarMedicamento(medicamento: MedicamentoLocal) {
        val nuevoId = (_medicamentos.value.maxOfOrNull { it.id } ?: 0) + 1
        _medicamentos.value = _medicamentos.value + medicamento.copy(id = nuevoId)
    }

    fun actualizarMedicamento(medicamento: MedicamentoLocal) {
        _medicamentos.value = _medicamentos.value.map {
            if (it.id == medicamento.id) medicamento else it
        }
    }

    fun eliminarMedicamento(medicamentoId: Int) {
        _medicamentos.value = _medicamentos.value.filter { it.id != medicamentoId }
    }

    fun obtenerMedicamentoPorId(id: Int): MedicamentoLocal? {
        return _medicamentos.value.find { it.id == id }
    }

    fun limpiarMedicamentos() {
        _medicamentos.value = emptyList()
    }

    fun setRecetaUri(uri: Uri?) {
        _recetaUri.value = uri
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun resetSaveStatus() {
        _saveSuccess.value = false
        _error.value = null
    }
} 