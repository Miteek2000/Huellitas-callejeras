package com.proyecto.huellitas_callejeras.remote

import com.proyecto.huellitas_callejeras.dto.AnimalDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AnimalApi {

    @GET("animal")
    suspend fun obtenerAnimalitos(): List<AnimalDto>

    @GET("animal/{id}")
    suspend fun obtenerPorId(@Path("id") id: String): AnimalDto

    @POST("animal")
    suspend fun crearAnimalito(@Body body: AnimalDto): AnimalDto

    @PUT("animal/{id}")
    suspend fun actualizarAnimalito(
        @Path("id") id: String,
        @Body body: AnimalDto
    ): AnimalDto

    @DELETE("animal/{id}")
    suspend fun eliminarAnimalito(@Path("id") id: String)
}
