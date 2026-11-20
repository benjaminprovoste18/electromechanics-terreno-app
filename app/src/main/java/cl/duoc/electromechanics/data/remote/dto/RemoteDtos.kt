package cl.duoc.electromechanics.data.remote.dto

data class SucursalDto(
    val id: String? = null,
    val nombreCliente: String? = null,
    val direccion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val actualizadoEn: Long? = null
)

data class OrdenDto(
    val id: String? = null,
    val tecnicoId: String? = null,
    val sucursalId: String? = null,
    val fechaProgramada: Long? = null,
    val estado: String? = null,
    val descripcion: String? = null,
    val actividadesRequeridas: String? = null,
    val prioridad: String? = null,
    val fotoInicial: String? = null,
    val fotoFinal: String? = null,
    val horaInicio: Long? = null,
    val horaTermino: Long? = null,
    val aprobada: Boolean? = null,
    val creadaEn: Long? = null,
    val actualizadoEn: Long? = null
)

data class ActividadDto(
    val id: String? = null,
    val ordenId: String? = null,
    val detalle: String? = null,
    val repuestos: String? = null,
    val estadoEquipo: String? = null,
    val horaRegistro: Long? = null,
    val comentarios: String? = null,
    val actualizadoEn: Long? = null
)

data class JornadaDto(
    val id: String? = null,
    val empleadoId: String? = null,
    val tipo: String? = null,
    val fechaHora: Long? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val precisionMetros: Double? = null,
    val fotoRespaldo: String? = null,
    val sucursalCercanaId: String? = null,
    val distanciaMetros: Double? = null,
    val dentroDeRadio: Boolean? = null,
    val ubicacionSimulada: Boolean? = null,
    val aprobado: Boolean? = null,
    val actualizadoEn: Long? = null
)
