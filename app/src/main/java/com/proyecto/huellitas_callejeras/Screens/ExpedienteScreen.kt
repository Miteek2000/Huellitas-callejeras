package com.proyecto.huellitas_callejeras.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.proyecto.huellitas_callejeras.R
import com.proyecto.huellitas_callejeras.components.FormDropDownMenu
import com.proyecto.huellitas_callejeras.components.FormTextField
import com.proyecto.huellitas_callejeras.navegacion.AppScreens
import com.proyecto.huellitas_callejeras.ui.theme.pink1
import com.proyecto.huellitas_callejeras.ui.theme.purple1
import com.proyecto.huellitas_callejeras.viewmodels.ExpedienteViewModel
import java.lang.SecurityException


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpedienteScreen(
    navController: NavController,
    viewModel: ExpedienteViewModel,
    patientId: Int?,
    isEditable: Boolean
) {

    val uiState by viewModel.uiState.collectAsState()
    val datePickerState = rememberDatePickerState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                try {
                    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(it, flags)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
                viewModel.onImageSelected(it)
            }
        }
    )

    if (uiState.showDatePickerDialogFor != null) {
        DatePickerDialog(
            onDismissRequest = { viewModel.onShowDatePickerDialog(null) },
            confirmButton = {
                TextButton(onClick = { viewModel.onDateSelected(datePickerState.selectedDateMillis) })
                {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onShowDatePickerDialog(null) }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(key1 = patientId, key2 = isEditable) {
        viewModel.init(patientId, isEditable)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Huellitas Callejeras") },
                navigationIcon = {
                    Row {
                        IconButton(onClick = { navController.navigate(AppScreens.GaleriaScreen.route) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = "Home",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { navController.navigate(AppScreens.CitasMedicasScreen.route) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.calender),
                                contentDescription = "Calendar",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5B2D5B),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8C6D4), shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Expediente del paciente",
                    fontSize = 18.sp,
                    color = Color(0xFF5B2D5B),
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = {
                        if (!uiState.isEditing) {
                            viewModel.setEditing(true)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Editar",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(pink1, purple1)))
                    .padding(16.dp)
            ) {
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(80.dp).clickable(enabled = uiState.isEditing) { imagePickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color(0xFF7AB659))
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(0.9f)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(0.8f)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD68EBC))
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.agregarimagen),
                                    contentDescription = "Patient Icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                                if (uiState.selectedImageUri != null) {
                                    AsyncImage(
                                        model = uiState.selectedImageUri,
                                        contentDescription = "Patient Image",
                                        modifier = Modifier
                                            .fillMaxSize(0.8f)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Expediente de paciente",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5B2D5B),
                                    softWrap = false
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = { viewModel.onEstadoExpandedChange(true) },
                                        enabled = uiState.isEditing,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7AB659)),
                                        shape = RoundedCornerShape(28.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.3f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.heart),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Estado: ${uiState.estadoSelected}", fontSize = 16.sp)
                                    }
                                    DropdownMenu(
                                        expanded = uiState.estadoExpanded,
                                        onDismissRequest = { viewModel.onEstadoExpandedChange(false) }) {
                                        uiState.estadoOptions.forEach { option ->
                                            DropdownMenuItem(text = { Text(option) },
                                                onClick = {
                                                    viewModel.onEstadoSelected(option)
                                                    viewModel.onEstadoExpandedChange(false)
                                                })
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { /*TODO*/ },
                                    enabled = uiState.isEditing,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7AB659)),
                                    shape = RoundedCornerShape(28.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Tratamiento", fontSize = 16.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        FormTextField(
                            label = "Nombre",
                            value = uiState.nombre,
                            onValueChange = viewModel::onNombreChange,
                            enabled = uiState.isEditing,
                            isError = uiState.nombreError
                        )
                    }
                    item {
                        FormDropDownMenu(
                            label = "Especie",
                            options = uiState.especieOptions,
                            selectedOption = uiState.especie,
                            onOptionSelected = viewModel::onEspecieChange,
                            enabled = uiState.isEditing,
                            isError = uiState.especieError
                        )
                    }
                    item {
                        FormTextField(
                            label = "Raza",
                            value = uiState.raza,
                            onValueChange = viewModel::onRazaChange,
                            enabled = uiState.isEditing,
                            isError = uiState.razaError
                        )
                    }
                    item {
                        FormTextField(
                            label = "Edad",
                            value = uiState.edad,
                            onValueChange = { viewModel.onEdadChange(it.filter(Char::isDigit)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            enabled = uiState.isEditing,
                            isError = uiState.edadError
                        )
                    }
                    item {
                        FormDropDownMenu(
                            label = "Sexo",
                            options = uiState.sexoOptions,
                            selectedOption = uiState.sexo,
                            onOptionSelected = viewModel::onSexoChange,
                            enabled = uiState.isEditing,
                            isError = uiState.sexoError
                        )
                    }
                    item {
                        FormTextField(
                            label = "Peso",
                            value = uiState.peso,
                            onValueChange = {
                                val newText = it.filter { char -> char.isDigit() || char == '.' }
                                if (newText.count { char -> char == '.' } <= 1) {
                                    viewModel.onPesoChange(newText)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            enabled = uiState.isEditing,
                            isError = uiState.pesoError
                        )
                    }
                    item {
                        FormTextField(
                            label = "Fecha de salida",
                            value = uiState.fechaSalida,
                            onValueChange = viewModel::onFechaSalidaChange,
                            enabled = uiState.isEditing,
                            trailingIcon = { Icon(painterResource(id = R.drawable.calender),
                                contentDescription = "Calendar Icon",
                                modifier = Modifier.clickable(enabled = uiState.isEditing) { viewModel.onShowDatePickerDialog("salida") }) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Datos de rescate", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item {
                        FormTextField(
                            label = "Lugar de rescate",
                            value = uiState.lugarRescate,
                            onValueChange = viewModel::onLugarRescateChange,
                            enabled = uiState.isEditing,
                            isError = uiState.lugarRescateError
                        )
                    }
                    item {
                        FormTextField(
                            label = "Condiciones de rescate",
                            value = uiState.condicionesRescate,
                            onValueChange = viewModel::onCondicionesRescateChange,
                            enabled = uiState.isEditing,
                            singleLine = false,
                            isError = uiState.condicionesRescateError
                        )
                    }
                     item {
                        FormTextField(
                            label = "Fecha de ingreso",
                            value = uiState.fechaIngreso,
                            onValueChange = viewModel::onFechaIngresoChange,
                            enabled = uiState.isEditing,
                            trailingIcon = { Icon(painterResource(id = R.drawable.calender),
                                contentDescription = "Calendar Icon",
                                modifier = Modifier.clickable(enabled = uiState.isEditing) { viewModel.onShowDatePickerDialog("ingreso") }) },
                            isError = uiState.fechaIngresoError
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        if (uiState.isEditing) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.savePatient {
                                            navController.navigate(AppScreens.GaleriaScreen.route)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7AB659))
                                ) {
                                    Text("Guardar", fontSize = 16.sp, color = Color.White)
                                }
                                Button(
                                    onClick = {
                                        if (patientId != null) {
                                            viewModel.init(patientId, false)
                                        } else {
                                            navController.popBackStack()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                ) {
                                    Text("Cancelar", fontSize = 16.sp, color = Color.White)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }
            }
        }
    }
}
