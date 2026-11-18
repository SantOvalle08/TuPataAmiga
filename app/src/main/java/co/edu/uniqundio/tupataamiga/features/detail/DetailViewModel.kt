package co.edu.uniqundio.tupataamiga.features.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Comentario
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.model.Notificacion
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import co.edu.uniqundio.tupataamiga.domain.repository.NotificacionRepository
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class DetailUiState(
    val mascota: Mascota? = null,
    val propietario: Usuario? = null,
    val nuevoComentarioTexto: String = "",
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mascotaRepository: MascotaRepository,
    private val userRepository: UserRepository,
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    private val mascotaId: String = savedStateHandle.get<String>("mascotaId") ?: ""

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadMascota()
    }

    private fun loadMascota() {
        viewModelScope.launch {
            var propietarioIdCargado: String? = null
            var propietario: Usuario? = null
            mascotaRepository.observeMascotas().collect { list ->
                val encontrada = list.find { it.id == mascotaId }
                if (encontrada != null && encontrada.usuarioId != propietarioIdCargado) {
                    propietarioIdCargado = encontrada.usuarioId
                    propietario = encontrada.usuarioId
                        .takeIf { it.isNotBlank() }
                        ?.let { runCatching { userRepository.getUserById(it) }.getOrNull() }
                }
                _uiState.update {
                    it.copy(
                        mascota = encontrada,
                        propietario = propietario,
                    )
                }
            }
        }
    }

    fun onComentarioTextChanged(texto: String) {
        _uiState.update { it.copy(nuevoComentarioTexto = texto) }
    }

    fun agregarComentario(onSuccess: () -> Unit) {
        val texto = _uiState.value.nuevoComentarioTexto.trim()
        if (texto.isBlank() || mascotaId.isBlank()) return

        viewModelScope.launch {
            val user = userRepository.currentUser.value
            val nombreAutor = user?.nombre ?: "Usuario PataAmiga"
            val usuarioId = user?.id ?: "guest"

            val nuevoComentario = Comentario(
                id = UUID.randomUUID().toString(),
                usuarioId = usuarioId,
                nombreUsuario = nombreAutor,
                texto = texto,
                fecha = "hace un momento"
            )

            mascotaRepository.agregarComentario(mascotaId, nuevoComentario)

            val mascotaActual = _uiState.value.mascota
            notificacionRepository.agregarNotificacion(
                Notificacion(
                    id = System.currentTimeMillis().toString(),
                    titulo = "Nuevo comentario",
                    cuerpo = "$nombreAutor comentó en la publicación \"${mascotaActual?.nombre ?: "Mascota"}\"",
                    tiempo = "hace un momento",
                    tipo = "COMENTARIO",
                    leido = false,
                    usuarioId = mascotaActual?.usuarioId.orEmpty()
                )
            )

            _uiState.update { it.copy(nuevoComentarioTexto = "") }
            onSuccess()
        }
    }

    fun marcarInteres() {
        if (mascotaId.isBlank()) return
        viewModelScope.launch {
            mascotaRepository.incrementarInteres(mascotaId)
            val mascotaActual = _uiState.value.mascota
            notificacionRepository.agregarNotificacion(
                Notificacion(
                    id = System.currentTimeMillis().toString(),
                    titulo = "Persona interesada",
                    cuerpo = "Marcaste interés en adoptar a \"${mascotaActual?.nombre ?: "Mascota"}\"",
                    tiempo = "hace un momento",
                    tipo = "INTERES",
                    leido = false,
                    usuarioId = mascotaActual?.usuarioId.orEmpty()
                )
            )
        }
    }

    fun deleteMascota(onDeleted: () -> Unit) {
        viewModelScope.launch {
            mascotaRepository.deleteMascota(mascotaId)
            onDeleted()
        }
    }
}
