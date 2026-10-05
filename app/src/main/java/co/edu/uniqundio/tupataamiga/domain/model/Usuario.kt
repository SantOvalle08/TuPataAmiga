package co.edu.uniqundio.tupataamiga.domain.model

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: Rol
)
