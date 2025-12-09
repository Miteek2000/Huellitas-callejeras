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
import com.proyecto.huellitas_callejeras.viewmodel.MedicamentoViewModel
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentoFormScreen(
    viewModel: MedicamentoViewModel,
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetError()
        }
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
                onClick = {
                    viewModel.limpiarFormulario()
                    onNavigateBack()
                },
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
                            text = if (formState.medicamentoId == null || formState.medicamentoId == 0) "Nuevo Medicamento" else "Editar Medicamento",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Column {
                            Text("Medicamento", fontSize = 13.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(
                                value = formState.nombre,
                                onValueChange = { viewModel.actualizarNombre(it) },
                                isError = formState.nombreError.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.White
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }

                        Column {
                            Text("Dosis", fontSize = 13.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(
                                value = formState.dosis,
                                onValueChange = { viewModel.actualizarDosis(it) },
                                isError = formState.dosisError.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.White
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }

                        Column {
                             Text("Frecuencia", fontSize = 13.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(
                                value = formState.frecuencia,
                                onValueChange = { viewModel.actualizarFrecuencia(it) },
                                isError = formState.frecuenciaError.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.White
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }

                        Column {
                            Text("Fecha de Conclusión", fontSize = 13.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(
                                value = formState.fechaConclusion,
                                onValueChange = {}, // Not needed as it's read-only
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().height(48.dp).clickable { showDatePicker = true },
                                trailingIcon = {
                                    Icon(Icons.Default.EditCalendar, contentDescription = "Seleccionar fecha")
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.White
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }


                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                             Button(
                                onClick = onNavigateBack,
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Cancelar", color = Color.Black, fontSize = 13.sp)
                            }
                            Button(
                                onClick = {
                                    viewModel.guardarMedicamento {
                                        onNavigateBack()
                                    }
                                },
                                enabled = !isLoading,
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7CB342),
                                    disabledContainerColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White)
                                } else {
                                    Text("Guardar", color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            viewModel.actualizarFechaConclusion(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
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
