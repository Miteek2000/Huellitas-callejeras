package com.mayte.huellitas_callejeras.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mayte.huellitas_callejeras.R
import com.mayte.huellitas_callejeras.models.Patient
import com.mayte.huellitas_callejeras.ui.theme.pink1
import com.mayte.huellitas_callejeras.ui.theme.purple1
import com.mayte.huellitas_callejeras.viewmodels.GaleriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaleriaScreen(navController: NavController, galeriaViewModel: GaleriaViewModel = viewModel()) {
    val patients by galeriaViewModel.patients.collectAsState()
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Huellitas Callejeras") },
            navigationIcon = {
                Row {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Home", tint = Color.White)
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(painter = painterResource(id = R.drawable.calender), contentDescription = "Calendar", tint = Color.White)
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
                .background(Color(0xFFE8C6D4), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Text(
                    text = "Galeria de expedientes",
                    fontSize = 18.sp,
                    color = Color(0xFF5B2D5B)
                )
            }
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF7AB659))
                    .clickable { galeriaViewModel.addPatient() }
                    .padding(4.dp),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFE8C6D4), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {

                TextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        //Agregar en viewmodels funcionalidad searchPatients()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    placeholder = { Text("Buscar paciente por nombre o ID",
                      color = Color.Gray,
                      fontSize = 15.sp,
                      lineHeight = 5.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search,
                          contentDescription = "Search", tint = Color.Gray,
                          modifier = Modifier.size(20.dp))
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, lineHeight = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF5B2D5B),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.verticalGradient(listOf(pink1, purple1)))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(patients) { patient ->
                    PatientCard(patient = patient, onDelete = { galeriaViewModel.removePatient(patient) })
                }
            }
        }
    }
}

@Composable
fun PatientCard(patient: Patient, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.clickable {  },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ejemploexpediente),
                contentDescription = patient.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(126.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = patient.name, fontWeight = FontWeight.Bold)
                if (patient.isAdopted) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.huella),
                        contentDescription = "Adoptado",
                        tint = Color(0xFF7AB659),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(text = patient.breed, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(onClick = onDelete) {
                Icon(painter = painterResource(id = R.drawable.basura), contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}
