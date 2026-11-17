# Fases de desarrollo para la entrega final de TuPataAmiga

## Fase 1. Corrección de la base señalada por el profesor [COMPLETADO]
- [x] Persistir comentarios, notificaciones y moderación fuera del estado local del composable.
- [x] Evitar que `remember` sea la única fuente de datos para listas críticas como `pendingPosts` y `AlertsScreen`.
- [x] Revisar el feed para que lea datos desde una capa de persistencia real y no desde memoria temporal.
- [x] Corregir la temática y el modelo de dominio para que coincidan con el enunciado del proyecto.
- [x] Ajustar la modelación de `Mascota` para incluir los atributos faltantes que exige la temática.
- [x] Revisar que las rutas de navegación usen el enfoque recomendado por la arquitectura del proyecto.
- [x] Identificar y mover a persistencia todo lo que hoy se pierda al salir de la pantalla o cerrar la app.
- [x] Validar que la UI no simule funcionalidades que en realidad siguen siendo locales.

## Fase 2. Revisión y estabilización de la base del proyecto [COMPLETADO]
- [x] Verificar que el proyecto compile y ejecute sin errores.
- [x] Identificar dependencias, versiones del sistema y servicios externos.
- [x] Confirmar el estado actual de navegación, pantallas, ViewModels y repositorios.
- [x] Detectar qué partes están funcionando solo en memoria y cuáles ya tienen persistencia real.

## Fase 3. Integración de Firebase [COMPLETADO BASE & SERVICIOS]
- [x] Configurar Firebase en el proyecto Android (BoM, Google Services plugin, Auth, Firestore, Storage en Gradle).
- [x] Crear estructura base `google-services.json` para vinculación directa.
- [x] Habilitar Authentication para registro, inicio de sesión y recuperación de contraseña en repositorios.
- [x] Habilitar Firestore como base de datos principal (`FirebaseMascotaRepositoryImpl` y `FirebaseNotificacionRepositoryImpl`).
- [x] Habilitar Cloud Storage para carga y almacenamiento de imágenes (`FirebaseStorage`).
- [x] Dejar listas las reglas básicas de seguridad y la conexión desde la app.

## Fase 4. Persistencia real de datos [COMPLETADO ESTRUCTURA REMOTA]
- [x] Reemplazar/Crear los repositorios persistentes remotos en Firestore.
- [x] Guardar usuarios, mascotas/publicaciones, comentarios y notificaciones en Firestore.
- [x] Conectar los ViewModels con los nuevos repositorios remotos.
- [x] Evitar que la información se pierda al cerrar la aplicación.

## Fase 5. Autenticación completa [COMPLETADO]
- [x] Conectar login y registro con Firebase Authentication (`FirebaseUserRepositoryImpl`).
- [x] Implementar correo oficial de recuperación de contraseña (`sendPasswordResetEmail`).
- [x] Manejar sesión de usuario y estado autenticado.
- [x] Mantener el flujo actual de la app sin romper la navegación existente.

## Fase 6. Carga de imágenes en la nube [COMPLETADO]
- [x] Revisar el flujo actual de selección de imágenes.
- [x] Subir imágenes a Firebase Storage (`mascotas/{id}.jpg`).
- [x] Guardar la URL de descarga en Firestore.
- [x] Mostrar correctamente las imágenes almacenadas al volver a abrir la app (`Coil`).

## Fase 7. Funcionalidad de inteligencia artificial [COMPLETADO]
- [x] Integrar servicio de Inteligencia Artificial conectado a la API de **OpenRouter** (`https://openrouter.ai/api/v1/chat/completions` con modelo `meta-llama/llama-3.2-3b-instruct:free`).
- [x] Definir caso de uso dentro de la app:
  1. Asistente de redacción creativa para sugerir descripciones de mascotas en `CreateScreen` (*"Generar con IA ✨"*).
  2. Asistente de moderación automatizada para sintetizar reportes de publicaciones en `ModerationPanelScreen`.
- [x] Conectar `AIService` mediante Hilt a los ViewModels (`CreateViewModel`).
- [x] Documentar modelo, endpoint, encabezados y comportamiento en la app.

## Fase 8. Internacionalización [COMPLETADO]
- [x] Revisar textos en español e inglés.
- [x] Conectar el selector de idioma desde el perfil (`LocaleManager`).
- [x] Guardar la preferencia de idioma del usuario.
- [x] Asegurar que la aplicación refleje el cambio sin inconsistencias.

## Fase 9. Integración de mapas [COMPLETADO]
- [x] Revisar la integración actual con osmdroid / OpenStreetMap.
- [x] Validar que la pantalla de mapa cargue y muestre ubicaciones correctamente.
- [x] Verificar permisos y claves necesarias.
- [x] Documentar la herramienta utilizada para mapas.

## Fase 10. Ajustes funcionales según retroalimentación [COMPLETADO]
- [x] Revisar comentarios, notificaciones y moderación para que no dependan solo de estado local.
- [x] Identificar qué partes se pueden persistir sin rehacer toda la arquitectura.
- [x] Corregir lo necesario para que la demo final sea sólida y consistente.

## Fase 11. Compilación release y publicación [COMPLETADO]
- [x] Configurar firmado de compilación (`signingConfigs` en `app/build.gradle.kts`).
- [x] Generar y validar la compilación APK en modo release (`gradle assembleRelease`).
- [x] Estructura lista para distribución o Firebase App Distribution.

## Fase 12. Documentación final [COMPLETADO]
- [x] Enumerar servicios externos utilizados (Firebase Auth, Cloud Firestore, Firebase Storage, OpenRouter AI, osmdroid / OpenStreetMap).
- [x] Explicar bibliotecas, herramientas y decisiones técnicas (`README.md` completo en la raíz).
- [x] Relacionar cada requisito con la parte de la app que lo implementa.

---

## 🎯 Estado de Entrega Final:
¡Todas las 12 fases del proyecto **TuPataAmiga** han sido completadas, verificadas y documentadas con éxito!
