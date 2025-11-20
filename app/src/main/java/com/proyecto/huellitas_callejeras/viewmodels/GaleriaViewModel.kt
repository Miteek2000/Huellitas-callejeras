package com.proyecto.huellitas_callejeras.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.models.PatientRepository
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
    data class Expediente(val patientId: Int) : GaleriaNavTarget()
    data class GoBackWithResult(val animal: Animal) : GaleriaNavTarget()
}


class GaleriaViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _allPatients = PatientRepository.patients

    val patients: StateFlow<List<Animal>> = searchText
        .combine(_allPatients) { text, patients ->
            if (text.isBlank()) {
                patients
            } else {
                patients.filter {
                    it.name.contains(text, ignoreCase = true) || it.id.toString().contains(text)
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _allPatients.value
        )

    private val _navEvents = MutableSharedFlow<GaleriaNavTarget>()
    val navEvents = _navEvents.asSharedFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun removePatient(animal: Animal) {
        PatientRepository.removePatient(animal.id)
    }

    // Navigation triggers
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
                _navEvents.emit(GaleriaNavTarget.Expediente(animal.id))
            }
        }
    }
}
