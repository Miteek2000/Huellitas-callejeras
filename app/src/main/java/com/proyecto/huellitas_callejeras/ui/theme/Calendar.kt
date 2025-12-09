package com.proyecto.huellitas_callejeras.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun Calendar(
    calendar: Calendar,
    onDateSelected: (Long) -> Unit,
    onMonthChanged: (Boolean) -> Unit,
    onYearChanged: (Int) -> Unit,
    datesWithAppointments: Set<String>
) {
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var showYearPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onMonthChanged(false) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Mes anterior",
                    tint = Color(0xFF5B2D5B)
                )
            }

            Box {
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF5B2D5B),
                    modifier = Modifier.clickable { showYearPicker = true }
                )
                DropdownMenu(
                    expanded = showYearPicker,
                    onDismissRequest = { showYearPicker = false }
                ) {
                    val currentYear = calendar.get(Calendar.YEAR)
                    for (year in (currentYear - 10)..(currentYear + 10)) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    year.toString(),
                                    color = if (year == currentYear) Color(0xFF5B2D5B) else Color.Black
                                )
                            },
                            onClick = {
                                onYearChanged(year)
                                showYearPicker = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = { onMonthChanged(true) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Siguiente mes",
                    tint = Color(0xFF5B2D5B)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            val days = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
            for (day in days) {
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5B2D5B),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val tempCalendar = calendar.clone() as Calendar
        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = tempCalendar.get(Calendar.DAY_OF_WEEK)

        val dates = (1..daysInMonth).toList()
        val emptyCells = (1 until firstDayOfMonth).map { null }
        val allCells = emptyCells + dates

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(240.dp)
        ) {
            items(allCells.size) { index ->
                val day = allCells[index]

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (day != null) {
                        val dateCalendar = (tempCalendar.clone() as Calendar).apply {
                            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, day)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val dateMillis = dateCalendar.timeInMillis
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(Date(dateMillis))

                        val isSelected = selectedDate.time == dateMillis
                        val hasAppointment = datesWithAppointments.contains(formattedDate)

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> Color(0xFFE8C6D4)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    selectedDate = Date(dateMillis)
                                    onDateSelected(dateMillis)
                                }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    color = if (isSelected) Color(0xFF5B2D5B) else Color.Black,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )

                                if (hasAppointment) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF5B2D5B))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
