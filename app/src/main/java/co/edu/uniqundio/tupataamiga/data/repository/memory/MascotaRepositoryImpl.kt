package co.edu.uniqundio.tupataamiga.data.repository.memory

import android.content.Context
import co.edu.uniqundio.tupataamiga.domain.model.Categoria
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.model.Nivel
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MascotaRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : MascotaRepository {

    private val prefs = context.getSharedPreferences("mascota_repository_prefs", Context.MODE_PRIVATE)
    private val mascotasFlow = MutableStateFlow<List<Mascota>>(emptyList())

    init {
        loadMascotas()
    }

    private fun loadMascotas() {
        val savedJson = prefs.getString("mascotas_list", null)
        if (savedJson.isNullOrEmpty()) {
            val defaultList = listOf(
                Mascota("1", "Firulais", Categoria.PERRO, Nivel.MEDIO, EstadoPublicacion.PERDIDO, "Perro amigable perdido en el parque", "", "user1"),
                Mascota("2", "Michi", Categoria.GATO, Nivel.BAJO, EstadoPublicacion.ADOPCION, "Gato tierno buscando hogar", "", "user2")
            )
            mascotasFlow.value = defaultList
            saveMascotasToDisk(defaultList)
        } else {
            try {
                val list = mutableListOf<Mascota>()
                val array = JSONArray(savedJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.getString("id")
                    val nombre = obj.getString("nombre")
                    val catStr = obj.optString("categoria", Categoria.PERRO.name)
                    val categoria = runCatching { Categoria.valueOf(catStr) }.getOrDefault(Categoria.PERRO)
                    val nivStr = obj.optString("nivel", Nivel.MEDIO.name)
                    val nivel = runCatching { Nivel.valueOf(nivStr) }.getOrDefault(Nivel.MEDIO)
                    val estStr = obj.optString("estado", EstadoPublicacion.PERDIDO.name)
                    val estado = runCatching { EstadoPublicacion.valueOf(estStr) }.getOrDefault(EstadoPublicacion.PERDIDO)
                    val descripcion = obj.getString("descripcion")
                    val imagenUrl = obj.optString("imagenUrl", "")
                    val usuarioId = obj.optString("usuarioId", "user1")

                    list.add(Mascota(id, nombre, categoria, nivel, estado, descripcion, imagenUrl, usuarioId))
                }
                mascotasFlow.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveMascotasToDisk(list: List<Mascota>) {
        try {
            val array = JSONArray()
            list.forEach { mascota ->
                val obj = JSONObject().apply {
                    put("id", mascota.id)
                    put("nombre", mascota.nombre)
                    put("categoria", mascota.categoria.name)
                    put("nivel", mascota.nivel.name)
                    put("estado", mascota.estado.name)
                    put("descripcion", mascota.descripcion)
                    put("imagenUrl", mascota.imagenUrl)
                    put("usuarioId", mascota.usuarioId)
                }
                array.put(obj)
            }
            prefs.edit().putString("mascotas_list", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMascotas(): Flow<List<Mascota>> = mascotasFlow

    override suspend fun getMascotaById(id: String): Mascota? {
        return mascotasFlow.value.find { it.id == id }
    }

    override suspend fun createMascota(mascota: Mascota) {
        mascotasFlow.update { current ->
            val updated = current + mascota
            saveMascotasToDisk(updated)
            updated
        }
    }

    override suspend fun deleteMascota(id: String) {
        mascotasFlow.update { current ->
            val updated = current.filter { it.id != id }
            saveMascotasToDisk(updated)
            updated
        }
    }
}
