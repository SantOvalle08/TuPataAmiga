package co.edu.uniqundio.tupataamiga.domain.repository

import co.edu.uniqundio.tupataamiga.domain.model.Usuario

interface UserRepository {
    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun register(usuario: Usuario, clave: String): Boolean
}
