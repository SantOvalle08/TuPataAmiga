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

    // Imagen aleatoria de Unsplash para cumplir el requisito de la entrega
    private val randomImages = listOf(
        "https://images.unsplash.com/photo-1543466835-00a7907e9de1",
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba",
        "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e",
        "https://images.unsplash.com/photo-1537151625747-768eb6cf92b2"
    )

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

    fun createMascota(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val state = _uiState.value

        if (state.nombre.isBlank()) {
            onError("Ingresa el nombre de la mascota")
            return
        }
        if (state.descripcion.isBlank()) {
            onError("Ingresa una descripción")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val mascota = Mascota(
                    id = UUID.randomUUID().toString(),
                    nombre = state.nombre,
                    categoria = state.categoria,
                    nivel = Nivel.MEDIO,
                    estado = state.estado,
                    descripcion = state.descripcion,
                    imagenUrl = randomImages.random(),
                    usuarioId = "user1"
                )

                createMascotaUseCase(mascota)

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                onError(e.localizedMessage ?: "Ocurrió un error al guardar")
            }
        }
    }
}

data class CreateUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: Categoria = Categoria.PERRO,
    val estado: EstadoPublicacion = EstadoPublicacion.PERDIDO,
    val isLoading: Boolean = false
)