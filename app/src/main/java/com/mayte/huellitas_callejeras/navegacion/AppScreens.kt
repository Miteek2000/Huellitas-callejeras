package com.mayte.huellitas_callejeras.navegacion

sealed class AppScreens(val route: String) {
    object InicioSesion : AppScreens("inicio_sesion")
}
