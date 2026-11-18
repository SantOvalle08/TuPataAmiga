package co.edu.uniqundio.tupataamiga.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import co.edu.uniqundio.tupataamiga.domain.usecase.mascota.ObserveMascotasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    observeMascotasUseCase: ObserveMascotasUseCase,
    private val userRepository: UserRepository,
    private val mascotaRepository: MascotaRepository,
) : ViewModel() {

    val currentUser: StateFlow<Usuario?> = userRepository.currentUser

    val mascotas: StateFlow<List<Mascota>> = observeMascotasUseCase()
        .map { list -> list.filter { it.estado == EstadoPublicacion.APROBADO } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleInteres(mascota: Mascota) {
        viewModelScope.launch {
            val userId = currentUser.value?.id ?: return@launch
            if (userId in mascota.interesadosIds) {
                mascotaRepository.quitarInteres(mascota.id)
            } else {
                mascotaRepository.incrementarInteres(mascota.id)
            }

        }
    }

    suspend fun obtenerCorreoPropietario(mascota: Mascota): String? {
        return userRepository.getUserById(mascota.usuarioId)?.correo
    }
}
