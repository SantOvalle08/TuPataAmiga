package co.edu.uniqundio.tupataamiga.domain.usecase.mascota

import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import javax.inject.Inject

class GetMascotaByIdUseCase @Inject constructor(
    private val repository: MascotaRepository
) {
    suspend operator fun invoke(id: String): Mascota? = repository.getMascotaById(id)
}
