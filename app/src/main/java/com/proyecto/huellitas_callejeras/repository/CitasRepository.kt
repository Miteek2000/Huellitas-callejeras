package com.proyecto.huellitas_callejeras.repository

import com.proyecto.huellitas_callejeras.models.Cita
import com.proyecto.huellitas_callejeras.remote.ApiService
import com.proyecto.huellitas_callejeras.remote.dto.CitaDTO
import com.proyecto.huellitas_callejeras.remote.dto.CitaRequest
import java.util.UUID

interface CitasRepository {
    suspend fun getCitas(): List<Cita>
    suspend fun getCita(id: String): Cita
    suspend fun createCita(cita: Cita): Cita
    suspend fun updateCita(cita: Cita)
    suspend fun deleteCita(id: String)
}

class CitasRepositoryImpl(
    private val api: ApiService
) : CitasRepository {

    private fun dtoToDomain(dto: CitaDTO): Cita {
        return Cita(
            idCitas = dto.id_citas.toString(),
            titulo = dto.titulo,
            fechaRealizacion = dto.fecha_realizacion,
            fechaCita = dto.fecha_cita,
            motivo = dto.motivo,
            lugar = dto.lugar,
            animalitoId = dto.animalito_id?.toString() ?: ""
        )
    }

    private fun domainToRequest(cita: Cita): CitaRequest {
        return CitaRequest(
            titulo = cita.titulo,
            fecha_cita = cita.fechaCita,
            motivo = cita.motivo,
            lugar = cita.lugar,
            url_imagen = "",
            animalito_id = if (cita.animalitoId.isNotBlank()) UUID.fromString(cita.animalitoId) else null
        )
    }

    override suspend fun getCitas(): List<Cita> {
        val dtos = api.getCitas()
        return dtos.map { dtoToDomain(it) }
    }

    override suspend fun getCita(id: String): Cita {
        val dto = api.getCita(id)
        return dtoToDomain(dto)
    }

    override suspend fun createCita(cita: Cita): Cita {
        val body = domainToRequest(cita)
        val dto = api.createCita(body)
        return dtoToDomain(dto)
    }

    override suspend fun updateCita(cita: Cita) {
        val body = domainToRequest(cita)
        // la API espera el id en path
        api.updateCita(cita.idCitas, body)
    }

    override suspend fun deleteCita(id: String) {
        api.deleteCita(id)
    }
}
