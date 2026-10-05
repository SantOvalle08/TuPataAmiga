package co.edu.uniqundio.tupataamiga.domain.repository

import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import kotlinx.coroutines.flow.Flow

interface MascotaRepository {
    fun observeMascotas(): Flow<List<Mascota>>
    suspend fun getMascotaById(id: String): Mascota?
    suspend fun createMascota(mascota: Mascota)
    suspend fun deleteMascota(id: String)
}
