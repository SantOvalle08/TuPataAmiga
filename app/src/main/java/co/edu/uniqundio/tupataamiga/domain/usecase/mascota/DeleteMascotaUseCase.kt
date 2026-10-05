package co.edu.uniqundio.tupataamiga.domain.usecase.mascota

import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import javax.inject.Inject

class DeleteMascotaUseCase @Inject constructor(
    private val repository: MascotaRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteMascota(id)
}
