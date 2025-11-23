package com.proyecto.huellitas_callejeras.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.disk.DiskCache
import com.proyecto.huellitas_callejeras.data.DependencyProvider
import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.repository.AnimalRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


sealed class GaleriaNavTarget {
    object InicioSesion : GaleriaNavTarget()
    object CitasMedicas : GaleriaNavTarget()
    object NuevoExpediente : GaleriaNavTarget()
    data class Expediente(val patientId: String, val editable: Boolean) : GaleriaNavTarget()
    data class GoBackWithResult(val animal: Animal) : GaleriaNavTarget()
}

class GaleriaViewModel : ViewModel() {
    private val repository = DependencyProvider.animalRepository
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _allPatients = MutableStateFlow<List<Animal>>(emptyList())

    val patients: StateFlow<List<Animal>> =
        searchText.combine(_allPatients) { text, patients ->
            if (text.isBlank()) patients
            else patients.filter {
                it.nombre.contains(text, ignoreCase = true) ||
                        it.idAnimal.contains(text)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _navEvents = MutableSharedFlow<GaleriaNavTarget>()
    val navEvents = _navEvents.asSharedFlow()

    init {
        loadPatients()
    }

    private fun loadPatients() {
        viewModelScope.launch {
            try {
                _allPatients.value = repository.getAll()
            } catch (e: Exception) {
                println("ERROR cargando animales: ${e.message}")
            }
        }
    }

    fun removePatient(animal: Animal) {
        viewModelScope.launch {
            try {
                repository.delete(animal.idAnimal)
                loadPatients()
            } catch (e: Exception) {
                println("Error eliminando: ${e.message}")
            }
        }
    }

    // Navegación
    fun onHomeClicked() {
        viewModelScope.launch { _navEvents.emit(GaleriaNavTarget.InicioSesion) }
    }

    fun onCalendarClicked() {
        viewModelScope.launch { _navEvents.emit(GaleriaNavTarget.CitasMedicas) }
    }

    fun onBackClicked() {
        viewModelScope.launch { _navEvents.emit(GaleriaNavTarget.InicioSesion) }
    }

    fun onAddClicked() {
        viewModelScope.launch { _navEvents.emit(GaleriaNavTarget.NuevoExpediente) }
    }

    fun onPatientClicked(animal: Animal, from: String?) {
        viewModelScope.launch {
            if (from == "citas") {
                _navEvents.emit(GaleriaNavTarget.GoBackWithResult(animal))
            } else {
                val id = animal.idAnimal ?: return@launch
                _navEvents.emit(GaleriaNavTarget.Expediente(id, false))
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}
