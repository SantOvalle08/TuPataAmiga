package co.edu.uniqundio.tupataamiga.domain.model

import androidx.annotation.Keep

@Keep
data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: Rol,
    val direccion: String = "",
    val fotoUrl: String = "",
)
