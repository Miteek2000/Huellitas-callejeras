package com.mayte.huellitas_callejeras.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mayte.huellitas_callejeras.R
import com.mayte.huellitas_callejeras.models.Patient
import com.mayte.huellitas_callejeras.viewmodels.GaleriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaleriaScreen(galeriaViewModel: GaleriaViewModel = viewModel()) {
    val patients by galeriaViewModel.patients.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECDCE4))
    ) {
        TopAppBar(
            title = { Text("Huellitas Callejeras") },
            navigationIcon = {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Calendar")
                }
            },
            actions = {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Filled.Home, contentDescription = "Home")
                }
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF5B2D5B),
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE5B4D3))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            Text(text = "Galeria de expedientes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Green)
                    .clickable { galeriaViewModel.addPatient() }
                    .padding(4.dp),
                tint = Color.White
            )
        }

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Buscar paciente por nombre o ID") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5B2D5B),
                unfocusedBorderColor = Color.Gray,
            )
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(patients) { patient ->
                PatientCard(patient = patient, onDelete = { galeriaViewModel.removePatient(patient) })
            }
        }
    }
}

@Composable
fun PatientCard(patient: Patient, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.clickable { /* Handle selection */ },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Using logo as a placeholder
                contentDescription = patient.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = patient.name, fontWeight = FontWeight.Bold)
                if (patient.isVaccinated) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.Pets,
                        contentDescription = "Vaccinated",
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(text = patient.breed, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}
