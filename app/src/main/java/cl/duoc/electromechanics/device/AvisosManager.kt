package cl.duoc.electromechanics.device

import android.content.Context
import cl.duoc.electromechanics.data.local.relation.OrdenConDetalle
import cl.duoc.electromechanics.model.EstadoServicio
import cl.duoc.electromechanics.model.Prioridad

class AvisosManager(context: Context, private val notificationHelper: NotificationHelper) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun nuevosDesdeUltimaVez(clave: String, actuales: Set<String>): Set<String> {
        val key = "vistos_$clave"
        if (!prefs.contains(key)) {
            prefs.edit().putStringSet(key, actuales).apply()
            return emptySet()
        }
        val vistos = prefs.getStringSet(key, emptySet()).orEmpty().toSet()
        val nuevos = actuales - vistos
        if (nuevos.isNotEmpty()) prefs.edit().putStringSet(key, vistos + actuales).apply()
        return nuevos
    }

    fun avisarOrdenesNuevas(idTecnico: String, hojaDeRuta: List<OrdenConDetalle>) {
        val nuevas = nuevosDesdeUltimaVez("tecnico_$idTecnico", hojaDeRuta.map { it.orden.idServicio }.toSet())
        if (nuevas.isEmpty()) return
        val ordenes = hojaDeRuta.filter { it.orden.idServicio in nuevas }
        val urgente = ordenes.any { Prioridad.from(it.orden.prioridad) == Prioridad.URGENTE }
        val primera = ordenes.first()
        val titulo = when {
            ordenes.size > 1 -> "Tienes ${ordenes.size} órdenes nuevas"
            urgente -> "Orden URGENTE asignada"
            else -> "Nueva orden de trabajo"
        }
        val mensaje = "${primera.orden.descripcionTrabajo} en ${primera.sucursal?.nombreCliente ?: "sucursal del cliente"}."
        notificationHelper.notificarOrden(titulo, mensaje)
    }

    fun avisarServiciosFinalizados(idSupervisor: String, ordenes: List<OrdenConDetalle>) {
        val finalizadas = ordenes.filter { EstadoServicio.from(it.orden.estado) == EstadoServicio.FINALIZADO }
        val nuevas = nuevosDesdeUltimaVez("supervisor_$idSupervisor", finalizadas.map { it.orden.idServicio }.toSet())
        if (nuevas.isEmpty()) return
        val lista = finalizadas.filter { it.orden.idServicio in nuevas && !it.orden.aprobada }
        if (lista.isEmpty()) return
        val primera = lista.first()
        val titulo = if (lista.size > 1) "${lista.size} servicios por aprobar" else "Servicio finalizado"
        val mensaje = "${primera.tecnico?.nombreCompleto ?: "Un técnico"} terminó: ${primera.orden.descripcionTrabajo}. " +
            "Revísalo y apruébalo."
        notificationHelper.notificarOrden(titulo, mensaje)
    }

    private companion object {
        const val PREFS = "em_avisos"
    }
}
