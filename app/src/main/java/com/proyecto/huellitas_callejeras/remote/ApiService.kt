package com.proyecto.huellitas_callejeras.remote

import com.proyecto.huellitas_callejeras.remote.dto.ApiResponse
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoDto
import com.proyecto.huellitas_callejeras.remote.dto.MedicamentoRequestDto
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoCreateRequestDto
import com.proyecto.huellitas_callejeras.remote.dto.TratamientoDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("medicamentos")
    suspend fun getAllMedicamentos(): Response<ApiResponse<List<MedicamentoDto>>>

    @GET("medicamentos/{id}")
    suspend fun getMedicamentoById(@Path("id") id: String): Response<ApiResponse<MedicamentoDto>>

    @POST("medicamentos")
    suspend fun createMedicamento(@Body request: MedicamentoRequestDto): Response<ApiResponse<MedicamentoDto>>

    @PUT("medicamentos/{id}")
    suspend fun updateMedicamento(@Path("id") id: String, @Body request: MedicamentoRequestDto): Response<ApiResponse<Unit>>

    @DELETE("medicamentos/{id}")
    suspend fun deleteMedicamento(@Path("id") id: String): Response<ApiResponse<Unit>>
    //endregion

    //region Tratamientos
    @GET("tratamientos")
    suspend fun getAllTratamientos(): Response<ApiResponse<List<TratamientoDto>>>

    @GET("tratamientos/{id}")
    suspend fun getTratamientoById(@Path("id") id: String): Response<ApiResponse<TratamientoDto>>

    @GET("tratamientos/{id}/medicamentos")
    suspend fun getMedicamentosByTratamiento(@Path("id") id: String): Response<ApiResponse<List<MedicamentoDto>>>

    @GET("tratamientos/animal/{animalId}")
    suspend fun getTratamientosByAnimal(@Path("animalId") animalId: String): Response<ApiResponse<List<TratamientoDto>>>

    @POST("tratamientos")
    suspend fun createTratamiento(@Body tratamiento: TratamientoCreateRequestDto): Response<ApiResponse<TratamientoDto>>

    @PUT("tratamientos/{id}")
    suspend fun updateTratamiento(@Path("id") id: String, @Body tratamiento: TratamientoCreateRequestDto): Response<ApiResponse<Unit>>

    @DELETE("tratamientos/{id}")
    suspend fun deleteTratamiento(@Path("id") id: String): Response<ApiResponse<Unit>>
    //endregion
}
