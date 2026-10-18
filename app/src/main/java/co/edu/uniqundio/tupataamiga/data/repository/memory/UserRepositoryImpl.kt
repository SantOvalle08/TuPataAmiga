package co.edu.uniqundio.tupataamiga.data.repository.memory

import android.content.Context
import co.edu.uniqundio.tupataamiga.domain.model.Rol
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
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

    init {
        loadUsers()
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
            return entry.first
        }
        return null
    }

    override suspend fun register(usuario: Usuario, clave: String): Boolean {
        if (usuarios.containsKey(usuario.correo)) return false
        usuarios[usuario.correo] = Pair(usuario, clave)
        saveUsersToDisk()
        return true
    }
}
