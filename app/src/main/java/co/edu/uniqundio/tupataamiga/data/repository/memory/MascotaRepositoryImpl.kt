package co.edu.uniqundio.tupataamiga.data.repository.memory

import android.content.Context
import co.edu.uniqundio.tupataamiga.domain.model.*
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
                Mascota(
                    id = "1",
                    nombre = "Rex busca un hogar amoroso",
                    categoria = Categoria.ADOPCION,
                    tipoAnimal = TipoAnimal.PERRO,
                    raza = "Labrador",
                    tamano = TamanoMascota.GRANDE,
                    vacunasAlDia = true,
                    vacunasDetalle = "Carnet de vacunas completo al día",
                    nivelUrgencia = NivelUrgencia.MEDIO,
                    necesidadAdicional = "Requiere espacio amplio / Patio",
                    estado = EstadoPublicacion.APROBADO,
                    descripcion = "Rex es un labrador de 2 años muy amigable, juguetón y educado. Busca una familia amorosa.",
                    imagenUrl = "",
                    ubicacion = "El Bosque, Armenia",
                    usuarioId = "user1",
                    nombrePublicante = "Carlos Herrera",
                    fechaCreacion = "Hoy",
                    comentarios = listOf(
                        Comentario("c1", "user2", "Ana López", "¡Hola! Quisiera saber si Rex aún está disponible para adopción.", "hace 2 h")
                    ),
                    interesAdopcionCount = 5
                ),
                Mascota(
                    id = "2",
                    nombre = "Michi extraviado en el centro",
                    categoria = Categoria.PERDIDO,
                    tipoAnimal = TipoAnimal.GATO,
                    raza = "Siamés",
                    tamano = TamanoMascota.PEQUENO,
                    vacunasAlDia = true,
                    vacunasDetalle = "Esterilizado y vacunado",
                    nivelUrgencia = NivelUrgencia.ALTO,
                    necesidadAdicional = "Lleva collar azul con placa",
                    estado = EstadoPublicacion.APROBADO,
                    descripcion = "Gato siamés de ojos azules extraviado en la carrera 14 cerca al centro comercial. Responde a Michi.",
                    imagenUrl = "",
                    ubicacion = "Centro, Armenia",
                    usuarioId = "user2",
                    nombrePublicante = "Ana López",
                    fechaCreacion = "Ayer",
                    comentarios = emptyList(),
                    interesAdopcionCount = 2
                ),
                Mascota(
                    id = "3",
                    nombre = "Perrito mestizo encontrado",
                    categoria = Categoria.ENCONTRADO,
                    tipoAnimal = TipoAnimal.PERRO,
                    raza = "Mestizo",
                    tamano = TamanoMascota.MEDIANO,
                    vacunasAlDia = false,
                    vacunasDetalle = "Pendiente revisión veterinaria",
                    nivelUrgencia = NivelUrgencia.ALTO,
                    necesidadAdicional = "Búsqueda urgente de sus dueños originales",
                    estado = EstadoPublicacion.PENDIENTE,
                    descripcion = "Se encontró perrito mestizo color café en la Plaza de Bolívar. Muy dócil.",
                    imagenUrl = "",
                    ubicacion = "Plaza de Bolívar, Armenia",
                    usuarioId = "user3",
                    nombrePublicante = "Pedro Morales",
                    fechaCreacion = "Hace 1 día",
                    comentarios = emptyList(),
                    interesAdopcionCount = 0
                ),
                Mascota(
                    id = "4",
                    nombre = "Solicitud de Hogar Temporal para Michi",
                    categoria = Categoria.HOGAR_TEMPORAL,
                    tipoAnimal = TipoAnimal.GATO,
                    raza = "Persa",
                    tamano = TamanoMascota.PEQUENO,
                    vacunasAlDia = true,
                    vacunasDetalle = "Desparasitado y vacunado",
                    nivelUrgencia = NivelUrgencia.ALTO,
                    necesidadAdicional = "Se requiere hogar temporal por 15 días mientras viaja su dueño",
                    estado = EstadoPublicacion.PENDIENTE,
                    descripcion = "Se busca hogar temporal para gata persa tranquila por dos semanas. Se entrega alimento y arena.",
                    imagenUrl = "",
                    ubicacion = "La Castellana, Armenia",
                    usuarioId = "user1",
                    nombrePublicante = "Carlos Herrera",
                    fechaCreacion = "Hace 2 horas",
                    comentarios = emptyList(),
                    interesAdopcionCount = 1
                ),
                Mascota(
                    id = "5",
                    nombre = "Jornada de Desparasitación y Vacunación",
                    categoria = Categoria.JORNADA_VETERINARIA,
                    tipoAnimal = TipoAnimal.OTRO,
                    raza = "Comunitario",
                    tamano = TamanoMascota.MEDIANO,
                    vacunasAlDia = true,
                    vacunasDetalle = "Jornada gratuita de salud animal",
                    nivelUrgencia = NivelUrgencia.MEDIO,
                    necesidadAdicional = "Inscripción previa requerida",
                    estado = EstadoPublicacion.APROBADO,
                    descripcion = "Gran jornada veterinaria gratuita organizada por la comunidad universitaria este sábado.",
                    imagenUrl = "",
                    ubicacion = "Universidad del Quindío, Armenia",
                    usuarioId = "admin1",
                    nombrePublicante = "Moderador PataAmiga",
                    fechaCreacion = "Hace 3 días",
                    comentarios = emptyList(),
                    interesAdopcionCount = 8
                )
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
                    val catStr = obj.optString("categoria", Categoria.ADOPCION.name)
                    val categoria = runCatching { Categoria.valueOf(catStr) }.getOrDefault(Categoria.ADOPCION)
                    val tipoStr = obj.optString("tipoAnimal", TipoAnimal.PERRO.name)
                    val tipoAnimal = runCatching { TipoAnimal.valueOf(tipoStr) }.getOrDefault(TipoAnimal.PERRO)
                    val raza = obj.optString("raza", "Mestizo")
                    val tamStr = obj.optString("tamano", TamanoMascota.MEDIANO.name)
                    val tamano = runCatching { TamanoMascota.valueOf(tamStr) }.getOrDefault(TamanoMascota.MEDIANO)
                    val vacunasAlDia = obj.optBoolean("vacunasAlDia", true)
                    val vacunasDetalle = obj.optString("vacunasDetalle", "Vacunas al día")
                    val nivStr = obj.optString("nivelUrgencia", NivelUrgencia.MEDIO.name)
                    val nivelUrgencia = runCatching { NivelUrgencia.valueOf(nivStr) }.getOrDefault(NivelUrgencia.MEDIO)
                    val necesidadAdicional = obj.optString("necesidadAdicional", "")
                    val estStr = obj.optString("estado", EstadoPublicacion.APROBADO.name)
                    val estado = runCatching { EstadoPublicacion.valueOf(estStr) }.getOrDefault(EstadoPublicacion.APROBADO)
                    val descripcion = obj.getString("descripcion")
                    val imagenUrl = obj.optString("imagenUrl", "")
                    val ubicacion = obj.optString("ubicacion", "Armenia, Quindío")
                    val usuarioId = obj.optString("usuarioId", "user1")
                    val nombrePublicante = obj.optString("nombrePublicante", "Usuario PataAmiga")
                    val fechaCreacion = obj.optString("fechaCreacion", "Hoy")
                    val interesAdopcionCount = obj.optInt("interesAdopcionCount", 0)
                    val motivoRechazo = obj.optString("motivoRechazo", "")

                    // Load Comentarios
                    val commList = mutableListOf<Comentario>()
                    val commArray = obj.optJSONArray("comentarios")
                    if (commArray != null) {
                        for (j in 0 until commArray.length()) {
                            val cObj = commArray.getJSONObject(j)
                            commList.add(
                                Comentario(
                                    id = cObj.getString("id"),
                                    usuarioId = cObj.getString("usuarioId"),
                                    nombreUsuario = cObj.getString("nombreUsuario"),
                                    texto = cObj.getString("texto"),
                                    fecha = cObj.getString("fecha")
                                )
                            )
                        }
                    }

                    list.add(
                        Mascota(
                            id = id,
                            nombre = nombre,
                            categoria = categoria,
                            tipoAnimal = tipoAnimal,
                            raza = raza,
                            tamano = tamano,
                            vacunasAlDia = vacunasAlDia,
                            vacunasDetalle = vacunasDetalle,
                            nivelUrgencia = nivelUrgencia,
                            necesidadAdicional = necesidadAdicional,
                            estado = estado,
                            descripcion = descripcion,
                            imagenUrl = imagenUrl,
                            ubicacion = ubicacion,
                            usuarioId = usuarioId,
                            nombrePublicante = nombrePublicante,
                            fechaCreacion = fechaCreacion,
                            comentarios = commList,
                            interesAdopcionCount = interesAdopcionCount,
                            motivoRechazo = motivoRechazo
                        )
                    )
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
                    put("tipoAnimal", mascota.tipoAnimal.name)
                    put("raza", mascota.raza)
                    put("tamano", mascota.tamano.name)
                    put("vacunasAlDia", mascota.vacunasAlDia)
                    put("vacunasDetalle", mascota.vacunasDetalle)
                    put("nivelUrgencia", mascota.nivelUrgencia.name)
                    put("necesidadAdicional", mascota.necesidadAdicional)
                    put("estado", mascota.estado.name)
                    put("descripcion", mascota.descripcion)
                    put("imagenUrl", mascota.imagenUrl)
                    put("ubicacion", mascota.ubicacion)
                    put("usuarioId", mascota.usuarioId)
                    put("nombrePublicante", mascota.nombrePublicante)
                    put("fechaCreacion", mascota.fechaCreacion)
                    put("interesAdopcionCount", mascota.interesAdopcionCount)
                    put("motivoRechazo", mascota.motivoRechazo)

                    // Comentarios array
                    val commArray = JSONArray()
                    mascota.comentarios.forEach { comm ->
                        val cObj = JSONObject().apply {
                            put("id", comm.id)
                            put("usuarioId", comm.usuarioId)
                            put("nombreUsuario", comm.nombreUsuario)
                            put("texto", comm.texto)
                            put("fecha", comm.fecha)
                        }
                        commArray.put(cObj)
                    }
                    put("comentarios", commArray)
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
            val updated = listOf(mascota) + current
            saveMascotasToDisk(updated)
            updated
        }
    }

    override suspend fun updateMascota(mascota: Mascota) {
        mascotasFlow.update { current ->
            val updated = current.map { if (it.id == mascota.id) mascota else it }
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

    override suspend fun aprobarMascota(id: String) {
        mascotasFlow.update { current ->
            val updated = current.map {
                if (it.id == id) it.copy(estado = EstadoPublicacion.APROBADO, motivoRechazo = "") else it
            }
            saveMascotasToDisk(updated)
            updated
        }
    }

    override suspend fun rechazarMascota(id: String, motivo: String) {
        mascotasFlow.update { current ->
            val updated = current.map {
                if (it.id == id) it.copy(estado = EstadoPublicacion.RECHAZADO, motivoRechazo = motivo) else it
            }
            saveMascotasToDisk(updated)
            updated
        }
    }

    override suspend fun finalizarMascota(id: String) {
        mascotasFlow.update { current ->
            val updated = current.map {
                if (it.id == id) it.copy(estado = EstadoPublicacion.FINALIZADO) else it
            }
            saveMascotasToDisk(updated)
            updated
        }
    }

    override suspend fun agregarComentario(mascotaId: String, comentario: Comentario) {
        mascotasFlow.update { current ->
            val updated = current.map { m ->
                if (m.id == mascotaId) {
                    m.copy(comentarios = m.comentarios + comentario)
                } else m
            }
            saveMascotasToDisk(updated)
            updated
        }
    }

    override suspend fun incrementarInteres(mascotaId: String) {
        mascotasFlow.update { current ->
            val updated = current.map { m ->
                if (m.id == mascotaId) {
                    m.copy(
                        interesadosIds = (m.interesadosIds + "local-user").distinct(),
                        interesAdopcionCount = (m.interesadosIds + "local-user").distinct().size,
                    )
                } else m
            }
            saveMascotasToDisk(updated)
            updated
        }
    }

    override suspend fun quitarInteres(mascotaId: String) {
        mascotasFlow.update { current ->
            val updated = current.map { m ->
                if (m.id == mascotaId) {
                    val interesados = m.interesadosIds.filterNot { it == "local-user" }
                    m.copy(
                        interesadosIds = interesados,
                        interesAdopcionCount = interesados.size,
                    )
                } else m
            }
            saveMascotasToDisk(updated)
            updated
        }
    }
}
