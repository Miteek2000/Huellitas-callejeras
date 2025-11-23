package com.proyecto.huellitas_callejeras.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.huellitas_callejeras.viewmodels.CitasMedicasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCitaScreen(
    navController: NavController,
    viewModel: CitasMedicasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cita = uiState.cita ?: return run { navController.popBackStack(); false }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Color(0xFFF7D0E2), RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // CORREGIDO: Pasar onValueChange correctamente
            CitaTextField(
                label = "Título",
                value = cita.titulo,
                onValueChange = { newValue ->
                    viewModel.updateEditingCita { it.copy(titulo = newValue) }
                }
            )

            CitaTextField(
                label = "Fecha cita",
                value = cita.fechaCita,
                onValueChange = { newValue ->
                    viewModel.updateEditingCita { it.copy(fechaCita = newValue) }
                }
            )

            CitaTextField(
                label = "Lugar",
                value = cita.lugar,
                onValueChange = { newValue ->
                    viewModel.updateEditingCita { it.copy(lugar = newValue) }
                }
            )

            CitaTextField(
                label = "Motivo",
                value = cita.motivo,
                onValueChange = { newValue ->
                    viewModel.updateEditingCita { it.copy(motivo = newValue) }
                }
            )

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Cancelar", color = Color.Black)
                }

                Button(
                    onClick = {
                        viewModel.saveCita()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7AB659))
                ) {
                    Text("Guardar", color = Color.White)
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
    onValueChange: (String) -> Unit, // Este parámetro es obligatorio
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, color = Color.Black, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange, // Se pasa aquí
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