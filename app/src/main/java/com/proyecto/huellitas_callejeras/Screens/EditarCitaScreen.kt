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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyecto.huellitas_callejeras.components.FormTextField
import com.proyecto.huellitas_callejeras.viewmodels.CitasMedicasViewModel

@Composable
fun EditarCitaScreen(navController: NavController, viewModel: CitasMedicasViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val cita = uiState.cita

    if (cita == null) {
        // Handle error state, maybe navigate back
        navController.popBackStack()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
                .background(color = Color(0xFFF7D0E2), shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FormTextField(
                label = "Título",
                value = cita.title,
                onValueChange = { viewModel.onTitleChange(it) },
                isError = uiState.titleError,
                enabled = true
            )
            FormTextField(
                label = "Fecha Cita",
                value = cita.date,
                onValueChange = { viewModel.onDateChange(it) },
                isError = uiState.dateError,
                enabled = true
            )
            FormTextField(
                label = "Lugar",
                value = cita.place,
                onValueChange = { viewModel.onPlaceChange(it) },
                isError = uiState.placeError,
                enabled = true
            )
            FormTextField(
                label = "Motivo",
                value = cita.motive ?: "",
                onValueChange = { viewModel.onMotiveChange(it) },
                isError = uiState.motiveError,
                enabled = true
            )

            if (cita.patientName.isNotEmpty()) {
                Text("Paciente: ${cita.patientName}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Cancelar", color = Color.Black)
                }
                Button(
                    onClick = {
                        viewModel.saveCita {
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7AB659)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Guardar", color = Color.White)
                }
            }
        }
    }
}
