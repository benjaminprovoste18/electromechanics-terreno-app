package cl.duoc.electromechanics.data.remote.dto

import cl.duoc.electromechanics.data.local.entity.ActividadEntity
import cl.duoc.electromechanics.data.local.entity.OrdenTrabajoEntity
import cl.duoc.electromechanics.data.local.entity.RegistroJornadaEntity
import cl.duoc.electromechanics.data.local.entity.SucursalEntity

fun SucursalEntity.toDto() = SucursalDto(
    id = idSucursal,
    nombreCliente = nombreCliente,
    direccion = direccionSucursal,
    latitud = latitudRef,
    longitud = longitudRef
)

fun SucursalDto.toEntity(): SucursalEntity? {
    if (id == null || nombreCliente == null || direccion == null || latitud == null || longitud == null) return null
    return SucursalEntity(
        idSucursal = id,
        nombreCliente = nombreCliente,
        direccionSucursal = direccion,
        latitudRef = latitud,
        longitudRef = longitud,
        sincronizado = true
    )
}

fun OrdenTrabajoEntity.toDto() = OrdenDto(
    id = idServicio,
    tecnicoId = idEmpleadoAsignado,
    sucursalId = idSucursalCliente,
    fechaProgramada = fechaProgramada,
    estado = estado,
    descripcion = descripcionTrabajo,
    actividadesRequeridas = actividadesRequeridas,
    prioridad = prioridad,
    fotoInicial = fotoInicialPath,
    fotoFinal = fotoFinalPath,
    horaInicio = horaInicio,
    horaTermino = horaTermino,
    aprobada = aprobada,
    creadaEn = creadaEn
)

fun OrdenDto.toEntity(): OrdenTrabajoEntity? {
    if (id == null || tecnicoId == null || sucursalId == null || fechaProgramada == null) return null
    if (descripcion == null || actividadesRequeridas == null) return null
    return OrdenTrabajoEntity(
        idServicio = id,
        idEmpleadoAsignado = tecnicoId,
        idSucursalCliente = sucursalId,
        fechaProgramada = fechaProgramada,
        estado = estado ?: "PENDIENTE",
        descripcionTrabajo = descripcion,
        actividadesRequeridas = actividadesRequeridas,
        prioridad = prioridad ?: "NORMAL",
        fotoInicialPath = fotoInicial,
        fotoFinalPath = fotoFinal,
        horaInicio = horaInicio,
        horaTermino = horaTermino,
        aprobada = aprobada ?: false,
        creadaEn = creadaEn ?: fechaProgramada,
        sincronizado = true
    )
}

fun ActividadEntity.toDto() = ActividadDto(
    id = idActividad,
    ordenId = idServicio,
    detalle = detalleTareaEjecutada,
    repuestos = repuestosUtilizados,
    estadoEquipo = estadoEquipo,
    horaRegistro = horaRegistro,
    comentarios = comentariosTecnicos
)

fun ActividadDto.toEntity(): ActividadEntity? {
    if (id == null || ordenId == null || detalle == null || horaRegistro == null) return null
    return ActividadEntity(
        idActividad = id,
        idServicio = ordenId,
        detalleTareaEjecutada = detalle,
        repuestosUtilizados = repuestos ?: "Sin repuestos",
        estadoEquipo = estadoEquipo ?: "OPERATIVO",
        horaRegistro = horaRegistro,
        comentariosTecnicos = comentarios.orEmpty(),
        sincronizado = true
    )
}

fun RegistroJornadaEntity.toDto() = JornadaDto(
    id = idRegistro,
    empleadoId = idEmpleado,
    tipo = tipoMarcacion,
    fechaHora = fechaHoraMarcacion,
    latitud = latitudMarcacion,
    longitud = longitudMarcacion,
    precisionMetros = precisionMetros.toDouble(),
    fotoRespaldo = fotoRespaldoPath,
    sucursalCercanaId = idSucursalCercana,
    distanciaMetros = distanciaMetros,
    dentroDeRadio = dentroDeRadio,
    ubicacionSimulada = ubicacionSimulada,
    aprobado = aprobado
)

fun JornadaDto.toEntity(): RegistroJornadaEntity? {
    if (id == null || empleadoId == null || tipo == null || fechaHora == null) return null
    if (latitud == null || longitud == null) return null
    return RegistroJornadaEntity(
        idRegistro = id,
        idEmpleado = empleadoId,
        tipoMarcacion = tipo,
        fechaHoraMarcacion = fechaHora,
        latitudMarcacion = latitud,
        longitudMarcacion = longitud,
        precisionMetros = (precisionMetros ?: 0.0).toFloat(),
        fotoRespaldoPath = fotoRespaldo,
        idSucursalCercana = sucursalCercanaId,
        distanciaMetros = distanciaMetros,
        dentroDeRadio = dentroDeRadio ?: false,
        ubicacionSimulada = ubicacionSimulada ?: false,
        aprobado = aprobado ?: false,
        sincronizado = true
    )
}
