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
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMascotaByIdUseCase: GetMascotaByIdUseCase,
    private val deleteMascotaUseCase: DeleteMascotaUseCase
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

    fun deleteMascota(onDeleted: () -> Unit) {
        viewModelScope.launch {
            deleteMascotaUseCase(mascotaId)
            onDeleted()
        }
    }
}

data class DetailUiState(
    val mascota: Mascota? = null
)
