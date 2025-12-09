package com.proyecto.huellitas_callejeras.remote.dto

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
