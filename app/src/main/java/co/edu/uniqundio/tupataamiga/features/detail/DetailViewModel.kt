package co.edu.uniqundio.tupataamiga.features.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.usecase.mascota.DeleteMascotaUseCase
import co.edu.uniqundio.tupataamiga.domain.usecase.mascota.GetMascotaByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class Comentario(
    val id: String = UUID.randomUUID().toString(),
    val autor: String = "María Gómez",
    val texto: String,
    val tiempo: String = "Justo ahora",
)

data class DetailUiState(
    val mascota: Mascota? = null,
    val comentarios: List<Comentario> = listOf(
        Comentario(autor = "Ana Gómez", texto = "¡Qué tierno! ¿Sigue disponible?", tiempo = "hace 10 min"),
        Comentario(autor = "Carlos Ruiz", texto = "Me interesa mucho para adopción.", tiempo = "hace 1 h"),
    ),
    val nuevoComentarioTexto: String = "",
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMascotaByIdUseCase: GetMascotaByIdUseCase,
    private val deleteMascotaUseCase: DeleteMascotaUseCase,
) : ViewModel() {

    private val mascotaId: String = savedStateHandle.get<String>("mascotaId") ?: ""

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadMascota()
    }

    private fun loadMascota() {
        viewModelScope.launch {
            val mascota = getMascotaByIdUseCase(mascotaId)
            _uiState.update { it.copy(mascota = mascota) }
        }
    }

    fun onComentarioTextChanged(texto: String) {
        _uiState.update { it.copy(nuevoComentarioTexto = texto) }
    }

    fun agregarComentario(onSuccess: () -> Unit) {
        val texto = _uiState.value.nuevoComentarioTexto.trim()
        if (texto.isBlank()) return
        val nuevo = Comentario(autor = "María Gómez", texto = texto, tiempo = "Justo ahora")
        _uiState.update {
            it.copy(
                comentarios = listOf(nuevo) + it.comentarios,
                nuevoComentarioTexto = "",
            )
        }
        onSuccess()
    }

    fun deleteMascota(onDeleted: () -> Unit) {
        viewModelScope.launch {
            deleteMascotaUseCase(mascotaId)
            onDeleted()
        }
    }
}
