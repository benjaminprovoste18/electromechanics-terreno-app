package cl.duoc.electromechanics.data.remote

import cl.duoc.electromechanics.data.remote.api.JornadaApi
import cl.duoc.electromechanics.data.remote.api.OrdenApi
import cl.duoc.electromechanics.data.remote.api.SucursalApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val cliente = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun crear(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val sucursalApi: SucursalApi by lazy {
        crear(ApiConfig.URL_SUCURSALES).create(SucursalApi::class.java)
    }

    val ordenApi: OrdenApi by lazy {
        crear(ApiConfig.URL_ORDENES).create(OrdenApi::class.java)
    }

    val jornadaApi: JornadaApi by lazy {
        crear(ApiConfig.URL_JORNADAS).create(JornadaApi::class.java)
    }
}
