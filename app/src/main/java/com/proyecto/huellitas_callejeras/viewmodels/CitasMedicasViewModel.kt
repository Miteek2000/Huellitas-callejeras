package com.proyecto.huellitas_callejeras.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.data.DependencyProvider
import com.proyecto.huellitas_callejeras.models.Cita
import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.viewmodels.presentation.CitasUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID

class CitasMedicasViewModel : ViewModel() {
    private val repository = DependencyProvider.citasRepository

    private val animalRepository = DependencyProvider.animalRepository
    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState = _uiState.asStateFlow()

    private val _calendar = MutableStateFlow(Calendar.getInstance())
    val calendar = _calendar.asStateFlow()

    private val _selectedDate = MutableStateFlow(Calendar.getInstance().timeInMillis)
    val selectedDate = _selectedDate.asStateFlow()

    private val _allAnimals = MutableStateFlow<List<Animal>>(emptyList())
    val allAnimals = _allAnimals.asStateFlow()

    private val _selectedAnimal = MutableStateFlow<Animal?>(null)
    val selectedAnimal = _selectedAnimal.asStateFlow()

    init {
        cargarCitas()
        cargarAnimales()
    }

    fun cargarAnimales() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true) }
                val animales = animalRepository.getAll()
                _allAnimals.value = animales
                _uiState.update { it.copy(loading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "Error al cargar animales: ${e.message}"
                    )
                }
            }
        }
    }

    fun onAnimalSelected(animal: Animal) {
        _selectedAnimal.value = animal

        _uiState.value.cita?.let { currentCita ->
            _uiState.update {
                it.copy(cita = currentCita.copy(animalId = animal.idAnimal))
            }
        }
    }

    fun setSelectedAnimal(animal: Animal) {
        _selectedAnimal.value = animal
    }

    fun cargarCitas() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                val lista = repository.getCitas()
                _uiState.update {
                    it.copy(
                        citas = lista,
                        loading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "Error al cargar citas: ${e.message}"
                    )
                }
            }
        }
    }

    fun onDateSelected(dateMillis: Long) {
        _selectedDate.value = dateMillis
        filtrarCitasPorFecha(dateMillis)
    }
    fun filtrarCitasPorFecha(dateMillis: Long) {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val selectedDateFormatted = formatter.format(Date(dateMillis))

        val citasFiltradas = uiState.value.citas.filter { cita ->
            val citaDateFormatted = formatDateForCalendar(cita.fechaCita)
            citaDateFormatted == selectedDateFormatted
        }

        _uiState.update {
            it.copy(citasFiltradasPorFecha = citasFiltradas)
        }
    }

    fun onMonthChanged(isNext: Boolean) {
        val currentCalendar = _calendar.value.clone() as Calendar
        if (isNext) {
            currentCalendar.add(Calendar.MONTH, 1)
        } else {
            currentCalendar.add(Calendar.MONTH, -1)
        }
        _calendar.value = currentCalendar
    }

    fun onYearChanged(year: Int) {
        val currentCalendar = _calendar.value.clone() as Calendar
        currentCalendar.set(Calendar.YEAR, year)
        _calendar.value = currentCalendar
    }

    val datesWithAppointments: Set<String>
        get() = uiState.value.citas.mapNotNull { cita ->
            try {
                formatDateForCalendar(cita.fechaCita)
            } catch (e: Exception) {
                null
            }
        }.toSet()

    val appointmentsForSelectedDate: List<Cita>
        get() {
            val selectedDateFormatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date(_selectedDate.value))

            return uiState.value.citas.filter { cita ->
                try {
                    val citaDateFormatted = formatDateForCalendar(cita.fechaCita)
                    citaDateFormatted == selectedDateFormatted
                } catch (e: Exception) {
                    false
                }
            }
        }

    private fun formatDateForCalendar(dateString: String): String? {
        return try {

            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                "yyyy-MM-dd"
            )

            for (format in formats) {
                try {
                    val parser = SimpleDateFormat(format, Locale.getDefault())
                    val date = parser.parse(dateString)
                    if (date != null) {
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        return formatter.format(date)
                    }
                } catch (e: Exception) {

                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    fun onPatientSelected(animal: Animal) {
        _selectedAnimal.value = animal
    }

    fun onPatientIconClicked(cita: Cita): String? {
        return if (cita.animalId.isNotEmpty()) {
            cita.animalId
        } else {
            null
        }
    }

    fun getCitaPorId(id: String, onResult: (Cita?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            try {
                val c = repository.getCita(id)
                _uiState.update { it.copy(loading = false) }
                onResult(c)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "Error obteniendo cita: ${e.message}"
                    )
                }
                onResult(null)
            }
        }
    }

    fun crearCita(cita: Cita, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                val creada = repository.createCita(cita)
                val nuevasCitas = _uiState.value.citas + creada
                _uiState.update {
                    it.copy(
                        citas = nuevasCitas,
                        loading = false
                    )
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "Error creando cita: ${e.message}"
                    )
                }
            }
        }
    }

    fun editarCita(cita: Cita, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                repository.updateCita(cita)
                val nuevasCitas = _uiState.value.citas.map {
                    if (it.id == cita.id) cita else it
                }
                _uiState.update {
                    it.copy(
                        citas = nuevasCitas,
                        loading = false
                    )
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "Error editando cita: ${e.message}"
                    )
                }
            }
        }
    }

    fun eliminarCita(id: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                repository.deleteCita(id)
                val nuevasCitas = _uiState.value.citas.filterNot { it.id == id }
                _uiState.update {
                    it.copy(
                        citas = nuevasCitas,
                        loading = false
                    )
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "Error eliminando cita: ${e.message}"
                    )
                }
            }
        }
    }

    fun setEditingCita(cita: Cita?) {
        _uiState.update { it.copy(cita = cita, error = null) }
    }

    fun updateEditingCita(change: (Cita) -> Cita) {
        val currentCita = _uiState.value.cita
        currentCita?.let { cita ->
            _uiState.update { uiState ->
                uiState.copy(cita = change(cita))
            }
        }
    }


    fun saveCita(onSuccess: (() -> Unit)? = null) {
        val cita = _uiState.value.cita
        cita?.let {
            if (it.id.isNotEmpty()) {
                editarCita(it, onSuccess)
            } else {
                val citaParaCrear = if (it.id.isEmpty()) {
                    it.copy(id = UUID.randomUUID().toString())
                } else {
                    it
                }
                crearCita(citaParaCrear, onSuccess)
            }
        }
    }

    fun nuevaCitaVacia(): Cita {
        return Cita(
            id = "",
            titulo = "",
            fechaRealizacion = "",
            fechaCita = "",
            motivo = "",
            lugar = "",
            animalId = _selectedAnimal.value?.idAnimal ?: ""
        )
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }


    fun clearEditingCita() {
        _uiState.update { it.copy(cita = null) }
        _selectedAnimal.value = null
    }
}