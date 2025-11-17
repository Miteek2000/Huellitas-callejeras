package com.mayte.huellitas_callejeras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mayte.huellitas_callejeras.Screens.ExpedienteScreen
import com.mayte.huellitas_callejeras.Screens.GaleriaScreen
import com.mayte.huellitas_callejeras.Screens.InicioSesion
import com.mayte.huellitas_callejeras.navegacion.AppScreens
import com.mayte.huellitas_callejeras.ui.theme.Huellitas_callejerasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Huellitas_callejerasTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = AppScreens.InicioSesion.route) {
                    composable(route = AppScreens.InicioSesion.route) {
                        InicioSesion(navController = navController)
                    }
                    composable(route = AppScreens.GaleriaScreen.route) {
                        GaleriaScreen(navController = navController)
                    }
                    composable(
                        route = AppScreens.ExpedienteScreen.route + "?patientId={patientId}&editable={editable}",
                        arguments = listOf(
                            navArgument("patientId") {
                                type = NavType.StringType
                                nullable = true
                            },
                            navArgument("editable") {
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                    ) { backStackEntry ->
                        val patientId = backStackEntry.arguments?.getString("patientId")
                        val isEditable = backStackEntry.arguments?.getString("editable").toBoolean()

                        ExpedienteScreen(
                            navController = navController,
                            patientId = patientId,
                            isEditable = isEditable
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Huellitas_callejerasTheme {
        InicioSesion(navController = rememberNavController())
    }
}
