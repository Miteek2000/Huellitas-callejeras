package com.proyecto.huellitas_callejeras.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.proyecto.huellitas_callejeras.data.model.Medicamento
import com.proyecto.huellitas_callejeras.ui.components.HeaderBar
import com.proyecto.huellitas_callejeras.ui.viewmodel.TratamientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TratamientoScreen(
    viewModel: TratamientoViewModel,
    onNavigateToMedicamento: (Int, Int) -> Unit
) {
    val tratamientosConMedicamentos by viewModel.tratamientosConMedicamentos.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedTratamientoIndex.collectAsStateWithLifecycle()

    val selectedTratamiento = tratamientosConMedicamentos.getOrNull(selectedIndex)

    var showDeleteDialog by remember { mutableStateOf(false) }
    var tratamientoToDelete by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        HeaderBar()

        IconButton(
            onClick = { /* TODO: Agregar navegación cuando esté lista */ },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Regresar",
                tint = Color.Black
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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

            Spacer(modifier = Modifier.width(8.dp))

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
                text = selectedTratamiento?.tratamiento?.nombre ?: "Tratamiento",
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
                    EmptyStateCard()
                }
            } else {
                item {
                    AgregarMedicamentoButton(
                        onAgregarMedicamento = {
                            selectedTratamiento?.tratamiento?.id?.let { id ->
                                onNavigateToMedicamento(id, -1)
                            }
                        }
                    )
                }

                selectedTratamiento?.let { tratamientoCompleto ->
                    items(tratamientoCompleto.medicamentos) { medicamento ->
                        MedicamentoCard(
                            medicamento = medicamento,
                            onEdit = { medicamentoId ->
                                onNavigateToMedicamento(
                                    tratamientoCompleto.tratamiento.id,
                                    medicamentoId
                                )
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Fecha Inicio",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = medicamento.fechaInicio,
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Fecha Conclusión",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = medicamento.fechaConclusion,
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.White.copy(alpha = 0.3f)
            )

            Text(
                text = medicamento.nombre,
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dosis: ${medicamento.dosis}",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Repetición: ${medicamento.repeticion}",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }
                IconButton(
                    onClick = { onEdit(medicamento.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}