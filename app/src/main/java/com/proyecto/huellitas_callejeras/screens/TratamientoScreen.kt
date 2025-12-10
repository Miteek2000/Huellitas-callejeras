package com.proyecto.huellitas_callejeras.screens

import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
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
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.proyecto.huellitas_callejeras.R
import com.proyecto.huellitas_callejeras.screens.components.HeaderBar
import com.proyecto.huellitas_callejeras.viewmodel.MedicamentoLocal
import com.proyecto.huellitas_callejeras.viewmodel.TratamientoViewModel
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

    val snackbarHostState = remember { SnackbarHostState() }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    var imageName by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
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

    LaunchedEffect(saveSuccess, error) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("✓ Tratamiento guardado exitosamente")
            viewModel.resetSaveStatus()
            selectedDate = null
            imageName = null
        }
        error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.resetSaveStatus()
        }
    }

    if (showDatePicker) {
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
                    viewModel.limpiarMedicamentos()
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
                    "Nuevo Tratamiento",
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
                                    modifier = Modifier.clickable { showDatePicker = true }
                                )
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
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
                            Text(text = "Receta (Opcional)", fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                imageName?.let {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(end = 8.dp),
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                                IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
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

                item {
                    AgregarMedicamentoButton {
                        onNavigateToMedicamento(-1)
                    }
                }

                if (medicamentos.isEmpty()) {
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
                                    text = "Agrega al menos un medicamento",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(medicamentos) { medicamento ->
                        MedicamentoCard(
                            medicamento = medicamento,
                            onEdit = { onNavigateToMedicamento(medicamento.id) },
                            onDelete = { viewModel.eliminarMedicamento(medicamento.id) }
                        )
                    }
                }
            }

            Button(
                onClick = {

                    if (selectedDate == null) {
                        viewModel.setError("Debes seleccionar una fecha de inicio")
                        return@Button
                    }

                    if (medicamentos.isEmpty()) {
                        viewModel.setError("Debes agregar al menos un medicamento")
                        return@Button
                    }

                    val fechaInicioStr = selectedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE)

                    Log.d("TratamientoScreen", "=== GUARDANDO TRATAMIENTO ===")
                    Log.d("TratamientoScreen", "Animal ID: $animalId")
                    Log.d("TratamientoScreen", "Fecha: $fechaInicioStr")
                    Log.d("TratamientoScreen", "Medicamentos: ${medicamentos.size}")
                    Log.d("TratamientoScreen", "Receta: ${recetaUri != null}")

                    viewModel.guardarTratamientoRemoto(
                        animalId = animalId,
                        fechaInicio = fechaInicioStr,
                        medicamentosLocales = medicamentos,
                        recetaUri = recetaUri
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
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Guardar Tratamiento", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun AgregarMedicamentoButton(onAgregarMedicamento: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAgregarMedicamento),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
    medicamento: MedicamentoLocal,
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
                    .padding(end = 80.dp) // Espacio para los botones
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

            Row(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF642C51)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}