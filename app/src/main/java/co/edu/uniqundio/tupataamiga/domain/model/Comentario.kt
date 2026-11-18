package co.edu.uniqundio.tupataamiga.domain.model

import androidx.annotation.Keep

@Keep
data class Comentario(
    val id: String,
    val usuarioId: String,
    val nombreUsuario: String,
    val texto: String,
    val fecha: String
)
