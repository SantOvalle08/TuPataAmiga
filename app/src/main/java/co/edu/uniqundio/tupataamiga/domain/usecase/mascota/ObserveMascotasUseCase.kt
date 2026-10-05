package co.edu.uniqundio.tupataamiga.domain.usecase.mascota

import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMascotasUseCase @Inject constructor(
    private val repository: MascotaRepository
) {
    operator fun invoke(): Flow<List<Mascota>> = repository.observeMascotas()
}
