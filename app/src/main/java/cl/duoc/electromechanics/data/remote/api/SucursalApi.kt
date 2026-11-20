package cl.duoc.electromechanics.data.remote.api

import cl.duoc.electromechanics.data.remote.dto.SucursalDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface SucursalApi {
    @GET("api/sucursales")
    suspend fun listar(): List<SucursalDto>

    @PUT("api/sucursales/{id}")
    suspend fun guardar(@Path("id") id: String, @Body sucursal: SucursalDto): SucursalDto
}
