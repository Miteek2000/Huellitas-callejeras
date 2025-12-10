
sealed class AppScreens(val route: String) {
    object InicioSesion : AppScreens("inicio_sesion")
    object GaleriaScreen : AppScreens("galeria")
    object ExpedienteScreen : AppScreens("expediente")
    object CitasMedicasScreen : AppScreens("citas_medicas")
    object EditarCitaScreen : AppScreens("editar_cita")

    object TratamientoScreen : AppScreens("tratamiento") {
        fun createRoute(animalId: String) = "$route/$animalId"
    }

    object MedicamentoFormScreen : AppScreens("medicamento_form") {
        fun createRoute(animalId: String, medicamentoId: Int) = "$route/$animalId/$medicamentoId"
    }
}