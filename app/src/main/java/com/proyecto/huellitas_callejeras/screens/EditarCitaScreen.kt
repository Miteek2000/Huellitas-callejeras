package com.proyecto.huellitas_callejeras.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.huellitas_callejeras.screens.components.AnimalSearchDropdown
import com.proyecto.huellitas_callejeras.viewmodels.CitasMedicasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCitaScreen(
    navController: NavController,
    viewModel: CitasMedicasViewModel = viewModel(),
    citaId: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()
    val allAnimals by viewModel.allAnimals.collectAsState()
    val selectedAnimal by viewModel.selectedAnimal.collectAsState()

    val loading = uiState.loading
    val error = uiState.error

    val isEditMode = citaId.isNotEmpty()


    LaunchedEffect(citaId) {
        if (isEditMode) {

            viewModel.getCitaPorId(citaId) { cita ->
                if (cita != null) {
                    viewModel.setEditingCita(cita)

                    if (cita.animalId.isNotEmpty()) {
                        val animal = allAnimals.find { animal ->
                            animal.idAnimal == cita.animalId
                        }
                        animal?.let { foundAnimal ->
                            viewModel.setSelectedAnimal(foundAnimal)
                        }
                    }
                } else {
                    navController.popBackStack()
                }
            }
        } else {
            viewModel.setEditingCita(viewModel.nuevaCitaVacia())
        }
    }

    LaunchedEffect(Unit) {
        if (allAnimals.isEmpty()) {
            viewModel.cargarAnimales()
        }
    }

    LaunchedEffect(selectedAnimal) {
        selectedAnimal?.let { animal ->
            uiState.cita?.let { cita ->
                if (cita.animalId != animal.idAnimal) {  // ← CAMBIÉ animalitoId por animalId
                    viewModel.updateEditingCita {
                        it.copy(animalId = animal.idAnimal)  // ← CAMBIÉ animalitoId por animalId
                    }
                }
            }
        }
    }

    // Mostrar loading inicial
    if (loading && allAnimals.isEmpty()) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF5B2D5B))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando...", color = Color.Gray)
            }
        }
        return
    }

    // Obtener la cita actual o crear una nueva
    val cita = uiState.cita ?: run {
        // Si estamos en modo edición y no hay cita, mostrar error
        if (isEditMode) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: Cita no encontrada", color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.popBackStack() }
                    ) {
                        Text("Regresar")
                    }
                }
            }
            return
        } else {
            // En modo creación, crear una nueva cita
            viewModel.nuevaCitaVacia()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                viewModel.clearEditingCita()
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (isEditMode) "Editar Cita" else "Nueva Cita",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Color(0xFFF7D0E2), RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            CitaTextField(
                label = "Título *",
                value = cita.titulo,
                onValueChange = { newValue ->
                    viewModel.updateEditingCita { it.copy(titulo = newValue) }
                }
            )

            Text(
                text = "Animalito asociado *",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            AnimalSearchDropdown(
                animals = allAnimals,
                selectedAnimal = selectedAnimal,
                onAnimalSelected = { animal ->
                    viewModel.onAnimalSelected(animal)
                },
                modifier = Modifier.fillMaxWidth(),
                label = if (selectedAnimal == null) "Escribe el nombre del animalito"
                else selectedAnimal!!.nombre
            )

            Spacer(Modifier.height(16.dp))

            CitaTextField(
                label = "Fecha cita * (Formato: YYYY-MM-DDTHH:mm:ssZ)",
                value = cita.fechaCita,
                onValueChange = { newValue ->
                    viewModel.updateEditingCita { it.copy(fechaCita = newValue) }
                }
            )

            CitaTextField(
                label = "Lugar *",
                value = cita.lugar,
                onValueChange = { newValue ->
                    viewModel.updateEditingCita { it.copy(lugar = newValue) }
                }
            )

            CitaTextField(
                label = "Motivo *",
                value = cita.motivo,
                onValueChange = { newValue ->
                    viewModel.updateEditingCita { it.copy(motivo = newValue) }
                },
            )

            Spacer(Modifier.height(24.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                Button(
                    onClick = {
                        viewModel.clearEditingCita()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("Cancelar", color = Color.Black)
                }


                Button(
                    onClick = {
                        viewModel.saveCita {
                            viewModel.clearEditingCita()
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7AB659)),
                    enabled = cita.titulo.isNotEmpty() &&
                            cita.fechaCita.isNotEmpty() &&
                            cita.lugar.isNotEmpty() &&
                            cita.motivo.isNotEmpty() &&
                            cita.animalId.isNotEmpty() &&  // ← CAMBIÉ animalitoId por animalId
                            !loading
                ) {
                    Text(
                        if (loading) "Guardando..." else "Guardar",
                        color = Color.White
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, color = Color.Black, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color(0xFF5B2D5B),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}