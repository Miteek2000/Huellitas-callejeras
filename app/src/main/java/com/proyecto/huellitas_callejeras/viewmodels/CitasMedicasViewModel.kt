package com.proyecto.huellitas_callejeras.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.models.Cita
import com.proyecto.huellitas_callejeras.models.Patient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

private var allAppointments = mutableListOf(
    Cita(
        id = 1,
        title = "Vacunación Puppy",
        date = "25/07/2024",
        place = "Veterinaria 'El Roble'",
        realizationDate = "",
        patientName = "Puppy",
        patientId = 1
    ),
    Cita(
        id = 2,
        title = "Chequeo general",
        date = "25/07/2024",
        place = "Mi Casa",
        realizationDate = "",
        patientName = "Manchas",
        patientId = 2
    ),
    Cita(
        id = 3,
        title = "Desparasitación",
        date = "28/07/2024",
        place = "Veterinaria 'El Roble'",
        realizationDate = "",
        patientName = "Luna",
        patientId = 3
    ),
)

data class CitasUiState(
    val calendar: Calendar = Calendar.getInstance(),
    val appointmentsForSelectedDate: List<Cita> = emptyList(),
    val datesWithAppointments: Set<String> = emptySet(),
    val cita: Cita? = null,
    val titleError: Boolean = false,
    val dateError: Boolean = false,
    val placeError: Boolean = false,
    val motiveError: Boolean = false
)

sealed class CitasNavTarget {
    data object Galeria : CitasNavTarget()
    data object CitasMedicas : CitasNavTarget()
    data object EditarCita : CitasNavTarget()
    data class Expediente(val patientId: Int) : CitasNavTarget()
    data class GaleriaParaSeleccion(val from: String) : CitasNavTarget()
}

class CitasMedicasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState = _uiState.asStateFlow()

    var showDeleteConfirmationDialog by mutableStateOf(false)
        private set

    private var citaToDelete: Cita? = null

    private val _navEvents = MutableSharedFlow<CitasNavTarget>()
    val navEvents = _navEvents.asSharedFlow()

    init {
        onDateSelected(System.currentTimeMillis())
        updateDatesWithAppointments()
    }

    private fun updateDatesWithAppointments() {
        val dates = allAppointments.map { it.date }.toSet()
        _uiState.update { it.copy(datesWithAppointments = dates) }
    }

    fun onDateSelected(dateMillis: Long?) {
        if (dateMillis == null) return

        val formattedDate = formatDate(dateMillis)
        val appointmentsForDay = allAppointments.filter { it.date == formattedDate }

        _uiState.update { currentState ->
            val newCalendar = currentState.calendar.apply { timeInMillis = dateMillis }
            currentState.copy(
                calendar = newCalendar,
                appointmentsForSelectedDate = appointmentsForDay
            )
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(cita = it.cita?.copy(title = title), titleError = false) }
    }

    fun onDateChange(date: String) {
        _uiState.update { it.copy(cita = it.cita?.copy(date = date), dateError = false) }
    }

    fun onPlaceChange(place: String) {
        _uiState.update { it.copy(cita = it.cita?.copy(place = place), placeError = false) }
    }

    fun onMotiveChange(motive: String) {
        _uiState.update { it.copy(cita = it.cita?.copy(motive = motive), motiveError = false) }
    }

    fun saveCita(onSuccess: () -> Unit) {
        val citaToSave = _uiState.value.cita ?: return

        val titleError = citaToSave.title.isBlank()
        val dateError = citaToSave.date.isBlank()
        val placeError = citaToSave.place.isBlank()
        val motiveError = citaToSave.motive.isNullOrBlank()

        val hasError = titleError || dateError || placeError || motiveError

        if (!hasError) {
            val index = allAppointments.indexOfFirst { it.id == citaToSave.id }
            if (index != -1) {
                allAppointments[index] = citaToSave
            } else {
                allAppointments.add(citaToSave)
            }
            val dateMillis = try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(citaToSave.date)?.time
            } catch (e: Exception) {
                _uiState.value.calendar.timeInMillis
            }
            onDateSelected(dateMillis)
            updateDatesWithAppointments()
            _uiState.update { it.copy(titleError = false, dateError = false, placeError = false, motiveError = false) }
            onSuccess()
        } else {
            _uiState.update { it.copy(titleError = titleError, dateError = dateError, placeError = placeError, motiveError = motiveError) }
        }
    }

    private fun onEditCita(cita: Cita) {
        _uiState.update { it.copy(cita = cita) }
    }

    private fun onAddNewCita() {
        val newCita = Cita(
            id = Random.nextInt(),
            title = "",
            date = formatDate(_uiState.value.calendar.timeInMillis),
            place = "",
            realizationDate = "",
            patientName = "",
            motive = ""
        )
        _uiState.update { it.copy(cita = newCita) }
    }

    fun onDeleteCitaRequested(cita: Cita) {
        citaToDelete = cita
        showDeleteConfirmationDialog = true
    }

    fun onDeleteCitaConfirmed() {
        citaToDelete?.let {
            allAppointments.removeAll { c -> c.id == it.id }
            onDateSelected(_uiState.value.calendar.timeInMillis)
            updateDatesWithAppointments()
        }
        showDeleteConfirmationDialog = false
        citaToDelete = null
    }

    fun onDeletionCancelled() {
        showDeleteConfirmationDialog = false
        citaToDelete = null
    }

    fun onPatientSelected(patient: Patient) {
        _uiState.value.cita?.let { currentCita ->
            val updatedCita = currentCita.copy(patientId = patient.id, patientName = patient.name)
            val index = allAppointments.indexOfFirst { it.id == updatedCita.id }
            if (index != -1) {
                allAppointments[index] = updatedCita
            }
            onDateSelected(_uiState.value.calendar.timeInMillis)
            _uiState.update { it.copy(cita = updatedCita) }
        }
    }

    private fun formatDate(dateMillis: Long?): String {
        if (dateMillis == null) return ""
        val date = Date(dateMillis)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }

    fun onMonthChanged(forward: Boolean) {
        _uiState.update { currentState ->
            val newCalendar = currentState.calendar.clone() as Calendar
            newCalendar.add(Calendar.MONTH, if (forward) 1 else -1)
            currentState.copy(calendar = newCalendar)
        }
    }

    fun onYearChanged(year: Int) {
        _uiState.update { currentState ->
            val newCalendar = currentState.calendar.clone() as Calendar
            newCalendar.set(Calendar.YEAR, year)
            currentState.copy(calendar = newCalendar)
        }
    }

    // Navigation triggers
    fun onHomeClicked() {
        viewModelScope.launch { _navEvents.emit(CitasNavTarget.Galeria) }
    }

    fun onCalendarClicked() {
        viewModelScope.launch { _navEvents.emit(CitasNavTarget.CitasMedicas) }
    }

    fun onAddNewCitaClicked() {
        onAddNewCita()
        viewModelScope.launch { _navEvents.emit(CitasNavTarget.EditarCita) }
    }

    fun onEditCitaClicked(cita: Cita) {
        onEditCita(cita)
        viewModelScope.launch { _navEvents.emit(CitasNavTarget.EditarCita) }
    }

    fun onPatientIconClicked(cita: Cita) {
        viewModelScope.launch {
            if (cita.patientId != null) {
                _navEvents.emit(CitasNavTarget.Expediente(cita.patientId))
            } else {
                // Store the cita being modified before navigating
                _uiState.update { it.copy(cita = cita) }
                _navEvents.emit(CitasNavTarget.GaleriaParaSeleccion("citas"))
            }
        }
    }
}
