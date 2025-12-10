package com.proyecto.huellitas_callejeras.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.proyecto.huellitas_callejeras.R
import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.ui.theme.pink1
import com.proyecto.huellitas_callejeras.ui.theme.purple1
import com.proyecto.huellitas_callejeras.viewmodels.GaleriaNavTarget
import com.proyecto.huellitas_callejeras.viewmodels.GaleriaViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaleriaScreen(
    navController: NavController,
    galeriaViewModel: GaleriaViewModel = viewModel(),
    from: String? = null
) {
    val patients by galeriaViewModel.patients.collectAsState()
    val searchText by galeriaViewModel.searchText.collectAsState()

    LaunchedEffect(Unit) {
        galeriaViewModel.navEvents.collect { target ->
            when (target) {
                GaleriaNavTarget.InicioSesion ->
                    navController.navigate(AppScreens.InicioSesion.route)

                GaleriaNavTarget.CitasMedicas ->
                    navController.navigate(AppScreens.CitasMedicasScreen.route)

                GaleriaNavTarget.NuevoExpediente ->
                    navController.navigate(AppScreens.ExpedienteScreen.route + "?editable=true")

                is GaleriaNavTarget.Expediente ->
                    navController.navigate(AppScreens.ExpedienteScreen.route + "?patientId=${target.patientId}&editable=false")

                is GaleriaNavTarget.GoBackWithResult -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set("patient", target.animal)
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Huellitas Callejeras") },
                navigationIcon = {
                    Row {
                        IconButton(onClick = galeriaViewModel::onHomeClicked) {
                            Icon(
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = "Home",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = galeriaViewModel::onCalendarClicked) {
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
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5B2D5B),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(Color(0xFFE8C6D4), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = galeriaViewModel::onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
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
                        .clickable(onClick = galeriaViewModel::onAddClicked)
                        .padding(4.dp),
                    tint = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
            ) {
                TextField(
                    value = searchText,
                    onValueChange = galeriaViewModel::onSearchTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Buscar paciente por nombre o ID", color = Color.Gray, fontSize = 15.sp)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // --- LISTA ---
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(patients) { patient ->
                        PatientCard(
                            animal = patient,
                            onDelete = { galeriaViewModel.removePatient(patient) },
                            onPatientClick = { galeriaViewModel.onPatientClicked(patient, from) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PatientCard(
    animal: Animal,
    onDelete: () -> Unit,
    onPatientClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onPatientClick),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = animal.urlImagen,
                contentDescription = animal.nombre,
                modifier = Modifier.size(126.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ejemploexpediente),
                error = painterResource(id = R.drawable.ejemploexpediente)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = animal.nombre, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))

                if (animal.estado == "adoptado") {
                    Icon(
                        painter = painterResource(id = R.drawable.huella),
                        contentDescription = "Adoptado",
                        tint = Color(0xFF7AB659),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            animal.raza?.let {
                Text(text = it, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(id = R.drawable.basura),
                    contentDescription = "Delete",
                    tint = Color.Gray
                )
            }
        }
    }
}
