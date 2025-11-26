package com.proyecto.huellitas_callejeras.repository

import com.proyecto.huellitas_callejeras.models.Cita
import com.proyecto.huellitas_callejeras.remote.ApiService
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
}class CitasRepositoryImpl(
    private val api: ApiService
) : CitasRepository {

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
        val response = api.getCitas()
        if (response.success) {
            return response.data?.map { dtoToDomain(it) } ?: emptyList()
        } else {
            throw Exception(response.message ?: "Error al obtener citas")
        }
    }

    override suspend fun getCita(id: String): Cita {
        val response = api.getCita(id)
        if (response.success) {
            return dtoToDomain(response.data)
        } else {
            throw Exception(response.message ?: "Error al obtener la cita")
        }
    }

    override suspend fun createCita(cita: Cita): Cita {
        val body = domainToRequest(cita)
        val response = api.createCita(body)
        if (response.success) {
            return dtoToDomain(response.data)
        } else {
            throw Exception(response.message ?: "Error al crear la cita")
        }
    }

    override suspend fun updateCita(cita: Cita) {
        val body = domainToRequest(cita)
        val response = api.updateCita(cita.idCitas, body)
        if (!response.success) {
            throw Exception(response.message ?: "Error al actualizar la cita")
        }
    }

    override suspend fun deleteCita(id: String) {
        val response = api.deleteCita(id)
        if (!response.success) {
            throw Exception(response.message ?: "Error al eliminar la cita")
        }
    }
}