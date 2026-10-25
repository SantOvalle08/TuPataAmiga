package co.edu.uniqundio.tupataamiga.domain.repository

import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: StateFlow<Usuario?>

    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun register(usuario: Usuario, clave: String): Boolean
    fun setGuestSession()
    fun logout()
    suspend fun updateUserProfile(nombre: String, correo: String): Boolean
    suspend fun deleteCurrentAccount(): Boolean
}
