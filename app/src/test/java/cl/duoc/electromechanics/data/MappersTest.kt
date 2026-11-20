package cl.duoc.electromechanics.data

import cl.duoc.electromechanics.data.local.entity.OrdenTrabajoEntity
import cl.duoc.electromechanics.data.local.entity.RegistroJornadaEntity
import cl.duoc.electromechanics.data.local.entity.SucursalEntity
import cl.duoc.electromechanics.data.remote.dto.ActividadDto
import cl.duoc.electromechanics.data.remote.dto.OrdenDto
import cl.duoc.electromechanics.data.remote.dto.SucursalDto
import cl.duoc.electromechanics.data.remote.dto.toDto
import cl.duoc.electromechanics.data.remote.dto.toEntity
import cl.duoc.electromechanics.data.repository.EmpleadoRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MappersTest {

    @Test
    fun ordenViajaAlServidorYVuelveSinPerderDatos() {
        val orden = OrdenTrabajoEntity(
            idServicio = "OT-001",
            idEmpleadoAsignado = "EMP-333333333",
            idSucursalCliente = "SUC-01",
            fechaProgramada = 1_700_000_000_000L,
            estado = "EN_PROCESO",
            descripcionTrabajo = "Mantención preventiva de tablero eléctrico",
            actividadesRequeridas = "Reapriete de conexiones",
            prioridad = "URGENTE",
            fotoInicialPath = "/data/foto.jpg",
            horaInicio = 1_700_000_100_000L,
            creadaEn = 1_699_000_000_000L,
            sincronizado = false
        )

        val vuelta = orden.toDto().toEntity()

        assertEquals(orden.copy(sincronizado = true), vuelta)
    }

    @Test
    fun ordenSinCamposObligatoriosSeDescarta() {
        assertNull(OrdenDto(id = "OT-9", tecnicoId = null, sucursalId = "SUC-01").toEntity())
    }

    @Test
    fun sucursalDelServidorQuedaMarcadaComoSincronizada() {
        val dto = SucursalDto("SUC-07", "Cliente Demo F", "Calle 1, Cerrillos", -33.49, -70.71)

        val entidad = dto.toEntity()!!

        assertTrue(entidad.sincronizado)
        assertEquals(SucursalEntity("SUC-07", "Cliente Demo F", "Calle 1, Cerrillos", -33.49, -70.71, true), entidad)
    }

    @Test
    fun actividadSinRepuestosUsaElTextoPorDefecto() {
        val dto = ActividadDto(id = "A1", ordenId = "OT-001", detalle = "Cambio de rodamientos", horaRegistro = 10L)

        val entidad = dto.toEntity()!!

        assertEquals("Sin repuestos", entidad.repuestosUtilizados)
        assertEquals("OPERATIVO", entidad.estadoEquipo)
    }

    @Test
    fun marcacionConservaPrecisionYEstadoDeAprobacion() {
        val registro = RegistroJornadaEntity(
            idRegistro = "R1",
            idEmpleado = "EMP-333333333",
            tipoMarcacion = "ENTRADA",
            fechaHoraMarcacion = 5L,
            latitudMarcacion = -33.51,
            longitudMarcacion = -70.75,
            precisionMetros = 12.5f,
            dentroDeRadio = true,
            aprobado = true
        )

        val vuelta = registro.toDto().toEntity()!!

        assertEquals(12.5f, vuelta.precisionMetros)
        assertTrue(vuelta.aprobado)
        assertTrue(vuelta.dentroDeRadio)
    }

    @Test
    fun elIdDelEmpleadoSeArmaDesdeElRut() {
        assertEquals("EMP-333333333", EmpleadoRepository.idParaRut("33.333.333-3"))
        assertEquals("EMP-10101010K", EmpleadoRepository.idParaRut("10.101.010-k"))
    }
}
