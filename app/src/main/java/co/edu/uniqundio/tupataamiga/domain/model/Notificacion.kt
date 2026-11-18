package co.edu.uniqundio.tupataamiga.domain.model

import androidx.annotation.Keep

@Keep
data class Notificacion(
    val id: String,
    val titulo: String,
    val cuerpo: String,
    val tiempo: String,
    val tipo: String,
    val leido: Boolean = false,
    val usuarioId: String = ""
)
