package co.edu.uniqundio.tupataamiga.data.repository.memory

import android.content.Context
import co.edu.uniqundio.tupataamiga.domain.model.Notificacion
import co.edu.uniqundio.tupataamiga.domain.repository.NotificacionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificacionRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : NotificacionRepository {

    private val prefs = context.getSharedPreferences("notificacion_repository_prefs", Context.MODE_PRIVATE)
    private val notificacionesFlow = MutableStateFlow<List<Notificacion>>(emptyList())

    init {
        loadNotificaciones()
    }

    private fun loadNotificaciones() {
        val savedJson = prefs.getString("notificaciones_list", null)
        if (savedJson.isNullOrEmpty()) {
            val defaultList = listOf(
                Notificacion("1", "Publicación verificada", "Tu publicación \"Rex busca hogar\" fue verificada por un moderador.", "hace 30 min", "VERIFICADA", false),
                Notificacion("2", "5 personas interesadas", "5 personas marcaron interés en adoptar a Rex.", "hace 1 h", "INTERES", false),
                Notificacion("3", "Nuevo comentario", "Ana López comentó en tu publicación \"Rex busca hogar\".", "hace 2 h", "COMENTARIO", false),
                Notificacion("4", "¡Nuevo logro!", "Obtuviste la insignia \"Primera Huella\" por tu primera publicación.", "hace 1 día", "LOGRO", true),
                Notificacion("5", "Publicación rechazada", "Motivo: las imágenes no corresponden a la descripción.", "hace 2 días", "RECHAZADA", true)
            )
            notificacionesFlow.value = defaultList
            saveNotificacionesToDisk(defaultList)
        } else {
            try {
                val list = mutableListOf<Notificacion>()
                val array = JSONArray(savedJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        Notificacion(
                            id = obj.getString("id"),
                            titulo = obj.getString("titulo"),
                            cuerpo = obj.getString("cuerpo"),
                            tiempo = obj.getString("tiempo"),
                            tipo = obj.getString("tipo"),
                            leido = obj.optBoolean("leido", false),
                            usuarioId = obj.optString("usuarioId", "")
                        )
                    )
                }
                notificacionesFlow.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveNotificacionesToDisk(list: List<Notificacion>) {
        try {
            val array = JSONArray()
            list.forEach { notif ->
                val obj = JSONObject().apply {
                    put("id", notif.id)
                    put("titulo", notif.titulo)
                    put("cuerpo", notif.cuerpo)
                    put("tiempo", notif.tiempo)
                    put("tipo", notif.tipo)
                    put("leido", notif.leido)
                    put("usuarioId", notif.usuarioId)
                }
                array.put(obj)
            }
            prefs.edit().putString("notificaciones_list", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeNotificaciones(): Flow<List<Notificacion>> = notificacionesFlow

    override suspend fun agregarNotificacion(notificacion: Notificacion) {
        notificacionesFlow.update { current ->
            val updated = listOf(notificacion) + current
            saveNotificacionesToDisk(updated)
            updated
        }
    }

    override suspend fun marcarTodasLeidas() {
        notificacionesFlow.update { current ->
            val updated = current.map { it.copy(leido = true) }
            saveNotificacionesToDisk(updated)
            updated
        }
    }
}
