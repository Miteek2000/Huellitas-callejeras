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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.huellitas_callejeras.R
import com.proyecto.huellitas_callejeras.components.DeleteCitaConfirmationDialog
import com.proyecto.huellitas_callejeras.models.Cita
import com.proyecto.huellitas_callejeras.navegacion.AppScreens
import com.proyecto.huellitas_callejeras.ui.theme.Calendar
import com.proyecto.huellitas_callejeras.viewmodels.CitasMedicasViewModel
import com.proyecto.huellitas_callejeras.viewmodels.CitasNavTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasMedicasScreen(navController: NavController, viewModel: CitasMedicasViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (viewModel.showDeleteConfirmationDialog) {
        DeleteCitaConfirmationDialog(
            onConfirm = viewModel::onDeleteCitaConfirmed,
            onDismiss = viewModel::onDeletionCancelled
        )
    }

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { target ->
            when (target) {
                CitasNavTarget.Galeria -> navController.navigate(AppScreens.GaleriaScreen.route)
                CitasNavTarget.CitasMedicas -> navController.navigate(AppScreens.CitasMedicasScreen.route)
                CitasNavTarget.EditarCita -> navController.navigate(AppScreens.EditarCitaScreen.route)
                is CitasNavTarget.Expediente -> navController.navigate(AppScreens.ExpedienteScreen.route + "?patientId=${target.patientId}&editable=false")
                is CitasNavTarget.GaleriaParaSeleccion -> navController.navigate(AppScreens.GaleriaScreen.route + "?from=${target.from}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Huellitas Callejeras") },
                navigationIcon = {
                    Row {
                        IconButton(onClick = viewModel::onHomeClicked) {
                            Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Home", tint = Color.White)
                        }
                        IconButton(onClick = viewModel::onCalendarClicked) {
                            Icon(painter = painterResource(id = R.drawable.calender), contentDescription = "Calendar", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5B2D5B),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onAddNewCitaClicked,
                containerColor = Color(0xFF5B2D5B),
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Cita")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues)
              .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFE8C6D4))
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Citas Médicas", fontSize = 20.sp, color = Color(0xFF5B2D5B), fontWeight = FontWeight.Bold)
            }

            Calendar(
                calendar = uiState.calendar,
                onDateSelected = viewModel::onDateSelected,
                onMonthChanged = viewModel::onMonthChanged,
                onYearChanged = viewModel::onYearChanged,
                datesWithAppointments = uiState.datesWithAppointments
            )

            LazyColumn(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp)
            ) {
                items(uiState.appointmentsForSelectedDate) { cita ->
                    AppointmentCard(
                        cita = cita,
                        onEditClick = { viewModel.onEditCitaClicked(cita) },
                        onDeleteClick = { viewModel.onDeleteCitaRequested(cita) },
                        onPatientIconClick = { viewModel.onPatientIconClicked(cita) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
        ConstraintLayout(modifier = Modifier
          .padding(16.dp)
          .fillMaxWidth()) {
            val (title, edit, delete, date, place, realization, patientInfo) = createRefs()

            Text(cita.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            })

            IconButton(onClick = onEditClick, modifier = Modifier.constrainAs(edit) {
                top.linkTo(title.top)
                bottom.linkTo(title.bottom)
                end.linkTo(parent.end)
            }) {
                Icon(painterResource(
                  id = R.drawable.edittratamiento),
                  contentDescription = "Edit",
                  modifier = Modifier.size(24.dp))
            }

            IconButton(onClick = onDeleteClick, modifier = Modifier.constrainAs(delete) {
                top.linkTo(title.top)
                bottom.linkTo(title.bottom)
                end.linkTo(edit.start, margin = 8.dp)
            }) {
                Icon(painterResource(
                  id = R.drawable.basura),
                  contentDescription = "Delete",
                  modifier = Modifier.size(24.dp))
            }

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Fecha Cita: ")
                    }
                    append(cita.date)
                },
                modifier = Modifier.constrainAs(date) {
                top.linkTo(title.bottom, margin = 16.dp)
                start.linkTo(parent.start)
            })

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Lugar: ")
                    }
                    append(cita.place)
                },
                modifier = Modifier.constrainAs(place) {
                top.linkTo(date.bottom, margin = 8.dp)
                start.linkTo(parent.start)
            })

            if (cita.realizationDate.isNotEmpty()) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Fecha Realización: ")
                        }
                        append(cita.realizationDate)
                    },
                    modifier = Modifier.constrainAs(realization) {
                    top.linkTo(place.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                })
            }

            Column(
                modifier = Modifier.constrainAs(patientInfo) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    top.linkTo(place.bottom, margin = 8.dp) // Add some margin from the top
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (cita.patientId != null) {
                    Text(
                        text = cita.patientName,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.dog),
                    contentDescription = "Patient",
                    tint = Color.Unspecified,
                    modifier = Modifier.clickable(onClick = onPatientIconClick)
                )
            }
        }
    }
}
