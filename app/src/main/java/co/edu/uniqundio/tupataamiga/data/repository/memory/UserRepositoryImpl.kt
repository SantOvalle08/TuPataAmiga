package co.edu.uniqundio.tupataamiga.data.repository.memory

import android.content.Context
import co.edu.uniqundio.tupataamiga.domain.model.Rol
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : UserRepository {

    private val prefs = context.getSharedPreferences("user_repository_prefs", Context.MODE_PRIVATE)
    private val usuarios = mutableMapOf<String, Pair<Usuario, String>>()

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    override val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    init {
        loadUsers()
        restoreSession()
    }

    private fun loadUsers() {
        val savedJson = prefs.getString("users_list", null)
        if (savedJson.isNullOrEmpty()) {
            val defaultUsers: List<Pair<Usuario, String>> = listOf(
                Pair(Usuario("1", "Admin", "admin@uniquindio.edu.co", Rol.MODERADOR), "1234"),
                Pair(Usuario("2", "Usuario", "user@uniquindio.edu.co", Rol.USUARIO), "1234")
            )
            defaultUsers.forEach { (usr, pwd) ->
                usuarios[usr.correo] = Pair(usr, pwd)
            }
            saveUsersToDisk()
        } else {
            try {
                val array = JSONArray(savedJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.getString("id")
                    val nombre = obj.getString("nombre")
                    val correo = obj.getString("correo")
                    val rolStr = obj.optString("rol", Rol.USUARIO.name)
                    val rol = runCatching { Rol.valueOf(rolStr) }.getOrDefault(Rol.USUARIO)
                    val clave = obj.getString("clave")

                    val usuario = Usuario(id, nombre, correo, rol)
                    usuarios[correo] = Pair(usuario, clave)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun restoreSession() {
        val activeEmail = prefs.getString("active_session_email", null)
        if (!activeEmail.isNullOrEmpty() && activeEmail != "GUEST") {
            val user = usuarios[activeEmail]?.first
            _currentUser.value = user
        } else {
            _currentUser.value = null
        }
    }

    private fun saveUsersToDisk() {
        try {
            val array = JSONArray()
            usuarios.values.forEach { (usuario, clave) ->
                val obj = JSONObject().apply {
                    put("id", usuario.id)
                    put("nombre", usuario.nombre)
                    put("correo", usuario.correo)
                    put("rol", usuario.rol.name)
                    put("clave", clave)
                }
                array.put(obj)
            }
            prefs.edit().putString("users_list", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun login(correo: String, clave: String): Usuario? {
        val entry = usuarios[correo]
        if (entry != null && entry.second == clave) {
            val user = entry.first
            _currentUser.value = user
            prefs.edit().putString("active_session_email", user.correo).apply()
            return user
        }
        return null
    }

    override suspend fun register(usuario: Usuario, clave: String): Boolean {
        if (usuarios.containsKey(usuario.correo)) return false
        usuarios[usuario.correo] = Pair(usuario, clave)
        saveUsersToDisk()
        _currentUser.value = usuario
        prefs.edit().putString("active_session_email", usuario.correo).apply()
        return true
    }

    override fun setGuestSession() {
        _currentUser.value = null
        prefs.edit().putString("active_session_email", "GUEST").apply()
    }

    override fun logout() {
        _currentUser.value = null
        prefs.edit().putString("active_session_email", "GUEST").apply()
    }

    override suspend fun updateUserProfile(nombre: String, correo: String): Boolean {
        val current = _currentUser.value ?: return false
        val oldEmail = current.correo
        val pwd = usuarios[oldEmail]?.second ?: "1234"

        val updatedUser = current.copy(nombre = nombre, correo = correo)
        usuarios.remove(oldEmail)
        usuarios[correo] = Pair(updatedUser, pwd)
        saveUsersToDisk()

        _currentUser.value = updatedUser
        prefs.edit().putString("active_session_email", correo).apply()
        return true
    }

    override suspend fun deleteCurrentAccount(): Boolean {
        val current = _currentUser.value ?: return false
        usuarios.remove(current.correo)
        saveUsersToDisk()
        logout()
        return true
    }
}
