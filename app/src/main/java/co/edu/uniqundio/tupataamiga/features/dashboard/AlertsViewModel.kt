package co.edu.uniqundio.tupataamiga.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Notificacion
import co.edu.uniqundio.tupataamiga.domain.repository.NotificacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    val notificaciones: StateFlow<List<Notificacion>> = notificacionRepository.observeNotificaciones()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun marcarTodasLeidas() {
        viewModelScope.launch {
            notificacionRepository.marcarTodasLeidas()
        }
    }
}
