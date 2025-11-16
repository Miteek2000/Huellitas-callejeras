package com.mayte.huellitas_callejeras.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mayte.huellitas_callejeras.Screens.GaleriaScreen
import com.mayte.huellitas_callejeras.Screens.InicioSesion

@Composable
fun NavManager(){
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = AppScreens.InicioSesion.route) {
    composable(AppScreens.InicioSesion.route) {
        InicioSesion(navController)
    }
    composable(AppScreens.GaleriaScreen.route) {
        GaleriaScreen(navController)
    }
  }
}
