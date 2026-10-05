package co.edu.uniqundio.tupataamiga.data.repository.memory

import co.edu.uniqundio.tupataamiga.domain.model.Categoria
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.model.Nivel
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MascotaRepositoryImpl @Inject constructor() : MascotaRepository {
    private val mascotasFlow = MutableStateFlow(
        listOf(
            Mascota("1", "Firulais", Categoria.PERRO, Nivel.MEDIO, EstadoPublicacion.PERDIDO, "Perro amigable perdido en el parque", "", "user1"),
            Mascota("2", "Michi", Categoria.GATO, Nivel.BAJO, EstadoPublicacion.ADOPCION, "Gato tierno buscando hogar", "", "user2")
        )
    )

    override fun observeMascotas(): Flow<List<Mascota>> = mascotasFlow

    override suspend fun getMascotaById(id: String): Mascota? {
        return mascotasFlow.value.find { it.id == id }
    }

    override suspend fun createMascota(mascota: Mascota) {
        mascotasFlow.update { it + mascota }
    }

    override suspend fun deleteMascota(id: String) {
        mascotasFlow.update { list -> list.filter { it.id != id } }
    }
}
