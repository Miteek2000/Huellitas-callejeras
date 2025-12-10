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
import java.time.LocalDate
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

    // Nuevos estados para manejar edición y datos cargados
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _tratamientoCargado = MutableStateFlow<TratamientoCargado?>(null)
    val tratamientoCargado: StateFlow<TratamientoCargado?> = _tratamientoCargado.asStateFlow()

    private val _medicamentosCargados = MutableStateFlow<List<MedicamentoCargado>?>(null)
    val medicamentosCargados: StateFlow<List<MedicamentoCargado>?> = _medicamentosCargados.asStateFlow()

    private val _currentAnimalId = MutableStateFlow<String?>(null)
    val currentAnimalId: StateFlow<String?> = _currentAnimalId.asStateFlow()

    data class TratamientoCargado(
        val id: String,
        val fechaInicio: String,
        val recetaUrl: String?
    )

    data class MedicamentoCargado(
        val medicamentoId: String,
        val nombre: String,
        val dosis: Float,
        val repeticion: Float,
        val fechaConclusion: String?
    )

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

    suspend fun cargarTratamientoExistente(animalId: String) {
        _isLoading.value = true
        _error.value = null
        _currentAnimalId.value = animalId

        try {

            val tratamientoResult = tratamientoRepository.getTratamientoByAnimalId(animalId)

            tratamientoResult.onSuccess { tratamientoDto ->
                Log.d("TratamientoViewModel", "Tratamiento encontrado: ${tratamientoDto.id}")

                val urlCompleta = if (tratamientoDto.recetaUrl?.startsWith("/") == true) {
                    "http://34.195.100.95:8080${tratamientoDto.recetaUrl}"
                } else {
                    tratamientoDto.recetaUrl
                }

                _tratamientoCargado.value = TratamientoCargado(
                    id = tratamientoDto.id,
                    fechaInicio = tratamientoDto.fechaInicio,
                    recetaUrl = urlCompleta
                )

                val medicamentosResult = tratamientoRepository.getMedicamentosByTratamientoId(tratamientoDto.id)

                medicamentosResult.onSuccess { medicamentos ->
                    Log.d("TratamientoViewModel", "Medicamentos encontrados: ${medicamentos.size}")

                    _medicamentosCargados.value = medicamentos.map { dto ->
                        MedicamentoCargado(
                            medicamentoId = dto.medicamentoId,
                            nombre = dto.nombre,
                            dosis = dto.dosis,
                            repeticion = dto.repeticion,
                            fechaConclusion = dto.fechaConclusion
                        )
                    }

                    val medicamentosLocales = medicamentos.mapIndexed { index, dto ->
                        MedicamentoLocal(
                            id = index + 1, // IDs locales temporales
                            nombre = dto.nombre,
                            dosis = dto.dosis.toString(),
                            frecuencia = dto.repeticion.toString(),
                            fechaConclusion = dto.fechaConclusion ?: ""
                        )
                    }

                    _medicamentos.value = medicamentosLocales

                }.onFailure { error ->
                    _error.value = "Error cargando medicamentos: ${error.message}"
                }

            }.onFailure { error ->

                Log.d("TratamientoViewModel", "No hay tratamiento existente para este animal")
                _tratamientoCargado.value = null
                _medicamentosCargados.value = null
            }

        } catch (e: Exception) {
            Log.e("TratamientoViewModel", "Error cargando tratamiento: ${e.message}")
            _error.value = "Error cargando tratamiento: ${e.message}"
        } finally {
            _isLoading.value = false
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun actualizarTratamiento(
        animalId: String, // Ahora recibe animalId como parámetro
        fechaInicio: String,
        medicamentosLocales: List<MedicamentoLocal>,
        recetaUri: Uri? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false

            val tratamientoId = _tratamientoCargado.value?.id

            if (tratamientoId == null) {
                _error.value = "No hay tratamiento para actualizar"
                _isLoading.value = false
                return@launch
            }

            if (animalId.isBlank()) {
                _error.value = "ID del animal no disponible"
                _isLoading.value = false
                return@launch
            }

            try {
                if (fechaInicio.isBlank()) {
                    _error.value = "Fecha de inicio inválida o vacía"
                    _isLoading.value = false
                    return@launch
                }

                if (medicamentosLocales.isEmpty()) {
                    _error.value = "Debes agregar al menos un medicamento"
                    _isLoading.value = false
                    return@launch
                }

                val medicamentosConId = mutableListOf<MedicamentoTratamientoDto>()

                for (medicamentoLocal in medicamentosLocales) {
                    Log.d("TratamientoViewModel", "Actualizando medicamento: ${medicamentoLocal.nombre}")

                    val medicamentoRequest = MedicamentoRequest(
                        nombre = medicamentoLocal.nombre
                    )

                    val resultadoMedicamento = medicamentoRepository.createMedicamento(medicamentoRequest)

                    resultadoMedicamento.onSuccess { medicamentoDTO ->
                        val dosis = medicamentoLocal.dosis.toFloatOrNull() ?: 0f
                        val repeticion = medicamentoLocal.frecuencia.toFloatOrNull() ?: 0f

                        medicamentosConId.add(
                            MedicamentoTratamientoDto(
                                medicamentoId = medicamentoDTO.id,
                                nombre = medicamentoDTO.nombre,
                                dosis = dosis,
                                repeticion = repeticion,
                                fechaConclusion = medicamentoLocal.fechaConclusion
                            )
                        )
                    }.onFailure { error ->
                        Log.e("TratamientoViewModel", "Error creando medicamento para actualización: ${error.message}")
                        throw error
                    }
                }

                val tratamientoRequest = TratamientoCreateRequestDto(
                    animalId = animalId,
                    fechaInicio = fechaInicio,
                    medicamentos = medicamentosConId
                )

                val resultado = tratamientoRepository.updateTratamiento(
                    tratamientoId,
                    tratamientoRequest,
                    recetaUri
                )

                resultado.onSuccess {
                    Log.d("TratamientoViewModel", "Tratamiento actualizado exitosamente")
                    _saveSuccess.value = true
                    _isEditing.value = false
                }.onFailure { error ->
                    Log.e("TratamientoViewModel", "Error actualizando tratamiento: ${error.message}")
                    throw error
                }

            } catch (e: Exception) {
                Log.e("TratamientoViewModel", "Error al actualizar: ${e.message}", e)
                _error.value = "Error al actualizar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleModoEdicion() {
        _isEditing.value = !_isEditing.value
    }

    fun setModoEdicion(editando: Boolean) {
        _isEditing.value = editando
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

    fun limpiarTodo() {
        _medicamentos.value = emptyList()
        _tratamientoCargado.value = null
        _medicamentosCargados.value = null
        _isEditing.value = false
        _recetaUri.value = null
        _currentAnimalId.value = null
    }

    fun getCurrentAnimalId(): String? {
        return _currentAnimalId.value
    }
}