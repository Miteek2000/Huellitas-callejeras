package com.proyecto.huellitas_callejeras.remote

import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.remote.dto.AnimalDto
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRequest
import com.proyecto.huellitas_callejeras.remote.dto.CitaDTO
import com.proyecto.huellitas_callejeras.remote.dto.CitaRequest
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoDTO
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoRequest
import com.proyecto.huellitas_callejeras.remote.dto.RescateDTO
import com.proyecto.huellitas_callejeras.remote.dto.RescateRequest
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoDTO
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoMedicamentoRequest
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("animalitos")
    suspend fun getAnimalitos(): List<Animal>

    @GET("animalitos/{id}")
    suspend fun getAnimalito(@Path("id") id: String): AnimalDto

    @POST("animalitos")
    suspend fun createAnimalito(@Body body: AnimalRequest): AnimalDto

    @PUT("animalitos/{id}")
    suspend fun updateAnimalito(@Path("id") id: String, @Body body: AnimalRequest): AnimalDto

    @DELETE("animalitos/{id}")
    suspend fun deleteAnimalito(@Path("id") id: String)


    @GET("citas")
    suspend fun getCitas(): List<CitaDTO>

    @POST("citas")
    suspend fun createCita(@Body body: CitaRequest): CitaDTO

    @DELETE("citas/{id}")
    suspend fun deleteCita(@Path("id") id: String)


    @GET("medicamentos")
    suspend fun getMedicamentos(): List<MedicamentoDTO>

    @POST("medicamentos")
    suspend fun createMedicamento(@Body body: MedicamentoRequest): MedicamentoDTO


    @GET("tratamientos")
    suspend fun getTratamientos(): List<TratamientoDTO>

    @POST("tratamientos")
    suspend fun createTratamiento(@Body body: TratamientoRequest): TratamientoDTO


    @POST("tratamientos/{id}/medicamentos")
    suspend fun agregarMedicamentoATratamiento(
        @Path("id") id: String,
        @Body body: TratamientoMedicamentoRequest
    )


    @GET("rescate")
    suspend fun getRescates(): List<RescateDTO>

    @POST("rescate")
    suspend fun createRescate(@Body body: RescateRequest): RescateDTO
}

