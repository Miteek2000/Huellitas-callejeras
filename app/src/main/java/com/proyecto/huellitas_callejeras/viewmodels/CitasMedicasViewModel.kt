package com.proyecto.huellitas_callejeras.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.data.DependencyProvider
import com.proyecto.huellitas_callejeras.data.DependencyProvider.citasRepository
import com.proyecto.huellitas_callejeras.models.Cita
import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.remote.dto.CitaRequest
import com.proyecto.huellitas_callejeras.repository.CitasRepository
import com.proyecto.huellitas_callejeras.viewmodels.presentation.CitasUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


import kotlinx.coroutines.launch


class CitasMedicasViewModel : ViewModel() {
    private val repository = DependencyProvider.citasRepository
    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState = _uiState.asStateFlow()

    var citas = mutableStateListOf<Cita>()
        private set

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Estado para el animal seleccionado
    var selectedAnimal by mutableStateOf<Animal?>(null)
        private set

    init {
        cargarCitas()
    }

    fun cargarCitas() {
        viewModelScope.launch {
            loading = true
            errorMessage = null
            try {
                val lista = citasRepository.getCitas()
                citas.clear()
                citas.addAll(lista)
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                loading = false
            }
        }
    }


    fun onPatientSelected(animal: Animal) {
        selectedAnimal = animal
        // Aquí puedes agregar lógica adicional si es necesario
    }


    fun onPatientIconClicked(cita: Cita): String? {
        return if (cita.animalitoId.isNotEmpty()) {
            cita.animalitoId // Retorna el ID del animal para navegar
        } else {
            null
        }
    }

    fun getCitaPorId(id: String, onResult: (Cita?) -> Unit) {
        viewModelScope.launch {
            try {
                val c = citasRepository.getCita(id)
                onResult(c)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun crearCita(cita: Cita, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            loading = true
            try {
                val creada = citasRepository.createCita(cita)
                cargarCitas()
                onSuccess?.invoke()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                loading = false
            }
        }
    }

    fun editarCita(cita: Cita, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            loading = true
            try {
                citasRepository.updateCita(cita)
                cargarCitas()
                onSuccess?.invoke()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                loading = false
            }
        }
    }

    fun eliminarCita(id: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            loading = true
            try {
                citasRepository.deleteCita(id)
                cargarCitas()
                onSuccess?.invoke()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                loading = false
            }
        }
    }

    fun updateEditingCita(change: (Cita) -> Cita) {
        _uiState.update { it.copy(cita = change(it.cita!!)) }
    }

    // MÉTODO NUEVO - Para guardar cita
    fun saveCita() {
        val cita = _uiState.value.cita
        cita?.let {
            editarCita(it)
        }
    }
}

