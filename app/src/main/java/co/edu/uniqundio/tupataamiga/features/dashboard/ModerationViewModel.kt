package co.edu.uniqundio.tupataamiga.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.model.Notificacion
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import co.edu.uniqundio.tupataamiga.domain.repository.NotificacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val notificacionRepository: NotificacionRepository,
) : ViewModel() {

    val pendingPosts: StateFlow<List<Mascota>> = mascotaRepository.observeMascotas()
        .map { list -> list.filter { it.estado == EstadoPublicacion.PENDIENTE } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val approvedPosts: StateFlow<List<Mascota>> = mascotaRepository.observeMascotas()
        .map { list -> list.filter { it.estado == EstadoPublicacion.APROBADO } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun aprobarPublicacion(id: String, nombreMascota: String) {
        viewModelScope.launch {
            val mascota = mascotaRepository.getMascotaById(id)
            mascotaRepository.aprobarMascota(id)
            notificacionRepository.agregarNotificacion(
                Notificacion(
                    id = System.currentTimeMillis().toString(),
                    titulo = "Publicación verificada",
                    cuerpo = "Tu publicación \"$nombreMascota\" fue aprobada y ya es visible en el Feed.",
                    tiempo = "hace un momento",
                    tipo = "VERIFICADA",
                    leido = false,
                    usuarioId = mascota?.usuarioId.orEmpty()
                )
            )
        }
    }

    fun rechazarPublicacion(id: String, nombreMascota: String, motivo: String) {
        viewModelScope.launch {
            val mascota = mascotaRepository.getMascotaById(id)
            mascotaRepository.rechazarMascota(id, motivo)
            notificacionRepository.agregarNotificacion(
                Notificacion(
                    id = System.currentTimeMillis().toString(),
                    titulo = "Publicación rechazada",
                    cuerpo = "Publicación \"$nombreMascota\": $motivo",
                    tiempo = "hace un momento",
                    tipo = "RECHAZADA",
                    leido = false,
                    usuarioId = mascota?.usuarioId.orEmpty()
                )
            )
        }
    }
}
