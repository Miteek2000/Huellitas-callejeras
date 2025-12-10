package com.proyecto.huellitas_callejeras.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.proyecto.huellitas_callejeras.screens.components.HeaderBar
import com.proyecto.huellitas_callejeras.viewmodel.MedicamentoLocal
import com.proyecto.huellitas_callejeras.viewmodel.TratamientoViewModel
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentoFormScreen(
    viewModel: TratamientoViewModel,
    medicamentoId: Int,
    onNavigateBack: () -> Unit
) {
    val medicamentoActual = if (medicamentoId != -1) {
        viewModel.obtenerMedicamentoPorId(medicamentoId)
    } else null

    // Estados del formulario
    var nombre by remember { mutableStateOf(medicamentoActual?.nombre ?: "") }
    var dosis by remember { mutableStateOf(medicamentoActual?.dosis ?: "") }
    var frecuencia by remember { mutableStateOf(medicamentoActual?.frecuencia ?: "") }
    var fechaConclusion by remember { mutableStateOf(medicamentoActual?.fechaConclusion ?: "") }

    // Estados de error
    var nombreError by remember { mutableStateOf("") }
    var dosisError by remember { mutableStateOf("") }
    var frecuenciaError by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val snackbarHostState = remember { SnackbarHostState() }

    fun validarCampos(): Boolean {
        var valido = true

        if (nombre.isBlank()) {
            nombreError = "El nombre es requerido"
            valido = false
        } else {
            nombreError = ""
        }

        if (dosis.isBlank()) {
            dosisError = "La dosis es requerida"
            valido = false
        } else if (dosis.toFloatOrNull() == null) {
            dosisError = "Ingresa un número válido"
            valido = false
        } else {
            dosisError = ""
        }

        if (frecuencia.isBlank()) {
            frecuenciaError = "La frecuencia es requerida"
            valido = false
        } else if (frecuencia.toFloatOrNull() == null) {
            frecuenciaError = "Ingresa un número válido"
            valido = false
        } else {
            frecuenciaError = ""
        }

        return valido
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            HeaderBar()

            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD5A6D5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (medicamentoId == -1) "Nuevo Medicamento" else "Editar Medicamento",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Column {
                            Text(
                                "Nombre del Medicamento *",
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = {
                                    nombre = it
                                    nombreError = ""
                                },
                                isError = nombreError.isNotEmpty(),
                                supportingText = if (nombreError.isNotEmpty()) {
                                    { Text(nombreError, color = MaterialTheme.colorScheme.error) }
                                } else null,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(4.dp),
                                placeholder = { Text("Ej: Amoxicilina", color = Color.Gray) }
                            )
                        }

                        // Dosis
                        Column {
                            Text(
                                "Dosis (mg) *",
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = dosis,
                                onValueChange = {
                                    dosis = it
                                    dosisError = ""
                                },
                                isError = dosisError.isNotEmpty(),
                                supportingText = if (dosisError.isNotEmpty()) {
                                    { Text(dosisError, color = MaterialTheme.colorScheme.error) }
                                } else null,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(4.dp),
                                placeholder = { Text("Ej: 500", color = Color.Gray) }
                            )
                        }

                        Column {
                            Text(
                                "Frecuencia (horas) *",
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = frecuencia,
                                onValueChange = {
                                    frecuencia = it
                                    frecuenciaError = ""
                                },
                                isError = frecuenciaError.isNotEmpty(),
                                supportingText = if (frecuenciaError.isNotEmpty()) {
                                    { Text(frecuenciaError, color = MaterialTheme.colorScheme.error) }
                                } else null,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(4.dp),
                                placeholder = { Text("Ej: 8 (cada 8 horas)", color = Color.Gray) }
                            )
                        }

                        Column {
                            Text(
                                "Fecha de Conclusión (Opcional)",
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = fechaConclusion,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true },
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(
                                            Icons.Default.EditCalendar,
                                            contentDescription = "Seleccionar fecha"
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(4.dp),
                                placeholder = { Text("Seleccionar fecha", color = Color.Gray) }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Button(
                                onClick = onNavigateBack,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Cancelar",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Button(
                                onClick = {
                                    if (validarCampos()) {
                                        val medicamento = MedicamentoLocal(
                                            id = if (medicamentoId == -1) 0 else medicamentoId,
                                            nombre = nombre.trim(),
                                            dosis = dosis.trim(),
                                            frecuencia = frecuencia.trim(),
                                            fechaConclusion = fechaConclusion
                                        )

                                        if (medicamentoId == -1) {

                                            viewModel.agregarMedicamento(medicamento)
                                        } else {

                                            viewModel.actualizarMedicamento(medicamento)
                                        }

                                        onNavigateBack()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7CB342)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Guardar",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            fechaConclusion = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}