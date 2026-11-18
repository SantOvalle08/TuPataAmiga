package co.edu.uniqundio.tupataamiga.features.recovery

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecoveryUiState(
    val correo: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val sentSuccess: Boolean = false,
)

@HiltViewModel
class RecoveryViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    fun onCorreoChanged(correo: String) {
        _uiState.update { it.copy(correo = correo, emailError = null, sentSuccess = false) }
    }

    fun enviarEnlace(onError: (String) -> Unit) {
        val correo = _uiState.value.correo.trim()
        val error = when {
            correo.isBlank() -> "Ingresa tu correo electrónico"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> "Ingresa un correo electrónico válido"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(emailError = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val sent = userRepository.recuperarContrasena(correo)
            _uiState.update { it.copy(isLoading = false, sentSuccess = sent) }
            if (!sent) {
                onError("No se pudo enviar el enlace. Verifica el correo e inténtalo nuevamente.")
            }
        }
    }
}
