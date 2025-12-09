package com.proyecto.huellitas_callejeras.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.data.DependencyProvider
import com.proyecto.huellitas_callejeras.remote.AuthService
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

class ExpedienteViewModel : ViewModel() {
    private val repository: AnimalRepository = DependencyProvider.animalRepository
    private val authService: AuthService = DependencyProvider.authService
    private val _uiState = MutableStateFlow(ExpedienteUiState())
    val uiState = _uiState.asStateFlow()

    fun init(patientId: String?, editable: Boolean) {
        viewModelScope.launch {
            val rescatistaId = authService.getRescatistaId() ?: ""

            if (patientId == null) {
                _uiState.update { it.copy(isEditing = editable, rescatistaId = rescatistaId) }
                return@launch
            }

            _uiState.update { it.copy(loading = true, error = null) }

            try {
                val response = repository.getAnimalitoConRescate(patientId)

                if (response.success && response.data != null) {
                    val animalito = response.data.animal
                    val rescate = response.data.rescate

                    if (animalito == null) {
                        _uiState.update { it.copy(loading = false, error = "Animalito no encontrado") }
                        return@launch
                    }

                    if (rescate == null) {
                        _uiState.update { it.copy(loading = false, error = "Datos de rescate no encontrados") }
                        return@launch
                    }

                    val urlImagen: String = when {
                        animalito.urlImage?.startsWith("http") == true -> animalito.urlImage!!
                        animalito.urlImage?.startsWith("/") == true -> "http://34.195.100.95:8080${animalito.urlImage}"
                        else -> ""
                    }

                    _uiState.update {
                        it.copy(
                            loading = false,
                            id = animalito.id,
                            nombre = animalito.nombre,
                            especie = animalito.especie ?: "",
                            raza = animalito.raza ?: "",
                            edad = animalito.edad?.toString() ?: "",
                            sexo = animalito.sexo ?: "",
                            peso = animalito.peso?.toString() ?: "",
                            fechaSalida = animalito.fechaSalida ?: "",
                            estadoSelected = animalito.estado ?: "En adopción",
                            urlImagen = urlImagen,
                            selectedImageUri = if (urlImagen.isNotBlank()) {
                                try {
                                    Uri.parse(urlImagen)
                                } catch (e: Exception) {
                                    null
                                }
                            } else null,
                            lugarRescate = rescate.lugar,
                            condicionesRescate = rescate.descripcion,
                            fechaIngreso = rescate.fechaIngreso,
                            rescatistaId = animalito.rescatistaId ?: rescatistaId,
                            isEditing = editable
                        )
                    }
                } else {
                    val errorMsg = response.message ?: "Error al cargar datos"
                    _uiState.update { it.copy(loading = false, error = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun saveAnimalConRescate(onSuccess: () -> Unit = {}) {
        val state = uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }

            val rescatistaId = if (state.rescatistaId.isBlank()) {
                authService.getRescatistaId() ?: ""
            } else {
                state.rescatistaId
            }

            if (rescatistaId.isBlank()) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "No se pudo identificar al rescatista. Por favor, inicia sesión nuevamente."
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
                rescatistaId = rescatistaId
            )

            val rescateRequest = RescateRequest(
                lugar = state.lugarRescate,
                descripcion = state.condicionesRescate
            )

            try {
                val response = if (state.id == null) {
                    repository.createConRescate(
                        animalRequest,
                        rescateRequest,
                        state.selectedImageUri
                    )
                } else {
                    repository.updateConRescate(
                        state.id,
                        animalRequest,
                        rescateRequest,
                        state.selectedImageUri
                    )
                }

                if (response.success) {
                    _uiState.update { it.copy(loading = false, success = true) }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(loading = false, error = response.message ?: "Error al guardar") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message ?: "Error de conexión") }
            }
        }
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

        val nombreError = currentState.nombre.isBlank()
        val especieError = currentState.especie.isBlank()
        val edadError = currentState.edad.isBlank() || currentState.edad.toIntOrNull() == null
        val sexoError = currentState.sexo.isBlank()
        val pesoError = currentState.peso.isBlank() || currentState.peso.toDoubleOrNull() == null
        val lugarRescateError = currentState.lugarRescate.isBlank()
        val condicionesRescateError = currentState.condicionesRescate.isBlank()

        val hasError = nombreError || especieError || edadError || sexoError ||
                pesoError || lugarRescateError || condicionesRescateError

        if (!hasError) {
            saveAnimalConRescate(onSuccess)

            _uiState.update { it.copy(
                nombreError = false,
                especieError = false,
                edadError = false,
                sexoError = false,
                pesoError = false,
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
                lugarRescateError = lugarRescateError,
                condicionesRescateError = condicionesRescateError
            ) }
        }
    }
}

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
    val rescatistaId: String = "",
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