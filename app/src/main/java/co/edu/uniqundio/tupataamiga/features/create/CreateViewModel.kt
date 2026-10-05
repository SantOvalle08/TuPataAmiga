package co.edu.uniqundio.tupataamiga.features.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Categoria
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.model.Nivel
import co.edu.uniqundio.tupataamiga.domain.usecase.mascota.CreateMascotaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val createMascotaUseCase: CreateMascotaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    fun onNombreChanged(nombre: String) {
        _uiState.update { it.copy(nombre = nombre) }
    }

    fun onDescripcionChanged(descripcion: String) {
        _uiState.update { it.copy(descripcion = descripcion) }
    }

    fun onCategoriaChanged(categoria: Categoria) {
        _uiState.update { it.copy(categoria = categoria) }
    }

    fun onEstadoChanged(estado: EstadoPublicacion) {
        _uiState.update { it.copy(estado = estado) }
    }

    fun createMascota(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val mascota = Mascota(
                id = UUID.randomUUID().toString(),
                nombre = state.nombre,
                categoria = state.categoria,
                nivel = Nivel.MEDIO,
                estado = state.estado,
                descripcion = state.descripcion,
                imagenUrl = "",
                usuarioId = "user1"
            )
            createMascotaUseCase(mascota)
            onSuccess()
        }
    }
}

data class CreateUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: Categoria = Categoria.PERRO,
    val estado: EstadoPublicacion = EstadoPublicacion.PERDIDO
)
