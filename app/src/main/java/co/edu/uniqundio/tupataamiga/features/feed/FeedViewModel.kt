package co.edu.uniqundio.tupataamiga.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import co.edu.uniqundio.tupataamiga.domain.usecase.mascota.ObserveMascotasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    observeMascotasUseCase: ObserveMascotasUseCase,
    userRepository: UserRepository,
) : ViewModel() {

    val currentUser: StateFlow<Usuario?> = userRepository.currentUser

    val mascotas: StateFlow<List<Mascota>> = observeMascotasUseCase()
        .map { list -> list.filter { it.estado == EstadoPublicacion.APROBADO } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
