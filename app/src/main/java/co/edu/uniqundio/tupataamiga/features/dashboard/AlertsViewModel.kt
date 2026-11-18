package co.edu.uniqundio.tupataamiga.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Notificacion
import co.edu.uniqundio.tupataamiga.domain.repository.NotificacionRepository
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val notificacionRepository: NotificacionRepository,
    mascotaRepository: MascotaRepository,
) : ViewModel() {

    val notificaciones: StateFlow<List<Notificacion>> = notificacionRepository.observeNotificaciones()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val publicacionesAprobadas = mascotaRepository.observeMascotas()
        .map { mascotas -> mascotas.filter { it.estado == co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion.APROBADO } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun marcarTodasLeidas() {
        viewModelScope.launch {
            notificacionRepository.marcarTodasLeidas()
        }
    }
}
