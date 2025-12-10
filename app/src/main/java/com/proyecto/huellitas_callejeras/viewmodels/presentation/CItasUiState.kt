package com.proyecto.huellitas_callejeras.viewmodels.presentation

import com.proyecto.huellitas_callejeras.models.Cita

data class CitasUiState(
    val citas: List<Cita> = emptyList(),
    val citasFiltradasPorFecha: List<Cita> = emptyList(),
    val cita: Cita? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val showDatePickerDialog: Boolean = false
)
