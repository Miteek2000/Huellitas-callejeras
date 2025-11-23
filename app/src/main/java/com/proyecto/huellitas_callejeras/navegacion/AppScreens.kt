package com.proyecto.huellitas_callejeras.navegacion


sealed class AppScreens(val route: String) {
    object InicioSesion : AppScreens("inicio_sesion")
    object GaleriaScreen : AppScreens("galeria_screen")
    object ExpedienteScreen : AppScreens("expediente_screen")
    object CitasMedicasScreen : AppScreens("citas_medicas_screen")
    object EditarCitaScreen : AppScreens("editar_cita_screen")
}
