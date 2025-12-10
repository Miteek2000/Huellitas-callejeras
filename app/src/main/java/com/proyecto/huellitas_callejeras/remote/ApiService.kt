package com.proyecto.huellitas_callejeras.remote


import com.proyecto.huellitas_callejeras.models.Animal
import com.proyecto.huellitas_callejeras.models.CreateRescatistaRequest
import com.proyecto.huellitas_callejeras.models.LoginRequest
import com.proyecto.huellitas_callejeras.models.LoginResponse
import com.proyecto.huellitas_callejeras.models.RescatistaData
import com.proyecto.huellitas_callejeras.remote.dto.AnimalDto
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRequest
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRescateRequest
import com.proyecto.huellitas_callejeras.remote.dto.AnimalRescateResponse
import com.proyecto.huellitas_callejeras.remote.dto.ApiResponse
import com.proyecto.huellitas_callejeras.remote.dto.CitaDTO
import com.proyecto.huellitas_callejeras.remote.dto.CitaRequest
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoDTO
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoRequest
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoTratamientoDto
import com.proyecto.huellitas_callejeras.remote.dto.RescateDTO
import com.proyecto.huellitas_callejeras.remote.dto.RescateRequest
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoDto
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoMedicamentoRequest
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoResumenDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @GET("animal")
    suspend fun getAnimalitos(): ApiResponse<List<AnimalDto>>

    @GET("animal/{id}")
    suspend fun getAnimalito(@Path("id") id: String): AnimalDto

    @POST("animal")
    suspend fun createAnimalito(@Body body: AnimalRequest): AnimalDto

    @PUT("animal/{id}")
    suspend fun updateAnimalito(@Path("id") id: String, @Body body: AnimalRequest): AnimalDto

    @DELETE("animal/{id}")
    suspend fun deleteAnimalito(@Path("id") id: String)

    @POST("animal/crear-con-rescate")
    suspend fun createAnimalitoConRescate(
        @Body request: AnimalRescateRequest
    ): ApiResponse<AnimalRescateResponse>

    @Multipart
    @POST("animal/crear-con-rescate")
    suspend fun createAnimalitoConRescateConImagen(
        @Part("animal") animal: RequestBody,
        @Part("rescate") rescate: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): ApiResponse<AnimalRescateResponse>

    @PUT("animal/{id}/actualizar-con-rescate")
    suspend fun updateAnimalitoConRescate(
        @Path("id") id: String,
        @Part("animal") animal: RequestBody,
        @Part("rescate") rescate: RequestBody,
    ): ApiResponse<AnimalRescateResponse>

    @Multipart
    @PUT("animal/{id}/actualizar-con-rescate")
    suspend fun updateAnimalitoConRescateConImagen(
        @Path("id") id: String,
        @Part("animal") animal: RequestBody,
        @Part("rescate") rescate: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): ApiResponse<AnimalRescateResponse>

    @GET("animal/{id}/con-rescate")
    suspend fun getAnimalitoConRescate(
        @Path("id") id: String
    ): ApiResponse<AnimalRescateResponse>

    @GET("citas")
    suspend fun getCitas(): ApiResponse<List<CitaDTO>>


    @GET("citas/{id}")
    suspend fun getCita(@Path("id") id: String): ApiResponse<CitaDTO>

    @POST("citas")
    suspend fun createCita(@Body body: CitaRequest): ApiResponse<CitaDTO>

    @PUT("citas/{id}")
    suspend fun updateCita(@Path("id") id: String, @Body body: CitaRequest) : ApiResponse<Unit?>


    @DELETE("citas/{id}")
    suspend fun deleteCita(@Path("id") id: String) : ApiResponse<Unit?>



    @GET("tratamientos")
    suspend fun getAllTratamientos(): Response<ApiResponse<List<TratamientoDto>>>

    @GET("tratamientos/{id}")
    suspend fun getTratamiento(@Path("id") id: String): Response<ApiResponse<TratamientoDto>>

    @Multipart
    @POST("tratamientos")
    suspend fun createTratamiento(
        @Part("tratamiento") tratamiento: RequestBody,
        @Part archivo: MultipartBody.Part?
    ): Response<ApiResponse<TratamientoDto>>

    @PUT("tratamientos/{id}")
    @Multipart
    suspend fun updateTratamiento(
        @Path("id") id: String,
        @Part("tratamiento") tratamiento: RequestBody,
        @Part archivo: MultipartBody.Part?
    ): Response<ApiResponse<TratamientoDto>>

    @DELETE("tratamientos/{id}")
    suspend fun deleteTratamiento(@Path("id") id: String): Response<ApiResponse<Unit>>

    @GET("medicamentos")
    suspend fun getAllMedicamentos(): Response<ApiResponse<List<MedicamentoDTO>>>

    @POST("medicamentos")
    suspend fun createMedicamento(@Body request: MedicamentoRequest): Response<ApiResponse<MedicamentoDTO>>

    @DELETE("medicamentos/{id}")
    suspend fun deleteMedicamento(@Path("id") id: String): Response<ApiResponse<Unit>>

    @GET("tratamientos/animal/{animalId}")
    suspend fun getTratamientoByAnimalId(
        @Path("animalId") animalId: String
    ): Response<ApiResponse<List<TratamientoResumenDto>>>

    @GET("tratamientos/{tratamientoId}/medicamentos")
    suspend fun getMedicamentosByTratamientoId(
        @Path("tratamientoId") tratamientoId: String
    ): Response<ApiResponse<List<MedicamentoTratamientoDto>>>


}

