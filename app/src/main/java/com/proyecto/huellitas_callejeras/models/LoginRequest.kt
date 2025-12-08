package com.proyecto.huellitas_callejeras.models

data class LoginRequest(
    val nombre: String,
    val contrasena: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val data: LoginData? = null
) {
    val token: String?
        get() = data?.token

    val rescatista: RescatistaData?
        get() = data?.rescatista
}

data class LoginData(
    val token: String,
    val rescatista: RescatistaData
)

data class RescatistaData(
    val id: String,
    val nombre: String
)

data class CreateRescatistaRequest(
    val nombre: String,
    val contrasena: String
)