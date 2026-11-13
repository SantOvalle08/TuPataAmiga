package co.edu.uniqundio.tupataamiga.domain.model

data class Comentario(
    val id: String,
    val usuarioId: String,
    val nombreUsuario: String,
    val texto: String,
    val fecha: String
)
