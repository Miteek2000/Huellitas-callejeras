package com.proyecto.huellitas_callejeras.repository

import android.content.Context
import com.proyecto.huellitas_callejeras.models.Cita
import com.proyecto.huellitas_callejeras.remote.ApiService
import com.proyecto.huellitas_callejeras.remote.AuthService
import com.proyecto.huellitas_callejeras.remote.RetrofitClient
import com.proyecto.huellitas_callejeras.remote.dto.CitaDTO
import com.proyecto.huellitas_callejeras.remote.dto.CitaRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

interface CitasRepository {
    suspend fun getCitas(): List<Cita>
    suspend fun getCita(id: String): Cita
    suspend fun createCita(cita: Cita): Cita
    suspend fun updateCita(cita: Cita)
    suspend fun deleteCita(id: String)
}

class CitasRepositoryImpl(
    private val api: ApiService,
    private val context: Context
) : CitasRepository {

    private val authService = AuthService(context)

    private suspend fun getAuthenticatedService(): ApiService {
        val token = authService.getToken()
            ?: throw Exception("No hay sesión activa. Por favor inicia sesión.")
        return RetrofitClient.createAuthenticatedService(token)
    }

    private fun dtoToDomain(dto: CitaDTO?): Cita {
        return Cita(
            idCitas = dto?.id_citas.toString(),
            titulo = dto?.titulo ?: "sin titulo",
            fechaRealizacion = dto?.fechaRealizacion ?: LocalDateTime.now().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            fechaCita = dto?.fechaCita ?: LocalDateTime.now().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            motivo = dto?.motivo ?: "sin motivo",
            lugar = dto?.lugar ?: "sin lugar",
            animalitoId = dto?.animalitoId?.toString() ?: ""
        )
    }

    private fun domainToRequest(cita: Cita): CitaRequest {
        return CitaRequest(
            titulo = cita.titulo,
            fechaCita = cita.fechaCita,
            motivo = cita.motivo,
            lugar = cita.lugar,
            fechaRealizacion = cita.fechaRealizacion,
            animalitoId = try {
                if (cita.animalitoId.isNotBlank()) UUID.fromString(cita.animalitoId) else null
            } catch (e: Exception) {
                null
            }
        )
    }

    override suspend fun getCitas(): List<Cita> {
        return try {
            val service = getAuthenticatedService()
            val response = service.getCitas()

            if (response.success) {
                response.data?.map { dtoToDomain(it) } ?: emptyList()
            } else {
                throw Exception(response.message ?: "Error al obtener citas")
            }
        } catch (e: Exception) {
            throw Exception("Error al cargar las citas: ${e.message}")
        }
    }

    override suspend fun getCita(id: String): Cita {
        return try {
            val service = getAuthenticatedService()
            val response = service.getCita(id)

            if (response.success) {
                dtoToDomain(response.data)
            } else {
                throw Exception(response.message ?: "Error al obtener la cita")
            }
        } catch (e: Exception) {
            throw Exception("Error al cargar la cita: ${e.message}")
        }
    }

    override suspend fun createCita(cita: Cita): Cita {
        return try {
            val service = getAuthenticatedService()
            val body = domainToRequest(cita)
            val response = service.createCita(body)

            if (response.success) {
                dtoToDomain(response.data)
            } else {
                throw Exception(response.message ?: "Error al crear la cita")
            }
        } catch (e: Exception) {
            throw Exception("Error al crear la cita: ${e.message}")
        }
    }

    override suspend fun updateCita(cita: Cita) {
        try {
            val service = getAuthenticatedService()
            val body = domainToRequest(cita)
            val response = service.updateCita(cita.idCitas, body)

            if (!response.success) {
                throw Exception(response.message ?: "Error al actualizar la cita")
            }
        } catch (e: Exception) {
            throw Exception("Error al actualizar la cita: ${e.message}")
        }
    }

    override suspend fun deleteCita(id: String) {
        try {
            val service = getAuthenticatedService()
            val response = service.deleteCita(id)

            if (!response.success) {
                throw Exception(response.message ?: "Error al eliminar la cita")
            }
        } catch (e: Exception) {
            throw Exception("Error al eliminar la cita: ${e.message}")
        }
    }
}