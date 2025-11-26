package com.proyecto.huellitas_callejeras.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.huellitas_callejeras.R
import com.proyecto.huellitas_callejeras.models.Cita
import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.navegacion.AppScreens
import com.proyecto.huellitas_callejeras.ui.theme.Calendar
import com.proyecto.huellitas_callejeras.viewmodels.CitasMedicasViewModel
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasMedicasScreen(
    navController: NavController,
    viewModel: CitasMedicasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val calendar by viewModel.calendar.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Huellitas Callejeras") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5B2D5B),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.setEditingCita(viewModel.nuevaCitaVacia())
                    navController.navigate(AppScreens.EditarCitaScreen.route)
                },
                containerColor = Color(0xFF5B2D5B),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Cita")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Header de Citas Médicas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8C6D4))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Citas Médicas",
                    fontSize = 20.sp,
                    color = Color(0xFF5B2D5B),
                    fontWeight = FontWeight.Bold
                )
            }

            // Tu componente Calendar existente - CORREGIDO
            Calendar(
                calendar = calendar,
                onDateSelected = viewModel::onDateSelected,
                onMonthChanged = viewModel::onMonthChanged,
                onYearChanged = viewModel::onYearChanged,
                datesWithAppointments = viewModel.datesWithAppointments
            )

            // Mostrar la fecha seleccionada
            val selectedDateFormatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date(selectedDate))

            Text(
                text = "Citas para: $selectedDateFormatted",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5B2D5B),
                fontSize = 16.sp
            )

            // Lista de citas para la fecha seleccionada
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                val citasDelDia = viewModel.appointmentsForSelectedDate

                if (citasDelDia.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay citas para esta fecha",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    items(citasDelDia) { cita ->
                        AppointmentCard(
                            cita = cita,
                            onEditClick = {
                                viewModel.setEditingCita(cita)
                                navController.navigate(
                                    AppScreens.EditarCitaScreen.route + "?id=${cita.idCitas}"
                                )
                            },
                            onDeleteClick = {
                                viewModel.eliminarCita(cita.idCitas)
                            },
                            onPatientIconClick = {
                                val animalId = viewModel.onPatientIconClicked(cita)
                                animalId?.let { id ->
                                    navController.navigate(
                                        AppScreens.ExpedienteScreen.route + "?patientId=$id&editable=false"
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Mostrar errores
            uiState.error?.let { error ->
                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Mostrar loading
            if (uiState.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5B2D5B))
                }
            }
        }
    }
}


@Composable
fun AppointmentCard(
    cita: Cita,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPatientIconClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7D0E2))
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val (title, edit, delete, date, place, realization, patientInfo) = createRefs()

            Text(
                cita.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(edit.start)
                    width = Dimension.fillToConstraints
                }
            )

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.constrainAs(edit) {
                    top.linkTo(parent.top)
                    end.linkTo(delete.start, margin = 8.dp)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edittratamiento),
                    contentDescription = "Edit",
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.constrainAs(delete) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.basura),
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Fecha Cita: ")
                    }
                    append(cita.fechaCita)
                },
                modifier = Modifier.constrainAs(date) {
                    top.linkTo(title.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                }
            )

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Lugar: ")
                    }
                    append(cita.lugar)
                },
                modifier = Modifier.constrainAs(place) {
                    top.linkTo(date.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                }
            )

            if (cita.fechaRealizacion.isNotEmpty()) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Fecha Realización: ")
                        }
                        append(cita.fechaRealizacion)
                    },
                    modifier = Modifier.constrainAs(realization) {
                        top.linkTo(place.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                    }
                )
            }

            Column(
                modifier = Modifier.constrainAs(patientInfo) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    top.linkTo(place.bottom, margin = 8.dp)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (cita.animalitoId.isNotEmpty()) {
                    Text(
                        text = "Paciente",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.dog),
                    contentDescription = "Ver expediente del paciente",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(
                            onClick = onPatientIconClick,

                        )
                )
            }
        }
    }
}