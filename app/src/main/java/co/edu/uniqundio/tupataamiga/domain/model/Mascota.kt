package co.edu.uniqundio.tupataamiga.domain.model

data class Mascota(
    val id: String,
    val nombre: String,
    val categoria: Categoria,
    val nivel: Nivel,
    val estado: EstadoPublicacion,
    val descripcion: String,
    val imagenUrl: String,
    val usuarioId: String
)
