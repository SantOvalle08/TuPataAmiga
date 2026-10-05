package co.edu.uniqundio.tupataamiga.features.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Rol
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.usecase.user.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChanged(nombre: String) {
        _uiState.update { it.copy(nombre = nombre) }
    }

    fun onCorreoChanged(correo: String) {
        _uiState.update { it.copy(correo = correo) }
    }

    fun onClaveChanged(clave: String) {
        _uiState.update { it.copy(clave = clave) }
    }

    fun register(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            val usuario = Usuario(
                id = UUID.randomUUID().toString(),
                nombre = state.nombre,
                correo = state.correo,
                rol = Rol.USUARIO
            )
            val success = registerUserUseCase(usuario, state.clave)
            _uiState.update { it.copy(isLoading = false) }
            if (success) {
                onSuccess()
            } else {
                onError("El correo ya está registrado")
            }
        }
    }
}

data class RegisterUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val isLoading: Boolean = false
)
