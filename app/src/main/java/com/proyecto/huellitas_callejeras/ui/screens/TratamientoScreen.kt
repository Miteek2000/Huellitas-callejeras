package com.proyecto.huellitas_callejeras.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.proyecto.huellitas_callejeras.data.model.Medicamento
import com.proyecto.huellitas_callejeras.data.remote.dto.MedicamentoInTratamientoDto
import com.proyecto.huellitas_callejeras.ui.components.HeaderBar
import com.proyecto.huellitas_callejeras.ui.viewmodel.TratamientoViewModel
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TratamientoScreen(
    viewModel: TratamientoViewModel,
    onNavigateToMedicamento: (Int, Int) -> Unit
) {
    val tratamientosConMedicamentos by viewModel.tratamientosConMedicamentos.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedTratamientoIndex.collectAsStateWithLifecycle()
    val selectedTratamiento = tratamientosConMedicamentos.getOrNull(selectedIndex)

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var tratamientoToDelete by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(saveSuccess, error) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("¡Tratamiento guardado en el servidor!")
            viewModel.resetSaveStatus()
        }
        error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.resetSaveStatus()
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
                onClick = { /* TODO: Agregar navegación */ },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Regresar", tint = Color.Black)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                LazyRow(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tratamientosConMedicamentos.size) { index ->
                        val tratamientoConMeds = tratamientosConMedicamentos[index]
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
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .clickable { viewModel.agregarNuevoTratamiento() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, "Agregar tratamiento", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD5A6D5))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(selectedTratamiento?.tratamiento?.nombre ?: "Tratamiento", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (tratamientosConMedicamentos.isEmpty()) {
                    item { EmptyStateCard() }
                } else {
                    item {
                        AgregarMedicamentoButton {
                            selectedTratamiento?.tratamiento?.id?.let { id -> onNavigateToMedicamento(id, -1) }
                        }
                    }
                    selectedTratamiento?.let { tratamientoCompleto ->
                        items(tratamientoCompleto.medicamentos) { medicamento ->
                            MedicamentoCard(medicamento) { medId ->
                                onNavigateToMedicamento(tratamientoCompleto.tratamiento.id, medId)
                            }
                        }
                    }
                }
            }

            if (selectedTratamiento != null) {
                Button(
                    onClick = {
                        val tratamientoActual = selectedTratamiento.tratamiento
                        val medicamentosActuales = selectedTratamiento.medicamentos

                        val medicamentosDto = medicamentosActuales.mapNotNull {
                            val fechaInicioInstant = if (it.fechaInicio.isNotBlank()) LocalDate.parse(it.fechaInicio, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay().toInstant(ZoneOffset.UTC) else null
                            val fechaConclusionInstant = if (it.fechaConclusion.isNotBlank()) LocalDate.parse(it.fechaConclusion, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay().toInstant(ZoneOffset.UTC) else null

                            if(fechaInicioInstant != null && fechaConclusionInstant != null) {
                                MedicamentoInTratamientoDto(
                                    nombre = it.nombre,
                                    dosis = it.dosis,
                                    repeticion = it.frecuencia,
                                    fechaInicio = fechaInicioInstant,
                                    fechaConclusion = fechaConclusionInstant
                                )
                            } else {
                                null
                            }
                        }

                        val fechaInicioParaEnviar = if (tratamientoActual.fechaInicio.isBlank()) {
                            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                        } else {
                            tratamientoActual.fechaInicio
                        }

                        viewModel.guardarTratamientoRemoto(
                            animalId = "5265a1b4-ca82-4c8c-b2aa-9fd968b49b50", // Tu UUID del backend
                            fechaInicio = fechaInicioParaEnviar,
                            medicamentos = medicamentosDto
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF642C51))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Guardar Tratamiento en Servidor", color = Color.White)
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
                TextButton(
                    onClick = {
                        tratamientoToDelete?.let { id ->
                            viewModel.eliminarTratamiento(id)
                        }
                        showDeleteDialog = false
                        tratamientoToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    tratamientoToDelete = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TratamientoTabConIcono(
    nombre: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .background(
                if (isSelected) Color(0xFFE8E8E8) else Color.White,
                RoundedCornerShape(4.dp)
            )
            .border(
                1.dp,
                Color.Gray,
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = nombre,
            fontSize = 12.sp,
            color = Color.Black
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "No hay tratamientos",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Presiona + para agregar un tratamiento",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AgregarMedicamentoButton(onAgregarMedicamento: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAgregarMedicamento),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar medicamento",
                tint = Color(0xFF642C51),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Agregar Medicamento",
                fontSize = 14.sp,
                color = Color(0xFF642C51),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MedicamentoCard(
    medicamento: Medicamento,
    onEdit: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8C5E8)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(text = medicamento.nombre, fontWeight = FontWeight.Bold)
                Text(text = "Dosis: ${medicamento.dosis}")
                Text(text = "Frecuencia: ${medicamento.frecuencia}")
            }
            IconButton(
                onClick = { onEdit(medicamento.id) },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar Medicamento"
                )
            }
        }
    }
}
