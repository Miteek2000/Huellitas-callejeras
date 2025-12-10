package com.proyecto.huellitas_callejeras.screens

import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.proyecto.huellitas_callejeras.screens.components.HeaderBar
import com.proyecto.huellitas_callejeras.viewmodel.MedicamentoLocal
import com.proyecto.huellitas_callejeras.viewmodel.TratamientoViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TratamientoScreen(
    viewModel: TratamientoViewModel,
    animalId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMedicamento: (Int) -> Unit
) {
    val medicamentos by viewModel.medicamentos.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()
    val recetaUri by viewModel.recetaUri.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val tratamientoCargado by viewModel.tratamientoCargado.collectAsStateWithLifecycle()
    val medicamentosCargados by viewModel.medicamentosCargados.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    var imageName by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    var recetaExistenteUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tratamientoCargado) {
        tratamientoCargado?.let { tratamiento ->
            try {
                selectedDate = LocalDate.parse(
                    tratamiento.fechaInicio.substringBefore("T"),
                    DateTimeFormatter.ISO_LOCAL_DATE
                )
                recetaExistenteUrl = tratamiento.recetaUrl
            } catch (e: Exception) {
            }
        }
    }

    LaunchedEffect(animalId) {
        viewModel.cargarTratamientoExistente(animalId)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (isEditing || tratamientoCargado == null) {
            viewModel.setRecetaUri(uri)
            if (uri == null) {
                imageName = null
            } else {
                context.contentResolver.query(uri, null, null, null, null)?.use {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    it.moveToFirst()
                    imageName = it.getString(nameIndex)
                }
            }
        }
    }

    LaunchedEffect(saveSuccess, error) {
        if (saveSuccess) {
            val mensaje = if (tratamientoCargado != null && isEditing) {
                "✓ Tratamiento actualizado exitosamente"
            } else {
                "✓ Tratamiento guardado exitosamente"
            }
            snackbarHostState.showSnackbar(mensaje)
            viewModel.resetSaveStatus()

            if (tratamientoCargado != null && isEditing) {
                viewModel.cargarTratamientoExistente(animalId)
            } else {
                selectedDate = null
                imageName = null
            }
        }
        error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.resetSaveStatus()
        }
    }

    if (showDatePicker && (isEditing || tratamientoCargado == null)) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        selectedDate = datePickerState.selectedDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
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
                    viewModel.limpiarTodo()
                    onNavigateBack()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Regresar", tint = Color.Black)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD5A6D5))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    when {
                        tratamientoCargado != null && !isEditing -> "Tratamiento"
                        tratamientoCargado != null && isEditing -> "Editar Tratamiento"
                        else -> "Nuevo Tratamiento"
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Fecha Inicio *",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                        ?: "Seleccionar fecha",
                                    color = if (selectedDate == null) Color.Gray else Color.Black,
                                    modifier = Modifier.clickable(
                                        enabled = isEditing || tratamientoCargado == null
                                    ) {
                                        if (isEditing || tratamientoCargado == null) {
                                            showDatePicker = true
                                        }
                                    }
                                )
                                if (isEditing || tratamientoCargado == null) {
                                    IconButton(
                                        onClick = { showDatePicker = true },
                                        enabled = isEditing || tratamientoCargado == null
                                    ) {
                                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Receta Medica", fontSize = 16.sp)
                            if (isEditing || tratamientoCargado == null) {
                                IconButton(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    enabled = isEditing || tratamientoCargado == null
                                ) {
                                    Icon(Icons.Default.UploadFile, contentDescription = "Subir receta")
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                recetaExistenteUrl?.takeIf { it.isNotBlank() && !isEditing }?.let { url ->
                                    Text(
                                        text = "Receta adjuntada",
                                        fontSize = 12.sp,
                                        color = Color.Green,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                imageName?.let {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(end = 8.dp),
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }

                                if (isEditing || tratamientoCargado == null) {
                                    IconButton(
                                        onClick = { imagePickerLauncher.launch("image/*") },
                                        enabled = isEditing || tratamientoCargado == null
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.UploadFile,
                                            contentDescription = "Subir receta",
                                            tint = Color.Unspecified
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isEditing || tratamientoCargado == null) {
                    item {
                        AgregarMedicamentoButton(
                            enabled = isEditing || tratamientoCargado == null,
                            onAgregarMedicamento = { onNavigateToMedicamento(-1) }
                        )
                    }
                }

                if (medicamentos.isEmpty() && medicamentosCargados.isNullOrEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                                    text = "No hay medicamentos",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (isEditing || tratamientoCargado == null)
                                        "Agrega al menos un medicamento"
                                    else
                                        "Este tratamiento no tiene medicamentos registrados",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    val medicamentosAMostrar = if (isEditing) {
                        medicamentos
                    } else {
                        medicamentosCargados?.mapIndexed { index, medicamentoCargado ->
                            MedicamentoLocal(
                                id = index + 1,
                                nombre = medicamentoCargado.nombre,
                                dosis = medicamentoCargado.dosis.toString(),
                                frecuencia = medicamentoCargado.repeticion.toString(),
                                fechaConclusion = medicamentoCargado.fechaConclusion ?: ""
                            )
                        } ?: medicamentos
                    }

                    items(medicamentosAMostrar) { medicamento ->
                        MedicamentoCard(
                            medicamento = medicamento,
                            isEditing = isEditing || tratamientoCargado == null,
                            onEdit = {
                                if (isEditing || tratamientoCargado == null) {
                                    onNavigateToMedicamento(medicamento.id)
                                }
                            },
                            onDelete = {
                                if (isEditing || tratamientoCargado == null) {
                                    viewModel.eliminarMedicamento(medicamento.id)
                                }
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    when {
                        tratamientoCargado != null && !isEditing -> {
                            viewModel.toggleModoEdicion()
                        }

                        tratamientoCargado != null && isEditing -> {
                            if (selectedDate == null) {
                                viewModel.setError("Debes seleccionar una fecha de inicio")
                                return@Button
                            }

                            if (medicamentos.isEmpty()) {
                                viewModel.setError("Debes agregar al menos un medicamento")
                                return@Button
                            }

                            val fechaInicioStr = selectedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE)

                            viewModel.actualizarTratamiento(
                                fechaInicio = fechaInicioStr,
                                medicamentosLocales = medicamentos,
                                recetaUri = recetaUri,
                                animalId = animalId
                            )
                        }

                        else -> {
                            if (selectedDate == null) {
                                viewModel.setError("Debes seleccionar una fecha de inicio")
                                return@Button
                            }

                            if (medicamentos.isEmpty()) {
                                viewModel.setError("Debes agregar al menos un medicamento")
                                return@Button
                            }

                            val fechaInicioStr = selectedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE)

                            viewModel.guardarTratamientoRemoto(
                                animalId = animalId,
                                fechaInicio = fechaInicioStr,
                                medicamentosLocales = medicamentos,
                                recetaUri = recetaUri
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(48.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF642C51))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = when {
                            tratamientoCargado != null && !isEditing -> "Editar Tratamiento"
                            tratamientoCargado != null && isEditing -> "Guardar Cambios"
                            else -> "Guardar Tratamiento"
                        },
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            if (tratamientoCargado != null && isEditing) {
                Button(
                    onClick = {
                        viewModel.setModoEdicion(false)
                        coroutineScope.launch {
                            viewModel.cargarTratamientoExistente(animalId)
                        }
                        viewModel.setRecetaUri(null)
                        imageName = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancelar", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun AgregarMedicamentoButton(
    enabled: Boolean = true,
    onAgregarMedicamento: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onAgregarMedicamento
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color.White else Color.LightGray
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
                tint = if (enabled) Color(0xFF642C51) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Agregar Medicamento",
                fontSize = 14.sp,
                color = if (enabled) Color(0xFF642C51) else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MedicamentoCard(
    medicamento: MedicamentoLocal,
    isEditing: Boolean = true,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8C5E8)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(end = if (isEditing) 80.dp else 0.dp)
            ) {
                Text(
                    text = medicamento.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Dosis: ${medicamento.dosis} mg", fontSize = 14.sp)
                Text(text = "Frecuencia: cada ${medicamento.frecuencia} hrs", fontSize = 14.sp)
                if (medicamento.fechaConclusion.isNotBlank()) {
                    Text(
                        text = "Hasta: ${medicamento.fechaConclusion}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            if (isEditing) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = onEdit,
                        enabled = isEditing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = if (isEditing) Color(0xFF642C51) else Color.Gray
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        enabled = isEditing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = if (isEditing) Color.Red else Color.Gray
                        )
                    }
                }
            }
        }
    }
}