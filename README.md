# VoluntariST

Aplicación Android para que los estudiantes de la Universidad Santo Tomás vean actividades de voluntariado, se inscriban en ellas, lleven el registro de sus horas y reciban recordatorios.

Proyecto académico de la asignatura **Programación Android**.

**Autor:** Matias Andres Moreno Caamaño

## Funcionalidades

- Registro de estudiantes con nombre, carrera (selector), correo y contraseña, con validaciones.
- Inicio de sesión validado contra la base de datos local.
- Menú principal con acceso a todas las secciones.
- Lista de actividades en tarjetas (RecyclerView y CardView) con fecha, lugar y cupos.
- Detalle de la actividad, botón "Ver en Google Maps" e inscripción con control de cupos e inscripciones duplicadas.
- Mis inscripciones: ver, cancelar o marcar una actividad como completada.
- Historial de horas de voluntariado con el total acumulado.
- Perfil con datos reales, horas acumuladas, foto de perfil tomada con la cámara y cierre de sesión.
- Mapa con la ubicación de las actividades (Pendiente) y la posición del usuario (GPS).
- Recordatorios: notificación programada al inscribirse en una actividad.
- Acelerómetro: al agitar el teléfono en la lista de actividades, la lista se recarga.
- Pantalla "Acerca de la app".

## Tecnologías y requisitos

- Java 11, interfaz en XML con ConstraintLayout y Material Components.
- Min SDK 29 y Target SDK 37.
- SQLite con clase contrato y `SQLiteOpenHelper`.
- Google Maps SDK y Fused Location Provider (Google Play Services).
- RecyclerView, CardView, ExecutorService, AlarmManager y FileProvider.
- Android Studio con un emulador con Google Play o un teléfono real.

## Estructura del proyecto

```
com.example.proyecto
├─ adapter      ActividadAdapter, InscripcionAdapter
├─ data         VoluntariadoContract, VoluntariadoDbHelper
├─ model        Actividad, Inscripcion
├─ util         EjecutorBd, SeguridadUtil, RecordatorioHelper, RecordatorioReceiver
└─ ui
   ├─ auth         MainActivity, LoginAlumnoActivity, RegistroAlumnoActivity
   ├─ menu         MenuPrincipalActivity, PerfilActivity, AcercaActivity
   └─ actividades  ListaActividadesActivity, DetalleActividadActivity,
                   MisInscripcionesActivity, HistorialHorasActivity, MapaActivity
```

## Base de datos

Base local `voluntariado.db` con tres tablas (ver `docs/Diagrama_ERD_VoluntariST.png`):

- `usuarios`: nombre, correo (único), contraseña protegida con hash y carrera.
- `actividades`: nombre, fecha, lugar, descripción, horas, cupos, latitud y longitud. Se cargan 4 actividades de ejemplo con fechas relativas al día en que se crea la base.
- `inscripciones`: usuario, actividad y estado (Inscrito o Completada), con clave única por usuario y actividad.

Las operaciones de guardar, consultar, actualizar y eliminar se concentran en `VoluntariadoDbHelper`.

## Seguridad

- Consultas parametrizadas (`selectionArgs` y `ContentValues`).
- Contraseñas con hash PBKDF2WithHmacSHA256 y sal aleatoria.
- `FLAG_SECURE` en login, registro y perfil.
- Componentes privados (`android:exported="false"`) y permisos mínimos.
- FileProvider para la foto de perfil, sin permiso de cámara.
- Clave de Google Maps fuera del código (`local.properties`) y respaldo automático desactivado.
- Consultas a la base de datos en segundo plano.

## Documentación

Carpeta `docs`:

- `Informe_Pruebas_y_Deteccion_VoluntariST.docx`: informe de pruebas y detección de vulnerabilidades.
- `Diagrama_ERD_VoluntariST.png`: modelo entidad-relación.
- `Diagrama_Flujo_VoluntariST.png`: flujo de pantallas e Intents.
- `Diagrama_Ciclo_Vida_VoluntariST.png`: ciclo de vida de las activities.

## Cómo ejecutar el proyecto

1. Clonar el repositorio y abrirlo con Android Studio.
2. Esperar a que Gradle sincronice las dependencias.
3. Para ver el mapa, agregar en `local.properties` (archivo local que no se sube a Git) la línea `MAPS_API_KEY=tu_clave`. Sin una clave válida la app funciona, pero el mapa se muestra sin los dibujos de calles.
4. Ejecutar la aplicación en un emulador con Google Play o en un teléfono.
5. Registrar un usuario nuevo, ya que la base de datos comienza vacía.

## Flujo de uso

1. Registrarse e iniciar sesión.
2. Ver las actividades y entrar al detalle de una.
3. Inscribirse. Los cupos bajan en uno y se programa el recordatorio.
4. En Mis inscripciones, marcar la actividad como completada o cancelarla.
5. Revisar las horas en el historial y en el perfil.

## Limitaciones y próximos pasos

- El estudiante marca por sí mismo las actividades como completadas, ya que aún no existe un rol de encargado que las valide.
- Las actividades son datos de ejemplo cargados al crear la base.
- Próximo avance: integrar Firebase trabajando junto a SQLite.
- Implementación de Google Maps (KEY).
- Mejorar el diseño visual de la aplicación.