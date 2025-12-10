package com.proyecto.huellitas_callejeras.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
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
import com.proyecto.huellitas_callejeras.navegacion.NavManager
import com.proyecto.huellitas_callejeras.ui.theme.Calendar
import com.proyecto.huellitas_callejeras.viewmodels.CitasMedicasViewModel
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasMedicasScreen(
    navController: NavController,
    viewModel: CitasMedicasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val calendar by viewModel.calendar.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    LaunchedEffect(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)) {
        viewModel.cargarCitas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Citas Médicas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5B2D5B),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val nuevaCita = viewModel.nuevaCitaVacia().copy(
                        fechaCita = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                            .format(Date(selectedDate))
                    )
                    viewModel.setEditingCita(nuevaCita)
                    navController.navigate(AppScreens.EditarCitaScreen.route)
                },
                containerColor = Color(0xFF5B2D5B),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar cita")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {

            Calendar(
                calendar = calendar,
                onDateSelected = { dateMillis ->
                    viewModel.onDateSelected(dateMillis)
                },
                onMonthChanged = viewModel::onMonthChanged,
                onYearChanged = viewModel::onYearChanged,
                datesWithAppointments = viewModel.datesWithAppointments
            )

            val selectedDateFormatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date(selectedDate))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Citas para: $selectedDateFormatted",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5B2D5B),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${viewModel.appointmentsForSelectedDate.size} citas",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val citasDelDia = viewModel.appointmentsForSelectedDate

                if (citasDelDia.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.calender),
                            contentDescription = "Sin citas",
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay citas para esta fecha",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Toca el botón + para agregar una",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(citasDelDia) { cita ->
                            AppointmentCard(
                                cita = cita,
                                onEditClick = {
                                    viewModel.setEditingCita(cita)
                                    navController.navigate(
                                        AppScreens.EditarCitaScreen.route + "?id=${cita.id}"
                                    )
                                },
                                onDeleteClick = {
                                    // Mostrar confirmación antes de eliminar
                                    viewModel.eliminarCita(cita.id) {
                                        viewModel.filtrarCitasPorFecha(selectedDate)
                                    }
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
            }

            uiState.error?.let { error ->
                if (error.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x1AFF0000), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    }
                }
            }

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
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEditClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7D0E2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que quieres eliminar esta cita?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirm = false
                            onDeleteClick()
                        }
                    ) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirm = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val (title, time, edit, delete, details, patient) = createRefs()

            Text(
                cita.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(edit.start)
                    width = Dimension.fillToConstraints
                }
            )

            val timeText = try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val date = sdf.parse(cita.fechaCita)
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date ?: Date())
            } catch (e: Exception) {
                "Sin hora"
            }

            Text(
                timeText,
                color = Color(0xFF5B2D5B),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.constrainAs(time) {
                    top.linkTo(title.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                }
            )

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.constrainAs(edit) {
                    top.linkTo(parent.top)
                    end.linkTo(delete.start)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edittratamiento),
                    contentDescription = "Editar cita",
                    tint = Color(0xFF5B2D5B)
                )
            }

            IconButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.constrainAs(delete) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.basura),
                    contentDescription = "Eliminar cita",
                    tint = Color.Red
                )
            }

            Column(
                modifier = Modifier.constrainAs(details) {
                    top.linkTo(time.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(patient.start)
                    width = Dimension.fillToConstraints
                }
            ) {
                if (cita.motivo.isNotEmpty()) {
                    Text(
                        "Motivo: ${cita.motivo}",
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (cita.lugar.isNotEmpty()) {
                    Text(
                        "Lugar: ${cita.lugar}",
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (cita.animalId.isNotEmpty()) {
                Column(
                    modifier = Modifier.constrainAs(patient) {
                        top.linkTo(time.bottom)
                        end.linkTo(parent.end)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.dog),
                        contentDescription = "Ver expediente del paciente",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(onClick = onPatientIconClick)
                            .clip(CircleShape)
                    )
                    Text(
                        "Paciente",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}