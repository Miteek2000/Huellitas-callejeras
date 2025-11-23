package com.mayte.huellitas_callejeras.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import com.proyecto.huellitas_callejeras.R
import com.proyecto.huellitas_callejeras.navegacion.AppScreens
import com.proyecto.huellitas_callejeras.ui.theme.pink1
import com.proyecto.huellitas_callejeras.ui.theme.purple1

@Composable
fun InicioSesionScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(pink1, purple1))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .height(400.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFECDCE4)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Bienvenido",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5B2D5B)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Yadira Lizbeth Castro Velasco",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate(AppScreens.GaleriaScreen.route) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5B2D5B)
                    )
                ) {
                    Text(text = "Iniciar",
                    color = Color(0xFFF7D0E2))

                }
            }
        }
    }
}
