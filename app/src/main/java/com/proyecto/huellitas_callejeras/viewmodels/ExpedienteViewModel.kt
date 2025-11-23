package com.proyecto.huellitas_callejeras.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.remote.dto.AnimalDto
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRequest
import com.proyecto.huellitas_callejeras.remote.dto.RescateRequest
import com.proyecto.huellitas_callejeras.repository.AnimalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.proyecto.huellitas_callejeras.data.DependencyProvider




class ExpedienteViewModel : ViewModel() {
    private val repository: AnimalRepository = DependencyProvider.animalRepository
    private val _uiState = MutableStateFlow(ExpedienteUiState())
    val uiState = _uiState.asStateFlow()

    fun init(patientId: String?, editable: Boolean) {
        println("DEBUG -> init llamado con patientId: $patientId, editable: $editable")

        if (patientId == null) {
            println("DEBUG -> patientId es null, modo creación")
            _uiState.update { it.copy(isEditing = editable) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }

            try {
                println("DEBUG -> Llamando a repository.getAnimalitoConRescate($patientId)")
                val response = repository.getAnimalitoConRescate(patientId)

                println("DEBUG -> Response recibida: success=${response.success}")
                println("DEBUG -> Response data: ${response.data}")
                println("DEBUG -> Response message: ${response.message}")

                if (response.success && response.data != null) {
                    val animalito = response.data.animal
                    val rescate = response.data.rescate

                    println("DEBUG -> Animalito: $animalito")
                    println("DEBUG -> Rescate: $rescate")

                    // Verificar que los datos no sean null
                    if (animalito == null) {
                        println("DEBUG -> ERROR: animalito es null")
                        _uiState.update { it.copy(loading = false, error = "Animalito no encontrado") }
                        return@launch
                    }

                    if (rescate == null) {
                        println("DEBUG -> ERROR: rescate es null")
                        _uiState.update { it.copy(loading = false, error = "Datos de rescate no encontrados") }
                        return@launch
                    }

                    println("DEBUG -> Cargando datos en UI State:")
                    println("  - ID: ${animalito.id}")
                    println("  - Nombre: ${animalito.nombre}")
                    println("  - Especie: ${animalito.especie}")
                    println("  - Raza: ${animalito.raza}")
                    println("  - Edad: ${animalito.edad}")
                    println("  - Sexo: ${animalito.sexo}")
                    println("  - Peso: ${animalito.peso}")
                    println("  - Estado: ${animalito.estado}")
                    println("  - URL Imagen: ${animalito.urlImage}")
                    println("  - Lugar Rescate: ${rescate.lugar}")
                    println("  - Condiciones: ${rescate.descripcion}")
                    println("  - Fecha Ingreso: ${rescate.fechaIngreso}")

                    _uiState.update {
                        it.copy(
                            loading = false,
                            id = animalito.id,
                            nombre = animalito.nombre,
                            especie = animalito.especie,
                            raza = animalito.raza ?: "",
                            edad = animalito.edad.toString(),
                            sexo = animalito.sexo,
                            peso = animalito.peso.toString(),
                            fechaSalida = animalito.fechaSalida ?: "",
                            estadoSelected = animalito.estado,
                            urlImagen = animalito.urlImage,
                            selectedImageUri = if (animalito.urlImage.isNotEmpty() && animalito.urlImage != "null") {
                                try {
                                    Uri.parse(animalito.urlImage)
                                } catch (e: Exception) {
                                    println("DEBUG -> Error parseando URI: ${e.message}")
                                    null
                                }
                            } else null,
                            lugarRescate = rescate.lugar,
                            condicionesRescate = rescate.descripcion,
                            fechaIngreso = rescate.fechaIngreso,
                            isEditing = editable
                        )
                    }

                    println("DEBUG -> UI State actualizado correctamente")

                } else {
                    val errorMsg = response.message ?: "Error al cargar datos"
                    println("DEBUG -> Error en response: $errorMsg")
                    _uiState.update { it.copy(loading = false, error = errorMsg) }
                }

            } catch (e: Exception) {
                println("DEBUG -> Excepción en init: ${e.message}")
                println("DEBUG -> Stack trace: ${e.stackTraceToString()}")
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun saveAnimalConRescate(onSuccess: () -> Unit = {}) {
        val state = uiState.value

        println("DEBUG -> saveAnimalConRescate llamado")
        println("DEBUG -> Estado actual:")
        println("  - ID: ${state.id}")
        println("  - Nombre: ${state.nombre}")
        println("  - Especie: ${state.especie}")
        println("  - Raza: ${state.raza}")
        println("  - Edad: ${state.edad}")
        println("  - Sexo: ${state.sexo}")
        println("  - Peso: ${state.peso}")
        println("  - Estado: ${state.estadoSelected}")
        println("  - URL Imagen: ${state.urlImagen}")
        println("  - Selected Image URI: ${state.selectedImageUri}")
        println("  - Lugar Rescate: ${state.lugarRescate}")
        println("  - Condiciones: ${state.condicionesRescate}")
        println("  - Fecha Ingreso: ${state.fechaIngreso}")

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }


            val fechaIngreso = state.fechaIngreso
            if (fechaIngreso.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "La fecha de ingreso es requerida",
                        fechaIngresoError = true
                    )
                }
                return@launch
            }

            val animalRequest = AnimalRequest(
                nombre = state.nombre,
                especie = state.especie,
                raza = state.raza,
                edad = state.edad.toIntOrNull() ?: 0,
                sexo = state.sexo,
                peso = state.peso.toDoubleOrNull() ?: 0.0,
                estado = state.estadoSelected,
                urlImage = state.selectedImageUri?.toString() ?: state.urlImagen
            )

            val rescateRequest = RescateRequest(
                fechaIngreso = fechaIngreso,
                lugar = state.lugarRescate,
                descripcion = state.condicionesRescate
            )

            println("DEBUG -> AnimalRequest: $animalRequest")
            println("DEBUG -> RescateRequest: $rescateRequest")

            try {
                val response = if (state.id == null) {
                    println("DEBUG -> Creando nuevo animal")
                    repository.createConRescate(animalRequest, rescateRequest)
                } else {
                    println("DEBUG -> Actualizando animal existente con ID: ${state.id}")
                    repository.updateConRescate(state.id, animalRequest, rescateRequest)
                }

                println("DEBUG -> Response del guardado: success=${response.success}")
                println("DEBUG -> Response message: ${response.message}")
                println("DEBUG -> Response data: ${response.data}")

                if (response.success) {
                    println("DEBUG -> Guardado exitoso")
                    _uiState.update { it.copy(loading = false, success = true) }
                    onSuccess()
                } else {
                    println("DEBUG -> Error en guardado: ${response.message}")
                    _uiState.update { it.copy(loading = false, error = response.message ?: "Error al guardar") }
                }
            } catch (e: Exception) {
                println("DEBUG -> Excepción en saveAnimalConRescate: ${e.message}")
                println("DEBUG -> Stack trace: ${e.stackTraceToString()}")
                _uiState.update { it.copy(loading = false, error = e.message ?: "Error de conexión") }
            }
        }
    }

    // ... (todos los demás métodos se mantienen igual)
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

    fun onFechaSalidaChange(fecha: String) {
        _uiState.update { it.copy(fechaSalida = fecha) }
    }

    fun onEstadoSelected(estado: String) {
        _uiState.update { it.copy(estadoSelected = estado) }
    }

    fun onEstadoExpandedChange(expanded: Boolean) {
        _uiState.update { it.copy(estadoExpanded = expanded) }
    }

    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun setEditing(editing: Boolean) {
        _uiState.update { it.copy(isEditing = editing) }
    }

    fun onLugarRescateChange(lugar: String) {
        _uiState.update { it.copy(lugarRescate = lugar) }
    }

    fun onCondicionesRescateChange(condiciones: String) {
        _uiState.update { it.copy(condicionesRescate = condiciones) }
    }

    fun onFechaIngresoChange(fecha: String) {
        _uiState.update { it.copy(fechaIngreso = fecha) }
    }

    fun onShowDatePickerDialog(forField: String?) {
        _uiState.update { it.copy(showDatePickerDialogFor = forField) }
    }

    // En onDateSelected, modifica para incluir la hora:
    fun onDateSelected(dateMillis: Long?) {
        val dateString = if (dateMillis != null) {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(Date(dateMillis))
        } else {
            ""
        }

        _uiState.update { state ->
            when (state.showDatePickerDialogFor) {
                "ingreso" -> state.copy(fechaIngreso = dateString)
                "salida" -> state.copy(fechaSalida = dateString)
                else -> state
            }.copy(showDatePickerDialogFor = null)
        }
    }

    fun savePatient(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Validaciones (puedes ajustar según tus requisitos)
        val nombreError = currentState.nombre.isBlank()
        val especieError = currentState.especie.isBlank()
        val edadError = currentState.edad.isBlank() || currentState.edad.toIntOrNull() == null
        val sexoError = currentState.sexo.isBlank()
        val pesoError = currentState.peso.isBlank() || currentState.peso.toDoubleOrNull() == null
        val fechaIngresoError = currentState.fechaIngreso.isNullOrBlank()
        val lugarRescateError = currentState.lugarRescate.isBlank()
        val condicionesRescateError = currentState.condicionesRescate.isBlank()

        val hasError = nombreError || especieError || edadError || sexoError ||
                pesoError || fechaIngresoError || lugarRescateError || condicionesRescateError

        if (!hasError) {
            // En lugar de crear un Patient, llamamos al método que ya tienes para la API
            saveAnimalConRescate(onSuccess)

            // Actualizar estado de errores
            _uiState.update { it.copy(
                nombreError = false,
                especieError = false,
                edadError = false,
                sexoError = false,
                pesoError = false,
                fechaIngresoError = false,
                lugarRescateError = false,
                condicionesRescateError = false
            ) }
        } else {
            _uiState.update { it.copy(
                nombreError = nombreError,
                especieError = especieError,
                edadError = edadError,
                sexoError = sexoError,
                pesoError = pesoError,
                fechaIngresoError = fechaIngresoError,
                lugarRescateError = lugarRescateError,
                condicionesRescateError = condicionesRescateError
            ) }
        }
    }

}

// Data class se mantiene igual
data class ExpedienteUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val id: String? = null,
    val nombre: String = "",
    val especie: String = "",
    val raza: String = "",
    val edad: String = "",
    val sexo: String = "",
    val peso: String = "",
    val fechaSalida: String? = null,
    val lugarRescate: String = "",
    val condicionesRescate: String = "",
    val fechaIngreso: String? = null,
    val estadoSelected: String = "En adopción",
    val urlImagen: String = "",
    val selectedImageUri: Uri? = null,
    val isEditing: Boolean = false,
    val estadoExpanded: Boolean = false,
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
    val lugarRescateError: Boolean = false,
    val condicionesRescateError: Boolean = false,
    val fechaIngresoError: Boolean = false
)