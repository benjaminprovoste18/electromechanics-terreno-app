# EM Terreno: Registro de Jornada y Seguimiento de Mantenciones

Aplicación móvil nativa Android para **ELECTROMECHANICS INGENIERÍA SPA** (caso DSY1105). Centraliza el control de asistencia con GPS, la hoja de ruta de cada técnico y la bitácora de mantenciones con evidencia fotográfica. Se conecta por **Retrofit** a tres microservicios **Spring Boot** con base de datos **Oracle**.

> Experiencia 3. Todos los datos son **ficticios**, en cumplimiento de la Ley N.º 19.628 y la Ley N.º 21.719.

Repositorio del backend: `electromechanics-backend` (microservicios ms-sucursales, ms-ordenes y ms-jornadas).

## Integrantes

| Nombre | Rol en el equipo |
|---|---|
| Benjamin Provoste | Datos, sincronización con Retrofit, microservicios ms-sucursales y ms-jornadas |
| Alonso Pino | Interfaz, navegación por rol, notificaciones, microservicio ms-ordenes y base de datos Oracle |

## Cumplimiento de la Experiencia 3

| Indicación | Dónde se evidencia |
|---|---|
| 1. Dos roles con MVVM | `Rol.EMPLEADO` (Técnico) y `Rol.SUPERVISOR`. Cada rol tiene sus pantallas y ViewModels (`TecnicoHomeViewModel`, `SupervisorViewModel`). |
| 2. Autenticación con SQLite en ambos roles | `AuthRepository.login` valida RUT y hash SHA-256 contra la tabla `empleado` de SQLite. |
| 3. Microservicios Spring Boot | Repositorio backend: ms-sucursales (8081), ms-ordenes (8082), ms-jornadas (8083). |
| 4. Pruebas JUnit en el backend | Carpeta `src/test` de cada microservicio (servicios, reglas y controladores). |
| 5. Base de datos relacional | Oracle Database 23ai Free (Docker) con esquema `EMTERRENO`. Scripts en `database/` del backend. |
| 6. Recursos nativos por rol | Técnico: GPS, cámara, vibración, linterna, notificación de orden nueva. Supervisor: GPS al crear sucursal, notificación de servicio finalizado. |
| 7. Retrofit hacia los microservicios | `data/remote` (`RetrofitClient`, `SucursalApi`, `OrdenApi`, `JornadaApi`) y `data/sync/SyncRepository`. |
| 8. Iniciar y cerrar sesión | Login con bloqueo tras 5 intentos; botón de cerrar sesión en la barra de cada rol. |
| 9. Formularios validados | Login, Actividad, Orden de trabajo, Usuario y Sucursal, con ícono y mensaje por campo. Reglas en `domain/validation`. |
| 10. Navegación por rol | `Routes.homeFor(rol)` en `ui/navigation/AppNavigation.kt`. |
| Rama `documents` | En esa rama: `docs/ARQUITECTURA.md` y `docs/diagramas/` (microservicios, proyecto, modelo de datos y sincronización). |

## Funcionalidades por rol

### Técnico
- **Jornada con GPS**: marca entrada y salida con hora, coordenadas, sucursal más cercana y radio de 300 m. Detecta ubicación simulada y permite una foto de respaldo.
- **Hoja de ruta** por día con estado y prioridad. Notificación cuando le asignan una orden nueva.
- **Detalle del servicio** con flujo `Pendiente → En Proceso → Finalizado`, fotos inicial y final, y requisitos que se marcan solos.
- **Bitácora de actividades**, historial de jornada, vibración al marcar y linterna.

### Supervisor
- **Órdenes**: crea y asigna órdenes, con contadores y filtros por estado.
- **Auditoría**: horas trabajadas hoy por técnico, marcaciones fuera de radio y aprobación de marcaciones y servicios.
- **Equipo**: crea usuarios (técnico o supervisor) y activa o desactiva cuentas.
- **Sucursales**: crea sucursales con coordenadas a mano o con el GPS del teléfono.
- Notificación cuando un técnico finaliza un servicio.

