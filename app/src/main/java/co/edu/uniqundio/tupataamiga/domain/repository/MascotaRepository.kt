package co.edu.uniqundio.tupataamiga.domain.repository

import co.edu.uniqundio.tupataamiga.domain.model.Comentario
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import kotlinx.coroutines.flow.Flow

interface MascotaRepository {
    fun observeMascotas(): Flow<List<Mascota>>
    suspend fun getMascotaById(id: String): Mascota?
    suspend fun createMascota(mascota: Mascota)
    suspend fun updateMascota(mascota: Mascota)
    suspend fun deleteMascota(id: String)
    suspend fun aprobarMascota(id: String)
    suspend fun rechazarMascota(id: String, motivo: String)
    suspend fun finalizarMascota(id: String)
    suspend fun agregarComentario(mascotaId: String, comentario: Comentario)
    suspend fun incrementarInteres(mascotaId: String)
}
