package cl.duoc.electromechanics.data.sync

import android.util.Log
import cl.duoc.electromechanics.data.local.dao.ActividadDao
import cl.duoc.electromechanics.data.local.dao.OrdenTrabajoDao
import cl.duoc.electromechanics.data.local.dao.RegistroJornadaDao
import cl.duoc.electromechanics.data.local.dao.SucursalDao
import cl.duoc.electromechanics.data.remote.api.JornadaApi
import cl.duoc.electromechanics.data.remote.api.OrdenApi
import cl.duoc.electromechanics.data.remote.api.SucursalApi
import cl.duoc.electromechanics.data.remote.dto.toDto
import cl.duoc.electromechanics.data.remote.dto.toEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException

sealed interface EstadoSync {
    data object Inactivo : EstadoSync
    data object Sincronizando : EstadoSync
    data class Sincronizado(val hora: Long) : EstadoSync
    data class SinConexion(val pendientes: Int) : EstadoSync
}

class SyncRepository(
    private val sucursalDao: SucursalDao,
    private val ordenDao: OrdenTrabajoDao,
    private val actividadDao: ActividadDao,
    private val jornadaDao: RegistroJornadaDao,
    private val sucursalApi: SucursalApi,
    private val ordenApi: OrdenApi,
    private val jornadaApi: JornadaApi,
    private val scope: CoroutineScope
) {
    private val mutex = Mutex()
    private val _estado = MutableStateFlow<EstadoSync>(EstadoSync.Inactivo)
    val estado: StateFlow<EstadoSync> = _estado.asStateFlow()

    fun solicitar() {
        scope.launch { sincronizar() }
    }

    suspend fun sincronizar(): Boolean = mutex.withLock {
        _estado.value = EstadoSync.Sincronizando
        try {
            subirPendientes()
            descargarDelServidor()
            _estado.value = EstadoSync.Sincronizado(System.currentTimeMillis())
            true
        } catch (e: CancellationException) {
            _estado.value = EstadoSync.Inactivo
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo sincronizar con el servidor: ${e.message}")
            _estado.value = EstadoSync.SinConexion(contarPendientes())
            false
        }
    }

    private suspend fun subirPendientes() {
        val sucursales = sucursalDao.pendientes().filter { enviar { sucursalApi.guardar(it.idSucursal, it.toDto()) } }
        sucursalDao.marcarSincronizadas(sucursales.map { it.idSucursal })

        val ordenes = ordenDao.pendientes().filter { enviar { ordenApi.guardar(it.idServicio, it.toDto()) } }
        ordenDao.marcarSincronizadas(ordenes.map { it.idServicio })

        val actividades = actividadDao.pendientes()
            .filter { enviar { ordenApi.guardarActividad(it.idActividad, it.toDto()) } }
        actividadDao.marcarSincronizadas(actividades.map { it.idActividad })

        val registros = jornadaDao.pendientes().filter { enviar { jornadaApi.guardar(it.idRegistro, it.toDto()) } }
        jornadaDao.marcarSincronizados(registros.map { it.idRegistro })
    }

    private suspend fun descargarDelServidor() {
        sucursalDao.guardarDesdeServidor(sucursalApi.listar().mapNotNull { it.toEntity() })
        ordenDao.guardarDesdeServidor(ordenApi.listar().mapNotNull { it.toEntity() })
        actividadDao.guardarDesdeServidor(ordenApi.listarActividades().mapNotNull { it.toEntity() })
        jornadaDao.guardarDesdeServidor(jornadaApi.listar().mapNotNull { it.toEntity() })
    }

    private suspend fun enviar(envio: suspend () -> Unit): Boolean = try {
        envio()
        true
    } catch (e: HttpException) {
        if (e.code() in 400..499) {
            Log.w(TAG, "El servidor rechazó un cambio (${e.code()}), se conserva la versión del servidor")
            true
        } else {
            throw e
        }
    }

    private suspend fun contarPendientes(): Int =
        sucursalDao.contarPendientes() + ordenDao.contarPendientes() +
            actividadDao.contarPendientes() + jornadaDao.contarPendientes()

    private companion object {
        const val TAG = "SyncRepository"
    }
}
