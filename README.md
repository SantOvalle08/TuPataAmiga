# 🐾 TuPataAmiga - Plataforma de Adopción, Reporte y Protección Animal

**TuPataAmiga** es una aplicación móvil nativa para Android desarrollada en **Kotlin** con **Jetpack Compose**, orientada a facilitar el reporte, búsqueda, geolocalización, adopción y cuidado comunitario de mascotas. El proyecto implementa principios de **Clean Architecture**, **MVVM**, e integra tecnologías modernas como **Firebase**, **OSMDroid (OpenStreetMap)**, **Cloudinary** y **Asistencia con IA (OpenRouter API)**.

---

## 🚀 Características Principales

- 🐾 **Publicación y Adopción de Mascotas:** 
  - Creación de reportes de mascotas en adopción, perdidas o encontradas con fotografías, detalles de salud/vacunas y nivel de urgencia.
  - Asistencia por Inteligencia Artificial para la redacción automática de descripciones atractivas y amigables.
- 📍 **Geolocalización en Tiempo Real (Mapa Interactivo):**
  - Mapa interactivo alimentado por **OSMDroid** para ubicar reportes de mascotas perdidas o en adopción con marcadores interactivos.
- 🚨 **Módulo de Alertas y Notificaciones:**
  - Sistema de alertas comunitarias con comentarios en tiempo real y notificaciones de casos urgentes.
- 🤖 **Panel de Moderación Intuitivo asistido por IA:**
  - Control administrativo de publicaciones para aprobar o rechazar reportes, incorporando un resumen inteligente generado por IA que evalúa la legitimidad y coherencia del contenido.
- 🏆 **Perfil de Usuario, Estadísticas y Logros:**
  - Seguimiento del impacto del usuario (adopciones realizadas, reportes activos, publicaciones guardadas y logros desbloqueados).
- 🌐 **Soporte Multilingüe y Personalización de Tema:**
  - Alternancia dinámica de idioma (**Español** e **Inglés**).
  - Selector de tema (**Claro / Oscuro**).

---

## 🛠️ Tecnologías y Librerías

- **Lenguaje:** Kotlin 2.x
- **UI & Diseño:** Jetpack Compose, Material Design 3, Compose Navigation
- **Arquitectura:** Clean Architecture + MVVM (Model-View-ViewModel) + StateFlow & Coroutines
- **Inyección de Dependencias:** Hilt (Dagger Hilt) con KSP
- **Backend & Almacenamiento:**
  - **Firebase Auth:** Autenticación y gestión de sesiones de usuario.
  - **Firebase Firestore:** Base de datos en tiempo real para publicaciones, comentarios y notificaciones.
- **Gestión de Imágenes:** Cloudinary API + Coil para carga asíncrona y almacenamiento en la nube.
- **Mapas & Ubicación:** OSMDroid (OpenStreetMap)
- **Inteligencia Artificial:** OpenRouter API (Meta LLaMA 3.2 3B Instruct) para asistencia en redacción de publicaciones y moderación inteligente.

---

## 📁 Estructura del Proyecto

El proyecto está organizado siguiendo los principios de **Clean Architecture**:

```text
co.edu.uniqundio.tupataamiga/
├── core/                        # Utilidades y wrappers globales (RequestResult, etc.)
├── data/                        # Capa de datos (Repositorios, Firebase, Cloudinary, IA, DI)
│   ├── di/                      # Módulos de inyección de dependencias (Hilt)
│   ├── remote/                  # Servicios remotos (CloudinaryService, AIService)
│   └── repository/              # Implementaciones de repositorios (Firebase / Memory Fallback)
├── domain/                      # Capa de dominio (Modelos de negocio, Interfaces, Casos de uso)
│   ├── model/                   # Data classes (Mascota, Usuario, Notificacion, Comentario, etc.)
│   ├── repository/              # Contratos de repositorios
│   └── usecase/                 # Casos de uso de negocio (Mascotas, Usuarios)
├── features/                    # Capa de presentación / pantallas por característica
│   ├── create/                  # Creación de reportes de mascotas
│   ├── dashboard/               # Main, Feed, Mapa, Alertas, Perfil, Moderación, Stats
│   ├── detail/                  # Vista detallada de mascota y comentarios
│   ├── feed/                    # Muro principal de mascotas
│   ├── login/                   # Inicio de sesión
│   ├── recovery/                # Recuperación de contraseña
│   └── register/                # Registro de nuevos usuarios
├── navigation/                  # Grafos y rutas de navegación (AppNavigation, MainRoutes)
└── ui/theme/                    # Configuración de Material Theme, Colores, Tipografías y Locales
```

---

## 📋 Requisitos Previos

- **Android Studio:** Ladybug (2024.2.1) o superior.
- **JDK:** Java 11 o Java 17.
- **Android SDK:** `minSdk` 28 (Android 9.0) | `targetSdk` 37.

---

## 🔧 Configuración del Entorno

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/TuPataAmiga.git
   cd TuPataAmiga
   ```

2. **Configuración de variables de entorno y API Keys:**
   En el archivo `gradle.properties` o `local.properties`, agrega tu clave de API para OpenRouter (Servicio de IA):
   ```properties
   OPENROUTER_API_KEY=tu_api_key_de_openrouter
   ```

3. **Configuración de Firebase:**
   Asegúrate de incluir el archivo `google-services.json` en la carpeta `app/` para que la autenticación y Firestore funcionen correctamente.

---

## 📲 Compilación y Ejecución

1. Abre el proyecto en **Android Studio**.
2. Sincroniza el proyecto con Gradle (**Sync Project with Gradle Files**).
3. Selecciona un emulador Android (API 28+) o conecta un dispositivo físico mediante depuración USB.
4. Presiona **Run (Shift + F10)**.

---

## 🎓 Créditos e Institución

Proyecto desarrollado en el marco de formación académica en la **Universidad del Quindío** (`co.edu.uniqundio.tupataamiga`).

- **Desarrollador:** Santiago & Equipo TuPataAmiga
- **Ubicación:** Armenia, Quindío, Colombia

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT**. Consulta el archivo `LICENSE` para más información.
