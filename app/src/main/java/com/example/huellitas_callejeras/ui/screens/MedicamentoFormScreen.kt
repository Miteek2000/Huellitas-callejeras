package com.example.huellitas_callejeras.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.huellitas_callejeras.ui.components.HeaderBar
import com.example.huellitas_callejeras.ui.utils.DateUtils
import com.example.huellitas_callejeras.ui.viewmodel.MedicamentoViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentoFormScreen(tratamientoId: Int, viewModel: MedicamentoViewModel, onNavigateBack: () -> Unit) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var showSuccessMessage by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
            HeaderBar()
            IconButton(onClick = { viewModel.limpiarFormulario(); onNavigateBack() }, modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.ArrowBack, "Regresar", tint = Color.Black)
            }
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFD5A6D5)), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column {
                            Text("Medicamento", fontSize = 13.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(value = formState.nombre, onValueChange = { viewModel.actualizarNombre(it) }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, unfocusedBorderColor = Color.White), shape = RoundedCornerShape(4.dp))
                        }
                        Column {
                            Text("Fecha de Conclusión", fontSize = 13.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(value = formState.fechaConclusion, onValueChange = {}, modifier = Modifier.fillMaxWidth().height(48.dp), readOnly = true, placeholder = { Text("DD/MM/AAAA", fontSize = 13.sp) }, trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.CalendarToday, "Seleccionar fecha", tint = Color.Black, modifier = Modifier.size(20.dp)) } }, colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, unfocusedBorderColor = Color.White), shape = RoundedCornerShape(4.dp))
                        }
                        Column {
                            Text("Dosis", fontSize = 13.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(value = formState.dosis, onValueChange = { viewModel.actualizarDosis(it) }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, unfocusedBorderColor = Color.White), shape = RoundedCornerShape(4.dp))
                        }
                        Column {
                            Text("Repetición", fontSize = 13.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(value = formState.repeticion, onValueChange = { viewModel.actualizarRepeticion(it) }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, unfocusedBorderColor = Color.White), shape = RoundedCornerShape(4.dp))
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = { viewModel.limpiarFormulario(); onNavigateBack() }, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(4.dp)) { Text("Cancelar", color = Color.Black, fontSize = 13.sp) }
                            Button(onClick = { viewModel.guardarMedicamento(tratamientoId) { showSuccessMessage = true } }, modifier = Modifier.weight(1f).height(40.dp), enabled = formState.isValid(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB342), disabledContainerColor = Color.Gray), shape = RoundedCornerShape(4.dp)) { Text("Guardar", color = Color.White, fontSize = 13.sp) }
                        }
                    }
                }
            }
        }
        if (showSuccessMessage) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Card(modifier = Modifier.padding(32.dp).fillMaxWidth(0.8f), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("✓", fontSize = 48.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        Text("¡Guardado con éxito!", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        LaunchedEffect(Unit) { delay(2000); showSuccessMessage = false; onNavigateBack() }
                    }
                }
            }
        }
    }
    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.actualizarFechaConclusion(DateUtils.formatTimestamp(it)) }; showDatePicker = false }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }) { DatePicker(state = datePickerState) }
    }
}