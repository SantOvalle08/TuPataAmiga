package co.edu.uniqundio.tupataamiga.data.repository.memory

import co.edu.uniqundio.tupataamiga.domain.model.Rol
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {
    private val usuarios = mutableMapOf(
        "admin@uniquindio.edu.co" to Pair(Usuario("1", "Admin", "admin@uniquindio.edu.co", Rol.MODERADOR), "1234"),
        "user@uniquindio.edu.co" to Pair(Usuario("2", "Usuario", "user@uniquindio.edu.co", Rol.USUARIO), "1234")
    )

    override suspend fun login(correo: String, clave: String): Usuario? {
        val entry = usuarios[correo]
        if (entry != null && entry.second == clave) {
            return entry.first
        }
        return null
    }

    override suspend fun register(usuario: Usuario, clave: String): Boolean {
        if (usuarios.containsKey(usuario.correo)) return false
        usuarios[usuario.correo] = Pair(usuario, clave)
        return true
    }
}
