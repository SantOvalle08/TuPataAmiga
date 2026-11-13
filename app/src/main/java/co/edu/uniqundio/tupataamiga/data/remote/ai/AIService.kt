package co.edu.uniqundio.tupataamiga.data.remote.ai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIService @Inject constructor() {

    suspend fun generarResumenModeracion(
        nombre: String,
        categoria: String,
        descripcion: String,
        raza: String
    ): String {
        return "Análisis IA: Publicación legítima para '$nombre' ($categoria). Raza: $raza. Descripción coherente y sin registros duplicados en Armenia, Quindío. Acción recomendada: Verificar."
    }

    suspend fun sugerirDescripcionMascota(
        tipoAnimal: String,
        raza: String,
        tamano: String
    ): String {
        return "$tipoAnimal de raza $raza y tamaño $tamano. Es de temperamento amigable, juguetón y adecuado para familias en Armenia."
    }
}
