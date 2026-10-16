# VoluntariST

Aplicación Android para que los estudiantes de la Universidad Santo Tomás vean actividades de voluntariado, se inscriban en ellas y lleven el registro de sus horas.

Proyecto académico de la asignatura **Programación Android**: Prototipo, Primer Avance de Proyecto Personal.

## Funcionalidades

- Registro de estudiantes con nombre, carrera (selector), correo y contraseña, con validaciones.
- Inicio de sesión validado contra la base de datos local.
- Menú principal con acceso a todas las secciones.
- Listado de actividades de voluntariado (fecha, lugar y cupos) en tarjetas.
- Detalle de la actividad e inscripción con control de cupos e inscripciones duplicadas.
- Mis inscripciones: ver, cancelar o marcar una actividad como completada.
- Historial de horas de voluntariado con total acumulado.
- Perfil del usuario con datos reales, horas acumuladas y cierre de sesión.
- Mapa con la ubicación de las actividades y ubicación GPS del  (La ubicacion del usuario aun no esta implementada).
- Botón "Ver en Google Maps" desde el detalle de la actividad.
- Pantalla "Acerca de la app".
- Al agitar el teléfono en la lista de actividades, la lista se recarga.

## Tecnologías y requisitos

- Lenguaje: Java (compatibilidad Java 11)
- Interfaz: XML con ConstraintLayout y Material Components
- Min SDK 29, Target SDK 37
- Persistencia: SQLite (`SQLiteOpenHelper`)
- Mapas y ubicación: Google Maps SDK y Fused Location Provider (Google Play Services)
- Listas: RecyclerView y CardView
- Android Studio con un emulador con Google Play o un teléfono real

## Componentes técnicos

- **SQLite:** `VoluntariadoContract` (clase contrato con `BaseColumns`) y `VoluntariadoDbHelper` (`onCreate`, `onUpgrade`). Consultas con `ContentValues`, `Cursor`, `query`, `rawQuery` y argumentos parametrizados (`selectionArgs`).
- **CRUD:**
    - Crear: registro de usuarios e inscripción a actividades.
    - Consultar: login, listas, perfil e historial.
    - Actualizar: completar inscripción y ajustar cupos.
    - Eliminar: cancelar inscripción.
- **GPS y mapas:** permisos en el Manifest y solicitud en tiempo de ejecución (`ACCESS_FINE_LOCATION`), `FusedLocationProviderClient`, `SupportMapFragment` con `OnMapReadyCallback`, marcadores con `MarkerOptions` y animación de cámara con `CameraUpdateFactory`.
- **Interfaz:** ConstraintLayout, RecyclerView con Adapter y ViewHolder, CardView, Spinner y ScrollView.
- **Navegación:** Intents explícitos con `putExtra` y lectura de parámetros, Intent implícito (`geo:`) para abrir la app de mapas, y control del back stack (`finish()` y `FLAG_ACTIVITY_CLEAR_TASK` al cerrar sesión).
- **Multihilo:** consultas a la base de datos en segundo plano con `ExecutorService` y `Handler` del hilo principal.
- **Sensor:** acelerómetro (`SensorEventListener`) registrado en `onResume` y desregistrado en `onPause`, leído en un hilo secundario.

## Pantallas (Activities)

- `MainActivity`: bienvenida
- `RegistroAlumnoActivity` y `LoginAlumnoActivity`
- `MenuPrincipalActivity`
- `ListaActividadesActivity` y `DetalleActividadActivity`
- `MisInscripcionesActivity`
- `HistorialHorasActivity`
- `MapaActivity`
- `PerfilActivity`
- `AcercaActivity`

## Base de datos

Base local `voluntariado.db` con tres tablas:

- `usuarios`: nombre, correo (único), contraseña y carrera.
- `actividades`: nombre, fecha, lugar, descripción, horas, cupos, latitud y longitud. Se cargan 4 actividades de ejemplo al crear la base.
- `inscripciones`: usuario, actividad y estado (Inscrito o Completada).

## Cómo ejecutar el proyecto

1. Clonar el repositorio y abrirlo con Android Studio.
2. Esperar a que Gradle sincronice las dependencias.
3. Para ver el mapa, colocar una clave de Google Maps válida en `app/src/main/AndroidManifest.xml`, en el valor de `com.google.android.geo.API_KEY`. Sin una clave válida la app funciona, pero el mapa se muestra sin los dibujos de calles.
4. Ejecutar la aplicación en un emulador con Google Play o en un teléfono.
5. Registrar un usuario nuevo, ya que la base de datos comienza vacía.

## Flujo de uso

1. Registrarse e iniciar sesión.
2. Ver las actividades y entrar al detalle de una.
3. Inscribirse. Los cupos bajan en uno.
4. En Mis inscripciones, marcar la actividad como completada o cancelarla.
5. Revisar las horas en el historial y en el perfil.

## Limitaciones del prototipo

- Las contraseñas se guardan en texto plano en la base local.
- El estudiante marca por sí mismo las actividades como completadas, ya que aún no existe un rol de encargado que las valide.
- Las actividades son datos de ejemplo cargados al crear la base.
- Al no tener una llave de prueba el mapa del usuario se vera en blanco, pero la aplicación continuara funcionando.

## Próximos pasos

- Recordatorios antes de cada actividad.
- Edición del perfil y pantalla de configuración.
- Iconos e imágenes y ajustes visuales finales.
- Arreglo por parte del mapeado al usuario.