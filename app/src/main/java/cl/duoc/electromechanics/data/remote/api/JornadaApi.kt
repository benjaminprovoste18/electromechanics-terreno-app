package cl.duoc.electromechanics.data.remote.api

import cl.duoc.electromechanics.data.remote.dto.JornadaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface JornadaApi {
    @GET("api/jornadas")
    suspend fun listar(): List<JornadaDto>

    @PUT("api/jornadas/{id}")
    suspend fun guardar(@Path("id") id: String, @Body registro: JornadaDto): JornadaDto
}
