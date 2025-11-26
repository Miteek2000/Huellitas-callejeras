// components/AnimalSearchDropdown.kt
package com.proyecto.huellitas_callejeras.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.huellitas_callejeras.models.Animal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalSearchDropdown(
    animals: List<Animal>,
    selectedAnimal: Animal?,
    onAnimalSelected: (Animal) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Buscar animalito"
) {
    var searchText by remember { mutableStateOf(selectedAnimal?.nombre ?: "") }
    var isExpanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Actualizar searchText cuando cambia el animal seleccionado
    LaunchedEffect(selectedAnimal) {
        searchText = selectedAnimal?.nombre ?: ""
    }

    Column(modifier = modifier) {
        Box {
            OutlinedTextField(
                value = searchText,
                onValueChange = { text ->
                    searchText = text
                    isExpanded = text.isNotEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isExpanded = focusState.isFocused && searchText.isNotEmpty()
                    },
                label = { Text(label) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        isExpanded = false
                    }
                )
            )

            // Dropdown con resultados de búsqueda - CORREGIDO
            if (isExpanded && searchText.isNotEmpty()) {
                val filteredAnimals = animals.filter { animal ->
                    animal.nombre.contains(searchText, ignoreCase = true)
                }

                if (filteredAnimals.isNotEmpty()) {
                    // Usar un Box personalizado en lugar de DropdownMenu para evitar el problema
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = androidx.compose.ui.graphics.Color.White,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = androidx.compose.ui.graphics.Color.Gray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            filteredAnimals.forEach { animal ->
                                Text(
                                    text = animal.nombre,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onAnimalSelected(animal)
                                            searchText = animal.nombre
                                            isExpanded = false
                                            keyboardController?.hide()
                                        }
                                        .padding(16.dp),
                                    color = androidx.compose.ui.graphics.Color.Black
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = androidx.compose.ui.graphics.Color.White,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = androidx.compose.ui.graphics.Color.Gray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Text(
                            text = "No se encontraron animales",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                }
            }
        }

        // Mostrar información del animal seleccionado
        selectedAnimal?.let { animal ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "✅ Animal seleccionado: ${animal.nombre}",
                    color = androidx.compose.ui.graphics.Color(0xFF5B2D5B),
                    fontSize = 14.sp
                )
                if (animal.raza?.isNotEmpty() ?: false) {
                    Text(
                        text = "Raza: ${animal.raza}",
                        color = androidx.compose.ui.graphics.Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}