## Arquitectura

Los diagramas completos están en la rama `documents` (`docs/ARQUITECTURA.md`). Resumen:

```
app/src/main/java/cl/duoc/electromechanics/
├── model/            # Enums del negocio y estado de cada pantalla
├── ui/
│   ├── screen/       # Pantallas Compose
│   ├── viewmodel/    # Un ViewModel por pantalla + AppViewModelProvider
│   ├── components/   # Componentes reutilizables, permisos, banner de sincronización
│   ├── navigation/   # Rutas por rol con transiciones animadas
│   └── theme/        # Material 3
├── data/
│   ├── local/        # SQLite: tablas, entidades, DAOs
│   ├── remote/       # Retrofit: cliente, APIs y DTOs
│   ├── sync/         # SyncRepository (sube pendientes y descarga del servidor)
│   ├── repository/   # Única puerta a los datos
│   ├── seed/         # Datos ficticios de prueba
│   └── session/      # Sesión del usuario
├── domain/           # Validaciones y reglas puras
├── device/           # GPS, cámara, notificaciones, vibración, linterna y avisos
└── di/               # AppContainer
```

## Stack

AGP 9.3.3 · Gradle 9.5.0 · Kotlin 2.2.10 · Compose BOM 2026.02.01 · Material 3 · Navigation Compose · SQLite · Coroutines/Flow · **Retrofit 2.11.0 + Gson + OkHttp logging 4.12.0** · Play Services Location · JUnit.

## Cómo ejecutar

1. Levantar el backend (ver README del repositorio `electromechanics-backend`). Comprobar en el navegador del PC que responde `http://localhost:8081/api/sucursales`.
2. Abrir este proyecto en Android Studio y esperar la sincronización de Gradle.
3. Ejecutar en el emulador. La app llega al backend del PC mediante `http://10.0.2.2` (configurado en `data/remote/ApiConfig.kt`).
4. Ingresar con un usuario de prueba:

| Perfil | RUT | Contraseña |
|---|---|---|
| Técnico | 33.333.333-3 | Demo2026 |
| Supervisor | 22.222.222-2 | Demo2026 |

El banner bajo la barra superior indica si la app está sincronizada con el servidor o si hay cambios pendientes.

### Cambiar la dirección del servidor
En `data/remote/ApiConfig.kt` cambiar `HOST`:
- Emulador con backend en el mismo PC: `http://10.0.2.2` (valor por defecto).
- Celular físico en la misma red wifi: la IP del PC, por ejemplo `http://192.168.1.50`.
- Backend en AWS (EFT): la IP elástica de la instancia EC2, por ejemplo `http://54.86.28.33`.

### Probar el GPS en el emulador
`Extended controls (⋯) > Location`, ingresar **-33.5110, -70.7580** (Cliente Demo A, Maipú) y presionar **Set location**.

### Flujo de demostración
1. Entrar como **Técnico** una vez (así la app registra sus órdenes actuales) y cerrar sesión.
2. Entrar como **Supervisor** y crear una orden para "Operario Alfa Demo". Se envía al microservicio ms-ordenes.
3. Entrar como **Técnico**: aparece la orden nueva y llega la notificación. Marcar entrada, tomar foto inicial, iniciar, registrar actividad, foto final y finalizar.
4. Entrar como **Supervisor**: llega la notificación del servicio finalizado. Aprobar en Auditoría.
5. Mostrar los datos en Oracle (SQL Developer) o en Postman.

### Pruebas unitarias
- `ValidatorsTest`: RUT, correo, contraseña, coordenadas, fechas y reglas.
- `MappersTest`: conversión entre entidades SQLite y JSON de los microservicios.

Clic derecho sobre la carpeta `test` > **Run Tests**.

### APK firmado
`Build > Generate Signed App Bundle / APK > APK`, crear un keystore nuevo (guardarlo fuera del repositorio y anotar las contraseñas) y elegir la variante **release**. El APK queda en `app/release/app-release.apk`.
