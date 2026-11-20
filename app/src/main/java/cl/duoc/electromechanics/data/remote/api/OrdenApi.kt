package cl.duoc.electromechanics.data.remote.api

import cl.duoc.electromechanics.data.remote.dto.ActividadDto
import cl.duoc.electromechanics.data.remote.dto.OrdenDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrdenApi {
    @GET("api/ordenes")
    suspend fun listar(): List<OrdenDto>

    @PUT("api/ordenes/{id}")
    suspend fun guardar(@Path("id") id: String, @Body orden: OrdenDto): OrdenDto

    @GET("api/actividades")
    suspend fun listarActividades(): List<ActividadDto>

    @PUT("api/actividades/{id}")
    suspend fun guardarActividad(@Path("id") id: String, @Body actividad: ActividadDto): ActividadDto
}
