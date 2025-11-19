package com.example.huellitas_callejeras.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.huellitas_callejeras.data.model.Medicamento
import com.example.huellitas_callejeras.ui.components.HeaderBar
import com.example.huellitas_callejeras.ui.utils.DateUtils
import com.example.huellitas_callejeras.ui.viewmodel.TratamientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TratamientoScreen(
    viewModel: TratamientoViewModel,
    onNavigateToMedicamento: (Int, Int) -> Unit
) {
    val tratamientosConMedicamentos by viewModel.tratamientosConMedicamentos.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedTratamientoIndex.collectAsStateWithLifecycle()
    val showFormulario by viewModel.showFormulario.collectAsStateWithLifecycle()

    val selectedTratamiento = tratamientosConMedicamentos.getOrNull(selectedIndex)

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var tratamientoToDelete by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        HeaderBar()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tratamientosConMedicamentos.forEachIndexed { index, tratamientoConMeds ->
                TratamientoTabConIcono(
                    nombre = tratamientoConMeds.tratamiento.nombre,
                    isSelected = index == selectedIndex,
                    onClick = { viewModel.seleccionarTratamiento(index) },
                    onDelete = {
                        tratamientoToDelete = tratamientoConMeds.tratamiento.id
                        showDeleteDialog = true
                    }
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .clickable { viewModel.agregarNuevoTratamiento() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar tratamiento",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFD5A6D5))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tratamiento",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (tratamientosConMedicamentos.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Add, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Text("No hay tratamientos", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text("Presiona + para agregar un tratamiento", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                if (showFormulario || selectedTratamiento?.tratamiento?.fechaInicio?.isEmpty() == true) {
                    item {
                        FormularioInicial(
                            fechaInicio = selectedTratamiento?.tratamiento?.fechaInicio ?: "",
                            onClickFecha = { showDatePicker = true },
                            onAgregarMedicamento = {
                                selectedTratamiento?.tratamiento?.id?.let { id ->
                                    onNavigateToMedicamento(id, -1)
                                }
                            }
                        )
                    }
                }

                selectedTratamiento?.let { tratamiento ->
                    items(tratamiento.medicamentos) { medicamento ->
                        MedicamentoCard(
                            medicamento = medicamento,
                            onEdit = { medicamentoId ->
                                onNavigateToMedicamento(tratamiento.tratamiento.id, medicamentoId)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar tratamiento") },
            text = { Text("¿Estás segura de que deseas eliminar este tratamiento?") },
            confirmButton = {
                TextButton(onClick = {
                    tratamientoToDelete?.let { viewModel.eliminarTratamiento(it) }
                    showDeleteDialog = false
                    tratamientoToDelete = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; tratamientoToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { timestamp ->
                        selectedTratamiento?.tratamiento?.id?.let { id ->
                            viewModel.actualizarFechaInicio(id, DateUtils.formatTimestamp(timestamp))
                        }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun TratamientoTabConIcono(nombre: String, isSelected: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.height(32.dp).background(if (isSelected) Color(0xFFE8E8E8) else Color.White, RoundedCornerShape(4.dp)).border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(nombre, fontSize = 12.sp, color = Color.Black)
        IconButton(onClick = onDelete, modifier = Modifier.size(16.dp)) {
            Icon(Icons.Default.Delete, "Eliminar", tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun FormularioInicial(fechaInicio: String, onClickFecha: () -> Unit, onAgregarMedicamento: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClickFecha), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Fecha Inicio", fontSize = 14.sp, color = Color.Black)
                if (fechaInicio.isNotEmpty()) Text(fechaInicio, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }
            Icon(Icons.Default.DateRange, "Seleccionar fecha", tint = Color(0xFF6B8E23), modifier = Modifier.size(28.dp))
        }
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Medicamentos", fontSize = 14.sp, color = Color.Black)
            IconButton(onClick = onAgregarMedicamento, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Add, "Agregar medicamento", tint = Color.Black, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun MedicamentoCard(medicamento: Medicamento, onEdit: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8C5E8)), shape = RoundedCornerShape(8.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(medicamento.nombre, fontSize = 13.sp, color = Color.Black)
                Text(medicamento.fechaConclusion, fontSize = 13.sp, color = Color.Black)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(medicamento.dosis, fontSize = 13.sp, color = Color.Black)
                Text(medicamento.repeticion, fontSize = 13.sp, color = Color.Black)
                IconButton(onClick = { onEdit(medicamento.id) }, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Edit, "Editar", tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}